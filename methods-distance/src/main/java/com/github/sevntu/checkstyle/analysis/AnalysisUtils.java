////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2022 the original author or authors.
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

package com.github.sevntu.checkstyle.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.github.sevntu.checkstyle.common.UnexpectedTokenTypeException;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.TokenUtil;

public final class AnalysisUtils {

    private AnalysisUtils() {
        // no code
    }

    public static boolean isInsideClassDef(DetailAST node) {
        final DetailAST parent = getClosestParentOfTypes(node, TokenTypes.CLASS_DEF,
                TokenTypes.LITERAL_NEW, TokenTypes.INTERFACE_DEF);
        final boolean result;
        switch (parent.getType()) {
            case TokenTypes.CLASS_DEF:
                result = true;
                break;
            case TokenTypes.LITERAL_NEW:
                result = !isAnonymousClassDef(parent);
                break;
            case TokenTypes.INTERFACE_DEF:
                result = false;
                break;
            default:
                throw new UnexpectedTokenTypeException(parent);
        }
        return result;
    }

    public static DetailAST getEnclosingClass(DetailAST node) {
        return getClosestParentOfTypes(node, TokenTypes.CLASS_DEF);
    }

    public static boolean isNestedInsideMethodDef(DetailAST node) {
        final DetailAST parent = getClosestParentOfTypes(node, TokenTypes.METHOD_DEF,
                TokenTypes.CTOR_DEF, TokenTypes.VARIABLE_DEF);
        final boolean result;
        switch (parent.getType()) {
            case TokenTypes.METHOD_DEF:
            case TokenTypes.CTOR_DEF:
                result = true;
                break;
            case TokenTypes.VARIABLE_DEF:
                result = !isFieldDeclaration(parent) && isNestedInsideMethodDef(parent);
                break;
            default:
                throw new UnexpectedTokenTypeException(parent);
        }
        return result;
    }

    public static boolean isFieldDeclaration(DetailAST variableDef) {
        return variableDef.getParent().getType() == TokenTypes.OBJBLOCK;
    }

    public static DetailAST getEnclosingMethod(DetailAST node) {
        return getClosestParentOfTypes(node, TokenTypes.METHOD_DEF, TokenTypes.CTOR_DEF);
    }

    public static DetailAST getClosestParentOfTypes(DetailAST node, Integer... ofTypes) {
        for (DetailAST parent = node.getParent(); parent != null; parent = parent.getParent()) {
            if (Arrays.asList(ofTypes).contains(parent.getType())) {
                return parent;
            }
        }
        final String tokenTypeNames = Arrays.stream(ofTypes)
                .map(TokenUtil::getTokenName)
                .collect(Collectors.joining(", "));
        final String msg = String.format(
                "Node of type %s is not contained within node of types %s",
                TokenUtil.getTokenName(node.getType()),
                tokenTypeNames);
        throw new IllegalStateException(msg);
    }

    /**
     * Get direct children of node with specified types.
     *
     * @param node parent node
     * @param ofTypes child node types
     * @return list of child nodes
     */
    public static List<DetailAST> getNodeChildren(DetailAST node, Integer... ofTypes) {
        final List<DetailAST> result = new ArrayList<>();
        for (DetailAST child = node.getFirstChild();
             child != null; child = child.getNextSibling()) {
            if (Arrays.asList(ofTypes).contains(child.getType())) {
                result.add(child);
            }
        }
        return result;
    }

    /**
     * Whether the AST is a definition of an anonymous class.
     *
     * @param ast the AST to process.
     * @return true if the AST is a definition of an anonymous class.
     */
    private static boolean isAnonymousClassDef(DetailAST ast) {
        final DetailAST lastChild = ast.getLastChild();
        return lastChild != null
            && lastChild.getType() == TokenTypes.OBJBLOCK;
    }
}
