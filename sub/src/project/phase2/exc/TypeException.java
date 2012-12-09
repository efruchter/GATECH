package project.phase2.exc;

import project.scangen.tokenizer.Token;

public class TypeException extends MiniRERuntimeException {
    public TypeException(final String message) {
        super(message);
    }
}