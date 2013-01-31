package snakemeleon;

import java.awt.Color;
import java.awt.Graphics2D;

import snakemeleon.types.Collectable;
import toritools.entity.Level;
import toritools.math.Vector2;
import toritools.scripting.ScriptUtils;

public class SnakemeleonHUD {

    //private int splashW = 270, splashH = 132;

    long currentTime = 0;

    boolean chamIsDead = false;

    public void update(long timeStep, final Level level) {
        if (Collectable.getCollectablesRemaining() == 0) {
            currentTime += timeStep;
        } else {
            currentTime = 0;
        }
        chamIsDead = !level.getEntityWithId("player").isActive();
    }

    public void draw(Graphics2D g, final Vector2 viewport) {

        if (currentTime > 60) {
            /*
             * Draw the in between level splash screen.
             */
            g.drawImage(ScriptUtils.fetchImage(SnakemeleonConstants.victoryImageFile), 0, 0, viewport.getWidth(),
                    viewport.getHeight(), null);
            g.setColor(Color.black);
            g.drawString("Challenge Complete!", viewport.getWidth() / 2, viewport.getHeight() / 2);
            if (currentTime > 60 * 3) {
                Snakemeleon.nextLevel();
                currentTime = 0;
            }
        } else {
            if (!chamIsDead) {
                g.setColor(Color.CYAN);
                // g.drawImage(ScriptUtils.fetchImage(SnakemeleonConstants.hudImageFile),
                // viewport.getWidth() - splashW,
                // viewport.getHeight() - splashH, splashW, splashH, null);
                // g.drawString("Apples Remaining: " +
                // Collectable.getCollectablesRemaining(), viewport.getWidth() -
                // splashW,
                // viewport.getHeight() - splashH);
                g.drawString("Apples Remaining: " + Collectable.getCollectablesRemaining(), 5, 25);
            } else {
                g.setColor(Color.RED);
                g.drawString("DEAD", viewport.getWidth() / 2, viewport.getHeight() / 5);
            }
        }
    }
}
