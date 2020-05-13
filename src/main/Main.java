package main;


import Impls.PolyReduce;

import java.io.File;

// Usage: java -jar Main [Input filepath] [Original type] [Target type]
public class Main {

    /**
     * Check user input format, file existence, type name.
     * @param args cmd arg. 0: filepath, 1: original type, 2: target type.
     */
    public static void main(String[] args) {
        if (args.length == 4) {
            File file = new File(args[0]);
            if (!file.exists()) {
                System.err.println("File " + args[0] + " does not exists!");
                System.exit(-404);
            }
            PolyReduce reduce = new PolyReduce(args[1], args[2], args[3]);
            reduce.run(file);
            return;
        }
        System.out.println("Usage: java -jar Main [Input filepath] [Original type] [Target type] [output filepath]\n" +
                "java Main [Input filepath] [Original type] [Target type] [output filepath]");
    }
}
