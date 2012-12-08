package project.phase2;

import project.phase2.file.StringMatchOperations;
import project.phase2.structs.StringMatchTuple;
import project.scangen.ScannerGenerator;

import java.io.File;
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
        ASTNode<String> minire_program = root.get(0);
        statement_list(minire_program.get(1));
    }

    public void statement_list(final ASTNode<String> statement_list) {
        ASTNode<String> statement = statement_list.get(0);

        if (statement.children.size() == 0) {
            return;
        }

        if (statement.get(0).value.equals("PRINT")) {
            print(statement.get(2));
        }

        statement_list(statement_list.get(1));
    }

    private String formatRegex(final String regex) {
        return regex.substring(1, regex.length() - 1);
    }

    private String formatAsciiString(final String asciiString) {
        return asciiString.substring(1, asciiString.length() - 1);
    }

    private void print(ASTNode<String> exp_list) {
        print_exp(exp_list.get(0));

        if (exp_list.children.size() > 1) {
            print(exp_list.get(2));
        }
    }

    private void print_exp(ASTNode<String> exp) {
        ASTNode<String> tok = exp.get(0);

        if (tok.value.equals("ID")) {
            System.out.println(exp.get(0).get(0).value);
        } else if (tok.value.equals("term")) {
            String regex = formatRegex(tok.get(1).get(0).value);
            String filename = formatAsciiString(tok.get(3).get(0).get(0).value);
            StringMatchTuple res = StringMatchOperations.find(new File(filename), regex);
            System.out.println(res);
            System.exit(0);
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
