package org.pirat9600q;

import org.pirat9600q.analysis.Dependencies;
import org.pirat9600q.analysis.MethodDefinition;
import org.pirat9600q.dot.Cluster;
import org.pirat9600q.dot.Colors;
import org.pirat9600q.dot.Comment;
import org.pirat9600q.dot.Edge;
import org.pirat9600q.dot.Graph;
import org.pirat9600q.dot.Node;
import org.pirat9600q.dot.Rankdirs;
import org.pirat9600q.dot.Shapes;
import org.pirat9600q.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        graph.setRankdir(Rankdirs.LR);
        final Cluster simpleMethods = new Cluster("simple");
        final Map<MethodDefinition, Node> methodToNode = info.getMethods().stream()
            .filter(method -> !info.isInterfaceMethod(method))
            .collect(Collectors.toMap(Function.<MethodDefinition>identity(),
                DependencyInfoGraphSerializer::createNode));
        methodToNode.entrySet().stream()
            .forEach(methodAndNode -> {
                if (info.hasMethodDependencies(methodAndNode.getKey())) {
                    graph.addComponent(methodAndNode.getValue());
                }
                else {
                    simpleMethods.addComponent(methodAndNode.getValue());
                }
            });
        graph.addComponent(simpleMethods);
        for (final MethodDefinition caller : methodToNode.keySet()) {
            for (final MethodDefinition callee : info.getMethodDependencies(caller)) {
                graph.addComponent(createEdge(caller, callee, methodToNode));
            }
        }
        final Comment comment = new Comment(getDescription());
        graph.addComponent(comment);
        return graph.serialize();
    }

    private static String getDescription() {
        return FileUtils.getTextStreamContents(
            DependencyInfoGraphSerializer.class.getResourceAsStream("graph description.txt"));
    }

    private static Edge createEdge(final MethodDefinition caller,
        final MethodDefinition callee, final Map<MethodDefinition, Node> methodToNode) {
        final Node callerNode = methodToNode.get(caller);
        final Node calleeNode = methodToNode.get(callee);
        final Edge edge = new Edge(callerNode, calleeNode);
        final int indexDistance = caller.getIndexDistanceTo(callee);
        final int lineDistance = caller.getLineDistanceTo(callee);
        edge.setLabel(getFormattedEdgeLabel(indexDistance, lineDistance));
        return edge;
    }

    private static Node createNode(final MethodDefinition method) {
        final Node node = new Node(method.getSignature());
        node.setColor(getColorForMethod(method));
        node.setShape(getShapeForMethod(method));
        return node;
    }

    private static String getFormattedEdgeLabel(final int indexDistance, final int lineDistance) {
        return String.format("%d/%d", indexDistance, lineDistance);
    }

    private static Colors getColorForMethod(final MethodDefinition method) {
        switch (method.getAccessibility()) {
            case PUBLIC: return Colors.GREEN;
            case PROTECTED: return Colors.YELLOW;
            case PRIVATE: return Colors.BLACK;
            case DEFAULT: return Colors.BLUE;
            default: throw new IllegalArgumentException(
                "Unexpected accessibility type " + method.getAccessibility());
        }
    }

    private static Shapes getShapeForMethod(final MethodDefinition method) {
        if (method.isStatic()) {
            return Shapes.POLYGON;
        }
        else if (method.isOverride()) {
            return Shapes.TRAPEZIUM;
        }
        else if (method.isOverloaded()) {
            return Shapes.INVTRIANGLE;
        }
        else {
            return Shapes.ELLIPSE;
        }
    }
}
