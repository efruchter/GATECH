package toritools.dialog;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Tools for dialog generating/parsing
 * 
 * @author toriscope
 * 
 */
public class DialogSplitterUtility {
	private DialogSplitterUtility() {

	}

	/**
	 * Form a list of sentences that are all below maxL in length without
	 * cutting words.
	 * 
	 * @param words
	 *            trimmed list of strings to segment
	 * @param maxL
	 *            max Length of a line in chars
	 * @return Lines of text
	 */
	public static List<StringBuffer> lineParser(final List<String> words,
			final int maxL) {
		List<StringBuffer> sList = new LinkedList<StringBuffer>();
		sList.add(new StringBuffer());
		int sindex = 0;
		for (int i = 0; i < words.size(); i++) {
			if (sList.get(sindex).length() + (words.get(i) + " ").length() <= maxL) {
				sList.set(sindex, sList.get(sindex).append(words.get(i))
						.append(" "));
			} else {
				sindex++;
				sList.add(new StringBuffer());
				sList.set(sindex, sList.get(sindex).append(words.get(i))
						.append(" "));
			}
		}
		return sList;
	}

	/**
	 * Form a list of sentences that are all below maxL in length without
	 * cutting words.
	 * 
	 * @param words
	 *            string to segment
	 * @param maxL
	 *            max Length of a line in chars
	 * @return Lines of text
	 */
	public static List<StringBuffer> lineParser(final String words,
			final int maxL) {
		Scanner scan = new Scanner(words);
		List<String> wordList = new LinkedList<String>();
		while (scan.hasNext()) {
			wordList.add(scan.next());
		}
		return lineParser(wordList, maxL);
	}
}
