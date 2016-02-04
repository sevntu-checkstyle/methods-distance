package org.pirat9600q.graph;

import com.google.common.collect.Sets;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.ModuleFactory;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import org.junit.Ignore;
import org.junit.Test;
import org.pirat9600q.graph.MethodCallInfo.CallType;
import org.pirat9600q.graph.MethodInfo.Accessibility;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.*;
import java.util.stream.Collectors;

public class MethodCallDependencyCheckTest extends BaseCheckTestSupport {

    private StatefulModuleFactory moduleFactory = new StatefulModuleFactory();

    @Test
    public void testThatWeCanWriteTests() throws Exception {
        final DefaultConfiguration config = createCheckConfig(MethodCallDependencyCheck.class);
        verify(config, getInputPath("InputIndependentMethods.java"));
    }

    @Test
    public void testSimpleDependency() throws Exception {
        final DefaultConfiguration dc = createCheckConfig(MethodCallDependencyCheck.class);
        final DependencyInfo expected = DependencyInfo.builder()
                .addMethod(MethodInfo.builder().signature("InputSimpleDependency()")
                    .isStatic(false).isOverride(false).isOverloaded(false).isVarArg(false)
                    .minArgCount(0)
                    .index(0)
                    .accessibility(Accessibility.PUBLIC)
                    .get())
                .addMethod(MethodInfo.builder().signature("dependant()")
                    .isStatic(false).isOverride(false).isOverloaded(false).isVarArg(false)
                    .minArgCount(0)
                    .index(1)
                    .accessibility(Accessibility.PUBLIC)
                    .get())
                .addMethod(MethodInfo.builder().signature("dependency()")
                    .isStatic(false).isOverride(false).isOverloaded(false).isVarArg(false)
                    .minArgCount(0)
                    .index(2)
                    .accessibility(Accessibility.PUBLIC)
                    .get())
                .addMethod(MethodInfo.builder().signature("dependencyDependency1()")
                    .isStatic(false).isOverride(false).isOverloaded(false).isVarArg(false)
                    .minArgCount(0)
                    .index(3)
                    .accessibility(Accessibility.PUBLIC)
                    .get())
                .addMethod(MethodInfo.builder().signature("dependencyDependency1()")
                    .isStatic(false).isOverride(false).isOverloaded(false).isVarArg(false)
                    .minArgCount(0)
                    .index(4)
                    .accessibility(Accessibility.PUBLIC)
                    .get())
                .addMethodCall(MethodCallInfo.builder()
                    .callerIndex(0).calleeIndex(1).lineNo(6).columnNo(17)
                    .callType(CallType.METHOD_CALL).get())
                .addMethodCall(MethodCallInfo.builder()
                    .callerIndex(1).calleeIndex(2).lineNo(10).columnNo(18)
                    .callType(CallType.METHOD_CALL).get())
                .addMethodCall(MethodCallInfo.builder()
                    .callerIndex(2).calleeIndex(3).lineNo(14).columnNo(29)
                    .callType(CallType.METHOD_CALL).get())
                .addMethodCall(MethodCallInfo.builder()
                        .callerIndex(2).calleeIndex(4).lineNo(15).columnNo(29)
                        .callType(CallType.METHOD_CALL).get())
                .get();
        verifyInfo(dc, "InputSimpleDependency.java", expected);
    }

    @Test
    public void testEmptyInterface() throws Exception {
        final DefaultConfiguration dc = createCheckConfig(MethodCallDependencyCheck.class);
        verifyInfo(dc, "InputEmptyInterface.java", DependencyInfo.builder().get());
    }

    @Test
    public void testMethodCallInInitialization() throws Exception {
        final DefaultConfiguration dc = createCheckConfig(MethodCallDependencyCheck.class);
        verifyInfo(dc, "InputMethodCallInInitialization.java", DependencyInfo.builder().get());
    }

    @Test
    public void testAnonymousClasses() throws Exception {
        final DefaultConfiguration dc = createCheckConfig(MethodCallDependencyCheck.class);
        final DependencyInfo expected = DependencyInfo.builder()
                .addMethod(MethodInfo.builder().signature("method()")
                    .notStatic().notOverride().notOverloaded().notVarArg()
                    .minArgCount(0).index(0).accessibility(Accessibility.PUBLIC)
                    .get())
                .addMethod(MethodInfo.builder().signature("a()")
                    .notStatic().notOverride().isOverloaded().notVarArg()
                    .minArgCount(0).index(1).accessibility(Accessibility.PUBLIC)
                    .get())
                .addMethod(MethodInfo.builder().signature("a(String)")
                    .notStatic().notOverride().isOverloaded().notVarArg()
                    .minArgCount(1).index(2).accessibility(Accessibility.PUBLIC)
                    .get())
                .addMethod(MethodInfo.builder().signature("topLevelMethod()")
                    .notStatic().notOverride().notOverloaded().notVarArg()
                    .minArgCount(0).index(3).accessibility(Accessibility.PUBLIC)
                    .get())
                .get();
        verifyInfo(dc, "InputAnonymousClasses.java", expected);
    }

