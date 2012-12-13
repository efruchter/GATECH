
package project.phase2.ll1parsergenerator.dfastuff;

import java.util.*;

/**
 * A finite automata is any system that utilizes a finite number of states and associated transitions in order to represent a behavioral model.
 * 
 * T represents the transition values.
 */
public abstract class TableDrivenFiniteAutomaton<T> implements IFiniteAutomaton<Integer, T>
{
	//
	// DATA
	//
	/**
	 * The empty transition.
	 */
	protected final T EMPTY_TRANSITION = null;
	
	/**
	 * A representation of the automaton implemented as a table of states and their associated transitions.
	 */
	private List<Map<T, Transition<T, Integer>>> mAutomaton;
	
	/**
	 * The starting state of this automaton.
	 */
	private int mStartState;
	
	/**
	 * The final states of this automaton.
	 */
	private Set<Integer> mGoalStates;
	
	/**
	 * The labels of the goal states.
	 */
	private Map<Integer, String> mGoalLabels;
	
	
	//
	// CTOR
	//
	public TableDrivenFiniteAutomaton()
	{
		mAutomaton = new ArrayList<Map<T, Transition<T, Integer>>>();
		mGoalStates = new HashSet<Integer>();
		mGoalLabels = new HashMap<Integer, String>();
	}
	
	
	//
	// PUBLIC METHODS
	//
	/*
	 * (non-Javadoc)
	 * @see dfabuilder.IFiniteAutomata#getStates()
	 */
	public List<Integer> getStates()
	{
		ArrayList<Integer> ret = new ArrayList<Integer>(mAutomaton.size());
		for(int i = 0; i < mAutomaton.size(); i++)
			ret.add(i);
		
		return ret;
	}
	
	/*
	 * (non-Javadoc)
	 * @see dfabuilder.IFiniteAutomata#getStartState()
	 */
	public Integer getStartState()
	{
		return mStartState;
	}
	
	/*
	 * (non-Javadoc)
	 * @see dfabuilder.IFiniteAutomata#getGoalStates()
	 */
	public Set<Integer> getGoalStates()
	{
		Set<Integer> ret = new HashSet<Integer>();
		ret.addAll(mGoalStates);
		
		return ret;
	}
	
