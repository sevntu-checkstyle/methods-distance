////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2018 the original author or authors.
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

package com.github.sevntu.checkstyle.analysis;

import org.junit.Ignore;
import org.junit.Test;

import com.github.sevntu.checkstyle.domain.ExpectedDependencies;
import com.github.sevntu.checkstyle.module.MethodCallDependencyCheckstyleModule;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class MethodCallDependencyCheckstyleModuleTest extends MethodCallDependenciesModuleTestSupport {

    @Test
    public void testSimpleDependency() throws Exception {
        final DefaultConfiguration dc = createCheckConfig(MethodCallDependencyCheckstyleModule.class);
        final ExpectedDependencies dependencies = ExpectedDependencies.build()
                .method("InputSimpleDependency()")
                .callsTo(1).at(6, 17)
                .method("dependant()")
                .callsTo(2).at(10, 18)
                .method("dependency()")
                .callsTo(3).at(14, 29)
                .callsTo(4).at(15, 29)
                .method("dependencyDependency1()")
                .method("dependencyDependency2()")
                .get();
        verifyInfo(dc, "InputSimpleDependency.java", dependencies);
    }

    @Test
    @Ignore
    public void testEmptyInterface() throws Exception {
        final DefaultConfiguration dc = createCheckConfig(MethodCallDependencyCheckstyleModule.class);
        verifyInfo(dc, "InputEmptyInterface.java", ExpectedDependencies.build().get());
    }

    @Test
    public void testMethodCallInInitialization() throws Exception {
        final DefaultConfiguration dc = createCheckConfig(MethodCallDependencyCheckstyleModule.class);
        verifyInfo(dc, "InputMethodCallInInitialization.java", ExpectedDependencies.build().get());
    }

    @Test
    public void testAnonymousClasses() throws Exception {
        final DefaultConfiguration dc = createCheckConfig(MethodCallDependencyCheckstyleModule.class);
        final ExpectedDependencies expected = ExpectedDependencies.build()
                .method("method()")
                .method("a()")
                .method("a(String)")
                .method("topLevelMethod()")
                .get();
        verifyInfo(dc, "InputAnonymousClasses.java", expected);
    }

    @Test
    public void testRecursiveMethod() throws Exception {
        final DefaultConfiguration dc = createCheckConfig(MethodCallDependencyCheckstyleModule.class);
        final ExpectedDependencies expected = ExpectedDependencies.build()
                .method("method()")
                .callsTo(0).at(6, 14)
                .get();
        verifyInfo(dc, "InputRecursiveMethod.java", expected);
    }

    @Test
    public void testNotThisClassMethodCall() throws Exception {
        final DefaultConfiguration dc = createCheckConfig(MethodCallDependencyCheckstyleModule.class);
        final ExpectedDependencies expected = ExpectedDependencies.build()
                .method("method()")
                .method("format(String)")
                .get();
        verifyInfo(dc, "InputMethodNameClashes.java", expected);
    }

    @Test
    public void testMethodSignatures() throws Exception {
        final DefaultConfiguration dc = createCheckConfig(MethodCallDependencyCheckstyleModule.class);
        final ExpectedDependencies dep = ExpectedDependencies.build()
                .method("m()")
                .method("m(boolean)")
                .method("m(char)")
                .method("m(byte)")
                .method("m(short)")
                .method("m(int)")
                .method("m(long)")
                .method("m(double)")
                .method("m(String)")
                .method("m(String,String)")
                .method("m(String,Integer...)")
                .method("m(Integer...)")
                .method("m(int[])")
                .method("m(Long[])")
                .method("m(List)")
                .method("m(String,List...)")
                .method("m(List[])")
                .method("m(List[]...)")
                .get();
        verifyInfo(dc, "InputMethodSignatures.java", dep);
    }

    @Test
    public void testVarargMethodCall() throws Exception {
        final DefaultConfiguration dc = createCheckConfig(MethodCallDependencyCheckstyleModule.class);
        final ExpectedDependencies expected = ExpectedDependencies.build()
                .method("c1()")
                .callsTo(3).at(6, 20)
                .method("c2()")
                .callsTo(3).at(10, 20)
                .method("c3()")
                .callsTo(3).at(14, 20)
                .method("varargMethod(Integer...)")
                .get();
        verifyInfo(dc, "InputVarargMethodCall.java", expected);
    }

    @Test
    public void testMethodCallsInLambda() throws Exception {
        final DefaultConfiguration dc = createCheckConfig(MethodCallDependencyCheckstyleModule.class);
        final ExpectedDependencies expected = ExpectedDependencies.build()
                .method("m()")
                .callsTo(1).at(8, 43)
                .method("dependency()")
                .get();
        verifyInfo(dc, "InputMethodCallsInLambda.java", expected);
    }

    @Test
    public void testMethodCallThroughMethodReference() throws Exception {
        final DefaultConfiguration dc = createCheckConfig(MethodCallDependencyCheckstyleModule.class);
        final ExpectedDependencies expected = ExpectedDependencies.build()
                .method("a()")
                .callsTo(2).at(11, 61)
                .method("a1()")
                .method("a1(Integer)")
                .method("b()")
                .callsTo(5).at(20, 24)
                .method("b1()")
                .method("b1(Integer)")
                .get();
        verifyInfo(dc, "InputMethodCallThroughMethodReference.java", expected);
    }

    @Test
    public void testOverloadedMethods2() throws Exception {
        final DefaultConfiguration dc = createCheckConfig(MethodCallDependencyCheckstyleModule.class);
        final ExpectedDependencies expected = ExpectedDependencies.build()
                .method("b1()")
                .method("b1(String)")
                .method("b1(String,String)")
                .method("b1(String,String,String...)")
                .method("a1()")
                .callsTo(0).at(14, 10)
                .method("a2()")
                .callsTo(1).at(18, 10)
                .method("a3()")
                .callsTo(2).at(22, 10)
                .method("a4()")
                .callsTo(3).at(26, 10)
                .method("c1()")
                .method("c1(String...)")
                .method("d1()")
                .callsTo(9).at(34, 10)
                .get();
        verifyInfo(dc, "InputOverloadedMethods2.java", expected);
    }

    @Test
    public void testAppearanceOrder() throws Exception {
        final DefaultConfiguration dc = createCheckConfig(MethodCallDependencyCheckstyleModule.class);
        final ExpectedDependencies expected = ExpectedDependencies.build()
                .method("b1()")
                .method("b2()")
                .method("b3()")
                .method("a()")
                .callsTo(2).at(12, 10)
                .callsTo(1).at(13, 10)
                .callsTo(0).at(15, 10)
                .method("c1(String)")
                .method("c2()")
                .method("d()")
                .callsTo(5).at(24, 13)
                .callsTo(4).at(24, 10)
                .method("e1(String)")
                .method("e2(Integer)")
                .method("e3()")
                .method("f()")
                .callsTo(9).at(34, 16)
                .callsTo(8).at(34, 13)
                .callsTo(7).at(34, 10)
                .get();
        verifyInfo(dc, "InputAppearanceOrder.java", expected);
    }
}
