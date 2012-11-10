package spec;

import java.util.ArrayList;
import java.util.List;

public class Spec {
    private List<CharClass> charClasses;
    private List<TokenDef> tokenDefs;

    public Spec() {
        charClasses = new ArrayList<CharClass>();
        tokenDefs = new ArrayList<TokenDef>();
    }

    public void addCharClass(final CharClass charClass) {
        charClasses.add(charClass);
    }

    public void addTokenDef(final TokenDef tokenDef) {
        tokenDefs.add(tokenDef);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (CharClass charClass : charClasses) {
            sb.append(charClass.toString());
            sb.append("\n");
        }

        for (TokenDef tokenDef : tokenDefs) {
            sb.append(tokenDef.toString());
            sb.append("\n");
        }

        return sb.toString();
    }
}
