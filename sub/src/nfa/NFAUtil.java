package nfa;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * Utilities for analyzing and converting NFA's.
 * 
 * @author toriscope
 * 
 */
public class NFAUtil {

	private NFAUtil() {
		;
	}

	public NFA convertToDFA(final NFA nfaInit, final char[] totalLexicon) {
		// Run the e-closure conversion algorithm

		return null;
	}

	public NFA minimizeDFA(final NFA nfaInit, final char[] totalLexicon) {

		// Run the minimization algorithm

		return null;
	}

	/**
	 * Check if the string is valid in the NFA.
	 * 
	 * @param nfa
	 *            the nfa to check the string against
	 * @param string
	 *            string to check for validity
	 * @return true if the string is valid, false otherwise.
	 */
	public static boolean isValid(final NFA nfa, final String string) {
		final HashSet<NFAStep> steps = new HashSet<NFAStep>();
		boolean isValid = false;
		steps.add(new NFAStep(nfa.getStartState(), string));
		while (!isValid && !steps.isEmpty()) {
			for (NFAStep step : new LinkedList<NFAStep>(steps)) {
				if (step.string.isEmpty()) {
					if (step.state.isFinal()) {
						isValid = true;
					} else {
						steps.remove(step);
					}
				} else {
					for (Transition t : step.state.getTransitions()) {
						if (t.isEmptyTransition()) {
							NFAStep newStep = new NFAStep(
									t.getDestinationState(), step.string);
							if (!steps.contains(newStep)) {
								steps.add(newStep);
							}
						} else if (t.isValid(step.string.charAt(0))) {
							steps.add(new NFAStep(t.getDestinationState(),
									step.string.substring(1)));
						}
					}
				}
				steps.remove(step);
			}
		}
		steps.clear();
		return isValid;
	}

	/**
	 * Execution step in an NFA walk.
	 * 
	 * @author toriscope
	 * 
	 */
	private static class NFAStep {
		private final State state;
		private final String string;

		public NFAStep(final State state, final String string) {
			this.state = state;
			this.string = string;
		}

		public boolean equals(Object o) {
			if (o instanceof NFAStep) {
				NFAStep p = (NFAStep) o;
				return p.state.equals(this.state)
						&& p.string.equals(this.string);
			} else {
				System.err.println("Object is not an instance of NFAStep");
				return false;
			}
		}

		public String toString() {
			return state.toString() + " " + string;
		}
	}
}
