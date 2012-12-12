package toritools.dialog;

import java.util.ArrayList;
import java.util.List;

import toritools.entity.Level;

/**
 * A dialog node. You may attach a DialogAction to the dialog if you want to
 * have something be done after the dialog is complete.
 * 
 * @author toriscope
 * 
 */
public class DialogNode {
    private final List<StringBuffer> sentences;
    private final DialogAction action;

    public DialogNode(final String sentence, final DialogAction action) {
        this.sentences = DialogSplitterUtility.lineParser(sentence, 70);
        this.action = action;
    }

    public DialogNode(final String sentence) {
        this(sentence, null);
    }

    /**
     * Remove the lines of text remaining in the node.
     * 
     * @param amount
     *            the amount of lines to get.
     * @return The List of strings.
     */
    public List<String> getNextLines(int amount) {
        amount = Math.min(sentences.size(), amount);
        List<String> sentencesList = new ArrayList<String>();
        while (amount-- != 0) {
            sentencesList.add(sentences.remove(0).toString());
        }
        return sentencesList;
    }

    public void doAction(final Level level) {
        if (action != null) {
            action.action(level);
        }
    }

    public boolean isEmpty() {
        return sentences.isEmpty();
    }

    /**
     * A simple action to be performed after a dialog.
     * 
     * @author toriscope
     * 
     */
    public static interface DialogAction {
        void action(final Level level);
    }
}
