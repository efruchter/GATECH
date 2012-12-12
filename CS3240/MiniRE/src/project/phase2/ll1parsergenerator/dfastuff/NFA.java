package project.phase2.ll1parsergenerator.dfastuff;

/**
 * A specific, Nondeterministic, type of Finite Automaton. This specific example
 * assumes Characters as the transition values.
 */
public class NFA extends TableDrivenFiniteAutomaton<String> {
	/**
	 * Empty construct of NFA
	 */
	public NFA() {
	}

	/**
	 * Constructor of NFA
	 * 
	 * @param a the string to generate the new NFA from (only the first character is used).
	 */
	public NFA(String a) {
		this.createState();
		this.createState();
		this.addTransition(new Transition<String, Integer>(a.substring(0, 1),
				0, new Integer[] { 1 }));
		this.setStartState(0);
		this.setGoalState(1);
	}

	/**
	 * Create a NFA with start state and goal state
	 * 
	 * @return new NFA
	 */
	public static NFA nullNFA() {
		NFA ret = new NFA();
		int state = ret.createState();
		ret.setStartState(state);
		ret.setGoalState(state);
		return ret;
	}

	/**
	 * Union
	 * 
	 * @param sec
	 *            The NFA to be unioned with
	 * @return a new NFA a|b
	 */
	public TableDrivenFiniteAutomaton<String> union(
			TableDrivenFiniteAutomaton<String> sec) {
		// null check
		if (sec == null) {
			return this;
		}
		NFA ret = new NFA();
		// Copyt states of first into ret
		int firstnumStates = this.getStates().size();
		for (int i = 0; i < firstnumStates; i++) {
			ret.createState();
		}
		// Copy transition from first NFA to ret
		for (int i : this.getStates()) {
			for (Transition<String, Integer> t : this.getTransitions(i)) {
				ret.addTransition(new Transition<String, Integer>(t.getValue(),
						t.getStart(), t.getDestinations()));
			}
		}
		// Copy states from sec to ret
		int secnumStates = sec.getStates().size();
		for (int i = 0; i < secnumStates; i++) {
			ret.createState();
		}
		for (int i : sec.getStates()) {
			for (Transition<String, Integer> t : sec.getTransitions(i)) {
				Integer[] dest = new Integer[t.getDestinations().length];
				for (int j = 0; j < dest.length; j++) {
					dest[j] = t.getDestinations()[j] + firstnumStates;
				}
				ret.addTransition(new Transition<String, Integer>(t.getValue(),
						t.getStart() + firstnumStates, dest));
			}
		}
		int start = ret.createState();
		ret.addTransition(new Transition<String, Integer>(null, start,
				new Integer[] { this.getStartState(),
						sec.getStartState() + firstnumStates }));
		ret.setStartState(start);
		// Copy first final states
		for (int i : this.getGoalStates()) {
			ret.setGoalState(i, this.getGoalLabel(i));
		}
		// Set sec final states
		for (int i : sec.getGoalStates()) {
			ret.setGoalState(i + firstnumStates, sec.getGoalLabel(i));
		}
		return ret;
	}

	/**
	 * Concatenation
	 * 
	 * @param sec
	 *            The NFA to be concatenated with
	 * @return A new NFA ab
	 */
	public TableDrivenFiniteAutomaton<String> concat(
			TableDrivenFiniteAutomaton<String> sec) {
		// null check
		if (sec == null) {
			return this;
		}
		NFA ret = new NFA();
		// Copyt states of first into ret
		int firstnumStates = this.getStates().size();
		for (int i = 0; i < firstnumStates; i++) {
			ret.createState();
		}
		// Copy transitions
		ret.setStartState(this.getStartState());
		for (int i : this.getStates()) {
			for (Transition<String, Integer> t : this.getTransitions(i)) {
				ret.addTransition(new Transition<String, Integer>(t.getValue(),
						t.getStart(), t.getDestinations()));
			}
		}
		// Copy states of sec into ret
		int secnumStates = sec.getStates().size();
		for (int i = 0; i < secnumStates; i++) {
			ret.createState();
		}
		// Copy transitions
		for (int i : sec.getStates()) {
			for (Transition<String, Integer> t : sec.getTransitions(i)) {
				Integer[] dest = new Integer[t.getDestinations().length];
				for (int j = 0; j < dest.length; j++) {
					dest[j] = t.getDestinations()[j] + firstnumStates;
				}
				ret.addTransition(new Transition<String, Integer>(t.getValue(),
						t.getStart() + firstnumStates, dest));
			}
		}
		// Concat
		for (int i : this.getGoalStates()) {
			ret.addTransition(new Transition<String, Integer>(null, i,
					new Integer[] { sec.getStartState() + firstnumStates }));
		}
		// Set final states
		for (int i : sec.getGoalStates()) {
			ret.setGoalState(i + firstnumStates, sec.getGoalLabel(i));
		}
		return ret;
	}

	/**
	 * Repetition
	 * 
	 * @return A new NFA a*
	 */
	public TableDrivenFiniteAutomaton<String> star() {
		NFA ret = new NFA();
		// Copyt states of into ret
		int states = this.getStates().size();
		for (int i = 0; i < states; i++) {
			ret.createState();
		}
		// Copy transition from NFA to ret
		for (int i : this.getStates()) {
			for (Transition<String, Integer> t : this.getTransitions(i)) {
				ret.addTransition(new Transition<String, Integer>(t.getValue(),
						t.getStart(), t.getDestinations()));
			}
		}
		int start = ret.createState();
		ret.addTransition(new Transition<String, Integer>(null, start,
				new Integer[] { this.getStartState() }));
		ret.setStartState(start);
		// Copy final states
		// Add Transition from this goal state to start state
		for (int i : this.getGoalStates()) {
			ret.setGoalState(i, this.getGoalLabel(i));
			ret.addTransition(new Transition<String, Integer>(null, i,
					new Integer[] { start }));
		}
		// start is goal
		ret.setGoalState(start);
		return ret;
	}

	/**
	 * Test NFA
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		NFA n = new NFA();
		n.createState();
		n.setStartState(0);
		n.createState();
		n.createState();
		n.addTransition(new Transition<String, Integer>("a", 0,
				new Integer[] { 1 }));
		n.addTransition(new Transition<String, Integer>("b", 0,
				new Integer[] { 2 }));
		n.setGoalState(1);
		n.setGoalState(2);

		NFA m = new NFA();
		m.createState();
		m.setStartState(0);
		m.createState();
		m.createState();
		m.addTransition(new Transition<String, Integer>("c", 0,
				new Integer[] { 1 }));
		m.addTransition(new Transition<String, Integer>("d", 0,
				new Integer[] { 2 }));
		m.setGoalState(1);
		m.setGoalState(2);
		NFA l = new NFA();
		l = (NFA) n.union(m);
		System.out.println(((NFA) (l.star())).concat(l));
	}
}
