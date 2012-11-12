package tokenizer.test;

import nfa.NFA;
import nfa.NFAUtil;
import nfa.State;
import nfa.Transition;
import org.junit.Test;
import tokenizer.Token;
import tokenizer.Tokenizer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Iterator;

import static nfa.NFAUtil.*;
import static org.junit.Assert.assertTrue;

public class TokenizerTest {

    @Test
    public void FactoryMethodTests() {
        // a(a|b)*b
        State a = State.createState("a", false);
        State b = State.createState("b", false);
        State c = State.createState("c", true);
        a.addTransition(Transition.createTransition("a", b));
        b.addTransition(Transition.createTransition("a", b), Transition.createTransition("b", c));
        c.addTransition(Transition.createTransition("a", b), Transition.createTransition("b", c));

        NFA n = NFA.createNFA(a); //
        // n.addState(a, b, c);

        assertTrue("a(a|b)*b should be a DFA", n.isDFA());
        assertTrue(
                "a(a|b)*b",
                NFAUtil.isValid(n, "ab") && NFAUtil.isValid(n, "abababab") && NFAUtil.isValid(n, "aabababababbaabbaab")
                        && NFAUtil.isValid(n, "ababb") && NFAUtil.isValid(n, "abbbbbbaaaaaaaabbbbbb")
                        && !NFAUtil.isValid(n, "bbbbaaaa") && !NFAUtil.isValid(n, "aaaabbbbbbbbba"));
        String s = "aabababbbb  abb baaba ab aaaaaaaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb";
        InputStream input = new ByteArrayInputStream(s.getBytes());
        Tokenizer tokenizer = new Tokenizer(n, input);
        Iterator<Token> tokenIterator = tokenizer.iterator();

        // Not sure what it supposed to be tested here
        for (int i = 0; i < 6; ++i) {
            tokenIterator.next();
        }

        // ab+c
        NFASegment d = ab(ab(a("a"), aPlus(a("b"))), a("c"));
        d.end.addTransition(Transition.spawnGoal("Valid"));
        NFA nyet = new NFA(d.start);
        nyet = NFAUtil.convertToDFA(nyet);
        s = "abbcabcabbbbbbbcabbbbbbbbbbbbcaaabbbc";
        input = new ByteArrayInputStream(s.getBytes());
        tokenizer = new Tokenizer(nyet, input);
        tokenIterator = tokenizer.iterator();
        Token latest;
        do {
            latest = tokenIterator.next();
        } while (latest != null);
    }
}
