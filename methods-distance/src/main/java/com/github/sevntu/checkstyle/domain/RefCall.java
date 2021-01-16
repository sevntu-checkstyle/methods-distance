////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2021 the original author or authors.
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

package com.github.sevntu.checkstyle.domain;

import com.github.sevntu.checkstyle.analysis.AnalysisUtils;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Method invocation using method reference syntax. Called method is unknown.
 *
 * @author Zuy Alexey
 */
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
