package org.pirat9600q.graph;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class MethodCall extends AnalysisSubject {

    private final DetailAST methodCall;

    private final ClassDefinition classDefinition;

    public MethodCall(final ClassDefinition classDefinition, final DetailAST methodCall) {
        this.classDefinition = classDefinition;
        this.methodCall = methodCall;
    }

    public String getMethodName() {
        final DetailAST methodCallFirstChild = methodCall.getFirstChild();
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
        final DetailAST firstChild = methodCall.getFirstChild();
        return firstChild.getType() == TokenTypes.IDENT
                || firstChild.getType() == TokenTypes.DOT
                && firstChild.getFirstChild().getType() == TokenTypes.LITERAL_THIS;
    }

    public int getArgCount() {
        return methodCall.findFirstToken(TokenTypes.ELIST).getChildCount(TokenTypes.EXPR);
    }

    public DetailAST getEnclosingMethod() {
        return getEnclosingMethod(methodCall);
    }
}
