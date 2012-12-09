package project.phase2.ll1parsergenerator.dfastuff;

import java.util.*;

/**
 * A specific, Deterministic, type of Finite Automaton.  This specific example assumes Characters as the transition values.
 */
public class DFA extends TableDrivenFiniteAutomaton<String>
{
	/**
	 * A current match, for parsing through data iteratively.
	 */
	private TokenMatch mCurrMatch;
	
	/**
	 * Used to reset iterative matching.
	 */
	public void reset()
	{
		mCurrMatch = null;
	}
	
	/**
	 * Used to iteratively test input for matching.
	 * 
	 * @param input the input token.
	 * @return the match descriptor.
	 */
	public TokenMatch test(String input)
	{
		if(mCurrMatch == null)
			mCurrMatch = new TokenMatch(this.getStartState());
		
		if(mCurrMatch.isRejected())
			return mCurrMatch;
		
		Integer state = mCurrMatch.mState;		
		Transition<String, Integer> t = this.getTransition(input, state);
		if(t == null || t.getDestinations().length < 1)
			mCurrMatch = new TokenMatch(-1);
		else
			mCurrMatch = new TokenMatch(t.getDestinations()[0]);
		
		return mCurrMatch;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see dfabuilder.TableDrivenFiniteAutomaton#addTransition(dfabuilder.Transition)
	 */
	public boolean addTransition(Transition<String, Integer> trans)
	{
		if(trans.getDestinations().length != 1 || trans.getValue() == EMPTY_TRANSITION)
			return false;
		else
		{
			if(getTransition(trans.getValue(), trans.getStart()) != null)
			{
				return false;
			}
			else
			{
				return super.addTransition(trans);
			}
		}
	}
	
	/**
	 * Creates a minimum DFA from the current DFA.
	 * 
	 * @return a DFA that has been minimized.
	 */
	public DFA minimize()
	{
		// The NFA-DFA conversion on this DFA is to remove unreachable states.
		DFA retDFA = new DFA(), startDFA = DFA.fromNFA(this);
		int numStates = startDFA.getStates().size();
		
		if(numStates < 1)
			return retDFA;
		
		Set<Integer> goalStates = startDFA.getGoalStates();
		boolean[][] diff = new boolean[numStates][];
		for(int i = 0; i < numStates; i++)
		{
			diff[i] = new boolean[i];
		}
		
		// Initialize to final states != nonfinal.
		for(int j = 0; j < diff.length; j++)
		{
			for(int i = 0; i < diff[j].length; i++)
			{
				diff[j][i] = (goalStates.contains(j) != goalStates.contains(i));
				
				if(!diff[j][i] && (goalStates.contains(j) && goalStates.contains(i)))
				{
					String sI = startDFA.getGoalLabel(i), sJ = startDFA.getGoalLabel(j);
					diff[j][i] = (sI == null) ? (sJ != null) : (!sI.equals(sJ));
				}
			}
		}
		
		// Create our differences matrix.
		// This builds a table of the kind we were shown in class.
		boolean changed = true;
		while(changed)
		{
			changed = false;
			for(int j = 0; j < diff.length; j++)
			{
				for(int i = 0; i < diff[j].length; i++)
				{
					if(diff[j][i])
						continue;
					
					Set<String> transVals = new HashSet<String>();
					transVals.addAll(startDFA.getTransitionValues(j));
					int transSize = transVals.size();
					transVals.addAll(startDFA.getTransitionValues(i));
					
					// If adding the transition values for the second item 
					if(startDFA.getTransitionValues(i).size() != transSize || transVals.size() != transSize)
					{
						diff[j][i] = true;
						changed = true;
						continue;
					}
					
					for(String tVal : transVals)
					{
						Transition<String, Integer> t1 = startDFA.getTransition(tVal, i), t2 = startDFA.getTransition(tVal, j);
						
						// We are a DFA, so we should only have 1 destination for any transition.
						int dest1 = Math.max(t1.getDestinations()[0], t2.getDestinations()[0]), dest2 = Math.min(t1.getDestinations()[0], t2.getDestinations()[0]);
						if(dest1 != dest2 && diff[dest1][dest2])
						{
							diff[j][i] = true;
							changed = true;
							break;
						}
					}
				}
			}
		}
		
		// Start building our new dfa representation.
		Map<Integer, Set<Integer>> setMap = new HashMap<Integer, Set<Integer>>();
		
		Map<Set<Integer>, Integer> stateMap = new HashMap<Set<Integer>, Integer>();
		Map<Set<Integer>, Map<String, Set<Integer>>> transMap = new HashMap<Set<Integer>, Map<String, Set<Integer>>>();
		
		// Find out what the aggregations our states are in are.
		for(int j = diff.length - 1; j >= 0; j--)
		{
			if(setMap.containsKey(j))
				continue;
			
			Set<Integer> stateSet = new HashSet<Integer>();
			stateSet.add(j);
			
			for(int i = 0; i < diff[j].length; i++)
			{
				if(!diff[j][i])
					stateSet.add(i);
			}
			
			for(Integer state : stateSet)
			{
				setMap.put(state, stateSet);
			}
		}
		
		// Remove Dead States
		Set<Integer> deadStates = new HashSet<Integer>();
		for(Map.Entry<Integer, Set<Integer>> stateSet : setMap.entrySet())
		{
			Integer key = stateSet.getKey();
			if(goalStates.contains(key))
				continue;
			
			boolean dead = true;
			for(String s : startDFA.getTransitionValues(key))
			{
				Integer dest = startDFA.getTransition(s, key).getDestinations()[0];
				if(!setMap.get(dest).equals(stateSet.getValue()))
				{
					dead = false;
					break;
				}
			}
			
			if(dead)
			{
				deadStates.add(key);
			}
		}
		
		
		// Add all of the appropriate states and create the transition map.
		for(Map.Entry<Integer , Set<Integer>> stateSet : setMap.entrySet())
		{
			if(!stateMap.containsKey(stateSet.getValue()))
			{
				if(deadStates.contains(stateSet.getKey()))
				{
					stateMap.put(stateSet.getValue(), null);
				}
				else
				{
					stateMap.put(stateSet.getValue(), retDFA.createState());
					HashMap<String, Set<Integer>> currTrans = new HashMap<String, Set<Integer>>();
					
					for(String s : startDFA.getTransitionValues(stateSet.getKey()))
					{
						Integer dest = startDFA.getTransition(s, stateSet.getKey()).getDestinations()[0];
						if(!deadStates.contains(dest))
							currTrans.put(s, setMap.get(dest));
					}
					
					if(goalStates.contains(stateSet.getKey()))
						retDFA.setGoalState(stateMap.get(stateSet.getValue()), startDFA.getGoalLabel(stateSet.getKey()));
					
					if(startDFA.getStartState() == stateSet.getKey())
						retDFA.setStartState(stateMap.get(stateSet.getValue()));
					
					transMap.put(stateSet.getValue(), currTrans);
				}
			}
		}
		
		// Actually add the transitions.
		for(Map.Entry<Set<Integer>, Map<String, Set<Integer>>> state : transMap.entrySet())
		{
			Integer stateNumber = stateMap.get(state.getKey());
			for(Map.Entry<String, Set<Integer>> transition : state.getValue().entrySet())
			{
				Transition<String, Integer> trans = new Transition<String, Integer>(transition.getKey(), stateNumber, new Integer[]{stateMap.get(transition.getValue())});
				retDFA.addTransition(trans);
			}
		}
		
		// Now we must construct our new DFA from our old DFA and correctly merge states.
		return retDFA;
	}
	
	/**
	 * Creates a DFA given an NFA.
	 * 
	 * @param nfa the nfa to convert to a dfa.
	 * @return the dfa
	 */
	public static DFA fromNFA(TableDrivenFiniteAutomaton<String> nfa)
	{
		DFA retDFA = new DFA();
		
		if(nfa.getStates().size() < 1)
			return retDFA;
		
		// Our maps for building the DFA and understanding what sets of NFA states map to what DFA states.
		Map<Set<Integer>, Integer> stateMap = new HashMap<Set<Integer>, Integer>();
		Map<Set<Integer>, Map<String, Set<Integer>>> transMap = new HashMap<Set<Integer>, Map<String, Set<Integer>>>();
		
		// Our aggregated states.
		Set<Integer> currStates = new HashSet<Integer>(), newStates;
		currStates.add(nfa.getStartState());
		currStates = epsilonClosure(nfa, currStates);
		
		// Current transitions.
		Set<String> currTrans;
		Transition<String, Integer> trans;
		
		// Open list.
		LinkedList<Set<Integer>> open = new LinkedList<Set<Integer>>();
		open.add(currStates);
		
		// We will need to fully explore everything.
		while(!open.isEmpty())
		{
			// Get our next testing state.
			currStates = open.poll();
			if(!stateMap.containsKey(currStates))
			{
				// Update our data to build the 
				stateMap.put(currStates, retDFA.createState());
				transMap.put(currStates, new HashMap<String, Set<Integer>>());
				
				// Get our transitions.
				currTrans = new HashSet<String>();
				for(Integer state : currStates)
				{
					for(String s : nfa.getTransitionValues(state))
					{
						if(s != nfa.EMPTY_TRANSITION)
							currTrans.add(s);
					}
				}
				
				// Add successor states to the open list if they have not already been visited.
				for(String transVal : currTrans)
				{
					newStates = new HashSet<Integer>();
					
					// Get our successors for this transition.
					for(Integer state : currStates)
					{
						trans = nfa.getTransition(transVal, state);
						if(trans != null)
							newStates.addAll(Arrays.asList(trans.getDestinations()));
					}
					
					// Get the epsilon closure of the new successor, add the transition to the current state's transitions.
					newStates = epsilonClosure(nfa, newStates);
					transMap.get(currStates).put(transVal, newStates);
					
					// If we have not already explored the node, add it to the open list.
					if(!stateMap.containsKey(newStates))
						open.add(newStates);
				}
			}	
		}
		
		// Add our transitions to the DFA.
		for(Map.Entry<Set<Integer>, Map<String, Set<Integer>>> state : transMap.entrySet())
		{
			Integer stateNumber = stateMap.get(state.getKey());
			for(Map.Entry<String, Set<Integer>> transition : state.getValue().entrySet())
			{
				trans = new Transition<String, Integer>(transition.getKey(), stateNumber, new Integer[]{stateMap.get(transition.getValue())});
				retDFA.addTransition(trans);
			}
		}
		
		// Set our start state.
		newStates = new HashSet<Integer>();
		newStates.add(nfa.getStartState());
		newStates = epsilonClosure(nfa, newStates);
		retDFA.setStartState(stateMap.get(newStates));
		
		// Set our goal states.
		Set<Integer> goals = nfa.getGoalStates();
		for(Map.Entry<Set<Integer>, Integer> dfaState : stateMap.entrySet())
		{
			boolean isGoal = false;
			Set<String> labels = new HashSet<String>();
			
			for(Integer nfaState : dfaState.getKey())
			{
				String currLabel = nfa.getGoalLabel(nfaState);
				
				if(goals.contains(nfaState))
				{
					isGoal = true;
					
					if(currLabel != null)
						labels.add(currLabel);
				}
			}
			
			if(isGoal)
			{
				String label = null;
				if(labels.size() > 0)
				{
					String[] labelArr = labels.toArray(new String[0]);
					Arrays.sort(labelArr);
					label = "";
					for(String s : labelArr)
						label += (label.length() == 0)?(s):("+" + s);
				}
				
				retDFA.setGoalState(dfaState.getValue(), label);
			}
		}
		
		return retDFA;
	}
	
	
	//
	// PRIVATE METHODS
	//
	private static Set<Integer> epsilonClosure(TableDrivenFiniteAutomaton<String> fa, Set<Integer> states)
	{
		Set<Integer> interSet, newSet = new HashSet<Integer>(), retSet = new HashSet<Integer>();
		Transition<String, Integer> t;
		retSet.addAll(states);
		newSet.addAll(states);
		int oldSize = 0, newSize = retSet.size();
		
		while((oldSize != newSize) && !newSet.isEmpty())
		{
			interSet = newSet;
			newSet = new HashSet<Integer>();
			for(Integer state : interSet)
			{
				t = fa.getTransition(fa.EMPTY_TRANSITION, state);
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
	
	
	//
	// TESTING
	//
	public static void main(String[] args)
	{
		NFA n = new NFA();
		n.createState();
		n.setStartState(0);
		n.createState();
		n.addTransition(new Transition<String, Integer>("0", 0, new Integer[]{1}));
		n.createState();
		n.setGoalState(1);
		n.addTransition(new Transition<String, Integer>("1", 0, new Integer[]{2}));
		n.addTransition(new Transition<String, Integer>("0", 2, new Integer[]{2}));
		n.createState();
		n.addTransition(new Transition<String, Integer>("1", 2, new Integer[]{3}));
		n.addTransition(new Transition<String, Integer>("0", 3, new Integer[]{3}));		
		n.addTransition(new Transition<String, Integer>("1", 3, new Integer[]{2}));
		
		System.out.println("NFA");
		System.out.println(n);
		System.out.println();
		
		DFA dfa = DFA.fromNFA(n);
		System.out.println("DFA");
		System.out.println(dfa);
		System.out.println();
		
		System.out.println("Minimized");
		System.out.println(dfa.minimize());
	}
	
	
	//
	// INNER CLASS
	//
	public class TokenMatch
	{
		//
		// CLASS/INSTANCE DATA
		//
		/**
		 * The current DFA state.
		 */
		private int mState;
		
		
		//
		// CTOR
		//
		public TokenMatch(int dfaState)
		{
			mState = dfaState;
		}
		
		
		//
		// PUBLIC METHODS
		//
		/**
		 * Returns whether or not the token was accepted.
		 * 
		 * @return whether or not the token was accepted.
		 */
		public boolean isAccepted()
		{
			return getGoalStates().contains(mState);
		}
		
		/**
		 * Returns whether or not the token was rejected.
		 * 
		 * @return whether or not the token was rejected.
		 */
		public boolean isRejected()
		{
			return !getStates().contains(mState);
		}
		
		/**
		 * Returns the label that the token has been accepted with (if it has one).
		 * 
		 * @return the label that the token has been accepted with (if it has one).
		 */
		public String getLabel()
		{
			return getGoalLabel(mState);
		}
	}
}
