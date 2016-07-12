package com.github.sevntu.checkstyle.analysis;

import com.github.sevntu.checkstyle.ordering.Method;
import com.github.sevntu.checkstyle.ordering.Ordering;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OrderingReorderTest extends MethodCallDependenciesModuleTestSupport {

    @Test
    public void testReordering() throws Exception {
        final int screenLinesCount = 5;
        final Ordering first = withDefaultConfigOrdering("InputOrderingReordering1.java");
        final Method b = first.getMethodByInitialIndex(3);
        final Method c = first.getMethodByInitialIndex(6);
        final Method d3 = first.getMethodByInitialIndex(9);
        final Method e = first.getMethodByInitialIndex(13);
        final Method f = first.getMethodByInitialIndex(15);
        final Method h = first.getMethodByInitialIndex(17);
        final Ordering firstStep1 = first.moveMethodBy(b, -3);
        final Ordering firstStep2 = firstStep1.moveMethodBy(c, -1);
        final Ordering firstStep3 = firstStep2.moveMethodBy(d3, 2);
        final Ordering firstStep4 = firstStep3.moveMethodBy(e, 1);
        final Ordering firstStep5 = firstStep4.moveMethodBy(f, 1);
        final Ordering firstLikeSecond = firstStep5.moveMethodBy(h, 1);
        final Ordering second = withDefaultConfigOrdering("InputOrderingReordering2.java");
        compare(second, firstLikeSecond, screenLinesCount);
    }

    private static void compare(final Ordering expected, final Ordering actual, final int screenLinesCount) {
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
