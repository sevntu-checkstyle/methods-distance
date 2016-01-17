package org.pirat9600q.graph;

import com.google.common.collect.ImmutableSet;
import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;

import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.TokenUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MethodCallDependencyCheck extends Check {

    private static final Set<Integer> PRIMITIVE_TOKEN_TYPES = ImmutableSet.of(
            TokenTypes.LITERAL_VOID,
            TokenTypes.LITERAL_BOOLEAN,
            TokenTypes.LITERAL_CHAR,
            TokenTypes.LITERAL_BYTE,
            TokenTypes.LITERAL_SHORT,
            TokenTypes.LITERAL_INT,
            TokenTypes.LITERAL_LONG,
            TokenTypes.LITERAL_DOUBLE
    );

    private DependencyGraph graph;

    private DetailAST topLevelClass;

    private boolean writeResult;

    public DependencyGraph getGraph() {
        return graph;
    }

    public void setWriteResult(final boolean wr) {
        writeResult = wr;
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[]{TokenTypes.METHOD_CALL, TokenTypes.METHOD_REF, TokenTypes.CLASS_DEF};
    }

    @Override
    public void beginTree(DetailAST rootAST) {
        graph = new DependencyGraph();
        topLevelClass = null;
    }

    @Override
    public void visitToken(DetailAST ast) {
        switch (ast.getType()) {
            case TokenTypes.METHOD_CALL:
                processMethodCall(ast);
                break;
            case TokenTypes.METHOD_REF:
                processMethodRefCall(ast);
                break;
            case TokenTypes.CLASS_DEF:
                processTopLevelClass(ast);
                break;
            default:
                throw unexpectedTokenTypeException(ast);
        }
    }

    protected void processMethodCall(final DetailAST methodCall) {
        if (isNestedInsideMethodDef(methodCall)
                && isInsideClassDef(methodCall)
                && isThisClassMethodCall(methodCall)) {
            final DetailAST enclosingMethod = getEnclosingMethod(methodCall);
            final DetailAST enclosingClass = getEnclosingClass(methodCall);
            if (enclosingClass.equals(topLevelClass)) {
                final DetailAST calledMethod =
                        getClassDeclaredMethodByCallSignature(enclosingClass, methodCall);
                if (calledMethod != null) {
                    graph.setFromTo(enclosingMethod, calledMethod);
                }
            }
        }
    }

    protected void processMethodRefCall(final DetailAST methodRef) {
        if (isNestedInsideMethodDef(methodRef)
                && isInsideClassDef(methodRef)
                && isMethodRefToStaticMethodOfClass(methodRef, topLevelClass)) {
            final DetailAST calledMethod =
                    getClassStaticDeclaredMethodByMethodRefCall(topLevelClass, methodRef);
            if (calledMethod != null) {
                final DetailAST caller = getEnclosingMethod(methodRef);
                graph.setFromTo(caller, calledMethod);
            }
        }
    }

    protected void processTopLevelClass(final DetailAST classDef) {
        if (topLevelClass == null) {
            topLevelClass = classDef;
            for (final DetailAST method : getClassDeclaredMethods(classDef)) {
                graph.addMethod(method, getMethodSignature(method));
            }
        }
    }

    @Override
    public void finishTree(DetailAST rootAST) {
        if (writeResult) {
            final String fileName = new File(getFileContents().getFileName()).getName() + ".dot";
            DependencyGraphSerializer.writeToFile(graph, fileName);
        }
    }

    /**
     * Creates textual representation of method signature.
     * <br>
     * Result string contains methodDef name followed by coma-separated
     * parameter types enclosed in parenthesis. Keyword 'final'
     * if present is omitted. If parameter type is generic type it`s
     * type arguments are also omitted.
     *
     * @param methodDef node of type TokenTypes.METHOD_DEF
     * @return signature text
     */
    protected static String getMethodSignature(final DetailAST methodDef) {
        final DetailAST parameters = methodDef.findFirstToken(TokenTypes.PARAMETERS);
        final String parametersText = getNodeChildren(parameters, TokenTypes.PARAMETER_DEF).stream()
                .map(MethodCallDependencyCheck::getMethodParameterDefText)
                .collect(Collectors.joining(","));
        return String.format("%s(%s)", getMethodDefName(methodDef), parametersText);
    }

    protected static String getMethodParameterDefText(final DetailAST parameterDef) {
        final DetailAST type = parameterDef.findFirstToken(TokenTypes.TYPE);
        final DetailAST typeFirstChild = type.getFirstChild();
        String typeName;
        switch (typeFirstChild.getType()) {
            case TokenTypes.IDENT:
                typeName = typeFirstChild.getText();
                break;
            case TokenTypes.DOT:
                typeName = typeFirstChild.getNextSibling().getText();
                break;
            case TokenTypes.ARRAY_DECLARATOR:
                typeName = typeFirstChild.getFirstChild().getText();
                break;
            default:
                if (PRIMITIVE_TOKEN_TYPES.contains(typeFirstChild.getType())) {
                    typeName = typeFirstChild.getText();
                }
                else {
                    throw unexpectedTokenTypeException(typeFirstChild);
                }
        }
        if (typeFirstChild.getType() == TokenTypes.ARRAY_DECLARATOR) {
            typeName += "[]";
        }
        if (parameterDef.findFirstToken(TokenTypes.ELLIPSIS) != null) {
            typeName += "...";
        }
        return typeName;
    }

    /**
     * Get method declared in given class that most likely was called
     * in given method call node.
     * <br>
     * This method returns null if no appropriate method was found,
     * for example if:
     * <ul>
     * <li>super-class method called</li>
     * <li>statically imported method was called</li>
     * <li>given class is nested inside class or interface</li>
     * </ul>
     *
     * @param classNode  class for searching method
     * @param methodCall method call(TokenTypes.METHOD_CALL)
     * @return method of class that most likely was called or null if
     * no appropriate method was found.
     */
    protected static DetailAST getClassDeclaredMethodByCallSignature(
            final DetailAST classNode, final DetailAST methodCall) {
        final List<DetailAST> possibleMethodDefs = getClassDeclaredMethodDefsByName(
                classNode,
                getMethodCallName(methodCall));

        if (possibleMethodDefs.isEmpty()) {
            return null;
        }
        else {
            for (final DetailAST methodDef : possibleMethodDefs) {
                final int methodDefParameterCount = getMethodDefParameterCount(methodDef);
                final int methodCallParameterCount = getMethodCallParameterCount(methodCall);
                if (isVariableArgumentMethodDef(methodDef)) {
                    if (methodCallParameterCount >= methodDefParameterCount - 1) {
                        return methodDef;
                    }
                }
                else if (methodCallParameterCount == methodDefParameterCount) {
                    return methodDef;
                }
            }
            return null;
        }
    }

    protected static DetailAST getClassStaticDeclaredMethodByMethodRefCall(
            final DetailAST classDef, final DetailAST methodRef) {
        final String calledMethodName = getMethodRefMethodName(methodRef);
        final List<DetailAST> possibleMethods =
                getClassDeclaredStaticMethodsByName(classDef, calledMethodName);
        if (possibleMethods.isEmpty()) {
            return null;
        }
        else {
            return possibleMethods.get(0);
        }
    }

    protected static List<DetailAST> getClassDeclaredStaticMethodsByName(
            final DetailAST classDef, final String methodName) {
        return getClassDeclaredMethodDefsByName(classDef, methodName).stream()
                .filter(MethodCallDependencyCheck::isStaticMethodDef)
                .collect(Collectors.toList());
    }

    private static boolean isStaticMethodDef(DetailAST methodDef) {
        return methodDef
                .findFirstToken(TokenTypes.MODIFIERS)
                .findFirstToken(TokenTypes.LITERAL_STATIC) != null;
    }

    protected static String getMethodRefMethodName(DetailAST methodRef) {
        return methodRef.getLastChild().getText();
    }

    protected static boolean isMethodRefToStaticMethodOfClass(
            final DetailAST methodRef, final DetailAST classDef) {
        final String calledClassName = methodRef.getFirstChild().getText();
        final String enclosingClassName = classDef.findFirstToken(TokenTypes.IDENT).getText();
        return calledClassName.equals(enclosingClassName);
    }

    private static boolean isVariableArgumentMethodDef(DetailAST methodDef) {
        final DetailAST parameters = methodDef.findFirstToken(TokenTypes.PARAMETERS);
        final List<DetailAST> parameterDefs =
                getNodeChildren(parameters, TokenTypes.PARAMETER_DEF);
        if (parameterDefs.isEmpty()) {
            return false;
        }
        else {
            final DetailAST lastParameterDef = parameterDefs.get(parameterDefs.size() - 1);
            return lastParameterDef.findFirstToken(TokenTypes.ELLIPSIS) != null;
        }
    }

    protected static List<DetailAST> getClassDeclaredMethodDefsByName(
            final DetailAST classDef, final String methodName) {
        return getClassDeclaredMethods(classDef).stream()
                .filter(methodDef -> getMethodDefName(methodDef).equals(methodName))
                .collect(Collectors.toList());
    }

    protected static List<DetailAST> getClassDeclaredMethods(final DetailAST classDef) {
        return getNodeChildren(classDef.findFirstToken(TokenTypes.OBJBLOCK),
                TokenTypes.METHOD_DEF, TokenTypes.CTOR_DEF);
    }

    protected static String getMethodDefName(final DetailAST methodDef) {
        return methodDef.findFirstToken(TokenTypes.IDENT).getText();
    }

    protected static int getMethodDefParameterCount(final DetailAST methodDef) {
        return methodDef.findFirstToken(TokenTypes.PARAMETERS).getChildCount(
                TokenTypes.PARAMETER_DEF);
    }

    protected static String getMethodCallName(final DetailAST methodCall) {
        final DetailAST methodCallFirstChild = methodCall.getFirstChild();
        switch (methodCallFirstChild.getType()) {
            case TokenTypes.IDENT:
                return methodCallFirstChild.getText();
            case TokenTypes.DOT:
                return methodCallFirstChild.getLastChild().getText();
            default:
                throw unexpectedTokenTypeException(methodCallFirstChild);
        }
    }

    protected static int getMethodCallParameterCount(final DetailAST methodCall) {
        return methodCall.findFirstToken(TokenTypes.ELIST).getChildCount(TokenTypes.EXPR);
    }

    protected static DetailAST getEnclosingMethod(final DetailAST node) {
        return getClosestParentOfTypes(node, TokenTypes.METHOD_DEF, TokenTypes.CTOR_DEF);
    }

    protected static DetailAST getEnclosingClass(final DetailAST node) {
        return getClosestParentOfTypes(node, TokenTypes.CLASS_DEF);
    }

    protected static boolean isThisClassMethodCall(final DetailAST node) {
        final DetailAST firstChild = node.getFirstChild();
        return firstChild.getType() == TokenTypes.IDENT
                || firstChild.getType() == TokenTypes.DOT
                && firstChild.getFirstChild().getType() == TokenTypes.LITERAL_THIS;
    }

    protected static boolean isNestedInsideMethodDef(final DetailAST node) {
        final DetailAST parent = getClosestParentOfTypes(node, TokenTypes.METHOD_DEF,
                TokenTypes.CTOR_DEF, TokenTypes.VARIABLE_DEF);
        switch (parent.getType()) {
            case TokenTypes.METHOD_DEF:
            case TokenTypes.CTOR_DEF:
                return true;
            case TokenTypes.VARIABLE_DEF:
                if (isFieldDeclaration(parent)) {
                    return false;
                }
                else {
                    return isNestedInsideMethodDef(parent);
                }
            default:
                throw unexpectedTokenTypeException(parent);
        }
    }

    protected static boolean isInsideClassDef(final DetailAST node) {
        final DetailAST parent = getClosestParentOfTypes(node, TokenTypes.CLASS_DEF,
                TokenTypes.LITERAL_NEW, TokenTypes.INTERFACE_DEF);
        switch (parent.getType()) {
            case TokenTypes.CLASS_DEF:
                return true;
            case TokenTypes.LITERAL_NEW:
            case TokenTypes.INTERFACE_DEF:
                return false;
            default:
                throw unexpectedTokenTypeException(parent);
        }
    }

    protected static boolean isFieldDeclaration(final DetailAST variableDef) {
        return variableDef.getParent().getType() == TokenTypes.OBJBLOCK;
    }

    /**
     * Get direct children of node with specified types.
     *
     * @param node parent node
     * @param ofTypes child node types
     * @return list of child nodes
     */
    protected static List<DetailAST> getNodeChildren(final DetailAST node,
                                                     final Integer... ofTypes) {
        final List<DetailAST> result = new ArrayList<>();
        for (DetailAST child = node.getFirstChild();
             child != null; child = child.getNextSibling()) {
            if (Arrays.asList(ofTypes).contains(child.getType())) {
                result.add(child);
            }
        }
        return result;
    }

    protected static DetailAST getClosestParentOfTypes(final DetailAST node,
                                                       final Integer... ofTypes) {
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
        throw new RuntimeException(msg);
    }

    protected static RuntimeException unexpectedTokenTypeException(final DetailAST node) {
        return new RuntimeException("Unexpected token " + node + " of type "
                + TokenUtils.getTokenName(node.getType()));
    }
}
