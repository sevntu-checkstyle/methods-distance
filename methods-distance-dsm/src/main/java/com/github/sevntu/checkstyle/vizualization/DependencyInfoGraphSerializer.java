package com.github.sevntu.checkstyle.vizualization;

import com.github.sevntu.checkstyle.domain.Dependencies;
import com.github.sevntu.checkstyle.dot.Cluster;
import com.github.sevntu.checkstyle.dot.Colors;
import com.github.sevntu.checkstyle.dot.Comment;
import com.github.sevntu.checkstyle.dot.Edge;
import com.github.sevntu.checkstyle.dot.Graph;
import com.github.sevntu.checkstyle.dot.Node;
import com.github.sevntu.checkstyle.dot.Rankdirs;
import com.github.sevntu.checkstyle.dot.Shapes;
import com.github.sevntu.checkstyle.ordering.Method;
import com.github.sevntu.checkstyle.ordering.Ordering;
import com.github.sevntu.checkstyle.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class DependencyInfoGraphSerializer {

    private DependencyInfoGraphSerializer() { }

    public static void writeToFile(Dependencies info, String fileName) {
        try (final PrintWriter file = new PrintWriter(new File(fileName))) {
            file.write(serialize(info));
        }
        catch (final FileNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String serialize(Dependencies dependencies) {
        final Ordering info = new Ordering(dependencies);
        final Graph graph = new Graph("dependencies");
        graph.setRankdir(Rankdirs.LR);
        final Cluster simpleMethods = new Cluster("simple");
        final Map<Method, Node> methodToNode = info.getMethods().stream()
            .filter(method -> !info.isInterfaceMethod(method))
            .collect(Collectors.toMap(Function.<Method>identity(),
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
        for (final Method caller : methodToNode.keySet()) {
            for (final Method callee : info.getMethodDependenciesInAppearanceOrder(caller)) {
                graph.addComponent(createEdge(caller, callee, methodToNode, info));
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

    private static Edge createEdge(Method caller,
        Method callee, Map<Method, Node> methodToNode, Ordering ordering) {

        final Node callerNode = methodToNode.get(caller);
        final Node calleeNode = methodToNode.get(callee);
        final Edge edge = new Edge(callerNode, calleeNode);
        final int indexDistance = ordering.getMethodsIndexDifference(caller, callee);
        final int lineDistance = ordering.getMethodsLineDifference(caller, callee);
        edge.setLabel(getFormattedEdgeLabel(indexDistance, lineDistance));
        return edge;
    }

    private static Node createNode(Method method) {
        final Node node = new Node(method.getSignature());
        node.setColor(getColorForMethod(method));
        node.setShape(getShapeForMethod(method));
        return node;
    }

    private static String getFormattedEdgeLabel(int indexDistance, int lineDistance) {
        return String.format("%d/%d", indexDistance, lineDistance);
    }

    private static Colors getColorForMethod(Method method) {
        switch (method.getAccessibility()) {
            case PUBLIC: return Colors.GREEN;
            case PROTECTED: return Colors.YELLOW;
            case PRIVATE: return Colors.BLACK;
            case DEFAULT: return Colors.BLUE;
            default: throw new IllegalArgumentException(
                "Unexpected accessibility type " + method.getAccessibility());
        }
    }

    private static Shapes getShapeForMethod(Method method) {
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
