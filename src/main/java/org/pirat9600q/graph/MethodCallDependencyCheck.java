package org.pirat9600q.graph;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class MethodCallDependencyCheck extends Check {

    private DependencyGraph graph = new DependencyGraph();

    public DependencyGraph getGraph() {
        return graph;
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[]{TokenTypes.METHOD_CALL};
    }

    @Override
    public void visitToken(DetailAST ast) {
        System.out.println(getClass().getName() + " called");
    }

    protected DetailAST getEnclosingMethod(final DetailAST node) {
        return null; //TODO
    }

    protected DetailAST getEnclosingClass(final DetailAST node) {
        return null; //TODO
    }
}
