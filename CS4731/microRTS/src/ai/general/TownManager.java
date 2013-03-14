package ai.general;

import java.util.ArrayList;

/**
 * \brief A defense manager, mostly
 * @author Jeff Bernard
 *
 */
public class TownManager extends TaskManager {
	public ArrayList<GeneralAITown> towns; /**< all of the towns we've built */
	
	/**
	 * Constructs a new town manager
	 */
	public TownManager() {
		super();
		
		towns = new ArrayList<GeneralAITown>();
	}

	@Override
	public void update(GeneralAI ai) {
		// this is done inline in other managers, for speed reasons
	}

	@Override
	/**
	 * Requests units to come and protect this town!
	 * @param ai the parent ai
	 */
	public void manage_units(GeneralAI ai) {
		if ((ai.getLesion()&GeneralAI.LESION_NO_DEFENSE) == 0) {
			// check if any of the scouted units have become available
			for (int i = 0; i < units_scouted.size(); i++) {
				GeneralAIUnit unit = units_scouted.get(i);
				if (unit.strategy == GeneralAI.STRATEGY_NONE) {
					// free agent!!
					unit.strategy = GeneralAI.STRATEGY_DEFENSE;
					unit.wanted_strategy = GeneralAI.STRATEGY_NONE;
					unit.object = null;
					units.add(unit);
					units_scouted.remove(i--);
				} else if (unit.wanted_strategy == GeneralAI.STRATEGY_NONE) {
					unit.wanted_strategy = GeneralAI.STRATEGY_DEFENSE;
				}
			}
			
			int defenders = 0;
			for (int i = 0; i < towns.size(); i++) {
				if (towns.get(i).owner == ai.player_id) { // my town
					defenders += towns.get(i).defenders_wanted;
				}
			}
			
			// scout ALL units possible
			if (units.size()+units_scouted.size() < defenders) {
				for (int i = 0; i < ai.units.size(); i++) {
					GeneralAIUnit unit = ai.units.get(i);
					if (!unit.stats.isBuilding() && !unit.stats.isWorker()) { // workers are already in town
						if (unit.wanted_strategy != GeneralAI.STRATEGY_ATTACK) {
							if (unit.strategy == GeneralAI.STRATEGY_NONE) {
								units.add(unit);
								unit.strategy = GeneralAI.STRATEGY_DEFENSE;
								unit.object = null;
							} else if (unit.strategy == GeneralAI.STRATEGY_EXPLORE && unit.wanted_strategy == GeneralAI.STRATEGY_NONE) {
								unit.wanted_strategy = GeneralAI.STRATEGY_DEFENSE;
								units_scouted.add(unit);
							}
							if (units.size()+units_scouted.size() >= defenders) {
								break;
							}
						}
					}
				}
			}
			
			// deal with the units already in the army
			for (int i = 0; i < units.size(); i++) {
				GeneralAIUnit unit = units.get(i); // units aren't given up once in here
				if (unit.object == null) {
					// find closest town
					int distance = GeneralAI.DISTANCE_IGNORE;
					for (int j = 0; j < towns.size(); j++) {
						int d = towns.get(j).distance(unit, ai);
						if (d != GeneralAI.DISTANCE_IGNORE && (distance == GeneralAI.DISTANCE_IGNORE || d < distance)) {
							distance = d;
							unit.object = towns.get(j);
						}
					}
					if (unit.object == null) { // somehow there are no more towns... (?)
						// yield to any other behavior
						unit.strategy = GeneralAI.STRATEGY_NONE;
						units.remove(i--);
					} else {
						((GeneralAITown)unit.object).add_defender();
					}
				}
			}
		}
	}
}
