package org.pirat9600q.graph;

import com.google.common.collect.ImmutableSet;
import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import static com.puppycrawl.tools.checkstyle.api.TokenTypes.*;
import com.puppycrawl.tools.checkstyle.utils.TokenUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MethodCallDependencyCheck extends Check {

    private static final Set<Integer> PRIMITIVE_TOKEN_TYPES = ImmutableSet.of(
            LITERAL_VOID,
            LITERAL_BOOLEAN,
            LITERAL_CHAR,
            LITERAL_BYTE,
            LITERAL_SHORT,
            LITERAL_INT,
            LITERAL_LONG,
            LITERAL_DOUBLE
    );

    private DependencyGraph graph;

    private DetailAST topLevelClass;

    private boolean writeResult = false;

    public DependencyGraph getGraph() {
        return graph;
    }

    public void setWriteResult(final boolean wr) {
        writeResult = wr;
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[]{METHOD_CALL, METHOD_REF, CLASS_DEF};
    }

    @Override
    public void beginTree(DetailAST rootAST) {
        graph = new DependencyGraph();
        topLevelClass = null;
    }

    @Override
    public void visitToken(DetailAST ast) {
        switch (ast.getType()) {
            case METHOD_CALL: {
                if(isNestedInsideMethodDef(ast) && isInsideClassDef(ast) && isThisClassMethodCall(ast)) {
                    final DetailAST enclosingMethod = getEnclosingMethod(ast);
                    final DetailAST enclosingClass = getEnclosingClass(ast);
                    if(enclosingClass.equals(topLevelClass)) {
                        final DetailAST calledMethod = getClassDeclaredMethodByCallSignature(enclosingClass, ast);
                        if(calledMethod != null) {
                            graph.setFromTo(enclosingMethod, calledMethod);
                        }
                    }
                }
            }
            break;
            case METHOD_REF: {
                if(isNestedInsideMethodDef(ast) &&
                    isInsideClassDef(ast) &&
                    isMethodRefToStaticMethodOfClass(ast, topLevelClass))
                {
                    final DetailAST calledMethod =
                            getClassStaticDeclaredMethodByMethodRefCall(topLevelClass, ast);
                    if(calledMethod != null) {
                        final DetailAST caller = getEnclosingMethod(ast);
                        graph.setFromTo(caller, calledMethod);
                    }
                }
            }
            break;
            case CLASS_DEF: {
                if(topLevelClass == null) {
                    topLevelClass = ast;
                    for(final DetailAST method : getClassDeclaredMethods(ast)) {
                        graph.addMethod(method, getMethodSignature(method));
                    }
                }
            }
            break;
            default:
                throw unexpectedTokenTypeException(ast);
        }
    }

    @Override
    public void finishTree(DetailAST rootAST) {
        if(writeResult) {
            final String fileName = new File(getFileContents().getFileName()).getName() + ".dot";
            DependencyGraphSerializer.writeToFile(graph, fileName);
        }
    }

    /**
     * Creates textual representation of method signature.
     *
     * Result string contains methodDef name followed by coma-separated
     * parameter types enclosed in parenthesis. Keyword 'final'
     * if present is omitted. If parameter type is generic type it`s
     * type arguments are also omitted.
     * @param methodDef node of type TokenTypes.METHOD_DEF
     * @return signature text
     */
    protected static String getMethodSignature(final DetailAST methodDef) {
        final DetailAST parameters = methodDef.findFirstToken(PARAMETERS);
        final String parametersText = getNodeChildren(parameters, PARAMETER_DEF).stream()
                .map(MethodCallDependencyCheck::getMethodParameterDefText)
                .collect(Collectors.joining(","));
        return String.format("%s(%s)", getMethodDefName(methodDef), parametersText);
    }

    protected static String getMethodParameterDefText(final DetailAST parameterDef) {
        final DetailAST type = parameterDef.findFirstToken(TYPE);
        final DetailAST typeFirstChild = type.getFirstChild();
        String typeName;
        switch (typeFirstChild.getType()) {
            case IDENT:
                typeName = typeFirstChild.getText();
                break;
            case DOT:
                typeName = typeFirstChild.getNextSibling().getText();
                break;
            case ARRAY_DECLARATOR:
                typeName = typeFirstChild.getFirstChild().getText();
                break;
            default:
                if(PRIMITIVE_TOKEN_TYPES.contains(typeFirstChild.getType())) {
                    typeName = typeFirstChild.getText();
                }
                else {
                    throw unexpectedTokenTypeException(typeFirstChild);
                }
        }
        if(typeFirstChild.getType() == ARRAY_DECLARATOR) {
            typeName += "[]";
        }
        if(parameterDef.findFirstToken(ELLIPSIS) != null) {
            typeName += "...";
        }
        return typeName;
    }

    /**
     * Get method declared in given class that most likely was called
     * in given method call node.
     *
     * This method returns null if no appropriate method was found,
     * for example if:
     * <ul>
     *   <li>super-class method called</li>
     *   <li>statically imported method was called</li>
     *   <li>given class is nested inside class or interface</li>
     * </ul>
     *
     *
     * @param classNode class for searching method
     * @param methodCall method call(TokenTypes.METHOD_CALL)
     * @return method of class that most likely was called or null if
     * no appropriate method was found.
     */
    protected static DetailAST getClassDeclaredMethodByCallSignature(
            final DetailAST classNode, final DetailAST methodCall)
    {
        final List<DetailAST> possibleMethodDefs = getClassDeclaredMethodDefsByName(
                classNode,
                getMethodCallName(methodCall));

        if(possibleMethodDefs.isEmpty()) {
            return null;
        }
        else {
            for(final DetailAST methodDef : possibleMethodDefs) {
                final int methodDefParameterCount = getMethodDefParameterCount(methodDef);
                final int methodCallParameterCount = getMethodCallParameterCount(methodCall);
                if(isVariableArgumentMethodDef(methodDef)) {
                    if(methodCallParameterCount >= methodDefParameterCount - 1) {
                        return methodDef;
                    }
                }
                else if(methodCallParameterCount == methodDefParameterCount) {
                    return methodDef;
                }
            }
            return null;
        }
    }

    protected static DetailAST getClassStaticDeclaredMethodByMethodRefCall(final DetailAST classDef, final DetailAST methodRef) {
        final String calledMethodName = getMethodRefMethodName(methodRef);
        final List<DetailAST> possibleMethods = getClassDeclaredStaticMethodsByName(classDef, calledMethodName);
        if(possibleMethods.isEmpty()) {
            return null;
        }
        else {
            return possibleMethods.get(0);
        }
    }

    protected static List<DetailAST> getClassDeclaredStaticMethodsByName(final DetailAST classDef, final String methodName) {
        return getClassDeclaredMethodDefsByName(classDef, methodName).stream()
                .filter(MethodCallDependencyCheck::isStaticMethodDef)
                .collect(Collectors.toList());
    }

    private static boolean isStaticMethodDef(DetailAST methodDef) {
        return methodDef.findFirstToken(MODIFIERS).findFirstToken(LITERAL_STATIC) != null;
    }

    protected static String getMethodRefMethodName(DetailAST methodRef) {
        return methodRef.getLastChild().getText();
    }

    protected static boolean isMethodRefToStaticMethodOfClass(final DetailAST methodRef, final DetailAST classDef) {
        final String calledClassName = methodRef.getFirstChild().getText();
        final String enclosingClassName = classDef.findFirstToken(IDENT).getText();
        return calledClassName.equals(enclosingClassName);
    }

    private static boolean isVariableArgumentMethodDef(DetailAST methodDef) {
        final DetailAST parameters = methodDef.findFirstToken(PARAMETERS);
        final List<DetailAST> parameterDefs = getNodeChildren(parameters, PARAMETER_DEF);
        if(parameterDefs.isEmpty()) {
            return false;
        }
        else {
            final DetailAST lastParameterDef = parameterDefs.get(parameterDefs.size() - 1);
            return lastParameterDef.findFirstToken(ELLIPSIS) != null;
        }
    }

    protected static List<DetailAST> getClassDeclaredMethodDefsByName(
            final DetailAST classDef, final String methodName)
    {
        return getClassDeclaredMethods(classDef).stream()
                .filter(methodDef -> getMethodDefName(methodDef).equals(methodName))
                .collect(Collectors.toList());
    }

    protected static List<DetailAST> getClassDeclaredMethods(final DetailAST classDef) {
        return getNodeChildren(classDef.findFirstToken(OBJBLOCK), METHOD_DEF, CTOR_DEF);
    }

    protected static String getMethodDefName(final DetailAST methodDef) {
        return methodDef.findFirstToken(IDENT).getText();
    }

    protected static int getMethodDefParameterCount(final DetailAST methodDef) {
        return methodDef.findFirstToken(PARAMETERS).getChildCount(PARAMETER_DEF);
    }

    protected static String getMethodCallName(final DetailAST methodCall) {
        final DetailAST methodCallFirstChild = methodCall.getFirstChild();
        switch(methodCallFirstChild.getType()) {
            case IDENT:
                return methodCallFirstChild.getText();
            case DOT:
                return methodCallFirstChild.getLastChild().getText();
            default:
                throw unexpectedTokenTypeException(methodCallFirstChild);
        }
    }

    protected static int getMethodCallParameterCount(final DetailAST methodCall) {
        return methodCall.findFirstToken(ELIST).getChildCount(EXPR);
    }

    protected static DetailAST getEnclosingMethod(final DetailAST node) {
        return getClosestParentOfTypes(node, METHOD_DEF, CTOR_DEF);
    }

    protected static DetailAST getEnclosingClass(final DetailAST node) {
        return getClosestParentOfTypes(node, CLASS_DEF);
    }

    protected static boolean isThisClassMethodCall(final DetailAST node) {
        final DetailAST firstChild = node.getFirstChild();
        return firstChild.getType() == IDENT ||
                firstChild.getType() == DOT && firstChild.getFirstChild().getType() == LITERAL_THIS;
    }

    protected static boolean isNestedInsideMethodDef(final DetailAST node) {
        final DetailAST parent = getClosestParentOfTypes(node, METHOD_DEF, CTOR_DEF, VARIABLE_DEF);
        switch (parent.getType()) {
            case METHOD_DEF:
            case CTOR_DEF:
                return true;
            case VARIABLE_DEF:
                if(isFieldDeclaration(parent)) {
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
        final DetailAST parent = getClosestParentOfTypes(node, CLASS_DEF, LITERAL_NEW, INTERFACE_DEF);
        switch (parent.getType()) {
            case CLASS_DEF:
                return true;
            case LITERAL_NEW:
            case INTERFACE_DEF:
                return false;
            default:
                throw unexpectedTokenTypeException(parent);
        }
    }

    protected static boolean isFieldDeclaration(final DetailAST variableDef) {
        return variableDef.getParent().getType() == OBJBLOCK;
    }

    /**
     * Get direct children of node with specified types.
     *
     * @param node
     * @param ofTypes
     * @return
     */
    protected static List<DetailAST> getNodeChildren(final DetailAST node, final Integer... ofTypes) {
        final List<DetailAST> result = new ArrayList<>();
        for(DetailAST child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
            if(Arrays.asList(ofTypes).contains(child.getType())) {
                result.add(child);
            }
        }
        return result;
    }

    protected static DetailAST getClosestParentOfTypes(final DetailAST node, final Integer... ofTypes) {
        for(DetailAST parent = node.getParent(); parent != null; parent = parent.getParent()) {
            if(Arrays.asList(ofTypes).contains(parent.getType())) {
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
