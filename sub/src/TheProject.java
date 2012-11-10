import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class TheProject {
    final InputStream specFileInputStream;
    final InputStream programFileInputStream;

    public TheProject(InputStream specFileInputStream, InputStream programFileInputStream) {
        this.specFileInputStream = specFileInputStream;
        this.programFileInputStream = programFileInputStream;
    }

    public void doStuff() {
        // Fun?
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Parameters: <spec-file> <program-file>");
            System.exit(1);
        }

        String specFilePath = args[0];
        String programFilePath = args[1];

        InputStream specFileInputStream = null;
        InputStream programFileInputStream = null;

        try {
            specFileInputStream = new FileInputStream(specFilePath);
            programFileInputStream = new FileInputStream(programFilePath);
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
            System.exit(1);
        }

        TheProject project = new TheProject(specFileInputStream, programFileInputStream);
        project.doStuff();
    }
}
