package spec;

import java.util.*;

public class Spec {
    private Map<String, CharClass> charClasses;
    private List<TokenDef> tokenDefs;

    public Spec() {
        charClasses = new HashMap<String, CharClass>();
        tokenDefs = new ArrayList<TokenDef>();
    }

    public Iterator iterCharClasses() {
        return charClasses.entrySet().iterator();
    }

    public CharClass getCharClass(final String charClassName) {
        return charClasses.get(charClassName);
    }

    public void addCharClass(final String charClassName, final CharClass charClass) {
        charClasses.put(charClassName, charClass);
    }

    public List<TokenDef> getTokenDefs() {
        return tokenDefs;
    }

    public void addTokenDef(final TokenDef tokenDef) {
        tokenDefs.add(tokenDef);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        Iterator charClassesIterator = charClasses.entrySet().iterator();
        while (charClassesIterator.hasNext()) {
            Map.Entry entry = (Map.Entry) charClassesIterator.next();
            sb.append(String.format("$%s: %s\n", entry.getKey(),
                    entry.getValue()));
        }

        for (TokenDef tokenDef : tokenDefs) {
            sb.append(tokenDef.toString());
            sb.append("\n");
        }

        return sb.toString();
    }
}
