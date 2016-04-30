package org.pirat9600q.analysis;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClassDefinition extends AnalysisSubject {

    private final DetailAST classDef;

    private List<MethodDefinition> methods;

    public ClassDefinition(final DetailAST classDef) {
        this.classDef = classDef;
        methods = getDeclaredMethods(this, classDef);
    }

    private static List<MethodDefinition> getDeclaredMethods(
            final ClassDefinition definition, final DetailAST classDef) {
        return getMethodDefsAstNodes(classDef).stream()
                .map(methodDef -> new MethodDefinition(definition, methodDef))
                .collect(Collectors.toList());
    }

    public List<DetailAST> getMethodDefsAstNodes() {
        return getMethodDefsAstNodes(classDef);
    }

    private static List<DetailAST> getMethodDefsAstNodes(final DetailAST classDef) {
        return getNodeChildren(classDef.findFirstToken(TokenTypes.OBJBLOCK),
                TokenTypes.METHOD_DEF, TokenTypes.CTOR_DEF);
    }

    public boolean isInsideMethodOfClass(final DetailAST node) {
        return isNestedInsideMethodDef(node) && isInsideClassDef(node)
                && getEnclosingClass(node) == classDef;
    }

    public DetailAST getAstNode() {
        return classDef;
    }

    public List<MethodDefinition> getMethods() {
        return methods;
    }

    public String getClassName() {
        return classDef.findFirstToken(TokenTypes.IDENT).getText();
    }

    public MethodDefinition getMethodByIndex(final int index) {
        return methods.stream()
                .filter(method -> method.getIndex() == index)
                .findFirst()
                .get();
    }

    public MethodDefinition getMethodByAstNode(final DetailAST methodDef) {
        return methods.stream()
                .filter(method -> method.getAstNode().getLineNo() == methodDef.getLineNo()
                        && method.getAstNode().getColumnNo() == methodDef.getColumnNo())
                .findFirst()
                .orElse(null);
    }

    public List<MethodDefinition> getMethodsByName(final String methodName) {
        return methods.stream()
                .filter(methodDef -> methodDef.getName().equals(methodName))
                .collect(Collectors.toList());
    }

    public List<MethodDefinition> getStaticMethodsByName(final String methodName) {
        return getMethodsByName(methodName).stream()
                .filter(MethodDefinition::isStatic)
                .collect(Collectors.toList());
    }

    public List<MethodDefinition> getInstanceMethodsByName(final String methodName) {
        return getMethodsByName(methodName).stream()
                .filter(MethodDefinition::isInstance)
                .collect(Collectors.toList());
    }

    public Map<String, List<MethodDefinition>> getPropertiesAccessors() {
        return getMethods().stream()
                .filter(method -> method.isGetter() || method.isSetter())
                .collect(Collectors.groupingBy(MethodDefinition::getAccessiblePropertyName));
    }
}