    @Test
    public void testRecursiveMethod() throws Exception {
        final DefaultConfiguration dc = createCheckConfig(MethodCallDependencyCheck.class);
        final DependencyInfo dependencyInfo = DependencyInfo.builder()
                .addMethod(MethodInfo.builder().signature("method()")
                    .isStatic(false).isOverride(false).isOverloaded(false).isVarArg(false)
                    .minArgCount(0)
                    .index(0)
                    .accessibility(Accessibility.PUBLIC)
                    .get())
                .addMethodCall(MethodCallInfo.builder()
                    .callerIndex(0).calleeIndex(0).lineNo(6).columnNo(14)
                    .callType(CallType.METHOD_CALL)
                    .get())
                .get();
        verifyInfo(dc, "InputRecursiveMethod.java", dependencyInfo);
    }

    @Test
    public void testMethodNameClashes() throws Exception {
        final DefaultConfiguration dc = createCheckConfig(MethodCallDependencyCheck.class);
        final DependencyInfo expected = DependencyInfo.builder()
                .addMethod(MethodInfo.builder().signature("method()")
                    .notStatic().notOverride().notOverloaded().notVarArg()
                    .minArgCount(0).index(0).accessibility(Accessibility.PUBLIC)
                    .get())
                .addMethod(MethodInfo.builder().signature("format(String)")
                    .notStatic().notOverride().notOverloaded().notVarArg()
                    .minArgCount(1).index(1).accessibility(Accessibility.PUBLIC)
                    .get())
                .get();
        verifyInfo(dc, "InputMethodNameClashes.java", expected);
    }

    @Test
    public void testMethodSignatures() throws Exception {
        final DefaultConfiguration dc = createCheckConfig(MethodCallDependencyCheck.class);
        final DependencyInfo expected = DependencyInfo.builder()
                .addMethod(MethodInfo.builder().signature("m()")
                    .notStatic().notOverride().isOverloaded().notVarArg()
                    .minArgCount(0).index(0).accessibility(Accessibility.PUBLIC)
                    .get())
                .addMethod(MethodInfo.builder().signature("m(boolean)")
                    .notStatic().notOverride().isOverloaded().notVarArg()
                    .minArgCount(1).index(1).accessibility(Accessibility.PUBLIC)
                    .get())
                .addMethod(MethodInfo.builder().signature("m(char)")
                    .notStatic().notOverride().isOverloaded().notVarArg()
                    .minArgCount(1).index(2).accessibility(Accessibility.PUBLIC)
                    .get())
                .addMethod(MethodInfo.builder().signature("m(byte)")
                    .notStatic().notOverride().isOverloaded().notVarArg()
                    .minArgCount(1).index(3).accessibility(Accessibility.PUBLIC)
                    .get())
                .addMethod(MethodInfo.builder().signature("m(short)")
                    .notStatic().notOverride().isOverloaded().notVarArg()
                    .minArgCount(1).index(4).accessibility(Accessibility.PUBLIC)
                    .get())
                .addMethod(MethodInfo.builder().signature("m(int)")
                    .notStatic().notOverride().isOverloaded().notVarArg()
                    .minArgCount(1).index(5).accessibility(Accessibility.PUBLIC)
                    .get())
                .addMethod(MethodInfo.builder().signature("m(long)")
                    .notStatic().notOverride().isOverloaded().notVarArg()
                    .minArgCount(1).index(6).accessibility(Accessibility.PUBLIC)
                    .get())
                .addMethod(MethodInfo.builder().signature("m(double)")
                    .notStatic().notOverride().isOverloaded().notVarArg()
                    .minArgCount(1).index(7).accessibility(Accessibility.PUBLIC)
                    .get())
                .addMethod(MethodInfo.builder().signature("m(String)")
                    .notStatic().notOverride().isOverloaded().notVarArg()
                    .minArgCount(1).index(8).accessibility(Accessibility.PUBLIC)
                    .get())
                .addMethod(MethodInfo.builder().signature("m(String,String)")
                    .notStatic().notOverride().isOverloaded().notVarArg()
                    .minArgCount(2).index(9).accessibility(Accessibility.PUBLIC)
                    .get())
                .addMethod(MethodInfo.builder().signature("m(String,Integer...)")
                    .notStatic().notOverride().isOverloaded().isVarArg()
                    .minArgCount(2).index(10).accessibility(Accessibility.PUBLIC)
                    .get())
                .addMethod(MethodInfo.builder().signature("m(Integer...)")
                    .notStatic().notOverride().isOverloaded().isVarArg()
                    .minArgCount(0).index(11).accessibility(Accessibility.PUBLIC)
                    .get())
                .addMethod(MethodInfo.builder().signature("m(int[])")
                    .notStatic().notOverride().isOverloaded().notVarArg()
                    .minArgCount(1).index(12).accessibility(Accessibility.PUBLIC)
                    .get())
                .addMethod(MethodInfo.builder().signature("m(Long[])")
                    .notStatic().notOverride().isOverloaded().notVarArg()
                    .minArgCount(1).index(13).accessibility(Accessibility.PUBLIC)
                    .get())
                .addMethod(MethodInfo.builder().signature("m(List)")
                    .notStatic().notOverride().isOverloaded().notVarArg()
                    .minArgCount(1).index(14).accessibility(Accessibility.PUBLIC)
                    .get())
                .addMethod(MethodInfo.builder().signature("m(String,List...)")
                    .notStatic().notOverride().isOverloaded().isVarArg()
                    .minArgCount(1).index(15).accessibility(Accessibility.PUBLIC)
                    .get())
                .addMethod(MethodInfo.builder().signature("m(List[])")
                    .notStatic().notOverride().isOverloaded().notVarArg()
                    .minArgCount(1).index(16).accessibility(Accessibility.PUBLIC)
                    .get())
                .addMethod(MethodInfo.builder().signature("m(List[]...)")
                    .notStatic().notOverride().isOverloaded().isVarArg()
                    .minArgCount(0).index(17).accessibility(Accessibility.PUBLIC)
                    .get())
                .get();
        verifyInfo(dc, "InputMethodSignatures.java", expected);
    }

