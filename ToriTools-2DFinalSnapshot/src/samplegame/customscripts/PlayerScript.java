package samplegame.customscripts;

import java.awt.event.KeyEvent;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;

public class PlayerScript implements EntityScript {
	public void onSpawn(Entity self, Level level) {
		System.out.println("The kid is spawned!");
		String warpTo;
		if ((warpTo = ScriptUtils.getVar("warpTo")) != null) {
			Entity portal;
			if ((portal = level.getEntityWithId(warpTo)) != null) {
				self.setPos(portal.getPos());
				ScriptUtils.setVar("warpTo", null);
			} else {
				System.out.println("Could not warp player to " + warpTo + "!");
			}
		}
	}

	public void onUpdate(Entity self, float time, Level level) {
		float speed = time * .15f;
		boolean walked = false;
		Vector2 delta = new Vector2();

		if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_A)) {
			walked = true;
			delta = delta.add(-speed, 0);
			self.getSprite().setCycle(1);
		}
		if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_D)) {
			walked = true;
			delta = delta.add(speed, 0);
			self.getSprite().setCycle(2);
		}

		if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_W)) {
			walked = true;
			delta = delta.add(0, -speed);
			self.getSprite().setCycle(3);
		}

		if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_S)) {
			walked = true;
			delta = delta.add(0, speed);
			self.getSprite().setCycle(0);
		}

		self.setPos(self.getPos().add(delta));

		ScriptUtils.moveOut(self, false, level.getSolids());

		if (walked)
			self.getSprite().nextFrame();
	}

	public void onDeath(Entity self, Level level, boolean isRoomExit) {
		if (isRoomExit)
			System.out.println("The kid has been lost forever (room closed).");
	}
}
