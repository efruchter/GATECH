package project.phase2.ll1parsergenerator.dfastuff;
import java.util.Scanner;
import java.util.LinkedList;
/**
 * The Parser class takes in a Scanner of an input stream and generates a NFA for the tokens
 * within that stream.
 * The input file should contain a predefined classes, a empty or non token line, then
 * the tokens with which the nfa will be generated.
 * The parser class uses a recursive descent parser based on the grammar rules provided.
 * All methods are static.
 */
public class Parser {
	private static String line;
	private static int index;
	private static boolean DEBUG=false;
	private static final CharacterClass DOT = CharacterClass.fromRange((char)0x20+"-"+(char)0x7e);
	private static LinkedList<CharacterClass> predefined;
	//TODO test cases
	public static void main(String[] args){
		predefined = new LinkedList<CharacterClass>();
		CharacterClass c = CharacterClass.fromRange("0-9");
		c.setName("$DIGIT");
		predefined.add(c);
		//Testing repitition, union, and parenthesis
		line="(a|b)*";
		index=0;
		System.out.println("Test Case 1:");
		System.out.println("NFA for (a|b)* \n");
		System.out.println(regEx() +"\n\n");
		
		System.out.println("Test Case 2:");
		line="(a|b)+";
		index=0;
		System.out.println("NFA for (a|b)+ \n");
		System.out.println(regEx()+"\n\n");
		
		//testing for character classes
		line="[ a-d0-3]";
		index=0;
		System.out.println("Test Case 3:");
		System.out.println("NFA for [ a-d0-3] \n");
		System.out.println(regEx()+"\n\n");
		
		//testing escaped characters
		line="\\*[\\[-\\]]";
		index=0;
		System.out.println("Test Case 4:");
		System.out.println("NFA for \\*[\\[-\\]] \n");
		System.out.println(regEx()+"\n\n");
		
		//testing exclude sets
		line="[^1-9] IN [0-9]";
		index=0;
		System.out.println("Test Case 5:");
		System.out.println("NFA for [^1-9] IN [0-9] \n");
		System.out.println(regEx()+"\n\n");
		
		//testing predefined classes
		line="$DIGIT";
		index=0;
		System.out.println("Test Case 6:");
		System.out.println("NFA for $DIGIT, where $DIGIT is [0-9]\n");
		System.out.println(regEx()+"\n\n");
		
	}
	/**
	 * If the RegEx is enclosed in ''
	 * @param s
	 * @return
	 */
	public static NFA fromString2(String s){
		String s2="";
		int i=0;
		while (s.charAt(i)!='\''){
			i++;
		}
		i++;
		while (s.charAt(i)!='\''){
			s2+=s.charAt(i);
			i++;
		}
		return parseNFA(new Scanner(s2));
	}
	//Parse regex from string
	public static NFA fromString(String s){
		line=s;
		index=0;
		predefined = new LinkedList<CharacterClass>();		
		return regEx();
	}
	/**
	 * Generates a NFA that detects tokens as defined in the input stream.
	 * @param input A scanner containing the input stream
	 * @return The NFA generated from the scanner
	 */
	public static NFA parseNFA(Scanner input){
		NFA sum = null;
		String[] s=null;
		NFA identifier;
		CharacterClass def;
		predefined = new LinkedList<CharacterClass>();
		
		//finding the first predefined class
		while(s == null)
			s = split(input.nextLine());
		
		//generating the predefined character classes
		while(input.hasNext() && s!=null){
			line=s[1];
			index=0;
			def = charClass();
			def.setName(s[0]);
			predefined.add(def);
			s=split(input.nextLine());
		}
		
		//printing out predefined character classes
		if (DEBUG)
			for (CharacterClass c : predefined)
				System.out.println(c.getName()+" "+c.getClassDescriptor());
		
		//Generating the NFA for each token and unioning them.
		while (input.hasNext()){
			s=split(input.nextLine());
			if (s!=null){
				line=s[1];
				index=0;
				identifier=regEx();
				identifier.setGoalLabels(s[0]);
				if (sum==null) sum=identifier;
				else sum = (NFA)sum.union(identifier);
			}
		}
		return sum;
	}
	
