package com.github.sevntu.checkstyle.domain;

import java.util.List;

public class Dependencies {

    private final ClassDefinition classDefinition;

    private final List<ResolvedCall> resolvedCalls;

    public Dependencies(ClassDefinition classDefinition, List<ResolvedCall> resolvedCalls) {
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
