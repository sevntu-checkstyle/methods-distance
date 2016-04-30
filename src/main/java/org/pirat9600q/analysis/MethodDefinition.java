package org.pirat9600q.analysis;

import com.google.common.collect.ImmutableSet;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MethodDefinition extends AnalysisSubject {

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

    private static final Pattern GETTER_METHOD_REGEX = Pattern.compile("get[A-Z]\\w*");

    private static final Pattern BOOLEAN_GETTER_METHOD_REGEX = Pattern.compile("is[A-Z]\\w*");

    private static final Pattern SETTER_METHOD_REGEX = Pattern.compile("set[A-Z]\\w*");

    private static final Pattern ACCESSOR_METHOD_REGEX = Pattern.compile("(set|get|is)([A-Z]\\w*)");

    private final ClassDefinition classDefinition;

    private final DetailAST methodDef;

    public MethodDefinition(final ClassDefinition classDefinition, final DetailAST methodDef) {
        this.classDefinition = classDefinition;
        this.methodDef = methodDef;
    }

    public DetailAST getAstNode() {
        return methodDef;
    }

    public ClassDefinition getClassDefinition() {
        return classDefinition;
    }

    public int getLineNo() {
        return methodDef.getLineNo();
    }

    public int getArgCount() {
        final int parameterCount = methodDef.findFirstToken(TokenTypes.PARAMETERS).getChildCount(
                TokenTypes.PARAMETER_DEF);
        return isVarArg() ? parameterCount - 1 : parameterCount;
    }

    public String getName() {
        return methodDef.findFirstToken(TokenTypes.IDENT).getText();
    }

    public boolean isVarArg() {
        final List<DetailAST> parameterDefs = getNodeChildren(
                methodDef.findFirstToken(TokenTypes.PARAMETERS),
                TokenTypes.PARAMETER_DEF);
        if (parameterDefs.isEmpty()) {
            return false;
        }
        else {
            final DetailAST lastParameterDef = parameterDefs.get(parameterDefs.size() - 1);
            return lastParameterDef.findFirstToken(TokenTypes.ELLIPSIS) != null;
        }
    }

    public boolean isStatic() {
        return isMethodDefHasModifier(methodDef, TokenTypes.LITERAL_STATIC);
    }

    public boolean isInstance() {
        return !isStatic();
    }

    private static boolean isMethodDefHasModifier(
            final DetailAST methodDef, final int modifierTokenType) {
        return methodDef.findFirstToken(TokenTypes.MODIFIERS)
                .findFirstToken(modifierTokenType) != null;
    }

    /**
     * Creates textual representation of method signature.
     * <br>
     * Result string contains methodDef name followed by coma-separated
     * parameter types enclosed in parenthesis. Keyword 'final'
     * if present is omitted. If parameter type is generic type it`s
     * type arguments are also omitted.
     *
     * @return signature text
     */
    public String getSignature() {
        final DetailAST parameters = methodDef.findFirstToken(TokenTypes.PARAMETERS);
        final String parametersText = getNodeChildren(parameters, TokenTypes.PARAMETER_DEF).stream()
                .map(MethodDefinition::getMethodParameterDefText)
                .collect(Collectors.joining(","));
        return String.format("%s(%s)", getName(), parametersText);
    }

    public Accessibility getAccessibility() {
        if (isMethodDefHasModifier(methodDef, TokenTypes.LITERAL_PUBLIC)) {
            return Accessibility.PUBLIC;
        }
        else if (isMethodDefHasModifier(methodDef, TokenTypes.LITERAL_PROTECTED)) {
            return Accessibility.PROTECTED;
        }
        else if (isMethodDefHasModifier(methodDef, TokenTypes.LITERAL_PRIVATE)) {
            return Accessibility.PRIVATE;
        }
        else {
            return Accessibility.DEFAULT;
        }
    }

    public boolean isOverride() {
        final DetailAST modifiers = methodDef.findFirstToken(TokenTypes.MODIFIERS);
        final List<DetailAST> annotations = getNodeChildren(modifiers, TokenTypes.ANNOTATION);
        return annotations.stream()
                .anyMatch(annotation ->
                        "Override".equals(annotation.findFirstToken(TokenTypes.IDENT).getText()));
    }

    public boolean isOverloaded() {
        return classDefinition.getMethodsByName(getName()).size() > 1;
    }

    public int getIndex() {
        return classDefinition.getMethodDefsAstNodes().indexOf(methodDef);
    }

    private static String getMethodParameterDefText(final DetailAST parameterDef) {
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

    public int getIndexDistanceTo(final MethodDefinition other) {
        return other.getIndex() - getIndex();
    }

    public int getLineDistanceTo(final MethodDefinition other) {
        return other.getLineNo() - getLineNo();
    }

    public String getAccessiblePropertyName() {
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

    public boolean isSetter() {
        return getAccessibility().equals(Accessibility.PUBLIC)
            && !isCtor()
            && getArgCount() == 1
            && !isVarArg()
            && isVoid()
            && SETTER_METHOD_REGEX.matcher(getName()).matches();
    }

    public boolean isGetter() {
        return getAccessibility().equals(Accessibility.PUBLIC)
            && !isCtor()
            && getArgCount() == 0
            && !isVoid()
            && (GETTER_METHOD_REGEX.matcher(getName()).matches()
                || BOOLEAN_GETTER_METHOD_REGEX.matcher(getName()).matches() && isReturnsBoolean());
    }

    public boolean isCtor() {
        return methodDef.getType() == TokenTypes.CTOR_DEF;
    }

    public boolean isVoid() {
        return getReturnType(methodDef).getType() == TokenTypes.LITERAL_VOID;
    }

    private boolean isReturnsBoolean() {
        final DetailAST returnType = getReturnType(methodDef);
        return returnType.getType() == TokenTypes.LITERAL_BOOLEAN
            || returnType.getText().equals("Boolean");
    }

    private static DetailAST getReturnType(final DetailAST methodDef) {
        return methodDef.findFirstToken(TokenTypes.TYPE).getFirstChild();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        else if (o == this) {
            return true;
        }
        else {
            final MethodDefinition rhs = (MethodDefinition) o;
            return methodDef.getLineNo() == rhs.methodDef.getLineNo()
                && methodDef.getColumnNo() == rhs.methodDef.getColumnNo();
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(methodDef.getLineNo())
                .append(methodDef.getColumnNo())
                .toHashCode();
    }

    public enum Accessibility {
        PUBLIC,
        PROTECTED,
        DEFAULT,
        PRIVATE
    }
}
