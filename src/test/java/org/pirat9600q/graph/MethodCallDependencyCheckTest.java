package org.pirat9600q.graph;

import com.google.common.collect.Sets;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.ModuleFactory;
import com.puppycrawl.tools.checkstyle.TreeWalker;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
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
                .method("dependant()")
                .dependsOn("dependency()")
                .method("dependency()")
                .dependsOn("dependencyDependency1()")
                .dependsOn("dependencyDependency2()")
                .method("dependencyDependency1()")
                .method("dependencyDependency2()");
        verifyGraph(dc, "InputSimpleDependency.java", expected);
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
        final Dependencies result = Dependencies.builder();
        for(final DetailAST method : graph.getAllMethods()) {
            result.method(graph.getMethodSignature(method));
            for(final DetailAST dependency : graph.getMethodDependencies(method)) {
                result.dependsOn(graph.getMethodSignature(dependency));
            }
        }
        return result;
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

    public static class Dependencies {

        private final SortedMap<String, SortedSet<String>> methodDependencies = new TreeMap<>();

        private String method;

        private SortedSet<String> dependencies;

        public SortedMap<String, SortedSet<String>> getResult() {
            return methodDependencies;
        }

        private Dependencies() {}

        public Dependencies method(final String methodName) {
            if(method != null) {
                methodDependencies.put(method, dependencies);
            }
            method = methodName;
            dependencies = new TreeSet<>();
            return this;
        }

        public Dependencies dependsOn(final String method) {
            dependencies.add(method);
            return this;
        }

        public static Dependencies builder() {
            return new Dependencies();
        }
    }
}
