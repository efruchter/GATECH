package audioProject.entities;

import java.awt.Color;
import java.awt.Graphics2D;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.sprite.AbstractSprite.AbstractSpriteAdapter;
import toritools.math.Vector2;
import toritools.scripting.EntityScript.EntityScriptAdapter;
import toritools.scripting.ScriptUtils;

public class HealthPickup extends Entity {

	public HealthPickup(final Vector2 position) {
		type = "health";

		layer = 1;

		pos = position;
		dim = new Vector2(20, 20);

		addScript(new EntityScriptAdapter() {
			
			boolean explodeDeath = true;

			@Override
			public void onUpdate(Entity self, float time, Level level) {
				
				if (!ScriptUtils.isColliding(level, self)) {
					level.despawnEntity(self);
					explodeDeath = false;
				}

				self.setPos(self.getPos().add(new Vector2(-1, 0)));
			}

			@Override
			public void onDeath(Entity self, Level level, boolean isRoomExit) {
				if (explodeDeath)
					level.spawnEntity(new Explosion(self.getPos(), Color.GREEN, self.getDim().x, 20));
			}
		});

		setSprite(new AbstractSpriteAdapter() {
			@Override
			public void draw(Graphics2D g, Entity self) {
				g.setColor(Color.GREEN);
				g.fillOval((int) self.getPos().x, (int) (self.getPos().y), (int) self.getDim().x, (int) self.getDim().y);
			}
		});
	}
}
