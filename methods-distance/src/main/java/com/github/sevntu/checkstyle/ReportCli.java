////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2020 the original author or authors.
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
import java.util.Collections;
import java.util.List;

import com.github.sevntu.checkstyle.common.DependencyInformationConsumerInjector;
import com.github.sevntu.checkstyle.module.DependencyInformationConsumer;
import com.github.sevntu.checkstyle.module.MethodCallDependencyCheckstyleModule;
import com.github.sevntu.checkstyle.module.ViolationReporterDependencyInformationConsumer;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.DefaultLogger;
import com.puppycrawl.tools.checkstyle.ModuleFactory;
import com.puppycrawl.tools.checkstyle.TreeWalker;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;

/**
 * Application entry point that accepts file path, processes it, extracts methods
 * dependency information, calculates other method order and generates instructions
 * on how to convert current methods order to calculated one.
 * @author Zuy Alexey
 */
public final class ReportCli {

    private ReportCli() {
        // no code
    }

    public static void main(String... args) throws CheckstyleException {
        final DependencyInformationConsumer consumer =
            new ViolationReporterDependencyInformationConsumer();
        final ModuleFactory moduleFactory = new DependencyInformationConsumerInjector(consumer);

        final DefaultConfiguration moduleConfig = new DefaultConfiguration(
            MethodCallDependencyCheckstyleModule.class.getCanonicalName());
        moduleConfig.addAttribute("screenLinesCount", "50");

        final TreeWalker tw = new TreeWalker();
        tw.setModuleFactory(moduleFactory);
        tw.finishLocalSetup();
        tw.setupChild(moduleConfig);

        final AuditListener listener = new DefaultLogger(System.out, false);

        final Checker checker = new Checker();
        checker.setModuleFactory(moduleFactory);
        checker.addFileSetCheck(tw);
        checker.addListener(listener);

        final List<File> files = Collections.singletonList(new File(args[0]));
        checker.process(files);
    }
}
