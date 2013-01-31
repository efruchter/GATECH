package samplegame.customscripts;

import java.awt.event.KeyEvent;
import java.util.Random;

import samplegame.SampleGame;
import toritools.dialog.DialogNode;
import toritools.dialog.DialogNode.DialogAction;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;
import toritools.scripting.ScriptUtils.Direction;

/**
 * The script for the wolf. This shows how to spawn instances on the fly, as
 * well as how to use the script to store state.
 * 
 * @author toriscope
 * 
 */
public class WolfScript implements EntityScript {
	private Random rand = new Random();
	private float speed = .2f;
	private float direction = 0;

	private Entity player;

	public void onSpawn(Entity self, Level level) {
		player = level.getEntityWithId("player");
		newDirection();
	}

	public void onUpdate(final Entity self, float time, Level level) {
		if (SampleGame.inDialog)
			return;
		if (rand.nextDouble() > .99)
			newDirection();
		if (rand.nextDouble() > .8) {
			self.setPos(self.getPos()
					.add(Vector2.buildVector(direction).scale(time * speed)));
			ScriptUtils.moveOut(self, false, level.getSolids());
			self.getSprite().nextFrame();
		}

		switch (Direction.findEnum(direction)) {
		case DOWN:
		case DOWN_RIGHT:
		case DOWN_LEFT:
			self.getSprite().setCycle(3);
			break;
		case UP:
		case UP_RIGHT:
		case UP_LEFT:
			self.getSprite().setCycle(0);
			break;
		case RIGHT:
			self.getSprite().setCycle(2);
			break;
		case LEFT:
			self.getSprite().setCycle(1);
			break;
		}

		if (!SampleGame.inDialog && ScriptUtils.isColliding(self, player)) {

			SampleGame.setDisplayPrompt("Talk <SPACE>");

			/*
			 * This spawns a dialog entity, and attaches a DialogNode with a
			 * lengthy saying. It then attaches an action to that dialogNode,
			 * one that adds a new DialogEntity to the level. This is one way to
			 * chain dialog, or give the player items, etc..
			 */
			if (ScriptUtils.getKeyHolder().isPressedThenRelease(KeyEvent.VK_SPACE)) {
				level.spawnEntity(new DialogEntity(
						new DialogNode(
								"The 1689 Boston revolt was a popular uprising against the rule of Sir Edmund Andros (pictured), governor of the Dominion of New England that followed the Glorious Revolution deposing James II of England, who had appointed Andros. During the revolt, on April 18, 1689, a well-organized body of Puritan citizens and militiamen entered the dominion capital of Boston and arrested officials of the dominion, a colonial entity composed of present-day Maine, New Hampshire, Vermont, Massachusetts, Rhode Island, Connecticut, New York, and New Jersey. The rebellion was inspired by actions taken by Andros and dominion administrators, including promoting the Church of England, invalidating land titles, and famously attempting to seize the colonial charter of Connecticut.",
								new DialogAction() {
									public void action(final Level level) {
										level.spawnEntity(new DialogEntity(new DialogNode("Did you get all that?"), self));
									}
								}), self));
			}
		}
	}

	public void onDeath(Entity self, Level level, boolean isRoomExit) {
	}

	private void newDirection() {
		direction = rand.nextFloat() * 6.28f;
	}
}
