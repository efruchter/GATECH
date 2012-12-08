package project.phase2.file;

import project.phase2.structs.StringMatchList;
import project.phase2.structs.StringMatchTuple;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
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

    public static void replaceFirstSubstring(final File file, final String replaceMe, final String replaceWith) throws IOException {
        String print = readEntireFile(file).replaceFirst(replaceMe, replaceWith);
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        out.write(print);
        out.close();
    }

    public static StringMatchTuple findInFile(final String s, final String file) throws IOException{
        StringMatchTuple t = new StringMatchTuple(s);
        t.fileName = file;
        findInFile(t);
        return t;
    }

    private static void findInFile(final StringMatchTuple stringMatchTuple) throws IOException{
        List<String> lines = readEntireFileIntoLines(new File(stringMatchTuple.fileName));
        for (int line = 0; line < lines.size(); line++) {
            int i = -1;
            if ((i = lines.indexOf(line)) != -1) {
                stringMatchTuple.startIndex = i;
                stringMatchTuple.endIndex = i + stringMatchTuple.string.length();
                break;
            }
        }
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
        for (String s : readEntireFileIntoLines(file)) {
            a.append("\n").append(s);
        }
        return a.toString().replaceFirst("\n", "");
    }

    public static List<String> readEntireFileIntoLines(final File file) throws IOException {
        List<String> list = new ArrayList<String>();
        Scanner s = new Scanner(file);
        while (s.hasNextLine()) {
            list.add(s.nextLine());
        }
        return list;
    }
}