package test;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import org.junit.Test;
import scannergenerator.ScannerGenerator;
import spec.RegexExpander;
import tokenizer.Token;
import tokenizer.Tokenizer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class ScannerGeneratorTest {

    private final String simpleSpec = "\n\n$INT 0";
    private final String simpleInput = "\n\n$INT 0";
    private final String simpleOutput = "\n\n$INT 0";
    private final String complexSpec = "\n\n$INT 0";
    private final String complexInput = "\n\n$INT 0";
    private final String complexOutput = "\n\n$INT 0";

    private void runScannerGeneratorTest(final String spec, final String input, final String output) {
        InputStream specInputStream = new ByteArrayInputStream(spec.getBytes());
        InputStream inputInputStream = new ByteArrayInputStream(input.getBytes());
        ScannerGenerator scannerGenerator = new ScannerGenerator(specInputStream, inputInputStream);
        Tokenizer tokenizer = scannerGenerator.generateTokenizer();

        StringBuilder sb = new StringBuilder();

        Token token;
        while ((token = tokenizer.getNextToken()) != null) {
            sb.append(token.toString()).append("\n");
        }

        assertEquals(output, sb.toString());
    }

    @Test
    public void testScannerGenerator() {
        runScannerGeneratorTest(simpleSpec, simpleInput, simpleOutput);
        runScannerGeneratorTest(complexSpec, complexInput, complexOutput);
    }
}
