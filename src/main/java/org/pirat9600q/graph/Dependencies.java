package org.pirat9600q.graph;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import org.apache.commons.lang3.builder.CompareToBuilder;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Dependencies {

    private final ClassDefinition classDefinition;

    private final List<ResolvedCall> resolvedCalls;

    public Dependencies(final ClassDefinition classDefinition,
                        final List<ResolvedCall> resolvedCalls) {
        this.classDefinition = classDefinition;
        this.resolvedCalls = resolvedCalls;
    }

    public ClassDefinition getClassDefinition() {
        return classDefinition;
    }

    public List<MethodDefinition> getMethods() {
        return classDefinition.getMethods();
    }

    public MethodDefinition getMethodByIndex(final int index) {
        return classDefinition.getMethodByIndex(index);
    }

    public List<MethodDefinition> getMethodDependencies(final MethodDefinition caller) {
        return resolvedCalls.stream()
                .filter(mco -> mco.getCaller().equals(caller))
                .sorted(new AppearanceOrderMethodCallOccurrenceComparator())
                .filter(new UniqueCallerCalleeCallsFilter())
                .map(ResolvedCall::getCallee)
                .collect(Collectors.toList());
    }

    public List<Integer> getMethodDependenciesAsIndices(final MethodDefinition caller) {
        return getMethodDependencies(caller).stream()
                .map(MethodDefinition::getIndex)
                .collect(Collectors.toList());
    }

    public boolean hasMethodDependencies(final MethodDefinition caller) {
        return !getMethodDependencies(caller).isEmpty();
    }

    public boolean hasMethodDependants(final MethodDefinition callee) {
        return !getMethodDependants(callee).isEmpty();
    }

    public List<MethodDefinition> getMethodDependants(final MethodDefinition callee) {
        return resolvedCalls.stream()
                .filter(mco -> mco.getCallee().equals(callee))
                .filter(new UniqueCallerCalleeCallsFilter())
                .map(ResolvedCall::getCaller)
                .collect(Collectors.toList());
    }

    public boolean isMethodDependsOn(final MethodDefinition caller, final MethodDefinition callee) {
        return resolvedCalls.stream()
                .anyMatch(mco -> mco.getCaller().equals(caller) && mco.getCallee().equals(callee));
    }

    public int getTotalSumOfMethodDistances() {
        return classDefinition.getMethods().stream()
                .collect(Collectors.summingInt(caller -> getMethodDependencies(caller).stream()
                        .collect(Collectors.summingInt(callee ->
                                Math.abs(caller.getIndexDistanceTo(callee))))));
    }

    public int getDeclarationBeforeUsageCases() {
        return (int) resolvedCalls.stream()
                .filter(new UniqueCallerCalleeCallsFilter())
                .filter(rc -> rc.getCaller().getIndexDistanceTo(rc.getCallee()) < 0)
                .count();
    }

    public int getOverrideGroupSplitCases() {
        final List<Integer> overrideMethodIndices = classDefinition.getMethods().stream()
                .filter(MethodDefinition::isOverride)
                .map(MethodDefinition::getIndex)
                .collect(Collectors.toList());
        if (overrideMethodIndices.isEmpty()) {
            return 0;
        }
        else {
            final MinMax<Integer> firstLastIndices = minMax(overrideMethodIndices);
            final int firstLastDistance = firstLastIndices.getMax() - firstLastIndices.getMin();
            final int totalOverrideMethods = overrideMethodIndices.size();
            return firstLastDistance - totalOverrideMethods + 1;
        }
    }

    public int getOverloadGroupSplitCases() {
        return classDefinition.getMethods()
                .stream()
                .collect(Collectors.groupingBy(MethodDefinition::getName))
                .entrySet().stream()
                .map(e -> {
                    final List<Integer> overloadGroupIndices = e.getValue().stream()
                            .map(MethodDefinition::getIndex).collect(Collectors.toList());
                    final MinMax<Integer> bounds = minMax(overloadGroupIndices);
                    return bounds.getMax() - bounds.getMin() - overloadGroupIndices.size() + 1;
                })
                .reduce(0, (a1, a2) -> a1 + a2);
    }

    public int getRelativeOrderInconsistencyCases() {
        return classDefinition.getMethods().stream()
                .map(caller -> {
                    int maxCalleeIndex = 0;
                    int orderViolations = 0;
                    for (final MethodDefinition callee : getMethodDependencies(caller)) {
                        final int calleeIndex = callee.getIndex();
                        if (calleeIndex < maxCalleeIndex) {
                            ++orderViolations;
                        }
                        else {
                            maxCalleeIndex = calleeIndex;
                        }
                    }
                    return orderViolations;
                })
                .reduce(0, (a1, a2) -> a1 + a2);
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

    private static class AppearanceOrderMethodCallOccurrenceComparator implements
            Comparator<ResolvedCall> {
        @Override
        public int compare(ResolvedCall left, ResolvedCall right) {
            if (isNestedInside(left.getAstNode(), right.getAstNode())) {
                return -1;
            }
            else if (isNestedInside(right.getAstNode(), left.getAstNode())) {
                return 1;
            }
            else {
                return new CompareToBuilder()
                        .append(left.getAstNode().getLineNo(), right.getAstNode().getLineNo())
                        .append(left.getAstNode().getColumnNo(), right.getAstNode().getColumnNo())
                        .toComparison();
            }
        }

        private static boolean isNestedInside(final DetailAST node, final DetailAST enclosingNode) {
            for (DetailAST parent = node.getParent(); parent != null; parent = parent.getParent()) {
                if (parent.getLineNo() == enclosingNode.getLineNo()
                    && parent.getColumnNo() == enclosingNode.getColumnNo()) {
                    return true;
                }
            }
            return false;
        }
    }

    private class UniqueCallerCalleeCallsFilter implements Predicate<ResolvedCall> {

        private final Set<ResolvedCall> unique =
                new TreeSet<>(new UniqueCallerCalleeCallOccurrencesComparator());

        @Override
        public boolean test(ResolvedCall resolvedCall) {
            return unique.add(resolvedCall);
        }
    }

    private class UniqueCallerCalleeCallOccurrencesComparator implements
            Comparator<ResolvedCall> {
        @Override
        public int compare(final ResolvedCall left, final ResolvedCall right) {
            return new CompareToBuilder()
                    .append(left.getCaller().getIndex(), right.getCaller().getIndex())
                    .append(left.getCallee().getIndex(), right.getCallee().getIndex())
                    .toComparison();
        }
    }
}