	/**
	 * <reg-ex>
	 * Generates a NFA for a regular expression stored in 'line' starting at index 'index'
	 * @return the generated NFA
	 */
	private static NFA regEx(){
		return rExp();
	}
	/**
	 * <rexp>
	 * Generates a NFA for a regular expression stored in 'line' starting at index 'index'
	 * @return the generated nFA
	 */
	private static NFA rExp(){
		if (DEBUG) System.out.println("rExp");
		NFA exp = rExp1();
		NFA exp2 = rExp_();
		return (NFA)exp.union(exp2);
	}
	/**
	 * <rexp'>
	 * Detects and consumes union operators
	 * @return the generated NFA from the unioned regex
	 */
	private static NFA rExp_(){
		if (DEBUG) System.out.println("rExp'");
		if (peek()=='|'){
			match('|');
			NFA exp = rExp1();
			NFA exp2 = rExp_();
			return (NFA)exp.union(exp2);
		}
		return null;
	}
	/**
	 * <rexp1>
	 * Detects and consumes everything with other than |.
	 * @return the generated NFA
	 */
	private static NFA rExp1(){
		if (DEBUG) System.out.println("rExp1");
		NFA exp1 = rExp2();
		if (exp1==null) exp1=NFA.nullNFA();
		NFA exp2 = rExp1_();
		return (NFA)exp1.concat(exp2);
	}
	/**
	 * <rexp1'>
	 * Detects concatenated expressions
	 * @return the generated NFA
	 */
	private static NFA rExp1_(){
		if (DEBUG) System.out.println("rExp1'");
		NFA exp1 = rExp2();
		if (exp1!=null){
			NFA exp2 = rExp1_();
			return (NFA)exp1.concat(exp2);
		}
		return null;
	}
	/**
	 * <rexp2>
	 * Detects parenthesis, lone characters, and character classes.
	 * @return the generated NFA
	 */
	private static NFA rExp2(){
		if (DEBUG) System.out.println("rExp2");
		NFA exp;
		if (peek()=='('){
			match('(');
			exp = rExp();
			match(')');
			exp = rExp2Tail(exp);
		}
		else if (peek()!='$' && isReChar()){
			char c = peek();
			match(c);
			exp = CharacterClass.fromSet(""+c).getNFA();
			exp = rExp2Tail(exp);
		}
		else{
			exp=rExp3();
		}
		return exp;
	}
	/**
	 * <rexp2-tail>
	 * Detects a + or * operator following a nfa and returns the resulting nfa.
	 * @param nfa The preceding nfa
	 * @return nfa*, nfa+, or nfa depending on detected operators
	 */
	public static NFA rExp2Tail(NFA nfa){
		if (DEBUG) System.out.println("rExp2Tail");
		if (peek()=='+'){
			match('+');
			return (NFA)nfa.concat(nfa.star());
		}
		if (peek()=='*'){
			match('*');
			return (NFA)nfa.star();
		}
		return nfa;
	}
	/**
	 * <rexp3>
	 * Detects character classes and implied empty strings
	 * @return the generated NFA
	 */
	public static NFA rExp3(){
		if (DEBUG) System.out.println("rExp3");
		CharacterClass chars = charClass();
		if (chars==null) return null;
		return chars.getNFA();
	}
	/**
	 * <char-class>
	 * Detects character classes
	 * @return the detected character class
	 */
	private static CharacterClass charClass(){
		if (DEBUG) System.out.println("charClass");
		if (peek()=='.'){
			match('.');
			return DOT;
		}
		if (peek()=='['){
			index++;
			return charClass1();
		}
		if (peek()=='$'){
			CharacterClass c = preClass();
			if (c==null){
				match('$');
				c=CharacterClass.fromSet("$");
			}
			return c;
		}
		return null;
	}
	/**
	 * <char-class1>
	 * Detects exclude sets and ranges
	 * @return the detected character class
	 */
	private static CharacterClass charClass1(){
		if (DEBUG) System.out.println("charClass1");
		if (peek()=='^'){
			return excludeSet();
		}
		return charSetList(null);
	}
/**
 * <char-set-list>
 * Detects characters within brackets
 * @param chars the characters found thus far
 * @return the character class created thus far within the brackets
 */
	private static CharacterClass charSetList(CharacterClass chars){
		if (DEBUG) System.out.println("charSetList");
		if (peek()==']'){
			match(']');
			return chars;
		}
		if (chars==null) chars=charSet();
		else chars = chars.merge(charSet());
		return charSetList(chars);
	}
	
