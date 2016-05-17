package com.github.sevntu.checkstyle;

import com.github.sevntu.checkstyle.analysis.Dependencies;
import com.github.sevntu.checkstyle.analysis.DependencyInformationConsumer;
import com.github.sevntu.checkstyle.analysis.MethodCallDependencyCheck;
import com.github.sevntu.checkstyle.ordering.Ordering;
import com.github.sevntu.checkstyle.ordering.TopologicalMethodSorter;
import com.github.sevntu.checkstyle.utils.FileUtils;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;

import java.io.File;
import java.util.Collections;
import java.util.List;

public final class ReorderingMain {

    private ReorderingMain() { }

    public static void main(String... args) throws CheckstyleException {
        final DefaultConfiguration mcdc = new DefaultConfiguration(
            MethodCallDependencyCheck.class.getCanonicalName());
        mcdc.addAttribute("screenLinesCount", "50");
        final DependencyInformationSerializer consumer = new DependencyInformationSerializer(mcdc);
        final List<File> files = Collections.singletonList(new File(args[0]));
        final MethodCallDependencyCheckInvoker runner =
            new MethodCallDependencyCheckInvoker(mcdc, consumer);
        runner.invoke(files);
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
            final String source = FileUtils.getFileContents(filePath);
            final Ordering initialOrdering = new Ordering(dependencies);
            final Ordering topologicalOrdering = new TopologicalMethodSorter()
                .sort(initialOrdering);
            DependencyInfoMatrixSerializer.writeToFile(source, initialOrdering, config,
                baseName + ".initial.html");
            DependencyInfoMatrixSerializer.writeToFile(source, topologicalOrdering, config,
                baseName + ".topological.html");
        }
    }
}
