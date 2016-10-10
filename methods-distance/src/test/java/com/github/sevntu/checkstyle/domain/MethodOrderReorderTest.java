package com.github.sevntu.checkstyle.domain;

import com.github.sevntu.checkstyle.analysis.MethodCallDependenciesModuleTestSupport;
import com.github.sevntu.checkstyle.ordering.Method;
import com.github.sevntu.checkstyle.ordering.MethodOrder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MethodOrderReorderTest extends MethodCallDependenciesModuleTestSupport {

    @Test
    public void testReordering() throws Exception {
        final int screenLinesCount = 5;
        final MethodOrder first = withDefaultConfigOrdering("InputOrderingReordering1.java");
        final Method b = first.getMethodByInitialIndex(3);
        final Method c = first.getMethodByInitialIndex(6);
        final Method d3 = first.getMethodByInitialIndex(9);
        final Method e = first.getMethodByInitialIndex(13);
        final Method f = first.getMethodByInitialIndex(15);
        final Method h = first.getMethodByInitialIndex(17);
        final MethodOrder firstStep1 = first.moveMethodBy(b, -3);
        final MethodOrder firstStep2 = firstStep1.moveMethodBy(c, -1);
        final MethodOrder firstStep3 = firstStep2.moveMethodBy(d3, 2);
        final MethodOrder firstStep4 = firstStep3.moveMethodBy(e, 1);
        final MethodOrder firstStep5 = firstStep4.moveMethodBy(f, 1);
        final MethodOrder firstLikeSecond = firstStep5.moveMethodBy(h, 1);
        final MethodOrder second = withDefaultConfigOrdering("InputOrderingReordering2.java");
        compare(second, firstLikeSecond, screenLinesCount);
    }

    private static void compare(final MethodOrder expected, final MethodOrder actual, final int screenLinesCount) {
        assertEquals(expected.getAccessorsSplitCases(), actual.getAccessorsSplitCases());
        assertEquals(expected.getDeclarationBeforeUsageCases(), actual.getDeclarationBeforeUsageCases());
        assertEquals(expected.getDependenciesBetweenDistantMethodsCases(screenLinesCount),
            actual.getDependenciesBetweenDistantMethodsCases(screenLinesCount));
        assertEquals(expected.getOverloadGroupsSplitCases(), actual.getOverloadGroupsSplitCases());
        assertEquals(expected.getOverrideGroupSplitCases(), actual.getOverrideGroupSplitCases());
        assertEquals(expected.getRelativeOrderInconsistencyCases(), actual.getRelativeOrderInconsistencyCases());
        assertEquals(expected.getTotalSumOfMethodDistances(), expected.getTotalSumOfMethodDistances());
    }
}
