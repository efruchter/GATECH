package samplegame.customscripts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

import samplegame.SampleGame;
import toritools.dialog.DialogNode;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.sprite.AbstractSprite;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;

/**
 * This entity displays and controls a DialogNode.
 * 
 * @author toriscope
 * 
 */
public class DialogEntity extends Entity {

    private List<String> currentDisplay;

    /**
     * Spawn a DialogEntity that will display a given dialog, run it's action
     * when needed and advance its text as required.
     * 
     * @param dialogNode
     *            the dialog node to control/display.
     * @param entityToTrack
     *            the entity that the word bubble will track.
     */
    public DialogEntity(final DialogNode dialogNode, final Entity entityToTrack) {

        setPos(new Vector2(300, 300));

        setDim(new Vector2(500, 75));

        addScript(new EntityScript() {

            @Override
            public void onSpawn(Entity self, Level level) {
                SampleGame.inDialog = true;
            }

            @Override
            public void onUpdate(Entity self, float time, Level level) {

                SampleGame.setDisplayPrompt("Next <SPACE>");

                if (entityToTrack != null) {
                    self.setPos(entityToTrack.getPos().add(new Vector2(5, -(self.getDim().y + 10))));
                }

                if (getCurrentDisplay() == null
                        || ScriptUtils.getKeyHolder().isPressedThenRelease(KeyEvent.VK_SPACE)) {
                    setCurrentDisplay(dialogNode.getNextLines(3));
                    if (getCurrentDisplay().isEmpty()) {
                    	level.despawnEntity(self);
                    }
                }

            }

            @Override
            public void onDeath(Entity self, Level level, boolean isRoomExit) {
                SampleGame.inDialog = false;
                dialogNode.doAction(level);
            }

        });

        setLayer(0);

        setSprite(new AbstractSprite() {
            @Override
            public void draw(final Graphics2D g, final Entity self) {
                List<String> displayString = getCurrentDisplay();
                if (displayString == null) {
                    return;
                }
                g.setColor(Color.GRAY);

                int[] x = new int[] { (int) pos.x, (int) pos.x + 30, (int) pos.x + 5 };
                int[] y = new int[] { (int) pos.y, (int) pos.y + 30, (int) (pos.y + dim.y + 15) };
                g.fillPolygon(x, y, 3);
                g.fillRoundRect((int) pos.x, (int) pos.y, (int) dim.x, (int) dim.y, 4, 4);
                g.setColor(Color.WHITE);
                for (int i = 0; i < displayString.size(); i++) {
                    g.drawString(displayString.get(i), (int) pos.x + 20, (int) pos.y + 20 + i * 20);
                }
            }
			public void nextFrame() {}
			public void nextFrameAbsolute() {}
			public void setFrame(int frame) {}
			public void setCycle(int cycle) {}
			public void set(int frame, int cycle) {}
			public void setTimeStretch(int timeStretch) {}
			public void setsizeOffset(int sizeOffset) {}
			public Dimension getTileDimension() {return null;}
            public File getImageIndex() {return null;}
        });
    }

    private List<String> getCurrentDisplay() {
        return currentDisplay;
    }

    private void setCurrentDisplay(List<String> currentDisplay) {
        this.currentDisplay = currentDisplay;
    }
}
