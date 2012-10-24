package nfa.test;

import static org.junit.Assert.assertTrue;
import nfa.NFA;
import nfa.NFASolver;
import nfa.State;
import nfa.Transition;

import org.junit.Test;

public class NFATest {

	@Test
	public void test() {

		// a*b*
		State a = new State("a", true);
		State b = new State("b", true);
		State c = new State("c", false);
		a.addTransition(new Transition('a', a), new Transition('b', b));
		b.addTransition(new Transition('a', c), new Transition('b', b));
		c.addTransition(new Transition('a', c), new Transition('b', c));

		NFA n = new NFA(a);
		n.addState(a, b, c);

		NFASolver solver = new NFASolver(n);
		assertTrue("a*b* should be a DFA", n.isDFA());
		assertTrue("a*b*", solver.isValid("ab") && solver.isValid("a") && solver.isValid("abbbb")
				&& solver.isValid("") && solver.isValid("aaaabbbbb") && !solver.isValid("bbbbaaaa")
				&& !solver.isValid("aaaabbbbbbbbba"));

		// (a|b)*(ab)+
		State s = new State("S", false);
		State s0 = new State("0", false);
		State s1 = new State("1", false);
		State s2 = new State("2", true);

		s.addTransition(new Transition('a', s), new Transition('b', s), new Transition(s0));
		s0.addTransition(new Transition('a', s1));
		s1.addTransition(new Transition('b', s2));
		s2.addTransition(new Transition('a', s1));

		n = new NFA(s, s0, s1, s2);
		solver = new NFASolver(n);
		assertTrue("(a|b)*(ab)+ should not be a DFA", !n.isDFA());
		assertTrue(
				"(a|b)*(ab)+",
				!solver.isValid("") && solver.isValid("ab") && !solver.isValid("babababa")
						&& solver.isValid("bababab")
						&& solver.isValid("bababababababbbbbbabababababaaaaaaaaaaabbbbaaab"));
	}
}
