package com.github.sevntu.checkstyle;

import com.puppycrawl.tools.checkstyle.ModuleFactory;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.github.sevntu.checkstyle.analysis.DependencyInformationConsumer;
import com.github.sevntu.checkstyle.analysis.MethodCallDependencyCheck;

import java.lang.reflect.InvocationTargetException;

public final class DependencyInformationConsumerInjector implements ModuleFactory {

    private DependencyInformationConsumer consumer;

    public DependencyInformationConsumerInjector(final DependencyInformationConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public Object createModule(String name) throws CheckstyleException {
        try {
            final Class<?> moduleClass = Class.forName(name);
            if (moduleClass.equals(MethodCallDependencyCheck.class)) {
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
