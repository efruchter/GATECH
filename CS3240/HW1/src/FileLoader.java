import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JFileChooser;

/**
 * This class is a file selector.
 * 
 * @author Eric Fruchter
 * 
 */
public class FileLoader {
	private FileLoader() {
	}

	/**
	 * Bring up a file chooser and allow the user to pick a file.
	 * 
	 * @return
	 */
	public static File loadFile() {
		// Create a file chooser
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Select a document for analysis:");
		// In response to a button click:
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			System.out.println("Diagnostics for " + fc.getSelectedFile().getAbsolutePath());
			return fc.getSelectedFile();
		} else {
			System.err.println("No file selected!");
			System.exit(0);
		}
		return null;
	}

	/**
	 * Convert a file to a string.
	 * 
	 * @param file
	 * @return string version of file.
	 */
	public static String fileToString(File file) {
		String result = null;
		DataInputStream in = null;

		try {
			byte[] buffer = new byte[(int) file.length()];
			in = new DataInputStream(new FileInputStream(file));
			in.readFully(buffer);
			result = new String(buffer);
		} catch (IOException e) {
			throw new RuntimeException("IO problem in fileToString", e);
		} finally {
			try {
				in.close();
			} catch (IOException e) { /* ignore it */
			}
		}
		return result;
	}
}
