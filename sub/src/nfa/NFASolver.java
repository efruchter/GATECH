package nfa;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * Checks if strings are valid in a given NFA.
 * 
 * @author toriscope
 * 
 */
public class NFASolver {

	private final NFA nfa;
	private final HashSet<NFAStep> steps;

	/**
	 * Create an NFA validty checker using a given nfa.
	 * 
	 * @param nfa
	 */
	public NFASolver(final NFA nfa) {
		this.nfa = nfa;
		this.steps = new HashSet<NFAStep>();
	}

	/**
	 * Check if the string is valid in the NFA.
	 * 
	 * @param string
	 *            string to check for validity
	 * @return true if the string is valid, false otherwise.
	 */
	public boolean isValid(final String string) {
		boolean isValid = false;
		steps.add(new NFAStep(this.nfa.getStartState(), string));
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
							NFAStep newStep = new NFAStep(t.getDestinationState(), step.string);
							if (!steps.contains(newStep)) {
								steps.add(newStep);
							}
						} else if (t.isValid(step.string.charAt(0))) {
							steps.add(new NFAStep(t.getDestinationState(), step.string.substring(1)));
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
	private class NFAStep {
		private State state;
		private String string;

		public NFAStep(final State state, final String string) {
			this.state = state;
			this.string = string;
		}

		public boolean equals(Object o) {
			if (o instanceof NFAStep) {
				NFAStep p = (NFAStep) o;
				return p.state.equals(this.state) && p.string.equals(this.string);
			} else
				return false;
		}

		public String toString() {
			return state.toString() + " " + string;
		}
	}
}
