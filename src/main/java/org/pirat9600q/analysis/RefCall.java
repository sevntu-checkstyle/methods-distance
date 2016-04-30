package org.pirat9600q.analysis;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class RefCall extends AnalysisSubject {

    private final DetailAST methodRef;

    private final ClassDefinition classDefinition;

    public RefCall(final ClassDefinition classDefinition, final DetailAST methodRef) {
        this.methodRef = methodRef;
        this.classDefinition = classDefinition;
    }

    public String getMethodName() {
        return methodRef.getLastChild().getText();
    }

    public String getCalledClass() {
        return methodRef.getFirstChild().getText();
    }

    public boolean isRefToMethodOfEnclosingClass() {
        return isRefToStaticMethodOfEnclosingClass() || isRefToInstanceMethodOfClass();
    }

    public boolean isRefToStaticMethodOfEnclosingClass() {
        return getCalledClass().equals(classDefinition.getClassName());
    }

    public boolean isRefToInstanceMethodOfClass() {
        return methodRef.getFirstChild().getType() == TokenTypes.LITERAL_THIS;
    }

    public DetailAST getEnclosingMethod() {
        return getEnclosingMethod(methodRef);
    }
}
