import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Print several document statistics for the user.
 * 
 * @author Eric Fruchter
 * 
 */
public class DocumentStatistics {
	public static void main(String[] args) throws IOException {

		TokenScanner scanner = new TokenScanner(FileLoader.loadFile());
		BufferedWriter wr = new BufferedWriter(new FileWriter(new File("output.txt")));

		try {
			while (true) {
				wr.append(scanner.getNextValidToken().toString()).append("\n");
			}
		} catch (NoSuchElementException ex) {
			// No more tokens.
		} finally {
			wr.close();
			System.out.println("\n Token output has been written to \"output.txt\".");
		}

		String choice = "";
		Scanner in = new Scanner(System.in);
		do {
			System.out.println("\n--- --- ---\nPlease choose your operation:" + "\n[m] Most Frequent VARs"
					+ "\n[i] Min/Max INT/VARS" + "\n[t] Print totals"
					+ "\n[v] # of times VAR is followed by (INT|FLOAT)" + "\n[q] Quotes found" + "\n[e] Exit");
			System.out.print("Selection: ");
			choice = in.next();
			if ("m".equals(choice)) {
				System.out.println("\nMost Frequent VARS:");
				for (Token v : scanner.getMostFrequentVARS(20)) {
					System.out.println(v + ": " + v.frequency);
				}
			} else if ("i".equals(choice)) {
				System.out.print("\nMin INT: " + scanner.getMinINT());
				System.out.print("\nMax INT: " + scanner.getMaxINT());
				System.out.print("\nMin FLOAT: " + scanner.getMinFLOAT());
				System.out.print("\nMax FLOAT: " + scanner.getMaxFLOAT());
			} else if ("t".equals(choice)) {
				System.out.print("\n# of VARS: " + scanner.getTotalVARS());
				System.out.print("\n# of INTS: " + scanner.getTotalINTS());
				System.out.print("\n# of FLOATS: " + scanner.getTotalFLOATS() + "\n");
			} else if ("v".equals(choice)) {
				System.out
						.println("\n# of times VAR is followed by INT or FLOAT: " + scanner.getVARFollowOccurrences());
			} else if ("q".equals(choice)) {
				System.out.println("\nQuotes found:");
				for (String quote : scanner.getQuotes()) {
					System.out.println("\"" + quote + "\"");
				}
			}
		} while (!"e".equals(choice));
	}
}
