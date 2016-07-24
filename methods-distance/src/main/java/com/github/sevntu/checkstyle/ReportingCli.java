package com.github.sevntu.checkstyle;

import com.github.sevntu.checkstyle.module.MethodCallDependencyModule;
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
 * Application entry point that accepts file path and
 * generates violations of method ordering.
 */
public final class ReportingCli {

    private ReportingCli() { }

    public static void main(String... args) throws CheckstyleException {
        final DependencyInformationConsumer consumer =
            new ViolationReporterDependencyInformationConsumer();
        final ModuleFactory moduleFactory = new DependencyInformationConsumerInjector(consumer);

        final DefaultConfiguration mcdc = new DefaultConfiguration(
            MethodCallDependencyModule.class.getCanonicalName());
        mcdc.addAttribute("screenLinesCount", "50");

        final TreeWalker tw = new TreeWalker();
        tw.setModuleFactory(moduleFactory);
        tw.finishLocalSetup();
        tw.setupChild(mcdc);

        final AuditListener listener = new DefaultLogger(System.out, false);

        final Checker checker = new Checker();
        checker.setModuleFactory(moduleFactory);
        checker.finishLocalSetup();
        checker.addFileSetCheck(tw);
        checker.addListener(listener);

        final List<File> files = Collections.singletonList(new File(args[0]));
        checker.process(files);
    }

    private static final class SimpleModuleFactory implements ModuleFactory {

        @Override
        public Object createModule(String name) throws CheckstyleException {
            try {
                return Class.forName(name).newInstance();
            }
            catch (final ClassNotFoundException | IllegalAccessException
                | InstantiationException e) {
                throw new CheckstyleException("Failed to instantiate module " + name, e);
            }
        }
    }
}
