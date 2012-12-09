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
