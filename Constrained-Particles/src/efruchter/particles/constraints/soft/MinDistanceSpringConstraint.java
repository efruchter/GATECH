package efruchter.particles.constraints.soft;

import efruchter.particles.datatypes.Particle;

/**
 * Jakobsen constraint that has an activation threshold based on
 * distance. The distance must be below the given value for the constraint to
 * attempt satisfaction.
 * 
 * @author toriscope
 * 
 */
public class MinDistanceSpringConstraint extends DistanceConstraint {

	private float minDist;

	public MinDistanceSpringConstraint(final Particle a, final Particle b, final float minDist) {
		super(a, b, minDist, 1f);
		this.minDist = minDist;
	}

	@Override
	public void satisfy() {
		if (a.x.distance(b.x) < minDist) {
			super.satisfy();
		}
	}
}
