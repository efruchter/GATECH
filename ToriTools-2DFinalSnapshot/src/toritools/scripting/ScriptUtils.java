package toritools.scripting;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import toritools.controls.KeyHolder;
import toritools.debug.Debug;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entrypoint.Binary;
import toritools.map.ToriMapIO;
import toritools.map.VariableCase;
import toritools.math.Vector2;

/**
 * Construction yard for various things that help users out in writing entity
 * scripts.
 * 
 * @author toriscope
 * 
 */
public class ScriptUtils {

    private static VariableCase profileVariables = new VariableCase();
    private final static String PROFILE = "profile.save";
    private static KeyHolder keyHolder = new KeyHolder();
    private static boolean debugMode = false;

    private static Level level;
    private static Level newLevel;

    public static Random random = new Random();

    public static KeyHolder getKeyHolder() {
        return keyHolder;
    }

    public static void setKeyHolder(final KeyHolder keyHolder) {
        ScriptUtils.keyHolder = keyHolder;
    }

    public static String getVar(final String key) {
        return profileVariables.getVar(key);
    }

    public static void setVar(final String key, final String value) {
        profileVariables.setVar(key, value);
    }

    public static void saveProfileVariables(final String prefix) throws IOException {
        ToriMapIO.writeMap(new File(prefix + "_" + PROFILE), profileVariables.getVariables());
    }

    public static void loadProfileVariables(final String prefix) throws FileNotFoundException {
        profileVariables = new VariableCase(ToriMapIO.readMap(new File(prefix + "_" + PROFILE)));
    }

    /**
     * Represents the 8 directions. Use the getDirection method to grab an enum
     * easily.
     * 
     * @author toriscope
     * 
     */
    public static enum Direction {
        UP, DOWN, LEFT, RIGHT, UP_RIGHT, UP_LEFT, DOWN_LEFT, DOWN_RIGHT, CENTER;

        /**
         * Find the proper enum for the direction.
         * 
         * @param dir
         *            direction in radians.
         * @return the proper enum.
         */
        public static Direction findEnum(float dir) {
            dir = (float) Math.toDegrees(dir) % 360;
            if (dir >= 337.5 || dir < 22.5)
                return Direction.RIGHT;
            if (dir >= 22.5 && dir < 67.5)
                return Direction.UP_RIGHT;
            if (dir >= 67.5 && dir < 112.5)
                return Direction.UP;
            if (dir >= 112.5 && dir < 157.5)
                return Direction.UP_LEFT;
            if (dir >= 157.5 && dir < 202.5)
                return Direction.LEFT;
            if (dir >= 202.5 && dir < 247.5)
                return Direction.DOWN_LEFT;
            if (dir >= 247.5 && dir < 292.5)
                return Direction.DOWN;
            return Direction.DOWN_RIGHT;
        }
    }

    public static boolean isColliding(final Entity a, final Entity b) {
        // left of
        if (a.getPos().x + a.getDim().x <= b.getPos().x) {
            return false;
        }
        // below
        else if (a.getPos().y + a.getDim().y <= b.getPos().y) {
            return false;
        }
        // right
        else if (b.getPos().x + b.getDim().x <= a.getPos().x) {
            return false;
        }
        // above
        else if (b.getPos().y + b.getDim().y <= a.getPos().y) {
            return false;
        }

        return true;
    }

    public static boolean isPointWithin(final Entity a, final Vector2 point) {
        return point.x > a.getPos().x && point.x < a.getPos().x + a.getDim().x && point.y > a.getPos().y
                && point.y < a.getPos().y + a.getDim().y;
    }

    public static boolean isCollidingRad(final Entity a, final Entity b) {
        return Vector2.dist(a.getPos().add(a.getDim().scale(.5f)), b.getPos().add(b.getDim().scale(.5f))) < a.getDim().x
                / 2 + b.getDim().x / 2;
    }

    public static boolean isColliding(final Entity a, final List<Entity> b) {
        for (Entity e : b) {
            if (e != a && isColliding(e, a))
                return true;
        }
        return false;
    }

    public static Vector2 moveOut(final Entity self, final boolean disregardOutOfView, final Entity entity) {
        Vector2 delta = new Vector2();
        if (self != entity) {
            if (!(disregardOutOfView && !entity.isInView()) && isColliding(entity, self)) {
                self.setPos(self.getPos().add(delta = findBestVectorOut(self, entity).scale(1.1f)));
            }

        }
        return delta;
    }

