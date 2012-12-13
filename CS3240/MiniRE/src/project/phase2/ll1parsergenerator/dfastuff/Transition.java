
package project.phase2.ll1parsergenerator.dfastuff;

import java.util.Arrays;

/**
 * T represents what the actual transitions are represented by, S represents the states.
 */
public class Transition<T, S>
{
	//
	// DATA
	//
	/**
	 * The transition value.
	 */
	private T mValue;
	
	/**
	 * The transition destination states.
	 */
	private S[] mDestinations;
	
	/**
	 * The start state of this transition.
	 */
	private S mStart;
	
	
	//
	// CTOR
	//
	/**
	 * Create a new transition for the given data.
	 * @param value the value upon which to take this transition.
	 * @param start the state prior to this transition.
	 * @param destinations the states reached by taking this transition.
	 */
	public Transition(T value, S start, S[] destinations)
	{
		mValue = value;
		mStart = start;
		mDestinations = destinations;
	}
	
	
	//
	// PUBLIC METHODS
	//
	/**
	 * @return the transition value.
	 */
	public T getValue()
	{
		return mValue;
	}
	
	/**
	 * @return the start state.
	 */
	public S getStart()
	{
		return mStart;
	}
	
	/**
	 * @return the destinations.
	 */
	public S[] getDestinations()
	{
		return mDestinations;
	}
	
	/**
	 * @return a string representation of the transition.
	 */
	public String toString()
	{
		return mStart + "-" + mValue + "->" + Arrays.toString(mDestinations);
	}
}
