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

    private final ScannerGenerator scangen;

    public MiniREParser(InputStream programFileInputStream) throws IOException {
        InputStream specFileInputStream = new FileInputStream(MINIRE_SPEC_PATH);
        scangen = new ScannerGenerator(specFileInputStream, programFileInputStream);
    }

    /**
     * RDP utility methods
     */

    private Token token = null;
    private Iterator<Token> tokenIterator;

    public void parse() throws ParseException {
        tokenIterator = scangen.generateTokenizer().iterator();
        nextsym();
        minire_program();
    }

    /**
     * Set token to the next one, or special token 'EOF' if none remaining.
     */
    private void nextsym() {
        if (tokenIterator.hasNext()) {
            token = MiniREParser.parseKeywords(tokenIterator.next());
        } else {
            token = new Token("EOF", "EOF");
        }
    }

    /**
     * If tokenType matches the current token's type, consume it and return
     * true; otherwise return false.
     */
    private boolean accept(String tokenType) {
        if (token.type.equals(tokenType)) {
            nextsym();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Invoke accept, raising an exception if token unexpected.
     */
    private void expect(String tokenType) throws ParseException {
        if (!accept(tokenType))
            throw new ParseException(String.format("Expected `%s' got `%s'", tokenType, token.type));
    }

    /**
     * Begin RDP methods
     */

    private void minire_program() throws ParseException {
        expect("BEGIN");
        statement();
        expect("END");
    }

    private void statement() throws ParseException {
        if (accept("ID")) {
            expect("EQUALS");

            if (accept("OCTOTHORPE")) {
                exp();
            } else if (accept("MAXFREQSTRING")) {
                expect("OPEN-PAREN");
                expect("ID");
                expect("CLOSE-PAREN");
            } else {
                exp();
            }
        } else if (accept("PRINT")) {
            expect("OPEN-PAREN");
            exp_list();
            expect("CLOSE-PAREN");
        }

        expect("SEMICOLON");
    }

    private void exp_list() throws ParseException {
        exp();
        exp_list_tail();
    }

    private void exp_list_tail() throws ParseException {
        if (accept("COMMA")) {
            exp();
            exp_list_tail();
        }
    }

    private void exp() throws ParseException {
        if (accept("ID")) {
        } else {
            expect("OPEN-PAREN");
            exp();
            expect("CLOSE-PAREN");
        }
    }

    /**
     * End RDP methods
     */

    /**
     * If token is of type ID-OR-KEYWORD, identify which and return a new
     * such token, otherwise return the original.
     */
    private static Token parseKeywords(final Token token) {
        if (token.type.equals("ID-OR-KEYWORD")) {
            if (KEYWORDS.contains(token.value)) {
                return new Token(token.value.toUpperCase(), token.value);
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
