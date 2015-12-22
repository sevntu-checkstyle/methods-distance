package org.pirat9600q.graph;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import static com.puppycrawl.tools.checkstyle.api.TokenTypes.*;
import com.puppycrawl.tools.checkstyle.utils.TokenUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InputTextStatistics extends Check {

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
        return new int[]{METHOD_CALL, CLASS_DEF};
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
                throw unexpectedTokenTypeException(ast.getType());
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
        final List<String> parameterList = new ArrayList<>();
        final DetailAST parameters = methodDef.findFirstToken(PARAMETERS);
        for(final DetailAST parameterDef : getNodeChildren(parameters, PARAMETER_DEF)) {
            final DetailAST type = parameterDef.findFirstToken(TYPE);
            final int typeFirstChildType = type.getFirstChild().getType();
            final String typeName;
            switch (typeFirstChildType) {
                case IDENT:
                    typeName = type.getFirstChild().getText();
                    break;
                case DOT:
                    typeName = type.getFirstChild().getNextSibling().getText();
                    break;
                default:
                    throw unexpectedTokenTypeException(typeFirstChildType);
            }
            parameterList.add(typeName);
        }
        final String params = parameterList.stream().collect(Collectors.joining(","));
        return String.format("%s(%s)", getMethodDefName(methodDef), params);
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
        final List<DetailAST> possibleMethodDefs = getClassMethodDefsByNameAndParameterCount(
                classNode,
                getMethodCallName(methodCall),
                getMethodCallParameterCount(methodCall));
        if(possibleMethodDefs.size() > 1) {
            //TODO if we have multiple methods with specified name and parameter count
            // use some other signs to make more accurate gues.
            // Currently we will just take first one
            return possibleMethodDefs.get(0);
        }
        else if(possibleMethodDefs.size() == 1) {
            return possibleMethodDefs.get(0);
        }
        else {
            return null;
        }
    }

    protected static List<DetailAST> getClassMethodDefsByNameAndParameterCount(
            final DetailAST classDef, final String methodName, final int parameterCount)
    {
        return getClassDeclaredMethods(classDef).stream()
                .filter(methodDef -> getMethodDefName(methodDef).equals(methodName))
                .filter(methodDef -> getMethodDefParameterCount(methodDef) == parameterCount)
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
                throw unexpectedTokenTypeException(methodCallFirstChild.getType());
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
        final DetailAST parent = getClosestParentOfTypes(node, METHOD_DEF, CTOR_DEF, VARIABLE_DEF, LAMBDA);
        switch (parent.getType()) {
            case METHOD_DEF:
            case CTOR_DEF:
                return true;
            case LAMBDA:
                return false;
            case VARIABLE_DEF:
                if(isFieldDeclaration(parent)) {
                    return false;
                }
                else {
                    return isNestedInsideMethodDef(parent);
                }
            default:
                throw unexpectedTokenTypeException(parent.getType());
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
                throw unexpectedTokenTypeException(parent.getType());
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

    protected static boolean isNestedInNodeOfTypes(final DetailAST node, final Integer... ofTypes) {
        for(DetailAST parent = node.getParent();; parent = parent.getParent()) {
            if(parent == null) {
                return false;
            }
            else if(Arrays.asList(ofTypes).contains(parent.getType())) {
                return true;
            }
        }
    }

    protected static RuntimeException unexpectedTokenTypeException(final int type) {
        return new RuntimeException("Unexpected token of type " + TokenUtils.getTokenName(type));
    }
}
