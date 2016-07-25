package com.github.sevntu.checkstyle;

import com.github.sevntu.checkstyle.module.MethodCallDependencyModule;
import com.github.sevntu.checkstyle.common.MethodCallDependencyCheckInvoker;
import com.github.sevntu.checkstyle.domain.Dependencies;
import com.github.sevntu.checkstyle.module.DependencyInformationConsumer;
import com.github.sevntu.checkstyle.ordering.Ordering;
import com.github.sevntu.checkstyle.utils.FileUtils;
import com.github.sevntu.checkstyle.dot.DependencyInfoGraphSerializer;
import com.github.sevntu.checkstyle.dsm.DependencyInfoMatrixSerializer;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Application entry point that accepts file paths as command line arguments and
 * generates DSM and DOT files in working directory.
 */
public final class Main {

    private Main() { }

    public static void main(String... args) throws CheckstyleException {

        final Map<String, String> attributes = Collections.singletonMap("screenLinesCount", "50");

        final DependencyInformationSerializer consumer = new DependencyInformationSerializer();

        final MethodCallDependencyCheckInvoker invoker =
            new MethodCallDependencyCheckInvoker(attributes, consumer);

        consumer.setConfig(invoker.getConfiguration());

        final List<File> files = Collections.singletonList(new File(args[0]));
        invoker.invoke(files);
    }

    private static final class DependencyInformationSerializer implements
        DependencyInformationConsumer {

        private Optional<Configuration> config = Optional.empty();

        private DependencyInformationSerializer() { }

        public void setConfig(final Configuration config) {
            this.config = Optional.of(config);
        }

        @Override
        public void accept(
            MethodCallDependencyModule check, String filePath, Dependencies dependencies) {

            config.ifPresent(configuration -> {
                final String baseName = new File(filePath).getName();
                DependencyInfoGraphSerializer.writeToFile(dependencies, baseName + ".dot");
                final String source = FileUtils.getFileContents(filePath);
                DependencyInfoMatrixSerializer.writeToFile(
                    source, new Ordering(dependencies), configuration, baseName + ".html");
            });
        }
    }
}
