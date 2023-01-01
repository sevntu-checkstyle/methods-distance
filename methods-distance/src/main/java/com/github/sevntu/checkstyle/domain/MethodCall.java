///////////////////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code and other text files for adherence to a set of rules.
// Copyright (C) 2001-2023 the original author or authors.
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
///////////////////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle.domain;

import com.github.sevntu.checkstyle.analysis.AnalysisUtils;
import com.github.sevntu.checkstyle.common.UnexpectedTokenTypeException;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Method call using round parenthesis syntax. Called method is not known.
 *
 * @author Zuy Alexey
 */
public class MethodCall {

    private final String methodName;

    private final boolean thisClassMethodCall;

    private final int argCount;

    private final DetailAST enclosingMethod;

    public MethodCall(DetailAST methodCall) {
        methodName = getMethodNameImpl(methodCall);
        thisClassMethodCall = isThisClassMethodCallImpl(methodCall);
        argCount = getArgCountImpl(methodCall);
        enclosingMethod = getEnclosingMethodImpl(methodCall);
    }

    private static String getMethodNameImpl(DetailAST methodCallNode) {
        final String result;
        final DetailAST methodCallFirstChild = methodCallNode.getFirstChild();
        switch (methodCallFirstChild.getType()) {
            case TokenTypes.IDENT:
                result = methodCallFirstChild.getText();
                break;
            case TokenTypes.DOT:
                result = methodCallFirstChild.getLastChild().getText();
                break;
            default:
                throw new UnexpectedTokenTypeException(methodCallFirstChild);
        }
        return result;
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
