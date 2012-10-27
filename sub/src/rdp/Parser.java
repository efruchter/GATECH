package rdp;

/**
 * A really basic implementation of a recursive descent parser.
 *
 * This parser essentially implements the grammar corresponding to the regex:
 *     ([0-9]\+)*[0-9]
 *
 * You read that right, only single digit numbers and no spaces because I
 * didn't feel like writing a real scanner.
 *
 * Read the Wikipedia article because it's far more coherent than the slides:
 *     http://en.wikipedia.org/wiki/Recursive_descent_parser
 */
public class Parser {

    private Scanner scanner;
    private Scanner.Symbol sym;

    public Parser(final String input) {
        scanner = new Scanner(input);
    }

    private void getsym() {
        sym = scanner.nextSymbol();
    }

    private boolean accept(Scanner.Symbol s) {
        if (s == sym) {
            getsym();
            return true;
        } else {
            return false;
        }
    }

    private void error() throws ParserException {
        throw new ParserException();
    }

    /*
     * Begin RDP steps
     */

    private void expr() throws ParserException {
        term();
        e_tail();
    }

    private void term() throws ParserException {
        if (accept(Scanner.Symbol.num)) {
        } else {
            error();
        }
    }

    private void e_tail() throws ParserException {
        if (accept(Scanner.Symbol.plus)) {
            term();
            e_tail();
        } else {
            return;
        }
    }

    /**
     * Top-level step, expecting a single expr
     */
    private void program() throws ParserException {
        getsym();
        expr();
    }

    /*
     * End RDP steps
     */

    public boolean parse() {
        try {
            program();
        } catch (ParserException e) {
            return false;
        }

        return true;
    }

}
