package org.pirat9600q.graph;

import net.claribole.zgrviewer.dot.BasicNode;
import net.claribole.zgrviewer.dot.Cluster;
import net.claribole.zgrviewer.dot.Edge;
import net.claribole.zgrviewer.dot.Graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

//CSOFF:
public class DependencyInfoSerializerWithSingleCluster {

    private DependencyInfoSerializerWithSingleCluster() { }

    public static void writeToFile(final DependencyInfo info, final String fileName) {
        try (final PrintWriter file = new PrintWriter(new File(fileName))) {
            file.write(serialize(info));
        }
        catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String serialize(DependencyInfo info) {
        try {
            final Graph graph = new Graph("dependencies");
            graph.setDirected(true);
            graph.setRankdir(Graph.LR);
            final Map<MethodInfo, BasicNode> methodToNode = new HashMap<>();
            final Cluster simpleMethods = new Cluster(graph, "simple");
            for (final MethodInfo method : info.getMethods()) {
                final BasicNode node = new BasicNode(graph, quote(method.getSignature()));
                methodToNode.put(method, node);
                if (info.hasMethodDependencies(method)) {
                    graph.addNode(node);
                }
                else {
                    simpleMethods.addNode(node);
                }
            }
            for (final MethodInfo caller : info.getMethods()) {
                if (info.hasMethodDependencies(caller)) {
                    for (final MethodInfo callee : info.getMethodDependencies(caller)) {
                        final BasicNode callerNode = methodToNode.get(caller);
                        final BasicNode calleeNode = methodToNode.get(callee);
                        final Edge edge = new Edge(graph, callerNode, calleeNode);
                        edge.setLabel(String.valueOf(caller.getDistanceTo(callee)));
                        graph.addEdge(edge);
                    }
                }
            }
            graph.addGenericNode(simpleMethods);
            return graph.toString();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String quote(final String str) {
        return "\"" + str + "\"";
    }
}
