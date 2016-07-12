package com.github.sevntu.checkstyle.analysis;

import com.github.sevntu.checkstyle.ordering.Method;
import com.github.sevntu.checkstyle.ordering.Ordering;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MethodDefinitionTest extends MethodCallDependenciesModuleTestSupport {

    @Test
    public void testGetterSetterRecognition() throws Exception {
        final Ordering dc = withDefaultConfigOrdering("InputMethodDefinition1.java");
        for(final int index : new int[]{0, 1, 2, 3, 4}) {
            final Method method = dc.getMethodByInitialIndex(index);
            assertTrue(method.isGetter());
            assertFalse(method.isSetter());
        }
        for(final int index : new int[]{7}) {
            final Method method = dc.getMethodByInitialIndex(index);
            assertFalse(method.isGetter());
            assertTrue(method.isSetter());
        }
        for(final int index : new int[]{5, 6, 8, 9, 10, 11}) {
            final Method method = dc.getMethodByInitialIndex(index);
            assertFalse(method.isGetter());
            assertFalse(method.isSetter());
        }
    }

    @Test
    public void testGetterSetterRecognitionsWithCtors() throws Exception {
        final Ordering dc = withDefaultConfigOrdering("InputMethodDefinition2.java");
        final Method noArgCtor = dc.getMethodByInitialIndex(0);
        assertFalse(noArgCtor.isGetter());
        assertFalse(noArgCtor.isSetter());
        final Method singleArgCtor = dc.getMethodByInitialIndex(1);
        assertFalse(singleArgCtor.isGetter());
        assertFalse(singleArgCtor.isSetter());
    }
}
