package ai.general;

import java.util.ArrayList;

import rts.GameState;
import rts.units.Unit;
import rts.units.UnitAction;

/**
 * \brief A farm is a place where one can gather resources it should define pathways for farmers to take
 * @author Jeff Bernard
 *
 */
public class GeneralAIFarm extends GeneralAIObject {	
	public static final int FARM_CLOSED = -1; /**< the farm is closed */
	public static final int FARM_OPEN = -2; /**< the farm is open */
	public static final int FARM_WALL_OPEN = -3; /**< the farm is open to fliers */
	public static final int FARM_UNKNOWN = -4; /**< nobody knows what's going on at the farm !? */
	
	public static final int FARMER_UP = 0; /**< the farmer in the up spot */
	public static final int FARMER_DOWN = 1; /**< the farmer in the down spot */
	public static final int FARMER_LEFT = 2; /**< the farmer in the left spot */
	public static final int FARMER_RIGHT = 3; /**< the farmer in the right spot */
	
	public Unit resources; /**< the resources unit */
	public long[] farmers; /**< the guys farming */
	public int[] _farmers; /**< the farmers original states */
	
	/**
	 * Constructs a new farm
	 * @param rsrc the resources
	 * @param ai the ai
	 */
	public GeneralAIFarm(Unit rsrc, GeneralAI ai) {
		resources = rsrc;
		
		farmers = new long[4]; // assumption, square grid
		_farmers = new int[4];
		farmers[0] = FARM_UNKNOWN;
		farmers[1] = FARM_UNKNOWN;
		farmers[2] = FARM_UNKNOWN;
		farmers[3] = FARM_UNKNOWN;
		_farmers[0] = FARM_UNKNOWN;
		_farmers[1] = FARM_UNKNOWN;
		_farmers[2] = FARM_UNKNOWN;
		_farmers[3] = FARM_UNKNOWN;
		
		update_openings(ai);
	}
	
