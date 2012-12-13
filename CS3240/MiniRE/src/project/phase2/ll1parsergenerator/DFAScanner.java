package project.phase2.ll1parsergenerator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import project.phase2.ll1parsergenerator.dfastuff.DFA;
import project.phase2.ll1parsergenerator.dfastuff.DFA.TokenMatch;
import project.phase2.ll1parsergenerator.dfastuff.NFA;
import project.phase2.ll1parsergenerator.dfastuff.Parser;

/**
 * Used for scanning a stream using DFAs. Implements some methods for common
 * uses.
 * 
 */
public class DFAScanner {
	//
	// CLASS/INSTANCE DATA
	//
	/**
	 * The DFA to scan using.
	 */
	private DFA mScan;

	/**
	 * Token buffer (in we need to maintain data from passed in input stream).
	 */
	private String mBuffer;

	/**
	 * Whether the tables should be minimized.
	 */
	private boolean mMinimize;

	//
	// CTOR
	//
	public DFAScanner(boolean minimize) {
		mMinimize = minimize;
	}

	public DFAScanner() {
		this(true);
	}

	//
	// PUBLIC METHODS
	//
	/**
	 * Returns the input buffer.
	 * 
	 * @return the input buffer.
	 */
	public String getBuffer() {
		return mBuffer;
	}

	/**
	 * Adds a regular expression to the scanner.
	 * 
	 * @param regex
	 *            the regular expression.
	 */
	public void addRegex(String regex) {
		addRegex(regex, null);
	}

	/**
	 * Adds a regular expression with a label to the scanner.
	 * 
	 * @param regex
	 *            the regular expression.
	 * @param label
	 *            the label.
	 */
	public void addRegex(String regex, String label) {
		NFA nfa = Parser.fromString(regex);
		nfa.setGoalLabels(label);

		if (mScan == null) {
			mScan = DFA.fromNFA(nfa);
		} else {
			mScan = DFA.fromNFA(nfa.union(mScan));
		}

		if (mMinimize)
			mScan = mScan.minimize();
	}

	/**
	 * Returns the label of the token starting at the beginning of the stream.
	 * Returns null if there is not a valid match to the scanner.
	 * 
	 * @param stream
	 *            the stream to search.
	 * @return the label.
	 */
	public String labelToken(InputStream stream) throws IOException,
			ParseException {
		mScan.reset();
		mBuffer = "";
		int nextInt;
		char next;

		String longestLabel = null;

		while ((nextInt = stream.read()) != -1) {
			next = (char) nextInt;
			mBuffer += next;

			TokenMatch tm = mScan.test(Character.toString(next));
			if (tm.isAccepted()) {
				longestLabel = tm.getLabel();
			} else if (tm.isRejected()) {
				break;
			}
		}

		if (longestLabel == null)
			throw new ParseException(mBuffer, mBuffer.length());

		return longestLabel;
	}

	/**
	 * Finds all occurrences of the specified regex in the given file. Uses
	 * longest matching and does not include overlapping occurrences.
	 * 
	 * @param f
	 *            the file to search.
	 * @throws IOException
	 */
	public List<MatchDescriptor> findAllInFile(File f) throws IOException// throws
																			// IOException
	{
		RandomAccessFile raf = null;
		ArrayList<MatchDescriptor> ret = new ArrayList<MatchDescriptor>();
		MatchDescriptor currMatch = null;
		String currentString;
		int currPos = 0;
		int nextInt;
		char next;

		try {
			raf = new RandomAccessFile(f, "r");

			while (currPos < raf.length()) {
				raf.seek(currPos);
				mScan.reset();
				currentString = "";
				currMatch = null;

				while ((nextInt = raf.read()) != -1) {
					next = (char) nextInt;

					TokenMatch tm = mScan.test(Character.toString(next));

					if (tm.isRejected()) {
						break;
					} else {
						currentString += next;

						if (tm.isAccepted()) {
							currMatch = new MatchDescriptor(currentString,
									(int) raf.getFilePointer()
											- currentString.length());
						}
					}
				}

				if (currMatch != null) {
					ret.add(currMatch);
					currPos += currMatch.getString().length();
				} else {
					currPos++;
				}
			}
		} finally {
			if (raf != null)
				raf.close();
		}

		return ret;
	}

	/**
	 * Finds all occurrences of the specified regex in the given string. Uses
	 * longest matching and does not include overlapping occurrences.
	 * 
	 * @param f
	 *            the file to search.
	 * @throws IOException
	 */
	public List<MatchDescriptor> findAllInString(String f) {
		ArrayList<MatchDescriptor> ret = new ArrayList<MatchDescriptor>();
		MatchDescriptor currMatch = null;
		String currentString;
		int currPos = 0, currInnerPos = 0;
		char next;

		while (currPos < f.length()) {
			currInnerPos = currPos;
			mScan.reset();
			currentString = "";
			currMatch = null;

			while (currInnerPos < f.length()) {
				next = f.charAt(currInnerPos);
				currInnerPos++;

				TokenMatch tm = mScan.test(Character.toString(next));

				if (tm.isRejected()) {
					break;
				} else {
					currentString += next;

					if (tm.isAccepted()) {
						currMatch = new MatchDescriptor(currentString,
								currInnerPos - currentString.length());
					}
				}
			}

			if (currMatch != null) {
				ret.add(currMatch);
				currPos += currMatch.getString().length();
			} else {
				currPos++;
			}
		}

		return ret;
	}

	//
	// INNER CLASS
	//
	public class MatchDescriptor {
		//
		// CLASS/INSTANCE DATA
		//
		/**
		 * The string that was matched.
		 */
		private String mString;

		/**
		 * The location that the string was matched at.
		 */
		private int mLocation;

		//
		// CTOR
		//
		public MatchDescriptor(String str, int loc) {
			mString = str;
			mLocation = loc;
		}

		//
		// PUBLIC METHODS
		//
		/**
		 * Returns the string.
		 * 
		 * @return the string.
		 */
		public String getString() {
			return mString;
		}

		/**
		 * Returns the location.
		 * 
		 * @return the location.
		 */
		public int getLocation() {
			return mLocation;
		}

		/**
		 * Returns a string representation of the match.
		 * 
		 * @return the string representation.
		 */
		public String toString() {
			return "\"" + mString + "\" at " + mLocation;
		}
	}
}
