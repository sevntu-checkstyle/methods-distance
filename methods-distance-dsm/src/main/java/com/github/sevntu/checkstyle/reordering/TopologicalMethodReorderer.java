package com.github.sevntu.checkstyle.reordering;

import com.github.sevntu.checkstyle.analysis.PenaltyCalculator;
import com.github.sevntu.checkstyle.ordering.Method;
import com.github.sevntu.checkstyle.ordering.Ordering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TopologicalMethodReorderer implements MethodReorderer {

    private static final int DEFAULT_SCREEN_LINES_COUNT = 50;

    private final int screenLinesCount = DEFAULT_SCREEN_LINES_COUNT;

    private final PenaltyCalculator calculator = new PenaltyCalculator();

    @Override
    public Ordering reorder(final Ordering initialOrdering) {
        Ordering currentOrdering = initialOrdering.reorder(
            breadthFirstOrder(initialOrdering, getFirstMethod(initialOrdering)));
        currentOrdering = overrideMethodGrouping(currentOrdering);
        currentOrdering = overloadMethodGrouping(currentOrdering);
        currentOrdering = methodDependenciesRoundShiftOptimization(currentOrdering);
        currentOrdering = methodDependenciesPullingOptimization(currentOrdering);
        return currentOrdering;
    }

    private Ordering methodDependenciesRoundShiftOptimization(final Ordering ordering) {
        Ordering currentOrdering = ordering;
        for (final Method caller : currentOrdering.getMethods()) {
            final List<Method> dependencies =
                currentOrdering.getMethodDependenciesInAppearanceOrder(caller);
            if (dependencies.size() > 1) {
                final int from = currentOrdering.getMethodIndex(caller);
                final int till = from + dependencies.size();
                final List<Method> optimized = roundShift(currentOrdering.getMethods(), from, till);
                final Ordering optimizedOrdering = currentOrdering.reorder(optimized);
                currentOrdering = getBestOrdering(currentOrdering, optimizedOrdering);
            }
        }
        return currentOrdering;
    }

    private Ordering methodDependenciesPullingOptimization(final Ordering startingOrdering) {
        Ordering currentOrdering = startingOrdering;
        for (final Method caller : startingOrdering.getMethods()) {
            final List<Method> dependencies =
                currentOrdering.getMethodDependenciesInAppearanceOrder(caller);
            if (!dependencies.isEmpty()) {
                final int callerIndex = currentOrdering.getMethodIndex(caller);
                boolean allDependenciesLocatedAfterCaller = true;
                for (final Method callee : dependencies) {
                    allDependenciesLocatedAfterCaller = allDependenciesLocatedAfterCaller
                        && currentOrdering.getMethodIndex(callee) > callerIndex;
                }
                if (allDependenciesLocatedAfterCaller) {
                    final List<Method> allMethods = new ArrayList<>(currentOrdering.getMethods());
                    final List<Method> subList = allMethods.subList(callerIndex, allMethods.size());
                    subList.removeAll(dependencies);
                    subList.addAll(1, dependencies);
                    final Ordering optimizedOrdering = currentOrdering.reorder(allMethods);
                    currentOrdering = getBestOrdering(currentOrdering, optimizedOrdering);
                }
            }
        }
        return currentOrdering;
    }

    private Ordering overloadMethodGrouping(final Ordering ordering) {
        final List<List<Method>> overloadGroups = ordering.getMethods().stream()
            .collect(Collectors.groupingBy(Method::getName))
            .values().stream().filter(list -> list.size() > 1).collect(Collectors.toList());
        return methodGroupsGrouping(ordering, overloadGroups);
    }

    private Ordering overrideMethodGrouping(final Ordering ordering) {
        final List<Method> overrideMethods = ordering.getMethods().stream()
            .filter(Method::isOverride)
            .collect(Collectors.toList());
        if (overrideMethods.size() > 1) {
            return methodGroupsGrouping(ordering, Collections.singletonList(overrideMethods));
        }
        else {
            return ordering;
        }
    }

    private Ordering methodGroupsGrouping(final Ordering startingOrdering,
        final List<List<Method>> groups) {
        Ordering currentOrdering = startingOrdering;
        for (final List<Method> methodsGroup : groups) {
            final Method lastMethod = methodsGroup.stream()
                .sorted(new MethodIndexComparator(currentOrdering).reversed())
                .findFirst().get();
            final List<Method> nonLastMethodsInGroup = new ArrayList<>(methodsGroup);
            nonLastMethodsInGroup.remove(lastMethod);
            final List<Method> optimizedOrdering = new ArrayList<>(currentOrdering.getMethods());
            optimizedOrdering.removeAll(nonLastMethodsInGroup);
            optimizedOrdering.addAll(currentOrdering.getMethodIndex(lastMethod),
                nonLastMethodsInGroup);
            currentOrdering =
                getBestOrdering(currentOrdering, currentOrdering.reorder(optimizedOrdering));
        }
        return currentOrdering;
    }

    private Ordering getBestOrdering(final Ordering lhs, final Ordering rhs) {
        if (calculator.getPenalty(lhs, screenLinesCount)
            < calculator.getPenalty(rhs, screenLinesCount)) {
            return lhs;
        }
        else {
            return rhs;
        }
    }

    private static <T> List<T> roundShift(final List<T> list, final int from, final int till) {
        final List<T> result = new ArrayList<>(list);
        final List<T> sublist = result.subList(from, till);
        final T head = sublist.remove(0);
        sublist.add(head);
        return result;
    }

    private static Method getFirstMethod(final Ordering ordering) {
        return Stream.<Supplier<Optional<Method>>>of(
            () -> ordering.getMethods().stream()
                .filter(Method::isCtor)
                .sorted((lhs, rhs) -> Integer.compare(lhs.getArgCount(), rhs.getArgCount()))
                .findFirst(),
            () -> ordering.getMethods().stream()
                .sorted((lhs, rhs) -> {
                    final int lhsDeps = ordering.getMethodDependenciesInAppearanceOrder(lhs).size();
                    final int rhsDeps = ordering.getMethodDependenciesInAppearanceOrder(rhs).size();
                    return Integer.compare(rhsDeps, lhsDeps);
                })
                .findFirst(),
            () -> Optional.of(ordering.getMethods().get(0)))
            .map(Supplier::get)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst().get();
    }

    private static List<Method> breadthFirstOrder(final Ordering ordering,
        final Method startMethod) {
        final Queue<Method> queue = new LinkedList<>();
        final List<Method> result = new ArrayList<>();
        queue.add(startMethod);
        while (result.size() < ordering.getMethods().size()) {
            if (queue.isEmpty()) {
                final Optional<Method> firstUnvisitedMethod = ordering.getMethods().stream()
                    .filter(method -> !result.contains(method))
                    .findFirst();
                queue.add(firstUnvisitedMethod.get());
            }
            else {
                final Method head = queue.remove();
                if (!result.contains(head)) {
                    result.add(head);
                    ordering.getMethodDependenciesInAppearanceOrder(head).stream()
                        .filter(callee -> !result.contains(callee))
                        .forEach(queue::add);
                }
            }
        }
        return result;
    }

    private static final class MethodIndexComparator implements Comparator<Method> {

        private final Ordering ordering;

        private MethodIndexComparator(final Ordering ordering) {
            this.ordering = ordering;
        }

        @Override
        public int compare(final Method lhs, final Method rhs) {
            return Integer.compare(ordering.getMethodIndex(lhs), ordering.getMethodIndex(rhs));
        }
    }
}
