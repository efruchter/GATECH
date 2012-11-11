package nfa.test;

import static nfa.NFAUtil.*;
import static nfa.NFAUtil.a;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import nfa.NFA;
import nfa.NFAUtil;
import nfa.State;
import nfa.Transition;

import org.junit.Test;

import java.util.List;

public class NFATest {

    @Test
    public void dfaMinimizeTest() {
        NFASegment d = ab(ab(aStar(a("a")), a("b")), aPlus(aOrB(a("a"), a("b"))));
        d.end.addTransition(Transition.spawnGoal());
        NFA n = new NFA(d.start);
        n = NFAUtil.convertToDFA(n);
        n = NFAUtil.minimizeDFA(n);
        assertTrue("a*b(a|b)+ is not NFA", n.isDFA());
        assertTrue("a*b(a|b)+", NFAUtil.isValid(d, "aaba"));
        assertTrue("a*b(a|b)+", !NFAUtil.isValid(d, "aa"));
        assertTrue("a*b(a|b)+", !NFAUtil.isValid(d, "b"));
    }

    @Test
    public void EClosureTest() {
        State a = new State("a", false);
        State b = new State("b", false);
        State c = new State("c", false);
        State d = new State("d", false);
        State e = new State("e", false);

        a.addTransition(new Transition(b));
        b.addTransition(new Transition(c), new Transition("f", e));
        b.addTransition(new Transition(d));
        c.addTransition(new Transition(d));
        d.addTransition(new Transition("f", e));

        List<State> results = NFAUtil.findClosure(a);

        assertTrue("E-Closure retrieval failed.", results.contains(a) && results.contains(b)
                && results.contains(c) && results.contains(d) && !results.contains(e));
    }

    @Test
    public void nfaBuilderTest() {
        // (a|b)*
        NFAUtil.NFASegment a = NFAUtil.a("a");
        NFAUtil.NFASegment b = NFAUtil.a("b");
        NFAUtil.NFASegment aOrB = NFAUtil.aOrB(a, b);
        NFAUtil.NFASegment total = NFAUtil.aStar(aOrB);
        total.end.addTransition(new Transition(new State("trueEnd", true)));
        assertTrue("(a|b)*", NFAUtil.isValid(total, "ababba"));
        assertTrue("(a|b)*", NFAUtil.isValid(total, ""));

        // a*b(a|b)+
        NFAUtil.NFASegment d = ab(ab(aStar(a("a")), a("b")), aPlus(aOrB(a("a"), a("b"))));
        d.end.addTransition(Transition.spawnGoal());
        assertTrue("a*b(a|b)+", NFAUtil.isValid(d, "aaaaba"));
        assertTrue("a*b(a|b)+", !NFAUtil.isValid(d, "aa"));
        assertTrue("a*b(a|b)+", !NFAUtil.isValid(d, "b"));
    }

    @Test
    public void nfaConverterTest() {
        // a*b(a|b)+
        NFASegment d = ab(ab(aStar(a("a")), a("b")), aPlus(aOrB(a("a"), a("b"))));
        d.end.addTransition(Transition.spawnGoal());
        NFA n = new NFA(d.start);
        n = NFAUtil.convertToDFA(n);
        assertTrue("a*b(a|b)+ is not NFA", n.isDFA());
        assertTrue("a*b(a|b)+", NFAUtil.isValid(d, "aaba"));
        assertTrue("a*b(a|b)+", !NFAUtil.isValid(d, "aa"));
        assertTrue("a*b(a|b)+", !NFAUtil.isValid(d, "b"));

        // (a|b)*
        NFASegment a = NFAUtil.a("a");
        NFASegment b = NFAUtil.a("b");
        NFASegment aOrB = NFAUtil.aOrB(a, b);
        NFASegment total = NFAUtil.aStar(aOrB);
        total.end.addTransition(new Transition(new State("trueEnd", true)));
        NFA daNFA = new NFA(total.start);

        assertFalse("(a|b)* is a dfa", daNFA.isDFA());
        assertTrue("(a|b)*", NFAUtil.isValid(daNFA, "ababba"));
        assertTrue("(a|b)*", NFAUtil.isValid(daNFA, ""));
        assertTrue("(a|b)*", !NFAUtil.isValid(daNFA, "gggab"));

        daNFA = NFAUtil.convertToDFA(daNFA);
        assertTrue("(a|b)* is not a dfa", daNFA.isDFA());
        assertTrue("(a|b)*", NFAUtil.isValid(daNFA, "ababba"));
        assertTrue("(a|b)*", NFAUtil.isValid(daNFA, ""));
        assertTrue("(a|b)*", !NFAUtil.isValid(daNFA, "gggab"));
    }

