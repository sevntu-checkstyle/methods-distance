///////////////////////////////////////////////////////////////////////////////////////////////
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
///////////////////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.github.sevntu.checkstyle.common.MethodCallDependencyCheckInvoker;
import com.github.sevntu.checkstyle.domain.Dependencies;
import com.github.sevntu.checkstyle.json.JsonSerializer;
import com.github.sevntu.checkstyle.module.DependencyInformationConsumer;
import com.github.sevntu.checkstyle.utils.FileUtils;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;

/**
 * Application entry point that accepts file path and generates
 * JSON representation of methods and their call relationship.
 *
 * @author Zhu Tianyi
 */
public final class JsonCli {

    private JsonCli() {
        // no code
    }

    public static void main(String... args) throws CheckstyleException {

        final Map<String, String> attributes = Collections.singletonMap("screenLinesCount", "50");

        String outputDir = args.length == 2 ? args[1] : ".";
        final JsonConsumer serializer = new JsonConsumer(outputDir);

        final MethodCallDependencyCheckInvoker runner =
            new MethodCallDependencyCheckInvoker(attributes, serializer);

        final List<File> files = Collections.singletonList(new File(args[0]));
        runner.invoke(files);
    }

    private static final class JsonConsumer implements DependencyInformationConsumer {

        private Configuration configuration;
        private String outputDir;

        private JsonConsumer(String outputDir) {
            this.outputDir = outputDir;
        }

        @Override
        public void setConfiguration(Configuration config) {
            configuration = config;
        }

        @Override
        public void accept(String filePath, Dependencies dependencies) {
            final String baseName = new File(filePath).getName();
            Path outputPath = Paths.get(outputDir, baseName + ".json");
            final String source = FileUtils.getFileContents(filePath);
            JsonSerializer.writeToFile(
                source, dependencies, configuration, outputPath.toString());
        }
    }
}
