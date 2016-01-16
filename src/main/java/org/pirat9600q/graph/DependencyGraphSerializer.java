package org.pirat9600q.graph;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import de.parsemis.graph.ListGraph;
import de.parsemis.graph.MutableGraph;
import de.parsemis.graph.Node;
import de.parsemis.parsers.DotGraphParser;
import de.parsemis.parsers.StringLabelParser;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public final class DependencyGraphSerializer {

    private DependencyGraphSerializer() {}

    public static void writeToFile(final DependencyGraph graph, final String fileName) {
        try(final PrintWriter file = new PrintWriter(new File(fileName))) {
            file.write(serialize(graph));
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String serialize(final DependencyGraph graph) {
        final MutableGraph<String, String> mg = new ListGraph<>("dependencies");
        final Map<DetailAST, Node<String, String>> methodToGraphNode = new HashMap<>();
        for(final DetailAST method : graph.getAllMethods()) {
            methodToGraphNode.put(method, mg.addNode(graph.getMethodSignature(method)));
        }
        for(final DetailAST caller : graph.getAllMethods()) {
            for(final DetailAST callee : graph.getMethodDependencies(caller)) {
                final Node<String, String> callerNode = methodToGraphNode.get(caller);
                final Node<String, String> calleeNode = methodToGraphNode.get(callee);
                mg.addEdge(callerNode, calleeNode, "", 1);
            }
        }
        return new DotGraphParser<String, String>(new StringLabelParser(), new StringLabelParser()).serialize(mg);
    }
}
