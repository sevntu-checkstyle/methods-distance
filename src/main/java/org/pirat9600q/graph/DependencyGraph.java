package org.pirat9600q.graph;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.*;
import java.util.stream.Collectors;

public class DependencyGraph {

    private final IncidenceMatrix matrix = new IncidenceMatrix();

    private final Map<DetailAST, String> nodeToSignature = new HashMap<>();

    private final Map<DetailAST, Integer> nodeToIndex = new HashMap<>();

    public void addMethod(final DetailAST method, final String signatureText) {
        matrix.growOrder(+1);
        nodeToIndex.put(method, nodeToIndex.size());
        nodeToSignature.put(method, signatureText);
    }

    public String getMethodSignature(final DetailAST method) {
        return nodeToSignature.get(method);
    }

    public void setFromTo(final DetailAST caller, final DetailAST callee) {
        matrix.setFromTo(getNodeIndex(caller), getNodeIndex(callee));
    }

    public Set<DetailAST> getAllMethods() {
        return nodeToIndex.keySet();
    }

    public List<DetailAST> getMethodDependencies(final DetailAST method) {
        return mapIndicesToNodes(matrix.getSuccessorsOf(getNodeIndex(method)));
    }

    public List<DetailAST> getMethodDependants(final DetailAST method) {
        return mapIndicesToNodes(matrix.getPredecessorsOf(getNodeIndex(method)));
    }

    private List<DetailAST> mapIndicesToNodes(final List<Integer> indices) {
        final Map<Integer, DetailAST> indexToNode = getIndexToNodeMap();
        return indices.stream().map(indexToNode::get).collect(Collectors.toList());
    }

    private int getNodeIndex(final DetailAST node) {
        if(nodeToIndex.containsKey(node)) {
            return nodeToIndex.get(node);
        }
        else {
            throw new RuntimeException("Method node " + node + "was not registered in dependency graph");
        }
    }

    private Map<Integer, DetailAST> getIndexToNodeMap() {
        return MapUtils.invertMap(nodeToIndex);
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    private static class IncidenceMatrix {

        private int order;

        private boolean[][] matrix;

        public IncidenceMatrix() {
            this(0);
        }

        public IncidenceMatrix(final int order) {
            matrix = createMatrix(order);
            this.order = order;
        }

        public int getOrder() {
            return order;
        }

        public void growOrder(final int by) {
            final int newOrder = order + by;
            final boolean[][] newMatrix = createMatrix(newOrder);
            for(int i = 0; i < matrix.length; ++i)
                for(int j = 0; j < matrix[i].length; ++j)
                    newMatrix[i][j] = matrix[i][j];
            order = newOrder;
            matrix = newMatrix;
        }

        public List<Integer> getSuccessorsOf(final int index) {
            final List<Integer> result = new ArrayList<>();
            for(int j = 0; j < matrix[index].length; ++j)
                if(matrix[index][j]) {
                    result.add(j);
                }
            return result;
        }

        public List<Integer> getPredecessorsOf(final int index) {
            final List<Integer> result  = new ArrayList<>();
            for(int i = 0; i < matrix.length; ++i)
                if(matrix[i][index]) {
                    result.add(i);
                }
            return result;
        }

        public boolean isSetFromTo(final int from, final int to) {
            return matrix[from][to];
        }

        public void setFromTo(final int from, final int to) {
            matrix[from][to] = true;
        }

        public void clearFromTo(final int from, final int to) {
            matrix[from][to] = false;
        }

        private static boolean[][] createMatrix(int order) {
            return new boolean[order][order];
        }

        @Override
        public boolean equals(Object o) {
            return EqualsBuilder.reflectionEquals(this, o);
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }
    }
}
