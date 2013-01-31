
package audioProject;

import static java.lang.Math.abs;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.util.Random;

import javax.swing.JOptionPane;

import maryb.player.Player;
import toritools.additionaltypes.ColorCycler;
import toritools.additionaltypes.ColorUtils;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entrypoint.Binary;
import toritools.math.Vector2;
import toritools.scripting.ScriptUtils;
import audioProject.controller.WaveController;
import audioProject.entities.BadShipFactory;
import audioProject.entities.PlayerShip;
import audioProject.entities.ScrollingBackground;

/**
 * Template for our audio project.
 * 
 * @author toriscope
 * 
 */
public class AudioProject extends Binary {

	public static Player soundPlayer = new Player();
	public static WaveController controller;

	public static Random random;

	public static boolean bossMode = false, bossIsAlive = false;

	/**
	 * To make it easier to change things.
	 */
	public static Color barsColor = new Color(0, 250, 0),
			barsLighterColor = new Color(0, 250, 0),
			barsDarkerColor = new Color(245, 153, 255),
			shipColor = Color.black, enemyColor = Color.RED, bgColor = null;

	public static float getFloat() {
		return random.nextFloat();
	}

	public static int bars = 100;
	public static boolean win;

	public static void main(String[] args) {
		new AudioProject();
	}

	public AudioProject() {
		super(new Vector2(800, 600), 60, "Audio Project Tech Demo");
	}

	@Override
	protected boolean render(Graphics2D rootCanvas, Level level) {
		try {
			rootCanvas.setStroke(new BasicStroke(4));
			rootCanvas.setColor(bgColor);
			rootCanvas.fillRect(-1, -1, (int) VIEWPORT.x + 2,
					(int) VIEWPORT.y + 2);
			for (int i = level.getLayers().size() - 1; i >= 0; i--) {
				for (Entity e : level.getLayers().get(i)) {
					if (e.isVisible())
						e.draw(rootCanvas);
				}
			}
			rootCanvas.setColor(Color.BLACK);

			rootCanvas.drawString("Feel: " + controller.getFeel(), 20, 20);			
			rootCanvas.drawString("BM: " + bossMode, 20, 40);			
			rootCanvas.drawString("%: " + controller.getPercentage(), 80, 40);

			if (win) {
				rootCanvas.setColor(Color.RED);
				rootCanvas
						.setFont(new Font("LucidaSansOblique", Font.BOLD, 70));
				rootCanvas.drawString("Victory!", (int) level.getDim().x / 2,
						(int) level.getDim().y / 2);
			}

		} catch (final Exception uhoh) {
			return false;
		}
		return true;
	}

	@Override
	protected void initialize() {
		win = false;
		String songName = JOptionPane
				.showInputDialog("Name of song? (goo / unicorn) \n<WASD> Move \n<SPACE> Shoot \n <.,> Angle Shots");
		if (songName == null) {
			JOptionPane.showMessageDialog(null, "YOU ARE MAXIMUM LAME");
			System.exit(1);
		}

		try {
			controller = new WaveController(songName, bars);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}

		soundPlayer.setSourceLocation("audioProject/" + songName + ".mp3");

		soundPlayer.play();
		soundPlayer.seek(1);

		moments = entities = 0;
		random = new Random(0);
	}

	long moments, entities;

	ColorCycler enemyColorCycler = new ColorCycler(230, 255, 0, 0, 0, 0);
	ColorCycler bgColorCycler = new ColorCycler(220, 255, 220, 255, 220, 255);

	@Override
	protected void globalLogic(Level level) {

		long time = (long) (soundPlayer.getCurrentPosition() * 0.001);

		controller.setTime44100(time);

		ScrollingBackground bg = (ScrollingBackground) level
				.getEntityWithId("bg");
		PlayerShip player = (PlayerShip) level.getEntityWithId("player");

		bg.setFocus(player.getPos(), .5f);
		bg.setSpeed(2 * controller.getFeel());

		if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_ESCAPE)) {
			System.exit(0);
		}

		if (time > controller.getBossTime()) {
			if (!bossMode) {
				bossMode = true;
				level.spawnEntity(BadShipFactory.makeBoss());
				System.out.println("Spawning Boss!");
			}
		}

		if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_T)) {
			String percentage = "";
			try {
				percentage = JOptionPane
						.showInputDialog("Do Something? \nTo warp: (ex .4 = 40%)");
				seek((int) (soundPlayer.getTotalPlayTimeMcsec() * Float
						.parseFloat(percentage)),
						level);
			} catch (Exception e) {
				if ("win".equals(percentage))
					win = true;
			} finally {
				ScriptUtils.getKeyHolder().clearKeys();
			}
		}

		// bgColor = ColorUtils.blend(Color.BLACK, new Color(0, 64, 13),
		// controller.getFeel());
		// barsColor = ColorUtils.blend(Color.GREEN, new Color(0, 64, 13), 1 -
		// controller.getFeel());

		bgColor = ColorUtils
				.blend(Color.BLUE, Color.CYAN, controller.getFeel());
		Color c = ColorUtils.blend(barsDarkerColor, barsLighterColor,
				controller.getFeel());
		barsColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), 120);

		if (time > controller.getVictoryTime()) {
			win = true;
			for (Entity enemy : level.getEntitiesWithType("enemy")) {
				level.despawnEntity(enemy);
			}
		} else if (getFloat() < .015 * abs(controller.getFeel())
				* (bossMode ? .5f : 1)) {
			level.spawnEntity(BadShipFactory.makeDefaultEnemy(VIEWPORT));
		}
	}

	@Override
	protected void setupCurrentLevel(Level levelBeingLoaded) {
		levelBeingLoaded.spawnEntity(new PlayerShip());
		levelBeingLoaded.spawnEntity(new ScrollingBackground(VIEWPORT, 1, bars,
				2.3f, .614f, 100, 100, .05f * controller.getAverageFeel()));
		addLevelBounds(levelBeingLoaded);
	}

	private void addLevelBounds(final Level levelBeingLoaded) {
		Entity l, r, u, d;

		l = new Entity();
		l.setSolid(true);
		l.setPos(new Vector2(-20, 0));
		l.setDim(new Vector2(20, VIEWPORT.y));

		r = new Entity();
		r.setSolid(true);
		r.setPos(new Vector2(VIEWPORT.x, 0));
		r.setDim(new Vector2(VIEWPORT.x, VIEWPORT.y));

		u = new Entity();
		u.setSolid(true);
		u.setPos(new Vector2(0, -20));
		u.setDim(new Vector2(VIEWPORT.x, 20));

		d = new Entity();
		d.setSolid(true);
		d.setPos(new Vector2(0, VIEWPORT.y));
		d.setDim(new Vector2(VIEWPORT.x, 20));

		levelBeingLoaded.spawnEntity(l);
		levelBeingLoaded.spawnEntity(r);
		levelBeingLoaded.spawnEntity(u);
		levelBeingLoaded.spawnEntity(d);
	}

	@Override
	protected Level getStartingLevel() {
		Level level = new Level();
		level.setDim(VIEWPORT);
		return level;
	}

	public static void seek(final int time, final Level level) {
		level.getEntityWithId("player").onSpawn(level);

		for (Entity enemy : level.getEntitiesWithType("enemy")) {
			level.despawnEntity(enemy);
		}

		for (Entity bullets : level.getEntitiesWithType("BadBullet")) {
			level.despawnEntity(bullets);
		}
		bossMode = false;
		AudioProject.soundPlayer.seek(time);
	}

}
