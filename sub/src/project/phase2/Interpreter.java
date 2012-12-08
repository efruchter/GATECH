package project.phase2;

import project.scangen.ScannerGenerator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Interpreter {
    private static final String MINIRE_SPEC_PATH = "doc/minire_spec.txt";

    private final MiniREParser parser;

    public Interpreter(MiniREParser parser) {
        this.parser = parser;
    }

    public void interpret() throws ParseException {
        AST<String> ast = parser.parse();
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Parameters: <program-file>");
            System.exit(1);
        }

        String programFilePath = args[0];

        ScannerGenerator scannerGenerator = null;

        try {
            InputStream specFileInputStream = new FileInputStream(MINIRE_SPEC_PATH);
            InputStream programFileInputStream = new FileInputStream(programFilePath);
            scannerGenerator = new ScannerGenerator(specFileInputStream, programFileInputStream);
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
            System.exit(1);
        }

        MiniREParser parser = new MiniREParser(scannerGenerator);
        Interpreter interpreter = new Interpreter(parser);

        try {
            interpreter.interpret();
        } catch (ParseException ex) {
            System.out.println(ex);
            System.exit(1);
        }
    }
}
