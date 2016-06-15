package com.github.sevntu.checkstyle.analysis;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class RefCall {

    private final DetailAST methodRef;

    private final ClassDefinition classDefinition;

    public RefCall(ClassDefinition classDefinition, DetailAST methodRef) {
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
        return AnalysisUtils.getEnclosingMethod(methodRef);
    }
}
