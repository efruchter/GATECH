package nfa;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * An NFA State.
 * 
 * @author toriscope
 * 
 */
public class State {
	public String name;
	public boolean isFinal;
	private final HashSet<Transition> transitions;

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
		this.transitions = new HashSet<Transition>();
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

    public Transition getTransByString(String string) {
        for(Transition transition : transitions) {
            if(!transition.isEmptyTransition() && string.equals(transition.getString())) {
                return transition;
            }
        }
        return null;
    }

    public List<Transition> getNonEmptyTransitions() {
        List<Transition> t = new LinkedList<Transition>();
        for(Transition transition : transitions) {
            if(!transition.isEmptyTransition()) {
                t.add(transition);
            }
        }
        return t;
    }

    public List<Transition> getEmptyTransitions() {
        List<Transition> t = new LinkedList<Transition>();
        for(Transition transition : transitions) {
            if(transition.isEmptyTransition()) {
                t.add(transition);
            }
        }
        return t;
    }

	public void addTransition(final Transition... t) {
		for (Transition tr : t) {
            if (!transitions.contains(t))
			   transitions.add(tr);
        }
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(isFinal() ? "{" : "[").append(name).append(isFinal() ? "}" : "]").append("\t");
		for (Transition tr : this.transitions) {
			b.append("{").append(tr.toString()).append("}");
		}
		return b.toString();
	}
}
