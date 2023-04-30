///////////////////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code and other text files for adherence to a set of rules.
// Copyright (C) 2001-2023 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 3 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
///////////////////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle.ordering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.MultiMapUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.collections4.SetValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.commons.lang.builder.CompareToBuilder;

import com.github.sevntu.checkstyle.domain.Dependencies;
import com.github.sevntu.checkstyle.domain.MethodDefinition;
import com.github.sevntu.checkstyle.domain.ResolvedCall;

public class MethodOrder {

    private final Map<String, Method> methods;

    private final List<Method> currentOrdering;

    private final List<Method> initialOrdering;

    private final Set<MethodInvocation> invocations;

    private final MultiValuedMap<MethodInvocation, MethodInvocation> invocationNesting;

    public MethodOrder(Dependencies dependencies) {
        methods = MapUtils.unmodifiableMap(getAllMethods(dependencies));
        initialOrdering = ListUtils.unmodifiableList(getInitialMethodOrdering(methods));
        currentOrdering = initialOrdering;
        final Map<ResolvedCall, MethodInvocation> callsToInvocations =
            getAllInvocations(dependencies, methods);
        invocations = SetUtils.unmodifiableSet(new HashSet<>(callsToInvocations.values()));
        invocationNesting = MultiMapUtils.unmodifiableMultiValuedMap(
            getMethodInvocationsNesting(callsToInvocations));
    }

    private MethodOrder(MethodOrder methodOrder, final List<Method> newMethodOrdering) {
        currentOrdering = ListUtils.unmodifiableList(newMethodOrdering);
        initialOrdering = methodOrder.initialOrdering;
        methods = methodOrder.methods;
        invocations = methodOrder.invocations;
        invocationNesting = methodOrder.invocationNesting;
    }

    public List<Method> getMethods() {
        return currentOrdering;
    }

    public int getMethodIndex(Method method) {
        return currentOrdering.indexOf(method);
    }

    public Method getMethodByInitialIndex(int index) {
        return initialOrdering.get(index);
    }

    public MethodOrder moveMethodBy(Method method, int indexShift) {
        final int currentIndex = getMethodIndex(method);
        final int newIndex = currentIndex + indexShift;
        if (0 <= newIndex && newIndex < methods.size()) {
            final ArrayList<Method> newOrdering = new ArrayList<>(currentOrdering);
            newOrdering.remove(currentIndex);
            newOrdering.add(newIndex, method);
            return new MethodOrder(this, newOrdering);
        }
        else {
            throw new IllegalArgumentException(String.format(
                "Trying to move method #%d by %d positions", currentIndex, newIndex));
        }
    }

    public MethodOrder reorder(List<Method> order) {
        final boolean allMethodsPresent = currentOrdering.stream().allMatch(order::contains);
        if (allMethodsPresent && currentOrdering.size() == order.size()) {
            return new MethodOrder(this, new ArrayList<>(order));
        }
        else {
            final String currentOrderingString = methodsSignatureList(currentOrdering);
            final String newOrderingString = methodsSignatureList(order);
            throw new IllegalArgumentException("New ordering contains another set of methods:\n "
                + "currentOrdering " + currentOrderingString + "\n"
                + "newOrdering" + newOrderingString);
        }
    }

    private static String methodsSignatureList(Collection<Method> methods) {
        return methods.stream().map(Object::toString).collect(Collectors.joining("; ", "[", "]"));
    }

    public List<Method> getMethodDependenciesInAppearanceOrder(Method caller) {
        return invocations.stream()
            .filter(methodInvocation -> methodInvocation.getCaller().equals(caller))
            .sorted(new AppearanceOrderMethodInvocationComparator())
            .filter(new UniqueCallerCalleeMethodInvocationFilter())
            .map(MethodInvocation::getCallee)
            .collect(Collectors.toList());
    }

    public List<Integer> getMethodDependenciesIndexesInAppearanceOrder(Method caller) {
        return getMethodDependenciesInAppearanceOrder(caller).stream()
            .map(this::getMethodIndex)
            .collect(Collectors.toList());
    }

    public boolean hasMethodDependencies(Method method) {
        return !getMethodDependenciesInAppearanceOrder(method).isEmpty();
    }

    public List<Method> getMethodDependants(Method callee) {
        return invocations.stream()
            .filter(methodInvocation -> methodInvocation.getCallee().equals(callee))
            .filter(new UniqueCallerCalleeMethodInvocationFilter())
            .map(MethodInvocation::getCaller)
            .collect(Collectors.toList());
    }

    public boolean hasMethodDependants(Method method) {
        return !getMethodDependants(method).isEmpty();
    }

    public boolean isInterfaceMethod(Method method) {
        return method.getAccessibility() == MethodDefinition.Accessibility.PUBLIC
            && !hasMethodDependencies(method)
            && !hasMethodDependants(method);
    }

