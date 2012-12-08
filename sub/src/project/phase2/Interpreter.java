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
        ASTNode<String> root = parser.parse().root;

        ASTNode<String> minire_program = root.children.get(0);
        ASTNode<String> statement_list = minire_program.children.get(1);
        ASTNode<String> statement = statement_list.children.get(0);

        if (statement.children.get(0).value.equals("PRINT")) {
            print(statement.children.get(2));
        }
    }

    private void print(ASTNode<String> exp_list) {
        print_exp(exp_list.children.get(0));

        if (exp_list.children.size() > 1) {
            print(exp_list.children.get(2));
        }
    }

    private void print_exp(ASTNode<String> exp) {
        if (exp.children.get(0).value.equals("ID")) {
            System.out.println(exp.children.get(0).children.get(0).value);
        }
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
