package nfa;

import java.io.BufferedReader;
import java.io.IOException;

public class Tokenizer {
	private BufferedReader br;
	private StringBuilder sb = new StringBuilder();
	private String everything;
	private int index = 0;
	private NFA dfa;
	private State state;

	public Tokenizer(final NFA dfa, final BufferedReader br) {
		if (!dfa.isDFA())
			throw new RuntimeException("nfa is not a dfa for tokenizer");

		this.dfa = dfa;
		this.br = br;
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			everything = sb.toString();
			sb.setLength(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the next token
	 * 
	 * @param nfa
	 *            the nfa to check the string against
	 * @param string
	 *            string to check for validity
	 * @return true if the string is valid, false otherwise.
	 */
	public Token getNextToken() {
		Token t = null;
		for (int max = everything.length(); max > index; max--) {
			t = getNextToken(dfa.getStartState(), index, max,
					new StringBuilder());
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

	public Token getNextToken(State state, int min, int max,
			StringBuilder sbCurr) {
		Token t = null;
		for (Transition tr : state.getTransitions()) {
			if (min == max) {
				break;
			}
			if (tr.isValid(everything.charAt(min))) {
				sbCurr.append(everything.charAt(min));
				return getNextToken(tr.getDestinationState(), min + 1, max, sbCurr);
			}
		}
		if(state.isFinal()){
			t = new Token(state.getName(), sbCurr.toString());
			sbCurr.setLength(0);
		}else{
			sbCurr.setLength(0);
		}
		return t;
	}

}
