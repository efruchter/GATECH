package ai.general;

import java.util.ArrayList;

import rts.units.Unit;

/**
 * \brief Manages engaging the enemy
 * @author Jeff Bernard
 *
 */
public class AttackManager extends TaskManager {
	public ArrayList<GeneralAIEnemy> enemies; /**< enemy units */
	
	public float enemy_evaluation; /**< evaluation (or prediction) of the enemy's might */
	public float army_evaluation; /**< evaluation of my (attacking units only) army */
	
	private int cheapest_unit; /**< cheapest unit */
	private int expensive_unit; /**< most expensive unit */
	private int strongest_unit; /**< cheapest unit */
	private int weakest_unit; /**< most expensive unit */
	
	/**
	 * Constructs a new attack manager
	 * @param units list of all possible units defintions
	 */
	public AttackManager(ArrayList<GeneralAIProduction> units) {
		super();
		
		enemies = new ArrayList<GeneralAIEnemy>();
		enemy_evaluation = 0;
		army_evaluation = 0;
		
		int minCost = -1;
		int maxCost = -1;
		float minAtk = -1;
		float maxAtk = -1;
		for (int i = 0; i < units.size(); i++) {
			if (!units.get(i).def.is_worker) { 
				int cost = 0;
				for (int j = 0; j < units.get(i).def.cost.size(); j++) {
					cost += units.get(i).def.cost.get(j);
				}
				float atk = (float)(units.get(i).def.attack_max+units.get(i).def.attack_min)/(float)units.get(i).def.attack_speed;
				if (i == 0 || cost < minCost) {
					cheapest_unit = i;
					minCost = cost;
				}
				if (i == 0 || cost > maxCost) {
					expensive_unit = i;
					maxCost = cost;
				}
				if (i == 0 || atk < minAtk) {
					weakest_unit = i;
					minAtk = atk;
				}
				if (i == 0 || atk > maxAtk) {
					strongest_unit = i;
					maxAtk = atk;
				}
			}
		}
	}
	
	@Override
	/**
	 * Requests units to start fighting foes
	 * @param ai the parent ai
	 */
	public void manage_units(GeneralAI ai) {
		// check if any of the scouted units have become available
		for (int i = 0; i < units_scouted.size(); i++) {
			GeneralAIUnit unit = units_scouted.get(i);
			if (unit.strategy == GeneralAI.STRATEGY_NONE) {
				// free agent!!
				unit.strategy = GeneralAI.STRATEGY_ATTACK;
				unit.wanted_strategy = GeneralAI.STRATEGY_NONE;
				unit.object = null;
				units.add(unit);
				units_scouted.remove(i--);
			} else if (unit.wanted_strategy == GeneralAI.STRATEGY_NONE) {
				unit.wanted_strategy = GeneralAI.STRATEGY_ATTACK;
			}
		}
		
		// build an ideal army
		if (army_evaluation < enemy_evaluation) {
			float difference = enemy_evaluation-army_evaluation;
			for (int i = 0; i < ai.production_manager.units_possible.size(); i++) {
				GeneralAIProduction production = ai.production_manager.units_possible.get(i);
				production.priority = GeneralAI.DISTANCE_IGNORE;
				
				if (!production.def.is_worker) {
					if (((ai.getLesion()&GeneralAI.LESION_ONLY_RANGE) != 0 && production.def.attack_range <= 1) ||
						((ai.getLesion()&GeneralAI.LESION_NO_RANGE) != 0 && production.def.attack_range > 1) ||
						((ai.getLesion()&GeneralAI.LESION_ONLY_FLYING) != 0 && !production.def.is_flying) ||
						((ai.getLesion()&GeneralAI.LESION_NO_FLYING) != 0 && production.def.is_flying) ||
						((ai.getLesion()&GeneralAI.LESION_CHEAPEST_ARMY) != 0 && i != cheapest_unit) || 
						((ai.getLesion()&GeneralAI.LESION_EXPENSIVE_ARMY) != 0 && i != expensive_unit) ||
						((ai.getLesion()&GeneralAI.LESION_WEAKEST_ARMY) != 0 && i != weakest_unit) || 
						((ai.getLesion()&GeneralAI.LESION_STRONGEST_ARMY) != 0 && i != strongest_unit)) {
						continue;
					}
				}
				
				for (int j = 0; j < production.def.cost.size(); j++) {
					if (production.def.cost.get(j) > ai.money.get(j)) {
						production.priority = GeneralAI.DISTANCE_IGNORE;
						break;
					} else {
						production.priority += ai.money.get(j)-production.def.cost.get(j);
					}
				}
				if (production.priority == GeneralAI.DISTANCE_IGNORE) {
					continue;
				}
				production.priority = (int)(difference/production.evaluate()*(float)production.priority/production.cost_ratio);
			}
		}
		
		// scout ALL units possible
		for (int i = 0; i < ai.units.size(); i++) {
			GeneralAIUnit unit = ai.units.get(i);
			if (!unit.stats.isBuilding() && (!unit.stats.isWorker() || !ai.state.isFog() || (ai.getLesion()&GeneralAI.LESION_WORKER_ARMY) != 0)) {
				if (unit.wanted_strategy != GeneralAI.STRATEGY_BUILD && unit.wanted_strategy != GeneralAI.STRATEGY_FARM) {
					if (unit.strategy == GeneralAI.STRATEGY_NONE) {
						units.add(unit);
						unit.strategy = GeneralAI.STRATEGY_ATTACK;
						unit.object = null;
					} else if (unit.strategy == GeneralAI.STRATEGY_EXPLORE && unit.wanted_strategy == GeneralAI.STRATEGY_NONE) {
						// pull in the near by searchers?
						unit.wanted_strategy = GeneralAI.STRATEGY_ATTACK;
						units_scouted.add(unit);
					}
				}
			}
		}
		
		// deal with the units already in the army
		for (int i = 0; i < units.size(); i++) {
			GeneralAIUnit unit = units.get(i);
			if (unit.wanted_strategy != GeneralAI.STRATEGY_NONE) {
				// yield to all requests
				if (unit.object != null) {
					unit.object.remove(unit, ai);
					unit.strategy = GeneralAI.STRATEGY_NONE;
					units.remove(i--);
				}
			} else if (enemies.size() == 0) { // TODO switch to exploration manager(?), which is basically sub of this...
				unit.strategy = GeneralAI.STRATEGY_NONE;
				units.remove(i--);
			} else if (unit.object == null) {
				// needs something to kill
				int distance = GeneralAI.DISTANCE_IGNORE;
				for (int j = 0; j < enemies.size(); j++) {
					int d = enemies.get(j).distance(unit, ai);
					if (d != GeneralAI.DISTANCE_IGNORE && (distance == GeneralAI.DISTANCE_IGNORE || d < distance)) {
						distance = d;
						unit.object = enemies.get(j);
					}
				}
				if (unit.object == null) {
					// yield to any other behavior
					unit.strategy = GeneralAI.STRATEGY_NONE;
					units.remove(i--);
				}
			} else {
				// ensure that the current target is truly the closest
				int distance = unit.object.distance(unit, ai);
				boolean different = false;
				if (distance == GeneralAI.DISTANCE_IGNORE) {
					unit.object.remove(unit, ai);
					different = true;
				}
				for (int j = 0; j < enemies.size(); j++) {
					int d = enemies.get(j).distance(unit, ai);
					if (d != GeneralAI.DISTANCE_IGNORE && (distance == GeneralAI.DISTANCE_IGNORE || d < distance)) {
						distance = d;
						if (!different && enemies.get(j).stats.getID() != ((GeneralAIEnemy)unit.object).stats.getID()) {
							unit.object.remove(unit, ai);
							different = true;
						}
						unit.object = enemies.get(j);
					}
				}
			}
		}
	}