	/*
	 * (non-Javadoc)
	 * @see dfabuilder.IFiniteAutomaton#getGoalLabel(java.lang.Object)
	 */
	@Override
	public String getGoalLabel(Integer goalState)
	{
		if(mGoalLabels.containsKey(goalState))
			return mGoalLabels.get(goalState);
		
		return null;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see dfabuilder.IFiniteAutomata#getTransitions(java.lang.Object)
	 */
	public List<Transition<T, Integer>> getTransitions(Integer state)
	{
		if(state >= mAutomaton.size() || state < 0)
			return null;
		
		ArrayList<Transition<T, Integer>> ret = new ArrayList<Transition<T, Integer>>();
		ret.addAll(mAutomaton.get(state).values());
		
		return ret;
	}
	
	/*
	 * (non-Javadoc)
	 * @see dfabuilder.IFiniteAutomaton#getTransition(java.lang.Object, java.lang.Object)
	 */
	public Transition<T, Integer> getTransition(T value, Integer state)
	{
		if(state >= mAutomaton.size() || state < 0)
			return null;
		
		return mAutomaton.get(state).get(value);
	}
	
	
	public List<T> getTransitionValues(Integer state)
	{
		if(state >= mAutomaton.size() || state < 0)
			return null;
		
		ArrayList<T> ret = new ArrayList<T>();
		ret.addAll(mAutomaton.get(state).keySet());
		
		return ret;
	}
	
	/*
	 * (non-Javadoc)
	 * @see dfabuilder.IFiniteAutomaton#createState()
	 */
	public Integer createState()
	{
		int ret = mAutomaton.size();
		mAutomaton.add(new HashMap<T, Transition<T, Integer>>());
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see dfabuilder.IFiniteAutomaton#setStartState(java.lang.Object)
	 */
	@Override
	public void setStartState(Integer state)
	{
		if(state >= mAutomaton.size() || state < 0)
			return;
		
		mStartState = state;
	}


	/* (non-Javadoc)
	 * @see dfabuilder.IFiniteAutomaton#setGoalState(java.lang.Object)
	 */
	@Override
	public void setGoalState(Integer state)
	{
		setGoalState(state, null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see dfabuilder.IFiniteAutomaton#setGoalState(java.lang.Object, java.lang.String)
	 */
	@Override
	public void setGoalState(Integer state, String label)
	{
		if(state >= mAutomaton.size() || state < 0)
			return;
		
		mGoalStates.add(state);
		
		mGoalLabels.put(state, label);
	}
	
	/*
	 * (non-Javadoc)
	 * @see dfabuilder.IFiniteAutomaton#setGoalLabels(java.lang.String)
	 */
	@Override
	public void setGoalLabels(String label)
	{
		for(Integer goal : mGoalStates)
			mGoalLabels.put(goal, label);
	}
	
	/*
	 * (non-Javadoc)
	 * @see dfabuilder.IFiniteAutomaton#addTransition(dfabuilder.Transition)
	 */
	public boolean addTransition(Transition<T, Integer> trans)
	{
		// Make sure we have the start state.
		if(trans.getStart() >= mAutomaton.size() || trans.getStart() < 0)
			return false;
		
		// Make sure that we have the end states.
		for(int i : trans.getDestinations())
		{
			if(i >= mAutomaton.size() || i < 0)
			{
				return false;
			}
		}
		
		// If we already have a transition for the given state/value, reject.
		Map<T, Transition<T, Integer>> state = mAutomaton.get(trans.getStart());
		if(state.containsKey(trans.getValue()))
		{
			Set<Integer> dests = new HashSet<Integer>();
			for(int org : getTransition(trans.getValue(), trans.getStart()).getDestinations())
			{
				dests.add(org);
			}
			
			for(int newD : trans.getDestinations())
			{
				dests.add(newD);
			}
			
			state.put(trans.getValue(), new Transition<T, Integer>(trans.getValue(), trans.getStart(), dests.toArray(new Integer[0])));
		}
		else
			state.put(trans.getValue(), trans);
		
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see dfabuilder.IFiniteAutomaton#testInput(T[])
	 */
	public AcceptLabel testInput(T[] input)
	{
		Transition<T, Integer> t;
		Set<Integer> newStates, currentStates = new HashSet<Integer>();
		currentStates.add(mStartState);
		
		currentStates = epsilonClosure(currentStates);
		
		// Walk through transitions until we have reached the end of input.
		for(int transition = 0; transition < input.length; transition++)
		{
			newStates = new HashSet<Integer>();
			for(Integer state : currentStates)
			{
				t = this.getTransition(input[transition], state);
				if(t != null)
					newStates.addAll(Arrays.asList(t.getDestinations()));
			}
			
			// If the transition was not valid for any of our current states return false.
			if(newStates.isEmpty())
				return new AcceptLabel();
			
			currentStates = epsilonClosure(newStates);
		}
		
		// If any of our current states are a goal, we accept.
		for(Integer state : currentStates)
		{
			if(mGoalStates.contains(state))
			{
				return new AcceptLabel(true, getGoalLabel(state));
			}
		}
		
		// We stayed within the FA but did not reach a goal.
		return new AcceptLabel();
	}
	
	/**
	 * Generates a string representation of the automaton in the form:
	 * 
	 * This string is of the form: ({StateNumber: [ Transitions ]}
	 * 								{StateNumber: [ Transitions ]}
	 * 								...
	 * 								{StateNumber: [ Transitions ]})
	 * 
	 * The starting state number will be preceded by a ==>, and goal state numbers will be encapsulated in parentheses () along with the label if the goal has one.
	 * 
	 * @return a string representation of the automaton.
	 */
	public String toString()
	{
		String ret = "(";
		
		for(int i = 0; i < mAutomaton.size(); i++)
		{
			ret += "{";
			
			if((Integer.valueOf(mStartState)).equals(i))
				ret += "==>";
			
			if(mGoalStates.contains(i))
			{
				ret += "(" + i + ((getGoalLabel(i) != null)?("-\"" + getGoalLabel(i) + "\")"):(")"));
			}
			else
				ret += i;
			
			ret += ": ";
			ret += Arrays.toString(getTransitions(i).toArray());
			ret += "}\n";
		}
		
		ret = ret.trim();
		ret += ")";
		
		return ret;
	}
	
	//
	// PRIVATE METHODS
	//
	private Set<Integer> epsilonClosure(Set<Integer> states)
	{
		Set<Integer> interSet, newSet = new HashSet<Integer>(), retSet = new HashSet<Integer>();
		Transition<T, Integer> t;
		retSet.addAll(states);
		newSet.addAll(states);
		int oldSize = 0, newSize = retSet.size();
		
		while((oldSize != newSize) && !newSet.isEmpty())
		{
			interSet = newSet;
			newSet = new HashSet<Integer>();
			for(Integer state : interSet)
			{
				t = this.getTransition(EMPTY_TRANSITION, state);
				if(t != null)
					newSet.addAll(Arrays.asList(t.getDestinations()));
			}
			
			// This rather than add all to prevent duplications and same states being expaned over and over.
			interSet = new HashSet<Integer>();
			for(Integer newS : newSet)
			{
				if(!retSet.contains(newS))
				{
					interSet.add(newS);
					retSet.add(newS);
				}
			}
			newSet = interSet;
			
			oldSize = newSize;
			newSize = retSet.size();
		}
		
		return retSet;
	}
}
