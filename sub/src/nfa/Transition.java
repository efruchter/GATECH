package nfa;

/**
 * NFA transition.
 * 
 * @author toriscope
 * 
 */
public class Transition {

	private final char character;
	private final State state;
	private final boolean isEmpty;

	/**
	 * Create a transition to state with character.
	 * 
	 * @param character
	 * @param state
	 */
	public Transition(final char character, final State state) {
		this.character = character;
		this.state = state;
		this.isEmpty = false;
	}

	/**
	 * Create an empty transition to a state.
	 * 
	 * @param state
	 */
	public Transition(final State state) {
		this.character = Character.MAX_VALUE;
		this.state = state;
		this.isEmpty = true;
	}

	/**
	 * Determines whether the given character is accepted by this transition.
	 * 
	 * @param character
	 * @return true if valid, false otherwise.
	 */
	public boolean isValid(final char character) {
		return this.character == character;
	}
	
	public char getCharacter() {
		return character;
	}

	public boolean isEmptyTransition() {
		return isEmpty;
	}

	public State getDestinationState() {
		return state;
	}

	public String toString() {
		return (this.isEmpty ? "Empty" : this.character) + "->"
				+ this.getDestinationState().getName();
	}
}
