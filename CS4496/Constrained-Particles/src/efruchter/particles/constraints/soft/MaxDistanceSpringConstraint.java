package efruchter.particles.constraints.soft;

import efruchter.particles.datatypes.Particle;

/**
 * Jakobsen constraint that has an activation threshold based on
 * distance. The distance must be above the given value for the constraint to
 * attempt satisfaction.
 * 
 * @author toriscope
 * 
 */
public class MaxDistanceSpringConstraint extends DistanceConstraint {

	private float maxDist;

	public MaxDistanceSpringConstraint(final Particle a, final Particle b, final float maxDist) {
		super(a, b, maxDist, 1f);
		this.maxDist = maxDist;
	}

	@Override
	public void satisfy() {
		if (a.x.distance(b.x) > maxDist) {
			super.satisfy();
		}
	}
}