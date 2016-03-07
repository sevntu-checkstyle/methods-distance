package org.pirat9600q.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExpectedDependencies {

    private final List<String> methodSignatures = new ArrayList<>();

    private final List<MethodInvocation> methodDependencies = new ArrayList<>();

    private ExpectedDependencies() {}

    public static WithCallsToOrNewMethodOrGet build() {
        return new Builder();
    }

    public List<String> getMethods() {
        return methodSignatures;
    }

    public List<MethodInvocation> getDependencies() {
        return methodDependencies;
    }

    public List<MethodInvocation> getInvocationsFromMethod(final String callerSignature) {
        final int callerIndex = methodSignatures.indexOf(callerSignature);
        return methodDependencies.stream()
                .filter(md -> md.caller == callerIndex)
                .collect(Collectors.toList());
    }

    public String getMethodByIndex(int index) {
        return methodSignatures.get(index);
    }

    public static class MethodInvocation {

        public int caller;

        public int callee;

        public int atLine;

        public int atCol;

        private MethodInvocation() {}
    }

    public static class Builder implements WithCallsToOrNewMethodOrGet, WithLineCol {

        private final ExpectedDependencies ed = new ExpectedDependencies();

        private MethodInvocation currentDependency;

        private Builder() { }

        @Override
        public WithCallsToOrNewMethodOrGet method(String signature) {
            ed.methodSignatures.add(signature);
            return this;
        }

        @Override
        public WithLineCol callsTo(int calleeIndex) {
            currentDependency = new MethodInvocation();
            currentDependency.caller = ed.methodSignatures.size() - 1;
            currentDependency.callee = calleeIndex;
            return this;
        }

        @Override
        public ExpectedDependencies get() {
            return ed;
        }

        @Override
        public WithCallsToOrNewMethodOrGet at(int line, int col) {
            currentDependency.atLine = line;
            currentDependency.atCol = col;
            ed.methodDependencies.add(currentDependency);
            currentDependency = null;
            return this;
        }
    }

    public interface WithCallsToOrNewMethodOrGet {
        WithCallsToOrNewMethodOrGet method(String signature);
        WithLineCol callsTo(int calleeIndex);
        ExpectedDependencies get();
    }

    public interface WithLineCol {
        WithCallsToOrNewMethodOrGet at(int line, int col);
    }
}