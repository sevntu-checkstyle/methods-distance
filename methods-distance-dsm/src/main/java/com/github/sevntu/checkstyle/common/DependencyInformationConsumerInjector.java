package com.github.sevntu.checkstyle.common;

import com.github.sevntu.checkstyle.module.MethodCallDependencyModule;
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
            if (moduleClass.equals(MethodCallDependencyModule.class)) {
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
