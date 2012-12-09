package project.phase2;

import project.phase2.ll1parsergenerator.AST;
import project.phase2.ll1parsergenerator.ASTNode;
import project.scangen.ScannerGenerator;
import project.scangen.tokenizer.Token;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class MiniREParser {
    private static final Set<String> KEYWORDS = new HashSet<String>() {{
        add("begin"); add("end");
        add("find"); add("replace"); add("recursivereplace");
        add("maxfreqstring");
        add("diff"); add("union"); add("inters");
        add("print");
        add("with"); add("in");
    }};

    private final ScannerGenerator scangen;

    public MiniREParser(final ScannerGenerator scangen) {
        this.scangen = scangen;
    }

    /**
     * RDP utility methods
     */

    private Token token = null;
    private Iterator<Token> tokenIterator;
    private Stack<ASTNode<String>> nodeStack;

    public AST<String> parse() throws ParseException {
        AST<String> ast = new AST<String>();
        ast.setRoot(new ASTNode<String>("root", false));

        nodeStack = new Stack<ASTNode<String>>();
        nodeStack.push(ast.getRoot());

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
            token = new Token("EOF", "EOF", -1, -1);
        }
    }

    /**
     * If tokenType matches the current token's type, consume it and return
     * true; otherwise return false.
     */
    private boolean accept(String tokenType) {
        if (token.type.equals(tokenType)) {
            ASTNode<String> tokenTypeNode = new ASTNode<String>(tokenType, false);
            tokenTypeNode.insert(new ASTNode<String>(token.value, true));
            nodeStack.peek().insert(tokenTypeNode);
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
            throw new ParseException(String.format("Expected `%s' got `%s'", tokenType, token.type), token);
    }

    /**
     * Add a new nonterminal node and push it on the stack.
     */
    private void push(String value) {
        ASTNode<String> node = new ASTNode<String>(value, false);
        nodeStack.peek().insert(node);
        nodeStack.push(node);
    }

    /**
     * Pop the current nonterminal node off the stack.
     */
    private void pop() {
        nodeStack.pop();
    }

    /**
     * Begin RDP methods
     */

    private void minire_program() throws ParseException {
        push("minire_program");

        expect("BEGIN");
        statement_list();
        minire_program_tail();

        pop();
    }

    private void minire_program_tail() throws ParseException {
        push("minire_program_tail");

        expect("END");

        pop();
    }

    private void statement_list() throws ParseException {
        push("statement_list");

        statement();
        statement_list_tail();

        pop();
    }

    private void statement_list_tail() throws ParseException {
        push("statement_list_tail");

        // hax
        if (token.type.equals("END")) {
            pop();
            return;
        }

        statement();
        statement_list_tail();

        pop();
    }

    private void statement() throws ParseException {
        push("statement");

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
            pop();
            throw new ParseException(String.format("Expected one of `ID', `REPLACE', `RECURSIVEREPLACE', " +
                    "`PRINT' got `%s'", token.type), token);
        }

        expect("SEMICOLON");

        pop();
    }

    private void file_names() throws ParseException {
        push("file_names");

        source_file();
        expect("GREATER-BANG");
        destination_file();

        pop();
    }

    private void source_file() throws ParseException {
        push("source_file");

        expect("ASCII-STRING");

        pop();
    }

    private void destination_file() throws ParseException {
        push("destination_file");

        expect("ASCII-STRING");

        pop();
    }

    private void exp_list() throws ParseException {
        push("exp_list");

        exp();
        exp_list_tail();

        pop();
    }

    private void exp_list_tail() throws ParseException {
        if (accept("COMMA")) {
            push("exp_list_tail");

            exp();
            exp_list_tail();

            pop();
        }
    }

    private void exp() throws ParseException {
        push("exp");

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

        pop();
    }

    private void exp_tail() throws ParseException {
        push("exp_tail");

        try {
            bin_op();
            term();
            exp_tail();
        } catch (ParseException e) {
        }

        pop();
    }

    private void term() throws ParseException {
        push("term");

        expect("FIND");
        expect("REGEX");
        expect("IN");
        filename();

        pop();
    }

    private void filename() throws ParseException {
        push("filename");

        expect("ASCII-STRING");

        pop();
    }

    private void bin_op() throws ParseException {
        push("bin_op");

        if (accept("DIFF")) {
        } else if (accept("UNION")) {
        } else if (accept("INTERS")) {
        } else {
            pop();
            throw new ParseException(String.format("Expected one of `DIFF', `UNION', `INTERS' " +
                    "got `%s'", token.type), token);
        }

        pop();
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
                return new Token(token.value.toUpperCase(), token.value, token.line, token.pos);
            } else {
                return new Token("ID", token.value, token.line, token.pos);
            }
        } else {
            return token;
        }
    }
}
