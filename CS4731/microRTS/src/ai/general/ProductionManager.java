package ai.general;

import java.util.ArrayList;


/**
 * \brief Manages the various productions that are needed
 * @author Jeff Bernard
 *
 */
public class ProductionManager extends TaskManager {
	private static final int BUILD_TURN_WAIT = 100; /**< how many turns to wait before buildings can be made */
	private static final int PRIORITY_HIGH = 0; /**< a quite high priority */
	private static final int WORKER_PROB = 1; /**< out of HUNDRED_PERCENT; sometimes you want to build a worker */
	private static final int HUNDRED_PERCENT = 10000;
	
	public ArrayList<GeneralAIProduction> units_possible; /**< units we could build */
	public ArrayList<GeneralAIProduction> buildings_possible; /**< buildings we could build */
	
	private ArrayList<GeneralAIProduction> units_wanted; /**< units we want to build */
	public ArrayList<GeneralAIProduction> buildings_wanted; /**< buildings we want to build */
	
	public int workers_wanted; /**< how many workers we want to scout, but not don't exist */
	public int workers_queued; /**< how many workers are queued to be built */
	
	/**
	 * Constructs a new production manager
	 * @param ai the parent ai
	 */
	public ProductionManager(GeneralAI ai) {
		super();
		
		units_possible = new ArrayList<GeneralAIProduction>();
		buildings_possible = new ArrayList<GeneralAIProduction>();
		units_wanted = new ArrayList<GeneralAIProduction>();
		buildings_wanted = new ArrayList<GeneralAIProduction>();
		
		for (int i = 0; i < ai.state.getUnitList().size(); i++) {
			units_possible.add(new GeneralAIProduction(ai.state.getUnitList().get(i), i));
		}
		for (int i = 0; i < ai.state.getBuildingList().size(); i++) {
			buildings_possible.add(new GeneralAIProduction(ai.state.getBuildingList().get(i), i));
		}
		
		workers_wanted = 0;
		workers_queued = 0;
	}
	
	@Override
	/**
	 * Requests units to produce more units
	 * @param ai the parent ai
	 */
	public void manage_units(GeneralAI ai) {		
		// determine what we want to build
		for (int i = 0; i < buildings_possible.size(); i++) {
			GeneralAIProduction production = (GeneralAIProduction)buildings_possible.get(i);
			for (int j = 0; j < units_possible.size(); j++) {
				if (units_possible.get(j).priority != GeneralAI.DISTANCE_IGNORE && production.def.produces.contains(j)) {
					production.priority -= units_possible.get(j).priority;
				}
			}
			if (production.priority != GeneralAI.DISTANCE_IGNORE) {
				for (int j = 0; j < ai.town_manager.towns.size(); j++) {
					if (ai.town_manager.towns.get(j).owner == -1 || ai.town_manager.towns.get(j).owner == ai.player_id) {
						int wish_location = ai.town_manager.towns.get(j).get_location(production, ai);
						if (wish_location != GeneralAITown.NO_VACANCY) {
							buildings_wanted.add(new GeneralAIProduction(production, wish_location%ai.state.getMapWidth(), wish_location/ai.state.getMapWidth(), production.def.is_stockpile_building ? PRIORITY_HIGH : production.priority));
						}
					}
				}
			}
		}
		
		workers_wanted = Math.max(workers_wanted, ai.farm_manager.workers_wanted);
		workers_wanted -= workers_queued;
		if (ai.state.isFog()) {
			int xpct = (int)(Math.random()*HUNDRED_PERCENT);
			if (xpct < WORKER_PROB) {
				workers_wanted++;
			}
		}
		for (int i = 0; i < units_possible.size(); i++) {
			GeneralAIProduction production = (GeneralAIProduction)units_possible.get(i);
//			if (production.def.is_worker && !(ai.workers/2 < ai.farm_manager.owned_farms.size())) {
//				continue;
//			}
//			if (!production.def.is_worker) {
//				if (((ai.getLesion()&GeneralAI.LESION_ONLY_RANGE) != 0 && production.def.attack_range <= 1) ||
//					((ai.getLesion()&GeneralAI.LESION_NO_RANGE) != 0 && production.def.attack_range > 1) ||
//					((ai.getLesion()&GeneralAI.LESION_ONLY_FLYING) != 0 && !production.def.is_flying) ||
//					((ai.getLesion()&GeneralAI.LESION_NO_FLYING) != 0 && production.def.is_flying)) {
//					continue;
//				}
//			}
			if ((production.priority != GeneralAI.DISTANCE_IGNORE && !production.def.is_worker) || (production.def.is_worker && (workers_wanted > 0))) {
				// we can only build units near buildings
				for (int j = 0; j < units.size(); j++) {
					GeneralAIUnit building = units.get(j);
					if (building.stats.isBuilding() && building.stats.getProduce().contains(production.id)) {
						// maybe we just build units whereever?
						ArrayList<Integer> locations = building.adjacent_build_locations(production, ai);
						int location = locations.get((int)(Math.random()*locations.size()));
						units_wanted.add(new GeneralAIProduction(production, location%ai.state.getMapWidth(), location/ai.state.getMapHeight(), production.priority));
						if (production.def.is_worker) {
							workers_queued++;
							workers_wanted--;
							if (workers_wanted <= 0) {
								break;
							}
						}
					}
				}
			}
		}
		
		// assign units to to do these builds
		// check if any of the scouted units have become available
		for (int i = 0; i < units_scouted.size(); i++) {
			GeneralAIUnit unit = units_scouted.get(i);
			if (unit.strategy == GeneralAI.STRATEGY_NONE) {
				// free agent!!
				unit.strategy = GeneralAI.STRATEGY_BUILD;
				unit.wanted_strategy = GeneralAI.STRATEGY_NONE;
				for (int j = 0; j < buildings_wanted.size(); j++) {
					GeneralAIProduction building = buildings_wanted.get(j);
					if (building.builder == unit.stats.getID()) {
						for (int k = 0; k < building.def.cost.size(); k++) {
							ai.money.set(k, ai.money.get(k)-building.def.cost.get(k));
						}
						unit.object = building;
						buildings_wanted.remove(j);
						
						break;
					}
				}
				units.add(unit);
				units_scouted.remove(i--);
			} else if (unit.wanted_strategy == GeneralAI.STRATEGY_NONE) {
				unit.wanted_strategy = GeneralAI.STRATEGY_BUILD;
			}
		}
		
		// update what we're doing with each of the unit's we've got
		for (int i = 0; i < units.size(); i++) {
			GeneralAIUnit unit = units.get(i);
			
			if (unit.object == null) {
				if (!unit.stats.isBuilding()) {
					// drop the unit, we'll pick him up later if he's what we want for another task
					unit.strategy = GeneralAI.STRATEGY_NONE;
					units.remove(i--);
				} else {
					unit.strategy = GeneralAI.STRATEGY_BUILD;
					// check if the building can build anything
					int distance = GeneralAI.DISTANCE_IGNORE;
					int index = -1;
					for (int j = 0; j < units_wanted.size(); j++) {
						int d = units_wanted.get(j).distance(unit, ai);
						if (d != GeneralAI.DISTANCE_IGNORE && (distance == GeneralAI.DISTANCE_IGNORE || d < distance)) {
							unit.object = units_wanted.get(j);
							distance = d;	
							index = j;
							break;
						}
					}
					if (unit.object != null) {						
						for (int k = 0; k < units_wanted.get(index).def.cost.size(); k++) {
							ai.money.set(k, ai.money.get(k)-units_wanted.get(index).def.cost.get(k));
						}
						units_wanted.remove(index);
					}
				}
			}
		}
		
		// figure out how units we want to scout
		workers_wanted = 0;
		if (ai.current_turn > BUILD_TURN_WAIT) {
			for (int i = 0; i < buildings_wanted.size(); i++) {
				GeneralAIProduction building = buildings_wanted.get(i);
				if (building.builder == -1) {
					GeneralAIUnit closest_unit = null;
					int distance = GeneralAI.DISTANCE_IGNORE;
					for (int j = 0; j < ai.units.size(); j++) {
						GeneralAIUnit unit = ai.units.get(j);
						if (unit.stats.isWorker() && unit.wanted_strategy != GeneralAI.STRATEGY_BUILD && unit.strategy != GeneralAI.STRATEGY_BUILD) { // make sure not a unit already scouted
							int d = building.distance(unit, ai);
							if (d != GeneralAI.DISTANCE_IGNORE && (distance == GeneralAI.DISTANCE_IGNORE || d < distance)) {
								distance = d;
								closest_unit = unit;
							}
						}
					}
					if (closest_unit != null) {
						units_scouted.add(closest_unit);
						closest_unit.wanted_strategy = GeneralAI.STRATEGY_BUILD;
						building.builder = closest_unit.stats.getID();
					} else {
						workers_wanted++;
					}
				}
			}
		}
	}

