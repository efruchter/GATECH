package nfa;

import java.util.HashSet;
import java.util.List;

/**
 * An NFA.
 *
 * @author toriscope
 */
public class NFA {

    private State startState;

    /**
     * Create an NFA.
     *
     * @param startState the initial state.
     */
    public NFA(final State startState) {
        this.startState = startState;
    }

    public NFA(NFAUtil.NFASegment segment) {
        this(segment.start);
    }

    /**
     * Factory method of creating NFA
     *
     * @param startState
     * @return
     */
    public static NFA createNFA(final State startState) {
        return new NFA(startState);
    }

    public State getStartState() {
        return this.startState;
    }

    public void setStartState(State startState) {
        this.startState = startState;
    }

    /**
     * Returns whether this NFA is a valid DFA.
     *
     * @return true if no states contain an empty transition or multiple
     *         same-character transitions.
     */
    public boolean isDFA() {
        for (final State stateTuple : NFAUtil.getAllReachableStates(startState)) {
            HashSet<String> chars = new HashSet<String>();
            for (Transition state : stateTuple.getTransitions()) {
                if (state.isEmptyTransition() || chars.contains(state.getString())) {
                    return false;
                }
                chars.add(state.getString());
            }
        }
        return true;
    }

    public int numberOfStates() {
        return getStates().size();
    }

    public List<State> getStates() {
        return NFAUtil.getAllReachableStates(startState);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("Starting state: [").append(this.startState.getName()).append("]").append("\n");
        for (State state : NFAUtil.getAllReachableStates(startState)) {
            b.append(state.toString()).append("\n");
        }
        return b.toString();
    }
}
