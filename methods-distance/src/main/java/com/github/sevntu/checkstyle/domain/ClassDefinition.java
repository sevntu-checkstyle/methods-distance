package com.github.sevntu.checkstyle.domain;

import com.github.sevntu.checkstyle.analysis.AnalysisUtils;
import com.github.sevntu.checkstyle.analysis.MethodDefinitionParser;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClassDefinition {

    private final DetailAST classDef;

    private final String className;

    private final List<MethodDefinition> methods;

    private final List<DetailAST> methodDefs;

    private final Map<String, List<MethodDefinition>> propertyAccessors;

    public ClassDefinition(DetailAST classDef) {
        this.classDef = classDef;
        this.className = getClassNameImpl(classDef);
        this.methodDefs = getMethodDefsAstNodesImpl(classDef);
        this.methods = getDeclaredMethods();
        this.propertyAccessors = getPropertiesAccessorsImpl(this.methods);
    }

    private static String getClassNameImpl(DetailAST classDef) {
        return classDef.findFirstToken(TokenTypes.IDENT).getText();
    }

    private static List<DetailAST> getMethodDefsAstNodesImpl(DetailAST classDef) {
        return AnalysisUtils.getNodeChildren(classDef.findFirstToken(TokenTypes.OBJBLOCK),
            TokenTypes.METHOD_DEF, TokenTypes.CTOR_DEF);
    }

    private List<MethodDefinition> getDeclaredMethods() {
        return methodDefs.stream()
                .map(methodDef -> MethodDefinitionParser.parse(this, methodDef))
                .collect(Collectors.toList());
    }

    private static Map<String, List<MethodDefinition>> getPropertiesAccessorsImpl(
        List<MethodDefinition> methods) {

        return methods.stream()
            .filter(method -> method.isGetter() || method.isSetter())
            .collect(Collectors.groupingBy(MethodDefinition::getAccessiblePropertyName));
    }

    public String getClassName() {
        return className;
    }

    public List<DetailAST> getMethodDefsAstNodes() {
        return methodDefs;
    }

    public boolean isInsideMethodOfClass(DetailAST node) {
        return AnalysisUtils.isNestedInsideMethodDef(node)
            && AnalysisUtils.isInsideClassDef(node)
            && AnalysisUtils.getEnclosingClass(node) == classDef;
    }

    public DetailAST getAstNode() {
        return classDef;
    }

    public List<MethodDefinition> getMethods() {
        return methods;
    }

    public MethodDefinition getMethodByIndex(int index) {
        return methods.get(index);
    }

    public MethodDefinition getMethodByAstNode(DetailAST methodDef) {
        return methods.stream()
                .filter(method -> method.getAstNode().getLineNo() == methodDef.getLineNo()
                        && method.getAstNode().getColumnNo() == methodDef.getColumnNo())
                .findFirst()
                .orElse(null);
    }

    public List<MethodDefinition> getMethodsByName(String methodName) {
        return methods.stream()
                .filter(methodDef -> methodDef.getName().equals(methodName))
                .collect(Collectors.toList());
    }

    public List<MethodDefinition> getStaticMethodsByName(String methodName) {
        return methods.stream()
                .filter(methodDef -> methodDef.getName().equals(methodName))
                .filter(MethodDefinition::isStatic)
                .collect(Collectors.toList());
    }

    public List<MethodDefinition> getInstanceMethodsByName(String methodName) {
        return methods.stream()
                .filter(methodDef -> methodDef.getName().equals(methodName))
                .filter(MethodDefinition::isInstance)
                .collect(Collectors.toList());
    }

    public Map<String, List<MethodDefinition>> getPropertiesAccessors() {
        return propertyAccessors;
    }
}
