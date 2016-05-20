package com.github.sevntu.checkstyle;

import com.github.sevntu.checkstyle.check.MethodCallDependencyCheck;
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

public final class ReportingMain {

    private ReportingMain() { }

    public static void main(String... args) throws CheckstyleException {
        final DefaultConfiguration mcdc = new DefaultConfiguration(
            MethodCallDependencyCheck.class.getCanonicalName());
        mcdc.addAttribute("screenLinesCount", "50");
        final SimpleMuduleFactory moduleFactory = new SimpleMuduleFactory();
        final TreeWalker tw = new TreeWalker();
        tw.setModuleFactory(moduleFactory);
        tw.finishLocalSetup();
        tw.setupChild(mcdc);
        final Checker checker = new Checker();
        checker.setModuleFactory(moduleFactory);
        checker.finishLocalSetup();
        checker.addFileSetCheck(tw);
        final List<File> files = Collections.singletonList(new File(args[0]));
        final AuditListener listener = new DefaultLogger(System.out, false);
        checker.addListener(listener);
        checker.process(files);
    }

    private static final class SimpleMuduleFactory implements ModuleFactory {

        @Override
        public Object createModule(final String name) throws CheckstyleException {
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
