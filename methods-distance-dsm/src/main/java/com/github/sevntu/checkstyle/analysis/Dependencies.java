package com.github.sevntu.checkstyle.analysis;

import java.util.List;

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

    public List<ResolvedCall> getResolvedCalls() {
        return resolvedCalls;
    }
}
