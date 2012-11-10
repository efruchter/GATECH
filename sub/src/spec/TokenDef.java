package spec;

public class TokenDef {
    private final String tokenName;
    private final String tokenStuff;

    public TokenDef(final String tokenName, final String tokenStuff) {
        this.tokenName = tokenName;
        this.tokenStuff = tokenStuff;
    }

    @Override
    public String toString() {
        return String.format("<TokenDef $%s %s>", this.tokenName, this.tokenStuff);
    }
}
