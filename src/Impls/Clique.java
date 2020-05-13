package Impls;

import Interfaces.IClique;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Clique implements IClique {
    List<int[]> vertices; // int[2]
    List<int[]> edges; // int[4]
    List<int[]> reverseEdge;

    public Clique() {
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
        reverseEdge = new ArrayList<>();
    }

    @Override
    public void putVars(List<List<Integer>> clause) {
        for (int i = 0; i < clause.size(); i++) {
            for (int j = 0; j < clause.get(i).size(); j++) {
                vertices.add(new int[]{i, j});
                for  (int m = 0; m < clause.size(); m++) {
                    for (int n = 0; n < clause.get(m).size(); n++) {
                        if (i != m && (!clause.get(i).get(j).equals(-clause.get(m).get(n)))) {
                            edges.add(new int[] {i, j, m, n});
                        }
                        else {
                            if (i != m) {
                                // Prevent vertex connected to itself
                                reverseEdge.add(new int[]{i, j, m, n});
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void printInfo() {
        System.out.println("vertices:");
        for (int[] i : vertices) {
            System.out.print(Arrays.toString(i) + " ");
        }
        System.out.println();
        System.out.println("edges:");
        for (int[] i : edges) {
            System.out.print(Arrays.toString(i) + " ");
        }
        System.out.println();
    }

    @Override
    public void fCltVC() {
        System.out.println("reverse:");
        for (int[] i : reverseEdge) {
            System.out.print(Arrays.toString(i) + " ");
        }
        System.out.println();
    }
}
