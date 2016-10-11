package com.github.sevntu.checkstyle.analysis;

import com.github.sevntu.checkstyle.common.UnexpectedTokenTypeException;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.TokenUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class AnalysisUtils {

    private AnalysisUtils() { }

    public static boolean isInsideClassDef(DetailAST node) {
        final DetailAST parent = getClosestParentOfTypes(node, TokenTypes.CLASS_DEF,
                TokenTypes.LITERAL_NEW, TokenTypes.INTERFACE_DEF);
        switch (parent.getType()) {
            case TokenTypes.CLASS_DEF:
                return true;
            case TokenTypes.LITERAL_NEW:
            case TokenTypes.INTERFACE_DEF:
                return false;
            default:
                throw new UnexpectedTokenTypeException(parent);
        }
    }

    public static DetailAST getEnclosingClass(DetailAST node) {
        return getClosestParentOfTypes(node, TokenTypes.CLASS_DEF);
    }

    public static boolean isNestedInsideMethodDef(DetailAST node) {
        final DetailAST parent = getClosestParentOfTypes(node, TokenTypes.METHOD_DEF,
                TokenTypes.CTOR_DEF, TokenTypes.VARIABLE_DEF);
        switch (parent.getType()) {
            case TokenTypes.METHOD_DEF:
            case TokenTypes.CTOR_DEF:
                return true;
            case TokenTypes.VARIABLE_DEF:
                return !isFieldDeclaration(parent) && isNestedInsideMethodDef(parent);
            default:
                throw new UnexpectedTokenTypeException(parent);
        }
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
                .map(TokenUtils::getTokenName)
                .collect(Collectors.joining(", "));
        final String msg = String.format(
                "Node of type %s is not contained within node of types %s",
                TokenUtils.getTokenName(node.getType()),
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
}
