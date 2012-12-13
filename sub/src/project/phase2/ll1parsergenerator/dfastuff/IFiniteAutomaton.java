
package project.phase2.ll1parsergenerator.dfastuff;

import java.util.*;

/**
 * An interface for finite automata containing some useful definitions.
 * 
 * S represents the type of the states, T represents the transition values.
 */
public interface IFiniteAutomaton<S, T>
{
	//
	// ABSTRACT METHODS
	//
	/**
	 * Gets an array of the transitions from the given state.
	 * 
	 * @param state the state to get the transitions of.
	 * @return the transitions for the given state.
	 */
	public abstract List<Transition<T, S>> getTransitions(S state);
	
	/**
	 * Gets the transition for the given value at the given state.
	 * 
	 * @param value the value to get the transition for.
	 * @param state the state to get the transition for.
	 * @return the transition for the given inputs.
	 */
	public abstract Transition<T, S> getTransition(T value, S state);
	
	/**
	 * Gets the possible transition values for the given state.
	 * 
	 * @param state the state to get the transition values of.
	 * @return the transition values.
	 */
	public abstract List<T> getTransitionValues(S state);
	
	/**
	 * Gets the list of all of the states in this automata.
	 * 
	 * @return an array of the states in this automata.
	 */
	public abstract List<S> getStates();
	
	/**
	 * Gets the starting state for the automata.
	 * 
	 * @return the start state.
	 */
	public abstract S getStartState();
	
	/**
	 * Gets the goal states for the automata.
	 * 
	 * @return the goal states.
	 */
	public abstract Set<S> getGoalStates();
	
	/**
	 * Gets the label of the given goalstate.
	 * 
	 * @return the label.
	 */
	public abstract String getGoalLabel(S goalState);
	
	/**
	 * Creates a new state in the automaton and returns it to the caller.
	 * If you add/remove states from the automaton, the state returned here is not guaranteed to remain consistent within the representation.
	 * 
	 * @return the new state.
	 */
	public abstract S createState();
	
	/**
	 * Set the given state in the automaton as the start state.
	 * 
	 * @param state the state to make the start state.
	 */
	public abstract void setStartState(S state);
	
	/**
	 * Set the given state in the automaton as a goal state.
	 * 
	 * @param state the state to make a goal state.
	 */
	public abstract void setGoalState(S state);
	
	/**
	 * Set the given state in the automaton as a goal state with the given label.
	 * 
	 * @param state the state to make a goal state.
	 * @param label the label to add to the goal state.
	 */
	public abstract void setGoalState(S state, String label);
	
	/**
	 * Sets the label of all goals.
	 * 
	 * @param label the label to apply to all goals.
	 */
	public abstract void setGoalLabels(String label);
	
	/**
	 * Adds a transition to the automaton.  
	 * 
	 * @param trans the transition to add.
	 * @return true if the transition was added, false otherwise.
	 */
	public abstract boolean addTransition(Transition<T, S> trans);
	
	/**
	 * Tests to see if the given input is accepted by automaton.
	 * 
	 * @param input the input to test.
	 * @return whether or not the given input is accepted by this automaton.
	 */
	public abstract AcceptLabel testInput(T[] input);
	
	/**
	 * Pair which allows for a multiple return value from testInput.
	 */
	public class AcceptLabel
	{
		/**
		 * Whether or not the input was accepted by testInput.
		 */
		private boolean mAccept;
		
		/**
		 * The label if the input was accepted.
		 */
		private String mLabel;
		
		//
		// CTOR
		//
		public AcceptLabel(boolean accept, String label)
		{
			mAccept = accept;
			mLabel = label;
		}
		
		public AcceptLabel()
		{
			this(false, null);
		}
		
		/**
		 * Returns the acceptance of the input.
		 * 
		 * @return true on acceptance, false otherwise.
		 */
		public boolean isAccepted()
		{
			return mAccept;
		}
		
		/**
		 * Returns the label of the input.
		 * 
		 * @return the label if the input was accepted, null otherwise.
		 */
		public String getLabel()
		{
			return mLabel;
		}
	}
}
