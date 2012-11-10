package spec;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

public class TokenDef {
	private final String tokenName;
	private String tokenStuff;

	public TokenDef(final String tokenName, final String tokenStuff,
			final Map<String, CharClass> charClasses) {
		this.tokenName = tokenName;
		this.tokenStuff = tokenStuff;
		collapse(charClasses);
	}

	private void collapse(Map<String, CharClass> charClasses) {
		// TODO Auto-generated method stub
		Iterator charClassesIterator = charClasses.entrySet().iterator();
		while (charClassesIterator.hasNext()) {
			Map.Entry<String, CharClass> entry = (Map.Entry) charClassesIterator
					.next();
			this.tokenStuff = this.tokenStuff.replace("$" + entry.getKey(), entry.getValue().getRe());
		}
		this.tokenStuff = CharClass.parseFinal(this.tokenStuff);
	}

	@Override
	public String toString() {
		return String.format("<TokenDef $%s %s>", this.tokenName,
				this.tokenStuff);
	}
}
