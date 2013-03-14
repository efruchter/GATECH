package myai;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import rts.GameState;
import rts.units.Unit;
import rts.units.UnitAction;

public class BuildingManager {

	//List<Integer> unitsGround = new ArrayList<Integer>(), unitsAir = new ArrayList<Integer>();

	enum BTYPE {
		STOCKPILE, AIRPORT, SOLDIER_ORPHANAGE
	}

	LinkedHashMap<Long, BTYPE> myBuildings = new LinkedHashMap<Long, BTYPE>();

	public void init(MapModule map, final GameState state, UnitGeneral general) {
		/*for (int i = 0; i < state.getBuildingList().size(); i++) {
			UnitDefinition s = state.getBuildingList().get(i);
			if (i == general.SOLDIER_OFFICE_BUILD_ID) {
				for (Integer prod : s.produces) {
					unitsGround.add(prod);
					System.out.println(prod);
				}
			} else if (i == general.AIRPORT_BUILD_ID) {
				for (Integer prod : s.produces) {
					unitsAir.add(prod);
				}
			}
		}

		Comparator<Integer> sortByStrength = new Comparator<Integer>() {
			@Override
			public int compare(Integer a, Integer b) {
				Integer aa = state.getUnitList().get(a).attack_max;
				Integer bb = state.getUnitList().get(b).attack_max;
				return -1 * aa.compareTo(bb);
			}
		};

		Collections.sort(unitsGround, sortByStrength);
		Collections.sort(unitsAir, sortByStrength);*/

	}

	public void commandUnit(Unit unit, GameState state, MapModule map, UnitGeneral men) {
		if (!myBuildings.containsKey(unit.getID())) {
			BTYPE type = null;
			if (unit.isStockpile()) {
				type = BTYPE.STOCKPILE;
			} else if (unit.isBuilding()) {
				boolean flying = false;
				for (int un : unit.getProduce()) {
					flying |= state.getUnitList().get(un).is_flying;
				}
				if (flying)
					type = BTYPE.AIRPORT;
				else
					type = BTYPE.SOLDIER_ORPHANAGE;
			}
			if (type != null) {
				myBuildings.put(unit.getID(), type);
			}
		}

		switch (myBuildings.get(unit.getID())) {
			case AIRPORT:

			case SOLDIER_ORPHANAGE:
				if (Math.random() < .025) {
					buildSomethingIfAble(unit);
				}
				break;
			case STOCKPILE:
				if (men.numberOfWorkers < map.resources.size()) {
					buildSomethingIfAble(unit);
				}
				break;
			default:
				break;

		}
	}

	public boolean buildSomethingIfAble(Unit unit) {
		float prob = .25f/2f;
		for (UnitAction a : unit.getActions()) {
			if (a.getType() == UnitAction.BUILD) {
				if (Math.random() < prob) {
					unit.setAction(a);
					return true;
				}
				prob *= 2;
			}
		}
		return false;
	}

	private boolean canAfford(ArrayList<Integer> cost, GameState state) {
		for (int i = 0; i < state.getResourceTypes(); i++) {
			if (cost.get(i) + (i) > state.getResources(i)) {
				return false;
			}
		}
		return true;
	}
}
