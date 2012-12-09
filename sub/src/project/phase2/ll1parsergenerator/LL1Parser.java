package project.phase2.ll1parsergenerator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import project.phase2.ll1parsergenerator.dfastuff.Parser;

/**
 * The representation of an LL1Parser.
 * 
 */
public class LL1Parser {
	//
	// CLASS/INSTANCE DATA
	//
	/**
	 * Debugging flag.
	 */
	private static final boolean DEBUG = false;

	/**
	 * The names for some special cased tokens (epsilon, reg ex, ascii string,
	 * id).
	 */
	public static final String EPSILON = null;
	public static final String REG_EX = "REGEX";
	public static final String ASCII = "ASCII-STR";
	public static final String ID = "ID";

	/**
	 * Whether or not the scanning DFA should be minimized. This will
	 * significantly increase preprocessing time.
	 */
	public static boolean MINIMIZE_SCANNER = true;

	/**
	 * The ending token used to tell us that we are done parsing.
	 */
	public static final String END = "$";

	/**
	 * The rules representation.
	 */
	private Map<String, Rule> mRules;

	/**
	 * The transition table.
	 */
	private Map<String, Map<String, Integer>> mParseTable;

	/**
	 * The valid tokens scanner for this parser.
	 */
	private DFAScanner mScanner;

	/**
	 * The start rule.
	 */
	private String mStartRule;

	//
	// CTOR
	//
	public LL1Parser() {
		mRules = new HashMap<String, Rule>();
		mParseTable = new HashMap<String, Map<String, Integer>>();

		mScanner = new DFAScanner(MINIMIZE_SCANNER);

		String letters = "[a-zA-Z]";
		String numbers = "[0-9]";
		String anything = "[" + (char) 0x20 + "-" + (char) 0x7e + "]";

		String ascii = "\\\"([^\"] IN " + anything + ")*\\\"";
		mScanner.addRegex(ascii, ASCII);

		String regex = "\\\'([^\'] IN " + anything + ")*\\\'";
		mScanner.addRegex(regex, REG_EX);

		String id = letters;
		for (int i = 0; i < 9; i++) {
			id = id + "((" + letters + "|" + numbers + "|_)";
		}

		for (int i = 0; i < 9; i++) {
			id = id + "|)";
		}

		mScanner.addRegex(id, ID);

		mScanner.addRegex(END, END);
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
		String name = rule.getName();

		if (rule.isTerminal()) {
			if (name == EPSILON || name.equals(ASCII) || name.equals(END)
					|| name.equals(ID) || name.equals(REG_EX))
				return null;

			String newName = name;
			int added = 0;

			for (int i = 0; i < name.length(); i++) {
				for (int j = 0; j < Parser.escape.length; j++) {
					if (Parser.escape[j] == name.charAt(i)) {
						newName = newName.substring(0, i + added) + "\\"
								+ name.substring(i);
						added++;
					}
				}
			}

			mScanner.addRegex(newName, name);

			return null;
		}

		if (mRules.containsKey(name))
			return null;

		mRules.put(name, rule);
		mParseTable.put(name, new HashMap<String, Integer>());

		return name;
	}

	/**
	 * Adds the given rule selection criteria to the parse table.
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
						selection.mToken)) {
					System.out
							.println("Warning: Ambiguous grammar detected on: ("
									+ selection.mRuleName
									+ ", \""
									+ selection.mToken
									+ "\").  Results may not be correct to grammar specifications.");
				}

				mParseTable.get(selection.mRuleName).put(selection.mToken,
						selection.mRule);
			}
		}
	}

	/**
	 * Sets the given rule as the start rule.
	 * 
	 * @param rule
	 *            the rule to set as the start rule.
	 */
	public void setStartRule(Rule rule) {
		mStartRule = rule.getName();
	}

	/**
	 * Parses the given file and returns an abstract syntax tree for the file.
	 * 
	 * @param stream
	 *            the stream to parse.
	 * @return an abstract syntax tree.
	 * @throws IOException
	 * @throws ParseException
	 */
	public AST<String> parse(InputStream stream) throws IOException,
			ParseException {
		ParseStack p = new ParseStack();

		return p.parse(stream);
	}

