package com.github.sevntu.checkstyle.ordering;

import com.github.sevntu.checkstyle.domain.MethodDefinition;

/* This class is data-heavy. */
@SuppressWarnings("PMD.TooManyFields")
public class Method {

    private final String signature;

    private final int initialIndex;

    private final int argCount;

    private final boolean varArg;

    private final String name;

    private final boolean instance;

    private final MethodDefinition.Accessibility accessibility;

    private final boolean override;

    private final boolean overloaded;

    private final String accessiblePropertyName;

    private final boolean setter;

    private final boolean getter;

    private final boolean ctor;

    private final boolean returnsVoid;

    private final int initialLineNo;

    private final int columnNo;

    private final int length;

    public Method(MethodDefinition md) {
        signature = md.getSignature();
        initialIndex = md.getIndex();
        argCount = md.getArgCount();
        varArg = md.isVarArg();
        name = md.getName();
        instance = md.isInstance();
        accessibility = md.getAccessibility();
        override = md.isOverride();
        overloaded = md.isOverloaded();
        setter = md.isSetter();
        getter = md.isGetter();
        if (getter || setter) {
            accessiblePropertyName = md.getAccessiblePropertyName();
        }
        else {
            accessiblePropertyName = null;
        }
        ctor = md.isCtor();
        returnsVoid = !ctor && md.isVoid();
        initialLineNo = md.getLineNo();
        columnNo = md.getAstNode().getColumnNo();
        length = md.getLength();
    }

    public boolean isVarArg() {
        return varArg;
    }

    public String getName() {
        return name;
    }

    public String getSignature() {
        return signature;
    }

    public int getInitialIndex() {
        return initialIndex;
    }

    public int getArgCount() {
        return argCount;
    }

    public boolean isInstance() {
        return instance;
    }

    public boolean isStatic() {
        return !instance;
    }

    public MethodDefinition.Accessibility getAccessibility() {
        return accessibility;
    }

    public boolean isOverride() {
        return override;
    }

    public boolean isOverloaded() {
        return overloaded;
    }

    public String getAccessiblePropertyName() {
        return accessiblePropertyName;
    }

    public boolean isSetter() {
        return setter;
    }

    public boolean isGetter() {
        return getter;
    }

    public boolean isCtor() {
        return ctor;
    }

    public boolean isReturnsVoid() {
        return returnsVoid;
    }

    public int getInitialLineNo() {
        return initialLineNo;
    }

    public int getColumnNo() {
        return columnNo;
    }

    public int getLength() {
        return length;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        else {
            return o == this || signature.equals(((Method) o).signature);
        }
    }

    @Override
    public int hashCode() {
        return signature.hashCode();
    }

    @Override
    public String toString() {
        return signature;
    }
}
