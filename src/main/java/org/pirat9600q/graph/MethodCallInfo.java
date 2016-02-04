package org.pirat9600q.graph;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public final class MethodCallInfo {

    private int callerIndex;

    private int calleeIndex;

    private int lineNo;

    private int columnNo;

    private CallType callType;

    private MethodCallInfo() { }

    public int getCallerIndex() {
        return callerIndex;
    }

    public int getCalleeIndex() {
        return calleeIndex;
    }

    public int getLineNo() {
        return lineNo;
    }

    public int getColumnNo() {
        return columnNo;
    }

    public CallType getCallType() {
        return callType;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(callerIndex)
                .append(calleeIndex)
                .append(lineNo)
                .append(columnNo)
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
            final MethodCallInfo rhs = (MethodCallInfo) o;
            return new EqualsBuilder()
                    .append(callerIndex, rhs.callerIndex)
                    .append(calleeIndex, rhs.calleeIndex)
                    .append(lineNo, rhs.lineNo)
                    .append(columnNo, rhs.columnNo)
                    .isEquals();
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public static WithCallerIndex builder() {
        return new Builder();
    }

    public static final class Builder implements
            WithCallerIndex,
            WithCalleeIndex,
            WithLineNo,
            WithColumnNo,
            WithCallType,
            WithBuildResult {

        private MethodCallInfo methodCallInfo;

        private Builder() {
            methodCallInfo = new MethodCallInfo();
        }

        @Override
        public WithLineNo callFromTo(int callerIndex, int calleeIndex) {
            methodCallInfo.callerIndex = callerIndex;
            methodCallInfo.calleeIndex = calleeIndex;
            return this;
        }

        @Override
        public WithCalleeIndex callerIndex(int callerIndex) {
            methodCallInfo.callerIndex = callerIndex;
            return this;
        }

        @Override
        public WithLineNo calleeIndex(int calleeIndex) {
            methodCallInfo.calleeIndex = calleeIndex;
            return this;
        }

        @Override
        public WithCallType at(int lineNo, int columnNo) {
            methodCallInfo.lineNo = lineNo;
            methodCallInfo.columnNo = columnNo;
            return this;
        }

        @Override
        public WithColumnNo lineNo(int lineNo) {
            methodCallInfo.lineNo = lineNo;
            return this;
        }

        @Override
        public WithCallType columnNo(int columnNo) {
            methodCallInfo.columnNo = columnNo;
            return this;
        }

        @Override
        public WithBuildResult callType(CallType callType) {
            methodCallInfo.callType = callType;
            return this;
        }

        @Override
        public MethodCallInfo get() {
            final MethodCallInfo mci = methodCallInfo;
            methodCallInfo = null;
            return mci;
        }
    }

    public interface WithCallerIndex {
        WithCalleeIndex callerIndex(int callerIndex);

        WithLineNo callFromTo(int callerIndex, int calleeIndex);
    }

    public interface WithCalleeIndex {
        WithLineNo calleeIndex(int calleeIndex);
    }

    public interface WithLineNo {
        WithColumnNo lineNo(int lineNo);

        WithCallType at(int lineNo, int columnNo);
    }

    public interface WithColumnNo {
        WithCallType columnNo(int columnNo);
    }

    public interface WithCallType {
        WithBuildResult callType(CallType callType);
    }

    public interface WithBuildResult {
        MethodCallInfo get();
    }

    public enum CallType {
        METHOD_CALL,
        METHOD_REFERENCE
    }
}
