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

package com.github.sevntu.checkstyle.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.github.sevntu.checkstyle.analysis.MethodCallDependenciesModuleTestSupport;

public class ClassDefinitionTest extends MethodCallDependenciesModuleTestSupport {

    @Test
    public void testSeveralAccessors() throws Exception {
        final Dependencies d = withDefaultConfig("InputClassDefinition1.java");
        final Map<String, List<MethodDefinition>> accessors = d.getClassDefinition()
                .getPropertiesAccessors();
        final Map<String, List<String>> expected = new HashMap<>();
        expected.put("name", Collections.singletonList("getName()"));
        expected.put("height", Arrays.asList("getHeight()", "setHeight(int)"));
        expected.put("fat", Arrays.asList("isFat()", "getFat()", "setFat(boolean)"));
        assertEquals(expected.size(), accessors.size());
        for (final Map.Entry<String, List<MethodDefinition>> e : accessors.entrySet()) {
            final List<String> expectedMethods = expected.get(e.getKey());
            final List<MethodDefinition> actualMethods = e.getValue();
            assertEquals(expectedMethods.size(), actualMethods.size());
            for (final MethodDefinition methodDefinition : actualMethods) {
                assertTrue(expectedMethods.contains(methodDefinition.getSignature()));
            }
        }
    }
}
