package nfa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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

	public static NFA convertToDFA(final NFA nfaInit, final char[] totalLexicon) {
		final List<State> newStates = new ArrayList<State>();
		State startState = null;

		/*
		 * Each DfaConStep[] will feature a closure for every transition
		 * character.
		 */
		final HashMap<MetaState, MetaState[]> metaStateToClosures = new HashMap<MetaState, MetaState[]>();

		// Build state e-closure table
		boolean missingClosures = true;

		// find first closure.

		while (missingClosures) {
			// TODO: build closure table.
		}

		return null;
	}

	/**
	 * Returns all states withing E^* of given state.
	 * 
	 * @param state
	 *            state to find full closure of.
	 * @return all states that can be reached in zero or more E-Closures from
	 *         given state.
	 */
	public static Set<State> findClosure(final State state) {

		final HashSet<State> explored = new HashSet<State>();
		final HashSet<State> frontier = new HashSet<State>();
		frontier.add(state);

		while (!frontier.isEmpty()) {
			State c = frontier.iterator().next();
			for (Transition t : c.getTransitions()) {
				if (t.isEmptyTransition() && !frontier.contains(t.getDestinationState())
						&& !explored.contains(t.getDestinationState())) {
					frontier.add(t.getDestinationState());
				}
			}
			frontier.remove(c);
			explored.add(c);
		}

		return explored;

	}

	public static NFA minimizeDFA(final NFA nfaInit, final char[] totalLexicon) {

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
				return p.state.equals(this.state) && p.string.equals(this.string);
			} else {
				System.err.println("Object is not an instance of NFAStep");
				return false;
			}
		}

		public String toString() {
			return state.toString() + " " + string;
		}
	}

	/**
	 * Single meta-state entry in E-Closure table
	 * 
	 * @author toriscope
	 * 
	 */
	private static class MetaState {
		private final String name;
		private final HashSet<State> states;

		public MetaState(final String name) {
			states = new HashSet<State>();
			this.name = name;
		}

		@Override
		public boolean equals(final Object o) {
			if (o instanceof MetaState) {
				MetaState s = (MetaState) o;
				if (s.states.size() != this.states.size()) {
					return false;
				}
				for (State state : s.states) {
					if (!this.states.contains(state)) {
						return false;
					}
				}
			} else
				return false;

			return true;
		}
	}
}
