package toritools.io;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * A loader and register mechanism for TrueType fonts.
 * 
 * @author toriscope
 * 
 */
public class FontLoader {

	private FontLoader() {
	}

	/**
	 * Load and register all fonts in a directory.
	 * 
	 * @param directory
	 */
	public static void loadFonts(final File directory) {
		for (File file : directory.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".ttf");
			}
		})) {
			try {
				loadFont(file);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Load and register a font with the GraphicsEnvironment.
	 * 
	 * @param font
	 *            font file.
	 * @throws FileNotFoundException
	 * @throws FontFormatException
	 * @throws IOException
	 */
	public static void loadFont(final File font) throws FileNotFoundException,
			FontFormatException, IOException {
		GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(
				Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(font)));
	}

}
