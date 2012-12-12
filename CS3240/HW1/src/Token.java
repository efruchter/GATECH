/**
 * A token with a literal string, and a type.
 * 
 * @author Eric Fruchter
 * 
 */
public class Token {

	/**
	 * The actual string token.
	 */
	final public String TOKEN;

	/**
	 * The TokenType.
	 */
	final public TokenType TYPE;

	/**
	 * Reserved for encoding frequency data.
	 */
	public int frequency = 0;

	/**
	 * Init token with literal and type.
	 * 
	 * @param token
	 * @param type
	 */
	public Token(final String token, final TokenType type) {
		this.TOKEN = token;
		this.TYPE = type;
	}

	/**
	 * Get a pretty formatted token descriptor.
	 */
	@Override
	public String toString() {
		return TYPE + "(" + TOKEN + ")";
	}

	@Override
	public int hashCode() {
		return TOKEN.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Token && ((Token) o).TOKEN.equals(TOKEN);
	}
}