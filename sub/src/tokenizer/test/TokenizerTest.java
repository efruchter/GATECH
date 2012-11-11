package tokenizer.test;

import static nfa.NFAUtil.a;
import static nfa.NFAUtil.aPlus;
import static nfa.NFAUtil.ab;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import nfa.NFA;
import nfa.NFAUtil;
import nfa.NFAUtil.NFASegment;
import nfa.State;
import tokenizer.Token;
import tokenizer.Tokenizer;
import nfa.Transition;

import org.junit.Test;

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
		assertTrue("a(a|b)*b", NFAUtil.isValid(n, "ab") && NFAUtil.isValid(n, "abababab")
				&& NFAUtil.isValid(n, "aabababababbaabbaab") && NFAUtil.isValid(n, "ababb")
				&& NFAUtil.isValid(n, "abbbbbbaaaaaaaabbbbbb") && !NFAUtil.isValid(n, "bbbbaaaa")
				&& !NFAUtil.isValid(n, "aaaabbbbbbbbba"));
		String s = "aabababbbb  abb baaba ab aaaaaaaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb";
		InputStream input = new ByteArrayInputStream(s.getBytes());
		Tokenizer tokenizer = new Tokenizer(n, input);
		Token t = tokenizer.getNextToken();

		System.out.println(t);
		System.out.println(tokenizer.getNextToken());
		System.out.println(tokenizer.getNextToken());
		System.out.println(tokenizer.getNextToken());
		System.out.println(tokenizer.getNextToken());
		System.out.println(tokenizer.getNextToken());
		System.out.println(tokenizer.getNextToken());

		// ab+c
		NFASegment d = ab(ab(a("a"), aPlus(a("b"))), a("c"));
		d.end.addTransition(Transition.spawnGoal("Valid"));
		NFA nyet = new NFA(d.start);
		nyet = NFAUtil.convertToDFA(nyet);
		System.out.println(nyet.toString());
		s = "abbcabcabbbbbbbcabbbbbbbbbbbbc";
		input = new ByteArrayInputStream(s.getBytes());
		tokenizer = new Tokenizer(nyet, input);
		Token latest = null;
		System.out.println("Stuff below:");
		do {
			latest = tokenizer.getNextToken();
			System.out.println(latest);
		} while (latest != null);
		
		System.out.println(nyet);

	}
}
