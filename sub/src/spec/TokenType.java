package spec;

import java.util.Iterator;
import java.util.Map;

public class TokenType {
    private final String name;
    private final String re;

    public TokenType(final String name, final String re, final Iterator charClassesIterator) {
        this.name = name;
        this.re = collapse(re, charClassesIterator);
    }

    private String collapse(String re, Iterator charClassesIterator) {
        while (charClassesIterator.hasNext()) {
            Map.Entry<String, CharClass> entry = (Map.Entry<String, CharClass>)charClassesIterator.next();
            re = re.replace("$" + entry.getKey(), entry.getValue().getRe());
        }

        return RegexExpander.curseAgain(re);
    }

    public String getName() {
        return this.name;
    }

    public String getRe() {
        return this.re;
    }

    @Override
    public String toString() {
        return String.format("<TokenType $%s %s>", this.name, this.re);
    }
}
