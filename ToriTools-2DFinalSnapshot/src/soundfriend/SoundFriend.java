package soundfriend;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import soundfriend.types.Creature;
import soundfriend.types.GUIController;
import toritools.debug.Debug;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entrypoint.Binary;
import toritools.math.Vector2;
import toritools.scripting.ScriptUtils;

public class SoundFriend extends Binary {

    private Creature creature;

    private static JLabel messageLabel = new JLabel("Pet Message!");
    private static GUIController gui;

    private int timer = 0;

    private final File backgroundImage = new File("tamodatchi/grass.png");

    public SoundFriend() {
        super(new Vector2(800, 600), 60, "Kawaii Sound Friend Yes!");
    }

    @Override
    protected void initialize() {
        Debug.showDebugPrintouts = false;
        gui = new GUIController(messageLabel, super.getApplicationPanel());
        messageLabel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        super.getApplicationPanel().setFocusable(true);
        super.getApplicationFrame().add(messageLabel, BorderLayout.SOUTH);
        super.getApplicationFrame().setResizable(false);
        super.getApplicationFrame().pack();
        super.getApplicationFrame().setIconImage(ScriptUtils.fetchImage(new File("tamodatchi/kitten_icon.png")));
    }

    @Override
    protected void globalLogic(Level level) {

        if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_ESCAPE)) {
            System.exit(0);
        }

        if (ScriptUtils.getKeyHolder().isPressedThenRelease(KeyEvent.VK_P)) {
            Debug.showDebugPrintouts = !Debug.showDebugPrintouts;
        }

        if (--timer < 0) {
            timer = 30;
            gui.updateInfo(creature);
        }
    }

    @Override
    protected void setupCurrentLevel(Level levelBeingLoaded) {
        creature = new Creature();
        levelBeingLoaded.spawnEntity(creature);
    }

    @Override
    protected Level getStartingLevel() {
        Level level = new Level();
        level.setDim(VIEWPORT);
        return level;
    }

    @Override
    protected boolean render(Graphics2D rootCanvas, Level level) {
        try {
            rootCanvas.drawImage(ScriptUtils.fetchImage(backgroundImage), 0, 0, VIEWPORT.getWidth(),
                    VIEWPORT.getHeight(), null);
            for (List<Entity> layer : level.getLayers()) {
                for (Entity entity : layer) {
                    entity.draw(rootCanvas);
                }
            }
        } catch (final Exception exception) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        new SoundFriend();
    }

}
