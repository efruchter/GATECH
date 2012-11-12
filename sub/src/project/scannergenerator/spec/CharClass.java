package project.scannergenerator.spec;

public class CharClass {
    private final String re;

    public CharClass(String re) {
        this.re = re;
    }

    @Override
    public String toString() {
        return String.format("<CharClass %s>", this.re);
    }

    public String getRe() {
        return re;
    }
}
