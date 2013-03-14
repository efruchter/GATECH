package myai;

import static java.lang.Math.abs;
import static java.lang.Math.random;
import static myai.Tuples.T;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import myai.Tuples.Tuple2;
import myai.Tuples.Tuple3;
import rts.GameState;
import rts.units.Unit;

public class MapModule {

	private MapTile[][] map;
	public Dimension mapDim;
	public Point center;
	public static final int NAV_THRESH = 1000;

	/*
	 * UNITS + ELEMENTS
	 */
	LinkedHashMap<Point, Unit> pointRef_US = new LinkedHashMap<Point, Unit>();
	LinkedHashMap<Point, Unit> pointRef_THEM = new LinkedHashMap<Point, Unit>();

	public LinkedHashSet<Unit> resources = new LinkedHashSet<Unit>(), 
			stockpile = new LinkedHashSet<Unit>(),
			soldierOffices = new LinkedHashSet<Unit>();

	/**
	 * Update everything!
	 * 
	 * @param state
	 */
	public void update(GameState state, UnitGeneral dudes) {

		for (int i = 0; i < state.getMap().length; i++) {
			if ((state.getMap()[i] & GameState.MAP_WALL) != 0)
				map[i % mapDim.width][i / mapDim.width].isWall = true;
		}

		pointRef_US.clear();
		pointRef_THEM.clear();
		stockpile.clear();
		soldierOffices.clear();

		for (Unit u : state.getOtherUnits()) {
			pointRef_THEM.put(P(u), u);
		}

		for (Unit u : state.getMyUnits()) {
			pointRef_US.put(P(u), u);
			addUnit(u);
		}

		for (Unit u : state.getNeutralUnits()) {
			if (u.isResources()) {
				pointRef_US.put(P(u), u);
				addUnit(u);
			}
		}

		Iterator<Unit> ite = resources.iterator();
		while (ite.hasNext()) {
			Unit rec = ite.next();
			if ((state.getMap()[rec.getY() * mapDim.width + rec.getX()] & GameState.MAP_FOG) == 0) {
				if ((state.getMap()[rec.getY() * mapDim.width + rec.getX()] & GameState.MAP_NEUTRAL) == 0) {
					ite.remove();
				}
			}
		}
	}

	public void init(GameState state) {
		mapDim = new Dimension(state.getMapWidth(), state.getMapHeight());
		map = new MapTile[mapDim.width][mapDim.height];

		for (int x = 0; x < mapDim.width; x++) {
			for (int y = 0; y < mapDim.height; y++) {
				map[x][y] = new MapTile(P(x, y));
			}
		}

		// SET UP NEIGHBORS
		for (int x = 0; x < mapDim.width; x++) {
			for (int y = 0; y < mapDim.height; y++) {
				List<MapTile> tiles = new ArrayList<MapTile>();
				if (y != 0)
					tiles.add(map[x][y - 1]);
				if (y != mapDim.height - 1)
					tiles.add(map[x][y + 1]);
				if (x != mapDim.width - 1)
					tiles.add(map[x + 1][y]);
				if (x != 0)
					tiles.add(map[x - 1][y]);
				map[x][y].neighbors = tiles.toArray(new MapTile[0]);
			}
		}

		center = P(mapDim.width / 2, mapDim.height / 2);
	}

	public Tuple3<Boolean, Double, List<Point>> findPath(Point s, Point t, boolean all, boolean ignoreWalls) {
		return findPath(s, t, all, NAV_THRESH * 2 ,ignoreWalls);
	}

	int pAcc = 2;
	
