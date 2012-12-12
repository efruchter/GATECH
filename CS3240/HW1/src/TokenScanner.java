import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Consumptive document scanner. Capable of basic analysis. You may tokenize
 * everything at startup, and use getTokens(), or you may request single,
 * consumed tokens one at a time using getNextValidToken().
 * 
 * @author Eric Fruchter
 * 
 */
public class TokenScanner {

	/**
	 * Single line of file, latest grabbed from Scanner.
	 */
	private String latestLine = "";

	/**
	 * The file being scanned.
	 */
	private File file;

	/**
	 * The File reader for the document.
	 */
	private final Scanner fileScanner;

	/**
	 * LEXICONS
	 */
	final static String az = "abcdefghijklmnopqrstuvwxyz";
	final static String AZ = az.toUpperCase();
	final static String digits = "0123456789";
	final static String hyphen = "-";
	final static String space = " ";
	final static String dot = ".";
	final static String punctuation = dot + ",!?";
	final static String quote = "\"";

	/**
	 * Load a document into the scanner.
	 * 
	 * @param f
	 *            the file.
	 * @throws FileNotFoundException
	 */
	public TokenScanner(final File f) throws FileNotFoundException {
		fileScanner = new Scanner(file = f);
	}

	/**
	 * Consume a raw string token and return it.
	 * 
	 * @return a raw token, if it exists.
	 */
	private Token nextRawToken() throws NoSuchElementException {
		// Location of the char after the current projected token.
		int sLoc = 0;

		while (latestLine.trim().isEmpty()) {
			latestLine = fileScanner.nextLine();
		}

		// Find the next end segment
		while (latestLine.length() != sLoc + 1
				&& latestLine.charAt(sLoc + 1) != ' ') {
			// && getType(document.substring(0, sLoc + 1)) == TokenType.INVALID)
			// {
			sLoc++;
		}

		// End indices of the token, inclusive.
		int endTestToken = sLoc;

		/*
		 * Determine the longest sub-token by starting from the end and working
		 * backwards.
		 */
		boolean valid = false;
		for (int i = endTestToken; i >= 0; i--) {
			String testToken = latestLine.substring(0, i + 1);
			if (getType(testToken) != TokenType.INVALID) {
				sLoc = i;
				valid = true;
				break;
			}
		}
		if (!valid) {
			sLoc = 0;
		}

		String token = latestLine.substring(0, sLoc + 1);

		Token t = new Token(token, getType(token));
		runDiagnostic(t);

		// Consume the token form the document
		latestLine = latestLine.substring(sLoc + 1).trim();

		return t;
	}

	/**
	 * Diagnostic info.
	 */
	private Token prevToken = new Token("INVALID", TokenType.INVALID);
	final private HashMap<Token, Integer> VAR_FREQUENCY = new HashMap<Token, Integer>();
	private int totalVARS = 0, totalINTS = 0, totalFLOATS = 0;
	private Token minINT, maxINT, minFLOAT, maxFLOAT;
	private int varFollowOccurrences = 0;
	private boolean inQuote = false;

	/**
	 * Retrieve the next valid token. INVALID tokens will be ignored.
	 * 
	 * @return the next valid Token.
	 * @throws NoSuchElementException
	 *             when no more tokens are left in document.
	 */
	public Token getNextValidToken() {
		Token t = null;
		do {
			t = nextRawToken();
		} while (t.TYPE == TokenType.INVALID);
		return t;
	}

