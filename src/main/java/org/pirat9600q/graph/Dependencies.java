package org.pirat9600q.graph;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import org.apache.commons.lang3.builder.CompareToBuilder;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
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
        final Set<ResolvedCall> uniqueOccurrences =
                new TreeSet<>(new UniqueCallerCalleeCallOccurrencesComparator());
        return resolvedCalls.stream()
                .filter(mco -> mco.getCaller().equals(caller))
                .sorted(new AppearanceOrderMethodCallOccurrenceComparator())
                .filter(uniqueOccurrences::add)
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
        final Set<ResolvedCall> unique =
                new TreeSet<>(new UniqueCallerCalleeCallOccurrencesComparator());
        return resolvedCalls.stream()
                .filter(mco -> mco.getCallee().equals(callee) && unique.add(mco))
                .map(ResolvedCall::getCaller)
                .collect(Collectors.toList());
    }

    public boolean isMethodDependsOn(final MethodDefinition caller, final MethodDefinition callee) {
        return resolvedCalls.stream()
                .anyMatch(mco -> mco.getCaller().equals(caller) && mco.getCallee().equals(callee));
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
