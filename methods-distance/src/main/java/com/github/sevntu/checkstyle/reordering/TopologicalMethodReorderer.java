////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2019 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle.reordering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.sevntu.checkstyle.ordering.Method;
import com.github.sevntu.checkstyle.ordering.MethodOrder;
import com.github.sevntu.checkstyle.ordering.PenaltyCalculator;

public class TopologicalMethodReorderer implements MethodReorderer {

    private static final int DEFAULT_SCREEN_LINES_COUNT = 50;

    private final int screenLinesCount = DEFAULT_SCREEN_LINES_COUNT;

    private final PenaltyCalculator calculator = new PenaltyCalculator();

    @Override
    public MethodOrder reorder(MethodOrder initialMethodOrder) {
        final MethodOrder topologicalMethodOrder = initialMethodOrder.reorder(
            breadthFirstOrder(initialMethodOrder, getFirstMethod(initialMethodOrder)));
        return Function.<MethodOrder>identity()
            .andThen(this::overrideMethodGrouping)
            .andThen(this::accessorMethodGrouping)
            .andThen(this::overloadMethodGrouping)
            .andThen(this::methodDependenciesRelativeOrderOptimization)
            .andThen(this::methodDependenciesDistanceOptimization)
            .andThen(this::pullAllCtorsToStart)
            .apply(topologicalMethodOrder);
    }

    private MethodOrder pullAllCtorsToStart(MethodOrder methodOrder) {
        final List<Method> allCtors = methodOrder.getMethods().stream()
            .filter(Method::isCtor)
            .collect(Collectors.toList());
        final List<Method> newOrder = new ArrayList<>(methodOrder.getMethods());
        newOrder.removeAll(allCtors);
        newOrder.addAll(0, allCtors);
        return methodOrder.reorder(newOrder);
    }

    private MethodOrder methodDependenciesRelativeOrderOptimization(MethodOrder methodOrder) {
        MethodOrder currentMethodOrder = methodOrder;
        for (final Method caller : currentMethodOrder.getMethods()) {
            final List<Method> dependenciesInAppearanceOrder =
                currentMethodOrder.getMethodDependenciesInAppearanceOrder(caller);
            if (dependenciesInAppearanceOrder.size() > 1) {
                final List<Integer> dependenciesIndices = dependenciesInAppearanceOrder.stream()
                    .map(currentMethodOrder::getMethodIndex)
                    .sorted(Integer::compare)
                    .collect(Collectors.toList());
                final List<Method> optimized = new ArrayList<>(currentMethodOrder.getMethods());
                optimized.removeAll(dependenciesInAppearanceOrder);
                for (int i = 0; i < dependenciesIndices.size(); ++i) {
                    optimized.add(dependenciesIndices.get(i), dependenciesInAppearanceOrder.get(i));
                }
                final MethodOrder optimizedMethodOrder = currentMethodOrder.reorder(optimized);
                currentMethodOrder = getBestOrdering(currentMethodOrder, optimizedMethodOrder);
            }
        }
        return currentMethodOrder;
    }

    private MethodOrder methodDependenciesDistanceOptimization(MethodOrder startingOrder) {
        MethodOrder currentOrder = startingOrder;
        for (final Method caller : startingOrder.getMethods()) {
            final List<Method> dependencies =
                currentOrder.getMethodDependenciesInAppearanceOrder(caller);
            if (!dependencies.isEmpty()) {
                final int callerIndex = currentOrder.getMethodIndex(caller);
                boolean allDependenciesLocatedAfterCaller = true;
                for (final Method callee : dependencies) {
                    allDependenciesLocatedAfterCaller = allDependenciesLocatedAfterCaller
                        && currentOrder.getMethodIndex(callee) > callerIndex;
                }
                if (allDependenciesLocatedAfterCaller) {
                    final List<Method> allMethods = new ArrayList<>(currentOrder.getMethods());
                    final List<Method> subList = allMethods.subList(callerIndex, allMethods.size());
                    subList.removeAll(dependencies);
                    subList.addAll(1, dependencies);
                    final MethodOrder optimizedMethodOrder = currentOrder.reorder(allMethods);
                    currentOrder = getBestOrdering(currentOrder, optimizedMethodOrder);
                }
            }
        }
        return currentOrder;
    }

    private MethodOrder overloadMethodGrouping(MethodOrder methodOrder) {
        final List<List<Method>> overloadGroups = methodOrder.getMethods().stream()
            .collect(Collectors.groupingBy(Method::getName))
            .values().stream().filter(list -> list.size() > 1).collect(Collectors.toList());
        return methodGroupsGrouping(methodOrder, overloadGroups);
    }

