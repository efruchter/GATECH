package ai.general;

import java.util.ArrayList;

import rts.units.Unit;
import rts.units.UnitAction;

/**
 * \brief Manages the farming of resources
 * @author Jeff Bernard
 *
 */
public class FarmManager extends TaskManager {
	public ArrayList<GeneralAIFarm> farms; /**< resource fields we know about */
	public ArrayList<GeneralAIFarm> owned_farms; /**< a quick listing of all the farms that we "own" */
	
	public int workers_wanted; /**< how many workers we want, but don't exist */
	public ArrayList<Integer> neutral_farms;

	/**
	 * Constructs a new farm manager
	 */
	public FarmManager() {
		super();
		
		farms = new ArrayList<GeneralAIFarm>();
		owned_farms = new ArrayList<GeneralAIFarm>();
		workers_wanted = 0;
		neutral_farms = new ArrayList<Integer>();
	}
	
	@Override
	/**
	 * Requests units to start farming
	 * @param ai the parent ai
	 */
	public void manage_units(GeneralAI ai) {
		// check if any of the scouted units have become available
		for (int i = 0; i < units_scouted.size(); i++) {
			GeneralAIUnit unit = units_scouted.get(i);
			if (unit.strategy == GeneralAI.STRATEGY_NONE) {
				// free agent!!
				unit.strategy = GeneralAI.STRATEGY_FARM;
				unit.wanted_strategy = GeneralAI.STRATEGY_NONE;
				unit.object = null;
				for (int j = 0; j < owned_farms.size(); j++) {
					GeneralAIFarm farm = owned_farms.get(j);
					for (int k = 0; k < farm.farmers.length; k++) {
						if (farm.farmers[k] == unit.stats.getID()) {
							unit.object = farm;
							break;
						}
					}
					if (unit.object != null) {
						break;
					}
				}
				units.add(unit);
				units_scouted.remove(i--);
			} else if (unit.wanted_strategy == GeneralAI.STRATEGY_NONE) {
				unit.wanted_strategy = GeneralAI.STRATEGY_FARM;
			}
		}
		
		// figure out how units we want to scout
		workers_wanted = 0;
		for (int i = 0; i < owned_farms.size(); i++) {
			GeneralAIFarm farm = owned_farms.get(i);
			if (farm.has_openings_strict()) {
				boolean done_farm = false;
				for (int k = 0; k < farm.farmers.length; k++) {
					if (farm.farmers[k] == GeneralAIFarm.FARM_OPEN) {
						GeneralAIUnit closest_unit = null;
						int distance = GeneralAI.DISTANCE_IGNORE;
						for (int j = 0; j < ai.units.size(); j++) {
							GeneralAIUnit unit = ai.units.get(j);
							if (unit.stats.isWorker() && unit.wanted_strategy != GeneralAI.STRATEGY_FARM && unit.strategy != GeneralAI.STRATEGY_FARM) { // make sure not a unit already scouted
								int d = farm.distance(unit, ai);
								if (distance == GeneralAI.DISTANCE_IGNORE || (d != GeneralAI.DISTANCE_IGNORE && d < distance)) {
									distance = d;
									closest_unit = unit;
								}
							}
						}
						if (closest_unit != null) {
							if (closest_unit.strategy == GeneralAI.STRATEGY_NONE) { // we can recruit him now!
								closest_unit.strategy = GeneralAI.STRATEGY_FARM;
								closest_unit.object = farm;
								units.add(closest_unit);
							} else {
								units_scouted.add(closest_unit);
							}
							closest_unit.wanted_strategy = GeneralAI.STRATEGY_FARM;
							farm.farmers[k] = closest_unit.stats.getID();
						} else if (!done_farm && !neutral_farms.contains(i)) {
							workers_wanted++;
						}
						done_farm = true;
					}
				}
			}
		}
		
		// update what we're doing with each of the unit's we've got
		for (int i = 0; i < units.size(); i++) {
			GeneralAIUnit unit = units.get(i);
			if (ai.state.isFog() && unit.wanted_strategy == GeneralAI.STRATEGY_BUILD) {
				// can we give this unit up?
				// farm will only give up to build requests, when this unit has just performed return
				if (unit.last_action == UnitAction.RETURN) {
					unit.strategy = GeneralAI.STRATEGY_NONE;
					units.remove(i--);
					units_scouted.add(unit); // but we're going to want him back when he's done
					continue;
				}
			}
			
			if (unit.object == null) {
				// he needs a farm to farm at
				int distance = GeneralAI.DISTANCE_IGNORE;
				for (int j = 0; j < owned_farms.size(); j++) {
					int d = owned_farms.get(j).distance(unit, ai);
					if (d != GeneralAI.DISTANCE_IGNORE && (distance == GeneralAI.DISTANCE_IGNORE || d < distance)) {
						unit.object = owned_farms.get(j);
						distance = d;
					}
				}
				if (unit.object == null) {
					// there are no farms for this unit, so release his servitude
					unit.strategy = GeneralAI.STRATEGY_NONE;
					units.remove(i--);
				}
			}
		}
	}
	
