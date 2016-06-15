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
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TopologicalMethodReorderer implements MethodReorderer {

    private static final int DEFAULT_SCREEN_LINES_COUNT = 50;

    private final int screenLinesCount = DEFAULT_SCREEN_LINES_COUNT;

    private final PenaltyCalculator calculator = new PenaltyCalculator();

    @Override
    public Ordering reorder(Ordering initialOrdering) {
        final Ordering topologicalOrdering = initialOrdering.reorder(
            breadthFirstOrder(initialOrdering, getFirstMethod(initialOrdering)));
        return Function.<Ordering>identity()
            .andThen(this::overrideMethodGrouping)
            .andThen(this::accessorMethodGrouping)
            .andThen(this::overloadMethodGrouping)
            .andThen(this::methodDependenciesRelativeOrderOptimization)
            .andThen(this::methodDependenciesDistanceOptimization)
            .andThen(this::pullAllCtorsToStart)
            .apply(topologicalOrdering);
    }

    private Ordering pullAllCtorsToStart(Ordering ordering) {
        final List<Method> allCtors = ordering.getMethods().stream()
            .filter(Method::isCtor)
            .collect(Collectors.toList());
        final List<Method> newOrder = new ArrayList<>(ordering.getMethods());
        newOrder.removeAll(allCtors);
        newOrder.addAll(0, allCtors);
        return ordering.reorder(newOrder);
    }

    private Ordering methodDependenciesRelativeOrderOptimization(Ordering ordering) {
        Ordering currentOrdering = ordering;
        for (final Method caller : currentOrdering.getMethods()) {
            final List<Method> dependenciesInAppearanceOrder =
                currentOrdering.getMethodDependenciesInAppearanceOrder(caller);
            if (dependenciesInAppearanceOrder.size() > 1) {
                final List<Integer> dependenciesIndices = dependenciesInAppearanceOrder.stream()
                    .map(currentOrdering::getMethodIndex)
                    .sorted(Integer::compare)
                    .collect(Collectors.toList());
                final List<Method> optimized = new ArrayList<>(currentOrdering.getMethods());
                optimized.removeAll(dependenciesInAppearanceOrder);
                for (int i = 0; i < dependenciesIndices.size(); ++i) {
                    optimized.add(dependenciesIndices.get(i), dependenciesInAppearanceOrder.get(i));
                }
                final Ordering optimizedOrdering = currentOrdering.reorder(optimized);
                currentOrdering = getBestOrdering(currentOrdering, optimizedOrdering);
            }
        }
        return currentOrdering;
    }

    private Ordering methodDependenciesDistanceOptimization(Ordering startingOrdering) {
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

    private Ordering overloadMethodGrouping(Ordering ordering) {
        final List<List<Method>> overloadGroups = ordering.getMethods().stream()
            .collect(Collectors.groupingBy(Method::getName))
            .values().stream().filter(list -> list.size() > 1).collect(Collectors.toList());
        return methodGroupsGrouping(ordering, overloadGroups);
    }

    private Ordering overrideMethodGrouping(Ordering ordering) {
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

    private Ordering accessorMethodGrouping(Ordering ordering) {
        final List<List<Method>> accessorGroups = ordering.getMethods().stream()
            .filter(method -> method.isGetter() || method.isSetter())
            .collect(Collectors.groupingBy(Method::getAccessiblePropertyName))
            .values().stream()
            .filter(group -> group.size() > 1)
            .collect(Collectors.toList());
        return methodGroupsGrouping(ordering, accessorGroups);
    }

    private Ordering methodGroupsGrouping(Ordering startingOrdering,
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

    private Ordering getBestOrdering(Ordering lhs, Ordering rhs) {
        if (calculator.getPenalty(lhs, screenLinesCount)
            < calculator.getPenalty(rhs, screenLinesCount)) {
            return lhs;
        }
        else {
            return rhs;
        }
    }

    private static Method getFirstMethod(Ordering ordering) {
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

    private static List<Method> breadthFirstOrder(Ordering ordering, Method startMethod) {
        final Queue<Method> queue = new LinkedList<>();
        final List<Method> result = new ArrayList<>();
        queue.add(startMethod);
        while (result.size() < ordering.getMethods().size()) {
            if (queue.isEmpty()) {
                ordering.getMethods().stream()
                    .filter(method -> !result.contains(method))
                    .findFirst()
                    .ifPresent(queue::add);
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

        private MethodIndexComparator(Ordering ordering) {
            this.ordering = ordering;
        }

        @Override
        public int compare(Method lhs, Method rhs) {
            return Integer.compare(ordering.getMethodIndex(lhs), ordering.getMethodIndex(rhs));
        }
    }
}
