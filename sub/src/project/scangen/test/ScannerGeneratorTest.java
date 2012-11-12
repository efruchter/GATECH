package project.scangen.test;

import org.junit.Test;
import project.scangen.ScannerGenerator;
import project.scangen.tokenizer.Token;
import project.scangen.tokenizer.Tokenizer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ScannerGeneratorTest {

    private class ScannerGeneratorTestCase {
        public final String spec, input, output;

        public ScannerGeneratorTestCase(final String spec, final String input, final String output) {
            this.spec = spec;
            this.input = input;
            this.output = output;
        }
    }

    private final List<ScannerGeneratorTestCase> testCases = new ArrayList<ScannerGeneratorTestCase>() {{
        add(new ScannerGeneratorTestCase(
                "\n$DIGIT 0",
                "000",
                "DIGIT 0\nDIGIT 0\nDIGIT 0\n"
        ));
        add(new ScannerGeneratorTestCase(
                "$DIGIT [0-3]\n$CHAR [a-c]\n\n$INT $DIGIT+\n$WORD $CHAR+",
                "abc 12 3cba0",
                "WORD abc\nINT 12\nINT 3\nWORD cba\nINT 0\n"
        ));
        add(new ScannerGeneratorTestCase(
                "$DIGIT [0-9]\n$NON-ZERO [^0] IN $DIGIT\n$CHAR [a-z]\n\n" +
                        "$INT $NON-ZERO $DIGIT*\n$VAR $CHAR ($CHAR | $DIGIT)*",
                "123 0123 6abc abc67",
                "INT 123\nINT 123\nINT 6\nVAR abc\nVAR abc67\n"
        ));
    }};

    private void runScannerGeneratorTest(ScannerGeneratorTestCase testCase) {
        InputStream specInputStream = new ByteArrayInputStream(testCase.spec.getBytes());
        InputStream inputInputStream = new ByteArrayInputStream(testCase.input.getBytes());
        ScannerGenerator scannerGenerator = new ScannerGenerator(specInputStream, inputInputStream);
        Tokenizer tokenizer = scannerGenerator.generateTokenizer();

        StringBuilder sb = new StringBuilder();

        for (Token token : tokenizer) {
            sb.append(token).append("\n");
        }

        assertEquals(testCase.output, sb.toString());
    }

    @Test
    public void testScannerGenerator() {
        for (ScannerGeneratorTestCase testCase : testCases) {
            runScannerGeneratorTest(testCase);
        }
    }
}
