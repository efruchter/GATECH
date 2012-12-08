package project.phase2.ll1parsergenerator;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import project.scangen.tokenizer.Token;

public class LL1ParserTest {

	@Test
	public void test() {
		List<Rule> rules = RuleParser.parse("test/sample/grammartest.txt");
		for(Rule r: rules) {
			System.out.println(r);
		}
		
	}
	
	@Test
	public void test2() {
//        String input = "begin a := a + b; end";
//        System.out.println("Input: " + input);
//        Token ts = new Token(type, value)
//        List<Token> answer = ts.
//        System.out.println("Input Tokens: " + answer.toString());
//        MyParser parser = new MyParser(answer, "/org/resources/tiny.txt");
//        parser.algorithm();
	}

}