    @Test
    public void testVarargMethodCall() throws Exception {
        final DefaultConfiguration dc = createCheckConfig(MethodCallDependencyCheck.class);
        final DependencyInfo expected = DependencyInfo.builder()
                .addMethod(MethodInfo.builder().signature("c1()")
                    .notStatic().notOverride().notOverloaded().notVarArg()
                    .minArgCount(0).index(0).accessibility(Accessibility.PUBLIC).get())
                .addMethod(MethodInfo.builder().signature("c2()")
                    .notStatic().notOverride().notOverloaded().notVarArg()
                    .minArgCount(0).index(1).accessibility(Accessibility.PUBLIC).get())
                .addMethod(MethodInfo.builder().signature("c3()")
                    .notStatic().notOverride().notOverloaded().notVarArg()
                    .minArgCount(0).index(2).accessibility(Accessibility.PUBLIC).get())
                .addMethod(MethodInfo.builder().signature("varargMethod(Integer...)")
                        .notStatic().notOverride().notOverloaded().isVarArg()
                        .minArgCount(0).index(3).accessibility(Accessibility.PUBLIC).get())
                .addMethodCall(MethodCallInfo.builder().callFromTo(0, 3).at(6, 20)
                    .callType(CallType.METHOD_CALL).get())
                .addMethodCall(MethodCallInfo.builder().callFromTo(1, 3).at(10, 20)
                    .callType(CallType.METHOD_CALL).get())
                .addMethodCall(MethodCallInfo.builder().callFromTo(2, 3).at(14, 20)
                    .callType(CallType.METHOD_CALL).get())
                .get();
        verifyInfo(dc, "InputVarargMethodCall.java", expected);
    }

    @Test
    public void testMethodCallsInLambda() throws Exception {
        final DefaultConfiguration dc = createCheckConfig(MethodCallDependencyCheck.class);
        final DependencyInfo info = DependencyInfo.builder()
                .addMethod(MethodInfo.builder().signature("m()")
                        .notStatic().notOverride().notOverloaded().notVarArg()
                        .minArgCount(0).index(0).accessibility(Accessibility.PUBLIC).get())
                .addMethod(MethodInfo.builder().signature("dependency()")
                    .notStatic().notOverride().notOverloaded().notVarArg()
                    .minArgCount(0).index(1).accessibility(Accessibility.PUBLIC).get())
                .addMethodCall(MethodCallInfo.builder().callFromTo(0,1).at(8, 43)
                    .callType(CallType.METHOD_CALL).get())
                .get();
        verifyInfo(dc, "InputMethodCallsInLambda.java", info);
    }

