package Impls;

import Interfaces.ICNF;
import Interfaces.IGraphColor;
import Interfaces.IPolyReduce;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class PolyReduce implements IPolyReduce {

    private String original;
    private String target;
    private String outputPath;

    public PolyReduce(String original, String target, String output) {
        System.out.println("Original: " + original + "\nTarget: " + target);
        this.original = original;
        this.target = target;
        this.outputPath = output;
    }

    private void reduce(Object property) {
        if (property == null) {
            System.err.println("Unable to load object! Object is a null object.");
            System.exit(-404);
        }
        try {
            switch (target) {
                // Graph coloring to SAT
                case "SAT": {
                    if (original.equals("GC")) {
                        IGraphColor graphColor = (IGraphColor)property;
                        graphColor.fGCtSAT();
                        graphColor.printExternSAT(outputPath);
                    }
                    break;
                }
                case "3SAT": {
                    // SAT to 3SAT: Impled
                    if (original.equals("SAT")) {
                        ICNF cnf = (ICNF)property;
                        cnf.fSATt3SAT();
                        cnf.printAllClauses();
                        cnf.printToFile(outputPath);
                    }
                    break;
                }
                case "GC": {
                    // 3SAT to GC: Implemented.
                    if (original.equals("3SAT")) {
                        ICNF cnf = (ICNF)property;
                        cnf.f3SATtGC();
                        cnf.printExternGC(outputPath);
                    }
                    break;
                }
                case "Clique": {
                    if (original.equals("SAT")) {
                        ICNF cnf = (ICNF)property;
                        cnf.f3SATtCl();
                    }
                    break;
                }
                case "VC": {
                    if (original.equals("SAT")) {
                        ICNF cnf = (ICNF)property;
                        cnf.f3SATtVC();
                    }
                    break;
                }
                default: {
                    break;
                }
            }
        }
        catch (ClassCastException e) {
            System.err.println("Invalid casting type. Please check the parameter fed to the program");
            System.err.println("Input type: " + property.getClass().getSimpleName());
            System.exit(-1);
        }

    }

    @Override
    public void run(File file) {
        Object property = null;
        String type;
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                switch (scanner.next()) {
                    case "c": {
                        scanner.nextLine();
                        break;
                    }
                    case "p": {
                        type = scanner.next();
                        int clauseInd = 0;
                        switch (type) {
                            case "gc": {
                                int vertex = scanner.nextInt();
                                int color = scanner.nextInt();
                                property = new GraphColor(vertex, color);
                                for (int i = 0; i < vertex; i++) {
                                    for (int j = 0; j < vertex; j++) {
                                        if (scanner.hasNextInt()) {
                                            if (scanner.nextInt() == 1) {
                                                ((IGraphColor)property).putRelation(i, j);
                                            }
                                        }
                                        else {
                                            System.err.println("Invalid input format!");
                                            System.exit(-1);
                                        }
                                    }
                                }
                                break;
                            }
                            case "cnf": {
                                property = new CNF(scanner.nextInt(), scanner.nextInt());
                                while (scanner.hasNextInt()) {
                                    int next = scanner.nextInt();
                                    if (next != 0) {
                                        ((ICNF)property).putClause(clauseInd, next);
                                    } else {
                                        clauseInd++;
                                    }
                                }
                                break;
                            }
                            default: {
                                System.out.println("Type" + type + "not implemented. System exit!");
                                System.exit(-404);
                            }
                        }
                        break;
                    }
                    default: {
                        System.out.println("Not readable argument!");
                        System.exit(1);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            reduce(property);
        }
    }
}
