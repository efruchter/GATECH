package audioProject.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import toritools.additionaltypes.HealthBar;
import toritools.additionaltypes.HistoryQueue;
import toritools.controls.KeyHolder;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.physics.PhysicsModule;
import toritools.entity.sprite.AbstractSprite.AbstractSpriteAdapter;
import toritools.math.Vector2;
import toritools.scripting.EntityScript.EntityScriptAdapter;
import toritools.scripting.ScriptUtils;
import audioProject.AudioProject;

public class PlayerShip extends Entity {

	protected boolean vincible = true;
	
	public boolean isVincible() {
		return vincible;
	}

	public void setVincible(boolean vincible) {
		this.vincible = vincible;
	}



	public PlayerShip() {
		
		layer = 3;
		
		variables.setVar("id", "player");
		
		pos = new Vector2(100, 10);
		dim = new Vector2(2, 2);
		
		final Vector2 offsetSize = new Vector2(7, 7), offsetSize2 = offsetSize.scale(2);
		
		final HistoryQueue<Vector2> pastPos = new HistoryQueue<Vector2>(5);
		
		final HealthBar healthBar = new HealthBar(100, Color.RED, Color.GREEN);

		addScript(new EntityScriptAdapter() {

			final char UP = KeyEvent.VK_W, DOWN = KeyEvent.VK_S,
					RIGHT = KeyEvent.VK_D, LEFT = KeyEvent.VK_A,
					SHOOT = KeyEvent.VK_SPACE, SPREAD_UP = KeyEvent.VK_PERIOD,
					SPREAD_DOWN = KeyEvent.VK_COMMA;

			float speed = .002f;
			
			int canShoot = 0;

			KeyHolder keys;
			
			PhysicsModule physics;
			
			float spread = .1f, spreadFactor = .02f;

			@Override
			public void onSpawn(Entity self, Level level) {
				self.setPos(level.getDim().scale(.25f, .5f));
				keys = ScriptUtils.getKeyHolder();
				physics = new PhysicsModule(Vector2.ZERO, new Vector2(.9f), self);
				healthBar.setHealth(100);
			}

			@Override
			public void onUpdate(Entity self, float time, Level level) {
				
				float speed = this.speed * time;

				if (keys.isPressed(UP)) {
					physics.addAcceleration(new Vector2(0, -speed));
				}

				if (keys.isPressed(DOWN)) {
					physics.addAcceleration(new Vector2(0, speed));
				}

				if (keys.isPressed(LEFT)) {
					physics.addAcceleration(new Vector2(-speed, 0));
				}

				if (keys.isPressed(RIGHT)) {
					physics.addAcceleration(new Vector2(speed, 0));
				}
				
				if (keys.isPressed(SPREAD_DOWN)) {
					spread = Math.max(spread - spreadFactor, 0);
				}
				
				if (keys.isPressed(SPREAD_UP)) {
					spread = Math.min(spread + spreadFactor, 4);
				}
				
				if (keys.isPressedThenRelease(KeyEvent.VK_P)) {
					setVincible(!isVincible());
				}
				if (!isVincible()) {
					healthBar.setHealth(100);
				}

				if (canShoot-- < 0 && keys.isPressed(SHOOT)) {
					canShoot = 10;
					level.spawnEntity(new GoodBullet(self.getPos(), new Vector2(1, -spread).unit()));
					level.spawnEntity(new GoodBullet(self.getPos(), Vector2.RIGHT));
					Entity boolet = new GoodBullet(Vector2.ZERO, new Vector2(1, spread).unit());
					boolet.setPos(self.getPos().add(0, self.getDim().y - boolet.getDim().y));
					level.spawnEntity(boolet);
				}
				
				Vector2 delta  = physics.onUpdate(time);
				
				self.setPos(self.getPos().add(delta));
				
				ScriptUtils.moveOut(self, false, level.getSolids());
				
				pastPos.push(self.getPos());
				
				for (Entity badBullet : level.getEntitiesWithType("BadBullet")) {
					if (ScriptUtils.isCollidingRad(self, badBullet)) {
						level.despawnEntity(badBullet);
						healthBar.setHealth(healthBar.getHealth() - badBullet.getVariableCase().getFloat("damage"));
					}
				}
				
				for (Entity health : level.getEntitiesWithType("health")) {
					if (ScriptUtils.isCollidingRad(self, health)) {
						level.despawnEntity(health);
						healthBar.setHealth(100);
					}
				}
				
				if(healthBar.getHealth() <= 0) {
					AudioProject.seek(0, level);					
				}
			}
		});

		setSprite(new AbstractSpriteAdapter() {

			@Override
			public void draw(Graphics2D g, Entity self) {
				
				//g.fillOval((int) position.x, (int) position.y, (int) dimension.x, (int) dimension.y);
				
				healthBar.draw(g, new Vector2(10, 50), new Vector2(200, 30));
				
				Color c = isVincible() ? AudioProject.shipColor : Color.GREEN; //ColorUtils.blend(Color.BLACK, Color.BLUE, Math.abs(AudioProject.controller.getFeel()));
				int alpha = 255;
				for(Vector2 hPos: pastPos) {
					g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
					g.drawOval((int) (hPos.x - offsetSize.x), (int) (hPos.y - offsetSize.y), (int) (offsetSize2.x + self.getDim().x), (int) (offsetSize2.y + self.getDim().y));
					alpha = alpha / 2;
				}
				
				g.setColor(Color.DARK_GRAY);
				g.fillOval((int) self.getPos().x, (int) (self.getPos().y), (int) self.getDim().x, (int) self.getDim().y);
			}
		});
	}
}
