package com.github.sevntu.checkstyle.ordering;

import com.github.sevntu.checkstyle.analysis.ResolvedCall;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

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
