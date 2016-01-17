package org.pirat9600q;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.ModuleFactory;
import com.puppycrawl.tools.checkstyle.TreeWalker;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import org.pirat9600q.graph.MethodCallDependencyCheck;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) throws CheckstyleException {
        final DefaultConfiguration mcdc = new DefaultConfiguration(MethodCallDependencyCheck.class.getCanonicalName());
        mcdc.addAttribute("writeResult", "true");
        final SimpleModuleFactory moduleFactory = new SimpleModuleFactory();
        final TreeWalker tw = new TreeWalker();
        tw.setModuleFactory(moduleFactory);
        tw.finishLocalSetup();
        tw.setupChild(mcdc);
        final List<File> files = Collections.singletonList(new File(args[0]));
        final Checker checker = new Checker();
        checker.setModuleFactory(moduleFactory);
        checker.finishLocalSetup();
        checker.addFileSetCheck(tw);
        checker.process(files);
    }

    public static class SimpleModuleFactory implements ModuleFactory {

        @Override
        public Object createModule(String name) throws CheckstyleException {
            try {
                return Class.forName(name).newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                throw new CheckstyleException("Failed to instantiate module " + name, e);
            }
        }
    }
}
