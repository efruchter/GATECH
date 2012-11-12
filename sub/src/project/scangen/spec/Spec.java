package project.scangen.spec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Spec {
    private Map<String, CharClass> charClasses;
    private List<TokenType> tokenTypes;

    public Spec() {
        charClasses = new HashMap<String, CharClass>();
        tokenTypes = new ArrayList<TokenType>();
    }

    public Map<String, CharClass> getCharClasses() {
        return charClasses;
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

    public void addTokenType(final TokenType tokenType) {
        tokenTypes.add(tokenType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry entry : charClasses.entrySet()) {
            sb.append(String.format("$%s: %s\n", entry.getKey(),
                    entry.getValue()));
        }

        for (TokenType tokenType : tokenTypes) {
            sb.append(tokenType.toString()).append("\n");
        }

        return sb.toString();
    }
}
