package toritools.map;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

/**
 * HashMap IO utility.
 * 
 * @author toriscope
 * 
 */
public class ToriMapIO {
	public static <K, V> String writeMap(final File file,
			final HashMap<K, V> map) throws IOException {
		StringBuilder st = new StringBuilder();
		for (Entry<K, V> s : map.entrySet())
			st.append(s.getKey().toString() + " = " + s.getValue().toString()
					+ ";");
		if (file != null) {
			FileWriter f = new FileWriter(file);
			f.write(st.toString());
			f.close();
		}
		return st.toString();
	}

	public static HashMap<String, String> readMap(final File file)
			throws FileNotFoundException {
		Scanner scan = new Scanner(file);
		StringBuilder doc = new StringBuilder();
		while (scan.hasNextLine()) {
			doc.append(scan.nextLine()).append("\n");
		}
		return readMap(doc.toString());
	}

	public static HashMap<String, String> readMap(final String string)
			throws FileNotFoundException {

		HashMap<String, String> map = new HashMap<String, String>();
		for (String token : string.toString().split(";")) {
			if (!token.contains("="))
				continue;
			else {
				String[] entry = token.split("=");
				map.put(entry[0].trim(), entry[1].trim());
			}

		}
		return map;
	}

	public static void writeVariables(final VariableCase vars, final File file)
			throws IOException {
		writeMap(file, vars.getVariables());
	}

	public static VariableCase readVariables(final File file)
			throws FileNotFoundException {
		return new VariableCase(readMap(file));
	}
}
