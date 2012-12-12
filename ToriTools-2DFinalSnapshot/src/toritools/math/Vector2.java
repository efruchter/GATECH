package toritools.math;

import java.awt.Dimension;
import java.awt.Point;

import org.jbox2d.common.Vec2;

/**
 * 2D Float Vector with basic math functions. Immutable.
 * 
 * @author efruchter
 * 
 */
public class Vector2 {

    public final float x, y;

    public final static Vector2 ZERO = new Vector2();
    public final static Vector2 ONE = new Vector2(1, 1);
    public final static Vector2 UP_RIGHT = ONE.unit();
    public final static Vector2 DOWN_RIGHT = ONE.scale(1, -1).unit();
    public final static Vector2 RIGHT = new Vector2(1, 0);

    public Vector2(final float x, final float y) {
        this.x = x;
        this.y = y;
    }
    
    public Vector2(final double x, final double y) {
        this.x = (float) x;
        this.y = (float) y;
    }

    public Vector2(final Dimension dim) {
        this.x = dim.width;
        this.y = dim.height;
    }

    public Vector2(final float both) {
        this(both, both);
    }

    public Vector2(final Vector2 base) {
        this.x = base.getX();
        this.y = base.getY();
    }

    public Vector2(final Point base) {
        this.x = base.x;
        this.y = base.y;
    }

    public Vector2() {
        this(0, 0);
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public Vector2 add(final Vector2 o) {
        return new Vector2(this.x + o.getX(), this.y + o.getY());
    }

    public Vector2 add(final float x, final float y) {
        return new Vector2(this.x + x, this.y + y);
    }

    public Vector2 add(final float both) {
        return add(both, both);
    }

    public Vector2 sub(final Vector2 o) {
        return new Vector2(this.x - o.getX(), this.y - o.getY());
    }

    public Vector2 sub(final float o) {
        return new Vector2(this.x - o, this.y - o);
    }

    public Vector2 sub(final float x, final float y) {
        return new Vector2(this.x - x, this.y - y);
    }

    public Vector2 scale(final float scalar) {
        return new Vector2(this.x * scalar, this.y * scalar);
    }

    public Vector2 scale(final float x, final float y) {
        return new Vector2(this.x * x, this.y * y);
    }

    public Vector2 scale(final Vector2 other) {
        return scale(other.x, other.y);
    }

    public float dot(final Vector2 o) {
        return this.x * o.getX() + this.y * o.getY();
    }

    private float magCache = -1;

    public float mag() {
        if (magCache == -1) {
            magCache = (float) Math.sqrt(x * x + y * y);
        }
        return magCache;
    }

    public Vector2 unit() {
        if (x == 0 && y == 0)
            return Vector2.ZERO;
        return this.scale(1f / this.mag());
    }

    public static Vector2 min(final Vector2 a, final Vector2 b) {
        return new Vector2(Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()));
    }

    public static Vector2 max(final Vector2 a, final Vector2 b) {
        return new Vector2(Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY()));
    }

    public String toString() {
        return "[" + getX() + ", " + getY() + "]";
    }

    public float dist(final Vector2 o) {
        return Vector2.dist(this, o);
    }

    public static float dist(final Vector2 a, final Vector2 b) {
        return (float) Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
    }

    public Point toPoint() {
        return new Point((int) getX(), (int) getY());
    }

    /**
     * Rotate the vector by a number of radians.
     * 
     * @param angle
     *            the angle to rotate by.
     * @return the rotated vector.
     */
    public Vector2 rotate(final float angle) {
        return new Vector2((float) (x * Math.cos(angle) - y * Math.sin(angle)), (float) (x * Math.sin(angle) + y
                * Math.cos(angle)));
    }

    /**
     * Rotate the vector by a number of degrees.
     * 
     * @param angle
     *            the angle to rotate by.
     * @return the rotated vector.
     */
    public Vector2 rotateDeg(final float angle) {
        return rotate((float) Math.toRadians(angle));
    }

    /**
     * Get the unit direction from start to end.
     * 
     * @param start
     *            the start vector
     * @param end
     *            the destination vector
     * @return the unit vector pointing from start to end
     */
    public static Vector2 getDirectionTo(final Vector2 start, final Vector2 end) {
        return end.sub(start).unit();
    }

    /**
     * Build a unit vector out of the direction.
     * 
     * @param direction
     *            direction in radians.
     * @return
     */
    public static Vector2 buildVector(final float direction) {
        return new Vector2((float) Math.cos(direction), (float) Math.sin(direction)).unit();
    }

    /**
     * Get a unit vector angled from a to b.
     */
    public static Vector2 toward(final Vector2 a, final Vector2 b) {
        return b.sub(a).unit();
    }

    @Override
    public boolean equals(final Object o) {
        return o instanceof Vector2 && (x == ((Vector2) o).x && y == ((Vector2) o).y);
    }

    /**
     * Get the casted version of x.
     * 
     * @return
     */
    public int getWidth() {
        return (int) x;
    }

    /**
     * Get the casted version of y.
     * 
     * @return
     */
    public int getHeight() {
        return (int) y;
    }

    public Vector2(final Vec2 vec2) {
        x = vec2.x;
        y = vec2.y;
    }

    public Vec2 toVec() {
        return new Vec2(x, y);
    }
}
