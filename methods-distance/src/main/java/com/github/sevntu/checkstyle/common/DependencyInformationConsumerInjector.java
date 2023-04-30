///////////////////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code and other text files for adherence to a set of rules.
// Copyright (C) 2001-2023 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 3 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
///////////////////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle.common;

import java.lang.reflect.InvocationTargetException;

import com.github.sevntu.checkstyle.module.DependencyInformationConsumer;
import com.github.sevntu.checkstyle.module.MethodCallDependencyCheckstyleModule;
import com.puppycrawl.tools.checkstyle.ModuleFactory;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;

public final class DependencyInformationConsumerInjector implements ModuleFactory {

    private DependencyInformationConsumer consumer;

    public DependencyInformationConsumerInjector(DependencyInformationConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public Object createModule(String name) throws CheckstyleException {
        final Object result;
        try {
            final Class<?> moduleClass = Class.forName(name);
            if (moduleClass.equals(MethodCallDependencyCheckstyleModule.class)) {
                result = moduleClass.getConstructor(DependencyInformationConsumer.class)
                        .newInstance(consumer);
            }
            else {
                result = moduleClass.newInstance();
            }
        }
        catch (InstantiationException | IllegalAccessException | ClassNotFoundException
                | NoSuchMethodException | InvocationTargetException ex) {
            throw new CheckstyleException("Failed to instantiate module " + name, ex);
        }
        return result;
    }
}
