package spec;

import java.util.*;

public class Spec {
    private Map<String, CharClass> charClasses;
    private List<TokenType> tokenTypes;

    public Spec() {
        charClasses = new HashMap<String, CharClass>();
        tokenTypes = new ArrayList<TokenType>();
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

    public List<TokenType> getTokenTypes() {
        return tokenTypes;
    }

    public void addTokenDef(final TokenType tokenType) {
        tokenTypes.add(tokenType);
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

        for (TokenType tokenType : tokenTypes) {
            sb.append(tokenType.toString());
            sb.append("\n");
        }

        return sb.toString();
    }
}
