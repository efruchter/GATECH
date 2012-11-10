package spec;

import java.util.ArrayList;
import java.util.List;

public class CharClass {
	private final String re;
	private final String ore;

	public CharClass(String re) {
		this.ore = re;
		this.re = parseFinal(re);
	}

	public boolean match(final String input) {
		return true;
	}

	@Override
	public String toString() {
		return String.format("<CharClass %s>", this.re);
	}

	public static String parseFinal(String s) {
		String f = new String();
		char ch, nch;
		boolean flag = false;

		for (int i = 0; i < s.length();) {
			if (s.charAt(i) == '\\') {
				f = f.concat(s.substring(i, i + 2));
				i += 2;
			} else if ((i + 1) < s.length() && s.charAt(i) == '['
					&& s.charAt(i + 1) != '^') {
				f = f.concat("(" + s.substring(i, i + 4));
				i += 4;
				while (((i) < s.length()) && (s.charAt(i) != ']')) {
					flag = true;
					f = f.concat("]|[" + s.substring(i, i + 3));
					i += 3;
				}
				f = f.concat("])");
				i++;
			} else if ((i + 2) < s.length()
					&& s.substring(i, i + 2).equals("[^")) {

				if (s.charAt(i + 3) == '-') {
					char rlb = s.charAt(i + 2);
					char rub = s.charAt(i + 4);

					List<Character> accepts = new ArrayList<Character>();

					i += 9;

					while (s.charAt(i) != ']') { // [^A-C]IN[A-La-z]

						for (char _ch = s.charAt(i); _ch <= s.charAt(i + 2); _ch++)
							if (_ch < rlb || _ch > rub)
								accepts.add(((Character) _ch));

						i += 3;
					}

					f = f.concat("(");
					for (int k = 0; k < accepts.size() - 1; k++)
						f = f.concat(String.valueOf(accepts.get(k)) + "|");
					f = f.concat(accepts.get(accepts.size() - 1) + ")");

					i += 1;

				} else { // Simple case
					f = f.concat("(");
					nch = s.charAt(i + 2);
					for (ch = s.charAt(i + 7); ch < s.charAt(i + 9); ch++)
						if (ch != nch)
							f = f.concat(String.valueOf(ch) + "|");
					ch = s.charAt(i + 9);
					if (ch != nch)
						f = f.concat(String.valueOf(ch) + ")");
					i += 11;
				}

			} else if (s.charAt(i) == '+') {
				if (s.charAt(i - 1) == ']')
					f = f + s.substring(s.lastIndexOf("[", i), i) + "*";
				else if (s.charAt(i - 1) == ')')
					f = f + s.substring(s.lastIndexOf("(", i), i) + "*";
				else
					f = f + "(" + s.substring(i - 1, i) + ")" + "*";
				i++;
			} else {
				f = f.concat(((Character) s.charAt(i)).toString());
				i++;
			}
		}

		return f;
	}

	public String getRe() {
		return re;
	}

	public String getOre() {
		// TODO Auto-generated method stub
		return ore;
	}
}