	@Override
	/**
	 * Updates foe knowledge
	 */
	public void update(GeneralAI ai) {		
		for (int i = 0; i < ai.state.getOtherUnits().size(); i++) {
			Unit unit = ai.state.getOtherUnits().get(i);
			boolean found = false;
			for (int j = 0; j < enemies.size(); j++) { // check if we already know about this enemy
				GeneralAIEnemy enemy = enemies.get(j);
				if (enemy.stats.getID() == unit.getID()) {
					enemy.stats = unit;
					if (enemy.stats.getHP() <= 0) {
						enemy.dead = true;
						if (enemy.stats.isBuilding()) {
							for (int k = 0; k < ai.town_manager.towns.size(); k++) {
								if (ai.town_manager.towns.get(k).remove(enemy)) {
									break;
								}
							}
						}
						enemies.remove(j--);
					}
					enemy.seen = true;
					found = true;
					break;
				}
			}
			if (!found && unit.getHP() > 0) {
				GeneralAIEnemy enemy = new GeneralAIEnemy(unit);
				enemies.add(enemy);
				if (enemy.stats.isBuilding()) {
					boolean added = false;
					for (int j = 0; j < ai.town_manager.towns.size(); j++) {
						if (ai.town_manager.towns.get(j).add(enemy)) {
							added = true;
							break;
						}
					}
					if (!added) {
						ai.town_manager.towns.add(new GeneralAITown(enemy, ai.production_manager.buildings_possible.size()));
					}
				}
			}
		}
		
		enemy_evaluation = 1;
		for (int i = 0; i < enemies.size(); i++) {
			GeneralAIEnemy enemy = enemies.get(i);
			enemy_evaluation += enemy.evaluate();
		}
		
		army_evaluation = 1;
		for (int i = 0; i < units.size(); i++) {
			GeneralAIUnit unit = units.get(i);
			army_evaluation += unit.evaluate();
		}
		
		if (ai.state.isFog()) {
			// if there's fog, we need to do an estimate about the enemy army's actual might
			int enemy_count = 1;
			if (enemies.size() != 0) {
				enemy_count = enemies.size();
			}
			enemy_evaluation = (int)((float)ai.units.size()/(float)enemy_count*enemy_evaluation+(float)enemy_count/(float)ai.units.size()*army_evaluation);
		}
	}
	
	/**
	 * Removes this unit from the the manager
	 * @param unit the unit to remove 
	 */
	public void remove_unit(GeneralAIUnit unit) {
		super.remove_unit(unit.stats.getID());
	}
}