    public boolean isMethodDependsOn(Method caller, Method callee) {
        return invocations.stream()
            .anyMatch(methodInvocation -> {
                return methodInvocation.getCaller().equals(caller)
                        && methodInvocation.getCallee().equals(callee);
            });
    }

    public int getMethodsIndexDifference(Method caller, Method callee) {
        return getMethodIndex(callee) - getMethodIndex(caller);
    }

    public int getMethodsLineDifference(Method caller, Method callee) {
        return translateInitialLineNo(callee.getInitialLineNo())
            - translateInitialLineNo(caller.getInitialLineNo());
    }

    public int getTotalSumOfMethodDistances() {
        return currentOrdering.stream()
            .collect(Collectors.summingInt(caller -> {
                return getMethodDependenciesInAppearanceOrder(caller)
                    .stream()
                    .collect(Collectors.summingInt(callee -> {
                        return Math.abs(getMethodsIndexDifference(caller, callee));
                    }));
            }));
    }

    public int getDeclarationBeforeUsageCases() {
        return (int) invocations.stream()
            .filter(new UniqueCallerCalleeMethodInvocationFilter())
            .filter(invocation -> {
                return getMethodsIndexDifference(invocation.getCaller(),
                    invocation.getCallee()) < 0;
            })
            .count();
    }

    public int getCtorGroupsSplitCases() {
        final int result;
        final List<Method> constructors = currentOrdering.stream()
            .filter(Method::isCtor)
            .collect(Collectors.toList());
        if (constructors.isEmpty()) {
            result = 0;
        }
        else {
            result = getMethodGroupSplitCount(constructors);
        }
        return result;
    }

    public int getOverrideGroupSplitCases() {
        final int result;
        final List<Method> overrideMethods = currentOrdering.stream()
            .filter(Method::isOverride)
            .collect(Collectors.toList());
        if (overrideMethods.isEmpty()) {
            result = 0;
        }
        else {
            result = getMethodGroupSplitCount(overrideMethods);
        }
        return result;
    }

    public int getOverloadGroupsSplitCases() {
        return currentOrdering.stream()
            .filter(method -> !method.isCtor())
            .collect(Collectors.groupingBy(Method::getName))
            .values().stream()
            .collect(Collectors.summingInt(this::getMethodGroupSplitCount));
    }

    public int getDependenciesBetweenDistantMethodsCases(int screenLinesCount) {
        return invocations.stream()
            .collect(Collectors.groupingBy(MethodInvocation::getCaller))
            .values().stream()
            .collect(Collectors.summingInt(callerInvocations -> {
                return isBiggerThanScreenLinesCount(screenLinesCount, callerInvocations);
            }));
    }

    public int getAccessorsSplitCases() {
        return currentOrdering.stream()
            .filter(method -> method.isGetter() || method.isSetter())
            .collect(Collectors.groupingBy(Method::getAccessiblePropertyName))
            .values().stream()
            .collect(Collectors.summingInt(this::getMethodGroupSplitCount));
    }

    public int getRelativeOrderInconsistencyCases() {
        return getMethods().stream()
            .collect(Collectors.summingInt(this::countViolations));
    }

    private int countViolations(Method caller) {
        int maxCalleeIndex = 0;
        int orderViolations = 0;
        for (final Method callee : getMethodDependenciesInAppearanceOrder(caller)) {
            final int calleeIndex = getMethodIndex(callee);
            if (calleeIndex < maxCalleeIndex) {
                ++orderViolations;
            }
            else {
                maxCalleeIndex = calleeIndex;
            }
        }
        return orderViolations;
    }

    private int isBiggerThanScreenLinesCount(int screenLinesCount,
                                             List<MethodInvocation> callerInvocations) {
        return (int) callerInvocations.stream()
            .filter(invocation -> {
                final int invocationLineNo =
                    translateInitialLineNo(invocation.getInitialLineNo());
                final int calleeLineNo =
                    translateInitialLineNo(invocation
                        .getCallee().getInitialLineNo());
                return Math.abs(calleeLineNo - invocationLineNo) > screenLinesCount;
            })
            .filter(new UniqueCallerCalleeMethodInvocationFilter())
            .count();
    }

    private int getMethodGroupSplitCount(Collection<Method> methodGroup) {
        final List<Integer> methodIndices = methodGroup.stream()
            .map(this::getMethodIndex)
            .collect(Collectors.toList());
        final MinMax<Integer> bounds = minMax(methodIndices);
        return bounds.getMax() - bounds.getMin() - methodIndices.size() + 1;
    }

    private int translateInitialLineNo(int lineNo) {
        return currentOrdering.stream()
            .filter(method -> {
                final int start = method.getInitialLineNo();
                final int end = start + method.getLength();
                return start <= lineNo && lineNo <= end;
            })
            .findFirst()
            .map(method -> getLineCount(lineNo, method))
            .orElseThrow(() -> {
                return new IllegalArgumentException(
                    String.format("Line #%d does lies within any method", lineNo));
            });
    }

