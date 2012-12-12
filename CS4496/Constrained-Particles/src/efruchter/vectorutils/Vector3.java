package efruchter.vectorutils;

/**
 * An immutable vector class designed to make complex vector math easier to
 * code. Is rather wasteful, so don't use this in high-performance situations.
 * 
 * @author toriscope
 * 
 */
public class Vector3 {

    /**
     * A commonly used vector with zeros in each component. It is good form to
     * use this to get a zero vector instead of actually mallocing a new one.
     */
    public static final Vector3 ZERO = new Vector3(0, 0, 0);

    /**
     * The components of this 3D vector.
     */
    public final float x, y, z;

    public Vector3(final float x, final float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3 add(final Vector3 o) {
        return new Vector3(x + o.x, y + o.y, z + o.z);
    }

    public Vector3 add(final float s) {
        return new Vector3(x + s, y + s, z + s);
    }

    public Vector3 sub(final Vector3 o) {
        return new Vector3(x - o.x, y - o.y, z - o.z);
    }

    public Vector3 sub(final float s) {
        return new Vector3(x - s, y - s, z - s);
    }

    public Vector3 scale(final float scalar) {
        return new Vector3(x * scalar, y * scalar, z * scalar);
    }

    public float dot(final Vector3 o) {
        return x * o.x + y * o.y + z * o.z;
    }

    /**
     * Get the magnitude of the vector.
     * 
     * @return the magnitude.
     */
    public float mag() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    /**
     * Generate a unit version of this vector.
     * 
     * @return a unit version of this vector.
     */
    public Vector3 unit() {
        if (x == 0 && y == 0 && z == 0) {
            return Vector3.ZERO;
        } else {
            return scale(1f / mag());
        }
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + ", " + z + "]";
    }

    @Override
    public boolean equals(final Object o) {
        return o instanceof Vector3
                && (x == ((Vector3) o).x && y == ((Vector3) o).y && z == ((Vector3) o).z);
    }

    /**
     * Find the euclidean distance from this vector to another.
     * 
     * @param o
     *            the other vector
     * @return the distance
     */
    public float distance(Vector3 o) {
        return (float) Math.sqrt(Math.pow(x - o.x, 2) + Math.pow(y - o.y, 2)
                + Math.pow(z - o.z, 2));
    }
}
