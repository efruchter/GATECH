package spec;

public class CharClass {
    private final String charClassName;
    private final String re;
    private final String inCharClass;

    public CharClass(String charClassName, String re) {
        this.charClassName = charClassName;
        this.re = re;
        this.inCharClass = null;
    }

    public CharClass(String charClassName, String re, String inCharClass) {
        this.charClassName = charClassName;
        this.re = re;
        this.inCharClass = inCharClass;
    }

    @Override
    public String toString() {
        if (inCharClass == null) {
            return String.format("<CharClass $%s %s>", this.charClassName, this.re);
        } else {
            return String.format("<CharClass $%s %s IN %s>", this.charClassName, this.re, this.inCharClass);
        }
    }
}
