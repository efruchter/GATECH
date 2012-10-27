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

    private void error() throws SyntaxException {
        throw new SyntaxException();
    }

    private void expr() throws SyntaxException {
        term();
        e_tail();
    }

    private void term() throws SyntaxException {
        if (accept(Scanner.Symbol.num)) {
        } else {
            error();
        }
    }

    private void e_tail() throws SyntaxException {
        if (accept(Scanner.Symbol.plus)) {
            term();
            e_tail();
        } else {
            return;
        }
    }

    private void program() throws SyntaxException {
        getsym();
        expr();
    }

    public boolean parse() {
        try {
            program();
        } catch (SyntaxException e) {
            return false;
        }

        return true;
    }

    public static boolean parseString(String input) {
        Parser parser = new Parser(input);
        return parser.parse();
    }

    public static void main (String[] args) {
        String s;

        s = "1+2+3";
        System.out.print(s + ": ");
        System.out.println(Parser.parseString(s) ? "ok" : "fail");

        s = "1+2++3";
        System.out.print(s + ": ");
        System.out.println(Parser.parseString(s) ? "ok" : "fail");

        s = "";
        System.out.print(s + ": ");
        System.out.println(Parser.parseString(s) ? "ok" : "fail");

        s = "7";
        System.out.print(s + ": ");
        System.out.println(Parser.parseString(s) ? "ok" : "fail");
    }
}
