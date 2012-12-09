package project.phase2.exc;

import project.scangen.tokenizer.Token;

public class ParseException extends MiniREException {
    private final Token token;

    public ParseException(final String message, final Token token) {
        super(message);
        this.token = token;
    }

    public Token getToken() {
        return token;
    }
}
