/**
 * This will be the main class for a simple game that uses toritools.
 * 
 * @author toriscope
 */
package samplegame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;

import samplegame.customscripts.PlayerScript;
import samplegame.customscripts.WolfScript;
import samplegame.customscripts.WorldPortalScript;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.ReservedTypes;
import toritools.entrypoint.Binary;
import toritools.io.Importer;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;

public class SampleGame extends Binary {

    public static String savePrefix = "secondchance";
    public Vector2 zoom = new Vector2(1, 1);
    public static boolean inDialog = false;
    private static String displayString = "";

    public SampleGame() {
        super(new Vector2(800, 600), 60, "Second Chance");
    }

    public static void main(String[] args) {
        new SampleGame();
    }

    @Override
    protected void initialize() {

    }

    @Override
    protected void globalLogic(Level level) {
        displayString = null;

        ScriptUtils.setDebugMode(ScriptUtils.getKeyHolder().isPressedThenRelease(KeyEvent.VK_K) ? !ScriptUtils
                .isDebugMode() : ScriptUtils.isDebugMode());

        if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_ESCAPE)) {
            System.exit(0);
        }
    }

    @Override
    protected void setupCurrentLevel(final Level level) {

        level.getEntityWithId("player").addScript(new PlayerScript());

        Entity temp = level.getEntityWithId("wolf");
        if (temp != null)
            temp.addScript(new WolfScript());

        temp = level.getEntityWithId("pushblock1");
        if (temp != null) {
            temp.addScript(new EntityScript() {
                Entity player;

                public void onSpawn(Entity self, Level level) {
                    player = level.getEntityWithId("player");
                }

                public void onUpdate(Entity self, float time, Level level) {
                    ScriptUtils.moveOut(self, true, player);
                    ScriptUtils.moveOut(self, true, level.getSolids());
                    ScriptUtils.moveOut(player, true, self);
                }

                public void onDeath(Entity self, Level level, boolean isRoomExit) {
                }
            });
        }

        // Set up world portals.
        for (Entity e : level.getEntitiesWithType("worldPortal")) {
            e.addScript(new WorldPortalScript());
        }

    }

    @Override
    protected Level getStartingLevel() {
        try {
            return Importer.importLevel(new File("levels/MoreLevel.xml"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    /**
     * Set a string to be displayed in a prompt on screen for 1 frame.
     * 
     * @param s
     *            the string to set.
     */
    public static void setDisplayPrompt(final String s) {
        displayString = s;
    }

    @Override
    protected boolean render(Graphics2D rootCanvas, Level level) {
        try {
            rootCanvas.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            rootCanvas.setColor(Color.BLACK);
            rootCanvas.fillRect(0, 0, (int) VIEWPORT.x, (int) VIEWPORT.y);

            Vector2 offset = VIEWPORT.scale(.5f).sub(level.getEntityWithId("player").getPos());

            rootCanvas.translate(offset.getWidth(), offset.getHeight());

            for (int i = level.getLayers().size() - 1; i >= 0; i--)
                for (Entity e : level.getLayers().get(i)) {
                    if (e.isVisible() && e.isInView())
                        e.draw(rootCanvas);
                    if (!ReservedTypes.BACKGROUND.equals(e.getType()) && ScriptUtils.isDebugMode()) {
                        rootCanvas.setColor(Color.RED);
                        rootCanvas.drawRect((int) (e.getPos().x), (int) (e.getPos().y), (int) e.getDim().x,
                                (int) e.getDim().y);
                    }

                }
            
            ((Graphics2D) rootCanvas).translate(-offset.getWidth(), -offset.getHeight());

            rootCanvas.setColor(Color.white);
            String infoString = "[WASD] Move" + "  |  [K] Debug Mode: " + ScriptUtils.isDebugMode() + "  |  [Esc] Quit";

            rootCanvas.drawString(infoString, 5, (int) VIEWPORT.y - 5);

            if (displayString != null) {
                rootCanvas.drawString(displayString, (int) VIEWPORT.x / 2, (int) VIEWPORT.y / 2 + 64);
            }
        } catch (final Exception e) {
            return false;
        }
        return true;
    }
}
