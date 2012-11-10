import scannergenerator.DefinedClass;
import scannergenerator.RDPControl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class TheProject {
    final String specFilePath; // dumb.
    final InputStream specFileInputStream;
    final InputStream programFileInputStream;

    public TheProject(String specFilePath, InputStream specFileInputStream, InputStream programFileInputStream) {
        this.specFilePath = specFilePath;
        this.specFileInputStream = specFileInputStream;
        this.programFileInputStream = programFileInputStream;
    }

    public void doStuff() {
        List<DefinedClass> tokenTypes = RDPControl.getOutput(specFilePath);
        for (DefinedClass tokenType : tokenTypes) {
            System.out.println(tokenType);
        }
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

        TheProject project = new TheProject(specFilePath, specFileInputStream, programFileInputStream);
        project.doStuff();
    }
}
