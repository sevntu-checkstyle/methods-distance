package org.pirat9600q.graph;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class MethodCallInfo {

    private DetailAST callNode;

    private int callerIndex;

    private int calleeIndex;

    private int lineNo;

    private int columnNo;

    private CallType callType;

    public int getCallerIndex() {
        return callerIndex;
    }

    public DetailAST getCallNode() {
        return callNode;
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

    public MethodCallInfo(DetailAST callNode, int callerIndex, int calleeIndex, int lineNo,
                          int columnNo, CallType callType) {
        this.callNode = callNode;
        this.callerIndex = callerIndex;
        this.calleeIndex = calleeIndex;
        this.lineNo = lineNo;
        this.columnNo = columnNo;
        this.callType = callType;
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
        if(o == null || o.getClass() != this.getClass()) {
            return false;
        }
        else if(o == this) {
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

    public enum CallType {
        METHOD_CALL,
        METHOD_REFERENCE
    }
}
