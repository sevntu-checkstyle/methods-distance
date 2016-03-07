package org.pirat9600q.graph;

import com.puppycrawl.tools.checkstyle.api.Configuration;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DependenciesTest extends MethodCallDependenciesCheckTestSupport {

    private final Dependencies ds;

    public DependenciesTest() throws Exception {
        final Configuration dc = createCheckConfig(MethodCallDependencyCheck.class);
        ds = invokeCheckAndGetDependencies(dc, "InputDependencies.java");
    }

    @Test
    public void testDependencies() throws Exception {
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
}
