package project.phase2.file;

import project.phase2.structs.StringMatchList;
import project.phase2.structs.StringMatchTuple;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class StringMatchOperations {

    private StringMatchOperations() {
    }

    public static void print(final StringMatchList stringMatchList) {
        System.out.println(stringMatchList.toString());
    }

    public static StringMatchTuple find(final File file, final String string) {
        try {
            return findInFile(file, string);
        } catch (IOException i) {
            throw new RuntimeException("File not found! " + file);
        }
    }

    public static void replace(final File file, int startIndex, int endIndex, String replaceWith) {
        try {
            FileEditor.replaceAtSubstring(file, startIndex, endIndex, replaceWith);
        } catch (IOException i) {
            throw new RuntimeException("File not found! " + file);
        }
    }

    public static void recursiveReplace(final File file, final String match, final String replaceWith) {
        try {
            boolean remaining = true;
            while (remaining) {
                StringMatchTuple tuple = findInFile(file, match);
                if (tuple.found()) {
                    FileEditor.replaceAtSubstring(new File(tuple.fileName), tuple.startIndex, tuple.endIndex, replaceWith);
                } else {
                    remaining = false;
                }
            }
        } catch (IOException i) {
            throw new RuntimeException("File not found! " + file);
        }
    }

    private static StringMatchTuple findInFile(final File file, final String string) throws IOException {
        StringMatchTuple t = new StringMatchTuple(string);
        t.fileName = file.getPath();
        findInFile(t);
        return t;
    }

    private static void findInFile(final StringMatchTuple stringMatchTuple) throws IOException {
        List<String> lines = FileEditor.readEntireFileIntoLines(new File(stringMatchTuple.fileName));
        for (int line = 0; line < lines.size(); line++) {
            int i = -1;
            if ((i = lines.indexOf(line)) != -1) {
                stringMatchTuple.startIndex = i;
                stringMatchTuple.endIndex = i + stringMatchTuple.string.length();
                break;
            }
        }
    }
}
