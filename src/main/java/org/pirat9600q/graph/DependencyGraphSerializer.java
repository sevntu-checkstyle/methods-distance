package org.pirat9600q.graph;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

public final class DependencyGraphSerializer {

    private DependencyGraphSerializer() {}

    public static void writeToFile(final DependencyGraph graph, final String fileName) {
        final StringBuilder builder = new StringBuilder(30);
        builder.append("digraph dependencies {\n" );
        for(final DetailAST caller : graph.getAllMethods()) {
            List<DetailAST> dependencies = graph.getMethodDependencies(caller);
            if(dependencies.isEmpty()) {
                builder.append('\"').append(graph.getMethodSignature(caller)).append('\"').append('\n');
            }
            else {
                for(final DetailAST callee : dependencies) {
                    builder
                        .append('\"').append(graph.getMethodSignature(caller)).append('\"')
                        .append(" -> ")
                        .append('\"').append(graph.getMethodSignature(callee)).append('\"')
                        .append('\n');
                }
            }
        }
        builder.append("}");

        try(final PrintWriter file = new PrintWriter(new File(fileName))) {
            file.write(builder.toString());
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
