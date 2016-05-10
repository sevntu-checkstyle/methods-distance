package com.github.sevntu.checkstyle.analysis;

import com.github.sevntu.checkstyle.DependencyInformationConsumerInjector;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.Configuration;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MethodCallDependenciesCheckTestSupport extends BaseCheckTestSupport {

    protected final ByteArrayOutputStream stream = new ByteArrayOutputStream();

    private DependencyInformationCollector collector = new DependencyInformationCollector();

    protected void verifyInfo(final Configuration config, final String fileName, final ExpectedDependencies expected) throws Exception {
        mustBeSame(expected, invokeCheckAndGetDependencies(config, fileName));
    }

    protected Dependencies invokeCheckAndGetDependencies(final Configuration config, final String fileName) throws Exception {
        final String filePath = getInputPath(fileName);
        verify(config, filePath);
        return collector.getForFile(filePath);
    }

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
        checker.addListener(new BriefLogger(stream));
        return checker;
    }

    private static void mustBeSame(final ExpectedDependencies expected, final Dependencies actual) {
        for(final String expectedMethod : expected.getMethods()) {
            assertTrue("Method " + expectedMethod + " is not present is actual info",
                    actual.getMethods().stream().anyMatch(md -> expectedMethod.equals(md.getSignature())));
        }
        for(final MethodDefinition actualMethod: actual.getMethods()) {
            assertTrue("Method " + actualMethod.getSignature() + " is not present in expected info",
                    expected.getMethods().stream().anyMatch(mi -> mi.equals(actualMethod.getSignature())));
        }
        for(final String method : expected.getMethods()) {
            final MethodDefinition caller = actual.getMethods().stream()
                    .filter(md -> md.getSignature().equals(method)).findFirst().get();
            final List<MethodDefinition> dependencies = actual.getMethodDependencies(caller);
            final List<ExpectedDependencies.MethodInvocation> invocations = expected.getInvocationsFromMethod(method);
            assertEquals("Actual method dependencies count and count of invocations from method "
                    + method + " does not match", invocations.size(), dependencies.size());
            for(int i = 0; i < invocations.size(); ++i) {
                final MethodDefinition calledMethod = dependencies.get(i);
                final ExpectedDependencies.MethodInvocation invocationOfMethod = invocations.get(i);
                assertTrue("Method " + calledMethod.getSignature() + " is present as actual "
                                + i + " dependency of " + method + " but should not be!",
                        calledMethod.getSignature().equals(expected.getMethodByIndex(invocationOfMethod.callee)));
            }
        }
    }

    private static String getInputPath(final String fileName) {
        return MethodCallDependencyCheckTest.class.getResource(fileName).getPath();
    }

    protected Dependencies withDefaultConfig(final String fileName) throws Exception {
        return invokeCheckAndGetDependencies(createCheckConfig(MethodCallDependencyCheck.class), fileName);
    }

    private static class DependencyInformationCollector implements DependencyInformationConsumer {

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
