package com.github.sevntu.checkstyle.module;

import com.github.sevntu.checkstyle.domain.Dependencies;
import com.github.sevntu.checkstyle.ordering.MethodOrder;
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

        final MethodOrder initialMethodOrder = new MethodOrder(dependencies);
        final MethodOrder optimizedMethodOrder = reorderer.reorder(initialMethodOrder);
        logFirstMethodOutOfOrder(module, optimizedMethodOrder);
    }

    private void logFirstMethodOutOfOrder(
        MethodCallDependencyModule check, MethodOrder optimizedMethodOrder) {

        optimizedMethodOrder.getMethods().stream()
            .filter(method ->
                optimizedMethodOrder.getMethodIndex(method) != method.getInitialIndex())
            .findFirst()
            .ifPresent(method -> {
                final int difference =
                    method.getInitialIndex() - optimizedMethodOrder.getMethodIndex(method);
                check.log(method.getInitialLineNo(), MethodCallDependencyModule.MSG_KEY,
                    method.getSignature(), difference);
            });
    }
}
