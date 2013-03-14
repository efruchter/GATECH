package ai.general;

import java.util.ArrayList;

import rts.GameState;

/**
 * \brief Manages what areas of the map should be explored
 * @author Jeff Bernard
 *
 */
public class ExplorationManager extends TaskManager {
	public ArrayList<GeneralAIExploration> exploration; /**< exploration spaces */
	public int[] map; /**< the map, as we know it */
	
	/**
	 * Constructs a new exploration manager
	 * @param ai the parent ai
	 */
	public ExplorationManager(GeneralAI ai) {
		super();
		
		map = new int[ai.state.getMap().length];
		exploration = new ArrayList<GeneralAIExploration>();
		for (int i = 0; i < ai.state.getMap().length; i++) {
			exploration.add(new GeneralAIExploration(i));
			map[i] = GameState.MAP_FOG;
		}
	}

	@Override
	/**
	 * Requests units to go out an explore
	 * @param ai the parent ai
	 */
	public void manage_units(GeneralAI ai) {
		if (!ai.state.isFog()) {
			return; // this module is of no use when there is no fog
		}
		
		// update what we're doing with each of the unit's we've got
		for (int i = 0; i < units.size(); i++) {
			GeneralAIUnit unit = units.get(i);
			if (unit.wanted_strategy != GeneralAI.STRATEGY_NONE) {
				// yield all requests, no matter the situtation
				unit.strategy = GeneralAI.STRATEGY_NONE;
				if (unit.object != null) {
					unit.object.remove(unit, ai);
				}
				units.remove(i--);
			}
			
			if (unit.object == null) {
				// he needs a farm to farm at
				int distance = GeneralAI.DISTANCE_IGNORE;
				for (int j = 0; j < exploration.size(); j++) {
					int d = exploration.get(j).distance(unit, ai);
					if (d != GeneralAI.DISTANCE_IGNORE && (distance == GeneralAI.DISTANCE_IGNORE || d < distance)) {
						unit.object = exploration.get(j);
						distance = d;
					}
				}
				if (unit.object == null) { // pick one anyway
					System.out.println("picked a random one");
					unit.object = exploration.get((int)(Math.random()*exploration.size()));
				}
			} else {
				// ensure that the current target is truly the closest
				int distance = GeneralAI.DISTANCE_IGNORE;
				boolean different = false;
				for (int j = 0; j < exploration.size(); j++) {
					int d = exploration.get(j).distance(unit, ai);
					if (d != GeneralAI.DISTANCE_IGNORE && (distance == GeneralAI.DISTANCE_IGNORE || d < distance)) {
						distance = d;
						if (!different && exploration.get(j).location != ((GeneralAIExploration)unit.object).location) {
							unit.object.remove(unit, ai);
							different = true;
						}
						unit.object = exploration.get(j);
					}
				}
			}
		}
		
		// take in all units with nothing better to do
		for (int i = 0; i < ai.units.size(); i++) {
			GeneralAIUnit unit = ai.units.get(i);
			if (!unit.stats.isBuilding() && unit.strategy == GeneralAI.STRATEGY_NONE && unit.wanted_strategy == GeneralAI.STRATEGY_NONE) {
				unit.strategy = GeneralAI.STRATEGY_EXPLORE;
				units.add(unit);
			}
		}
	}

	@Override
	/**
	 * Updates the data on exploration
	 * @param ai the parent ai
	 */
	public void update(GeneralAI ai) {
		// consider fow
		for (int i = 0; i < ai.state.getMap().length; i++) {
			if ((ai.state.getMap()[i]&GameState.MAP_FOG) == 0) {
				// not fog, so we don't need to rely on memory
				exploration.get(i).last_seen = ai.current_turn;
				
				map[i] = ai.state.getMap()[i];
			}
		}
	}
}