	/**
	 * <char-set>
	 * Detects a character and returns the appropriate character class.
	 * Will also detect character ranges.
	 * @return the character class
	 */
	private static CharacterClass charSet(){
		if (DEBUG) System.out.println("charSet");
		char c = peek();
		if (isClassChar()){
			c = peek();
			matchClassChar(c);
		}
		else throw(new RuntimeException("" + c+ " is not a class character"));
		return charSetTail(c);
	}
	
	/**
	 * <char-set-tail>
	 * Detects ranges following a detected character-
	 * i.e. if 'a' was found then it looks for the "-z".
	 * @param c the detected char
	 * @return a character class based on whether or not a range was detected
	 */
	private static CharacterClass charSetTail(char c){
		if (DEBUG) System.out.println("charSet");
		if (peek()=='-'){
			matchClassChar('-');
			char d=peek();;
			if (isClassChar()){
				d=peek();
				matchClassChar(d);
			}
			else throw(new RuntimeException("" + peek()+ " is not a class character"));
			if (d<c) throw(new RuntimeException(""+c+"-"+d+" is not a valid range."));
			return CharacterClass.fromRange("" + c+'-'+d);
		}
		return CharacterClass.fromSet(""+c);
	}
	
	/**
	 * <exclude-set>
	 * Generates a character class from an exclude set.
	 * @return the character class.
	 */
	private static CharacterClass excludeSet(){
		if (DEBUG) System.out.println("excludeSet");
		match('^');
		CharacterClass excluded = charSet();
		match(']');
		match('I');
		match('N');
		return CharacterClass.fromExclude(excluded,excludeSetTail());
	}
	
	/**
	 * <exclude-set-tail>
	 * @return the character class from which characters are excluded.
	 */
	private static CharacterClass excludeSetTail(){
		if (DEBUG) System.out.println("excludeSetTail");
		if (peek()=='$'){
			return preClass();
		}
		matchCharSetOpen();
		CharacterClass chars = charSet();
		match(']');
		return chars;
	}
	
	/**
	 * Checks the expression for predefined classes starting at the current index.
	 * Consumes the characters if a class is matched.
	 * @return the predefined class, or null if none matched.
	 */
	private static CharacterClass preClass(){
		if (DEBUG) System.out.println("preClass");
		String name;
		for (CharacterClass charClass: predefined){
			name=charClass.getName();
			if (line.length()>=index+name.length()){
				if (name.compareTo(line.substring(index,index+name.length()))==0){
					index+=name.length();
					if(peek()==' ') match(' ');
					if (DEBUG) System.out.println("matched " + name);
					return charClass;	
				}
			}
		}
		return null;
	}
	
