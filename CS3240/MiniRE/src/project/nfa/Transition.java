package project.nfa;


/**
 * NFA transition.
 *
 * @author toriscope
 */
public class Transition {
    private String string;
    private State state;
    private boolean isEmpty;
    private boolean matchAll;
    public final static String MATCHALL_TOKEN = "";

    /**
     * Create a transition to state with character.
     *
     * @param string
     * @param state
     */
    public Transition(final String string, final State state) {
        this.string = string;
        this.state = state;
        this.isEmpty = false;
        this.matchAll = false;
        if (string.isEmpty()) {
            throw new RuntimeException("Non-empty transition cannot have empty string");
        }
    }

    /**
     * Factory method for non-empty transition
     *
     * @param string
     * @param state
     * @return
     */
    public static Transition createTransition(final String string, final State state) {
        return new Transition(string, state);
    }

    /**
     * Factory method for non-empty state
     *
     * @param state
     */
    public static Transition createEmptyTransition(final State state) {
        return new Transition(state);
    }

    /**
     * Create an empty transition to a state.
     *
     * @param state
     */
    public Transition(final State state) {
        this.string = "EMPTY";
        this.state = state;
        this.isEmpty = true;
        this.matchAll = false;
    }

    /**
     * Determines whether the given character is accepted by this transition.
     *
     * @param character
     * @return true if valid, false otherwise.
     */
    public boolean isValid(final String character) {
        return matchAll || string.equals(String.valueOf(character));
    }

    public String getString() {
        return string;
    }

    public boolean getMatchAll() {
        return matchAll;
    }

    public void setMatchAll() {
        matchAll = true;
        string = MATCHALL_TOKEN;
    }

    public boolean isEmptyTransition() {
        return isEmpty;
    }

    public void setIsEmptyTransition(boolean isEmpty) {
        this.isEmpty = isEmpty;
    }

    public State getDestinationState() {
        return state;
    }

    public void setDestinationState(State destinationState) {
        state = destinationState;
    }

    public String toString() {
        String s = "";
        if (isEmpty) {
            s += "EMPTY";
        } else if (matchAll) {
            s += "DOT";
        } else {
            s += "'" + string + "'";
        }
        return s + "->[" + this.getDestinationState().getName() + "]";
    }

    public static Transition spawnGoal() {
        return spawnGoal("DEFAULT_FINAL");
    }

    public static Transition createDotTransition(final State state) {
        return new Transition(".", state){{setMatchAll();}};
    }

    public static Transition spawnGoal(final String name) {
        return new Transition(new State(name, true));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Transition) {
            Transition t = (Transition) o;
            return t.getString().equals(string) && t.matchAll == matchAll && t.isEmptyTransition() == isEmptyTransition()
                    && t.getDestinationState().equals(getDestinationState());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return state.hashCode() + string.hashCode();
    }
}
