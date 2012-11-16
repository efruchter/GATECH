Code Organization
=================

All code is writtein in Java and resides in the `src/` directory.

- `project.scangen.ScannerGenerator`:
    - The application entry point. Given a spec file `InputStream` and input file `InputStream`, `generateTokenizer()`:
        - Uses `SpecReader` to create a `Spec` object.
        - Calls `NFABuilder` with the `Spec` object to construct an NFA.
        - Calls `NFAUtil.convertToDFA` with the NFA to construct the DFA.
        - Creates and returns a `Tokenizer` given the DFA and input file `InputStream`.

- `project.scangen.spec`:
    - `SpecReader` class takes an `InputStream` of a spec file and returns a `Spec` object.
        - When creating token types, it replaces '$CHARCLASS' expressions with the actual regex.
    - `Spec` class keeps a map of `CharClass` objects and `List` of `TokenType` objects.
    - `CharClass` is a container for a raw regex character class.
    - `TokenType` is a container for a token type name and fully expanded and simplified regex.

- `project.scangen.regex.RegexExpander`:
    - Takes a raw complete regex (no '$CHARCLASS' instances) and returns a fully expanded regex:
        - Contains only the special characters (, ), |, and +.
        - Eg. turns [1-3][a-e]* into (1|2|3)(a|b|c|d|e)(a|b|c|d|e)+

- `project.scangen.nfa.NFABuilder`:
    - Takes a `List` of `TokenType` objects, creates an `NFASegment` for each of their expanded regexes, and combines them into a full NFA.

- `project.scangen.tokenizer.Tokenizer`:
    - Takes a DFA and an input file `InputStream`, and implements `Iterable` returning `Token` objects.
    - For each line in the input file `InputStream`, it walks the DFA, yielding the longest matching token.

- `project.nfa`:
    - Provides a utility class `NFAUtil` which has convenience methods for creating NFA segments using `State` and `Transition` objects, as well as conversion from NFA to DFA.
    - Not in the `project.scangen` package since its logic is decoupled from the scanner generator part of the project.
