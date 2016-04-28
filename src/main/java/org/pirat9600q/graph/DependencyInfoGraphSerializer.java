package org.pirat9600q.graph;

import net.claribole.zgrviewer.dot.BasicNode;
import net.claribole.zgrviewer.dot.Cluster;
import net.claribole.zgrviewer.dot.Comment;
import net.claribole.zgrviewer.dot.Edge;
import net.claribole.zgrviewer.dot.Graph;
import net.claribole.zgrviewer.dot.Node;
import org.pirat9600q.utils.FileUtils;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// CSOFF: ClassDataAbstractionCoupling
public final class DependencyInfoGraphSerializer {

    private DependencyInfoGraphSerializer() { }

    public static void writeToFile(final Dependencies info, final String fileName) {
        try (final PrintWriter file = new PrintWriter(new File(fileName))) {
            file.write(serialize(info));
        }
        catch (final FileNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String serialize(final Dependencies info) {
        final Graph graph = new Graph("dependencies");
        graph.setDirected(true);
        graph.setRankdir(Graph.LR);
        final Map<MethodDefinition, Node> methodToNode = info.getMethods().stream()
            .filter(method -> !info.isInterfaceMethod(method))
            .collect(Collectors.toMap(Function.<MethodDefinition>identity(),
                method -> createNode(graph, method)));
        final Cluster simpleMethods = new Cluster(graph, "simple");
        methodToNode.entrySet().stream()
            .forEach(methodAndNode -> {
                if (info.hasMethodDependencies(methodAndNode.getKey())) {
                    graph.addNode(methodAndNode.getValue());
                }
                else {
                    simpleMethods.addNode(methodAndNode.getValue());
                }
            });
        graph.addGenericNode(simpleMethods);
        for (final MethodDefinition caller : methodToNode.keySet()) {
            for (final MethodDefinition callee : info.getMethodDependencies(caller)) {
                graph.addEdge(createEdge(graph, caller, callee, methodToNode));
            }
        }
        final Comment comment = new Comment(graph);
        comment.setText(getDescription());
        graph.addNode(comment);
        return graph.toString();
    }

    private static String getDescription() {
        return FileUtils.getTextStreamContents(
            DependencyInfoGraphSerializer.class.getResourceAsStream("graph description.txt"));
    }

    private static Edge createEdge(final Graph graph, final MethodDefinition caller,
        final MethodDefinition callee, final Map<MethodDefinition, Node> methodToNode) {
        final Node callerNode = methodToNode.get(caller);
        final Node calleeNode = methodToNode.get(callee);
        final Edge edge = new Edge(graph, callerNode, calleeNode);
        final int indexDistance = caller.getIndexDistanceTo(callee);
        final int lineDistance = caller.getLineDistanceTo(callee);
        edge.setLabel(getFormattedEdgeLabel(indexDistance, lineDistance));
        return edge;
    }

    private static Node createNode(final Graph graph, final MethodDefinition method) {
        final BasicNode node = new BasicNode(graph, quote(method.getSignature()));
        node.setColor(getColorForMethod(method));
        node.setShape(getShapeForMethod(method));
        return node;
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
            default: throw new IllegalArgumentException(
                "Unexpected accessibility type " + method.getAccessibility());
        }
    }

    private static int getShapeForMethod(final MethodDefinition method) {
        if (method.isStatic()) {
            return BasicNode.POLYGON;
        }
        else if (method.isOverride()) {
            return BasicNode.TRAPEZIUM;
        }
        else if (method.isOverloaded()) {
            return BasicNode.INVTRIANGLE;
        }
        else {
            return BasicNode.ELLIPSE;
        }
    }

    private static String quote(final String str) {
        return String.format("\"%s\"", str);
    }
}
