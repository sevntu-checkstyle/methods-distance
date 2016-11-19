package com.github.sevntu.checkstyle.common;

import com.github.sevntu.checkstyle.module.MethodCallDependencyCheckstyleModule;
import com.github.sevntu.checkstyle.module.DependencyInformationConsumer;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.DefaultLogger;
import com.puppycrawl.tools.checkstyle.ModuleFactory;
import com.puppycrawl.tools.checkstyle.TreeWalker;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;

import java.io.File;
import java.util.List;
import java.util.Map;

public class MethodCallDependencyCheckInvoker {

    private final Checker checker;

    private final Configuration configuration;

    public MethodCallDependencyCheckInvoker(Map<String, String> configAttributes,
        DependencyInformationConsumer consumer) throws CheckstyleException {

        this.configuration = getCompleteConfig(configAttributes);

        consumer.setConfiguration(configuration);

        final ModuleFactory moduleFactory = new DependencyInformationConsumerInjector(consumer);

        final TreeWalker tw = new TreeWalker();
        tw.setModuleFactory(moduleFactory);
        tw.finishLocalSetup();
        tw.setupChild(configuration);

        checker = new Checker();
        checker.setModuleFactory(moduleFactory);
        checker.finishLocalSetup();
        checker.addFileSetCheck(tw);
        checker.addListener(new DefaultLogger(System.out, false));
    }

    private static Configuration getCompleteConfig(Map<String, String> configAttributes) {
        final DefaultConfiguration config =
            new DefaultConfiguration(MethodCallDependencyCheckstyleModule.class.getCanonicalName());
        configAttributes.entrySet().forEach(kv -> config.addAttribute(kv.getKey(), kv.getValue()));
        return config;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void invoke(List<File> files) throws CheckstyleException {
        checker.process(files);
    }
}
