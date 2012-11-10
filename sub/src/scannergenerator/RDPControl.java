package scannergenerator;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class RDPControl {
	DefinedClass[] classes;
	RDP parser;
	static int tokenindex;

	public String generateRegex(String filename) {
		int index = 0, index2 = 0;

		RDPControl controller = new RDPControl();
		controller.classes = controller.ReadClassesFromFile(filename);
		controller.parser = new RDP();

		while (controller.classes[index] != null) {
			System.out.println("\n\n");
			controller.parser.regex = "";
			controller.parser.index = 0;
			controller.parser.classes = controller.classes;
			controller.parser.inputBuffer = controller.classes[index]
					.getDefinition();
			String out = controller.parser.regEx();
			controller.classes[index].setRegex(out.toCharArray());
			index += 1;
		}
		while (controller.classes[index2] != null) {
			controller.classes[index2].setRegex(parseFinal(
					new String(controller.classes[index2].getRegex()))
					.toCharArray());
			index2++;
		}
		DefinedClass[] copyofrange = Arrays.copyOfRange(controller.classes,
				tokenindex, controller.classes.length - 1);
		String merged = mergeDefinedClasses(copyofrange);
		return merged;
	}

	public static void main(String[] args) {
		String filename = "doc/sample_input_specification.txt";
		int index = 0;
		RDPControl controller = new RDPControl();
		controller.classes = controller.ReadClassesFromFile(filename);
		controller.parser = new RDP();
		while (controller.classes[index] != null) {
			controller.parser.regex = "";
			controller.parser.index = 0;
			controller.parser.classes = controller.classes;
			controller.parser.inputBuffer = controller.classes[index]
					.getDefinition();
			String out = controller.parser.regEx();
			System.out.println(out);
			System.out.println(controller.parseFinal(out));
			controller.classes[index].setRegex(out.toCharArray());
			index += 1;
		}
	}

	public static DefinedClass[] getOutput(String filename) {
		if (filename.equals("")) {
			filename = "doc/sample_input_specification.txt";
		}

		int index = 0;
		RDPControl controller = new RDPControl();
		controller.classes = controller.ReadClassesFromFile(filename);
		controller.parser = new RDP();
		while (controller.classes[index] != null) {
			controller.parser.regex = "";
			controller.parser.index = 0;
			controller.parser.classes = controller.classes;
			controller.parser.inputBuffer = controller.classes[index]
					.getDefinition();
			String out = controller.parser.regEx();
			System.out.println(out);
			System.out.println(controller.parseFinal(out));
			controller.classes[index].setRegex(out.toCharArray());
			index += 1;
		}
		return controller.classes;
	}

	public DefinedClass[] ReadClassesFromFile(String filename) {
		DefinedClass[] output = new DefinedClass[100];
		int index = 0;
		try {
			FileInputStream fs = new FileInputStream(filename);
			DataInputStream ds = new DataInputStream(fs);
			BufferedReader br = new BufferedReader(new InputStreamReader(ds));
			String dataIn;
			while ((dataIn = br.readLine()) != null) {
				if (dataIn.startsWith("%%")) {
					tokenindex = index;
					continue;
				} else if (dataIn.startsWith("$")) {
					String[] bits = dataIn.split("\\s+");
					System.out.print(bits[0] + "   \t\t");

					String d = "";
					for (int i = 1; i < bits.length; i++) {
						d = d + bits[i];
					}
					char[] def = new char[d.length()];
					d.getChars(0, d.length(), def, 0);
					System.out.println(d);
					output[index] = new DefinedClass(bits[0], def);
					index += 1;
				} else {
					System.out.println("Unexpected line in file: " + dataIn);
				}
			}
			return output;
		} catch (Exception ex) {
			System.out.println("Could not read from file: " + ex.getMessage());
			return null;
		}

	}

	public String parseFinal(String s) {
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
				// handle the not case.
				// Example [^0]IN[0-9]

				if (s.charAt(i + 3) == '-') { // complex case
												// [^a-b]in[a-zA-Z0-9]

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
				f = f + "(" + s.substring(s.lastIndexOf("(", i), i) + ")*";
				i++;
			} else {
				f = f.concat(((Character) s.charAt(i)).toString());
				i++;
			}
		}

		return f;
	}

	private String mergeDefinedClasses(DefinedClass[] classes) {
		String s;
		s = "";
		for (DefinedClass c : classes) {
			if (c == null)
				return s.substring(0, s.length() - 1);
			System.out.println(c.getRegex());
			s += "(" + (new String(c.getRegex())) + ")|";
		}
		return s.substring(0, s.length() - 1);
	}

}