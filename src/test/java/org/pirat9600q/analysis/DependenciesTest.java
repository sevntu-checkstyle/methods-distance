package org.pirat9600q.analysis;

import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DependenciesTest extends MethodCallDependenciesCheckTestSupport {

    @Test
    public void testDependencies() throws Exception {
        final Configuration dc = createCheckConfig(MethodCallDependencyCheck.class);
        final Dependencies ds = invokeCheckAndGetDependencies(dc, "InputDependencies.java");
        final MethodDefinition methodB = ds.getMethodByIndex(1);
        final List<MethodDefinition> dependencies = ds.getMethodDependencies(methodB);
        assertTrue(dependencies.size() == 1);
        assertTrue(dependencies.get(0).getSignature().equals("c()"));
        final List<MethodDefinition> dependants = ds.getMethodDependants(methodB);
        assertTrue(dependants.size() == 1);
        assertTrue(dependants.get(0).getSignature().equals("a()"));
        final MethodDefinition methodD = ds.getMethodByIndex(3);
        assertFalse(ds.hasMethodDependants(methodD));
        assertFalse(ds.hasMethodDependencies(methodD));
        final MethodDefinition methodA = ds.getMethodByIndex(0);
        assertTrue(ds.isMethodDependsOn(methodA, methodB));
        assertFalse(ds.isMethodDependsOn(methodA, methodD));
    }

    @Test
    public void testTotalSumOfMethodDistances1() throws Exception {
        final Dependencies ds = withDefaultConfig("InputDependenciesDistance1.java");
        assertEquals(12, ds.getTotalSumOfMethodDistances());
    }

    @Test
    public void testTotalSumOfMethodDistances2() throws Exception {
        final Dependencies ds = withDefaultConfig("InputDependenciesDistance2.java");
        assertEquals(3, ds.getTotalSumOfMethodDistances());
    }

    @Test
    public void testDeclarationBeforeUsageCases() throws Exception {
        final Dependencies ds = withDefaultConfig("InputDependenciesDeclarationBeforeUsage.java");
        assertEquals(2, ds.getDeclarationBeforeUsageCases());
    }

    @Test
    public void testOverloadSplit1() throws Exception {
        final Dependencies ds = withDefaultConfig("InputDependenciesOverloadSplit1.java");
        assertEquals(5, ds.getOverloadGroupSplitCases());
    }

    @Test
    public void testOverloadSplit2() throws Exception {
        final Dependencies ds = withDefaultConfig("InputDependenciesOverloadSplit2.java");
        assertEquals(14, ds.getOverloadGroupSplitCases());
    }

    @Test
    public void testOverrideSplit1() throws Exception {
        final Dependencies ds = withDefaultConfig("InputDependenciesOverrideSplit1.java");
        assertEquals(3, ds.getOverrideGroupSplitCases());
    }

    @Test
    public void testOverrideSplit2() throws Exception {
        final Dependencies ds = withDefaultConfig("InputDependenciesOverrideSplit2.java");
        assertEquals(0, ds.getOverrideGroupSplitCases());
    }

    @Test
    public void testOverrideSplit3() throws Exception {
        final Dependencies ds = withDefaultConfig("InputDependenciesOverrideSplit3.java");
        assertEquals(0, ds.getOverrideGroupSplitCases());
    }

    @Test
    public void testRelativeOrderInconsistency() throws Exception {
        final Map<String, Integer> expected = new TreeMap<>();
        expected.put("InputDependenciesOrderInconsistency1.java", 0);
        expected.put("InputDependenciesOrderInconsistency2.java", 1);
        expected.put("InputDependenciesOrderInconsistency3.java", 1);
        expected.put("InputDependenciesOrderInconsistency4.java", 1);
        expected.put("InputDependenciesOrderInconsistency5.java", 1);
        expected.put("InputDependenciesOrderInconsistency6.java", 0);
        for(final Map.Entry<String,Integer> e : expected.entrySet()) {
            final String msg = String.format("Incorrect result for input \"%s\"", e.getKey());
            assertEquals(msg, e.getValue().longValue(), withDefaultConfig(e.getKey()).getRelativeOrderInconsistencyCases());
        }
    }

    @Test
    public void testAccessorsSplit() throws Exception {
        final Dependencies ds = withDefaultConfig("InputDependenciesAccessorsSplit.java");
        assertEquals(3, ds.getAccessorsSplitCases());
    }

    @Test
    public void testCallsBetweenDistantMethods() throws Exception {
        final DefaultConfiguration config = createCheckConfig(MethodCallDependencyCheck.class);
        config.addAttribute("screenLinesCount", "5");
        final Map<String, Integer> expected = new TreeMap<>();
        expected.put("InputDependenciesDistantMethodCall1.java", 1);
        expected.put("InputDependenciesDistantMethodCall2.java", 2);
        expected.put("InputDependenciesDistantMethodCall3.java", 1);
        expected.put("InputDependenciesDistantMethodCall4.java", 0);
        for(final Map.Entry<String, Integer> e : expected.entrySet()) {
            final Dependencies dependencies = invokeCheckAndGetDependencies(config, e.getKey());
            final String msg = String.format("Incorrect result for input \"%s\"", e.getKey());
            assertEquals(msg, e.getValue().intValue(), dependencies.getDependenciesBetweenDistantMethodsCases());
        }
    }
}
