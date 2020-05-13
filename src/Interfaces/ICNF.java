package Interfaces;

/**
 * Handles the program format: cnf
 */
public interface ICNF {

    void putClause(int clause, int var);

    void printToFile(String filepath);

    void printAllClauses();

    /*
     * Polynomial reduce from SAT to 3SAT. Since instance of SAT maps to the instance of 3SAT.
     * By definition, such polyreduction is complete.
     * https://cse.iitkgp.ac.in/~palash/2018AlgoDesignAnalysis/SAT-3SAT.pdf
     * Since we needn't make all clauses to be 3 literals, so we does not need case 1 - 3 by slides represented.
     */
    void fSATt3SAT();

    void printExternGC(String filepath);

    /*
     * Polynomial reduce from 3SAT to Graph coloring.
     * If the input can be converted from 3SAT to Graph coloring, then the reduction is complete.
     * Here we regard there are 10 different literals in SAT, where there are 10 different vertexs available.
     * https://studres.cs.st-andrews.ac.uk/2018_2019/CS3052/Lectures/CS3052-pnpandreductions-slides-3.pdf
     * http://cse.vnit.ac.in/people/narendraschaudhari/wp-content/uploads/sites/15/2014/10/046-3-SAT-encoding-of-K-Colorability-encc004.pdf
     */
    void f3SATtGC();

    void f3SATtVC();

    void f3SATtCl();
}
