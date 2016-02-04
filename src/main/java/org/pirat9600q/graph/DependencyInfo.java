package org.pirat9600q.graph;

import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;
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

    public boolean hasMethodDependencies(final MethodInfo caller) {
        return !getMethodDependencies(caller).isEmpty();
    }

    public static DependencyInfoBuilder builder() {
        return new DependencyInfoBuilder();
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
