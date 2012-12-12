package efruchter.particles.integrators;

import efruchter.particles.datatypes.Particle;
import efruchter.particles.integrators.NewtonianIntegrators.RKState;
import efruchter.vectorutils.Vector3;

/**
 * State based functions for common force setups.
 * 
 * @author toriscope
 * 
 */
public class CommonForceFunctions {
    private CommonForceFunctions() {
    }

    /**
     * Calculate the force effect on a particle in a given state, when it is
     * rigged to a circle and held in place by a virtual restoring force.
     * 
     * @param state
     *            the state of the particle on the circle.
     * @param anchor
     *            the particle that anchors the center.
     * @param prexistingForces
     *            the forces that will be cancelled by the restoring force.
     * @param particleMass
     *            the mass of the particle on circle.
     * @return the restoring force.
     */
    public static Vector3 radialVirtual(final RKState state, final Particle anchor, final Vector3 prexistingForces,
            final float particleMass) {
        Vector3 x = state.x.sub(anchor.x);
        Vector3 v = state.v;
        float lambda = (-prexistingForces.dot(x) - v.scale(particleMass).dot(v)) / x.dot(x);
        return x.scale(lambda);
    }

    /**
     * 
     * @param state
     *            the state of the particle on the circle.
     * @param anchor
     *            the particle that anchors the stabel side of spring.
     * @param particleMass
     *            the mass of the particle on circle.
     * @param springConstant
     *            the k
     * @param restLength
     *            the rest length of the spring.
     * @return spring force.
     */
    public static Vector3 anchoredSpring(final RKState state, final Particle anchor, final float particleMass,
            final float springConstant, final float restLength) {
        float x = anchor.x.distance(state.x) - restLength;
        Vector3 springForce = state.x.sub(anchor.x).unit();
        return springForce.scale(-springConstant * x / particleMass);
    }
}
