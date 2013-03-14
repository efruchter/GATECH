package ai.general;

import java.util.ArrayList;

import rts.GameState;
import rts.units.Unit;
import rts.units.UnitAction;
import rts.units.UnitDefinition;

/**
 * \brief  An AI unit used by the GeneralAI. Allows for more sophisticated behaviors, like order queuing.
 * @author Jeff Bernard
 *
 */
public class GeneralAIUnit {
	public Unit stats; /**< the unit struct */
	public boolean exists; /**< whether or not this unit exists */
	public ArrayList<UnitAction> actions; /**< pending actions */
	public int last_action; /**< what the last action was */
	private ArrayList<Traffic> traffic; /**< the traffic for this unit */
	private Traffic building_traffic; /**< building traffic */
	
	//private UnitAction last_action; /**< the last action that was executed */
	private Traffic last_traffic; /**< the last traffic reservation */
	
	public int strategy; /**< the strategy this unit is adhering to */
	
	private boolean hasActed; /**< whether or not this unit has executed at least 1 action */
	
	public GeneralAIObject object; /**< the object this unit is interacting with */
	public boolean remove_object; /**< whether or not this unit should try and remove it's object */
	
	public int resources_held; /**< how many resources this guy previously held */
	
	public int wanted_strategy; /**< the strategy of the manager trying to recruit this unit */
	
	public ArrayList<Long> dont_attack; /**< list of enemies that we cannot reach */
	
	/**
	 * Constructs a new unit
	 * @param u the unit
	 * @param ai the ai this unit belongs to
	 */
	public GeneralAIUnit(Unit u, GeneralAI ai) {
		stats = u;
		exists = true;
		remove_object = false;
		actions = new ArrayList<UnitAction>();
		traffic = new ArrayList<Traffic>();
		
		if (stats.isBuilding()) {
			building_traffic = new Traffic(stats.getX()+stats.getY()*ai.state.getMapWidth(), ai.current_turn, -1);
			ai.traffic_map.reserve(building_traffic);
		} else {
			building_traffic = null;
		}
		
		strategy = GeneralAI.STRATEGY_NONE;
		hasActed = false;
		object = null;
		last_traffic = null;
		
		resources_held = 0;
		
		wanted_strategy = GeneralAI.STRATEGY_NONE;
		last_action = UnitAction.NONE;
		
		dont_attack = new ArrayList<Long>();
	}
	
	/**
	 * The unit should do something
	 * @param ai the general ai that is controlling this unit
	 */
	public void act(GeneralAI ai) {		
		if (stats.getResources() != 0) {
			resources_held = stats.getResources();
		}
		if (!stats.hasAction()) {			
			if (hasActed && actions.size() != 0) {
				if (stats.lastActionSucceeded()) {
					// last action was a success, remove from queue
					if (object != null) {
						object.action_succeeded(this, ai, actions.get(0).getType());
					}
					if (actions.size() != 0) {
						actions.remove(0);
					}
					if (traffic.size() != 0) {
						last_traffic = traffic.get(0);
						traffic.remove(0);
					}
				}
			}
			if (remove_object) {
				if (object != null) {
					object.remove(this, ai);
					if (!remove_object) {
						object = null;
					}
				}
			}
			if (object != null) {
				// update orders because the world is dynamic
				object.update_orders(this, ai);
			}
			
			nextAction(ai);
		}
	}
	
	/**
	 * Determines if this GeneralAIUnit equals a Unit
	 * @param u the unit to compare with
	 */
	public boolean equals(Unit u) {
		return(u.getID() == stats.getID());
	}
	
	/**
	 * Has pending Actions
	 * @return
	 */
	public boolean hasPendingActions() {
		return(actions.size() != 0);
	}
	
	/**
	 * Performs the next action
	 * @param ai the ai
	 * @return true if the next action was performed, otherwise false (no actions?)
	 */
	public void nextAction(GeneralAI ai) {
		if (actions.size() != 0) {
			hasActed = false;
			UnitAction action = actions.get(0); // ensure that action can be performed

			for (int i = 0; i < stats.getActions().size(); i++) {
				if (action.equals(stats.getActions().get(i))) {
					hasActed = true;
					last_action = action.getType();
					stats.setAction(stats.getActions().get(i));
					return;
				}
			}
			
			clearActions(ai.traffic_map);
			
			for (int i = 0; i < stats.getActions().size(); i++) {
				if (stats.getActions().get(i).getType() == UnitAction.MOVE) {
					hasActed = true;
					last_action = UnitAction.MOVE;
					stats.setAction(stats.getActions().get(i));
					return;
				}
			}
			
			if (action.getType() == UnitAction.BUILD) {
				// we need to pick a new spot to build, if the obstruction is caused by a different player
				ai.production_manager.change_build_location(this, ai);
			}
		}
	}
	
