package org.pirat9600q;

import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import org.pirat9600q.analysis.Dependencies;
import org.pirat9600q.analysis.DependencyInformationConsumer;
import org.pirat9600q.analysis.MethodCallDependencyCheck;
import org.pirat9600q.utils.FileUtils;

import java.io.File;
import java.util.Collections;
import java.util.List;

public final class Main {

    private Main() { }

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
            DependencyInfoGraphSerializer.writeToFile(dependencies, baseName + ".dot");
            final String source = FileUtils.getFileContents(filePath);
            DependencyInfoMatrixSerializer.writeToFile(source, dependencies, config,
                baseName + ".html");
        }
    }
}
