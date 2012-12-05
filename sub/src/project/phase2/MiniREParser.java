package project.phase2;

import project.scangen.ScannerGenerator;
import project.scangen.tokenizer.Token;
import project.scangen.tokenizer.Tokenizer;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

public class MiniREParser {
    private static final String MINIRE_SPEC_PATH = "doc/minire_spec.txt";
    private static final String MINIRE_TEST_SCRIPT_PATH = "doc/minire_spec_input.txt";

    private static final Set<String> KEYWORDS = new HashSet<String>() {{
        add("begin");
        add("end");
        add("replace");
        add("union");
        add("inters");
        add("replace");
    }};

    public static void main(String[] args) throws Exception {
        InputStream specFileInputStream = new FileInputStream(MINIRE_SPEC_PATH);
        InputStream programFileInputStream = new FileInputStream(MINIRE_TEST_SCRIPT_PATH);
        ScannerGenerator scangen = new ScannerGenerator(specFileInputStream, programFileInputStream);
        Tokenizer tokenizer = scangen.generateTokenizer();

        for (Token token : tokenizer) {
            if (token.type.equals("ID-OR-KEYWORD")) {
                if (KEYWORDS.contains(token.value)) {
                    token = new Token("KEYWORD", token.value);
                } else {
                    token = new Token("ID", token.value);
                }
            }

            System.out.println(token);
        }
    }
}
