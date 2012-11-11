package spec;

import java.util.Map;

public class TokenType {
    private final String name;
    private final String re;

    public TokenType(final String name, final String re, final Map<String, CharClass> charClasses) {
        this.name = name;
        this.re = collapse(re, charClasses);
    }

    public String getName() {
        return this.name;
    }

    public String getRe() {
        return this.re;
    }

    private String collapse(String re, Map<String, CharClass> charClasses) {
        for (Map.Entry<String, CharClass> entry : charClasses.entrySet()) {
            re = re.replace("$" + entry.getKey(), entry.getValue().getRe());
        }

        return RegexExpander.expandRegex(re);
    }

    @Override
    public String toString() {
        return String.format("<TokenType $%s %s>", this.name, this.re);
    }
}
