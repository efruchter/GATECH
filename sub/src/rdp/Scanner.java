package rdp;

/**
 * This scanner is a complete fraud. It requires that all tokens are exactly
 * one character (made it just for a basic test RDP implementation). I think
 * we're going to have to replace this with a 'real' scanner, a la the first
 * project.
 */
public class Scanner {

    public enum Symbol {
        plus,
        num
    };

    private final String input;
    private int idx;

    public Scanner(final String input) {
        this.input = input;
        this.idx = 0;
    }

    public Symbol nextSymbol() {
        if (idx > input.length() - 1) {
            return null;
        }

        char c = input.charAt(idx);
        idx++;

        if (c == '+') {
            return Symbol.plus;
        } else if (c >= '0' && c <= '9') {
            return Symbol.num;
        } else {
            throw new RuntimeException();
        }
    }
}
