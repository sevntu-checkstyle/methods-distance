package com.github.sevntu.checkstyle.domain;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Resolved method invocation.
 *
 * That is method invocation using round parenthesis syntax or method reference syntax for which
 * caller method and called method are known(resolved).
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
        for (DetailAST parent = getAstNode().getParent(); parent != null;
            parent = parent.getParent()) {
            if (parent.getLineNo() == other.getAstNode().getLineNo()
                && parent.getColumnNo() == other.getAstNode().getColumnNo()) {
                return true;
            }
        }
        return false;
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
