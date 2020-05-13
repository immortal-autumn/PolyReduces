package Impls;

import Interfaces.ICNF;
import Interfaces.IClique;
import Interfaces.IGraphColor;
import Utils.FileChecker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class CNF implements ICNF {

    private List<List<Integer>> clauses;
    private int num_of_var;
    private int num_of_clauses;
    private int new_var_ind = 1;

    // Entry for Graph coloring class
    IGraphColor graphColor;

    /**
     * Condition when directly put the data in the file to the class.
     *
     * @param num_of_var     is number of variable.
     * @param num_of_clauses is number of clauses.
     */
    public CNF(int num_of_var, int num_of_clauses) {
        this.num_of_var = num_of_var;
        this.num_of_clauses = num_of_clauses;
        init();
    }

    /**
     * Condition when externally initialise the CNF.
     *
     * @param num_of_var     is the number of variable.
     * @param num_of_clauses is the number of clauses.
     * @param clauses        is the clause extracted by external method.
     */
    public CNF(int num_of_var, int num_of_clauses, List<List<Integer>> clauses) {
        this.num_of_var = num_of_var;
        this.num_of_clauses = num_of_clauses;
        this.clauses = clauses;
    }

    private void init() {
        clauses = new ArrayList<>();
        for (int i = 0; i < num_of_clauses; i++) {
            clauses.add(new ArrayList<>());
        }
    }

    @Override
    public void putClause(int clause, int var) {
        if (Math.abs(var) > num_of_var) {
            System.err.println("Clause " + clause + " has invalid variable " + var);
            System.exit(-1);
        }
        clauses.get(clause).add(var);
    }

    @Override
    public void printAllClauses() {
        System.out.println("------------------\nProperties: CNF");
        System.out.println("Original variable range: [1," + num_of_var + "]");
        String newVarRange = new_var_ind == 1 ? "No new var introduced!" : "(" + num_of_var + "," + (num_of_var + new_var_ind) + ")";
        System.out.println("New variable range: " + newVarRange);
        System.out.println("Current clause: " + clauses);
        System.out.println("--------end--------");
    }

    @Override
    public void printToFile(String filepath) {
        File file = FileChecker.fileChecker(filepath);
        PrintWriter writer = null;
        int non_empty = clauses.size();
        for (List<Integer> i : clauses) {
            if (i.isEmpty()) {
                non_empty--;
            }
        }
        try {
            writer = new PrintWriter(file);
            writer.println("p cnf " + (num_of_var + new_var_ind - 1) + " " + non_empty);
            for (List<Integer> i : clauses) {
                if (!i.isEmpty()) {
                    for (int j : i) {
                        writer.print(j + " ");
                    }
                    writer.println("0");
                }
            }
            writer.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * Splitting a clause to 3-sat form. Splitting will follow with calculation of index.
     * A list with size 5 [1,2,3,4,5] => [1 2 6] [-6 3 4 5] => [1 2 6] [-6 3 7] [-7 4 5]
     * which is given a list [1,2,3,4,5] => [1,2] [3,4,5] => [1,2,6] [-6,3,4,5] Front is always satisfiable!
     *
     * @param clause is the clause of size > 3
     * @return the list of new clauses.
     */
    private List<List<Integer>> splitClause(List<Integer> clause) {
        List<List<Integer>> newList = new ArrayList<>();
        // Conditions for clause <= 3
        if (clause.size() <= 3) {
            newList.add(clause);
            return newList;
        }

        //Assign new variable
        int new_var = num_of_var + new_var_ind;
        new_var_ind++;

        // Assign front list as [1,2,new]
        List<Integer> front = new ArrayList<>(clause.subList(0, 2));
        front.add(new_var);
        newList.add(front);

        // Assign back list as [3,4,5,-new]. Add new var first to make new clauses beautiful.
        List<Integer> back = new ArrayList<>();
        back.add(-new_var);
        back.addAll(clause.subList(2, clause.size()));
        newList.addAll(splitClause(back));

        return newList;
    }

    @Override
    public void fSATt3SAT() {
        List<List<Integer>> removed = new ArrayList<>();
        List<List<Integer>> added = new ArrayList<>();
        for (List<Integer> clause : clauses) {
            if (clause.size() > 3) {
                removed.add(clause);
                added.addAll(splitClause(clause));
            }
        }
        clauses.removeAll(removed);
        clauses.addAll(added);
    }

    @Override
    public void printExternGC(String filepath) {
        if (graphColor == null) {
            System.err.println("Not a valid external graph coloring alg.");
            return;
        }
        graphColor.printVertexToFile(filepath);
    }

    /**
     * Alg:
     * x connect with -x, y joined to any other y
     * when i != j, y[i] joint to x[j] and -x[j]
     * Ci is joined to literal x[j] or -x[j] which is not in clause i.
     * [0,listX.size()) : [x1 : xn]
     * [listX.size, listX.size() * 2) : [-x1 : -xn]
     * [listX.size() * 2, listX.size() * 3) : [y1 : yn]
     * [listX.size() * 3, listX.size() * 3 + listC.size()) : [C1 : Ck]
     * listX.size() = num_of_var; listC.size() = num_of_clauses
     */
    @Override
    public void f3SATtGC() {
        int num_vertex = 3 * num_of_var + num_of_clauses;
        int[][] adjacency = new int[num_vertex][num_vertex];
        // Note: Symmetric assign to adjacency.
        for (int i = 0; i < num_of_var; i++) {
            // x connect with -x
            adjacency[i][i + num_of_var] = 1;
            adjacency[i + num_of_var][i] = 1;
            for (int j = 0; j < num_of_var; j++) {
                // y joined with any other y (!!!never join himself!)
                // y[i] join to x[j] and -x[j] when i != j
                if (i != j) {
                    adjacency[num_of_var * 2 + i][num_of_var * 2 + j] = 1;
                    adjacency[num_of_var * 2 + i][j] = 1;
                    adjacency[j][num_of_var * 2 + i] = 1;
                    adjacency[num_of_var * 2 + i][num_of_var + j] = 1;
                    adjacency[num_of_var + j][num_of_var * 2 + i] = 1;
                }
            }
            // Modify Cj to adjacency.
            for (int j = 0; j < num_of_clauses; j++) {
                List<Integer> clause = clauses.get(j);
                if (!clause.contains(i + 1)) {
                    adjacency[num_of_var * 3 + j][i] = 1;
                    adjacency[i][num_of_var * 3 + j] = 1;
                }
                if (!clause.contains(-(i + 1))) {
                    adjacency[num_of_var * 3 + j][num_of_var + i] = 1;
                    adjacency[num_of_var + i][num_of_var * 3 + j] = 1;
                }
            }
        }
        graphColor = new GraphColor(num_vertex, num_of_var + 1, adjacency);
        graphColor.printVertex();
    }

    @Override
    public void f3SATtVC() {
        IClique clique = new Clique();
        clique.putVars(clauses);
        clique.fCltVC();
    }

    @Override
    public void f3SATtCl() {
        IClique clique = new Clique();
        clique.putVars(clauses);
        clique.printInfo();
    }
}
