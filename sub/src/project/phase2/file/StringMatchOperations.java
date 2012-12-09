package project.phase2.file;

import project.phase2.structs.StringMatchList;
import project.phase2.structs.StringMatchTuple;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringMatchOperations {

    private StringMatchOperations() {
    }

    /**
     * Print a list, all-pretty like.
     *
     * @param stringMatchList
     */
    public static void print(final StringMatchList stringMatchList) {
        System.out.println(stringMatchList.toString());
    }

    /**
     * Find the string in a given file
     *
     * @param file
     * @param string
     * @return results tuple
     */
    public static StringMatchList find(final File file, final String string) {
        try {
            return findInFile(file, string);
        } catch (IOException i) {
            throw new RuntimeException("File not found! " + file);
        }
    }

    /**
     * replace a given location with a new string.
     */
    public static void replace(final String toReplace, final String replaceWith, final File src, final File dest, final boolean recursive) {
        try {
            String s = FileIO.readEntireFile(src);
            if (recursive) {
                s = s.replaceAll(toReplace, replaceWith);
            } else {
                s = s.replaceFirst(toReplace, replaceWith);
            }
            FileIO.writeFile(dest, s);
        } catch (IOException i) {
            throw new RuntimeException("File not found! " + src);
        }
    }

    private static StringMatchList findInFile(final File file, final String string) throws IOException {

        List<String> lines = FileIO.readEntireFileIntoLines(file);

        StringMatchList tuples = new StringMatchList();

        for (int line = 0; line < lines.size(); line++) {
            Pattern pattern = Pattern.compile(string);
            Matcher matcher = pattern.matcher(lines.get(line));
            while (matcher.find()) {
                StringMatchTuple t = new StringMatchTuple(string);
                t.fileName = file.getPath();
                t.startIndex = matcher.start();
                t.endIndex = matcher.end();
                t.line = 1 + line;
                tuples.add(t);
            }
        }
        return tuples;
    }

    public static void main(String[] args) {
        StringMatchOperations.replace("nuttin", "sumtin", new File("TestFile.txt"), new File("tori.txt"), true);
    }
}
