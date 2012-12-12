package toritools.leveleditor;

import java.awt.Point;
import java.io.File;

/**
 * This is one tile with associated image file and the index/cycle of the sprite that is the background.
 * 
 * @author toriscope
 * 
 */
public class LevelTile {
	public final File imageFile;
	public final Point subImage;

	public LevelTile(final File imageFile, final Point subImage) {
		this.imageFile = imageFile;
		this.subImage = subImage;
	}
}
