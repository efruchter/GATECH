package myai;

import static myai.MapModule.P;

import java.awt.Point;
import java.util.List;

import rts.GameState;
import rts.units.Unit;
import rts.units.UnitAction;
import rts.units.UnitDefinition;

public class UnitGeneral {

	private int lastUnitIndex = 0;

	public int numberOfWorkers = 0;

	WorkerManager workerManager = new WorkerManager();
	ArmyManager armyManager = new ArmyManager();
	BuildingManager buildingManager = new BuildingManager();

	int STOCKPILE_BUILD_ID = -1, SOLDIER_OFFICE_BUILD_ID = -1, AIRPORT_BUILD_ID = -1;

	MapModule map = null;

	public void init(GameState state, MapModule map) {
		workerManager.init(state);
		buildingManager.init(map, state, this);
		this.map = map;
		for (int i = 0; i < state.getBuildingList().size(); i++) {
			UnitDefinition u = state.getBuildingList().get(i);
			if (u.is_stockpile_building) {
				STOCKPILE_BUILD_ID = i;
			} else {
				boolean flying = false;
				for (int un : u.produces) {
					flying |= state.getUnitList().get(un).is_flying;
				}
				if (flying)
					AIRPORT_BUILD_ID = i;
				else
					SOLDIER_OFFICE_BUILD_ID = i;
			}
		}
	}

	public void update(GameState state, MapModule map, long delta) {

		long milliStart = System.currentTimeMillis();

		numberOfWorkers = 0;

		for (Unit unit : state.getMyUnits()) {
			if (unit.isWorker())
				numberOfWorkers++;
		}

		List<Unit> units = state.getMyUnits();

		workerManager.preUnitLogic(map, state);
		armyManager.preUnitLogic(map, state);

		while (System.currentTimeMillis() - milliStart < delta) {

			if (lastUnitIndex >= units.size()) {
				lastUnitIndex = 0;
			}

			Unit unit = units.get(lastUnitIndex);

			if (unit.hasAction())
				continue;

			// ACTION
			if (unit.isBuilding()) {
				buildingManager.commandUnit(unit, state, map, this);
			} else if (unit.isWorker()) {
				workerManager.commandUnit(unit, state, map, this);
			} else {
				armyManager.commandUnit(unit, state, map, this);
			}
			lastUnitIndex++;
		}
		lastUnitIndex++;
	}

	public UnitAction findAction(List<UnitAction> actions, Point target, int type, boolean attackIfAble) {
		if (attackIfAble) {
			for (UnitAction a : actions) {
				if ((a.getType() == UnitAction.ATTACK || a.getType() == UnitAction.ATTACK_KILL)
						&& !map.pointRef_US.containsKey(P(a.getTargetX(), a.getTargetY()))) {
					return a;
				}
			}
		}
		for (UnitAction a : actions) {
			if (a.getType() == type && a.getTargetX() == target.x && a.getTargetY() == target.y) {
				return a;
			}
		}
		return null;
	}

	public UnitAction findAction(List<UnitAction> actions, int type) {
		for (UnitAction a : actions) {
			if (a.getType() == type) {
				return a;
			}
		}
		return null;
	}
	
	public UnitAction findAttackAction(List<UnitAction> actions) {
		for (UnitAction a : actions) {
			if (a.getType() == UnitAction.ATTACK && !map.pointRef_US.containsKey(P(a.getTargetX(), a.getTargetY()))) {
				return a;
			}
		}
		return null;
	}

	public UnitAction action_buildStockpile(List<UnitAction> actions, Point target) {
		for (UnitAction a : actions) {
			if (a.getTargetX() == target.x && a.getTargetY() == target.y && a.getType() == UnitAction.BUILD
					&& a.getBuild() == STOCKPILE_BUILD_ID) {
				return a;
			}
		}
		return null;
	}

	public UnitAction action_buildSoldierOffice(List<UnitAction> actions, Point target) {
		for (UnitAction a : actions) {
			if (a.getTargetX() == target.x && a.getTargetY() == target.y && a.getType() == UnitAction.BUILD
					&& a.getBuild() == SOLDIER_OFFICE_BUILD_ID) {
				return a;
			}
		}
		return null;
	}
	
	public UnitAction action_buildAirport(List<UnitAction> actions, Point target) {
		for (UnitAction a : actions) {
			if (a.getTargetX() == target.x && a.getTargetY() == target.y && a.getType() == UnitAction.BUILD
					&& a.getBuild() == AIRPORT_BUILD_ID) {
				return a;
			}
		}
		return null;
	}

	public void findAndSetAction(Unit unit, Point target, int type, boolean attackIfAble) {
		UnitAction action = findAction(unit.getActions(), target, type, attackIfAble);
		if (action != null) {
			unit.setAction(action);
		}
	}
}
