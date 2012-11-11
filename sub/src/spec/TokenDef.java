package spec;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TokenDef {
    private final String name;
    private final String re;

    public TokenDef(final String name, final String re, final Iterator charClassesIterator) {
        this.name = name;
        this.re = collapse(re, charClassesIterator);
    }

    private String collapse(String re, Iterator charClassesIterator) {
        while (charClassesIterator.hasNext()) {
            Map.Entry<String, CharClass> entry = (Map.Entry<String, CharClass>)charClassesIterator.next();
            re = re.replace("$" + entry.getKey(), entry.getValue().getRe());
        }

        return parseFinal(re);
    }

    public String getName() {
        return this.name;
    }

    public String getRe() {
        return this.re;
    }

    @Override
    public String toString() {
        return String.format("<TokenDef $%s %s>", this.name, this.re);
    }

    private static String parseFinal(String s) {
        String f = "";
        char ch, nch;

        for (int i = 0; i < s.length();) {
            if (s.charAt(i) == '\\') {
                f = f.concat(s.substring(i, i + 2));
                i += 2;
            } else if ((i + 1) < s.length() && s.charAt(i) == '['
                    && s.charAt(i + 1) != '^') {
                f = f.concat("(" + s.substring(i, i + 4));
                i += 4;
                while (((i) < s.length()) && (s.charAt(i) != ']')) {
                    f = f.concat("]|[" + s.substring(i, i + 3));
                    i += 3;
                }
                f = f.concat("])");
                i++;
            } else if ((i + 2) < s.length()
                    && s.substring(i, i + 2).equals("[^")) {

                if (s.charAt(i + 3) == '-') {
                    char rlb = s.charAt(i + 2);
                    char rub = s.charAt(i + 4);

                    List<Character> accepts = new ArrayList<Character>();

                    i += 9;

                    while (s.charAt(i) != ']') { // [^A-C]IN[A-La-z]

                        for (char _ch = s.charAt(i); _ch <= s.charAt(i + 2); _ch++)
                            if (_ch < rlb || _ch > rub)
                                accepts.add(((Character) _ch));

                        i += 3;
                    }

                    f = f.concat("(");
                    for (int k = 0; k < accepts.size() - 1; k++)
                        f = f.concat(String.valueOf(accepts.get(k)) + "|");
                    f = f.concat(accepts.get(accepts.size() - 1) + ")");

                    i += 1;

                } else { // Simple case
                    f = f.concat("(");
                    nch = s.charAt(i + 2);
                    for (ch = s.charAt(i + 7); ch < s.charAt(i + 9); ch++)
                        if (ch != nch)
                            f = f.concat(String.valueOf(ch) + "|");
                    ch = s.charAt(i + 9);
                    if (ch != nch)
                        f = f.concat(String.valueOf(ch) + ")");
                    i += 11;
                }

            } else if (s.charAt(i) == '+') {
                if (s.charAt(i - 1) == ']')
                    f = f + s.substring(s.lastIndexOf("[", i), i) + "*";
                else if (s.charAt(i - 1) == ')')
                    f = f + s.substring(s.lastIndexOf("(", i), i) + "*";
                else
                    f = f + "(" + s.substring(i - 1, i) + ")" + "*";
                i++;
            } else {
                f = f.concat(((Character) s.charAt(i)).toString());
                i++;
            }
        }

        return f;
    }
}
