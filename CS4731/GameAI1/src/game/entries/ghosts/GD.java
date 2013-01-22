package game.entries.ghosts;

import game.core.Game;

import java.awt.Point;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Grid class with utilities for path-finding and sorting cells.
 * @author toriscope
 *
 */
public class GD {

	final private Map<Point, Integer> map;
	private Game game;

	public final static int CELL_PIXEL_SEPERATION = 4;
	public static final Direction[] CARDINAL = new Direction[]{Direction.DOWN, Direction.UP, Direction.LEFT, Direction.RIGHT};

	public GD() {
		map = new HashMap<Point, Integer>();
		game = null;
	}

	/**
	 * Call this each step.
	 * @param game
	 */
	public void rebuildMap(final Game game) {
		map.clear();
		this.game = game;

		for (int i = 0; i < 1291; i++) {
			Point p = new Point(game.getX(i), game.getY(i));
			map.put(p, i);
		}
	}

	public boolean isValid(final Point p) {
		return map.containsKey(p);
	}

	public int getIndex(final Point p) {
		if (isValid(p)) {
			return map.get(p);
		} else {
			return -1;
		}
	}

	public List<Direction> getAllowedCellsSurrounding(final Point origin) {
		final List<Direction> newDirs = new LinkedList<Direction>();
		for (Direction d : CARDINAL) {
			if (isValid(add(origin, d.offset))) {
				newDirs.add(d);
			}
		}
		Collections.shuffle(newDirs); // Shuffle to ensure random tie-breaking
		return newDirs;
	}

	public static Comparator<Direction> buildTargetComparator(
			final Point origin, final Point target) {
		return new Comparator<Direction>() {
			public int compare(Direction o1, Direction o2) {
				Float a = distance(add(origin, o1.offset), target);
				Float b = distance(add(origin, o2.offset), target);
				return a.compareTo(b);
			}
		};
	}

	public static enum Direction {
		NONE(-1, new Point(0, 0)),
		UP(Game.UP, new Point(0, -CELL_PIXEL_SEPERATION)),
		RIGHT(Game.RIGHT, new Point(CELL_PIXEL_SEPERATION, 0)),
		DOWN(Game.DOWN, new Point(0,CELL_PIXEL_SEPERATION)),
		LEFT(Game.LEFT, new Point(-CELL_PIXEL_SEPERATION, 0));
		
		final public int num;
		final public Point offset;

		private Direction(final int num, final Point p) {
			this.num = num;
			this.offset = p;
		}

		public static Direction getDIR(final int d) {
			for (Direction s : Direction.values()) {
				if (d == s.num)
					return s;
			}
			return Direction.NONE;
		}

		public static Direction getOpDIR(int dir) {
			switch(Direction.getDIR(dir)) {
			case UP:
				return DOWN;
			case DOWN:
				return UP;
			case LEFT:
				return RIGHT;
			case RIGHT:
				return LEFT;
			default:
				break;
			}
			return null;
		}
	}

	public static float distance(final Point a, final Point b) {
		return (float) Math.sqrt(Math.pow(a.x - b.x, 2)
				+ Math.pow(a.y - b.y, 2));
	}

	public static Point add(final Point a, final Point b) {
		return new Point(a.x + b.x, a.y + b.y);
	}
	
	public static Point sub(final Point a, final Point b) {
		return add(a, scale(b, -1));
	}
	
	public static Point scale(final Point a, final int scalar) {
		return new Point(a.x * scalar, a.y * scalar);
	}

	public Point toPoint(int loc) {
		return new Point(game.getX(loc), game.getY(loc));
	}
}
