package Impls;

import Interfaces.ICNF;
import Interfaces.IGraphColor;
import Utils.FileChecker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GraphColor implements IGraphColor {

    private int vertex;
    private int colors;
    private int[][] adjacency;

    /**
     * The result from reduction using Graph coloring reduce to SAT.
     */
    private ICNF result;

    public GraphColor(int vertex, int colors) {
        this.vertex = vertex;
        this.colors = colors;
        // initialising
        adjacency = new int[vertex][vertex];
    }

    /**
     * Externally assigned graph color.
     * @param vertex is the number of vertecises.
     * @param colors is the number of colors.
     * @param adjacency is the externally assigned 2-dim adjacency.
     */
    public GraphColor(int vertex, int colors, int[][] adjacency) {
        this.vertex = vertex;
        this.colors = colors;
        this.adjacency = adjacency;
    }

    @Override
    public void putRelation(int v0, int v1) {
        adjacency[v0][v1] = 1;
    }

    @Override
    public void fGCtSAT() {
        // Append constrains to clauses.
        List<List<Integer>> clauses = new ArrayList<>();
        for (int i = 0; i < vertex; i++) {
            clauses.addAll(vertexConstrain(i));
        }
        clauses.addAll(edgeConstrain());
        // num of vars / num of clause / whole list
        result = new CNF((colors * vertex), clauses.size(), clauses);
        result.printAllClauses();
    }

    @Override
    public void printExternSAT(String filepath) {
        if (result == null) {
            System.err.println("SAT is an invalid object...");
            return;
        }
        result.printToFile(filepath);
    }


    /**
     * Edge constrain: Two nodes cannot assign with same color, two colors cannot be assigned to the same node.
     * Since the 2-dim array is symmetric, so only consider the lower half.
     * Then assign each colors to the nodes and get nodes and generates nodes.
     * For edge (0, 1), the assigned value for 3 colors is (1, 2, 3) (4, 5, 6), which is ind * colors + current_Color
     * The literal of the assigned value is [vertex * color, vertex * color]
     * @return constrains for edges of the grapg.
     */
    private List<List<Integer>> edgeConstrain() {
        List<List<Integer>> edgeConstrain = new ArrayList<>();
        for (int i = 0; i < vertex; i++) {
            for (int j = 0; j < i; j++) {
                // i and j are two nodes and if a[i][j] = 1, they are linked together so that their colors are different
                if (adjacency[i][j] == 1) {
                    for (int m = 1; m <= colors; m++) {
                        for (int n = 1; n <= m; n++) {
                            int jColorm = (j * colors) + m;
                            int iColorn = (i * colors) + n;
                            edgeConstrain.add(new ArrayList<>(List.of(-jColorm, -iColorn)));
                        }
                    }
                }
            }
        }
        return edgeConstrain;
    }

    /**
     * Vertex constrain.
     * Color each vertex of a graph G should have at least one color available in number of colors.
     * For vertex 1: [1, 2, 3, ..., colors]
     * @param vertex is the index of the vertex.
     * @return colors able to assigned to this literal.
     */
    private List<List<Integer>> vertexConstrain(int vertex) {
        List<List<Integer>> clauses = new ArrayList<>();
        List<Integer> colorable = new ArrayList<>();
        for (int i = 1; i <= colors; i++) {
            colorable.add((vertex * colors) + i);
            for (int j = 1; j <= i; j++) {
                // Cannot assign two colors to one node.
                if (i != j) {
                    int thisColor = (vertex * colors) + i;
                    int thatColor = (vertex * colors) + j;
                    clauses.add(List.of(-thisColor, -thatColor));
                }
            }
        }
        clauses.add(colorable);
        return clauses;
    }

    @Override
    public void printVertex() {
        System.out.println("------------------");
        System.out.println("Properties: Vertex\nColors: " + colors + "\nvertices: " + vertex);
        System.out.println("Current adjacency: " + Arrays.deepToString(adjacency));
        System.out.println("--------end-------");
    }

    @Override
    public void printVertexToFile(String filepath) {
        File file = FileChecker.fileChecker(filepath);
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file);
            writer.println("p gc " + vertex + " " + colors);
            for (int[] i : adjacency) {
                for (int j : i) {
                    writer.print(j + " ");
                }
                writer.println();
            }
            writer.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
            System.exit(-1);
        }
    }
}
