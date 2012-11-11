package nfa;

import nfa.NFAUtil.*;
import spec.Spec;
import spec.TokenType;

import java.util.List;

public class NFABuilder {
    public static NFASegment buildNFAFromSpec(Spec spec) {
        List<TokenType> tokenTypes = spec.getTokenTypes();
        NFASegment segments[] = new NFASegment[tokenTypes.size()];

        for (int i = 0; i < tokenTypes.size(); ++i) {
            TokenType tokenType = tokenTypes.get(i);
            segments[i] = buildNFAFromRegex(tokenType.getRe());
        }

        NFASegment nfa = NFAUtil.aOrB(segments);
        nfa.end.addTransition(new Transition(new State("trueEnd", true)));
        return nfa;
    }

    private static NFASegment buildNFAFromRegex(String regex) {
        NFASegment nfa = NFAUtil.empty();

        int idx = 0;
        while (idx < regex.length()) {
            char c = regex.charAt(idx);
            ++idx;

            if (c == '(') {
                int ct = 1;
                int subidx = idx;
                while (ct > 0) {
                    char paren = regex.charAt(subidx);
                    if (paren == '(') {
                        ++ct;
                    } else if (paren == ')') {
                        --ct;
                    }
                    ++subidx;
                }
                nfa = NFAUtil.ab(nfa, buildNFAFromRegex(regex.substring(idx, subidx - 1)));
                idx = subidx;
            } else if (c == '|') {
                nfa = NFAUtil.aOrB(nfa, buildNFAFromRegex(regex.substring(idx)));
                idx = regex.length();
            } else if (c == '*') {
                nfa = NFAUtil.aStar(nfa);
            } else if (c == '\\') {
                // Wait for next char
            } else {
                nfa = NFAUtil.ab(nfa, NFAUtil.a(String.valueOf(c)));
            }
        }

        return nfa;
    }
}