	/**
	 * Get a list of the most frequent tokens, in order of frequency. Each token
	 * has it's frequency field properly filled in.
	 * 
	 * @param k
	 *            top k occurences desired.
	 * @return Ordered list of tokens in order of frequency. Check the frequncy
	 *         fields of each token for exact number of occurrences.
	 */
	public List<Token> getMostFrequentVARS(final int k) {
		List<Token> f = new LinkedList<Token>();
		for (Entry<Token, Integer> s : VAR_FREQUENCY.entrySet()) {
			Token t = s.getKey();
			t.frequency = s.getValue();
			f.add(t);
		}
		Comparator<Token> c = new Comparator<Token>() {
			@Override
			public int compare(Token a, Token b) {
				return new Integer(b.frequency).compareTo(new Integer(
						a.frequency));
			}
		};
		Collections.sort(f, c);
		if (k > 0 && f.size() > k) {
			List<Token> nf = new LinkedList<Token>();
			for (int i = 0; i < k; i++) {
				nf.add(f.get(i));
			}
			return nf;
		}
		return f;
	}

	public List<Token> getMostFrequentVARS() {
		return getMostFrequentVARS(-1);
	}

	/**
	 * Get total number of INTS
	 */
	public int getTotalINTS() {
		return totalINTS;
	}

	/**
	 * Get total number of FLOATS
	 */
	public int getTotalFLOATS() {
		return totalFLOATS;
	}

	/**
	 * Get total number of VARS
	 */
	public int getTotalVARS() {
		return totalVARS;
	}

	public Token getMinINT() {
		return minINT;
	}

	public Token getMaxINT() {
		return maxINT;
	}

	public Token getMinFLOAT() {
		return minFLOAT;
	}

	public Token getMaxFLOAT() {
		return maxFLOAT;
	}

	/**
	 * Get the amount of VARs followed immediately by INT or FLOAT.
	 * 
	 */
	public int getVARFollowOccurrences() {
		return varFollowOccurrences;
	}

	/**
	 * Get a List of all the quoted content in the document, unmodified.
	 * 
	 * @return a possibly empty list of all quotes.
	 * @throws FileNotFoundException
	 */
	public List<String> getQuotes() throws FileNotFoundException {
		List<String> quotes = new LinkedList<String>();
		Scanner scan = new Scanner(file);

		String curLine = "", rq = "";
		boolean inQuote = false;

		while (scan.hasNextLine()) {
			curLine = scan.nextLine();
			if (inQuote)
				rq += "\n";
			while (!curLine.isEmpty()) {
				String ch = Character.toString(curLine.charAt(0));
				curLine = curLine.substring(1);
				if (quote.equals(ch)) {
					if (inQuote) {
						quotes.add(rq);
						rq = "";
					}
					inQuote = !inQuote;
				} else if (inQuote) {
					rq += ch;
				}
			}
		}

		return quotes;
	}

	/**
	 * General document diagnostic events.
	 * 
	 * @param token
	 */
	private void runDiagnostic(Token token) {
		try {
			if (token.TYPE == TokenType.INVALID) {
				inQuote = !inQuote;
				return;
			}

			// Update most popular var
			if (token.TYPE == TokenType.VAR) {
				if (!VAR_FREQUENCY.containsKey(token)) {
					VAR_FREQUENCY.put(token, 1);
				} else {
					VAR_FREQUENCY.put(token, VAR_FREQUENCY.get(token) + 1);
				}
			}

			// Keep track of how many of each type.
			switch (token.TYPE) {
			case INT:
				totalINTS++;
				if (minINT == null || maxINT == null) {
					maxINT = minINT = token;
				} else {
					if (new BigInteger(token.TOKEN).compareTo(new BigInteger(
							minINT.TOKEN)) < 0) {
						minINT = token;
					}
					if (new BigInteger(token.TOKEN).compareTo(new BigInteger(
							maxINT.TOKEN)) > 0) {
						maxINT = token;
					}
				}
				break;
			case FLOAT:
				totalFLOATS++;
				if (minFLOAT == null || maxFLOAT == null) {
					maxFLOAT = minFLOAT = token;
				} else {
					if (new BigDecimal(token.TOKEN).compareTo(new BigDecimal(
							minFLOAT.TOKEN)) < 0) {
						minFLOAT = token;
					}
					if (new BigDecimal(token.TOKEN).compareTo(new BigDecimal(
							maxFLOAT.TOKEN)) > 0) {
						maxFLOAT = token;
					}
				}
				break;
			case VAR:
				totalVARS++;
				break;
			case INVALID:
			default:
				break;
			}

			/*
			 * Record occurrences of VARS followed by INTs of FLOATs.
			 */
			if (prevToken.TYPE == TokenType.VAR
					&& (token.TYPE == TokenType.FLOAT || token.TYPE == TokenType.INT)) {
				varFollowOccurrences++;
			}

			// Finally, register last used token.
			prevToken = token;

		} catch (NumberFormatException n) {
			// The number is too large for ints
			System.err
					.println("\nEncountered INT/FLOAT that was too large for processing: "
							+ token.TOKEN);
		}
	}

