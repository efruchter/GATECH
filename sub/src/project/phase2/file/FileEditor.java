package project.phase2.file;

import java.io.*;

/**
 * File editing tool.
 */
public class FileEditor {

    private FileEditor() {}

    /**
     * Replace a substring in a file with another string.
     * @param file the file
     * @param replaceMe the string to replace
     * @param replaceWith the string to replace replaceMe with
     * @throws IOException file writing/reading has been blocked
     */
    public static void replaceAllSubstring (final File file, final String replaceMe, final String replaceWith) throws IOException {
        String print = readEntireFile(file).replaceAll(replaceMe, replaceWith);
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        out.write(print);
        out.close();
    }

    /**
     * Convert an entire file to a string.
     * @param file the file
     * @return the string representing the file
     * @throws IOException file reading has been blocked
     */
    public static String readEntireFile(final File file) throws IOException {
        FileReader in = new FileReader(file);
        StringBuilder contents = new StringBuilder();
        char[] buffer = new char[4096];
        int read = 0;
        do {
            contents.append(buffer, 0, read);
            read = in.read(buffer);
        } while (read >= 0);
        return contents.toString();
    }

}