package project.phase2.exc;

import project.scangen.tokenizer.Token;

public class InvalidArgumentException extends MiniRERuntimeException {
    public InvalidArgumentException(final String message) {
        super(message);
    }
}