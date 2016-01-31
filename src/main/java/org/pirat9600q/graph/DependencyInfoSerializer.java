package org.pirat9600q.graph;

import de.parsemis.graph.Edge;
import de.parsemis.graph.ListGraph;
import de.parsemis.graph.MutableGraph;
import de.parsemis.graph.Node;
import de.parsemis.parsers.DotGraphParser;
import de.parsemis.parsers.StringLabelParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class DependencyInfoSerializer {

    public static void writeToFile(final DependencyInfo info, final String fileName) {
        try (final PrintWriter file = new PrintWriter(new File(fileName))) {
            file.write(serialize(info));
        }
        catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String serialize(DependencyInfo info) {
        final MutableGraph<String, String> mg = new ListGraph<>("dependencies");
        final Map<MethodInfo, Node<String, String>> methodToGraphNode = new HashMap<>();
        for (final MethodInfo method : info.getMethods()) {
            methodToGraphNode.put(method, mg.addNode(method.getSignature()));
        }
        for (final MethodInfo caller : info.getMethods()) {
            for (final MethodInfo callee : info.getMethodDependencies(caller)) {
                final Node<String, String> callerNode = methodToGraphNode.get(caller);
                final Node<String, String> calleeNode = methodToGraphNode.get(callee);
                final String label = String.valueOf(caller.getDistanceTo(callee));
                final Edge edge = mg.addEdge(callerNode, calleeNode, label, 1);
            }
        }
        return new DotGraphParser<String, String>(new StringLabelParser(), new StringLabelParser())
                .serialize(mg);
    }
}
