package nfa.test;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import nfa.NFA;
import nfa.NFAUtil;
import nfa.NFAUtil.NFASegment;
import nfa.State;
import nfa.Transition;

import org.junit.Test;

public class NFAUtilTest {

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

		Set<State> results = NFAUtil.findClosure(a);

		assertTrue("E-Closure retrieval failed.", results.contains(a) && results.contains(b)
				&& results.contains(c) && results.contains(d) && !results.contains(e));
	}

	@Test
	public void NFAMakerTest() {

		// (a|b)*
		NFASegment a = NFAUtil.a("a");
		NFASegment b = NFAUtil.a("b");
		NFASegment aOrB = NFAUtil.aOrB(a, b);
		NFASegment total = NFAUtil.aStar(aOrB);
		total.end.addTransition(new Transition(new State("end", true)));
		System.out.println(NFAUtil.isValid(total, "abahba"));

	}
}
