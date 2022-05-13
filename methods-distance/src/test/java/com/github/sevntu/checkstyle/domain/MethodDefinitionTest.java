///////////////////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code and other text files for adherence to a set of rules.
// Copyright (C) 2001-2022 the original author or authors.
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
///////////////////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle.domain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.sevntu.checkstyle.analysis.MethodCallDependenciesModuleTestSupport;
import com.github.sevntu.checkstyle.ordering.Method;
import com.github.sevntu.checkstyle.ordering.MethodOrder;

public class MethodDefinitionTest extends MethodCallDependenciesModuleTestSupport {

    @Test
    public void testGetterSetterRecognition() throws Exception {
        final MethodOrder dc = withDefaultConfigOrdering("InputMethodDefinition1.java");
        for (final int index : new int[]{0, 1, 2, 3, 4}) {
            final Method method = dc.getMethodByInitialIndex(index);
            assertTrue(method.isGetter());
            assertFalse(method.isSetter());
        }
        for (final int index : new int[]{7}) {
            final Method method = dc.getMethodByInitialIndex(index);
            assertFalse(method.isGetter());
            assertTrue(method.isSetter());
        }
        for (final int index : new int[]{5, 6, 8, 9, 10, 11}) {
            final Method method = dc.getMethodByInitialIndex(index);
            assertFalse(method.isGetter());
            assertFalse(method.isSetter());
        }
    }

    @Test
    public void testGetterSetterRecognitionsWithCtors() throws Exception {
        final MethodOrder dc = withDefaultConfigOrdering("InputMethodDefinition2.java");
        final Method noArgCtor = dc.getMethodByInitialIndex(0);
        assertFalse(noArgCtor.isGetter());
        assertFalse(noArgCtor.isSetter());
        final Method singleArgCtor = dc.getMethodByInitialIndex(1);
        assertFalse(singleArgCtor.isGetter());
        assertFalse(singleArgCtor.isSetter());
    }
}
