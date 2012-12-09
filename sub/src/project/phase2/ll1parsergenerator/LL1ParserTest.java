package project.phase2.ll1parsergenerator;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.junit.Test;

public class LL1ParserTest {

	@Test
	public void test() {
		List<Rule> rules = RuleParser.parse("test/sample/grammartest.txt");
		for (Rule r : rules) {
			System.out.println(r);
		}

	}

	@Test
	public void test2() {
		RuleParser.parse("test/sample/grammar.txt");

		Rule[] rules = RuleParser.rules.toArray(new Rule[0]);
		LL1Parser parse = ParserGenerator.generateParser(rules);

		FileInputStream fis;
		try {
			fis = new FileInputStream(new File("test/sample/script.txt"));
			AST<String> syn = parse.parse(fis);
			System.out.println(syn.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
