package spec.test;

import org.junit.Test;
import spec.RegexExpander;

import static org.junit.Assert.assertEquals;

public class RegexExpanderTest {

	@Test
	public void testStripOuterParens() {
        String test1 = "((((hi))))";
        assertEquals("(hi)", RegexExpander.stripOuterParens(test1));

        String test2 = "(((hi)|(hi)))";
        assertEquals("(((hi)|(hi)))", RegexExpander.stripOuterParens(test2));

        String test3 = "((hi)|(hi))";
        assertEquals("((hi)|(hi))", RegexExpander.stripOuterParens(test3));
    }

    @Test
    public void testRegexExpander() {
        assertEquals("((u|y|g|f|c|x|d|u|y)|(g|h|i|j|k|l))", RegexExpander.expandRegex("[uygfcxduyg-l]"));
    }
}
