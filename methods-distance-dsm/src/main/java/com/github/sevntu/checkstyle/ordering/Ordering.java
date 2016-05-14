package com.github.sevntu.checkstyle.ordering;

import com.github.sevntu.checkstyle.analysis.Dependencies;
import com.github.sevntu.checkstyle.analysis.MethodDefinition;
import com.github.sevntu.checkstyle.analysis.ResolvedCall;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.MultiMapUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.collections4.SetValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.commons.lang.builder.CompareToBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Ordering {

    private final Map<String, Method> methods;

    private final List<Method> currentOrdering;

    private final List<Method> initialOrdering;

    private final Set<MethodInvocation> invocations;

    private final MultiValuedMap<MethodInvocation, MethodInvocation> invocationNesting;

    public Ordering(final Dependencies dependencies) {
        this.methods = MapUtils.unmodifiableMap(getAllMethods(dependencies));
        this.initialOrdering = ListUtils.unmodifiableList(getInitialMethodOrdering(methods));
        this.currentOrdering = this.initialOrdering;
        final Map<ResolvedCall, MethodInvocation> callsToInvocations =
            getAllInvocations(dependencies, methods);
        this.invocations = SetUtils.unmodifiableSet(new HashSet<>(callsToInvocations.values()));
        this.invocationNesting = MultiMapUtils.unmodifiableMultiValuedMap(
            getMethodInvocationsNesting(callsToInvocations));
    }

    private Ordering(final Ordering ordering, final List<Method> newMethodOrdering) {
        this.currentOrdering = ListUtils.unmodifiableList(newMethodOrdering);
        this.initialOrdering = ordering.initialOrdering;
        this.methods = ordering.methods;
        this.invocations = ordering.invocations;
        this.invocationNesting = ordering.invocationNesting;
    }

    public List<Method> getMethods() {
        return currentOrdering;
    }

    public int getMethodIndex(final Method method) {
        return currentOrdering.indexOf(method);
    }

    public Method getMethodByInitialIndex(final int index) {
        return initialOrdering.get(index);
    }

    public Ordering moveMethodBy(final Method method, final int indexShift) {
        final int currentIndex = getMethodIndex(method);
        final int newIndex = currentIndex + indexShift;
        if (0 <= newIndex && newIndex < methods.size()) {
            final ArrayList<Method> newOrdering = new ArrayList<>(currentOrdering);
            newOrdering.remove(currentIndex);
            newOrdering.add(newIndex, method);
            return new Ordering(this, newOrdering);
        }
        else {
            throw new IllegalArgumentException(String.format(
                "Trying to move method #%d by %d positions", currentIndex, newIndex));
        }
    }

    public List<Method> getMethodDependenciesInAppearanceOrder(final Method caller) {
        return invocations.stream()
            .filter(methodInvocation -> methodInvocation.getCaller().equals(caller))
            .sorted(new AppearanceOrderMethodInvocationComparator())
            .filter(new UniqueCallerCalleeMethodInvocationFilter())
            .map(MethodInvocation::getCallee)
            .collect(Collectors.toList());
    }

    public List<Integer> getMethodDependenciesIndexesInAppearanceOrder(final Method caller) {
        return getMethodDependenciesInAppearanceOrder(caller).stream()
            .map(this::getMethodIndex)
            .collect(Collectors.toList());
    }

    public boolean hasMethodDependencies(final Method method) {
        return !getMethodDependenciesInAppearanceOrder(method).isEmpty();
    }

    public List<Method> getMethodDependants(final Method callee) {
        return invocations.stream()
            .filter(methodInvocation -> methodInvocation.getCallee().equals(callee))
            .filter(new UniqueCallerCalleeMethodInvocationFilter())
            .map(MethodInvocation::getCaller)
            .collect(Collectors.toList());
    }

    public boolean hasMethodDependants(final Method method) {
        return !getMethodDependants(method).isEmpty();
    }

    public boolean isInterfaceMethod(final Method method) {
        return method.getAccessibility() == MethodDefinition.Accessibility.PUBLIC
            && !hasMethodDependencies(method)
            && !hasMethodDependants(method);
    }

    public boolean isMethodDependsOn(final Method caller, final Method callee) {
        return invocations.stream()
            .anyMatch(mi -> mi.getCaller().equals(caller) && mi.getCallee().equals(callee));
    }

    public int getMethodsIndexDifference(final Method caller, final Method callee) {
        return getMethodIndex(callee) - getMethodIndex(caller);
    }

    public int getMethodsLineDifference(final Method caller, final Method callee) {
        return translateInitialLineNo(callee.getInitialLineNo())
            - translateInitialLineNo(caller.getInitialLineNo());
    }

    public int getTotalSumOfMethodDistances() {
        return currentOrdering.stream()
            .collect(Collectors.summingInt(caller -> getMethodDependenciesInAppearanceOrder(caller)
                .stream()
                .collect(Collectors.summingInt(callee ->
                    Math.abs(getMethodsIndexDifference(caller, callee))))));
    }

    public int getDeclarationBeforeUsageCases() {
        return (int) invocations.stream()
            .filter(new UniqueCallerCalleeMethodInvocationFilter())
            .filter(invocation ->
                getMethodsIndexDifference(invocation.getCaller(), invocation.getCallee()) < 0)
            .count();
    }

    public int getOverrideGroupSplitCases() {
        final List<Method> overrideMethods = currentOrdering.stream()
            .filter(Method::isOverride)
            .collect(Collectors.toList());
        return overrideMethods.isEmpty()
            ? 0
            : getMethodGroupSplitCount(overrideMethods);
    }

    public int getOverloadGroupsSplitCases() {
        return currentOrdering.stream()
            .collect(Collectors.groupingBy(Method::getName))
            .values().stream()
            .collect(Collectors.summingInt(this::getMethodGroupSplitCount));
    }

    public int getDependenciesBetweenDistantMethodsCases(final int screenLinesCount) {
        return invocations.stream()
            .collect(Collectors.groupingBy(MethodInvocation::getCaller))
            .values().stream()
            .collect(Collectors.summingInt(callerInvocations -> (int) callerInvocations.stream()
                    .filter(invocation -> {
                        final int invocationLineNo =
                            translateInitialLineNo(invocation.getInitialLineNo());
                        final int calleeLineNo =
                            translateInitialLineNo(invocation.getCallee().getInitialLineNo());
                        return Math.abs(calleeLineNo - invocationLineNo) > screenLinesCount;
                    })
                    .filter(new UniqueCallerCalleeMethodInvocationFilter())
                    .count()
            ));
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
            .collect(Collectors.summingInt(caller -> {
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
            }));
    }

    private int getMethodGroupSplitCount(final Collection<Method> methodGroup) {
        final List<Integer> methodIndices = methodGroup.stream()
            .map(this::getMethodIndex)
            .collect(Collectors.toList());
        final MinMax<Integer> bounds = minMax(methodIndices);
        return bounds.getMax() - bounds.getMin() - methodIndices.size() + 1;
    }

    private int translateInitialLineNo(final int lineNo) {
        final Optional<Method> containingMethod = currentOrdering.stream()
            .filter(method -> {
                final int start = method.getInitialLineNo();
                final int end = start + method.getLength();
                return start <= lineNo && lineNo <= end;
            })
            .findFirst();
        if (containingMethod.isPresent()) {
            final Method method = containingMethod.get();
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
        else {
            throw new IllegalArgumentException(
                String.format("Line #%d does lies within any method", lineNo));
        }
    }

    private static Map<String, Method> getAllMethods(final Dependencies dependencies) {
        return dependencies.getMethods().stream()
            .collect(Collectors.toMap(MethodDefinition::getSignature, Method::new));
    }

    private static Map<ResolvedCall, MethodInvocation> getAllInvocations(
        final Dependencies dependencies, final Map<String, Method> methods) {
        return dependencies.getResolvedCalls().stream()
        .collect(Collectors.toMap(Function.identity(), resolvedCall -> {
            final String callerSignature = resolvedCall.getCaller().getSignature();
            final String calleeSignature = resolvedCall.getCallee().getSignature();
            return new MethodInvocation(resolvedCall,
                methods.get(callerSignature), methods.get(calleeSignature));
        }));
    }

    private static MultiValuedMap<MethodInvocation, MethodInvocation> getMethodInvocationsNesting(
        final Map<ResolvedCall, MethodInvocation> callToInvocation) {
        final SetValuedMap<MethodInvocation, MethodInvocation> nestedInside =
            new HashSetValuedHashMap<>();
        callToInvocation.entrySet().stream()
            .forEach(entry -> {
                final ResolvedCall resolvedCall = entry.getKey();
                final MethodInvocation methodInvocation = entry.getValue();
                callToInvocation.keySet().stream()
                    .filter(rc -> !rc.equals(resolvedCall))
                    .filter(resolvedCall::isNestedInside)
                    .forEach(rc -> nestedInside.put(methodInvocation, callToInvocation.get(rc)));
            });
        return nestedInside;
    }

    private static List<Method> getInitialMethodOrdering(final Map<String, Method> methods) {
        return methods.values().stream()
            .sorted((lhs, rhs) -> Integer.compare(lhs.getInitialIndex(), rhs.getInitialIndex()))
            .collect(Collectors.toList());
    }

    private static <T> MinMax<T> minMax(final Collection<T> elements) {
        final SortedSet<T> sortedSet = new TreeSet<>(elements);
        return new MinMax<>(sortedSet.first(), sortedSet.last());
    }

    private static final class MinMax<T> {

        private final T min;

        private final T max;

        private MinMax(final T min, final T max) {
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

    private class AppearanceOrderMethodInvocationComparator
        implements Comparator<MethodInvocation> {

        @Override
        public int compare(final MethodInvocation lhs, final MethodInvocation rhs) {
            if (invocationNesting.containsMapping(lhs, rhs)) {
                return -1;
            }
            else if (invocationNesting.containsMapping(rhs, lhs)) {
                return 1;
            }
            else {
                return new CompareToBuilder()
                    .append(translateInitialLineNo(lhs.getInitialLineNo()),
                        translateInitialLineNo(rhs.getInitialLineNo()))
                    .append(lhs.getColumnNo(), rhs.getColumnNo())
                    .toComparison();
            }
        }
    }

    private static class UniqueCallerCalleeMethodInvocationFilter
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

        @SuppressWarnings("PMD.JUnit4TestShouldUseTestAnnotation")
        @Override
        public boolean test(MethodInvocation methodInvocation) {
            return set.add(methodInvocation);
        }
    }
}
