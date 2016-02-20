package org.pirat9600q.graph;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang.builder.CompareToBuilder;

import java.util.*;
import java.util.stream.Collectors;

public final class DependencyInfo {

    private Set<MethodInfo> methods;

    private Set<MethodCallInfo> methodCalls;

    private DependencyInfo(final Set<MethodInfo> methods, final Set<MethodCallInfo> methodCalls) {
        this.methods = methods;
        this.methodCalls = methodCalls;
    }

    public Set<MethodInfo> getMethods() {
        return methods;
    }

    public SortedSet<MethodInfo> getMethodsSortedByIndex() {
        final SortedSet<MethodInfo> set =
                new TreeSet<>((ml, mr) -> Integer.compare(ml.getIndex(), mr.getIndex()));
        set.addAll(methods);
        return set;
    }

    public Set<MethodCallInfo> getMethodCalls() {
        return methodCalls;
    }

    public MethodInfo getMethodByIndex(final int index) {
        return methods.stream().filter(m -> m.getIndex() == index).findFirst().get();
    }

    public Set<MethodInfo> getMethodDependencies(final MethodInfo caller) {
        return methodCalls.stream()
                .filter(mc -> mc.getCallerIndex() == caller.getIndex())
                .map(methodCall -> getMethodByIndex(methodCall.getCalleeIndex()))
                .collect(Collectors.toSet());
    }

    public Set<MethodInfo> getMethodDependants(final MethodInfo callee) {
        return methodCalls.stream()
                .filter(mc -> mc.getCalleeIndex() == callee.getIndex())
                .map(methodCall -> getMethodByIndex(methodCall.getCallerIndex()))
                .collect(Collectors.toSet());
    }

    public boolean isMethodDependsOn(final MethodInfo caller, final MethodInfo callee) {
        return methodCalls.stream()
                .anyMatch(mci ->
                    mci.getCallerIndex() == caller.getIndex()
                    && mci.getCalleeIndex() == callee.getIndex());
    }

    public List<Integer> getMethodDependenciesIndicesOrderedByAppearance(final MethodInfo caller) {
        return methodCalls.stream()
                .filter(mci -> mci.getCallerIndex() == caller.getIndex())
                .sorted(new AppearanceOrderComparator())
                .map(MethodCallInfo::getCalleeIndex)
                .distinct()
                .collect(Collectors.toList());
    }

    public boolean hasMethodDependencies(final MethodInfo caller) {
        return !getMethodDependencies(caller).isEmpty();
    }

    public boolean isSomeMethodDependsOn(final MethodInfo callee) {
        return !getMethodDependants(callee).isEmpty();
    }

    public static DependencyInfoBuilder builder() {
        return new DependencyInfoBuilder();
    }

    private static final class AppearanceOrderComparator implements Comparator<MethodCallInfo> {

        @Override
        public int compare(final MethodCallInfo left, final MethodCallInfo right) {
            return new CompareToBuilder()
                    .append(left.getLineNo(), right.getLineNo())
                    .append(left.getColumnNo(), right.getColumnNo())
                    .toComparison();
        }
    }

    public static final class DependencyInfoBuilder {

        private Set<MethodInfo> methods = new HashSet<>();

        private Set<MethodCallInfo> methodCalls = new HashSet<>();

        private DependencyInfoBuilder() { }

        public DependencyInfoBuilder addMethod(final MethodInfo method) {
            methods.add(method);
            return this;
        }

        public DependencyInfoBuilder addMethodCall(final MethodCallInfo methodCall) {
            methodCalls.add(methodCall);
            return this;
        }

        public DependencyInfo get() {
            return new DependencyInfo(
                    ImmutableSet.copyOf(methods), ImmutableSet.copyOf(methodCalls));
        }
    }
}
