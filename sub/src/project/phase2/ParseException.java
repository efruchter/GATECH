package project.phase2;

import project.scangen.tokenizer.Token;

public class ParseException extends Exception {
    private final Token token;

    public ParseException(final String message, final Token token) {
        super(message);
        this.token = token;
    }

    public Token getToken() {
        return token;
    }
}
