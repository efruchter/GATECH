package project.scangen.tokenizer;

/**
 * A cheap 'n flexible token
 *
 * @author Kefu Zhou
 */
public class Token {
    public String type;
    public String value;
    public int line;
    public int pos;

    public Token(String type, String value, int line, int pos) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.pos = pos;
    }
    
    @Override
    public String toString() {
        return String.format("line: %d, pos: %d, type: %s, value: %s", this.line, this.pos, this.type, this.value);
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
