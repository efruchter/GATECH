package efruchter.particles.integrators;

import efruchter.particles.datatypes.Particle;
import efruchter.vectorutils.Vector3;

/**
 * Numerical integrators for solving newtons "f = ma". For each of these, it is
 * assumed that mass was factored in when forces were accumulated on the
 * particle. This will update the particle, as well as the necessary fields, per
 * integration type, but will not clear the force accumulator.
 * 
 * @author toriscope
 */
public class NewtonianIntegrators {

    private NewtonianIntegrators() {
        // Prevent instantiation
    }

    /**
     * Constraint friendly, fast integrator. Works well with soft spring
     * constraints. 2nd order reversible.
     * 
     * @param p
     *            the particle. xOld will be used to derive current velocity.
     * @param dt
     */
    public static void verlet(final Particle p, final float dt) {
        Vector3 tempCurr = p.x;
        p.x = p.x.scale(2).sub(p.xOld).add(p.f.scale(dt * dt));
        p.xOld = tempCurr;

        p.time += dt;
    }

    /**
     * Good old fashion RK1. The silliest integrator known to man or machine.
     * 
     * @param p
     *            the particle. v will will be used and modified, as well as x.
     * @param dt
     */
    public static void explicitEuler(final Particle p, final float dt) {
        // P(h + t) = P(t) + v * h
        p.x = p.x.add(p.v.scale(dt));

        // V(h + t) = V(t) + a * timeStep
        p.v = p.v.add(p.f.scale(dt));

        p.time += dt;
    }

    /**
     * Good old fashion RK1, over stable.
     * 
     * @param p
     *            the particle. v will will be used and modified, as well as x.
     * @param dt
     */
    public static void implicitEuler(final Particle p, final float dt) {
        // Get explicit approx for n + 1

        // V(h + t) = V(t) + a * timeStep
        p.v = p.v.add(p.f.scale(dt));

        // P(h + t) = P(t) + v * h
        p.x = p.x.add(p.v.scale(dt));

        p.time += dt;
    }

    /**
     * Half explicit, half implicit, rk2 integrator.
     * 
     * @param p
     * @param dt
     */
    public static void trapazoidal(final Particle p, final float dt) {
        /*
         * Get velocity at t + 1
         */
        Vector3 v1 = p.v.add(p.f.scale(dt));

        /*
         * Add each velocity to get the new full step velocity. x_n + (f(x_n) +
         * f(x_n+1)) * .5 * timeStep
         */
        p.x = p.x.add(p.v.add(v1).scale(dt / 2));
        p.v = v1;

        p.time += dt;
    }

    /**
     * 4th order runge kutta. Powerful and accurate up to changes in jerk, but
     * costly. This version uses the acceleration function in the particle.
     * 
     * @param p
     * @param dt
     */
    public static void rungeKutta4(final Particle p, final float dt) {
        rungeKutta4(p, dt, p.accelerationFunction);
    }

    /**
     * 4th order runge kutta. Powerful and accurate up to changes in jerk, but
     * costly.
     * 
     * @param p
     * @param dt
     * @param acc
     *            an acceleration function so accurate predictions can be made.
     */
    public static void rungeKutta4(final Particle p, final float dt,
            final AccelerationFunction acc) {
        float t = p.time;
        RKState state = new RKState(p.x, p.v);
        RKDerivative a = evaluate(state, t, 0, RKDerivative.ZERO, acc);
        RKDerivative b = evaluate(state, t + dt * 0.5f, dt * 0.5f, a, acc);
        RKDerivative c = evaluate(state, t + dt * 0.5f, dt * 0.5f, b, acc);
        RKDerivative d = evaluate(state, t + dt, dt, c, acc);

        Vector3 dx = (a.dx.add(b.dx.scale(2)).add(c.dx.scale(2)).add(d.dx)).scale(1f / 6f);
        Vector3 dv = (a.dv.add(b.dv.scale(2)).add(c.dv.scale(2)).add(d.dv)).scale(1f / 6f);

        p.x = p.x.add(dx.scale(dt));
        p.v = p.v.add(dv.scale(dt));
        p.time += dt;

    }

    private static RKDerivative evaluate(RKState initial, float t, float dt,
            RKDerivative d, AccelerationFunction acc) {
        RKState state = new RKState(initial.x.add(d.dx.scale(dt)),
                initial.v.add(d.dv.scale(dt)));
        return new RKDerivative(state.v, acc.getAcceleration(state, t + dt));
    }

    /**
     * A state for a particle to be in. Immutable, for clarity.
     * 
     * @author Eric
     * 
     */
    public static class RKState {
        public final Vector3 x, v;

        public RKState(final Vector3 x, final Vector3 v) {
            this.x = x;
            this.v = v;
        }
    }

    private static class RKDerivative {
        public final Vector3 dx, dv;

        public RKDerivative(Vector3 dx, Vector3 dv) {
            this.dx = dx;
            this.dv = dv;
        }

        public static RKDerivative ZERO = new RKDerivative(Vector3.ZERO,
                Vector3.ZERO);
    }

    /**
     * A function for getting acceleration at a given particle state and time.
     * 
     * @author Eric
     * 
     */
    public static interface AccelerationFunction {
        /**
         * The acceleration function!
         * 
         * @param state
         *            the x/v state parameter.
         * @param time
         *            the time, t;
         * @return the result of the acceleration function given the two inputs
         *         provided.
         */
        Vector3 getAcceleration(RKState state, float time);
        
        public static AccelerationFunction BLANK = new AccelerationFunction(){
            @Override
            public Vector3 getAcceleration(RKState state, float time) {
                return Vector3.ZERO;
            }
        };
    }
}
