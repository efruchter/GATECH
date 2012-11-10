package scannergenerator;




public class DefinedClass {
	
	private String name;
	private char[] definition;
	private char[] regex;
	
	public DefinedClass(String Name, char[] def)
	{
		setName(Name);
		setDefinition(def);
		setRegex(new char[def.length * 2]);
	}

	public char[] getRegex() {
		return regex;
	}

	public void setRegex(char[] regex) {
		this.regex = regex;
	}

	public char[] getDefinition() {
		return definition;
	}

	public void setDefinition(char[] definition) {
		this.definition = definition;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

}
