package scannergenerator;


public class RDP {
	char EMPTY = 0;
	String regex = "";
	public char[] inputBuffer;
	public int index = 0;
	public DefinedClass[] classes;
	

	
	
	public String regEx()
	{
//		System.out.println("Entering regEx");
		RExp();	
//		System.out.println("Leaving regEx");
		return regex;
	}
	
	public void RExp()
	{
//		System.out.println("Entering RExp");
		RExp1();
		RExpPrime();
//		System.out.println("Leaving RExp");
	}
	
	public void RExp1()
	{
//		System.out.println("Entering RExp1");
		RExp2();
		RExp1Prime();
//		System.out.println("Leaving RExp1");
	}
	
	public void RExpPrime()
	{
//		System.out.println("Entering RExpPrime");
		if(peekToken() == '|') // UNION <rexp1> <rexp'>
		{
			match('|');
			RExp1();
			RExpPrime();
		}
		else // empty
		{
			// matchEmpty(); Handle empty case
		}		
//		System.out.println("Leaving RExpPrime");
	}
	
	public void RExp2()
	{
//		System.out.println("Entering RExp2");
		if(peekToken() == '(') // (<rexp>) <rexp2-tail>
		{
			match('(');
			RExp();
			match(')');
			matchOp();  // rexp2-tail is terminal as 1 of 3 symbols, simple enough not to have a unique function. matches +,*,empty
		}
		else if(peekToken() == '\\') // RE_CHAR <rexp2-tail> OR <rexp3>
		{
				getToken();
				switch(peekToken())
				{
					case '+': 
						getToken();
						regex += (char)1;
						//regex += 'p';
						break;
					case '*':
						getToken();
						regex += (char)2;
						//regex += 'S';
						break;
					case '[':
						getToken();
						regex += (char)3;
						//regex += "B";
						break;
					case ']':
						getToken();
						regex += (char)4;
						//regex += "b";
						break;
					case '(':
						getToken();
						regex += (char)5;
						//regex += "P";
						break;
					case ')':
						getToken();
						//regex += "p";
						regex += (char)6;
						break;	
					case '.':
						getToken();
						//regex += (char)7;
						regex += (char)7;
						break;
					default:
						matchOne();
						break;
				}
				//matchOp();			
			}
			else // choose between "RE_CHAR <rexp2-tail>" and "rexp3" 
			{
				getToken();
				if(peekToken() == '+' || peekToken() == '*' || peekToken() == EMPTY)
				{
					putOneBack();
					matchOne();
					matchOp(); // matches +,*,empty
				}
				else
				{
					putOneBack();
					RExp3();
				}
			}
			
		}
//		System.out.println("Leaving RExp2");
	
	public void RExp1Prime()
	{
//		System.out.println("Entering RExp1Prime");
		if(peekToken() == EMPTY)
		{
			// matchEmpty(); Handle empty case
		}
		else
		{
			RExp2();
			RExp1Prime();
		}
//		System.out.println("Leaving RExp1Prime");
	}
	
	public void RExp3()
	{
//		System.out.println("Entering RExp3");
		if(peekToken() == EMPTY)
		{
			// matchEmpty(); handle empty case
		}
		else
		{
			charClass();
		}
//		System.out.println("Leaving RExp3");
	}
	
	public void charClass()
	{
//		System.out.println("Entering charClass");
		if(peekToken() == '[') // [<char-class1>
		{
			match('[');
			charClass1();
		}
		else if(peekToken() == '$') // <defined-class>
		{
			matchDefined(); // match a defined class
		}
		else
		{
			matchOne(); // matches 1 ascii char of any value
		}
//		System.out.println("Leaving charClass");
	}
	
	public void charClass1()
	{
//		System.out.println("Entering charClass1");
		if(peekToken() == '^') // <exclude-set>
		{
			excludeSet();
		}
		else // <char-set-list>
		{
			charSetList();
		}
//		System.out.println("Leaving charClass1");
	}
	
