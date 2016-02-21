package org.pirat9600q.graph;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public final class MethodInfo {

    private String signature;

    private boolean isStatic;

    private boolean isOverride;

    private boolean isOverloaded;

    private boolean isVarArg;

    private int minArgCount;

    private int index;

    private int lineNo;

    private Accessibility accessibility;

    private MethodInfo() { }

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
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        else if (o == this) {
            return true;
        }
        else {
            final MethodInfo rhs = (MethodInfo) o;
            return index == rhs.index;
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public static WithSignature builder() {
        return new Builder();
    }

    public static final class Builder implements
            WithSignature,
            WithStatic,
            WithOverride,
            WithOverload,
            WithVarArg,
            WithMinArgCount,
            WithIndex,
            WithLineNo,
            WithAccessibility,
            WithBuildResult {

        private MethodInfo methodInfo;

        private Builder() {
            methodInfo = new MethodInfo();
        }

        @Override
        public WithStatic signature(String signature) {
            methodInfo.signature = signature;
            return this;
        }

        @Override
        public WithOverride isStatic(boolean isStatic) {
            methodInfo.isStatic = isStatic;
            return this;
        }

        @Override
        public WithOverload isOverride(boolean isOverride) {
            methodInfo.isOverride = isOverride;
            return this;
        }

        @Override
        public WithVarArg isOverloaded(boolean isOverloaded) {
            methodInfo.isOverloaded = isOverloaded;
            return this;
        }

        @Override
        public WithMinArgCount isVarArg(boolean isVarArg) {
            methodInfo.isVarArg = isVarArg;
            return this;
        }

        @Override
        public WithIndex minArgCount(int minArgCount) {
            methodInfo.minArgCount = minArgCount;
            return this;
        }

        @Override
        public WithLineNo index(int index) {
            methodInfo.index = index;
            return this;
        }

        public WithAccessibility atLine(int lineNo) {
            methodInfo.lineNo = lineNo;
            return this;
        }

        @Override
        public WithBuildResult accessibility(Accessibility accessibility) {
            methodInfo.accessibility = accessibility;
            return this;
        }

        public MethodInfo get() {
            final MethodInfo mi = methodInfo;
            methodInfo = null;
            return mi;
        }
    }

    public interface WithSignature {
        WithStatic signature(String signature);
    }

    public interface WithStatic {

        WithOverride isStatic(boolean isStatic);

        default WithOverride isStatic() {
            return isStatic(true);
        }

        default WithOverride notStatic() {
            return isStatic(false);
        }
    }

    public interface WithOverride {

        WithOverload isOverride(boolean isOverride);

        default WithOverload isOverride() {
            return isOverride(true);
        }

        default WithOverload notOverride() {
            return isOverride(false);
        }
    }

    public interface WithOverload {

        WithVarArg isOverloaded(boolean isOverloaded);

        default WithVarArg isOverloaded() {
            return isOverloaded(true);
        }

        default WithVarArg notOverloaded() {
            return isOverloaded(false);
        }
    }

    public interface WithVarArg {

        WithMinArgCount isVarArg(boolean isVarArg);

        default WithMinArgCount isVarArg() {
            return isVarArg(true);
        }

        default WithMinArgCount notVarArg() {
            return isVarArg(false);
        }
    }

    public interface WithMinArgCount {
        WithIndex minArgCount(int minArgCount);
    }

    public interface WithIndex {
        WithLineNo index(int index);
    }

    public interface WithLineNo {
        WithAccessibility atLine(int lineNo);
    }

    public interface WithAccessibility {
        WithBuildResult accessibility(Accessibility accessibility);
    }

    public interface WithBuildResult {
        MethodInfo get();
    }

    public enum Accessibility {
        PUBLIC,
        PROTECTED,
        DEFAULT,
        PRIVATE
    }
}
