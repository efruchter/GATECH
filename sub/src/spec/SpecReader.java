package spec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpecReader {
    private InputStream input;
    private Spec spec;

    public SpecReader(InputStream input) {
        this.input = input;
        this.spec = new Spec();
    }

    private void readCharClass(String line) {
        Pattern p = Pattern.compile("\\$([A-Z\\-]+) ((\\[.*\\])|(\\[\\^.*\\]) IN \\$([A-Z\\-]+))");
        Matcher matcher = p.matcher(line);
        matcher.matches();

        CharClass charClass;

        String charClassName = matcher.group(1);

        if (matcher.group(3) == null) {
            String re = matcher.group(2);
            charClass = new CharClass(charClassName, re);
        } else {
            String re = matcher.group(3);
            String inCharClass = matcher.group(4);
            charClass = new CharClass(charClassName, re, inCharClass);
        }

        spec.addCharClass(charClass);
    }

    private void readTokenDef(String line) {
        Pattern p = Pattern.compile("\\$([A-Z\\-]+) (.*)");
        Matcher matcher = p.matcher(line);
        matcher.matches();

        String tokenName = matcher.group(1);
        String tokenStuff = matcher.group(2);
        TokenDef tokenDef = new TokenDef(tokenName, tokenStuff);

        spec.addTokenDef(tokenDef);
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
