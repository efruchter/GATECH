package toritools.entity.physics;

import toritools.entity.Entity;
import toritools.math.Vector2;

public class PhysicsModule {

	/**
	 * Current acceleration
	 */
	private Vector2 acc = Vector2.ZERO;

	/**
	 * Previous position
	 */
	private Vector2 prePos = Vector2.ZERO;

	/**
	 * Global accel.
	 */
	private final Vector2 g;

	private Vector2 gDrag;

	private final Entity self;

	public PhysicsModule(final Vector2 globalAcceleration,
			final Vector2 globalDrag, final Entity self) {
		g = globalAcceleration;
		this.self = self;
		gDrag = globalDrag;
		prePos = self.getPos();
	}

	/**
	 * Calculates forces on entity, and generates the movement vector. The
	 * acceleration is then cleared.
	 * 
	 * @param time
	 *            the time in milliseconds between frame delays.
	 * @return the displacement vector.
	 */
	public Vector2 onUpdate(final float time) {

		// Add global accel
		acc = acc.add(g);
		acc = acc.unit().scale(acc.mag() * time);
		
		Vector2 velocity = self.getPos().sub(prePos).add(acc);

		velocity = velocity.unit().scale(gDrag.scale(velocity.mag()));		

		prePos = self.getPos();

		acc = Vector2.ZERO;

		return velocity;
	}

	public void addVelocity(final Vector2 vel) {
		prePos = prePos.sub(vel);
	}

	public Vector2 getCurrentVelocity() {
		return self.getPos().sub(prePos);
	}

	public void clearVelocity() {
		prePos = self.getPos();
	}

	public void clearXVelocity() {
		prePos = new Vector2(self.getPos().x, prePos.y);
	}

	public void clearYVelocity() {
		prePos = new Vector2(prePos.x, self.getPos().y);
	}

	public void addAcceleration(final Vector2 a) {
		acc = acc.add(a);
	}

	public float getMass() {
		return 1f;
	}

	public void setgDrag(final Vector2 drag) {
		gDrag = drag;
	}

	public Vector2 getgDrag() {
		return gDrag;
	}
}
