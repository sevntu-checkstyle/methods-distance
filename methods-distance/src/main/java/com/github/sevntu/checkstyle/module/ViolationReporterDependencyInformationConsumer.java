////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2018 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle.module;

import com.github.sevntu.checkstyle.domain.Dependencies;
import com.github.sevntu.checkstyle.ordering.MethodOrder;
import com.github.sevntu.checkstyle.reordering.MethodReorderer;
import com.github.sevntu.checkstyle.reordering.TopologicalMethodReorderer;

public class ViolationReporterDependencyInformationConsumer
    implements DependencyInformationConsumer {

    private final MethodReorderer reorderer = new TopologicalMethodReorderer();

    private MethodCallDependencyCheckstyleModule module;

    @Override
    public void setModule(MethodCallDependencyCheckstyleModule module) {
        this.module = module;
    }

    @Override
    public void accept(String filePath, Dependencies dependencies) {

        final MethodOrder initialMethodOrder = new MethodOrder(dependencies);
        final MethodOrder optimizedMethodOrder = reorderer.reorder(initialMethodOrder);
        logFirstMethodOutOfOrder(module, optimizedMethodOrder);
    }

    private void logFirstMethodOutOfOrder(
        MethodCallDependencyCheckstyleModule check, MethodOrder optimizedMethodOrder) {

        optimizedMethodOrder.getMethods().stream()
            .filter(method ->
                optimizedMethodOrder.getMethodIndex(method) != method.getInitialIndex())
            .findFirst()
            .ifPresent(method -> {
                final int difference =
                    method.getInitialIndex() - optimizedMethodOrder.getMethodIndex(method);
                check.log(method.getInitialLineNo(), MethodCallDependencyCheckstyleModule.MSG_KEY,
                    method.getSignature(), difference);
            });
    }
}
