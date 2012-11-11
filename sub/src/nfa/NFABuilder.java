package nfa;

import nfa.NFAUtil.*;
import spec.Spec;
import spec.TokenDef;

import java.util.List;

public class NFABuilder {
    public static NFASegment buildNFAFromSpec(Spec spec) {
        List<TokenDef> tokenTypes = spec.getTokenDefs();
        NFASegment segments[] = new NFASegment[tokenTypes.size()];

        for (int i = 0; i < tokenTypes.size(); ++i) {
            TokenDef tokenType = tokenTypes.get(i);
            segments[i] = buildNFAFromRegex(tokenType.getRe());
        }

        NFASegment nfa = NFAUtil.aOrB(segments);
        nfa.end.addTransition(new Transition(new State("trueEnd", true)));
        return nfa;
    }

    public static NFASegment buildNFAFromRegex(String regex) {
        NFASegment a = NFAUtil.a("a");
        NFASegment b = NFAUtil.a("b");
        NFASegment aOrB = NFAUtil.aOrB(a, b);
        NFASegment total = NFAUtil.aPlus(aOrB);
        total.end.addTransition(new Transition(new State("trueEnd", true)));
        return total;
    }
}