	/**
	 * Returns a string representation of this parser.
	 * 
	 * @return a string representation.
	 */
	public String toString() {
		String ret = "LL1Parser:(\nRules:[\n";
		for (Rule rule : mRules.values()) {
			String currName = rule.getName();
			for (Rule[] prod : rule.getRules()) {
				ret = ret + currName + "->";
				for (Rule elt : prod) {
					ret += elt.getName() + " ";
				}
				ret = ret.trim();
				ret += ",\n";
			}
		}

		ret = ret.substring(0, ret.length() - 2);

		ret += "],\n";
		ret += "\nParse Table:[\n";

		for (Map.Entry<String, Map<String, Integer>> ruleName : mParseTable
				.entrySet()) {
			String currName = ruleName.getKey();
			ret += currName + ": {";
			for (Map.Entry<String, Integer> transition : ruleName.getValue()
					.entrySet()) {
				ret += currName + "-" + transition.getKey() + "->"
						+ transition.getValue() + ", ";
			}

			ret = ret.substring(0, ret.length() - 2);
			ret += "},\n";
		}

		ret = ret.substring(0, ret.length() - 2);
		ret += "])";

		return ret;
	}

	//
	// INNER CLASS
	//
	/**
	 * Provides an easy interface for representing an entry in the parse table.
	 */
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

	/**
	 * Helper class used during parsing.
	 */
	private class ParseStack extends InputStream {
		//
		// CLASS/INSTANCE DATA
		//
		/**
		 * The back-end stack that this class manages.
		 */
		private Stack<Rule> mStack;

		/**
		 * The abstract syntax tree that this class builds during parsing.
		 */
		private AST<String> mResult;

		/**
		 * The item being read.
		 */
		private InputStreamReader mStream;

		/**
		 * Used in the case that a token needs to be put back into the stream.
		 * Tokens will be retrieved in reverse order that they
		 */
		private String mBuffer;

		/**
		 * The character location in the stream.
		 */
		private int mLocation;

		/**
		 * The line location in the stream.
		 */
		@SuppressWarnings("unused")
		private int mLine;

		//
		// PUBLIC METHODS
		//
		/**
		 * Parses the given input stream and returns the abstract syntax tree.
		 * 
		 * @param stream
		 *            the stream to parse.
		 * @return the resultant tree.
		 */
		public AST<String> parse(InputStream stream) throws IOException,
				ParseException {
			mStream = new InputStreamReader(stream);
			mBuffer = "";
			mStack = new Stack<Rule>();

			Rule endRule = new Rule(END);
			endRule.setTerminal(true);

			mStack.push(endRule);
			mStack.push(mRules.get(mStartRule));

			mResult = new AST<String>();

			mResult.root = parse();

			char next = getNextCharacter();
			Rule r = mStack.pop();

			if (r.getName().equals(END) && Character.toString(next).equals(END))
				return mResult;
			else if (mStream.ready())
				throw new ParseException(
						"Finished parsing with content remaining in the file.",
						mLocation);
			else if (!mStack.empty())
				throw new ParseException("Input ended unexpectedly.", mLocation);
			else
				throw new ParseException("An unexpected error occured.",
						mLocation);
		}

		//
		// PRIVATE METHODS
		//
		private char getNextCharacter() throws IOException, ParseException {
			char next;

			if (mBuffer.length() > 0) {
				next = mBuffer.charAt(0);

				if (next != (char) -1)
					mBuffer = mBuffer.substring(1);
				else
					return next;
			} else {
				next = (char) mStream.read();
				if (next == (char) -1) {
					next = '$';
					mBuffer = ((char) -1) + mBuffer;
				}
				if (DEBUG)
					System.out.print(next);
			}

			mLocation++;

			if (next == '\n') {
				mLine++;
			}

			return next;
		}

		private void replaceCharacter(char c) {
			mBuffer = c + mBuffer;

			mLocation--;

			if (c == '\n') {
				mLine--;
			}
		}

