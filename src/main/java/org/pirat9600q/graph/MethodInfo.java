package org.pirat9600q.graph;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class MethodInfo {

    private DetailAST methodDef;

    private String signature;

    private boolean isStatic;

    private boolean isOverride;

    private boolean isOverloaded;

    private boolean isVarArg;

    private int minArgCount;

    private int index;

    private Accessibility accessibility;

    public DetailAST getMethodDef() {
        return methodDef;
    }

    public String getSignature() {
        return signature;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean isOverride() {
        return isOverride;
    }

    public boolean isOverloaded() {
        return isOverloaded;
    }

    public boolean isVarArg() {
        return isVarArg;
    }

    public int getMinArgCount() {
        return minArgCount;
    }

    public int getIndex() {
        return index;
    }

    public Accessibility getAccessibility() {
        return accessibility;
    }

    public MethodInfo(DetailAST methodDef, String signature, boolean isStatic, boolean isOverride,
                      boolean isOverloaded, boolean isVarArg, int minArgCount, int index,
                      Accessibility accessibility) {
        this.methodDef = methodDef;
        this.signature = signature;
        this.isStatic = isStatic;
        this.isOverride = isOverride;
        this.isOverloaded = isOverloaded;
        this.isVarArg = isVarArg;
        this.minArgCount = minArgCount;
        this.index = index;
        this.accessibility = accessibility;
    }

    public int getDistanceTo(final MethodInfo other) {
        return Math.abs(index - other.index) - 1;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(index)
                .toHashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(o == null || o.getClass() != this.getClass()) {
            return false;
        }
        else if(o == this) {
            return true;
        }
        else {
            final MethodInfo rhs = (MethodInfo) o;
            return index == rhs.index;
        }
    }

    public enum Accessibility {
        PUBLIC,
        PROTECTED,
        DEFAULT,
        PRIVATE
    }
}
