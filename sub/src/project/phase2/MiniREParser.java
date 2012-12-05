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

    private static final Map<String, Token> KEYWORDS = new HashMap<String, Token>() {{
        put("begin", new Token("BEGIN", "begin"));
        put("end", new Token("END", "end"));
        put("replace", new Token("REPLACE", "replace"));
        put("union", new Token("UNION", "union"));
        put("inters", new Token("INTERS", "inters"));
        put("replace", new Token("REPLACE", "replace"));
    }};

    public static void main(String[] args) throws Exception {
        InputStream specFileInputStream = new FileInputStream(MINIRE_SPEC_PATH);
        InputStream programFileInputStream = new FileInputStream(MINIRE_TEST_SCRIPT_PATH);
        ScannerGenerator scangen = new ScannerGenerator(specFileInputStream, programFileInputStream);
        Tokenizer tokenizer = scangen.generateTokenizer();

        for (Token token : tokenizer) {
            if (token.type.equals("ID-OR-KEYWORD")) {
                if (KEYWORDS.containsKey(token.value)) {
                    token = KEYWORDS.get(token.value);
                } else {
                    token = new Token("ID", token.value);
                }
            }

            System.out.println(token);
        }
    }
}
