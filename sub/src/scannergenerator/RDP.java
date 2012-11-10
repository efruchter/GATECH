package scannergenerator;

public class RDP {
	char EMPTY = 0;
	String regex = "";
	public char[] inputBuffer;
	public int index = 0;
	public DefinedClass[] classes;

	public String regEx() {
		RExp();
		return regex;
	}

	public void RExp() {
		RExp1();
		RExpPrime();
	}

	public void RExp1() {
		RExp2();
		RExp1Prime();
	}

	public void RExpPrime() {
		if (peekToken() == '|') {
			match('|');
			RExp1();
			RExpPrime();
		} else {
		}
	}

	public void RExp2() {
		if (peekToken() == '(') {
			match('(');
			RExp();
			match(')');
			matchOp();
		} else if (peekToken() == '\\') {
			getToken();
			switch (peekToken()) {
			case '+':
				getToken();
				regex += (char) 1;
				break;
			case '*':
				getToken();
				regex += (char) 2;
				break;
			case '[':
				getToken();
				regex += (char) 3;
				break;
			case ']':
				getToken();
				regex += (char) 4;
				break;
			case '(':
				getToken();
				regex += (char) 5;
				break;
			case ')':
				getToken();
				regex += (char) 6;
				break;
			case '.':
				getToken();
				regex += (char) 7;
				break;
			default:
				matchOne();
				break;
			}
		} else {
			getToken();
			if (peekToken() == '+' || peekToken() == '*'
					|| peekToken() == EMPTY) {
				putOneBack();
				matchOne();
				matchOp();
			} else {
				putOneBack();
				RExp3();
			}
		}
	}

	public void RExp1Prime() {
		if (peekToken() == EMPTY) {
		} else {
			RExp2();
			RExp1Prime();
		}
	}

	public void RExp3() {
		if (peekToken() == EMPTY) {
		} else {
			charClass();
		}
	}

	public void charClass() {
		if (peekToken() == '[') {
			match('[');
			charClass1();
		} else if (peekToken() == '$') {
			matchDefined();
		} else {
			matchOne();
		}
	}

	public void charClass1() {
		if (peekToken() == '^') {
			excludeSet();
		} else {
			charSetList();
		}
	}

	public void excludeSet() {
		match('^');
		charSet();
		match(']');
		match('I');
		match('N');
		excludeSetTail();
	}

	public void charSetList() {
		if (peekToken() == ']') {
			match(']');
		} else {
			charSet();
		}
	}

	public void charSet() {
		matchCLS_CHAR();
		charSetTail();
	}

	public void charSetTail() {
		if (peekToken() == '-') {
			match('-');
			matchCLS_CHAR();
		} else {
		}
	}

	public void excludeSetTail() {
		if (peekToken() == '[') {
			match('[');
			charSet();
			match(']');
		} else {
			matchDefined();
		}
	}

	public char getToken() {
		if (inputBuffer.length > index) {
			char out = inputBuffer[index];
			index += 1;
			return out;
		} else {
			return 0;
		}
	}

	public char peekToken() {
		try {
			return inputBuffer[index];
		} catch (Exception ex) {
			return 0;
		}
	}

	public void putOneBack() {
		index -= 1;
	}

	public boolean match(char in) {
		char temp = getToken();
		if (temp == in) {
			regex += temp;
			return true;
		} else {
			return false;
		}
	}

	public boolean matchOne() {
		match(peekToken());
		return true;
	}

	public boolean matchDefined() {
		String ident = "";
		boolean stillMatches = true;
		while (stillMatches) {
			stillMatches = false;
			ident += getToken();
			int index = 0;
			while (classes[index] != null) {
				if (classes[index].getName().startsWith(ident)) {
					stillMatches = true;
				}
				if (classes[index].getName().compareTo(ident) == 0) {
					String reg = new String(classes[index].getRegex());
					regex += reg;
					return true;
				}
				index += 1;
			}
		}
		return false;
	}

	public boolean matchOp() {
		char temp = getToken();
		if (temp == '*' || temp == '+') {
			regex += temp;
			return true;
		} else {
			return false;
		}
	}

	public boolean isEscapeCharRE(char in) {
		return true;
	}

	public boolean matchCLS_CHAR() {
		if (peekToken() == '\\') {
			char temp = getToken();
			char peeked = peekToken();
			if (peeked == '\\' || peeked == '^' || peeked == '-'
					|| peeked == '[' || peeked == ']') {
				putOneBack();
				match('\\');
				match(peeked);
				return true;
			} else {
				return false;
			}
		} else if (peekToken() >= 32 && peekToken() <= 126) {
			match(peekToken());
			return true;
		} else {
			return false;
		}
	}
}
