///////////////////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code and other text files for adherence to a set of rules.
// Copyright (C) 2001-2023 the original author or authors.
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

package com.github.sevntu.checkstyle.common;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.github.sevntu.checkstyle.module.DependencyInformationConsumer;
import com.github.sevntu.checkstyle.module.MethodCallDependencyCheckstyleModule;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.DefaultLogger;
import com.puppycrawl.tools.checkstyle.ModuleFactory;
import com.puppycrawl.tools.checkstyle.TreeWalker;
import com.puppycrawl.tools.checkstyle.api.AutomaticBean;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;

public class MethodCallDependencyCheckInvoker {

    private final Checker checker;

    private final Configuration configuration;

    public MethodCallDependencyCheckInvoker(Map<String, String> configAttributes,
        DependencyInformationConsumer consumer) throws CheckstyleException {

        configuration = getCompleteConfig(configAttributes);

        consumer.setConfiguration(configuration);

        final ModuleFactory moduleFactory = new DependencyInformationConsumerInjector(consumer);

        final TreeWalker tw = new TreeWalker();
        tw.setModuleFactory(moduleFactory);
        tw.finishLocalSetup();
        tw.setupChild(configuration);

        checker = new Checker();
        checker.setModuleFactory(moduleFactory);
        checker.addFileSetCheck(tw);
        checker.addListener(new DefaultLogger(System.out, AutomaticBean.OutputStreamOptions.NONE));
    }

    private static Configuration getCompleteConfig(Map<String, String> configAttributes) {
        final DefaultConfiguration config =
            new DefaultConfiguration(MethodCallDependencyCheckstyleModule.class.getCanonicalName());
        configAttributes.entrySet().forEach(entry -> {
            config.addAttribute(entry.getKey(), entry.getValue());
        });
        return config;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void invoke(List<File> files) throws CheckstyleException {
        checker.process(files);
    }
}
