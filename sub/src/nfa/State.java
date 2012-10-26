package nfa;

import java.util.LinkedList;
import java.util.List;

/**
 * An NFA State.
 * 
 * @author toriscope
 * 
 */
public class State {
	private final String name;
	private final List<Transition> transitions;
	private final boolean isFinal;

	/**
	 * Create an NFA State
	 * 
	 * @param name
	 *            name of the state
	 * @param isFinal
	 *            is the state an end state?
	 */
	public State(final String name, final boolean isFinal) {
		this.name = name;
		this.isFinal = isFinal;
		this.transitions = new LinkedList<Transition>();
	}

	/**
	 * Factory method for new state
	 * 
	 * @param name
	 * @param isFinal
	 * @return State object
	 */
	public static State createState(final String name, final boolean isFinal) {
		return new State(name, isFinal);
	}

	public String getName() {
		return name;
	}

	/**
	 * Is the state an end state?
	 * 
	 * @return true if final state, false otherwise.
	 */
	public boolean isFinal() {
		return this.isFinal;
	}

	public List<Transition> getTransitions() {
		return new LinkedList<Transition>(this.transitions);
	}

	public void addTransition(final Transition... t) {
		for (Transition tr : t)
			this.transitions.add(tr);
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("[").append(name).append("]");
		for (Transition tr : this.transitions) {
			b.append("(").append(tr.toString()).append(")");
		}
		return b.toString();
	}
}
