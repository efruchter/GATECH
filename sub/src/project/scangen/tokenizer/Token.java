package project.scangen.tokenizer;

/**
 * A cheap 'n flexible token
 *
 * @author Kefu Zhou
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
    	if(this == obj) 
    		return true;
    	
    	if(obj instanceof Token) {
    		Token t = (Token) obj;
    		return this.type.equals(t.type) && this.value.equals(t.value);
    	} else {
    		return false;
    	}
    }
}
