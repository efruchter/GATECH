package spec.test;

import nfa.NFA;
import nfa.NFAUtil;
import nfa.State;
import nfa.Transition;
import org.junit.Test;
import spec.RegexExpander;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RegexExpanderTest {

	@Test
	public void stripOuterParensTest() {
        String test1 = "((((hi))))";
        assertEquals("(hi)", RegexExpander.stripOuterParens(test1));

        String test2 = "(((hi)|(hi)))";
        assertEquals("(((hi)|(hi)))", RegexExpander.stripOuterParens(test2));

        String test3 = "((hi)|(hi))";
        assertEquals("((hi)|(hi))", RegexExpander.stripOuterParens(test3));
    }
}
