package project.phase2.ll1parsergenerator.dfastuff;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import project.phase2.ll1parsergenerator.dfastuff.IFiniteAutomaton.AcceptLabel;

/**
 * Main running class.
 */
public class DFABuilderMain {
	private static final String USAGE = "USAGE: \"java DFABuilderMain lexicalSpecFile [testingFile [outputFile]]\"";

	/**
	 * This method is the main driving method for the application to demonstrate
	 * its use on a lexical specification and testing file.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println(USAGE);
			System.exit(0);
		}

		DFABuilderLogger logger = new DFABuilderLogger();

		String lexFile = args[0], testFile = null, outFile = null;

		if (args.length > 1)
			testFile = args[1];

		if (args.length > 2)
			outFile = args[2];

		try {
			if (outFile != null) {
				File out = new File(outFile);
				if (!out.exists())
					out.createNewFile();
				logger = new DFABuilderLogger(new PrintWriter(out));
			}

			logger.log("-NFA CONSTRUCTION BEGINNING-");
			Scanner lexScanner = new Scanner(new File(lexFile));
			NFA lex = Parser.parseNFA(lexScanner);
			logger.log("-NFA CONSTRUCTION COMPLETE-");
			logger.log("");

			logger.output("NFA Successfully Generated From Specifications");
			logger.output("Final NFA Size: " + lex.getStates().size());
			logger.log("Final NFA: " + lex);
			logger.line();

			logger.log("-DFA CONSTRUCTION BEGINNING-");
			DFA dfa = DFA.fromNFA(lex);
			logger.log("-DFA CONSTRUCTION COMPLETE-");
			logger.log("");

			logger.output("DFA Successfully Generated From NFA");
			logger.output("Final DFA Size: " + dfa.getStates().size());
			logger.log("Final DFA: " + dfa);
			logger.line();

			logger.log("-DFA MINIMIZATION BEGINNING-");
			DFA min = dfa.minimize();
			logger.log("-DFA MINIMIZATION COMPLETE-");
			logger.log("");

			logger.output("DFA Successfully Minimized");
			logger.output("Final MinDFA Size: " + min.getStates().size());
			logger.log("Final MinDFA: " + min);
			logger.line();

			if (testFile != null) {
				Map<String, Integer> countMap = new HashMap<String, Integer>();
				Set<String> failedTok = new HashSet<String>();
				int acceptedTokens = 0;
				int failedTokens = 0;

				logger.log("-BEGINNING TO TEST INPUT FILE-");
				FileReader testReader = new FileReader(new File(testFile));
				int currRead;
				char charRead;
				AcceptLabel al;

				String currToken = "";

				while ((currRead = testReader.read()) != -1) {
					charRead = (char) currRead;

					if (Character.isWhitespace(charRead)) {
						if (currToken.length() > 0) {
							al = min.testInput(Arrays.copyOfRange(
									currToken.split(""), 1,
									currToken.length() + 1));
							if (al.isAccepted()) {
								logger.log(currToken + " accepted as "
										+ al.getLabel() + ".");
								acceptedTokens++;

								if (countMap.get(al.getLabel()) != null) {
									countMap.put(al.getLabel(),
											countMap.get(al.getLabel()) + 1);
								} else {
									countMap.put(al.getLabel(), 1);
								}
							} else {
								logger.log(currToken + " rejected.");
								failedTokens++;
								failedTok.add(currToken);
							}
						}

						currToken = "";
					} else {
						currToken += charRead;
					}
				}

				if (currToken.length() > 0) {
					al = min.testInput(Arrays.copyOfRange(currToken.split(""),
							1, currToken.length() + 1));
					if (al.isAccepted()) {
						logger.log(currToken + " accepted as " + al.getLabel()
								+ ".");
						acceptedTokens++;

						if (countMap.get(al.getLabel()) != null) {
							countMap.put(al.getLabel(),
									countMap.get(al.getLabel()) + 1);
						} else {
							countMap.put(al.getLabel(), 1);
						}
					} else {
						logger.log(currToken + " rejected.");
						failedTokens++;
						failedTok.add(currToken);
					}
				}

				logger.log("-COMPLETED TESTING INPUT FILE-");
				logger.log("");

				logger.output("Input File Completed Testing");
				logger.output("Testing Verdict: "
						+ ((failedTokens > 0) ? ("Reject") : ("Accept")));
				logger.line();

				logger.output("There were " + acceptedTokens
						+ " accepted tokens and " + failedTokens
						+ " rejected tokens in the file, a total of "
						+ (acceptedTokens + failedTokens) + " tokens.");
				logger.output("Accepted Token Breakdown:");
				for (Map.Entry<String, Integer> elt : countMap.entrySet()) {
					logger.output(elt.getKey() + ": " + elt.getValue());
				}
				logger.line();

				logger.output("Rejected Tokens:");
				for (String str : failedTok) {
					logger.output(str);
				}
				logger.line();

				logger.output("Completed All Requested Operations.");
				logger.output("If an output file was provided, see it for more detailed output.");
			}
		} catch (IOException io) {
			System.out.println(USAGE);
			System.out.println(io.getMessage());
			io.printStackTrace();
		} finally {
			logger.close();
		}
	}

	/**
	 * An inner class to be used for output to the STDOut and Output file. This
	 * is implemented as a singleton so any running class can obtain it.
	 */
	public static class DFABuilderLogger {
		/**
		 * The output writer to write to if it is initialized.
		 */
		private PrintWriter mOutputWriter = null;

		/**
		 * Singleton instance so that other running apps can obtain this.
		 */
		private static DFABuilderLogger mInstance = null;

		//
		// CTOR
		//
		private DFABuilderLogger(PrintWriter outputWriter) {
			mOutputWriter = outputWriter;

			mInstance = this;
		}

		private DFABuilderLogger() {
			this(null);
		}

		public static DFABuilderLogger getInstance() {
			if (mInstance == null)
				new DFABuilderLogger();

			return mInstance;
		}

		public void line() {
			this.output("");
		}

		public void log(String str) {
			if (mOutputWriter != null)
				mOutputWriter.println(str);
		}

		public void output(String str) {
			System.out.println(str);
			this.log(str);
		}

		public void close() {
			if (mOutputWriter != null)
				mOutputWriter.close();
		}
	}
}