	@Test
	public void test() {

		// a*b*
		State a = new State("a", true);
		State b = new State("b", true);
		State c = new State("c", false);
		a.addTransition(new Transition("a", a), new Transition("b", b));
		b.addTransition(new Transition("a", c), new Transition("b", b));
		c.addTransition(new Transition("a", c), new Transition("b", c));

		NFA n = new NFA(a);
		n.addState(a, b, c);

		assertTrue("a*b* should be a DFA", n.isDFA());
		assertTrue(
				"a*b*",
				NFAUtil.isValid(n, "ab") && NFAUtil.isValid(n, "a") && NFAUtil.isValid(n, "abbbb")
						&& NFAUtil.isValid(n, "") && NFAUtil.isValid(n, "aaaabbbbb")
						&& !NFAUtil.isValid(n, "bbbbaaaa") && !NFAUtil.isValid(n, "aaaabbbbbbbbba"));

		// (a|b)*(ab)+
		State s = new State("S", false);
		State s0 = new State("0", false);
		State s1 = new State("1", false);
		State s2 = new State("2", true);

		s.addTransition(new Transition("a", s), new Transition("b", s), new Transition(s0));
		s0.addTransition(new Transition("a", s1));
		s1.addTransition(new Transition("b", s2));
		s2.addTransition(new Transition("a", s1));

		n = new NFA(s);
		assertTrue("(a|b)*(ab)+ should not be a DFA", !n.isDFA());
		assertTrue(
				"(a|b)*(ab)+",
				!NFAUtil.isValid(n, "") && NFAUtil.isValid(n, "ab")
						&& !NFAUtil.isValid(n, "babababa") && NFAUtil.isValid(n, "bababab")
						&& NFAUtil.isValid(n, "bababababababbbbbbabababababaaaaaaaaaaabbbbaaab"));
	}

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
		n.addState(a, b, c);

		assertTrue("a(a|b)*b should be a DFA", n.isDFA());
		assertTrue("a(a|b)*b", NFAUtil.isValid(n, "ab") && NFAUtil.isValid(n, "abababab")
				&& NFAUtil.isValid(n, "aabababababbaabbaab") && NFAUtil.isValid(n, "ababb")
				&& NFAUtil.isValid(n, "abbbbbbaaaaaaaabbbbbb") && !NFAUtil.isValid(n, "bbbbaaaa")
				&& !NFAUtil.isValid(n, "aaaabbbbbbbbba"));

		// (a|b)*(ab)+
		State s = State.createState("S", false);
		State s0 = State.createState("0", false);
		State s1 = State.createState("1", false);
		State s2 = State.createState("2", true);

		s.addTransition(Transition.createTransition("a", s), Transition.createTransition("b", s),
				Transition.createEmptyTransition(s0));
		s0.addTransition(Transition.createTransition("a", s1));
		s1.addTransition(Transition.createTransition("b", s2));
		s2.addTransition(Transition.createTransition("a", s1));

		n = NFA.createNFA(s);
		assertTrue("(a|b)*(ab)+ should not be a DFA", !n.isDFA());
		assertTrue(
				"(a|b)*(ab)+",
				!NFAUtil.isValid(n, "") && NFAUtil.isValid(n, "ab")
						&& !NFAUtil.isValid(n, "babababa") && NFAUtil.isValid(n, "bababab")
						&& NFAUtil.isValid(n, "bababababababbbbbbabababababaaaaaaaaaaabbbbaaab"));
	}
}
