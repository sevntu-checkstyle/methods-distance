package com.github.sevntu.checkstyle.analysis;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class MethodCall extends AnalysisSubject {

    private final DetailAST methodCallNode;

    public MethodCall(final DetailAST methodCall) {
        this.methodCallNode = methodCall;
    }

    public String getMethodName() {
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

    public boolean isThisClassMethodCall() {
        final DetailAST firstChild = methodCallNode.getFirstChild();
        return firstChild.getType() == TokenTypes.IDENT
                || firstChild.getType() == TokenTypes.DOT
                && firstChild.getFirstChild().getType() == TokenTypes.LITERAL_THIS;
    }

    public int getArgCount() {
        return methodCallNode.findFirstToken(TokenTypes.ELIST).getChildCount(TokenTypes.EXPR);
    }

    public DetailAST getEnclosingMethod() {
        return getEnclosingMethod(methodCallNode);
    }
}
