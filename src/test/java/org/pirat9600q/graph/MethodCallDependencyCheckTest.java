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
        final Dependencies expected = Dependencies.builder()
                .method("InputSimpleDependency()")
                .dependsOn("dependant()")
                .method("dependant()")
                .dependsOn("dependency()")
                .method("dependency()")
                .dependsOn("dependencyDependency1()")
                .dependsOn("dependencyDependency2()")
                .method("dependencyDependency1()")
                .method("dependencyDependency2()")
                .get();
        verifyGraph(dc, "InputSimpleDependency.java", expected);
    }

    @Test
    public void testEmptyInterface() throws Exception {
        final DefaultConfiguration dc = createCheckConfig(MethodCallDependencyCheck.class);
        verifyGraph(dc, "InputEmptyInterface.java", Dependencies.empty());
    }

    @Test
    public void testMethodCallInInitialization() throws Exception {
        final DefaultConfiguration dc = createCheckConfig(MethodCallDependencyCheck.class);
        verifyGraph(dc, "InputMethodCallInInitialization.java", Dependencies.empty());
    }

    @Test
    public void testAnonymousClasses() throws Exception {
        final DefaultConfiguration dc = createCheckConfig(MethodCallDependencyCheck.class);
        final Dependencies expected = Dependencies.builder()
                .method("method()")
                .method("a()")
                .method("a(String)")
                .method("topLevelMethod()")
                .get();
        verifyGraph(dc, "InputAnonymousClasses.java", expected);
    }

    @Test
    public void testRecursiveMethod() throws Exception {
        final DefaultConfiguration dc = createCheckConfig(MethodCallDependencyCheck.class);
        final Dependencies expected = Dependencies.builder()
                .method("method()")
                .dependsOn("method()")
                .get();
        verifyGraph(dc, "InputRecursiveMethod.java", expected);
    }

    @Test
    public void testMethodNameClashes() throws Exception {
        final DefaultConfiguration dc = createCheckConfig(MethodCallDependencyCheck.class);
        final Dependencies expected = Dependencies.builder()
                .method("method()")
                .method("format(String)")
                .get();
        verifyGraph(dc, "InputMethodNameClashes.java", expected);
    }

    @Test
    public void testMethodSignatures() throws Exception {
        final DefaultConfiguration dc = createCheckConfig(MethodCallDependencyCheck.class);
        final Dependencies expected = Dependencies.builder()
                .method("m()")
                .method("m(boolean)")
                .method("m(char)")
                .method("m(byte)")
                .method("m(short)")
                .method("m(int)")
                .method("m(long)")
                .method("m(double)")
                .method("m(String)")
                .method("m(String,String)")
                .method("m(String,Integer...)")
                .method("m(Integer...)")
                .method("m(int[])")
                .method("m(Long[])")
                .method("m(List)")
                .method("m(String,List...)")
                .method("m(List[])")
                .method("m(List[]...)")
                .get();
        verifyGraph(dc, "InputMethodSignatures.java", expected);
    }

    @Test
    public void testVarargMethodCall() throws Exception {
        final DefaultConfiguration dc = createCheckConfig(MethodCallDependencyCheck.class);
        final Dependencies expected = Dependencies.builder()
                .method("varargMethod(Integer...)")
                .method("c1()")
                .dependsOn("varargMethod(Integer...)")
                .method("c2()")
                .dependsOn("varargMethod(Integer...)")
                .method("c3()")
                .dependsOn("varargMethod(Integer...)")
                .get();
        verifyGraph(dc, "InputVarargMethodCall.java", expected);
    }

    @Test
    public void testMethodCallsInLambda() throws Exception {
        final DefaultConfiguration dc = createCheckConfig(MethodCallDependencyCheck.class);
        final Dependencies expected = Dependencies.builder()
                .method("dependency()")
                .method("m()")
                .dependsOn("dependency()")
                .get();
        verifyGraph(dc, "InputMethodCallsInLambda.java", expected);
    }

    @Test
    public void testMethodCallThroughMethodReference() throws Exception {
        final DefaultConfiguration dc = createCheckConfig(MethodCallDependencyCheck.class);
        final Dependencies expected = Dependencies.builder()
                .method("a()")
                .dependsOn("filter(Integer)")
                .method("filter(Integer)")
                .get();
        verifyGraph(dc, "InputMethodCallThroughMethodReference.java", expected);
    }

    protected void verifyGraph(final Configuration config, final String fileName, final Dependencies expected) throws Exception {
        verify(config, getInputPath(fileName));
        final MethodCallDependencyCheck check = moduleFactory.getLastCheckInstance();
        assertNotNull(MethodCallDependencyCheck.class.getSimpleName() + " was not instantiated", check);
        mustBeSame(expected, asDependencies(check.getGraph()));
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

    public static void mustBeSame(final Dependencies expectedDependencies, final Dependencies actualDependencies) {
        final SortedMap<String, SortedSet<String>> expected = expectedDependencies.getResult();
        final SortedMap<String, SortedSet<String>> actual = actualDependencies.getResult();
        for(final String method : expected.keySet()) {
            assertThat("Method " + method + " is not present in actual dependencies", actual.keySet(), hasItem(method));
        }
        final Set<String> extraMethods = Sets.difference(actual.keySet(), expected.keySet());
        if(!extraMethods.isEmpty()) {
            fail("Actual dependencies contain unexpected methods " + glueWith(extraMethods, ", "));
        }
        for(final String method : expected.keySet()) {
            assertArrayEquals(
                    "Dependencies of method " + method + " does not match",
                    expected.get(method).toArray(),
                    actual.get(method).toArray()
            );
        }
    }

    public static Dependencies asDependencies(final DependencyGraph graph) {
        final Builder result = Dependencies.builder();
        for(final DetailAST method : graph.getAllMethods()) {
            result.method(graph.getMethodSignature(method));
            for(final DetailAST dependency : graph.getMethodDependencies(method)) {
                result.dependsOn(graph.getMethodSignature(dependency));
            }
        }
        return result.get();
    }

    private static String getInputPath(final String fileName) {
        return MethodCallDependencyCheckTest.class.getResource(fileName).getPath();
    }

    private static String glueWith(final Collection<?> elements, final String glue) {
        return elements.stream().map(Object::toString).collect(Collectors.joining(glue));
    }

    public static class StatefulModuleFactory implements ModuleFactory {

        private MethodCallDependencyCheck check;

        public MethodCallDependencyCheck getLastCheckInstance() {
            return check;
        }

        @Override
        public Object createModule(String name) throws CheckstyleException {
            try {
                final Object object = getClass().forName(name).newInstance();
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

    public interface Builder {

        Builder method(final String methodName);

        Builder dependsOn(final String method);

        Dependencies get();
    }

    public static class Dependencies implements Builder {

        private final SortedMap<String, SortedSet<String>> methodDependencies = new TreeMap<>();

        private String method;

        private SortedSet<String> dependencies;

        public SortedMap<String, SortedSet<String>> getResult() {
            return methodDependencies;
        }

        private Dependencies() {}

        public Builder method(final String methodName) {
            if(method != null) {
                methodDependencies.put(method, dependencies);
            }
            method = methodName;
            dependencies = new TreeSet<>();
            return this;
        }

        public Builder dependsOn(final String method) {
            dependencies.add(method);
            return this;
        }

        public Dependencies get() {
            if(method != null) {
                methodDependencies.put(method, dependencies);
            }
            return this;
        }

        public static Builder builder() {
            return new Dependencies();
        }

        public static Dependencies empty() {
            return builder().get();
        }


    }
}
