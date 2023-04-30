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

package com.github.sevntu.checkstyle.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.github.sevntu.checkstyle.common.DependencyInformationConsumerInjector;
import com.github.sevntu.checkstyle.domain.BaseCheckTestSupport;
import com.github.sevntu.checkstyle.domain.Dependencies;
import com.github.sevntu.checkstyle.domain.ExpectedDependencies;
import com.github.sevntu.checkstyle.module.DependencyInformationConsumer;
import com.github.sevntu.checkstyle.module.MethodCallDependencyCheckstyleModule;
import com.github.sevntu.checkstyle.ordering.Method;
import com.github.sevntu.checkstyle.ordering.MethodOrder;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.Configuration;

public class MethodCallDependenciesModuleTestSupport extends BaseCheckTestSupport {

    private final ByteArrayOutputStream stream = new ByteArrayOutputStream();

    private DependencyInformationCollector collector = new DependencyInformationCollector();

    protected void verifyInfo(final Configuration config, final String fileName,
            final ExpectedDependencies expected) throws Exception {
        mustBeSame(expected, invokeCheckAndGetOrdering(config, fileName));
    }

    protected Dependencies invokeCheckAndGetDependencies(final Configuration config,
            final String fileName) throws Exception {
        final String filePath = getInputPath(fileName);
        verify(config, filePath);
        return collector.getForFile(filePath);
    }

    protected MethodOrder invokeCheckAndGetOrdering(final Configuration config,
            final String fileName) throws Exception {
        return new MethodOrder(invokeCheckAndGetDependencies(config, fileName));
    }

    @Override
    protected final Checker createChecker(Configuration checkConfig) throws Exception {
        final DefaultConfiguration dc = createCheckerConfig(checkConfig);
        final Checker checker = new Checker();
        // make sure the tests always run with default error messages (language-invariant)
        // so the tests don't fail in supported locales like German
        final Locale locale = Locale.ROOT;
        checker.setLocaleCountry(locale.getCountry());
        checker.setLocaleLanguage(locale.getLanguage());
        // following line is the only difference to super-class implementation of this method.
        checker.setModuleFactory(new DependencyInformationConsumerInjector(collector));
        checker.setModuleClassLoader(Thread.currentThread().getContextClassLoader());
        checker.configure(dc);
        checker.addListener(new BaseCheckTestSupport.BriefLogger(stream));
        return checker;
    }

    private static void mustBeSame(final ExpectedDependencies expected, final MethodOrder actual) {
        for (final String expectedMethod : expected.getMethods()) {
            assertTrue("Method " + expectedMethod + " is not present is actual info",
                    actual.getMethods().stream()
                        .anyMatch(method -> expectedMethod.equals(method.getSignature())));
        }
        for (final Method actualMethod: actual.getMethods()) {
            assertTrue("Method " + actualMethod.getSignature() + " is not present in expected info",
                    expected.getMethods().stream()
                        .anyMatch(method -> method.equals(actualMethod.getSignature())));
        }
        for (final String method : expected.getMethods()) {
            final Method caller = actual.getMethods().stream()
                    .filter(method1 -> method1.getSignature().equals(method)).findFirst().get();
            final List<Method> dependencies = actual.getMethodDependenciesInAppearanceOrder(caller);
            final List<ExpectedDependencies.MethodInvocation> invocations = expected
                    .getInvocationsFromMethod(method);
            assertEquals("Actual method dependencies count and count of invocations from method "
                    + method + " does not match", invocations.size(), dependencies.size());
            for (int index = 0; index < invocations.size(); ++index) {
                final Method calledMethod = dependencies.get(index);
                final ExpectedDependencies.MethodInvocation invocationOfMethod =
                        invocations.get(index);
                assertTrue("Method " + calledMethod.getSignature() + " is present as actual "
                                + index + " dependency of " + method + " but should not be!",
                        calledMethod.getSignature()
                            .equals(expected.getMethodByIndex(invocationOfMethod.getCallee())));
            }
        }
    }

    private String getInputPath(final String fileName) {
        return getClass().getResource(fileName).getPath();
    }

    protected Dependencies withDefaultConfig(final String fileName) throws Exception {
        return invokeCheckAndGetDependencies(
                createCheckConfig(MethodCallDependencyCheckstyleModule.class), fileName);
    }

    protected MethodOrder withDefaultConfigOrdering(final String fileName) throws Exception {
        return new MethodOrder(withDefaultConfig(fileName));
    }

    private static final class DependencyInformationCollector
            implements DependencyInformationConsumer {

        private Map<String, Dependencies> filePathToDependencies = new HashMap<>();

        @Override
        public void accept(String filePath, Dependencies dependencies) {
            filePathToDependencies.put(filePath, dependencies);
        }

        public Dependencies getForFile(final String filePath) {
            return filePathToDependencies.get(filePath);
        }
    }
}
