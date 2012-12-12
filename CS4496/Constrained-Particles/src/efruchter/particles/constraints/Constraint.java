package efruchter.particles.constraints;

import efruchter.particles.datatypes.Particle;

/**
 * Constraint class. Has a listener you can register to occur when satisfy is
 * run. When extending, be sure to call super.satify(). This particular
 * implementation doesn't do anything.
 * 
 * @author toriscope
 */
public class Constraint {

    protected Particle a, b;

    private ConstraintListener onSatisfy = null;

    public Constraint(final Particle a, final Particle b) {
        this.a = a;
        this.b = b;
    }

    public Particle getA() {
        return a;
    }

    public Particle getB() {
        return b;
    }

    /**
     * Call this from child to activate the constraint listener.
     */
    public void satisfy() {
        if (onSatisfy != null)
            onSatisfy.onSatisfy(a, b);
    }

    public void setConstraintListener(final ConstraintListener listener) {
        onSatisfy = listener;
    }

    /**
     * An action to perform when constraints occur.
     * 
     * @author toriscope
     */
    public static interface ConstraintListener {
        /**
         * Called post satisfaction
         * 
         * @param a
         *            the particle, a, or first input.
         * @param b
         *            the particle b, or the second input.
         */
        public void onSatisfy(Particle a, Particle b);
    }
}
