package com.github.sevntu.checkstyle.domain;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import org.apache.commons.lang.builder.HashCodeBuilder;

// This class is data-heavy
// CSOFF: MethodCount
@SuppressWarnings("PMD.TooManyFields")
public class MethodDefinition {

    private ClassDefinition classDefinition;

    private DetailAST methodDef;

    private int index;

    private String name;

    private String signature;

    private Accessibility accessibility;

    private int length;

    private int argCount;

    private boolean isCtorMethod;

    private boolean isVoidMethod;

    private boolean isVarArgMethod;

    private boolean isStaticMethod;

    private boolean isOverrideMethod;

    private boolean isSetterMethod;

    private boolean isGetterMethod;

    private String accessiblePropertyName;

    MethodDefinition() {
        //This constructor is intended to restrict access to package level
    }

    public DetailAST getAstNode() {
        return methodDef;
    }

    public int getLineNo() {
        return methodDef.getLineNo();
    }

    public boolean isInstance() {
        return !isStatic();
    }

    public ClassDefinition getClassDefinition() {
        return classDefinition;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getSignature() {
        return signature;
    }

    public Accessibility getAccessibility() {
        return accessibility;
    }

    public int getLength() {
        return length;
    }

    public int getArgCount() {
        return argCount;
    }

    public boolean isCtor() {
        return isCtorMethod;
    }

    public boolean isVoid() {
        return isVoidMethod;
    }

    public boolean isVarArg() {
        return isVarArgMethod;
    }

    public boolean isStatic() {
        return isStaticMethod;
    }

    public boolean isOverride() {
        return isOverrideMethod;
    }

    public boolean isOverloaded() {
        return classDefinition.getMethodsByName(getName()).size() > 1;
    }

    public boolean isSetter() {
        return isSetterMethod;
    }

    public boolean isGetter() {
        return isGetterMethod;
    }

    public String getAccessiblePropertyName() {
        return accessiblePropertyName;
    }

    public int getIndexDistanceTo(MethodDefinition other) {
        return other.getIndex() - getIndex();
    }

    public int getLineDistanceTo(MethodDefinition other) {
        return other.getLineNo() - getLineNo();
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

    void setClassDefinition(final ClassDefinition classDefinition) {
        this.classDefinition = classDefinition;
    }

    void setMethodDef(final DetailAST methodDef) {
        this.methodDef = methodDef;
    }

    void setArgCount(final int argCount) {
        this.argCount = argCount;
    }

    void setName(final String name) {
        this.name = name;
    }

    void setVarArg(final boolean varArg) {
        this.isVarArgMethod = varArg;
    }

    void setStatic(final boolean staticMethod) {
        isStaticMethod = staticMethod;
    }

    void setSignature(final String signature) {
        this.signature = signature;
    }

    void setAccessibility(
        final Accessibility accessibility) {
        this.accessibility = accessibility;
    }

    void setOverride(final boolean overrideMethod) {
        isOverrideMethod = overrideMethod;
    }

    void setIndex(final int index) {
        this.index = index;
    }

    void setAccessiblePropertyName(final String accessiblePropertyName) {
        this.accessiblePropertyName = accessiblePropertyName;
    }

    void setSetter(final boolean setterMethod) {
        isSetterMethod = setterMethod;
    }

    void setGetter(final boolean getterMethod) {
        isGetterMethod = getterMethod;
    }

    void setCtor(final boolean ctorMethod) {
        isCtorMethod = ctorMethod;
    }

    void setVoidMethod(final boolean voidMethod) {
        isVoidMethod = voidMethod;
    }

    void setLength(final int length) {
        this.length = length;
    }
}
