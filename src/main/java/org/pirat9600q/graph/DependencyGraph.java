package org.pirat9600q.graph;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DependencyGraph {

    private final IncidenceMatrix matrix;

    private final Map<DetailAST, Integer> nodeToIndex = new HashMap<>();

    public DependencyGraph(final int initialOrder) {
        matrix = new IncidenceMatrix(initialOrder);
    }

    public void setFromTo(final DetailAST caller, final DetailAST callee) {
        matrix.setFromTo(nodeIndex(caller), nodeIndex(callee));
    }

    private int nodeIndex(final DetailAST node) {
        if(nodeToIndex.containsKey(node)) {
            return nodeToIndex.get(node);
        }
        else {
            matrix.growOrder(+1);
            final int nodeIndex = nodeToIndex.size();
            nodeToIndex.put(node, nodeIndex);
            return nodeIndex;
        }
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        else if(o instanceof DependencyGraph) {
            final DependencyGraph dg = (DependencyGraph)o;
            return matrix.equals(dg.matrix) && nodeToIndex.equals(dg.nodeToIndex);
        }
        else {
            return false;
        }
    }

    private static class IncidenceMatrix {

        private int order;

        private boolean[][] matrix;

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
            if(o == null) {
                return false;
            }
            else if(o instanceof IncidenceMatrix) {
                final IncidenceMatrix other = (IncidenceMatrix)o;
                if(order == other.order) {
                    boolean matricesAreEqual = true;
                    for(int i = 0; i < matrix.length; ++i)
                        matricesAreEqual = matricesAreEqual && Arrays.equals(matrix[i], other.matrix[i]);
                    return matricesAreEqual;
                }
                else {
                    return false;
                }
            }
            else {
                return false;
            }
        }
    }
}
