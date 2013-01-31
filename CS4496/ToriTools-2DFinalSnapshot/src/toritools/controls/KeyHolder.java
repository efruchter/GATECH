package toritools.controls;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Mechanism for detecting if a key is pressed.
 * 
 * @author toriscope
 * 
 */
public class KeyHolder extends KeyAdapter {
    private HashMap<Integer, Boolean> keyBox = new HashMap<Integer, Boolean>();
    private List<Integer> freeQueue = new ArrayList<Integer>();

    public KeyHolder() {
    }

    public void clearKeys() {
        keyBox.clear();
    }

    /**
     * Checks to see if a key is pressed.
     * 
     * @param key
     *            the key to poll for.
     * @return whether or not the key is being pressed.
     */
    public boolean isPressed(int key) {
        if (!keyBox.containsKey(key))
            return false;
        return keyBox.get(key);
    }

    /**
     * Imitates a normal keyPressed event, by returning the result of isPressed,
     * and if the key is currently being held down, releases it when
     * freeQueuedKeys() is called.
     * 
     * @param key
     *            the key to poll for.
     * @return whether or not the key is being pressed.
     */
    public boolean isPressedThenRelease(int key) {
        if (!keyBox.containsKey(key))
            return false;
        freeQueue.add(key);
        Boolean b = keyBox.get(key);
        return b == null ? false : b;
    }

    /**
     * Triggered immediately when key is simply pressed upon.
     */
    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (keyBox.get(keyEvent.getKeyCode()) == null) {
            keyBox.put(keyEvent.getKeyCode(), true);
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        keyBox.remove(keyEvent.getKeyCode());
    }

    /**
     * Free the current keys that are queued for freeing.
     */
    public void freeQueuedKeys() {
        for (Integer key : freeQueue) {
            keyBox.put(key, false);
        }
        freeQueue.clear();
    }
}