    @Test
    public void testMethodCallThroughMethodReference() throws Exception {
        final DefaultConfiguration dc = createCheckConfig(MethodCallDependencyCheck.class);
        final DependencyInfo expected = DependencyInfo.builder()
                .addMethod(MethodInfo.builder().signature("a()")
                    .notStatic().notOverride().notOverloaded().notVarArg()
                    .minArgCount(0).index(0).accessibility(Accessibility.PUBLIC).get())
                .addMethod(MethodInfo.builder().signature("filter(Integer)")
                    .isStatic().notOverride().notOverloaded().notVarArg()
                    .minArgCount(1).index(1).accessibility(Accessibility.PUBLIC).get())
                .addMethodCall(MethodCallInfo.builder().callFromTo(0, 1).at(11, 61)
                        .callType(CallType.METHOD_REFERENCE).get())
                .get();
        verifyInfo(dc, "InputMethodCallThroughMethodReference.java", expected);
    }

    protected void verifyInfo(final Configuration config, final String fileName, final DependencyInfo expected) throws Exception {
        verify(config, getInputPath(fileName));
        final MethodCallDependencyCheck check = moduleFactory.getLastCheckInstance();
        assertNotNull(MethodCallDependencyCheck.class.getSimpleName() + " was not instantiated", check);
        mustBeSame(expected, check.getDependencyInfo());
    }

    protected Checker createChecker(Configuration checkConfig)
            throws Exception {
        final DefaultConfiguration dc = createCheckerConfig(checkConfig);
        final Checker checker = new Checker();
        // make sure the tests always run with default error messages (language-invariant)
        // so the tests don't fail in supported locales like German
        final Locale locale = Locale.ROOT;
        checker.setLocaleCountry(locale.getCountry());
        checker.setLocaleLanguage(locale.getLanguage());
        // following line is the only difference to super-class implementation of this method.
        checker.setModuleFactory(moduleFactory);
        checker.setModuleClassLoader(Thread.currentThread().getContextClassLoader());
        checker.configure(dc);
        checker.addListener(new BriefLogger(stream));
        return checker;
    }

    public static void mustBeSame(final DependencyInfo expected, final DependencyInfo actual) {
        for(final MethodInfo expectedMethod : expected.getMethods()) {
            assertThat("Method " + expectedMethod.getSignature() + " is not present is actual info",
                    actual.getMethods(), hasItem(expectedMethod));
        }
        for(final MethodInfo actualMethod : actual.getMethods()) {
            assertThat("Method " + actualMethod.getSignature() + " is not present in expected info",
                    expected.getMethods(), hasItem(actualMethod));
        }
        assertEquals("MethodCallInfo records count does not match",
                expected.getMethodCalls().size(),
                actual.getMethodCalls().size());
        for(final MethodCallInfo expectedCall : expected.getMethodCalls()) {
            final boolean existsInActual = actual.getMethodCalls().stream()
                    .anyMatch(mci -> areIdentical(mci, expectedCall));
            assertTrue("method call " + expectedCall + " is not reflected in actual method calls",
                    existsInActual);
        }
    }

    private static boolean areIdentical(final MethodCallInfo lhs, final MethodCallInfo rhs) {
        return lhs.getCallerIndex() == rhs.getCallerIndex()
                && lhs.getCalleeIndex() == rhs.getCalleeIndex()
                && lhs.getLineNo() == rhs.getLineNo()
                && lhs.getColumnNo() == rhs.getColumnNo()
                && lhs.getCallType().equals(rhs.getCallType());
    }

    private static String getInputPath(final String fileName) {
        return MethodCallDependencyCheckTest.class.getResource(fileName).getPath();
    }

    public static class StatefulModuleFactory implements ModuleFactory {

        private MethodCallDependencyCheck check;

        public MethodCallDependencyCheck getLastCheckInstance() {
            return check;
        }

        @Override
        public Object createModule(String name) throws CheckstyleException {
            try {
                final Object object = Class.forName(name).newInstance();
                if(object instanceof MethodCallDependencyCheck) {
                    check = (MethodCallDependencyCheck) object;
                }
                return object;
            }
            catch (Exception e) {
                throw new CheckstyleException("Cannot instantiate module " + name, e);
            }
        }
    }
}
