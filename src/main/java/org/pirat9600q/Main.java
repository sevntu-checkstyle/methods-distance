package org.pirat9600q;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.ModuleFactory;
import com.puppycrawl.tools.checkstyle.TreeWalker;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import org.pirat9600q.graph.Dependencies;
import org.pirat9600q.graph.DependencyInfoGraphSerializer;
import org.pirat9600q.graph.DependencyInfoMatrixSerializer;
import org.pirat9600q.graph.DependencyInformationConsumer;
import org.pirat9600q.graph.MethodCallDependencyCheck;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public final class Main {

    private Main() { }

    public static void main(String[] args) throws CheckstyleException {
        final DefaultConfiguration mcdc = new DefaultConfiguration(
                MethodCallDependencyCheck.class.getCanonicalName());
        mcdc.addAttribute("screenLinesCount", "50");
        final DependencyInformationSerializer consumer = new DependencyInformationSerializer(mcdc);
        final ModuleFactoryImpl moduleFactory = new ModuleFactoryImpl(consumer);
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

    private static final class DependencyInformationSerializer implements
            DependencyInformationConsumer {

        private final Configuration config;

        private DependencyInformationSerializer(final Configuration config) {
            this.config = config;
        }

        @Override
        public void accept(String filePath, Dependencies dependencies) {
            final String baseName = new File(filePath).getName();
            DependencyInfoGraphSerializer.writeToFile(dependencies, baseName + ".dot");
            final String source = getFileContents(filePath);
            DependencyInfoMatrixSerializer.writeToFile(source, dependencies, config,
                baseName + ".html");
        }

        private static String getFileContents(final String filePath) {
            try (final Scanner scanner = new Scanner(new FileInputStream(filePath))) {
                scanner.useDelimiter("\\Z");
                return scanner.next();
            }
            catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static final class ModuleFactoryImpl implements ModuleFactory {

        private DependencyInformationConsumer consumer;

        public ModuleFactoryImpl(final DependencyInformationConsumer consumer) {
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
}
