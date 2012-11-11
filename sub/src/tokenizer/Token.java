package tokenizer;

/**
 * A cheap 'n flexible token 
 * 
 * @author Kefu Zhou
 *
 */
public class Token {
	public String type;
	public String value;
	
	public Token(String type, String value) {
		this.type = type;
		this.value = value;
	}
	
	@Override
	public String toString() {
		return String.format("%s %s", this.type, this.value);
	}
	
	@Override
	public boolean equals(Object obj) {
		Token b = (Token) obj;
		return b.type.equals(this.type) && b.value.equals(this.value);
	}
}
