////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2018 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle.dot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.sevntu.checkstyle.domain.Dependencies;
import com.github.sevntu.checkstyle.dot.domain.AttributeHolder;
import com.github.sevntu.checkstyle.dot.domain.Cluster;
import com.github.sevntu.checkstyle.dot.domain.Colors;
import com.github.sevntu.checkstyle.dot.domain.Comment;
import com.github.sevntu.checkstyle.dot.domain.Edge;
import com.github.sevntu.checkstyle.dot.domain.Element;
import com.github.sevntu.checkstyle.dot.domain.Graph;
import com.github.sevntu.checkstyle.dot.domain.Node;
import com.github.sevntu.checkstyle.dot.domain.Rankdirs;
import com.github.sevntu.checkstyle.dot.domain.Shapes;
import com.github.sevntu.checkstyle.ordering.Method;
import com.github.sevntu.checkstyle.ordering.MethodOrder;
import com.github.sevntu.checkstyle.utils.FileUtils;

public final class DependencyInfoGraphSerializer {

    private DependencyInfoGraphSerializer() { }

    public static void writeToFile(Dependencies info, String fileName) {
        try (PrintWriter file = new PrintWriter(new File(fileName))) {
            file.write(serializeInfo(info));
        }
        catch (final FileNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String serializeInfo(Dependencies dependencies) {
        final MethodOrder info = new MethodOrder(dependencies);
        final Graph graph = new Graph("dependencies");
        graph.setRankdir(Rankdirs.LR);
        final Cluster simpleMethods = new Cluster("simple");
        final Map<Method, Node> methodToNode = info.getMethods().stream()
            .filter(method -> !info.isInterfaceMethod(method))
            .collect(Collectors.toMap(Function.identity(),
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
        return serialize(graph);
    }

    private static String getDescription() {
        return FileUtils.getTextStreamContents(
            DependencyInfoGraphSerializer.class.getResourceAsStream("graph description.txt"));
    }

    private static Edge createEdge(Method caller, Method callee, Map<Method, Node> methodToNode,
        MethodOrder methodOrder) {

        final Node callerNode = methodToNode.get(caller);
        final Node calleeNode = methodToNode.get(callee);
        final Edge edge = new Edge(callerNode, calleeNode);
        final int indexDistance = methodOrder.getMethodsIndexDifference(caller, callee);
        final int lineDistance = methodOrder.getMethodsLineDifference(caller, callee);
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
        return fmt("%d/%d", indexDistance, lineDistance);
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

    private static String serializeElement(Element element) {
        if (element instanceof Graph) {
            return serialize((Graph) element);
        }
        else if (element instanceof Node) {
            return serialize((Node) element);
        }
        else if (element instanceof Edge) {
            return serialize((Edge) element);
        }
        else if (element instanceof Comment) {
            return serialize((Comment) element);
        }
        else if (element instanceof Cluster) {
            return serialize((Cluster) element);
        }
        else {
            throw new IllegalArgumentException(
                "Illegal DOT node type " + element.getClass().getName());
        }
    }

    private static String serialize(Graph graph) {
        final String options = fmt("rankdir = \"%s\";\n", graph.getRankdir());
        return fmt("digraph \"%s\" {\n%s%s}\n", graph.getName(), options,
            serializeChildren(graph.components()));
    }

    private static String serialize(Node node) {
        if (node.hasAttributes()) {
            return fmt("\"%s\" %s;\n", node.getId(), serializeAttributes(node));
        }
        else {
            return fmt("\"%s\";\n", node.getId());
        }
    }

    private static String serialize(Edge edge) {
        if (edge.hasAttributes()) {
            return fmt("\"%s\" -> \"%s\" %s;\n", edge.getStart().getId(), edge.getEnd().getId(),
                serializeAttributes(edge));
        }
        else {
            return fmt("\"%s\" -> \"%s\";", edge.getStart().getId(), edge.getEnd().getId());
        }
    }

    private static String serialize(Comment comment) {
        return fmt("/*\n%s\n*/\n", comment.getText());
    }

    private static String serialize(Cluster cluster) {
        return fmt("subgraph cluster%s {\n%s}\n", cluster.getName(),
            serializeChildren(cluster.components()));
    }

    private static String serializeChildren(List<Element> elements) {
        return elements.stream()
            .map(DependencyInfoGraphSerializer::serializeElement).collect(Collectors.joining());
    }

    private static String serializeAttributes(AttributeHolder holder) {
        return holder.attributes().entrySet().stream()
            .map(nameAndValue -> fmt("%s=\"%s\"", nameAndValue.getKey(), nameAndValue.getValue()))
            .collect(Collectors.joining(" ", "[ ", " ]"));
    }

    private static String fmt(String fmt, Object... args) {
        return String.format(fmt, args);
    }
}