	/**
	 * Matches the char c with the next char. Will return false if they do not match.
	 * Also increments the index of the string. Consumes any subsequent whitespace.
	 * @param c
	 * @return true if c is the next character, false otherwise
	 */
	private static boolean match(char c){
		if (DEBUG) System.out.println("matched "+ c);
		if (line.charAt(index)==c){
			index++;
			if (peek()==' ' && c!='\\') 
				match(' ');
			return true;
		}
		System.out.println(""+ c + " expected, " + line.charAt(index) + " found");
		index++;
		return false;
	}
	/**
	 * A match which does not remove spaces within a newly opened character class.
	 * @return true if c is the next character, false otherwise
	 */
	private static boolean matchCharSetOpen()
	{
		if (DEBUG) System.out.println("matched [");
		if (line.charAt(index)=='['){
			index++;
			return true;
		}
		System.out.println("[ expected, " + line.charAt(index) + " found");
		index++;
		return false;
	}
	/**
	 * Matches the char c with the next char. Will return false if they do not match.
	 * Also increments the index of the string. Does NOT consume any subsequent whitespace.
	 * @param c
	 * @return true if c is the next character, false otherwise
	 */
	private static boolean matchClassChar(char c){
		if (DEBUG) System.out.println("matched in class def "+ c);
		if (line.charAt(index)==c){
			index++;
			return true;
		}
		System.out.println(""+ c + " expected in class def, " + line.charAt(index) + " found");
		index++;
		return false;
	}
	/**
	 * @return the next char
	 */
	private static char peek(){
		if (index < line.length()) return line.charAt(index);
		return (char)-1;
	}
	
	/**
	 * @return whether or not the next character is a reChar.
	 */
	private static boolean isReChar(){
		char c = peek();
		if (c == '\\'){
			index++;
			if (DEBUG) System.out.println("Escape found");
			if (!isEscape(peek())){
				throw(new RuntimeException("Invalid escape. "
						+ peek() + " was escaped but is not an escaped character."));
			}
			return true;
		}
		if (c<0x20 || c>0x7e) return false;
		if (isEscape(c)) return false;
		return true;
	}
	/**
	 * @return whether or not the next character is a class character. Consumes escapes.
	 */
	private static boolean isClassChar(){
		char c=peek();
		if (c=='\\'){
			matchClassChar(c);
			if (!isClassEscape(peek())){
				throw(new RuntimeException("Invalid escape. "
						+peek() +" was escaped in a class definition."));
			}
			return true;
		}
		if (c<0x20 || c>0x7e) return false;
		if (isClassEscape(c)) return false;
		return true;
	}
	
	/**
	 * Used for splitting a line into its identifier and regex
	 * @return an string array. s[0] is the token and s[1] is the regex.
	 */
	private static String[] split(String line){
		if(line==null) return null;
		if(line.length()==0 || line.charAt(0)!='$') return null;
		String out[] = new String[2];
		out[0]="";
		out[1]="";
		int i=0;
		while (!Character.isWhitespace(line.charAt(i))){
			out[0]+=line.charAt(i++);
			if (index==line.length())
				throw(new RuntimeException("Improper line, no regular expression for token: "
						+ out[0]));
		}
		while (Character.isWhitespace(line.charAt(i))){
			i++;
			if (index==line.length())
				throw(new RuntimeException("Improper line, no regular expression for token: "
						+ out[0]));
		}
		while (i<line.length()){
			out[1]+=line.charAt(i++);
		}
		return out;
	}
	/**
	 * @param c
	 * @return whether or not c must be escaped as a ReChar.
	 */
	private static boolean isEscape(char c){
		for (char d : escape){
			if (c==d) return true;
		}
		return false;
	}
	/**
	 * @param c
	 * @return whether or not c must be escaped as a class char.
	 */
	private static boolean isClassEscape(char c){
		for (char d : classEscape){
			if (c==d) return true;
		}
		return false;
	}
	/**
	 * Class characters that must be escaped
	 */
	public final static char[] classEscape={'\\','^','[',']','-'};
	/**
	 * ReChar that must be escaped
	 */
	public final static char[] escape={'\\','*','+', '?', '|', '[', ']', '(', ')', '.','\'','\"',' '};
}
