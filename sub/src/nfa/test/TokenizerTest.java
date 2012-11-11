package nfa.test;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.StringReader;

import nfa.NFA;
import nfa.NFAUtil;
import nfa.State;
import nfa.Token;
import nfa.Tokenizer;
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
		b.addTransition(Transition.createTransition("a", b),
				Transition.createTransition("b", c));
		c.addTransition(Transition.createTransition("a", b),
				Transition.createTransition("b", c));

		NFA n = NFA.createNFA(a, b, c); //
		//n.addState(a, b, c);
		assertTrue("a(a|b)*b should be a DFA", n.isDFA());
		assertTrue("a(a|b)*b", NFAUtil.isValid(n, "ab") && NFAUtil.isValid(n, "abababab")
				&& NFAUtil.isValid(n, "aabababababbaabbaab") && NFAUtil.isValid(n, "ababb")
				&& NFAUtil.isValid(n, "abbbbbbaaaaaaaabbbbbb") && !NFAUtil.isValid(n, "bbbbaaaa")
				&& !NFAUtil.isValid(n, "aaaabbbbbbbbba"));
		
		String s = "aabababbbb  abb baaba ab aaaaaaaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb";
		BufferedReader br = new BufferedReader(new StringReader(s));
		Tokenizer tokenizer = new Tokenizer(n, br);
		Token t = tokenizer.getNextToken();
		System.out.println(t);
		System.out.println(tokenizer.getNextToken());
		System.out.println(tokenizer.getNextToken());
		System.out.println(tokenizer.getNextToken());
		System.out.println(tokenizer.getNextToken());
		System.out.println(tokenizer.getNextToken());
		System.out.println(tokenizer.getNextToken());
	}
	
}
