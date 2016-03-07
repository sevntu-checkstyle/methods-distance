package org.pirat9600q.graph;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.utils.TokenUtils;

public class UnexpectedTokenTypeException extends RuntimeException {

    private final DetailAST node;

    public UnexpectedTokenTypeException(final DetailAST node) {
        this.node = node;
    }

    @Override
    public String getMessage() {
        return String.format("Unexpected token %s of type %s",
                node, TokenUtils.getTokenName(node.getType()));
    }
}