    private Integer getLineCount(int lineNo, Method method) {
        final int initialMethodIndex = initialOrdering.indexOf(method);
        final int currentMethodIndex = currentOrdering.indexOf(method);
        final int sumOfLengthsPresidingMethodsInInitialOrder = initialOrdering
            .subList(0, initialMethodIndex).stream()
            .collect(Collectors.summingInt(Method::getLength));
        final int sumOfLengthsPresidingMethodInCurrentOrder = currentOrdering
            .subList(0, currentMethodIndex).stream()
            .collect(Collectors.summingInt(Method::getLength));
        final int change = sumOfLengthsPresidingMethodInCurrentOrder
            - sumOfLengthsPresidingMethodsInInitialOrder;
        return lineNo + change;
    }

    private static Map<String, Method> getAllMethods(Dependencies dependencies) {
        return dependencies.getMethods().stream()
            .collect(Collectors.toMap(MethodDefinition::getSignature, Method::new));
    }

    private static Map<ResolvedCall, MethodInvocation> getAllInvocations(
        Dependencies dependencies, Map<String, Method> methods) {

        return dependencies.getResolvedCalls().stream()
        .collect(Collectors.toMap(Function.identity(), resolvedCall -> {
            final String callerSignature = resolvedCall.getCaller().getSignature();
            final String calleeSignature = resolvedCall.getCallee().getSignature();
            return new MethodInvocation(resolvedCall,
                methods.get(callerSignature), methods.get(calleeSignature));
        }));
    }

    private static MultiValuedMap<MethodInvocation, MethodInvocation> getMethodInvocationsNesting(
        Map<ResolvedCall, MethodInvocation> callToInvocation) {

        final SetValuedMap<MethodInvocation, MethodInvocation> nestedInside =
            new HashSetValuedHashMap<>();
        callToInvocation.entrySet().stream()
            .forEach(entry -> {
                final ResolvedCall resolvedCall = entry.getKey();
                final MethodInvocation methodInvocation = entry.getValue();
                callToInvocation.keySet().stream()
                    .filter(call -> !call.equals(resolvedCall))
                    .filter(resolvedCall::isNestedInside)
                    .forEach(call -> {
                        nestedInside.put(methodInvocation, callToInvocation.get(call));
                    });
            });
        return nestedInside;
    }

    private static List<Method> getInitialMethodOrdering(Map<String, Method> methods) {
        return methods.values().stream()
            .sorted((lhs, rhs) -> Integer.compare(lhs.getInitialIndex(), rhs.getInitialIndex()))
            .collect(Collectors.toList());
    }

    private static <T> MinMax<T> minMax(Collection<T> elements) {
        final SortedSet<T> sortedSet = new TreeSet<>(elements);
        return new MinMax<>(sortedSet.first(), sortedSet.last());
    }

    private static final class MinMax<T> {

        private final T min;

        private final T max;

        private MinMax(T min, T max) {
            this.min = min;
            this.max = max;
        }

        public T getMin() {
            return min;
        }

        public T getMax() {
            return max;
        }
    }

    private final class AppearanceOrderMethodInvocationComparator
        implements Comparator<MethodInvocation> {

        @Override
        public int compare(MethodInvocation lhs, MethodInvocation rhs) {
            final int result;
            if (invocationNesting.containsMapping(lhs, rhs)) {
                result = -1;
            }
            else if (invocationNesting.containsMapping(rhs, lhs)) {
                result = 1;
            }
            else {
                result = new CompareToBuilder()
                    .append(translateInitialLineNo(lhs.getInitialLineNo()),
                        translateInitialLineNo(rhs.getInitialLineNo()))
                    .append(lhs.getColumnNo(), rhs.getColumnNo())
                    .toComparison();
            }
            return result;
        }
    }

    private static final class UniqueCallerCalleeMethodInvocationFilter
        implements Predicate<MethodInvocation> {

        private final Set<MethodInvocation> set = new TreeSet<>(new Comparator<MethodInvocation>() {
            @Override
            public int compare(MethodInvocation lhs, MethodInvocation rhs) {
                return new CompareToBuilder()
                    .append(lhs.getCaller().getSignature(), rhs.getCaller().getSignature())
                    .append(lhs.getCallee().getSignature(), rhs.getCallee().getSignature())
                    .toComparison();
            }
        });

        /* This class is not unit test. Method name similarity is just accidence. */
        @SuppressWarnings("PMD.JUnit4TestShouldUseTestAnnotation")
        @Override
        public boolean test(MethodInvocation methodInvocation) {
            return set.add(methodInvocation);
        }
    }
}
