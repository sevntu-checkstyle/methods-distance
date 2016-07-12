package com.github.sevntu.checkstyle.analysis;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.utils.TokenUtils;

public class UnexpectedTokenTypeException extends RuntimeException {

    private final DetailAST node;

    public UnexpectedTokenTypeException(DetailAST node) {
        this.node = node;
    }

    @Override
    public String getMessage() {
        return String.format("Unexpected token %s of type %s",
                node, TokenUtils.getTokenName(node.getType()));
    }
}
