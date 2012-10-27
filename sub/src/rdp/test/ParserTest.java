package rdp.test;

import org.junit.Test;
import rdp.Parser;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ParserTest {

    private static boolean parseString(String input) {
        Parser parser = new Parser(input);
        return parser.parse();
    }

    @Test
    public void basicTests() {
        String validPrograms[] = {
                "1+2+3",
                "7",
        };

        String invalidPrograms[] = {
                "1+2++3",
                "",
        };

        for (String program : validPrograms) {
            assertTrue(parseString(program));
        }

        for (String program : invalidPrograms) {
            assertFalse(parseString(program));
        }
    }
}