	public void excludeSet()
	{
//		System.out.println("Entering excludeSet");
		match('^');
		charSet();
		match(']');
		match('I');
		match('N');
		excludeSetTail();
//		System.out.println("Leaving excludeSet");
	}
	
	public void charSetList()
	{
//		System.out.println("Entering charSetList");
		if(peekToken() == ']')
		{
			match(']');
		}
		else
		{
			charSet();
			//charSetList();
		}
//		System.out.println("Leaving charSetList");
	}
	
	public void charSet()
	{
//		System.out.println("Entering charSet");
		matchCLS_CHAR();
		charSetTail();
//		System.out.println("Leaving regEx");
	}
	
	public void charSetTail()
	{
//		System.out.println("Entering charSetTail");
		if(peekToken() == '-')
		{
			match('-');
			matchCLS_CHAR();
		}
		else
		{
			// matchEmpty();
		}
//		System.out.println("Leaving charSetTail");
	}
	
	public void excludeSetTail()
	{
//		System.out.println("Entering excludeSetTail");
		if(peekToken() == '[') // [<char-set>]
		{
			match('[');
			charSet();
			match(']');
		}
		else
		{
			matchDefined();
		}
//		System.out.println("Leaving excludeSetTail");
	}
	
	
	
	

	public char getToken()
	{
		if(inputBuffer.length > index)
		{
			char out = inputBuffer[index];
//			System.out.println("Getting Token: " + out);
			index+=1;
			return out;
		}
		else
		{
			return 0;
		}
	}
	

	public char peekToken()
	{
		try
		{
//			System.out.println("Peeking at: " + inputBuffer[index] + "(as int: " + (int)(inputBuffer[index]) + ")");
			return inputBuffer[index];
		}
		catch(Exception ex)
		{
			return 0;
		}
	}
	
	public void putOneBack()
	{
		index -= 1;
//		System.out.println("Putting back: " + inputBuffer[index]);
	}
	
	public boolean match(char in)
	{
		char temp = getToken();
		if(temp == in)
		{
			regex += temp;
//			System.out.println("Matched: " + temp + " :: Current regex: " + regex);
			return true;
		}
		else
		{
			// throw exception or cause error
			return false;
		}
	}
	
	public boolean matchOne()
	{
		match(peekToken());
		return true;
	}
	
	// Stub method, need to figure out!
	public boolean matchDefined()
	{
		String ident = "";
		
		boolean stillMatches = true;
		while(stillMatches)
		{
			stillMatches = false;
			ident += getToken();
			int index = 0;
			
			while(classes[index] != null)
			{
				// Check tokens against class identifiers
				if(classes[index].getName().startsWith(ident))
				{
					stillMatches = true;
				}

				if(classes[index].getName().compareTo(ident) == 0)
				{
					String reg = new String(classes[index].getRegex());
					regex += reg;
//					System.out.println("Matched " + classes[index].getName() + " Current regex: " + regex);
					return true;
				}
				index += 1;
			}
		}
		return false;
	}
	
	//Stub method, should be easy to define
	public boolean matchOp()
	{		
		char temp = getToken();
		if(temp == '*' || temp == '+')
		{
			regex += temp;
//			System.out.println("Matched: " + temp + " Current regex: " + regex);
			return true;
		}
		else
		{
			return false;
			// error case
		}


	}
	
	// Stub method, should be easy to define
	public boolean isEscapeCharRE(char in)
	{
		return true;
	}
	
	// Stub method, should be easy to define
	public boolean matchCLS_CHAR()
	{
//		System.out.println("Checking if valid CLS_CHAR");
		if(peekToken() == '\\')
		{
			char temp = getToken();
			char peeked = peekToken();
			if(peeked == '\\' || peeked == '^' || peeked == '-' || peeked == '[' || peeked == ']')
			{
				putOneBack();
				match('\\');
				match(peeked);
				return true;
			}
			else
			{
				// error case
				return false;
			}
		}
		else if(peekToken() >= 32 && peekToken() <= 126)
		{
			match(peekToken());
			return true;
		}
		else
		{
			// error case
			return false;
		}
	}
	
	

}
