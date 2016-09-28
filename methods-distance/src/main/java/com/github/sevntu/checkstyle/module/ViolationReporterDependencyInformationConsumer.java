package com.github.sevntu.checkstyle.module;

import com.github.sevntu.checkstyle.domain.Dependencies;
import com.github.sevntu.checkstyle.ordering.Ordering;
import com.github.sevntu.checkstyle.reordering.MethodReorderer;
import com.github.sevntu.checkstyle.reordering.TopologicalMethodReorderer;

public class ViolationReporterDependencyInformationConsumer
    implements DependencyInformationConsumer {

    private final MethodReorderer reorderer = new TopologicalMethodReorderer();

    private MethodCallDependencyModule module;

    @Override
    public void setModule(MethodCallDependencyModule module) {
        this.module = module;
    }

    @Override
    public void accept(String filePath, Dependencies dependencies) {

        final Ordering initialOrdering = new Ordering(dependencies);
        final Ordering optimizedOrdering = reorderer.reorder(initialOrdering);
        logFirstMethodOutOfOrder(module, optimizedOrdering);
    }

    private void logFirstMethodOutOfOrder(
        MethodCallDependencyModule check, Ordering optimizedOrdering) {

        optimizedOrdering.getMethods().stream()
            .filter(method ->
                optimizedOrdering.getMethodIndex(method) != method.getInitialIndex())
            .findFirst()
            .ifPresent(method -> {
                final int difference =
                    method.getInitialIndex() - optimizedOrdering.getMethodIndex(method);
                check.log(method.getInitialLineNo(), MethodCallDependencyModule.MSG_KEY,
                    method.getSignature(), difference);
            });
    }
}
