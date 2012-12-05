package project.phase2;

import project.scangen.ScannerGenerator;
import project.scangen.tokenizer.Token;
import project.scangen.tokenizer.Tokenizer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class MiniREParser {
    private static final String MINIRE_SPEC_PATH = "doc/minire_spec.txt";
    private static final String MINIRE_TEST_SCRIPT_PATH = "doc/minire_spec_input.txt";

    private static final Set<String> KEYWORDS = new HashSet<String>() {{
        add("begin"); add("end");
        add("find"); add("replace"); add("recursivereplace");
        add("maxfreqstring");
        add("union"); add("inters");
        add("print");
        add("with"); add("in");
    }};

    private final Tokenizer tokenizer;

    public MiniREParser(InputStream programFileInputStream) throws IOException {
        InputStream specFileInputStream = new FileInputStream(MINIRE_SPEC_PATH);
        ScannerGenerator scangen = new ScannerGenerator(specFileInputStream, programFileInputStream);
        this.tokenizer = scangen.generateTokenizer();
    }

    public void parse() {
        for (Token token : tokenizer) {
            token = MiniREParser.transformToken(token);
            System.out.println(token);
        }
    }

    /**
     * If token is of type ID-OR-KEYWORD, identify which and return a new
     * such token, otherwise return the original.
     */
    private static Token transformToken(final Token token) {
        if (token.type.equals("ID-OR-KEYWORD")) {
            if (KEYWORDS.contains(token.value)) {
                return new Token("KEYWORD", token.value);
            } else {
                return new Token("ID", token.value);
            }
        } else {
            return token;
        }
    }

    public static void main(String[] args) throws Exception {
        InputStream programFileInputStream = new FileInputStream(MINIRE_TEST_SCRIPT_PATH);
        MiniREParser parser = new MiniREParser(programFileInputStream);
        parser.parse();
    }
}
