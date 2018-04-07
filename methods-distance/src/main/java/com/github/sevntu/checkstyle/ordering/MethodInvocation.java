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

package com.github.sevntu.checkstyle.ordering;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.github.sevntu.checkstyle.domain.ResolvedCall;

public class MethodInvocation {

    private final Method caller;

    private final Method callee;

    private final int initialLineNo;

    private final int columnNo;

    public MethodInvocation(ResolvedCall resolvedCall, Method caller, Method callee) {
        this.caller = caller;
        this.callee = callee;
        initialLineNo = resolvedCall.getAstNode().getLineNo();
        columnNo = resolvedCall.getAstNode().getColumnNo();
    }

    public Method getCaller() {
        return caller;
    }

    public Method getCallee() {
        return callee;
    }

    public int getInitialLineNo() {
        return initialLineNo;
    }

    public int getColumnNo() {
        return columnNo;
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
            final MethodInvocation mi = (MethodInvocation) o;
            return new EqualsBuilder()
                .append(caller, mi.caller)
                .append(callee, mi.callee)
                .append(initialLineNo, mi.initialLineNo)
                .append(columnNo, mi.columnNo)
                .isEquals();
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(caller)
            .append(callee)
            .append(initialLineNo)
            .append(columnNo)
            .toHashCode();
    }

    @Override
    public String toString() {
        return String.format("%s -> %s initially at (%d,%d)",
            caller.getSignature(), callee.getSignature(), initialLineNo, columnNo);
    }
}
