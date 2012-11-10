package spec.test;

import org.junit.Test;
import rdp.Parser;
import spec.Spec;
import spec.SpecReader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SpecReaderTest {

    final private static String sampleSpec =
            "$DIGIT [0-9]\n" +
            "$NON-ZERO [^0] IN $DIGIT\n" +
            "$CHAR [a-zA-Z]\n" +
            "$UPPER [^a-z] IN $CHAR\n" +
            "$LOWER [^A-Z] IN $CHAR\n" +
            "\n" +
            "$IDENTIFIER $LOWER ($LOWER|$DIGIT)*\n" +
            "$INT ($DIGIT)+\n" +
            "$FLOAT ($DIGIT)+ \\. ($DIGIT)+\n" +
            "$ASSIGN =\n" +
            "$PLUS \\+\n" +
            "$MINUS -\n" +
            "$MULTIPLY \\*\n" +
            "$PRINT PRINT";

    @Test
    public void test() {
        InputStream input = new ByteArrayInputStream(sampleSpec.getBytes());
        SpecReader specReader = new SpecReader(input);
        Spec spec = specReader.specify();
        System.out.println(spec);
    }
}
