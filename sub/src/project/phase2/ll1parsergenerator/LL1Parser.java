package project.phase2.ll1parsergenerator;

import java.util.*;

/**
 * The representation of an LL1Parser.
 * 
 */
public class LL1Parser {
	//
	// CLASS/INSTANCE DATA
	//
	/**
	 * The names for some special cased tokens (reg ex, ascii string, id).
	 */
	public static final String EPSILON = "E";
	public static final String REGEX = "REGEX";
	public static final String ASCII = "ASCII-STR";
	public static final String ID = "ID";

	/**
	 * The rules representation.
	 */
	private Map<String, Rule> mRules;

	/**
	 * The transition table.
	 */
	private Map<String, Map<String, Integer>> mParseTable;

	//
	// CTOR
	//
	public LL1Parser() {
		mRules = new HashMap<String, Rule>();
		mParseTable = new HashMap<String, Map<String, Integer>>();
	}

	//
	// PUBLIC METHODS
	//
	/**
	 * Adds the given rule to the parser.
	 * 
	 * @param rule
	 *            the rule to add.
	 */
	public String addRule(Rule rule) {
		if (rule.isTerminal() || mRules.containsKey(rule.getName()))
			return null;

		String name = rule.getName();

		mRules.put(name, rule);
		mParseTable.put(name, new HashMap<String, Integer>());

		return name;
	}

	/**
	 * Adds the given rule sequencing to the parse table.
	 * 
	 * @param selection
	 *            rules to be added to the parse table.
	 */
	public void addRuleSelection(RuleSelection selection) {
		if (mParseTable.containsKey(selection.mRuleName)) {
			Rule rule = mRules.get(selection.mRuleName);

			if ((selection.mRule >= 0)
					&& (selection.mRule < rule.getRules().length)) {
				if (mParseTable.get(selection.mRuleName).containsKey(
						selection.mToken))
					System.out
							.println("Warning: Ambiguous grammar detected.  Results may not be correct to grammar specifications.");

				mParseTable.get(selection.mRuleName).put(selection.mToken,
						selection.mRule);
			}
		}
	}

	//
	// INNER CLASS
	//
	public static class RuleSelection {
		//
		// CLASS/INSTANCE DATA
		//
		/**
		 * The rule name this selection applies to.
		 */
		private String mRuleName;

		/**
		 * The token to transition on.
		 */
		private String mToken;

		/**
		 * The rule to transition to.
		 */
		private int mRule;

		//
		// CTOR
		//
		public RuleSelection(String name, String token, int rule) {
			mRuleName = name;
			mToken = token;
			mRule = rule;
		}
	}
}