    public static Vector2 moveOut(final Entity self, final boolean disregardOutOfView, final List<Entity> entities) {
        Vector2 delta = new Vector2();
        for (Entity entity : entities)
            if (self != entity && !(disregardOutOfView && !entity.isInView()) && isColliding(entity, self)) {
                self.setPos(self.getPos().add(delta = findBestVectorOut(self, entity).scale(1.1f)));
            }
        return delta;
    }

    public static Vector2 findBestVectorOut(final Entity toMove, final Entity noMove) {
        Vector2 test;
        Vector2 best = new Vector2(0, noMove.getPos().y - (toMove.getPos().y + toMove.getDim().y));

        test = new Vector2(0, (toMove.getPos().y - (noMove.getPos().y + noMove.getDim().y)) * -1);

        if (test.mag() < best.mag())
            best = test;

        test = new Vector2(noMove.getPos().x - (toMove.getPos().x + toMove.getDim().x), 0);
        if (test.mag() < best.mag())
            best = test;

        test = new Vector2((toMove.getPos().x - (noMove.getPos().x + noMove.getDim().x)) * -1, 0);
        if (test.mag() < best.mag())
            best = test;

        return best;
    }

    public static boolean isDebugMode() {
        return debugMode;
    }

    public static void setDebugMode(boolean debugMode) {
        ScriptUtils.debugMode = debugMode;
    }

    public static Level getCurrentLevel() {
        return level;
    }

    /**
     * Queue a level switch. If there is no current level, then it is switched
     * to automatically.
     * 
     * @param newLevel
     *            the level to switch to.
     */
    public static void queueLevelSwitch(final Level newLevel) {
        ScriptUtils.newLevel = newLevel;
        Debug.print("New level queued.");
    }

    public static boolean isLevelQueued() {
        return newLevel != null;
    }

    /**
     * Move to the queued level.
     */
    public static void moveToQueuedLevel() {
        Debug.print("Moving to the queued level.");
        keyHolder.clearKeys();
        level = newLevel;
        newLevel = null;
    }

    /*
     * Image Caching.
     */

    private static HashMap<File, Image> imageCache = new HashMap<File, Image>();

    /**
     * Fetch an image based on the file index. If it is invalid, or is not yet
     * cached, it will be cached and stored in video memory.
     * 
     * @param imageIndex
     * @return
     */
    public static Image fetchImage(final File imageIndex) {
        Image i = imageCache.get(imageIndex);
        if (i == null
                || (i instanceof VolatileImage && !(((VolatileImage) i).validate(Binary.gc) == VolatileImage.IMAGE_OK))) {
            imageCache.put(imageIndex, loadImage(imageIndex));
        }

        return imageCache.get(imageIndex);
    }

    private static Image loadImage(final File imageIndex) {
        Image image = null;
        try {
            image = (new ImageIcon(ClassLoader.getSystemResource(imageIndex.getPath().replace("\\", "/")))).getImage();
        } catch (Exception e) {
            try {
                image = ImageIO.read(imageIndex);
            } catch (IOException e1) {
                e1.printStackTrace();
                System.err.println("Can't find resouce " + imageIndex.toString());
                System.exit(0);
            }
        }

        VolatileImage i = Binary.gc.createCompatibleVolatileImage(image.getWidth(null), image.getHeight(null),
                VolatileImage.TRANSLUCENT);
        i.validate(Binary.gc);

        Graphics2D g = (Graphics2D) i.getGraphics();
        g.setColor(new Color(0, 0, 0, 0));
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OUT));
        g.fillRect(0, 0, i.getWidth(), i.getHeight());

        i.getGraphics().drawImage(image, 0, 0, image.getWidth(null), image.getHeight(null), null);
        i.getGraphics().dispose();
        return i;
    }

    /**
     * Get the amount of cached images.
     */
    public static int cachedImageAmount() {
        return imageCache.size();
    }

    /**
     * Clear the image cache.
     */
    public static void clearImageCache() {
        for (Entry<File, Image> s : imageCache.entrySet()) {
            if (s.getValue() instanceof VolatileImage) {
                ((VolatileImage) s.getValue()).flush();
            }
        }
        imageCache.clear();
    }
}