	public Tuple3<Boolean, Double, List<Point>> findPath(Point s, Point t, boolean all, int maxCost, boolean ignoreWalls) {
		
		if (manhattan(s, t) > 10) {
			t = P((s.x * pAcc + t.x) / (pAcc + 1), (s.y * pAcc + t.y) / (pAcc + 1));
		}
		
		Set<Point> open = new HashSet<Point>(), closed = new HashSet<Point>();
		PathN[][] pathNodes = new PathN[mapDim.width][mapDim.height];

		open.add(s);
		pathNodes[s.x][s.y] = new PathN();

		boolean found = false;

		while (!open.isEmpty() && !found) {
			Point best = lowestCost(open, t, all, ignoreWalls);
			if (pathNodes[best.x][best.y].walkCost > maxCost) {
				t = best;
				found = true;
			}
			closed.add(best);
			open.remove(best);
			if (best.equals(t)) {
				found = true;
			} else {
				for (MapTile tile : MT(best).neighbors) {
					Point p = tile.pos;
					if (!open.contains(p) && !closed.contains(p)) {
						pathNodes[p.x][p.y] = new PathN(best, 1 + pathNodes[best.x][best.y].walkCost);
						open.add(p);
					} else if (open.contains(p)) {
						double potentialCost = 1 + pathNodes[best.x][best.y].walkCost;
						if (potentialCost < pathNodes[p.x][p.y].walkCost) {
							pathNodes[p.x][p.y].parent = best;
							pathNodes[p.x][p.y].walkCost = potentialCost;
						}
					}
				}
			}
		}

		if (!found) {
			return T(false, 0D, null);
		}

		List<Point> path = new LinkedList<Point>();
		Double cost = pathNodes[t.x][t.y].walkCost;

		Point next = t;
		while (next != null) {
			path.add(0, next);
			next = pathNodes[next.x][next.y].parent;
		}

		return T(true, cost, path);
	}

	private class PathN {
		Point parent;
		double walkCost = 0;

		public PathN() {
		}

		public PathN(Point parent, double walkCost) {
			this.parent = parent;
			this.walkCost = walkCost;
		}
	}

	private Point lowestCost(Set<Point> open, Point target, boolean all, boolean ignoreWalls) {
		Point bestPoint = null;
		double lowestCost = Double.MAX_VALUE;
		for (Point current : open) {
			double cost = cost(current, target, all, ignoreWalls);
			if (cost < lowestCost || (cost == lowestCost && random() < .5)) {
				bestPoint = current;
				lowestCost = cost;
			}
		}

		return bestPoint;
	}

	private double cost(Point s, Point t, boolean all, boolean ignoreWalls) {
		return map[s.x][s.y].getDynamicCost(all, ignoreWalls) + manhattan(s, t);
	}

	public Point indexToPoint(int i) {
		return P(i % mapDim.width, i / mapDim.width);
	}

	public static Point P(int... xs) {
		int x = 0, y = 0;
		if (xs.length == 1)
			x = y = xs[0];
		else if (xs.length >= 2) {
			x = xs[0];
			y = xs[1];
		}
		return new Point(x, y);
	}

	public static Point P(Unit a) {
		if (a == null) return P();
		return P(a.getX(), a.getY());
	}

	public static double manhattan(Point a, Point b) {
		return abs(a.x - b.x) + abs(a.y - b.y);
	}

	class MapTile {
		boolean isWall = false;
		double cost = 0;
		MapTile[] neighbors;
		final Point pos;

		public MapTile(Point pos) {
			this.pos = pos;
		}

		boolean occupied(boolean all) {
			return pointRef_US.containsKey(pos) || (all && pointRef_THEM.containsKey(pos));
		};

		double getDynamicCost(boolean all, boolean ignoreWalls) {
			return cost + (occupied(all) ? NAV_THRESH : 0) + (isWall && !ignoreWalls ? NAV_THRESH : 0);
		};

	}

	public MapTile MT(Point p) {
		return map[p.x][p.y];
	}

	public void addUnit(Unit u) {
		if (u.isStockpile()) {
			stockpile.add(u);
		} else if (u.isResources()) {
			resources.add(u);
		} else if (u.isBuilding()) {
			soldierOffices.add(u);
		}
	}

	public Tuple2<Boolean, Unit> findClosest(Unit unit, Collection<Unit> things) {
		double b = Double.MAX_VALUE;
		Unit closest = null;
		for (Unit thing : things) {
			// VALID TARGET FOUND
			double dist = manhattan(P(unit), P(thing));
			if (dist < b || (dist == b && .5 < random())) {
				b = dist;
				closest = thing;
			}
		}

		return T(closest != null, closest);
	}
}
