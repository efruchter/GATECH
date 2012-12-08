package project.phase2.file;

import project.phase2.structs.StringMatchList;
import project.phase2.structs.StringMatchTuple;

import java.io.*;
import java.util.Scanner;

/**
 * File editing tool.
 */
public class FileEditor {

    private FileEditor() {
    }

    /**
     * Replace a substring in a file with another string.
     *
     * @param file        the file
     * @param replaceMe   the string to replace
     * @param replaceWith the string to replace replaceMe with
     * @throws IOException file writing/reading has been blocked
     */
    public static void replaceAllSubstring(final File file, final String replaceMe, final String replaceWith) throws IOException {
        String print = readEntireFile(file).replaceAll(replaceMe, replaceWith);
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        out.write(print);
        out.close();
    }

    public static StringMatchTuple findInFile(final String s) {
        return null;
    }

    /**
     * Convert an entire file to a string.
     *
     * @param file the file
     * @return the string representing the file
     * @throws IOException file reading has been blocked
     */
    public static String readEntireFile(final File file) throws IOException {
        StringBuffer a = new StringBuffer();
        Scanner s = new Scanner(file);
        while (s.hasNextLine()) {
            a.append("\n").append(s.nextLine());
        }
        return a.toString().replaceFirst("\n", "");
    }
}