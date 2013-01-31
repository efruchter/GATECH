package soundfriend.types;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import soundfriend.types.Creature.State;
import toritools.scripting.ScriptUtils;

public class GUIController implements ActionListener {

    private JProgressBar moodBar, energyBar;
    private JLabel nameLabel, messageLabel;
    private JButton playButton, feedButton;

    private boolean spawnFood = false, spawnBall = false;

    public GUIController(final JLabel messageLabel, final JPanel corePanel) {

        this.messageLabel = messageLabel;

        // corePanel.setLayout(new BoxLayout(corePanel, BoxLayout.Y_AXIS));

        nameLabel = new JLabel("Chet the Named One");
        nameLabel.setForeground(Color.WHITE);

        moodBar = new JProgressBar(0, 100);
        moodBar.setString("Mood");
        moodBar.setValue(90);
        moodBar.setStringPainted(true);

        energyBar = new JProgressBar(0, 100);
        energyBar.setString("Energy");
        energyBar.setValue(10);
        energyBar.setStringPainted(true);

        feedButton = new JButton("Feed");
        feedButton.addActionListener(this);

        playButton = new JButton("Play");
        playButton.addActionListener(this);

        corePanel.add(nameLabel);
        corePanel.add(moodBar);
        corePanel.add(energyBar);
        corePanel.add(feedButton);
        corePanel.add(playButton);
    }

    public void updateInfo(final Creature creature) {

        if (spawnBall) {
            spawnBall = false;
            Ball f = new Ball();
            ScriptUtils.getCurrentLevel().spawnEntity(f);
        }

        if (spawnFood) {
            spawnFood = false;
            Food f = new Food();
            ScriptUtils.getCurrentLevel().spawnEntity(f);
        }

        moodBar.setValue((int) (creature.getMood() * 100));
        energyBar.setValue((int) (creature.getEnergy() * 100));
        nameLabel.setText(creature.getName());

        StringBuffer message = new StringBuffer();

        // State messages
        if (creature.getState() == State.ROAM) {
            message.append(" ").append(creature.getName() + " is roaming around");
        }

        if (creature.getState() == State.SLEEP) {
            message.append(" ").append(creature.getName() + " is fast asleep");
        }

        if (creature.getState() == State.HUNTING) {
            message.append(" ").append(creature.getName() + " is looking for food");
        }

        if (creature.getState() == State.PLAYING) {
            message.append(" ").append(creature.getName() + " is playing with a ball");
        }

        if (creature.isSick()) {
            message.append(" ").append(
                    creature.getName() + " is feeling " + creature.sickPercentage() + "% sick from overeating!");
            if (creature.getMood() < .2f) {
                message.append(" ").append(creature.getName() + " is extremely unhappy!");
            }
        }

        messageLabel.setText(message.toString().trim());
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // Feed
        if (e.getSource() == feedButton) {
            spawnFood = true;
        }

        // play
        if (e.getSource() == playButton) {
            spawnBall = true;
        }
    }
}
