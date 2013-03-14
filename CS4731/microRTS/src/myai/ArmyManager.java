package myai;

import static myai.MapModule.P;
import static myai.MapModule.manhattan;

import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import myai.Tuples.Tuple3;
import rts.GameState;
import rts.units.Unit;
import rts.units.UnitAction;

public class ArmyManager {

	private LinkedHashMap<Long, TaskedSoldier> mySoldiers = new LinkedHashMap<Long, TaskedSoldier>();

	public static Random random = new Random();
	
	public void preUnitLogic(MapModule map, GameState state) {

		// Prune dead units
		List<Long> aliveSoldiers = new LinkedList<Long>();
		for (Unit unit : state.getMyUnits()) {
			if (!unit.isBuilding() && !unit.isWorker() && !unit.isResources()) {
				aliveSoldiers.add(unit.getID());
			}
		}
		Iterator<Entry<Long, TaskedSoldier>> ite = mySoldiers.entrySet().iterator();
		while (ite.hasNext()) {
			Entry<Long, TaskedSoldier> s = ite.next();
			if (!aliveSoldiers.contains(s.getValue().unit.getID())) {
				ite.remove();
			}
		}
	}

	public void commandUnit(Unit unit, GameState state, MapModule map, UnitGeneral men) {

		// ASSIGN DEFAULT TASK
		if (!mySoldiers.containsKey(unit.getID())) {
			mySoldiers.put(unit.getID(), new TaskedSoldier(unit, SoldierTaskType.SCOUT));
		}

		// FETCH CURRENT TASK
		TaskedSoldier UNIT = mySoldiers.get(unit.getID());

		switch (UNIT.task) {
			case SCOUT: {
				if (!map.pointRef_THEM.isEmpty()) {
					UNIT.clearScoutGoals();
					setTask(unit, SoldierTaskType.ATTACK_NEAREST);
				} else {
					if (UNIT.goalPos == null) {
						UNIT.goalPos = P(random.nextInt(map.mapDim.width), random.nextInt(map.mapDim.height));
					}

					Tuple3<Boolean, Double, List<Point>> g = map.findPath(P(unit), UNIT.goalPos, true, unit.isFlying());
					if (g.a && g.c.size() > 2) {
						// MOVE
						men.findAndSetAction(unit, g.c.get(1), UnitAction.MOVE, true);
					} else {
						UNIT.clearScoutGoals();
					}
				}
				break;
			}
			case ATTACK_NEAREST :{
				if (map.pointRef_THEM.isEmpty()) {
					setTask(unit, SoldierTaskType.SCOUT);
				} else {
					Point closest = null; double dist = Double.MAX_VALUE;
					Point u = P(unit);
					for (Entry<Point, Unit> p : map.pointRef_THEM.entrySet()) {
						double mDist = manhattan(p.getKey(), u);
						if (mDist < dist) {
							dist = mDist;
							closest = p.getKey();
						}
					}
					UNIT.goalPos = closest;
					// ATTACK!
					if (closest != null) {
						Tuple3<Boolean, Double, List<Point>> target = map.findPath(P(unit), UNIT.goalPos, false, unit.isFlying());
						if (target.c.size() > 1) {
							men.findAndSetAction(unit, target.c.get(1), UnitAction.MOVE, true);
						}
					}
				}
				break;
			}
			default:
				System.err.println("Unit " + unit + " is in a bad state!");
				break;
		}
	}

	enum SoldierTaskType {
		SCOUT, ATTACK_NEAREST;
	}

	private void setTask(Unit unit, SoldierTaskType type) {
		mySoldiers.get(unit.getID()).task = type;
	}

	public class TaskedSoldier {
		SoldierTaskType task = SoldierTaskType.SCOUT;
		Point goalPos;
		final Unit unit;

		public TaskedSoldier(Unit unit, SoldierTaskType task) {
			this.unit = unit;
			this.task = task;
		}

		public void clearScoutGoals() {
			goalPos = null;
		}
	}
}