    private MethodOrder overrideMethodGrouping(MethodOrder methodOrder) {
        final MethodOrder result;
        final List<Method> overrideMethods = methodOrder.getMethods().stream()
            .filter(Method::isOverride)
            .collect(Collectors.toList());
        if (overrideMethods.size() > 1) {
            result = methodGroupsGrouping(methodOrder, Collections.singletonList(overrideMethods));
        }
        else {
            result = methodOrder;
        }
        return result;
    }

    private MethodOrder accessorMethodGrouping(MethodOrder methodOrder) {
        final List<List<Method>> accessorGroups = methodOrder.getMethods().stream()
            .filter(method -> method.isGetter() || method.isSetter())
            .collect(Collectors.groupingBy(Method::getAccessiblePropertyName))
            .values().stream()
            .filter(group -> group.size() > 1)
            .collect(Collectors.toList());
        return methodGroupsGrouping(methodOrder, accessorGroups);
    }

    private MethodOrder methodGroupsGrouping(MethodOrder startingMethodOrder,
                                             final List<List<Method>> groups) {

        MethodOrder currentMethodOrder = startingMethodOrder;
        for (final List<Method> methodsGroup : groups) {
            final Method lastMethod = methodsGroup.stream()
                .sorted(new MethodIndexComparator(currentMethodOrder).reversed())
                .findFirst().get();
            final List<Method> nonLastMethodsInGroup = new ArrayList<>(methodsGroup);
            nonLastMethodsInGroup.remove(lastMethod);
            final List<Method> optimizedOrdering = new ArrayList<>(currentMethodOrder.getMethods());
            optimizedOrdering.removeAll(nonLastMethodsInGroup);
            optimizedOrdering.addAll(currentMethodOrder.getMethodIndex(lastMethod),
                nonLastMethodsInGroup);
            currentMethodOrder =
                getBestOrdering(currentMethodOrder, currentMethodOrder.reorder(optimizedOrdering));
        }
        return currentMethodOrder;
    }

    private MethodOrder getBestOrdering(MethodOrder lhs, MethodOrder rhs) {
        final MethodOrder result;
        if (calculator.getPenalty(lhs, screenLinesCount)
            < calculator.getPenalty(rhs, screenLinesCount)) {
            result = lhs;
        }
        else {
            result = rhs;
        }
        return result;
    }

    private static Method getFirstMethod(MethodOrder order) {
        return Stream.<Supplier<Optional<Method>>>of(
            () -> {
                return order.getMethods().stream()
                .filter(Method::isCtor)
                .sorted((lhs, rhs) -> Integer.compare(lhs.getArgCount(), rhs.getArgCount()))
                .findFirst();
            },
            () -> {
                return order.getMethods().stream()
                .sorted((lhs, rhs) -> {
                    final int lhsDeps = order.getMethodDependenciesInAppearanceOrder(lhs).size();
                    final int rhsDeps = order.getMethodDependenciesInAppearanceOrder(rhs).size();
                    return Integer.compare(rhsDeps, lhsDeps);
                })
                .findFirst();
            },
            () -> Optional.of(order.getMethods().get(0)))
            .map(Supplier::get)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst().get();
    }

    private static List<Method> breadthFirstOrder(MethodOrder methodOrder, Method startMethod) {
        final Queue<Method> queue = new LinkedList<>();
        final List<Method> result = new ArrayList<>();
        queue.add(startMethod);
        while (result.size() < methodOrder.getMethods().size()) {
            if (queue.isEmpty()) {
                methodOrder.getMethods().stream()
                    .filter(method -> !result.contains(method))
                    .findFirst()
                    .ifPresent(queue::add);
            }
            else {
                final Method head = queue.remove();
                if (!result.contains(head)) {
                    result.add(head);
                    methodOrder.getMethodDependenciesInAppearanceOrder(head).stream()
                        .filter(callee -> !result.contains(callee))
                        .forEach(queue::add);
                }
            }
        }
        return result;
    }

    private static final class MethodIndexComparator implements Comparator<Method> {

        private final MethodOrder order;

        private MethodIndexComparator(MethodOrder methodOrder) {
            this.order = methodOrder;
        }

        @Override
        public int compare(Method lhs, Method rhs) {
            return Integer.compare(order.getMethodIndex(lhs), order.getMethodIndex(rhs));
        }
    }
}
