package com.github.sevntu.checkstyle;

import com.github.sevntu.checkstyle.module.MethodCallDependencyCheckstyleModule;
import com.github.sevntu.checkstyle.module.ViolationReporterDependencyInformationConsumer;
import com.github.sevntu.checkstyle.common.DependencyInformationConsumerInjector;
import com.github.sevntu.checkstyle.module.DependencyInformationConsumer;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.DefaultLogger;
import com.puppycrawl.tools.checkstyle.ModuleFactory;
import com.puppycrawl.tools.checkstyle.TreeWalker;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Application entry point that accepts file path, processes it, extracts methods
 * dependency information, calculates other method order and generates instructions
 * on how to convert current methods order to calculated one.
 */
public final class ReportCli {

    private ReportCli() { }

    public static void main(String... args) throws CheckstyleException {
        final DependencyInformationConsumer consumer =
            new ViolationReporterDependencyInformationConsumer();
        final ModuleFactory moduleFactory = new DependencyInformationConsumerInjector(consumer);

        final DefaultConfiguration moduleConfig = new DefaultConfiguration(
            MethodCallDependencyCheckstyleModule.class.getCanonicalName());
        moduleConfig.addAttribute("screenLinesCount", "50");

        final TreeWalker tw = new TreeWalker();
        tw.setModuleFactory(moduleFactory);
        tw.finishLocalSetup();
        tw.setupChild(moduleConfig);

        final AuditListener listener = new DefaultLogger(System.out, false);

        final Checker checker = new Checker();
        checker.setModuleFactory(moduleFactory);
        checker.finishLocalSetup();
        checker.addFileSetCheck(tw);
        checker.addListener(listener);

        final List<File> files = Collections.singletonList(new File(args[0]));
        checker.process(files);
    }
}
