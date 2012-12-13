package project.phase2.ll1parsergenerator;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.junit.Test;

public class LL1ParserTest {

	@Test
	public void testParenthesesLLParserGenerator() {
		Rule[] rules = RuleParser.parse("test_ll1parsergenerator/parentheses_test/script.txt")
				.toArray(new Rule[0]);
		LL1Parser parse = ParserGenerator.generateParser(rules);
		try {
			FileInputStream fis;
			fis = new FileInputStream(new File("test_ll1parsergenerator/parentheses_test/input1.txt"));
			AST<String> syn = parse.parse(fis);
			System.out.println(syn.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void testMiniRE() {
		List<Rule> rulesList = RuleParser.parse("test_ll1parsergenerator/minire_test/script.txt");

		Rule[] rules = RuleParser.rules.toArray(new Rule[0]);
		LL1Parser parse = ParserGenerator.generateParser(rules);

		FileInputStream fis;
		try {
			fis = new FileInputStream(new File("test_ll1parsergenerator/minire_test/input1.txt"));
			AST<String> syn = parse.parse(fis);
			System.out.println(syn.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void AST() {
		AST<String> ast = new AST<String>();
		ASTNode<String> root = new ASTNode<String>("Rule", false);
		ast.setRoot(root);
		ASTNode<String> a = new ASTNode<String>("a", false);
		ASTNode<String> plus = new ASTNode<String>("+", true);
		ASTNode<String> b = new ASTNode<String>("b", false);
		ASTNode<String> c = new ASTNode<String>("c", true);
		ASTNode<String> minus = new ASTNode<String>("-", true);
		ASTNode<String> d = new ASTNode<String>("d", true);
		ASTNode<String> e = new ASTNode<String>("e", false);
		ASTNode<String> mul = new ASTNode<String>("*", true);
		ASTNode<String> f = new ASTNode<String>("f", true);
		ASTNode<String> g = new ASTNode<String>("g", true);
		ASTNode<String> div = new ASTNode<String>("/", true);
		ASTNode<String> h = new ASTNode<String>("h", true);
		root.insert(a);
		root.insert(plus);
		root.insert(b);
		a.insert(c);
		a.insert(minus);
		a.insert(d);
		b.insert(e);
		b.insert(mul);
		b.insert(f);
		e.insert(g);
		e.insert(div);
		e.insert(h);
		ast.printAST();
	}

}

//	@Test
//	public void testConditionalLLParserGenerator() {
//		Rule[] rules = RuleParser.parse("test/sample/grammarConditional.txt")
//				.toArray(new Rule[0]);
//		
//		LL1Parser parse = ParserGenerator.generateParser(rules);
//		try {
//			FileInputStream fis;
////			fis = new FileInputStream(new File("test/sample/inputConditional1.txt"));
////			AST<String> syn = parse.parse(fis);
////			System.out.println(syn.toString());
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	