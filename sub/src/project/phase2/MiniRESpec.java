package project.phase2;

public class MiniRESpec {
    public static final String spec =
            "$LETTER [a-zA-Z]\n" +
            "$DIGIT [0-9]\n" +
            "$ASCII [ -~]\n" +
            "$NO-DOUBLE-QUOTE-ASCII [^\"] IN $ASCII\n" +
            "$NO-SINGLE-QUOTE-ASCII [^'] IN $ASCII\n" +
            "\n" +
            "$EQUALS =\n" +
            "$OPEN-PAREN \\(\n" +
            "$CLOSE-PAREN \\)\n" +
            "$SEMICOLON ;\n" +
            "$GREATER-BANG >!\n" +
            "$OCTOTHORPE #\n" +
            "$COMMA ,\n" +
            "$ID-OR-KEYWORD $LETTER ($LETTER|$DIGIT|_)*\n" +
            "$ASCII-STRING \" ($NO-DOUBLE-QUOTE-ASCII|\\\\\")* \"\n" +
            "$REGEX ' ($NO-SINGLE-QUOTE-ASCII|\\\\')* '";
}
