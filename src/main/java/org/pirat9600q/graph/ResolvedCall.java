package org.pirat9600q.graph;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ResolvedCall extends AnalysisSubject {

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