	/**
	 * Attempt to parse the type of the given token.
	 * 
	 * @param token
	 *            the string token. Should have no spaces!
	 * @return the type of the token. Can be INT,VAR, FLOAT, or INVALID.
	 */
	public static TokenType getType(final String token) {
		if (isFLOAT(token))
			return TokenType.FLOAT;
		if (isINT(token))
			return TokenType.INT;
		if (isVAR(token))
			return TokenType.VAR;
		return TokenType.INVALID;
	}

	/**
	 * VAR : Begins with a lowercase (a-z) or uppercase letter (A-Z) and is
	 * followed by one or more of the same or by a digit (0-9) or a hyphen (-)
	 * 
	 * @param token
	 *            a string.
	 * @return true if string is a
	 */
	public static boolean isVAR(final String token) {
		// Check for empty or 1-size strings.
		if (token.length() < 2)
			return false;

		// Verify first char is azAZ
		String fChar = Character.toString(token.charAt(0));
		if (!(az.contains(fChar) || AZ.contains(fChar))) {
			return false;
		}

		// verify rest of chars are azAZ09-
		String rChars = token.substring(1);
		for (int i = 0; i < rChars.length(); i++) {
			fChar = Character.toString(rChars.charAt(i));
			if (!(az.contains(fChar) || AZ.contains(fChar)
					|| digits.contains(fChar) || hyphen.contains(fChar))) {
				return false;
			}
		}

		// Passes all tests!
		return true;
	}

	/**
	 * INT : Starts with a digit (0-9) followed by one or more of the same (0-9)
	 * 
	 * @param token
	 * @return
	 */
	public static boolean isINT(final String token) {
		// Check for empty or 1-size strings.
		if (token.length() < 2)
			return false;

		// verify all chars are 0-9.
		for (int i = 0; i < token.length(); i++) {
			if (!digits.contains(Character.toString(token.charAt(i)))) {
				return false;
			}
		}

		// Passes all tests!
		return true;
	}

	/**
	 * FLOAT: Starts with a digit (0-9) followed by zero or more of (0-9)
	 * followed by a dot (.) followed by zero or more of (0-9)
	 * 
	 * @param token
	 * @return
	 */
	public static boolean isFLOAT(final String token) {

		int indexOfDot = token.indexOf(dot);

		// Check for empty or 1-size strings.
		if (token.length() < 2 || indexOfDot <= 0)
			return false;

		// Check left side
		String lChars = token.substring(0, indexOfDot);
		// verify all chars are 0-9.
		for (int i = 0; i < lChars.length(); i++) {
			if (!digits.contains(Character.toString(lChars.charAt(i)))) {
				return false;
			}
		}

		// if right side exists, check it
		if (indexOfDot != token.length() - 1) {
			lChars = token.substring(indexOfDot + 1);
			// verify all chars are 0-9.
			for (int i = 0; i < lChars.length(); i++) {
				if (!digits.contains(Character.toString(lChars.charAt(i)))) {
					return false;
				}
			}
		}

		// Passes all tests!
		return true;
	}

	/**
	 * Fully tokenize the entire document at once.
	 */
	public void tokenizeAll() {
		try {
			while (true) {
				getNextValidToken();
			}
		} catch (NoSuchElementException done) {
			// Fully Tokenized
		}
	}
}
