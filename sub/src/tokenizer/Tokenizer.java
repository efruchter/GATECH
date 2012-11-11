package tokenizer;

import nfa.NFA;
import nfa.State;
import nfa.Transition;

import java.io.*;

/**
 * Tokenizes an input stream. The token type is the name of the final state.
 * 
 * @author Kefu Zhou
 *
 */
public class Tokenizer {
	private BufferedReader br;
	private StringBuilder stackBuffer = new StringBuilder();
	private StringBuilder lineBuffer = new StringBuilder();
	private NFA dfa;

	public Tokenizer(final NFA dfa, final InputStream input) {
		if (!dfa.isDFA())
			throw new RuntimeException("nfa is not a dfa for tokenizer");

		this.dfa = dfa;
		this.br = new BufferedReader(new InputStreamReader(input));
	}
	
	/**
	 * Gets one line from input stream.
	 * 
	 * @return true if non-null line is added
	 */
	private boolean bufferLine() {
		try {
			String currLine = br.readLine();
			if (currLine != null) {
				lineBuffer.append(currLine);
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Gets the next token
	 *
	 * @param nfa
	 *			the nfa to check the string against
	 * @param string
	 *			string to check for validity
	 * @return true if the string is valid, false otherwise.
	 */
	public Token getNextToken() {
		if(lineBuffer.length() == 0) {
			boolean more = bufferLine();
			if(!more) {
				return null;
			}
		}
		
		Token t = null;
		for (int max = lineBuffer.length(); max > 0; max--) {
			t = getNextToken(dfa.getStartState(), 0, max);
			if (t != null) {
				lineBuffer.delete(0, t.value.length());
				break;
			}
		}
		if (t == null && lineBuffer.length() > 0) {
			lineBuffer.deleteCharAt(0);
			return getNextToken();
		}
		return t;
	}

	private Token getNextToken(State state, int min, int max) {
		Token t = null;
		for (Transition tr : state.getTransitions()) {
			if (min == max) {
				break;
			}
			if (tr.isValid(String.valueOf(lineBuffer.charAt(min)))) {
				stackBuffer.append(lineBuffer.charAt(min));
				return getNextToken(tr.getDestinationState(), min + 1, max);
			}
		}
		if (state.isFinal()) {
			t = new Token(state.getName(), stackBuffer.toString());
		}
		stackBuffer.setLength(0);
		return t;
	}

}