	/**
	 * Returns whether or not this farm has an opening
	 * @param unit the unit to check if the farm has an opening for
	 * @return whether or not this farm has an opening
	 */
	public boolean has_opening(GeneralAIUnit unit) {
		if (farmers[FARMER_UP] == FARM_OPEN || (unit.stats.isFlying() && farmers[FARMER_UP] == FARM_WALL_OPEN)) {
			return true;
		}
		if (farmers[FARMER_DOWN] == FARM_OPEN || (unit.stats.isFlying() && farmers[FARMER_DOWN] == FARM_WALL_OPEN)) {
			return true;
		}
		if (farmers[FARMER_LEFT] == FARM_OPEN || (unit.stats.isFlying() && farmers[FARMER_LEFT] == FARM_WALL_OPEN)) {
			return true;
		}
		if (farmers[FARMER_RIGHT] == FARM_OPEN || (unit.stats.isFlying() && farmers[FARMER_RIGHT] == FARM_WALL_OPEN)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Returns whether or not there are any openings that don't require flying
	 * @return whether or not there are any openings that don't require flying
	 */
	public boolean has_openings_strict() {
		if (farmers[FARMER_UP] == FARM_OPEN || farmers[FARMER_DOWN] == FARM_OPEN || farmers[FARMER_LEFT] == FARM_OPEN || farmers[FARMER_RIGHT] == FARM_OPEN) {
			return true;
		}
		return false;
	}

	
	@Override
	/**
	 * Orders a unit to interact with this object
	 * @param unit the unit
	 */
	public void order_unit(GeneralAIUnit unit, GeneralAI ai) {		
		//System.out.println(unit.stats.getID()+" working at farm #"+resources.getID());
		if (unit.stats.getResources() == 0) {
			path_to_farm(unit, ai);
		} else {
			path_to_stockpile(unit, ai, unit.stats.getX()+unit.stats.getY()*ai.state.getMapWidth(), ai.current_turn);
		}
	}
	
	/**
	 * Plans a path to the farm
	 * @param unit
	 * @param ai
	 */
	private void path_to_farm(GeneralAIUnit unit, GeneralAI ai) {
		// check if we're already next to a farm
		for (int i = 0; i < unit.stats.getActions().size(); i++) {
			UnitAction action = unit.stats.getActions().get(i);
			if (action.getType() == UnitAction.HARVEST) {
				unit.addAction(action, ai.traffic_map, unit.stats.getX()+unit.stats.getY()*ai.state.getMapWidth(), ai.current_turn, ai.current_turn+resources.getHarvestSpeed());
				return;
			}
		}
		
		// find the path to nearest opening
		ArrayList<Integer> openings = new ArrayList<Integer>();
		int location = resources.getX()+resources.getY()*ai.state.getMapWidth();
		
		// first check if this unit has already reserved a spot
		if (farmers[FARMER_UP] == unit.stats.getID()) {
			openings.add(location-ai.state.getMapWidth());
		} else if (farmers[FARMER_DOWN] == unit.stats.getID()) {
			openings.add(location+ai.state.getMapWidth());
		} else if (farmers[FARMER_LEFT] == unit.stats.getID()) {
			openings.add(location-1);
		} else if (farmers[FARMER_RIGHT] == unit.stats.getID()) {
			openings.add(location+1);
		}
		
		if (openings.size() == 0) {
			if (farmers[FARMER_UP] == FARM_OPEN || (unit.stats.isFlying() && farmers[FARMER_UP] == FARM_WALL_OPEN)) {
				openings.add(location-ai.state.getMapWidth());
			}
			if (farmers[FARMER_DOWN] == FARM_OPEN || (unit.stats.isFlying() && farmers[FARMER_DOWN] == FARM_WALL_OPEN)) {
				openings.add(location+ai.state.getMapWidth());
			}
			if (farmers[FARMER_LEFT] == FARM_OPEN || (unit.stats.isFlying() && farmers[FARMER_LEFT] == FARM_WALL_OPEN)) {
				openings.add(location-1);
			}
			if (farmers[FARMER_RIGHT] == FARM_OPEN || (unit.stats.isFlying() && farmers[FARMER_RIGHT] == FARM_WALL_OPEN)) {
				openings.add(location+1);
			}
		}
		
		ArrayList<Integer[]> rpath = ai.get_path(unit.stats, unit.stats.getX()+unit.stats.getY()*ai.state.getMapWidth(), ai.current_turn, openings);
		if (rpath != null) { // is possible to reach goal
			boolean there = false;
			if (rpath.size() == 0) {
				rpath.add(new Integer[]{unit.stats.getX()+unit.stats.getY()*ai.state.getMapWidth(), ai.current_turn});
				there = true;
			}
			if (rpath.get(0)[0] == location-1) {
				//System.out.println("farming left");
				farmers[FARMER_LEFT] = unit.stats.getID();
			} else if (rpath.get(0)[0] == location+1) {
				//System.out.println("farming right");
				farmers[FARMER_RIGHT] = unit.stats.getID();
			} else if (rpath.get(0)[0] == location+ai.state.getMapWidth()) {
				//System.out.println("farming down");
				farmers[FARMER_DOWN] = unit.stats.getID();
			} else {
				//System.out.println("farming up");
				farmers[FARMER_UP] = unit.stats.getID();
			}
			
			// set order queue
			if (!there) {
				for (int i = rpath.size()-1; i >= 0; i--) {
					//System.out.println("adding MOVE");
					unit.addAction(new UnitAction(unit.stats, UnitAction.MOVE, rpath.get(i)[0]%ai.state.getMapWidth(), rpath.get(i)[0]/ai.state.getMapWidth(),-1), ai.traffic_map, rpath.get(i)[0], rpath.get(i)[1], rpath.get(i)[1]+unit.stats.getMoveSpeed());
				}
			}
			int now_at = rpath.get(0)[0];
			int now_start = rpath.get(0)[1];
			
			//System.out.println("adding HARVEST");
			unit.addAction(new UnitAction(unit.stats, UnitAction.HARVEST, resources.getX(), resources.getY(), -1), ai.traffic_map, now_at, now_start, now_start+resources.getHarvestSpeed());
			now_start += resources.getHarvestSpeed();
			
			// after harvest, need to return to a stockpile
			path_to_stockpile(unit, ai, now_at, now_start);
		} else { // cannot reach this...
			//unit.clearActions(ai.traffic_map);
			//unit.object = null;
		}
	}
	
	/**
	 * Plans a path to the nearest stockpile
	 * @param unit
	 * @param ai
	 * @param now_at
	 * @param now_start
	 */
	private void path_to_stockpile(GeneralAIUnit unit, GeneralAI ai, int now_at, int now_start) {
		// after harvest, need to return to a stockpile
		ArrayList<Integer> stockpiles = new ArrayList<Integer>();
		for (int i = 0; i < ai.units.size(); i++) {
			Unit u = ai.units.get(i).stats;
			if (u.isStockpile()) {
				stockpiles.add(u.getX()+u.getY()*ai.state.getMapWidth());
			}
		}
		
		ArrayList<Integer[]> rpath = ai.get_path(unit.stats, now_at, now_start, stockpiles);
		if (rpath != null) {
			boolean there = false;
			if (rpath.size() <= 1) {
				there = true;
				rpath.add(new Integer[]{now_at, now_start});
				if (rpath.size() == 1) {
					rpath.add(new Integer[]{now_at, now_start});
				}
			}
			if (!there) {
				for (int i = rpath.size()-1; i >= 1; i--) {
					//unit.actions.add(new UnitAction(unit.stats, UnitAction.MOVE, rpath.get(i)[0]%ai.state.getMapWidth(), rpath.get(i)[0]/ai.state.getMapWidth(),-1));
					//System.out.println("adding MOVE");
					unit.addAction(new UnitAction(unit.stats, UnitAction.MOVE, rpath.get(i)[0]%ai.state.getMapWidth(), rpath.get(i)[0]/ai.state.getMapWidth(),-1), ai.traffic_map, rpath.get(i)[0], rpath.get(i)[1], rpath.get(i)[1]+unit.stats.getMoveSpeed());
				}
				now_start = rpath.get(1)[1];
				now_at = rpath.get(1)[0];
			}
			//unit.actions.add(new UnitAction(unit.stats, UnitAction.RETURN, rpath.get(0)[0]%ai.state.getMapWidth(), rpath.get(0)[0]/ai.state.getMapWidth(), -1));
			//System.out.println("adding RETURN");
			unit.addAction(new UnitAction(unit.stats, UnitAction.RETURN, rpath.get(0)[0]%ai.state.getMapWidth(), rpath.get(0)[0]/ai.state.getMapWidth(),-1), ai.traffic_map, now_at, now_start, now_start+UnitAction.DEFAULT_COOLDOWN);
		} else {
		}
	}

	@Override
	/**
	 * Calculates the distance from the unit to this object
	 * @param unit the unit
	 * @param ai the ai
	 * @return the distance
	 */
	public int distance(GeneralAIUnit unit, GeneralAI ai) {
		if (has_opening(unit)) {
			return((resources.getX()-unit.stats.getX())*(resources.getX()-unit.stats.getX())+(resources.getY()-unit.stats.getY())*(resources.getY()-unit.stats.getY()));
		}
		
		return GeneralAI.DISTANCE_IGNORE;
	}

	@Override
	public void action_succeeded(GeneralAIUnit unit, GeneralAI ai, int type) {
		if (type == UnitAction.RETURN) {
			ai.money.set(resources.getResourcesType(), ai.money.get(resources.getResourcesType())+unit.resources_held);
		}
	}

	@Override
	public void remove(GeneralAIUnit unit, GeneralAI ai) {		
		// only remove this unit if it doesn't already have resources
		if (unit.stats.getResources() == 0) {
			//System.out.println(unit.stats.getID()+" was removed from farm #"+resources.getID());
			if (farmers[FARMER_LEFT] == unit.stats.getID()) {
				farmers[FARMER_LEFT] = _farmers[FARMER_LEFT];
			} else if (farmers[FARMER_RIGHT] == unit.stats.getID()) {
				farmers[FARMER_RIGHT] = _farmers[FARMER_RIGHT];
			} else if (farmers[FARMER_DOWN] == unit.stats.getID()) {
				farmers[FARMER_DOWN] = _farmers[FARMER_DOWN];
			} else {
				farmers[FARMER_UP] = _farmers[FARMER_UP];
			}
			
			//unit.actions.clear();
			unit.clearActions(ai.traffic_map);
			unit.object = null;
			unit.remove_object = false;
		} else {
			//System.out.println(unit.stats.getID()+" should be removed from a farm #"+resources.getID());
			unit.remove_object = true;
		}
	}
	
	@Override
	public void update_orders(GeneralAIUnit unit, GeneralAI ai) {
		// check if the spot we originally wanted to visit is no longer possible....
		boolean removed = false;
		if (farmers[FARMER_UP] == unit.stats.getID()) {
			if (_farmers[FARMER_UP] == FARM_CLOSED) {
				removed = true;
			}
		} else if (farmers[FARMER_DOWN] == unit.stats.getID()) {
			if (_farmers[FARMER_DOWN] == FARM_CLOSED) {
				removed = true;
			}
		} else if (farmers[FARMER_LEFT] == unit.stats.getID()) {
			if (_farmers[FARMER_LEFT] == FARM_CLOSED) {
				removed = true;
			}
		} else if (farmers[FARMER_RIGHT] == unit.stats.getID()) {
			if (_farmers[FARMER_RIGHT] == FARM_CLOSED) {
				removed = true;
			}
		}
		
		if (!removed) {
			unit.clearActions(ai.traffic_map); // because we need to issue a new traffic map
			order_unit(unit, ai);
		} else {
			remove(unit, ai);
		}
	}
	
	/**
	 * Updates the openings at the farm
	 * @param ai
	 */
	public void update_openings(GeneralAI ai) {
		// check if all spots are valid
		if (resources.getY() > 0) { // up might be open
			if ((ai.exploration_manager.map[(resources.getY()-1)*ai.state.getMapWidth()+resources.getX()]&(GameState.MAP_FOG|GameState.MAP_WALL|GameState.MAP_NEUTRAL)) != 0) { // up is a wall
				if (farmers[FARMER_UP] < 0) {
					farmers[FARMER_UP] = FARM_CLOSED;
				}
				_farmers[FARMER_UP] = FARM_CLOSED;
			} else {
				if (farmers[FARMER_UP] < 0) {
					farmers[FARMER_UP] = FARM_OPEN;
				}
				_farmers[FARMER_UP] = FARM_OPEN;
			}
		} else { // up is closed
			if (farmers[FARMER_UP] < 0) {
				farmers[FARMER_UP] = FARM_CLOSED;
			}
			_farmers[FARMER_UP] = FARM_CLOSED;
		}
		
		if (resources.getY() < ai.state.getMapHeight()-1) { // down might be open
			if ((ai.exploration_manager.map[(resources.getY()+1)*ai.state.getMapWidth()+resources.getX()]&(GameState.MAP_FOG|GameState.MAP_WALL|GameState.MAP_NEUTRAL)) != 0) { // up is a wall
				if (farmers[FARMER_DOWN] < 0) {
					farmers[FARMER_DOWN] = FARM_CLOSED;
				}
				_farmers[FARMER_DOWN] = FARM_CLOSED;
			} else {
				if (farmers[FARMER_DOWN] < 0) {
					farmers[FARMER_DOWN] = FARM_OPEN;
				}
				_farmers[FARMER_DOWN] = FARM_OPEN;
			}
		} else { // up is closed
			if (farmers[FARMER_DOWN] < 0) {
				farmers[FARMER_DOWN] = FARM_CLOSED;
			}
			_farmers[FARMER_DOWN] = FARM_CLOSED;
		}
		
		if (resources.getX() > 0) { // left might be open
			if ((ai.exploration_manager.map[resources.getY()*ai.state.getMapWidth()+resources.getX()-1]&(GameState.MAP_FOG|GameState.MAP_WALL|GameState.MAP_NEUTRAL)) != 0) { // up is a wall
				if (farmers[FARMER_LEFT] < 0) {
					farmers[FARMER_LEFT] = FARM_CLOSED;
				}
				_farmers[FARMER_LEFT] = FARM_CLOSED;
			} else {
				if (farmers[FARMER_LEFT] < 0) {
					farmers[FARMER_LEFT] = FARM_OPEN;
				}
				_farmers[FARMER_LEFT] = FARM_OPEN;
			}
		} else { // up is closed
			if (farmers[FARMER_LEFT] < 0) {
				farmers[FARMER_LEFT] = FARM_CLOSED;
			}
			_farmers[FARMER_LEFT] = FARM_CLOSED;
		}
		
		if (resources.getX() < ai.state.getMapWidth()-1) { // right might be open
			if ((ai.exploration_manager.map[resources.getY()*ai.state.getMapWidth()+resources.getX()+1]&(GameState.MAP_FOG|GameState.MAP_WALL|GameState.MAP_NEUTRAL)) != 0) { // up is a wall
				if (farmers[FARMER_RIGHT] < 0) {
					farmers[FARMER_RIGHT] = FARM_CLOSED;
				}
				_farmers[FARMER_RIGHT] = FARM_CLOSED;
			} else {
				if (farmers[FARMER_RIGHT] < 0) {
					farmers[FARMER_RIGHT] = FARM_OPEN;
				}
				_farmers[FARMER_RIGHT] = FARM_OPEN;
			}
		} else { // up is closed
			if (farmers[FARMER_RIGHT] < 0) {
				farmers[FARMER_RIGHT] = FARM_CLOSED;
			}
			_farmers[FARMER_RIGHT] = FARM_CLOSED;
		}
	}
}
