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

package com.github.sevntu.checkstyle.domain;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Resolved method invocation.
 * <p>
 * That is method invocation using round parenthesis syntax or method reference syntax for which
 * caller method and called method are known(resolved).
 * </p>
 * @author Zuy Alexey
 */
public class ResolvedCall {

    private final DetailAST methodInvocation;

    private final MethodDefinition caller;

    private final MethodDefinition callee;

    public ResolvedCall(
            final DetailAST methodInvocation,
            final MethodDefinition caller,
            final MethodDefinition callee) {

        this.methodInvocation = methodInvocation;
        this.caller = caller;
        this.callee = callee;
    }

    public DetailAST getAstNode() {
        return methodInvocation;
    }

    public MethodDefinition getCaller() {
        return caller;
    }

    public MethodDefinition getCallee() {
        return callee;
    }

    public boolean isMethodRef() {
        return methodInvocation.getType() == TokenTypes.METHOD_REF;
    }

    public boolean isNestedInside(ResolvedCall other) {
        boolean result = false;
        for (DetailAST parent = getAstNode().getParent(); parent != null;
            parent = parent.getParent()) {
            if (parent.getLineNo() == other.getAstNode().getLineNo()
                && parent.getColumnNo() == other.getAstNode().getColumnNo()) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        else if (o == this) {
            return true;
        }
        else {
            final ResolvedCall rhs = (ResolvedCall) o;
            return methodInvocation.getLineNo() == rhs.methodInvocation.getLineNo()
                    && methodInvocation.getColumnNo() == rhs.methodInvocation.getColumnNo();
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(methodInvocation.getLineNo())
                .append(methodInvocation.getColumnNo())
                .toHashCode();
    }
}