	/**
	 * Adds an action to this unit
	 * @param action the action to add
	 * @param traffic_map the traffic map this unit is in
	 * @param location the traffic location this action corresponds with
	 * @param start the traffic start
	 * @param end the traffic end
	 */
	public void addAction(UnitAction action, TrafficMap traffic_map, int location, int start, int end) {
		actions.add(action);
		if (traffic_map != null) {
			Traffic t = new Traffic(location, start, end);
			traffic_map.reserve(t);
			traffic.add(t);
		}
	}
	
	/**
	 * Clears all actions for this unit
	 *@param traffic_map the traffic map
	 */
	public void clearActions(TrafficMap traffic_map) {
		actions.clear();
		for (int i = 0; i < traffic.size(); i++) {
			traffic_map.unreserve(traffic.get(i));
		}
		traffic.clear();
		if (last_traffic != null) {
			traffic_map.unreserve(last_traffic);
			last_traffic = null;
		}
	}
	
	/**
	 * Removes the unit
	 *@param traffic_map the traffic map
	 */
	public void remove(TrafficMap traffic_map) {
		actions.clear();
		for (int i = 0; i < traffic.size(); i++) {
			traffic_map.unreserve(traffic.get(i));
		}
		traffic.clear();
		if (building_traffic != null) {
			traffic_map.unreserve(building_traffic);
		}
		if (last_traffic != null) {
			traffic_map.unreserve(last_traffic);
			last_traffic = null;
		}
	}
	
	/**
	 * Evaluates how good (at fighting) a unit is
	 * @param unit
	 * @return
	 */
	public static float evaluate(UnitDefinition def) {
		if (def.is_building) {
			return 0;
		}
		return(((def.attack_min*def.attack_range+def.vision)*0.5f+(def.is_flying?1:0))/(float)(def.attack_speed+def.move_speed));
	}
	
	/**
	 * Evaluates how good this unit is (fighting)
	 * @return
	 */
	public float evaluate() {
		if (stats.isBuilding()) {
			return 0;
		}
		return(((stats.getAttackMin()*stats.getAttackRange()+stats.getVision())*((float)stats.getHP()/(float)stats.getMaxHP())+(stats.isFlying()?1:0))/(float)(stats.getAttackSpeed()+stats.getMoveSpeed()));
	}
	
	/**
	 * Returns a listing of adjacent locations that are valid to build units on
	 * @param product
	 * @param ai
	 * @return
	 */
	public ArrayList<Integer> adjacent_build_locations(GeneralAIProduction product, GeneralAI ai) {
		ArrayList<Integer> locations = new ArrayList<Integer>();
		if (stats.getX() > 0 && ((ai.exploration_manager.map[stats.getX()-1+stats.getY()*ai.state.getMapWidth()]&GameState.MAP_WALL) != 1 || product.def.is_flying)) {
			locations.add(stats.getX()-1+stats.getY()*ai.state.getMapWidth());
		}
		if (stats.getX() < ai.state.getMapWidth()-1 && ((ai.exploration_manager.map[stats.getX()+1+stats.getY()*ai.state.getMapWidth()]&GameState.MAP_WALL) != 1 || product.def.is_flying)) {
			locations.add(stats.getX()+1+stats.getY()*ai.state.getMapWidth());
		}
		if (stats.getY() > 0 && ((ai.exploration_manager.map[stats.getX()+(stats.getY()-1)*ai.state.getMapWidth()]&GameState.MAP_WALL) != 1 || product.def.is_flying)) {
			locations.add(stats.getX()+(stats.getY()-1)*ai.state.getMapWidth());
		}
		if (stats.getY() < ai.state.getMapHeight()-1 && ((ai.exploration_manager.map[stats.getX()+(stats.getY()+1)*ai.state.getMapWidth()]&GameState.MAP_WALL) != 1 || product.def.is_flying)) {
			locations.add(stats.getX()+(stats.getY()+1)*ai.state.getMapWidth());
		}
		return locations;
	}
}
