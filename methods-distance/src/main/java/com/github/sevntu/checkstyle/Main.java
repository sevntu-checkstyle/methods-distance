////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2018 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.sevntu.checkstyle.common.MethodCallDependencyCheckInvoker;
import com.github.sevntu.checkstyle.domain.Dependencies;
import com.github.sevntu.checkstyle.dot.DependencyInfoGraphSerializer;
import com.github.sevntu.checkstyle.dsm.DependencyInfoMatrixSerializer;
import com.github.sevntu.checkstyle.module.DependencyInformationConsumer;
import com.github.sevntu.checkstyle.ordering.MethodOrder;
import com.github.sevntu.checkstyle.utils.FileUtils;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;

/**
 * Application entry point that accepts file paths as command line arguments and
 * generates DSM and DOT files in working directory.
 */
public final class Main {

    private Main() { }

    public static void main(String... args) throws CheckstyleException {

        final CliArguments arguments = new CliArguments(args);

        final Map<String, String> attributes = Collections.singletonMap("screenLinesCount", "50");

        final DependencyInformationSerializer serializer =
            new DependencyInformationSerializer(arguments);

        final MethodCallDependencyCheckInvoker invoker =
            new MethodCallDependencyCheckInvoker(attributes, serializer);

        invoker.invoke(arguments.getFiles());
    }

    private static final class DependencyInformationSerializer implements
        DependencyInformationConsumer {

        private Configuration configuration;

        private CliArguments arguments;

        private DependencyInformationSerializer(CliArguments arguments) {
            this.arguments = arguments;
        }

        @Override
        public void setConfiguration(Configuration config) {
            this.configuration = config;
        }

        @Override
        public void accept(String filePath, Dependencies dependencies) {
            final String baseName = new File(filePath).getName();
            if (arguments.shouldRenderDot()) {
                DependencyInfoGraphSerializer.writeToFile(dependencies, baseName + ".dot");
            }
            if (arguments.shouldRenderDsm()) {
                final String source = FileUtils.getFileContents(filePath);
                DependencyInfoMatrixSerializer.writeToFile(
                    source, new MethodOrder(dependencies), configuration, baseName + ".html");
            }
        }
    }

    private static final class CliArguments {

        private static final String SWITCH_GENERATE_DOT = "--generate-dot";

        private static final String SWITCH_GENERATE_DSM = "--generate-dsm";

        private static final List<String> SWITCHES = Arrays.asList(
            SWITCH_GENERATE_DOT, SWITCH_GENERATE_DSM);

        private List<String> args;

        CliArguments(String... args) {
            this.args = Arrays.asList(args);
        }

        public boolean shouldRenderDot() {
            return args.contains(SWITCH_GENERATE_DOT);
        }

        public boolean shouldRenderDsm() {
            return args.contains(SWITCH_GENERATE_DSM);
        }

        public List<File> getFiles() {
            return args.stream()
                .filter(arg -> !SWITCHES.contains(arg))
                .map(File::new)
                .collect(Collectors.toList());
        }
    }
}
