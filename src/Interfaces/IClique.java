package Interfaces;

import java.util.List;

public interface IClique {
    void putVars(List<List<Integer>> clause);

    void printInfo();

    void fCltVC();
}
