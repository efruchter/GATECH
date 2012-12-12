import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * This class will generate the extra credit manipulations and print out the
 * file sizes for analysis.
 * 
 * @author Eric Fruchter
 * 
 */
public class ExtraCredit {
	public static void main(String[] args) throws IOException {

		File file = FileLoader.loadFile();
		File result = new File("topKResults.txt");

		final String OTEXT = FileLoader.fileToString(file);
		System.out.println("Size of file from k=0 -> k=1000");

		FileWriter res = new FileWriter(result);

		TokenScanner scan = new TokenScanner(file);
		scan.tokenizeAll();

		List<Token> ks = scan.getMostFrequentVARS();

		String replaced = OTEXT;

		for (int k = 0; k < 1000; k++) {

			if (ks.size() <= k) {
				break;
			}

			replaced = replaced.replaceAll(ks.get(k).TOKEN, Integer.toString(k));

			System.out.println("k:" + k);
			res.append(replaced.length() + "\n");
		}
		res.close();
		System.out.println("Results printed to " + result.getPath());
	}
}
