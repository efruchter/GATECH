package project.scangen.spec;

import project.scangen.regex.RegexExpander;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpecReader {
    private InputStream input;
    private Spec spec;

    private final String CHAR_CLASS_NAME_RE = "\\$([A-Z_\\-]+)";
    private final String TOKEN_TYPE_NAME_RE = "\\$([A-Z_\\-]+)";

    public SpecReader(InputStream input) {
        this.input = input;
        this.spec = new Spec();
    }

    private void readCharClass(String line) {
        Pattern p = Pattern.compile(CHAR_CLASS_NAME_RE + " ((\\[.*\\])|(\\[\\^.*\\]) IN " + CHAR_CLASS_NAME_RE + ")");
        Matcher matcher = p.matcher(line);
        matcher.matches();

        String charClassName = matcher.group(1);

        String re = null;
        if (matcher.group(3) != null) {
            re = matcher.group(2);
        } else {
            String negate = matcher.group(4);
            CharClass inCharClass = spec.getCharClass(matcher.group(5));
            re = RegexExpander.expandRegex(String.format("%sIN$%s", negate, inCharClass.getRe()));
        }
        spec.addCharClass(charClassName, new CharClass(re));
    }

    private void readTokenDef(String line) {
        Pattern p = Pattern.compile(TOKEN_TYPE_NAME_RE + " (.*)");
        Matcher matcher = p.matcher(line);
        matcher.matches();

        String name = matcher.group(1);
        String re = matcher.group(2);
        TokenType tokenType = new TokenType(name, re, spec.getCharClasses());

        spec.addTokenType(tokenType);
    }

    public Spec specify() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        try {
            String line;
            boolean inCharClassSection = true;

            while ((line = reader.readLine()) != null) {
                if (line.equals("")) {
                    inCharClassSection = false;
                    continue;
                }

                if (inCharClassSection) {
                    readCharClass(line);
                } else {
                    readTokenDef(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }

        return spec;
    }
}
