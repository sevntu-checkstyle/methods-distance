package com.github.sevntu.checkstyle.domain;

import com.github.sevntu.checkstyle.utils.UnexpectedTokenTypeException;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class MethodCall {

    private final String methodName;

    private final boolean thisClassMethodCall;

    private final int argCount;

    private final DetailAST enclosingMethod;

    public MethodCall(DetailAST methodCall) {
        this.methodName = getMethodNameImpl(methodCall);
        this.thisClassMethodCall = isThisClassMethodCallImpl(methodCall);
        this.argCount = getArgCountImpl(methodCall);
        this.enclosingMethod = getEnclosingMethodImpl(methodCall);
    }

    private static String getMethodNameImpl(DetailAST methodCallNode) {
        final DetailAST methodCallFirstChild = methodCallNode.getFirstChild();
        switch (methodCallFirstChild.getType()) {
            case TokenTypes.IDENT:
                return methodCallFirstChild.getText();
            case TokenTypes.DOT:
                return methodCallFirstChild.getLastChild().getText();
            default:
                throw new UnexpectedTokenTypeException(methodCallFirstChild);
        }
    }

    private static boolean isThisClassMethodCallImpl(DetailAST methodCallNode) {
        final DetailAST firstChild = methodCallNode.getFirstChild();
        return firstChild.getType() == TokenTypes.IDENT
                || firstChild.getType() == TokenTypes.DOT
                && firstChild.getFirstChild().getType() == TokenTypes.LITERAL_THIS;
    }

    private static int getArgCountImpl(DetailAST methodCallNode) {
        return methodCallNode.findFirstToken(TokenTypes.ELIST).getChildCount(TokenTypes.EXPR);
    }

    private static DetailAST getEnclosingMethodImpl(DetailAST methodCallNode) {
        return AnalysisUtils.getEnclosingMethod(methodCallNode);
    }

    public String getMethodName() {
        return methodName;
    }

    public boolean isThisClassMethodCall() {
        return thisClassMethodCall;
    }

    public int getArgCount() {
        return argCount;
    }

    public DetailAST getEnclosingMethod() {
        return enclosingMethod;
    }
}
