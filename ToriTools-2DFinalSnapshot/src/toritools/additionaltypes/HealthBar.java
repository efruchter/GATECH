package toritools.additionaltypes;

import java.awt.Color;
import java.awt.Graphics;

import toritools.math.Vector2;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * A health bar that blends two colors as it goes.
 * @author toriscope
 *
 */
public class HealthBar{

	private final Color minColor, maxColor;
	
	private final float maxHealth;
	private float health = 0;

	public HealthBar(final float maxHealth, final Color minColor, final Color maxColor) {
		this.minColor = minColor;
		this.maxColor = maxColor;
		this.maxHealth = this.health = maxHealth;
	}

	public void draw(Graphics g, final Vector2 pos, final Vector2 dim) {
		float ratio = health / maxHealth;
		g.setColor(ColorUtils.blend(maxColor, minColor, ratio));
		g.fillRect((int) pos.x, (int) pos.y, (int) (dim.x * ratio), (int) dim.y);
		g.drawRect((int) pos.x, (int) pos.y, (int) dim.x, (int) dim.y);
	}
	
	public float getHealth() {
		return health;
	}
	
	public float getMaxHealth() {
		return maxHealth;
	}

	public void setHealth(float health) {
		this.health = min(max(health, 0), maxHealth);
	}
}
