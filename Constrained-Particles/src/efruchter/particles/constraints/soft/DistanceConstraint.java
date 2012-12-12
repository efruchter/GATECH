package efruchter.particles.constraints.soft;

import efruchter.particles.constraints.Constraint;
import efruchter.particles.datatypes.Particle;
import efruchter.vectorutils.Vector3;

/**
 * A simple, cheap constraint type that can be iterated for speed v. accuracy.
 * Min/max versions of this can be found in this package.
 * 
 * @author toriscope
 * 
 */
public class DistanceConstraint extends Constraint {
	protected float dist, squish;
	public static float DEFAULT_SQUISH = .5f;
	protected float scale = .5f;

	/**
	 * Create a constraint with a given distance and squish, with 0 as maximum
	 * give, and 1 as no give.
	 * 
	 * @param a
	 *            particle a.
	 * @param b
	 *            particle b.
	 * @param dist
	 *            distance between particles.
	 * @param squish
	 *            the amount of give.
	 */
	public DistanceConstraint(final Particle a, final Particle b,
			final float dist, final float squish) {
		super(a, b);
		this.dist = dist;
		this.squish = squish;
	}

	public DistanceConstraint(final Particle a, final Particle b) {
		this(a, b, a.x.distance(b.x), DEFAULT_SQUISH);
	}

	/**
	 * Move the particles a/b to satisfy constraint.
	 */
	public void satisfy() {
		Vector3 x1 = this.a.x, x2 = this.b.x;
		Vector3 delta = x2.sub(x1);
		delta = delta.scale(dist * dist / (delta.dot(delta) + dist * dist) - scale);
		this.a.x = x1.sub(delta.scale(squish * a.m));
		this.b.x = x2.add(delta.scale(squish * b.m));
		super.satisfy();
	}
}
