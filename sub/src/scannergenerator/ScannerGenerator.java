package scannergenerator;

import nfa.*;
import nfa.NFAUtil.NFASegment;
import spec.Spec;
import spec.SpecReader;
import tokenizer.Token;
import tokenizer.Tokenizer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ScannerGenerator {
    final InputStream specFileInputStream;
    final InputStream programFileInputStream;

    public ScannerGenerator(InputStream specFileInputStream, InputStream programFileInputStream) {
        this.specFileInputStream = specFileInputStream;
        this.programFileInputStream = programFileInputStream;
    }

    public Tokenizer generateTokenizer() {
        SpecReader specReader = new SpecReader(this.specFileInputStream);
        Spec spec = specReader.specify();

        NFASegment nfa = NFABuilder.buildNFAFromSpec(spec);
        NFA dfa = NFAUtil.convertToDFA(new NFA(nfa.start));

        return new Tokenizer(dfa, programFileInputStream);
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Parameters: <spec-file> <program-file>");
            System.exit(1);
        }

        String specFilePath = args[0];
        String programFilePath = args[1];

        ScannerGenerator scannerGenerator = null;

        try {
            InputStream specFileInputStream = new FileInputStream(specFilePath);
            InputStream programFileInputStream = new FileInputStream(programFilePath);
            scannerGenerator = new ScannerGenerator(specFileInputStream, programFileInputStream);
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
            System.exit(1);
        }

        Tokenizer tokenizer = scannerGenerator.generateTokenizer();

        for (Token token : tokenizer) {
            System.out.println(token);
        }
    }
}
