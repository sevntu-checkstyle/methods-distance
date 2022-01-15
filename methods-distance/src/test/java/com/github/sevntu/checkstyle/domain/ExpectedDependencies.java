////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
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
////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class ExpectedDependencies {

    private final List<String> methodSignatures = new ArrayList<>();

    private final List<MethodInvocation> methodDependencies = new ArrayList<>();

    private ExpectedDependencies() {
        // no code
    }

    public static WithCallsToOrNewMethodOrGet build() {
        return new Builder();
    }

    public List<String> getMethods() {
        return methodSignatures;
    }

    public List<MethodInvocation> getDependencies() {
        return methodDependencies;
    }

    public List<MethodInvocation> getInvocationsFromMethod(final String callerSignature) {
        final int callerIndex = methodSignatures.indexOf(callerSignature);
        return methodDependencies.stream()
                .filter(methodDep -> methodDep.caller == callerIndex)
                .collect(Collectors.toList());
    }

    public String getMethodByIndex(int index) {
        return methodSignatures.get(index);
    }

    public static final class MethodInvocation {

        private int caller;

        private int callee;

        private int atLine;

        private int atCol;

        private MethodInvocation() {
            // no code
        }

        public int getCaller() {
            return caller;
        }

        public int getCallee() {
            return callee;
        }

        public int getAtLine() {
            return atLine;
        }

        public int getAtCol() {
            return atCol;
        }
    }

    public static final class Builder implements WithCallsToOrNewMethodOrGet, WithLineCol {

        private final ExpectedDependencies expectedDependencies = new ExpectedDependencies();

        private MethodInvocation currentDependency;

        private Builder() {
            // no code
        }

        @Override
        public WithCallsToOrNewMethodOrGet method(String signature) {
            expectedDependencies.methodSignatures.add(signature);
            return this;
        }

        @Override
        public WithLineCol callsTo(int calleeIndex) {
            currentDependency = new MethodInvocation();
            currentDependency.caller = expectedDependencies.methodSignatures.size() - 1;
            currentDependency.callee = calleeIndex;
            return this;
        }

        @Override
        public ExpectedDependencies get() {
            return expectedDependencies;
        }

        @Override
        public WithCallsToOrNewMethodOrGet at(int line, int col) {
            currentDependency.atLine = line;
            currentDependency.atCol = col;
            expectedDependencies.methodDependencies.add(currentDependency);
            currentDependency = null;
            return this;
        }
    }

    public interface WithCallsToOrNewMethodOrGet {
        WithCallsToOrNewMethodOrGet method(String signature);

        WithLineCol callsTo(int calleeIndex);

        ExpectedDependencies get();
    }

    public interface WithLineCol {
        WithCallsToOrNewMethodOrGet at(int line, int col);
    }
}
