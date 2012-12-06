package project.phase2;

import project.scangen.ScannerGenerator;
import project.scangen.tokenizer.Token;

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
    private ASTNode<String> curNode;

    public AST<String> parse() throws ParseException {
        AST<String> ast = new AST<String>();
        curNode = ast.root = new ASTNode<String>("root", false);

        tokenIterator = scangen.generateTokenizer().iterator();

        nextsym();
        minire_program();

        return ast;
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
            curNode.insert(new ASTNode<String>(token.value, true));
            nextsym();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Add a new child node to curNode and return the old curNode.
     */
    private ASTNode<String> recurse(String value) {
        ASTNode<String> node = new ASTNode<String>(value, false);
        ASTNode<String> last = curNode;
        curNode.insert(node);
        curNode = node;
        return last;
    }

    /**
     * Decurse back up.
     */
    private void decurse(ASTNode<String> node) {
        curNode = node;
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
        ASTNode<String> last = recurse("minire_program");

        expect("BEGIN");
        statement_list();
        minire_program_tail();

        decurse(last);
    }

    private void minire_program_tail() throws ParseException {
        ASTNode<String> last = recurse("minire_program_tail");

        expect("END");

        decurse(last);
    }

    private void statement_list() throws ParseException {
        ASTNode<String> last = recurse("statement_list");

        statement();
        statement_list_tail();

        decurse(last);
    }

    private void statement_list_tail() throws ParseException {
        ASTNode<String> last = recurse("statement_list_tail");

        try {
            statement();
        } catch (ParseException e) {
            return;
        }

        statement_list_tail();

        decurse(last);
    }

    private void statement() throws ParseException {
        ASTNode<String> last = recurse("statement");

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
        } else if (accept("REPLACE")) {
            expect("REGEX");
            expect("WITH");
            expect("ASCII-STRING");
            expect("IN");
            file_names();
        } else if (accept("RECURSIVEREPLACE")) {
            expect("REGEX");
            expect("WITH");
            expect("ASCII-STRING");
            expect("IN");
            file_names();
        } else if (accept("PRINT")) {
            expect("OPEN-PAREN");
            exp_list();
            expect("CLOSE-PAREN");
        } else {
            throw new ParseException(String.format("Expected one of `ID', `REPLACE', `RECURSIVEREPLACE', " +
                    "`PRINT' got `%s'", token.type));
        }

        expect("SEMICOLON");

        decurse(last);
    }

    private void file_names() throws ParseException {
        ASTNode<String> last = recurse("file_names");

        source_file();
        expect("GREATER-BANG");
        destination_file();

        decurse(last);
    }

    private void source_file() throws ParseException {
        ASTNode<String> last = recurse("source_file");

        expect("ASCII-STRING");

        decurse(last);
    }

    private void destination_file() throws ParseException {
        ASTNode<String> last = recurse("destination_file");

        expect("ASCII-STRING");

        decurse(last);
    }

    private void exp_list() throws ParseException {
        ASTNode<String> last = recurse("exp_list");

        exp();
        exp_list_tail();

        decurse(last);
    }

    private void exp_list_tail() throws ParseException {
        if (accept("COMMA")) {
            ASTNode<String> last = recurse("exp_list_tail");

            exp();
            exp_list_tail();

            decurse(last);
        }
    }

    private void exp() throws ParseException {
        ASTNode<String> last = recurse("exp");

        if (accept("ID")) {
        } else {
            try {
                expect("OPEN-PAREN");
                exp();
                expect("CLOSE-PAREN");
            } catch (ParseException e) {
                term();
                exp_tail();
            }
        }

        decurse(last);
    }

    private void exp_tail() throws ParseException {
        ASTNode<String> last = recurse("exp_tail");

        try {
            bin_op();
            term();
            exp_tail();
        } catch (ParseException e) {
        }

        decurse(last);
    }

    private void term() throws ParseException {
        ASTNode<String> last = recurse("term");

        expect("FIND");
        expect("REGEX");
        expect("IN");
        filename();

        decurse(last);
    }

    private void filename() throws ParseException {
        ASTNode<String> last = recurse("filename");

        expect("ASCII-STRING");

        decurse(last);
    }

    private void bin_op() throws ParseException {
        ASTNode<String> last = recurse("bin_op");

        if (accept("DIFF")) {
        } else if (accept("UNION")) {
        } else if (accept("INTERS")) {
        } else {
            throw new ParseException(String.format("Expected one of `DIFF', `UNION', `INTERS' " +
                    "got `%s'", token.type));
        }

        decurse(last);
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
        AST<String> ast = parser.parse();
        System.out.println(ast);
    }
}
