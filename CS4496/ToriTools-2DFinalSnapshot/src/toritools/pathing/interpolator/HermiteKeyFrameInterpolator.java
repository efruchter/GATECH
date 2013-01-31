package toritools.pathing.interpolator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import toritools.math.Vector2;

/**
 * Interpolate a path based on keyframes.
 * 
 * @author toriscope
 * 
 */
public class HermiteKeyFrameInterpolator {

	private final List<HermiteKeyFrame> keyFrames;

	/**
	 * Spawn an interpolator, can be used as a pathing mechanism for smooth
	 * animations.
	 * 
	 * @param keyFrames
	 *            the list of keyframes. They will be copied and sorted into
	 *            proper time order.
	 * @throws RuntimeException
	 *             if less than two keyframes are provided, this will be thrown
	 *             with proper error message.
	 */
	public HermiteKeyFrameInterpolator(final List<HermiteKeyFrame> keyFrames) {
		if (keyFrames.size() < 2)
			throw new RuntimeException("Please include at least two keyframes!");
		this.keyFrames = new ArrayList<HermiteKeyFrame>(keyFrames);
		Collections.sort(this.keyFrames);
	}
	
	public HermiteKeyFrameInterpolator(final HermiteKeyFrame ... keyFrames) {
		this(Arrays.asList(keyFrames));
	}

	public float getStartTime() {
		return keyFrames.get(0).time;
	}

	public float getEndTime() {
		return keyFrames.get(keyFrames.size() - 1).time;
	}

	public void scaleTime(final float scalar) {
		for (HermiteKeyFrame keyframe : keyFrames) {
			keyframe.time *= scalar;
		}
	}

	/**
	 * Get the relative position for a given time.
	 * 
	 * @param time
	 *            the time to fetch the interpolated position for.
	 * @return the relative position at time.
	 */
	public Vector2 getPositionDeltaAtTime(final float time) {
		// No Keyframes
		if (keyFrames.isEmpty())
			return new Vector2();

		// Time below frames.
		if (time <= keyFrames.get(0).time)
			return keyFrames.get(0).pos;

		// Time above frames.
		if (time >= keyFrames.get(keyFrames.size() - 1).time)
			return keyFrames.get(keyFrames.size() - 1).pos;

		for (int i = 1; i < keyFrames.size(); i++) {
			if (time >= keyFrames.get(i - 1).time
					&& time <= keyFrames.get(i).time) {
				return getPositionInterpolated((time - keyFrames.get(i - 1).time)
						/ (keyFrames.get(i).time - keyFrames.get(i - 1).time),
						keyFrames.get(i - 1), keyFrames.get(i));
			}
		}

		throw new RuntimeException("A time " + time
				+ " could not be found in the keyframes.");

	}

	private Vector2 getPositionInterpolated(float s, final HermiteKeyFrame p1,
			final HermiteKeyFrame p2) {

		Vector2 r1 = p1.pos.scale(HermiteBasisEquation.h1(s));
		Vector2 r2 = p2.pos.scale(HermiteBasisEquation.h2(s));
		Vector2 r3 = p1.mag.scale(HermiteBasisEquation.h3(s));
		Vector2 r4 = p2.mag.scale(HermiteBasisEquation.h4(s));

		return r1.add(r2).add(r3).add(r4);
	}

	/**
	 * A simple keyframe with a position, direction at position, and a time.
	 * Comparable by time.
	 * 
	 * @author toriscope
	 * 
	 */
	public static class HermiteKeyFrame implements Comparable<HermiteKeyFrame> {
		public Vector2 pos;
		public Vector2 mag;
		public float time;

		/**
		 * A keyFrame for a hermite curve path.
		 * 
		 * @param pos
		 *            the position at the given time.
		 * @param mag
		 *            the direction and magnitude at that time, essentially the
		 *            point handle.
		 * @param time
		 *            the time.
		 */
		public HermiteKeyFrame(final Vector2 pos, final Vector2 mag,
				final float time) {
			this.pos = pos;
			this.mag = mag;
			this.time = time;
		}
		
		/**
		 * A keyFrame for a hermite curve path. No velocity handle.
		 * 
		 * @param pos
		 *            the position at the given time.
		 * @param time
		 *            the time.
		 */
		public HermiteKeyFrame(final Vector2 pos, final float time) {
			this(pos, new Vector2(), time);
		}
		
		public HermiteKeyFrame clone() {
			return new HermiteKeyFrame(pos, mag, time);
		}

		@Override
		public int compareTo(HermiteKeyFrame other) {
			if (this.time > other.time)
				return 1;
			else if (this.time < other.time)
				return -1;
			else
				return 0;
		}

	}

	/**
	 * The 2D hermite basis equations.
	 * 
	 * @author toriscope
	 * 
	 */
	private static class HermiteBasisEquation {

		/**
		 * h1(s) = 2s^3 - 3s^2 + 1
		 */
		public static float h1(final float s) {
			return (float) (2 * Math.pow(s, 3) - 3 * Math.pow(s, 2) + 1);
		}

		/**
		 * h2(s) = -2s^3 + 3s^2
		 */
		public static float h2(final float s) {
			return (float) (-2 * Math.pow(s, 3) + 3 * Math.pow(s, 2));
		}

		/**
		 * h3(s) = s^3 - 2s^2 + s
		 */
		public static float h3(final float s) {
			return (float) (Math.pow(s, 3) - 2 * Math.pow(s, 2) + s);
		}

		/**
		 * h4(s) = s^3 - s^2
		 */
		public static float h4(final float s) {
			return (float) (Math.pow(s, 3) - Math.pow(s, 2));
		}
	}
}