	@Override
	/**
	 * Updates the data on possible productions
	 *@param ai the parent ai
	 */
	public void update(GeneralAI ai) {
		// in case a unit defintion ever changes (ie- an upgrade occurs)
		for (int i = 0; i < units_possible.size(); i++) {
			GeneralAIProduction production = (GeneralAIProduction)units_possible.get(i);
			production.def = ai.state.getUnitList().get(production.id);
			//production.priority = GeneralAI.DISTANCE_IGNORE;
		}
		for (int i = 0; i < buildings_possible.size(); i++) {
			GeneralAIProduction production = (GeneralAIProduction)buildings_possible.get(i);
			production.def = ai.state.getBuildingList().get(production.id);
			production.priority = GeneralAI.DISTANCE_IGNORE;
		}
	}
	
	/**
	 * Changes the location of what we want to build, if the other player is blocking
	 * @param unit the uni doing the building
	 * @param ai the parent ai
	 */
	public void change_build_location(GeneralAIUnit unit, GeneralAI ai) {
		GeneralAIProduction product = (GeneralAIProduction)unit.object;
		if (product.def.is_building) {
			// need to call the town planner
			for (int j = 0; j < ai.town_manager.towns.size(); j++) {
				if (ai.town_manager.towns.get(j).in_limits(product.x+product.y*ai.state.getMapWidth(), ai.state.getMapWidth())) {
					int wish_location = ai.town_manager.towns.get(j).get_location(product, ai);
					if (wish_location != GeneralAITown.NO_VACANCY) {
						unit.object = new GeneralAIProduction(product, wish_location%ai.state.getMapWidth(), wish_location/ai.state.getMapWidth(), product.priority);
						break;
					}
					unit.object = null;
					break;
				}
			}
		} else {
			// consider somewheres else adjacent
			ArrayList<Integer> locations = unit.adjacent_build_locations(product, ai);
			int location = locations.get((int)(Math.random()*locations.size()));
			unit.object = new GeneralAIProduction(product, location%ai.state.getMapWidth(), location/ai.state.getMapHeight(), product.priority);
		}
	}
}
