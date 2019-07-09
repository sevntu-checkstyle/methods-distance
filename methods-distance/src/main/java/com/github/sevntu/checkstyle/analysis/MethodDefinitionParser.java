////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2019 the original author or authors.
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

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.github.sevntu.checkstyle.common.UnexpectedTokenTypeException;
import com.github.sevntu.checkstyle.domain.ClassDefinition;
import com.github.sevntu.checkstyle.domain.MethodDefinition;
import com.google.common.collect.ImmutableSet;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public final class MethodDefinitionParser {

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

    private static final String OVERRIDE_ANNOTATION_NAME = "Override";

    private static final String BOOLEAN_OBJECT_TYPE = "Boolean";

    private static final Pattern GETTER_METHOD_REGEX = Pattern.compile("get[A-Z]\\w*");

    private static final Pattern BOOLEAN_GETTER_METHOD_REGEX = Pattern.compile("is[A-Z]\\w*");

    private static final Pattern SETTER_METHOD_REGEX = Pattern.compile("set[A-Z]\\w*");

    private static final Pattern ACCESSOR_METHOD_REGEX = Pattern.compile("(set|get|is)([A-Z]\\w*)");

    private final ClassDefinition classDef;

    private final DetailAST methodDef;

    private MethodDefinitionParser(final ClassDefinition classDef, final DetailAST methodDef) {
        this.classDef = classDef;
        this.methodDef = methodDef;
    }

    public static MethodDefinition parse(ClassDefinition classDef, DetailAST methodDef) {
        final MethodDefinitionParser parser = new MethodDefinitionParser(classDef, methodDef);
        final MethodDefinition.MethodDefinitionBuilder builder = MethodDefinition.builder();
        builder.setClassDefinition(classDef);
        builder.setMethodDef(methodDef);
        builder.setIndex(parser.getIndex());
        builder.setName(parser.getName());
        builder.setSignature(parser.getSignature());
        builder.setAccessibility(parser.getAccessibility());
        builder.setLength(parser.getLength());
        builder.setArgCount(parser.getArgCount());
        final boolean isCtor = parser.isCtor();
        builder.setCtor(isCtor);
        if (!isCtor) {
            builder.setVoidMethod(parser.isVoid());
        }
        builder.setVarArg(parser.isVarArg());
        builder.setStatic(parser.isStatic());
        builder.setOverride(parser.isOverride());
        final boolean isSetter = parser.isSetter();
        builder.setSetter(isSetter);
        final boolean isGetter = parser.isGetter();
        builder.setGetter(isGetter);
        if (isGetter || isSetter) {
            builder.setAccessiblePropertyName(parser.getAccessiblePropertyName());
        }
        return builder.build();
    }

    private int getArgCount() {
        final int result;
        final int parameterCount = methodDef.findFirstToken(TokenTypes.PARAMETERS).getChildCount(
            TokenTypes.PARAMETER_DEF);
        if (isVarArg()) {
            result = parameterCount - 1;
        }
        else {
            result = parameterCount;
        }
        return result;
    }

    private boolean isVarArg() {
        final boolean result;
        final List<DetailAST> parameterDefs = AnalysisUtils.getNodeChildren(
            methodDef.findFirstToken(TokenTypes.PARAMETERS),
            TokenTypes.PARAMETER_DEF);
        if (parameterDefs.isEmpty()) {
            result = false;
        }
        else {
            final DetailAST lastParameterDef = parameterDefs.get(parameterDefs.size() - 1);
            result = lastParameterDef.findFirstToken(TokenTypes.ELLIPSIS) != null;
        }
        return result;
    }

    private String getName() {
        return methodDef.findFirstToken(TokenTypes.IDENT).getText();
    }

    private String getSignature() {
        final DetailAST parameters = methodDef.findFirstToken(TokenTypes.PARAMETERS);
        final String parametersText =
            AnalysisUtils.getNodeChildren(parameters, TokenTypes.PARAMETER_DEF).stream()
                .map(this::getMethodParameterDefText)
                .collect(Collectors.joining(","));
        return String.format("%s(%s)", getName(), parametersText);
    }

    private String getMethodParameterDefText(DetailAST parameterDef) {
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
                    throw new UnexpectedTokenTypeException(typeFirstChild);
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

    public boolean isStatic() {
        return isMethodDefHasModifier(methodDef, TokenTypes.LITERAL_STATIC);
    }

    private static boolean isMethodDefHasModifier(DetailAST methodDef, int modifierTokenType) {
        return methodDef.findFirstToken(TokenTypes.MODIFIERS)
            .findFirstToken(modifierTokenType) != null;
    }

    private MethodDefinition.Accessibility getAccessibility() {
        final MethodDefinition.Accessibility result;
        if (isMethodDefHasModifier(methodDef, TokenTypes.LITERAL_PUBLIC)) {
            result = MethodDefinition.Accessibility.PUBLIC;
        }
        else if (isMethodDefHasModifier(methodDef, TokenTypes.LITERAL_PROTECTED)) {
            result = MethodDefinition.Accessibility.PROTECTED;
        }
        else if (isMethodDefHasModifier(methodDef, TokenTypes.LITERAL_PRIVATE)) {
            result = MethodDefinition.Accessibility.PRIVATE;
        }
        else {
            result = MethodDefinition.Accessibility.DEFAULT;
        }
        return result;
    }

    private boolean isOverride() {
        final DetailAST modifiers = methodDef.findFirstToken(TokenTypes.MODIFIERS);
        final List<DetailAST> annotations =
            AnalysisUtils.getNodeChildren(modifiers, TokenTypes.ANNOTATION);
        return annotations.stream()
            .anyMatch(annotation -> OVERRIDE_ANNOTATION_NAME.equals(
                annotation.findFirstToken(TokenTypes.IDENT).getText()));
    }

    private int getIndex() {
        return classDef.getMethodDefsAstNodes().indexOf(methodDef);
    }

    private String getAccessiblePropertyName() {
        if (isGetter() || isSetter()) {
            final Matcher matcher = ACCESSOR_METHOD_REGEX.matcher(getName());
            if (matcher.matches()) {
                final String methodNamePart = matcher.group(2);
                return methodNamePart.substring(0, 1).toLowerCase() + methodNamePart.substring(1);
            }
            else {
                throw new IllegalArgumentException("Property accessor name does not matches regex");
            }
        }
        else {
            throw new IllegalStateException("The method " + getName() + " is not accessor");
        }
    }

    private boolean isSetter() {
        return getAccessibility().equals(MethodDefinition.Accessibility.PUBLIC)
            && !isCtor()
            && getArgCount() == 1
            && !isVarArg()
            && isVoid()
            && SETTER_METHOD_REGEX.matcher(getName()).matches();
    }

    private boolean isGetter() {
        return getAccessibility().equals(MethodDefinition.Accessibility.PUBLIC)
            && !isCtor()
            && getArgCount() == 0
            && !isVoid()
            && (GETTER_METHOD_REGEX.matcher(getName()).matches()
            || BOOLEAN_GETTER_METHOD_REGEX.matcher(getName()).matches() && isReturnsBoolean());
    }

    private boolean isCtor() {
        return methodDef.getType() == TokenTypes.CTOR_DEF;
    }

    private boolean isVoid() {
        return getReturnType(methodDef).getType() == TokenTypes.LITERAL_VOID;
    }

    private boolean isReturnsBoolean() {
        final DetailAST returnType = getReturnType(methodDef);
        return returnType.getType() == TokenTypes.LITERAL_BOOLEAN
            || returnType.getText().equals(BOOLEAN_OBJECT_TYPE);
    }

    private static DetailAST getReturnType(DetailAST methodDef) {
        return methodDef.findFirstToken(TokenTypes.TYPE).getFirstChild();
    }

    private int getLength() {
        final DetailAST rightCurly = methodDef.findFirstToken(TokenTypes.SLIST)
            .findFirstToken(TokenTypes.RCURLY);
        return rightCurly.getLineNo() - methodDef.getLineNo();
    }
}
