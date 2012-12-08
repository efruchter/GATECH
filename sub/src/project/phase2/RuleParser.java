package project.phase2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.LinkedList;

import project.phase2.file.FileEditor;

/**
 * Generates the rules from grammar file
 * 
 * @author Kefu Zhou
 * 
 */
public class RuleParser {
	public static String[] predef;
	public static List<Rule> rules;

	/**
	 * Adds the default terminals to the rule list. Also initializes the list.
	 */
	private static void initializeTerminals() {
		rules = new LinkedList<Rule>();
		addTerminal("E");
		addTerminal("REGEX");
		addTerminal("ASCII-STR");
		addTerminal("ID");
	}

	/**
	 * @param s
	 *            Adds the terminal s
	 */
	private static void addTerminal(String s) {
		Rule r = new Rule(s);
		r.setTerminal(true);
		rules.add(r);
	}

	/**
	 * parses the input scanner to generate the set of rules. The input scanner
	 * must have the predefined symbols, start state, and grammar as specified.
	 * 
	 * @param input
	 */
	public static List<Rule> parse(File grammarFile) {
		Scanner input = null;
		try {
			input = new Scanner(FileEditor.readEntireFile(grammarFile));
		} catch(Exception e) {
			e.printStackTrace();
		}

		String line = null;
		initializeTerminals();

		// Remove leading blank lines up to the predefines variables
		// System.out.println(grammarFile.length());
		line = input.nextLine();
		while (line.length() < 9
				|| line.substring(0, 9).compareTo("%% Tokens") != 0) {
			line = input.nextLine();
		}
		// Predefined token line
		line = input.nextLine();
		while (line.compareTo("") == 0)
			line = input.nextLine();
		predefined(line);

		// Remove any leading space before start section
		line = input.nextLine();
		while (line.length() < 8
				|| line.substring(0, 8).compareTo("%% Start") != 0) {
			line = input.nextLine();
		}
		// start symbol
		line = input.nextLine();
		while (line.compareTo("") == 0)
			line = input.nextLine();
		String start = line;

		// removing any leading space before rules section
		line = input.nextLine();
		while (line.length() < 8
				|| line.substring(0, 8).compareTo("%% Rules") != 0) {
			line = input.nextLine();
		}
		// Rules begin.
		readRules(input);

		// Set start rule
		boolean found = false;
		for (Rule r : rules) {
			if (r.getName().compareTo(start) == 0) {
				r.setStart(true);
				found = true;
			}
		}
		if (!found) {
			System.out.println("Start state is " + start
					+ " but no such rule found.");
			System.exit(0);
		}
		
		// replace E with "" 
		for (Rule r : rules) {
			if (r.getName().compareTo("E") == 0) {
				r.setName("");
			}
		}
		return rules;
	}

	/**
	 * Splits a line into the rule's name and production.
	 * 
	 * @param s
	 * @return
	 */
	private static String[] splitLine(String s) {
		String[] s2 = new String[2];
		s2[0] = "";
		s2[1] = "";
		int i = 0;
		if (s == null || s.compareTo("") == 0)
			return null;
		if (s.charAt(0) == '%')
			return null;
		// while(s.charAt(i)!='\u2192'){ TODO?
		while (s.charAt(i) != '=') {
			if (!Character.isWhitespace(s.charAt(i)))
				s2[0] += s.charAt(i);
			i++;
		}
		i++;
		while (i < s.length()) {
			if (!Character.isWhitespace(s.charAt(i)))
				s2[1] += s.charAt(i);
			i++;
		}
		return s2;
	}

	/**
	 * Reads in the predefined symbol line.
	 * 
	 * @param s
	 */
	private static void predefined(String s) {
		boolean t;
		Rule r;
		String s2 = "";
		int i = 0;
		while (i < s.length()) {
			if (s.charAt(i) == ' ') {
				t = true;
				for (Rule r2 : rules) {
					if (r2.getName().compareTo(s2) == 0)
						t = false;
				}
				if (t && s2.length() >= 1) {
					r = new Rule(s2);
					r.setTerminal(true);
					rules.add(r);
				}
				s2 = "";
				i++;
			} else {
				s2 += s.charAt(i);
				i++;
			}
		}
		if (s2.length() >= 1) {
			r = new Rule(s2);
			r.setTerminal(true);
			rules.add(r);
		}
	}

	/**
	 * Reads the rules recursively. This is so all rule names can be read before
	 * any production rules are consumed.
	 * 
	 * @param input
	 */
	private static void readRules(Scanner input) {
		if (!input.hasNext())
			return;
		String[] line = splitLine(input.nextLine());
		Rule r = null;
		LinkedList<Rule> production = new LinkedList<Rule>();
		if (line == null) {
			readRules(input);
		} else {
			for (Rule r2 : rules) {
				if (r2.getName().compareTo(line[0]) == 0) {
					r = r2;
				}
			}
			if (r == null) {
				r = new Rule(line[0]);
				rules.add(r);
			}

			readRules(input);

			while (line[1].length() != 0) {
				if (line[1].charAt(0) == ' ') {
					line[1] = line[1].substring(1);
				} else if (line[1].charAt(0) == '|') {
					line[1] = line[1].substring(1);
					r.addProductionRule(production.toArray(new Rule[production
							.size()]));
					production = new LinkedList<Rule>();
				} else {
					// System.out.println("Reading: " +line[1]);
					line[1] = matchRule(line[1], production);
				}
			}
			r.addProductionRule(production.toArray(new Rule[production.size()]));
		}
		return;
	}

	/**
	 * Matches a rule name in the production rule to a rule in the list.
	 * 
	 * @param s
	 * @param production
	 * @return
	 */
	private static String matchRule(String s, LinkedList<Rule> production) {
		Rule longestFound = null;
		String ruleName;
		for (Rule r2 : rules) {
			ruleName = r2.getName();
			if (ruleName.length() <= s.length()
					&& ruleName.compareTo(s.substring(0, ruleName.length())) == 0) {
				// System.out.println(ruleName);
				if (longestFound == null)
					longestFound = r2;
				else if (ruleName.length() > longestFound.getName().length()) {
					longestFound = r2;
				}
			}
		}
		// TODO error handling
		if (longestFound == null) {
			throw new RuntimeException("Cannot be matched: " + s);
		}
		s = s.substring(longestFound.getName().length());
		// System.out.println(line[0]+": " + ruleName);
		production.add(longestFound);
		return s;
	}
	
	// Testing
	public static void main(String[] args) {
		File grammar = new File("test/sample/grammar.txt");
		List<Rule> rules = RuleParser.parse(grammar);
		for(Rule r: rules) {
			System.out.println(r);
		}
	}
}