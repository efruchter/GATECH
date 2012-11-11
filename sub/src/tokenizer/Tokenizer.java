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
	@SuppressWarnings("unused")
	private BufferedReader br;
	private StringBuilder sb = new StringBuilder();
	private String everything;
	private int index = 0;
	private NFA dfa;

	public Tokenizer(final NFA dfa, final InputStream input) {
		if (!dfa.isDFA())
			throw new RuntimeException("nfa is not a dfa for tokenizer");

		this.dfa = dfa;
		this.br = new BufferedReader(new InputStreamReader(input));
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			everything = sb.toString();
			sb = new StringBuilder();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		Token t = null;
		for (int max = everything.length(); max > index; max--) {
			t = getNextToken(dfa.getStartState(), index, max);
			if (t != null) {
				index += t.value.length();
				break;
			}
		}
		if(t == null && index < everything.length()){
			index++;
			return getNextToken();
		}

		return t;
	}

	public Token getNextToken(State state, int min, int max) {
		Token t = null;
		for (Transition tr : state.getTransitions()) {
			if (min == max) {
				break;
			}
			if (tr.isValid("" + everything.charAt(min))) {
				sb.append(everything.charAt(min));
				return getNextToken(tr.getDestinationState(), min + 1, max);
			}
		}
		if (state.isFinal()) {
			t = new Token(state.getName(), sb.toString());
		}
		sb = new StringBuilder();
		return t;
	}

}
