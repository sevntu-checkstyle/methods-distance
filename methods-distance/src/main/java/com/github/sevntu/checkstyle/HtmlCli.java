////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code and other text files for adherence to a set of rules.
// Copyright (C) 2001-2024 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 3 of the License, or (at your option) any later version.
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.github.sevntu.checkstyle.common.MethodCallDependencyCheckInvoker;
import com.github.sevntu.checkstyle.domain.Dependencies;
import com.github.sevntu.checkstyle.html.HtmlSerializer;
import com.github.sevntu.checkstyle.module.DependencyInformationConsumer;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;

/**
 * Application entry point that accepts a Java file path and generates
 * a self-contained HTML report of methods and their call relationships.
 *
 * @author Zhu Tianyi
 */
public final class HtmlCli {

    private HtmlCli() {
        // no code
    }

    /**
     * Entry point.
     *
     * @param args command-line arguments: {@code <javaSourcePath> [outputDir]}
     * @throws CheckstyleException if analysis fails
     */
    public static void main(String... args) throws CheckstyleException {
        final String outputDir;
        if (args.length == 2) {
            outputDir = args[1];
        }
        else {
            outputDir = ".";
        }

        final HtmlConsumer consumer = new HtmlConsumer(outputDir);

        final Map<String, String> attributes = Collections.singletonMap("screenLinesCount", "50");
        final MethodCallDependencyCheckInvoker runner =
            new MethodCallDependencyCheckInvoker(attributes, consumer);

        final List<File> files = Collections.singletonList(new File(args[0]));
        runner.invoke(files);
    }

    private static final class HtmlConsumer implements DependencyInformationConsumer {

        private Configuration configuration;
        private final String outputDir;

        private HtmlConsumer(String outputDir) {
            this.outputDir = outputDir;
        }

        @Override
        public void setConfiguration(Configuration config) {
            this.configuration = config;
        }

        @Override
        public void accept(String javaSource, Dependencies dependencies) {
            final String fileName = outputDir + File.separator
                + new File(javaSource).getName() + ".html";
            HtmlSerializer.writeToFile(javaSource, dependencies, configuration, fileName);
        }
    }
}
