import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Test;

/**
 * This tests the token recognizing functions. Run as a JUnit test. Uses test
 * document, "TestFile.txt".
 * 
 * @author Eric Fruchter
 * 
 */
public class Homework1Test {

	@Test
	public void test() throws FileNotFoundException {
		testTokenAnalysis();
		testFullDocumentTokenize();
	}

	private void testTokenAnalysis() {
		String t = "";
		String header = "";

		// VAR
		header = "VAR ID FAILURE: ";
		t = "";
		assertFalse(header + t, TokenScanner.isVAR(t));
		t = "aA";
		assertTrue(header + t, TokenScanner.isVAR(t));
		t = "a-A-10";
		assertTrue(header + t, TokenScanner.isVAR(t));
		t = "a1--M";
		assertTrue(header + t, TokenScanner.isVAR(t));
		t = "3a";
		assertFalse(header + t, TokenScanner.isVAR(t));
		t = "-21";
		assertFalse(header + t, TokenScanner.isVAR(t));
		t = "k11-";
		assertTrue(header + t, TokenScanner.isVAR(t));
		t = "0";
		assertFalse(header + t, TokenScanner.isVAR(t));

		// INT
		header = "INT ID FAILURE: ";
		t = "";
		assertFalse(header + t, TokenScanner.isINT(t));
		t = "01";
		assertTrue(header + t, TokenScanner.isINT(t));
		t = "0";
		assertFalse(header + t, TokenScanner.isINT(t));
		t = "018721876578625482765";
		assertTrue(header + t, TokenScanner.isINT(t));
		t = "0hfkjhfwjkhgkwjhegr9870897";
		assertFalse(header + t, TokenScanner.isINT(t));

		// FLOAT
		header = "FLOAT ID FAILURE: ";
		t = "";
		assertFalse(header + t, TokenScanner.isFLOAT(t));
		t = "21.";
		assertTrue(header + t, TokenScanner.isFLOAT(t));
		t = "1.";
		assertTrue(header + t, TokenScanner.isFLOAT(t));
		t = ".6";
		assertFalse(header + t, TokenScanner.isFLOAT(t));
		t = "4";
		assertFalse(header + t, TokenScanner.isFLOAT(t));
		t = "090990800.18721876578625482765";
		assertTrue(header + t, TokenScanner.isFLOAT(t));
		t = "3.hfkjhfwjkhgkwjhegr9870897";
		assertFalse(header + t, TokenScanner.isFLOAT(t));

		// Types
		header = "TYPE ID FAILURE: ";
		t = "090990800.18721876578625482765";
		assertTrue(header + t, TokenScanner.getType(t) == TokenType.FLOAT);
		t = "a090990800.18721876578625482765";
		assertTrue(header + t, TokenScanner.getType(t) == TokenType.INVALID);
		t = "18721876578625482765";
		assertTrue(header + t, TokenScanner.getType(t) == TokenType.INT);
		assertFalse(header + t, TokenScanner.getType(t) == TokenType.INVALID);
		assertFalse(header + t, TokenScanner.getType(t) == TokenType.VAR);
		assertFalse(header + t, TokenScanner.getType(t) == TokenType.FLOAT);
		t = "a18721876578625482765";
		assertTrue(header + t, TokenScanner.getType(t) == TokenType.VAR);
		t = "Aabb-098098duiahinunkdbJhgJHGJHBKJHGkjh76987byYVI";
		assertTrue(header + t, TokenScanner.getType(t) == TokenType.VAR);
	}

	private void testFullDocumentTokenize() throws FileNotFoundException {
		TokenScanner scanner = new TokenScanner(new File("TestFile.txt"));

		scanner.tokenizeAll();

		/*
		 * Demanded Diagnostics
		 */

		System.out.println("\nMost Frequent VARS:");
		for (Token v : scanner.getMostFrequentVARS()) {
			System.out.println(v + ": " + v.frequency);
		}

		System.out.print("\n# of VARS: " + scanner.getTotalVARS());
		System.out.print("\n# of INTS: " + scanner.getTotalINTS());
		System.out.print("\n# of FLOATS: " + scanner.getTotalFLOATS() + "\n");

		System.out.print("\nMin INT: " + scanner.getMinINT());
		System.out.print("\nMax INT: " + scanner.getMaxINT());
		System.out.print("\nMin FLOAT: " + scanner.getMinFLOAT());
		System.out.print("\nMax FLOAT: " + scanner.getMaxFLOAT());

		System.out.println("\n# of times VAR is followed by INT or FLOAT: " + scanner.getVARFollowOccurrences());

		System.out.println("\nQuotes found:");
		for (String quote : scanner.getQuotes()) {
			System.out.println("\"" + quote + "\"");
		}
	}
}
