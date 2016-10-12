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

    private MethodDefinition() {
        //This constructor is intended to prevent instantiation from outside of class
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

    public static MethodDefinitionBuilder builder() {
        return new MethodDefinitionBuilder();
    }

    public static final class MethodDefinitionBuilder {

        private MethodDefinition instance = new MethodDefinition();

        public void setClassDefinition(final ClassDefinition classDefinition) {
            instance.classDefinition = classDefinition;
        }

        public void setMethodDef(final DetailAST methodDef) {
            instance.methodDef = methodDef;
        }

        public void setArgCount(final int argCount) {
            instance.argCount = argCount;
        }

        public void setName(final String name) {
            instance.name = name;
        }

        public void setVarArg(final boolean varArg) {
            instance.isVarArgMethod = varArg;
        }

        public void setStatic(final boolean staticMethod) {
            instance.isStaticMethod = staticMethod;
        }

        public void setSignature(final String signature) {
            instance.signature = signature;
        }

        public void setAccessibility(final Accessibility accessibility) {
            instance.accessibility = accessibility;
        }

        public void setOverride(final boolean overrideMethod) {
            instance.isOverrideMethod = overrideMethod;
        }

        public void setIndex(final int index) {
            instance.index = index;
        }

        public void setAccessiblePropertyName(final String accessiblePropertyName) {
            instance.accessiblePropertyName = accessiblePropertyName;
        }

        public void setSetter(final boolean setterMethod) {
            instance.isSetterMethod = setterMethod;
        }

        public void setGetter(final boolean getterMethod) {
            instance.isGetterMethod = getterMethod;
        }

        public void setCtor(final boolean ctorMethod) {
            instance.isCtorMethod = ctorMethod;
        }

        public void setVoidMethod(final boolean isVoidMethod) {
            instance.isVoidMethod = isVoidMethod;
        }

        public void setLength(final int length) {
            instance.length = length;
        }

        public MethodDefinition build() {
            return instance;
        }
    }
}
