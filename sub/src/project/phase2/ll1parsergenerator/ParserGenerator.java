package project.phase2.ll1parsergenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import project.phase2.ll1parsergenerator.LL1Parser.RuleSelection;

/**
 * Generates an LL1 Parser from the input formatted rules.
 * 
 */
public class ParserGenerator {
	/**
	 * Generates a parser based upon the given rules.
	 * 
	 * @param rules
	 *            the rules for the parser.
	 * @return a new parser.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static LL1Parser generateParser(Rule[] rules) {
		LL1Parser parse = new LL1Parser();

		for (Rule rule : rules) {
			parse.addRule(rule);

			if (rule.isStart())
				parse.setStartRule(rule);
		}

		Map[] firstMaps = getFirstMaps(rules);

		Map<String, Set<String>> ruleFirstMap = firstMaps[0];
		Map<Rule[], Set<String>> prodFirstMap = firstMaps[1];
		Map<String, Set<String>> followMap = getFollowMap(ruleFirstMap, rules);

		for (int i = 0; i < rules.length; ++i) {
			if (rules[i].isTerminal())
				continue;

			String ruleName = rules[i].getName();
			Rule[][] prodRules = rules[i].getRules();

			for (int j = 0; j < prodRules.length; ++j) {
				Set<String> tokens = new HashSet<String>(
						prodFirstMap.get(prodRules[j]));

				if (tokens.contains(LL1Parser.EPSILON)) {
					tokens.remove(LL1Parser.EPSILON);
					tokens.addAll(followMap.get(ruleName));
				}

				for (String token : tokens) {
					parse.addRuleSelection(new RuleSelection(ruleName, token, j));
				}
			}
		}

		return parse;
	}

	@SuppressWarnings("rawtypes")
	/**
	 * Get the maps of the first elements in each rule/production rule.
	 * @param rules the rules.
	 * @return the maps.
	 */
	public static Map[] getFirstMaps(Rule[] rules) {
		Map<String, Set<String>> firstMap = new HashMap<String, Set<String>>();
		Map<Rule[], Set<String>> prodFirstMap = new HashMap<Rule[], Set<String>>();

		for (Rule rule : rules) {
			if (rule.isTerminal())
				continue;

			firstMap.put(rule.getName(), new HashSet<String>());

			for (Rule[] prod : rule.getRules()) {
				prodFirstMap.put(prod, new HashSet<String>());
			}
		}

		boolean changed = true;

		while (changed) {
			changed = false;

			for (Rule rule : rules) {
				if (rule.isTerminal())
					continue;

				Set<String> first = firstMap.get(rule.getName());
				int startLen = first.size();

				for (Rule[] prodRule : rule.getRules()) {
					Set<String> prodFirst = prodFirstMap.get(prodRule);
					int prodStartLen = prodFirst.size();

					for (int i = 0; i <= prodRule.length; ++i) {
						if (i == prodRule.length) {
							prodFirst.add(LL1Parser.EPSILON);
						}

						if (prodRule[i].isTerminal()) {

							prodFirst.add(prodRule[i].getName());
							break;
						} else {
							Set<String> add = new HashSet<String>(
									firstMap.get(prodRule[i].getName()));
							add.remove(LL1Parser.EPSILON);

							prodFirst.addAll(add);

							if (!firstMap.get(prodRule[i].getName()).contains(
									LL1Parser.EPSILON))
								break;
						}
					}

					first.addAll(prodFirst);

					if (!changed && prodFirst.size() != prodStartLen)
						changed = true;
				}

				if (!changed && first.size() != startLen)
					changed = true;
			}
		}

		return new Map[] { firstMap, prodFirstMap };
	}

	/**
	 * Gets the follow map for the given rules and first map.
	 * 
	 * @param ruleFirstMap
	 *            the first map.
	 * @param rules
	 *            the rules.
	 * @return the follow map.
	 */
	public static Map<String, Set<String>> getFollowMap(
			Map<String, Set<String>> ruleFirstMap, Rule[] rules) {
		Map<String, Set<String>> followMap = new HashMap<String, Set<String>>();
		for (String s : ruleFirstMap.keySet()) {
			followMap.put(s, new HashSet<String>());
		}

		for (Rule r : rules) {
			if (r.isStart()) {
				followMap.get(r.getName()).add("$");
			}
		}

		boolean changed = true;

		while (changed) {
			changed = false;

			for (Rule rule : rules) {
				if (rule.isTerminal())
					continue;

				for (Rule[] prod : rule.getRules()) {
					for (int i = 0; i < prod.length; ++i) {
						if (!prod[i].isTerminal()) {
							Set<String> newFollow = new HashSet<String>();
							Set<String> oldFollow = followMap.get(prod[i]
									.getName());
							int startLen = oldFollow.size();

							for (int j = i + 1; j <= prod.length; ++j) {
								if (j == prod.length) {
									newFollow.addAll(followMap.get(rule
											.getName()));
									break;
								}

								if (prod[j].isTerminal()) {
									if (prod[j].getName() == LL1Parser.EPSILON) {
										continue;
									} else {
										newFollow.add(prod[j].getName());
										break;
									}
								} else {
									Set<String> add = new HashSet<String>(
											ruleFirstMap.get(prod[j].getName()));
									add.remove(LL1Parser.EPSILON);

									newFollow.addAll(add);

									if (!ruleFirstMap.get(prod[j].getName())
											.contains(LL1Parser.EPSILON))
										break;
								}
							}

							oldFollow.addAll(newFollow);

							if (!changed && oldFollow.size() != startLen)
								changed = true;
						}
					}
				}
			}
		}

		return followMap;
	}

	/**
	 * Returns the first elements accepted by the given production rule.
	 * 
	 * @param rules
	 *            the production rule.
	 * @return the first set.
	 */
	public static Set<String> first(Rule[] rules) {
		return first(rules, new HashSet<String>());
	}

	/**
	 * Utilized to avoid infinite loops.
	 */
	private static Set<String> first(Rule[] rules, Set<String> visited) {
		Set<String> curr = new HashSet<String>();

		for (Rule rule : rules) {
			// We can just skip in this case right? Since we are adding nothing
			// new to the rules.
			if (visited.contains(rule.getName()))
				continue;

			if (curr.contains(LL1Parser.EPSILON))
				curr.remove(LL1Parser.EPSILON);

			if (rule.isTerminal()) {
				curr.add(rule.getName());
			} else {
				Set<String> newVisited = new HashSet<String>(visited);
				newVisited.add(rule.getName());

				for (Rule[] newRules : rule.getRules()) {
					curr.addAll(first(newRules, newVisited));
				}
			}

			if (!curr.contains(LL1Parser.EPSILON))
				break;
		}

		return curr;
	}

	//
	// TESTING
	//
	public static void main(String[] args) throws IOException, ParseException {
		RuleParser.parse("test/sample/grammar.txt");

		Rule[] rules = RuleParser.rules.toArray(new Rule[0]);
		LL1Parser parse = ParserGenerator.generateParser(rules);

		FileInputStream fis = new FileInputStream(new File(
				"test/sample/script.txt"));

		AST<String> syn = parse.parse(fis);

		System.out.println(syn.toString());
		;
	}
}
