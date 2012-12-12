package toritools.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class FileParserUtility {

    /**
     * Prevents instantiation.
     */
    private FileParserUtility() {
    }

    /**
     * Red the contents of a file and return a string with the data.
     *
     * @param file
     *            the file to scan.
     * @return the content string.
     */
    public static String readFile(final File file) {
        try {
            final Scanner scan = new Scanner(file);
            final StringBuffer content = new StringBuffer();
            while (scan.hasNextLine()) {
                content.append("\n").append(scan.nextLine());
            }
            return content.toString().replaceFirst("\n", "");
        } catch (FileNotFoundException e) {
            return "TEXT NOT FOUND";
        }
    }
}
