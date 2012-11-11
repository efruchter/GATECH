import spec.Spec;
import spec.SpecReader;

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
        SpecReader specReader = new SpecReader(this.specFileInputStream);
        Spec spec = specReader.specify();
        System.out.println(spec);
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Parameters: <spec-file> <program-file>");
            System.exit(1);
        }

        String specFilePath = args[0];
        String programFilePath = args[1];

        TheProject project = null;

        try {
            InputStream specFileInputStream = new FileInputStream(specFilePath);
            InputStream programFileInputStream = new FileInputStream(programFilePath);
            project = new TheProject(specFileInputStream, programFileInputStream);
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
            System.exit(1);
        }

        project.doStuff();
    }
}
