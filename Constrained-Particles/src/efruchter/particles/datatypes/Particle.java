package efruchter.particles.datatypes;

import efruchter.particles.integrators.NewtonianIntegrators.AccelerationFunction;
import efruchter.particles.integrators.NewtonianIntegrators.RKState;
import efruchter.vectorutils.Vector3;

/**
 * A state based particle structure, with some useful fields for various types
 * of integrators.
 * 
 * @author toriscope
 * 
 */
public class Particle {

    /**
     * Position
     */
    public Vector3 x = Vector3.ZERO;

    /**
     * Extra position slot for implicit velocity integration.
     */
    public Vector3 xOld = Vector3.ZERO;

    /**
     * Velocity
     */
    public Vector3 v = Vector3.ZERO;

    /**
     * Mass
     */
    public float m = 1;

    /**
     * Force Accumulator
     */
    public Vector3 f = Vector3.ZERO;

    /**
     * Time.
     */
    public float time = 0;

    /**
     * When you plan on using rk4, set this up.
     */
    public AccelerationFunction accelerationFunction = AccelerationFunction.BLANK;

    /**
     * Instantiates all values to zero, mass to 1.
     */
    public Particle() {

    }

    /**
     * Create particle with position x. xOld will be set to a clone of x.
     * 
     * @param x
     *            starting position
     */
    public Particle(final Vector3 x) {
        this.x = xOld = x;
    }

    /**
     * Zero out force accumulator.
     */
    public void clearForces() {
        f = Vector3.ZERO;
    }

    /**
     * Add values to force accumulator. If you desire to factor in mass, be sure
     * to do it prior to account for it.
     * 
     * @param delta
     *            values
     */
    public void addForce(final Vector3 delta) {
        f = f.add(delta);
    }

    @Override
    public String toString() {
        return x.toString();
    }

    public Vector3 getImplicitVelocity() {
        return x.sub(xOld);
    }

    /**
     * Resets all variables sans position/m. sets xOld = x;
     */
    public void reset() {
        v = xOld = f = Vector3.ZERO;
        time = 0;
        accelerationFunction = AccelerationFunction.BLANK;
    }

    public void accumulateAccelFunction() {
        addForce(accelerationFunction.getAcceleration(new RKState(x, v), time));
    }
}
