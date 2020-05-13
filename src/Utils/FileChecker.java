package Utils;

import java.io.File;
import java.io.IOException;

public class FileChecker {
    public static File fileChecker(String filepath) {
        File file = new File(filepath);
        if (!file.exists()) {
            try {
                if(!file.createNewFile()) {
                    System.err.println("File already exists!");
                    System.exit(-1);
                }
            } catch (IOException e) {
                System.err.println("IO Exception: Unable to create the file!");
                System.exit(-1);
            }
        }
        return file;
    }
}
