package org.pirat9600q.graph;

import net.claribole.zgrviewer.dot.BasicNode;
import net.claribole.zgrviewer.dot.Cluster;
import net.claribole.zgrviewer.dot.Comment;
import net.claribole.zgrviewer.dot.Edge;
import net.claribole.zgrviewer.dot.Graph;
import org.pirat9600q.graph.MethodDefinition.Accessibility;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

//CSOFF:
public class DependencyInfoGraphSerializer {

    private static final String GRAPH_LEGEND = "Legend\n" +
            "Node border color:\n" +
            "    a) GREEN - public\n" +
            "    b) YELLOW - protected\n" +
            "    c) BLACK - private\n" +
            "    d) BLUE - default\n" +
            "Node shape:\n" +
            "    if static - rectangle\n" +
            "    otherwise if override - trapezium\n" +
            "    otherwise if overloaded - triangle\n" +
            "    otherwise ellipse\n";

    private DependencyInfoGraphSerializer() { }

    public static void writeToFile(final Dependencies info, final String fileName) {
        try (final PrintWriter file = new PrintWriter(new File(fileName))) {
            file.write(serialize(info));
        }
        catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String serialize(final Dependencies info) {
        try {
            final Graph graph = new Graph("dependencies");
            graph.setDirected(true);
            graph.setRankdir(Graph.LR);
            final Map<MethodDefinition, BasicNode> methodToNode = new HashMap<>();
            final Cluster simpleMethods = new Cluster(graph, "simple");
            final Set<MethodDefinition> nonInterfaceMethods = info.getMethods().stream()
                    .filter(method ->
                            !(method.getAccessibility() == Accessibility.PUBLIC
                            && !info.hasMethodDependencies(method)
                            && !info.hasMethodDependants(method)))
                    .collect(Collectors.toSet());
            for (final MethodDefinition method : nonInterfaceMethods) {
                final BasicNode node = new BasicNode(graph, quote(method.getSignature()));
                node.setColor(getColorForMethod(method));
                node.setShape(getShapeForMethod(method));
                methodToNode.put(method, node);
                if (info.hasMethodDependencies(method)) {
                    graph.addNode(node);
                }
                else {
                    simpleMethods.addNode(node);
                }
            }
            for (final MethodDefinition caller : nonInterfaceMethods) {
                if (info.hasMethodDependencies(caller)) {
                    for (final MethodDefinition callee : info.getMethodDependencies(caller)) {
                        final BasicNode callerNode = methodToNode.get(caller);
                        final BasicNode calleeNode = methodToNode.get(callee);
                        final Edge edge = new Edge(graph, callerNode, calleeNode);
                        final int indexDistance = caller.getIndexDistanceTo(callee);
                        final int lineDistance = caller.getLineDistanceTo(callee);
                        edge.setLabel(getFormattedEdgeLabel(indexDistance, lineDistance));
                        graph.addEdge(edge);
                    }
                }
            }
            graph.addGenericNode(simpleMethods);
            final Comment comment = new Comment(graph);
            comment.setText(GRAPH_LEGEND);
            graph.addNode(comment);
            return graph.toString();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getFormattedEdgeLabel(final int indexDistance, final int lineDistance) {
        return String.format("%d/%d", indexDistance, lineDistance);
    }

    private static Color getColorForMethod(final MethodDefinition method) {
        switch (method.getAccessibility()) {
            case PUBLIC: return Color.GREEN;
            case PROTECTED: return Color.YELLOW;
            case PRIVATE: return Color.BLACK;
            case DEFAULT: return Color.BLUE;
            default: throw new RuntimeException("Unexpected accessibility type " + method.getAccessibility());
        }
    }

    private static int getShapeForMethod(final MethodDefinition method) {
        if(method.isStatic()) {
            return BasicNode.POLYGON;
        }
        else if(method.isOverride()) {
            return BasicNode.TRAPEZIUM;
        }
        else if(method.isOverloaded()) {
            return BasicNode.INVTRIANGLE;
        }
        else {
            return BasicNode.ELLIPSE;
        }
    }

    private static String quote(final String str) {
        return "\"" + str + "\"";
    }
}