	@Override
	/**
	 * Removes a unit from a farm (unit has likely died)
	 * @param id
	 */
	public void remove_unit(long id) {
		super.remove_unit(id);
		
		for (int i = 0; i < owned_farms.size(); i++) {
			GeneralAIFarm farm = owned_farms.get(i);
			for (int j = 0; j < farm.farmers.length; j++) {
				if (farm.farmers[j] == id) {
					farm.farmers[j] = farm._farmers[j];
					return;
				}
			}
		}
	}

	@Override
	/**
	 * Updates farm data
	 */
	public void update(GeneralAI ai) {	
		// consider resources found
		for (int i = 0; i < ai.state.getNeutralUnits().size(); i++) {
			Unit resource = ai.state.getNeutralUnits().get(i);
			if (resource.isResources()) {
				boolean alreadyHas = false;
				for (int j = 0; j < farms.size(); j++) {
					GeneralAIFarm farm = farms.get(j);
					if (resource.getID() == farm.resources.getID()) {
						farm.resources = resource;
						alreadyHas = true;
						break;
					}
				}
				if (!alreadyHas) {					
					GeneralAIFarm farm = new GeneralAIFarm(resource, ai);
					farms.add(farm);
					boolean added = false;
					for (int j = 0; j < ai.town_manager.towns.size(); j++) {
						if (ai.town_manager.towns.get(j).add(farm)) {
							added = true;
							break;
						}
					}
					if (!added) {
						ai.town_manager.towns.add(new GeneralAITown(farm, ai.production_manager.buildings_possible.size()));
					}
				} else {
					//
					
				}
			}
		}
		// remove any empty resources
		for (int i = 0; i < farms.size(); i++) {
			GeneralAIFarm farm = farms.get(i);
			if (farm.resources.getResources() <= 0) {
				for (int j = 0; j < farm.farmers.length; j++) {
					if (farm.farmers[j] >= 0) {
						for (int k = 0; k < units.size(); k++) {
							if (units.get(k).stats.getID() == farm.farmers[j] && units.get(k).object != null) {
								units.get(k).object.remove(units.get(k), ai);
								if (units.get(k).object == null) {
									units.get(k).strategy = GeneralAI.STRATEGY_NONE;
									units.remove(k);
								}
								break;
							}
						}
					}
				}
				for (int j = 0; j < ai.town_manager.towns.size(); j++) {
					if (ai.town_manager.towns.get(j).remove(farm)) {
						if (ai.town_manager.towns.get(j).population() == 0) {
							ai.town_manager.towns.remove(j);
						}
						break;
					}
				}
				farms.remove(i--);
			}
		}
		
		owned_farms.clear();
		neutral_farms.clear();
		for (int i = 0; i < ai.town_manager.towns.size(); i++) {
			if ((ai.town_manager.towns.get(i).owner == ai.player_id && ai.town_manager.towns.get(i).has_stockpile()) || ai.town_manager.towns.get(i).owner == -1) {
				for (int j = 0; j < ai.town_manager.towns.get(i).farms.size(); j++) {
					if (ai.town_manager.towns.get(i).owner == -1) {
						neutral_farms.add(owned_farms.size());
					}
					owned_farms.add(ai.town_manager.towns.get(i).farms.get(j));
					owned_farms.get(owned_farms.size()-1).update_openings(ai);
				}
			}
		}
	}
}
