
package project.phase2.ll1parsergenerator.dfastuff;

import java.util.*;

/**
 * A class used to represent a character class during parsing.
 *
 */
public class CharacterClass
{
	//
	// CLASS/INSTANCE DATA
	//
	/**
	 * The name of this class.
	 * This will be null if unnamed.
	 */
	private String mName;
	
	/**
	 * The values in this character class.
	 */
	private Set<Character> mValues;
	
	
	//
	// CTOR
	//
	/**
	 * Creates a new character class with the given values and name.
	 * 
	 * @param name the name of the character class.
	 * @param vals the values of the character class.
	 */
	public CharacterClass(String name, Set<Character> vals)
	{
		mName = name;
		mValues = vals;
	}
	
	
	//
	// PUBLIC METHODS
	//
	/**
	 * Generates an NFA to accept any of the values in this class.
	 * 
	 * @return an NFA for any of the values in this class.
	 */
	public NFA getNFA()
	{
		NFA ret = new NFA();
		Integer start = ret.createState();
		ret.setStartState(start);
		Integer state = ret.createState();
		
		for(char c : mValues)
		{
			String s = Character.toString(c);
			ret.addTransition(new Transition<String, Integer>(s, start, new Integer[]{state}));
			ret.setGoalState(state);
		}
		
		return ret;
	}
	
	/**
	 * Returns whether or not this character class could still be the result for the given string if more characters were added.
	 * 
	 * @param str the testing string.
	 * @return true if the given string is a substring of the name starting at the beginning, false otherwise.
	 */
	public boolean matchesClassName(String str)
	{
		if(str == null && mName == null)
			return true;
		else if(str == null || mName == null)
			return false;
		
		return mName.startsWith(str);
	}
	
	/**
	 * Sets the name.
	 * 
	 * @param name the new name.
	 */
	public void setName(String name)
	{
		mName = name;
	}
	
	/**
	 * Returns the name.
	 * 
	 * @return the name.
	 */
	public String getName()
	{
		return mName;
	}
	
	/**
	 * Merges this character class with the given one and returns the result.
	 * 
	 * @param cc the character class to merge with.
	 * @return the merged character class.
	 */
	public CharacterClass merge(CharacterClass cc)
	{
		Set<Character> newSet = new HashSet<Character>();
		
		newSet.addAll(this.mValues);
		newSet.addAll(cc.mValues);

		String name = null;
		
		if(this.mName != null)
		{
			name = "";
			name += this.mName;
		}
		
		if(cc.mName != null)
		{
			name = (name == null)?(cc.mName):(name + "+" + cc.mName);
		}
		
		return new CharacterClass(name, newSet);
	}
	
	/**
	 * Returns a representation of the character class with the same form as the ones given for fromSet.
	 * Specifically, this will create a character class of the form "[abcdefg]" if those are the characters in the class.
	 * 
	 * @return a string representation of the character class.
	 */
	public String getClassDescriptor()
	{
		String ret = "[";
		
		for(Character c : mValues)
		{
			for(char escp : Parser.classEscape)
			{
				if(escp == c)
				{
					ret += "\\";
					break;
				}
			}
			ret += c.toString();
		}
		
		ret += "]";
		return ret;
	}
	
	//
	// CLASS METHODS
	//
	/**
	 * Creates a new character class for the given range of values in the form "a-z".
	 * 
	 * @param range the range of values in the form "a-z".
	 * @return a new character class.
	 */
	public static CharacterClass fromRange(String range)
	{
		Set<Character> chars = new HashSet<Character>();
		
		if(range.length() != 3)
			return null;
		
		char ch1 = range.charAt(0), ch2 = range.charAt(2);
		if((int)ch1 > (int)ch2 || range.charAt(1) != '-')
			throw new IndexOutOfBoundsException("Invalid Range");
		
		int start = (int)ch1, end = (int)ch2;
		
		for(int i = start; i <= end; i++)
			chars.add((char)i);
		
		return new CharacterClass(null, chars);
	}
	
	/**
	 * Creates a new character class for the given set of values in the form "abcde".
	 * This character class will accept any one of the given values.
	 * 
	 * @param set a string containing all possible values in the form "abcde".
	 * @return a new character class for the given set of values.
	 */
	public static CharacterClass fromSet(String set)
	{
		Set<Character> chars = new HashSet<Character>();
		for(int i = 0; i < set.length(); i++)
			chars.add(set.charAt(i));
		
		return new CharacterClass(null, chars);
	}
		
	
	/**
	 * Creates a new character class from another character class excluding a set of values.
	 * @param cSmall the set of characters to exclude from cLarge.
	 * @param cLarge the set to exclude the characters from.
	 * @return a new character class.
	 */
	public static CharacterClass fromExclude(CharacterClass cSmall, CharacterClass cLarge)
	{
		Set<Character> finalSet = new HashSet<Character>();
		finalSet.addAll(cLarge.mValues);
		finalSet.removeAll(cSmall.mValues);
		
		return new CharacterClass(null, finalSet);
	}
	
	//
	// TESTING
	//
	public static void main(String[] args)
	{
		CharacterClass cc = CharacterClass.fromRange("a-h"), cc2;
		cc.setName("A Through H");
		
		System.out.println("Name Matching Tests");
		System.out.println("Name: " + cc.getName());
		System.out.println("'A Through H' Matches: " + cc.matchesClassName("A Through H"));
		System.out.println("'Failure' Matches: " + cc.matchesClassName("Failure"));
		System.out.println("'A T' Matches: " + cc.matchesClassName("A T"));
		System.out.println("'A Through' Matches: " + cc.matchesClassName("A Through"));
		System.out.println("'A Through H++' Matches: " + cc.matchesClassName("A Through H++"));
		System.out.println();
		
		System.out.println("NFA For 'a-h'");
		System.out.println(cc.getNFA());
		System.out.println();
		
		cc2 = CharacterClass.fromSet("abcd");
		
		System.out.println("NFA For 'abcd'");
		System.out.println(cc2.getNFA());
		System.out.println();
		
		cc2 = CharacterClass.fromExclude(CharacterClass.fromRange("c-e"), cc);
		
		System.out.println("NFA For exclude 'c-e' from 'a-h'");
		System.out.println(cc2.getNFA());
		System.out.println();
		
		cc2 = CharacterClass.fromExclude(CharacterClass.fromSet("bdfh"), cc);
		
		System.out.println("NFA For exclude 'bdfh' from 'a-h'");
		System.out.println(cc2.getNFA());
		System.out.println();
	}
}
