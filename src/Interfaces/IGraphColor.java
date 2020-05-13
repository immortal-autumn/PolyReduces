package Interfaces;

public interface IGraphColor {
    // Set vertex[v0][v1] = 1, keep unchanged if the value is 0.
    void putRelation(int v0, int v1);

    // Graph coloring to SAT
    // Result for graph coloring is the conjunction between edge constrain and vertex constrain.
    // https://www.cs.utexas.edu/users/vl/teaching/lbai/coloring.pdf
    // http://cse.vnit.ac.in/people/narendraschaudhari/wp-content/uploads/sites/15/2014/10/046-3-SAT-encoding-of-K-Colorability-encc004.pdf
    void fGCtSAT();

    void printExternSAT(String filepath);

    void printVertex();

    void printVertexToFile(String filepath);
}
