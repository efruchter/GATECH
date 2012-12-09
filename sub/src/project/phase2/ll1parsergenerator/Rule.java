package project.phase2.ll1parsergenerator;

/**
 * Stores the rules from the grammar
 * 
 * @author Kefu Zhou
 */
public class Rule {
	private String name;
	private boolean terminal, start;
	private Rule[][] rules;
	private StringBuffer SB;

	/**
	 * Creates an empty non terminal grammar
	 * 
	 * @param name
	 */
	public Rule(String name) {
		this.name = name;
		terminal = false;
		start = false;
	}

	/**
	 * Set whether or not the rule is a terminal.
	 * 
	 * @param t
	 */
	public void setTerminal(boolean t) {
		this.terminal = t;
	}

	/**
	 * Set whether or not this rule is the starting rule.
	 * 
	 * @param t
	 */
	public void setStart(boolean t) {
		this.start = t;
	}

	/**
	 * @return whether or not this rule is a terminal.
	 */
	public boolean isTerminal() {
		return terminal;
	}

	/**
	 * @return all the production rules.
	 */
	public Rule[][] getRules() {
		return rules;
	}

	/**
	 * Add a production rule.
	 * 
	 * @param rule
	 */
	public void addProductionRule(Rule[] rule) {
		if (rules == null) {
			rules = new Rule[1][];
			rules[0] = rule;
		} else {
			Rule[][] newRules = new Rule[rules.length + 1][];
			for (int i = 0; i < rules.length; i++) {
				newRules[i] = rules[i];
			}
			newRules[rules.length] = rule;
			rules = newRules;
		}
	}

	/**
	 * @return the ruleName.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the rule's name.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return a string representation of the rule.
	 */
	public String toString() {
		SB = new StringBuffer();
		if (this.start) {
			SB.append("[START] ");
		}
		SB.append(name + ":");

		if (terminal) {
			SB.append(" terminal\n");
		} else {
			SB.append("\n");
			for (Rule[] rs : this.rules) {
				for (Rule r : rs) {
					SB.append(r.getName());
				}
				SB.append("\n");
			}
		}
		return SB.toString();
	}

	/**
	 * @return whether or not this is the starting rule
	 */
	public boolean isStart() {
		return start;
	}
}
