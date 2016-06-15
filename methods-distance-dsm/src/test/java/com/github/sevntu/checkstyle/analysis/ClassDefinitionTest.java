package com.github.sevntu.checkstyle.analysis;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ClassDefinitionTest extends MethodCallDependenciesModuleTestSupport {

    @Test
    public void testSeveralAccessors() throws Exception {
        final Dependencies d = withDefaultConfig("InputClassDefinition1.java");
        final Map<String, List<MethodDefinition>> accessors = d.getClassDefinition().getPropertiesAccessors();
        final Map<String, List<String>> expected = new HashMap<>();
        expected.put("name", Collections.singletonList("getName()"));
        expected.put("height", Arrays.asList("getHeight()", "setHeight(int)"));
        expected.put("fat", Arrays.asList("isFat()", "getFat()", "setFat(boolean)"));
        assertEquals(expected.size(), accessors.size());
        for(final Map.Entry<String, List<MethodDefinition>> e : accessors.entrySet()) {
            final List<String> expectedMethods = expected.get(e.getKey());
            final List<MethodDefinition> actualMethods = e.getValue();
            assertEquals(expectedMethods.size(), actualMethods.size());
            for(final MethodDefinition md : actualMethods) {
                assertTrue(expectedMethods.contains(md.getSignature()));
            }
        }
    }
}