		private ASTNode<String> parse() throws IOException, ParseException {
			Rule currRule = mStack.pop();

			if (currRule.isTerminal()) {
				String currName = currRule.getName();

				ASTNode<String> ret;

				if (currName == EPSILON) {
					ret = new ASTNode<String>(EPSILON);
					ret.isTerminal = true;

					return ret;
				}

				// Remove leading whitespace.
				Character next;
				while (Character.isWhitespace((next = getNextCharacter()))
						&& (next != (char) -1))
					;

				int backTrack = 1;

				if (currName.equals(END)) {
					if (!END.equals("" + next))
						throw new ParseException(
								"Expected end of input. Instead got: "
										+ Character.toString(next), mLocation
										- backTrack);

					return null;
				} else if (currName.equals(ASCII)) {
					if (next != '\"')
						throw new ParseException(
								"Expected ASCII string enclosed with \"\"\".",
								mLocation - backTrack);

					String asc = "";

					while ((next = getNextCharacter()) != '\"'
							&& (next != (char) -1)) {
						asc += next;
						backTrack++;
					}

					if (next == (char) -1)
						throw new ParseException(
								"Expected ASCII string enclosed with \"\"\".",
								mLocation - backTrack);

					ret = new ASTNode<String>(asc);
					ret.isTerminal = true;

					return ret;
				} else if (currName.equals(REG_EX)) {
					if (next != '\'')
						throw new ParseException(
								"Expected regular expression enclosed with \"\'\".",
								mLocation - backTrack);

					String reg = "";

					while ((next = getNextCharacter()) != '\''
							&& (next != (char) -1)) {
						reg += next;
						backTrack++;
					}

					if (next == (char) -1)
						throw new ParseException(
								"Expected regular expression enclosed with \"\'\".",
								mLocation - backTrack);

					// Create our DFA that we will need to recognize the
					// content.
					try {
						Parser.fromString(reg);
					} catch (Exception e) {
						throw new ParseException(
								"An error occured while parsing the given regular expression: "
										+ reg, mLocation - backTrack);
					}

					ret = new ASTNode<String>(reg);
					ret.isTerminal = true;

					return ret;
				} else if (currName.equals(ID)) {
					if (!Character.isLetter(next))
						throw new ParseException(
								"Identifiers must begin with a letter.",
								mLocation - backTrack);

					String id = "" + next;

					while ((id.length() < 10)
							&& (Character
									.isLetterOrDigit((next = getNextCharacter())) || next == '_')) {
						id += next;
					}

					// Replace a character if we determined it was not part of
					// our identifier.
					if (!(Character.isLetterOrDigit(next) || next == '_'))
						replaceCharacter(next);

					ret = new ASTNode<String>(id);
					ret.isTerminal = true;

					return ret;
				} else {
					String curr = "" + next;

					while (curr.length() < currName.length()) {
						curr += getNextCharacter();
						backTrack++;
					}

					if (!curr.equals(currName))
						throw new ParseException(
								"Unexpected token encounterd: " + curr
										+ "; Expected: " + currName, mLocation
										- backTrack);

					ret = new ASTNode<String>(curr);
					ret.isTerminal = true;

					return ret;
				}
			} else {
				String type = determineNextTokenType();

				if (!mParseTable.get(currRule.getName()).containsKey(type)) {
					throw new ParseException(
							"No Grammar Rule Found for Token Type: " + type,
							mLocation);
				} else {
					int rule = mParseTable.get(currRule.getName()).get(type);

					Rule[] prodRule = currRule.getRules()[rule];
					for (int i = 0; i < prodRule.length; i++) {
						mStack.push(prodRule[prodRule.length - (i + 1)]);
					}

					ASTNode<String> ret = new ASTNode<String>(
							currRule.getName());
					for (int i = 0; i < prodRule.length; i++) {
						ret.insert(parse());
					}

					if ("<file-names>".equals(currRule.getName())) {
						String src = ret.children.get(0).children
								.get(0).value;
						String dest = ret.children.get(2).children
								.get(0).value;

						if (src.equals(dest))
							throw new ParseException(
									"Source and destination file same in Replace or Recursive Replace.",
									mLocation);
					} else if (prodRule.length > 0
							&& "recursivereplace".equals(prodRule[0].getName())) {
						String regex = ret.children.get(1).value;
						String ascii = ret.children.get(3).value;

						if (regex.equals(ascii))
							throw new ParseException(
									"Replace detection and replace target are same in Recursive Replace.",
									mLocation);
					}

					return ret;
				}
			}
		}

		private String determineNextTokenType() throws IOException,
				ParseException {
			// Remove leading whitespace.
			Character next;
			while (Character.isWhitespace((next = getNextCharacter()))
					&& (next != (char) -1))
				;

			replaceCharacter(next);

			String nextToken;

			try {
				nextToken = mScanner.labelToken(this);
			} catch (ParseException ex) {
				String rep = mScanner.getBuffer();
				for (int i = 0; i < rep.length(); i++) {
					replaceCharacter(rep.charAt(rep.length() - (i + 1)));
				}

				throw new ParseException("Token Unrecognized by Scanner: "
						+ ex.getMessage(), mLocation);
			}

			String rep = mScanner.getBuffer();
			for (int i = 0; i < rep.length(); i++) {
				replaceCharacter(rep.charAt(rep.length() - (i + 1)));
			}

			String[] identifiers = nextToken.split("\\+");
			boolean selected = false;
			if (identifiers.length > 1) {
				for (String s : identifiers) {
					if (!s.equals(ID)) {
						selected = true;
						nextToken = s;
						break;
					}
				}

				if (!selected)
					nextToken = identifiers[0];
			}

			return nextToken;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.io.InputStream#read()
		 */
		@Override
		public int read() throws IOException {
			try {
				return getNextCharacter();
			} catch (ParseException ex) {
				throw new IOException(
						"Error while attempting to determine next token.", ex);
			}
		}
	}
}
