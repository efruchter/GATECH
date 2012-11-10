package spec;

public class CharClass {
    private final String re;
    private final CharClass inCharClass;

    public CharClass(String re) {
        this.re = re;
        this.inCharClass = null;
    }

    public CharClass(String re, CharClass inCharClass) {
        this.re = re;
        this.inCharClass = inCharClass;
    }

    public boolean match(final String input) {
        return true;
    }

    @Override
    public String toString() {
        if (inCharClass == null) {
            return String.format("<CharClass %s>", this.re);
        } else {
            return String.format("<CharClass %s IN %s>", this.re, this.inCharClass);
        }
    }
}
