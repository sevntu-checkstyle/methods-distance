package org.pirat9600q.graph;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class DependencyInfoTest {

    private DependencyInfo di;

    public DependencyInfoTest() {
        di = DependencyInfo.builder()
                .addMethod(MethodInfo.builder().signature("InputSimpleDependency()")
                        .isStatic(false).isOverride(false).isOverloaded(false).isVarArg(false)
                        .minArgCount(0).index(0).atLine(5).accessibility(MethodInfo.Accessibility.PUBLIC).get())
                .addMethod(MethodInfo.builder().signature("dependant()")
                        .isStatic(false).isOverride(false).isOverloaded(false).isVarArg(false)
                        .minArgCount(0).index(1).atLine(9).accessibility(MethodInfo.Accessibility.PUBLIC).get())
                .addMethod(MethodInfo.builder().signature("dependency()")
                        .isStatic(false).isOverride(false).isOverloaded(false).isVarArg(false)
                        .minArgCount(0).index(2).atLine(13).accessibility(MethodInfo.Accessibility.PUBLIC).get())
                .addMethod(MethodInfo.builder().signature("dependencyDependency1()")
                        .isStatic(false).isOverride(false).isOverloaded(false).isVarArg(false)
                        .minArgCount(0).index(3).atLine(18).accessibility(MethodInfo.Accessibility.PUBLIC).get())
                .addMethod(MethodInfo.builder().signature("dependencyDependency2()")
                        .isStatic(false).isOverride(false).isOverloaded(false).isVarArg(false)
                        .minArgCount(0).index(4).atLine(22).accessibility(MethodInfo.Accessibility.PUBLIC).get())
                .addMethodCall(MethodCallInfo.builder()
                        .callerIndex(0).calleeIndex(1).lineNo(6).columnNo(17)
                        .callType(MethodCallInfo.CallType.METHOD_CALL).get())
                .addMethodCall(MethodCallInfo.builder()
                        .callerIndex(1).calleeIndex(2).lineNo(10).columnNo(18)
                        .callType(MethodCallInfo.CallType.METHOD_CALL).get())
                .addMethodCall(MethodCallInfo.builder()
                        .callerIndex(2).calleeIndex(3).lineNo(14).columnNo(29)
                        .callType(MethodCallInfo.CallType.METHOD_CALL).get())
                .addMethodCall(MethodCallInfo.builder()
                        .callerIndex(2).calleeIndex(4).lineNo(15).columnNo(29)
                        .callType(MethodCallInfo.CallType.METHOD_CALL).get())
                .get();
    }

    @Test
    public void testReturnsCorrectMethodDependencies() throws Exception {
        final MethodInfo dependency = di.getMethodByIndex(2);
        final Set<MethodInfo> actualSet = di.getMethodDependencies(dependency);
        final Set<MethodInfo> expectedSet =
                ImmutableSet.of(di.getMethodByIndex(3), di.getMethodByIndex(4));
        assertArrayEquals(expectedSet.toArray(), actualSet.toArray());
    }

    @Test
    public void testReturnsCorrectMethodDependants() throws Exception {
        final MethodInfo dependency = di.getMethodByIndex(2);
        final Set<MethodInfo> actualSet = di.getMethodDependants(dependency);
        final Set<MethodInfo> expectedSet = ImmutableSet.of(di.getMethodByIndex(1));
        assertArrayEquals(expectedSet.toArray(), actualSet.toArray());
    }

    @Test
    public void testReturnsCorrectDependenciesByAppearance() throws Exception {
        final MethodInfo dependency = di.getMethodByIndex(2);
        final List<Integer> actualList = di.getMethodDependenciesIndicesOrderedByAppearance(dependency);
        final List<Integer> expectedList = ImmutableList.of(3,4);
        assertArrayEquals(expectedList.toArray(), actualList.toArray());
    }
}
