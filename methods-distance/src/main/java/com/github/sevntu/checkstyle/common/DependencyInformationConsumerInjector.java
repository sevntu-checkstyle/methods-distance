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

package com.github.sevntu.checkstyle.common;

import com.github.sevntu.checkstyle.module.MethodCallDependencyCheckstyleModule;
import com.github.sevntu.checkstyle.module.DependencyInformationConsumer;
import com.puppycrawl.tools.checkstyle.ModuleFactory;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;

import java.lang.reflect.InvocationTargetException;

public final class DependencyInformationConsumerInjector implements ModuleFactory {

    private DependencyInformationConsumer consumer;

    public DependencyInformationConsumerInjector(DependencyInformationConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public Object createModule(String name) throws CheckstyleException {
        try {
            final Class<?> moduleClass = Class.forName(name);
            if (moduleClass.equals(MethodCallDependencyCheckstyleModule.class)) {
                return moduleClass.getConstructor(DependencyInformationConsumer.class)
                        .newInstance(consumer);
            }
            else {
                return moduleClass.newInstance();
            }
        }
        catch (InstantiationException | IllegalAccessException | ClassNotFoundException
                | NoSuchMethodException | InvocationTargetException e) {
            throw new CheckstyleException("Failed to instantiate module " + name, e);
        }
    }
}
