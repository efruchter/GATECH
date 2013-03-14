package rts.units;

import java.util.ArrayList;

/**
 * \brief The stats that an individual unit has (this is an internal use only class)
 * @author Jeff Bernard
 *
 */
public class UnitStats {
	private static long next_id = 0;
	
	public long id;
	public int player;
	public int hp;
	public int x, y;
	public UnitDefinition definition;
	
	public int killed_by; /**< which player killed this unit */
	public boolean seen_dead; /**< whether or not the opponent has seen the dead guy */ 
	
	public int resources;
	public int resources_type;
	
	public UnitAction action; /**< the action this unit will take */
	public boolean last_action_success; /**< whether or not the last action was a success */
	
	public ArrayList<UnitAction> legalActions; /**< a listing of legal actions */
	
	private UnitStats(int _player, int _x, int _y, UnitDefinition _definition, boolean c) {
		if (c) {
			id = next_id;
		} else {
			id = next_id++;
		}
		
		player = _player;
		x = _x;
		y = _y;
		definition = _definition;
		hp = definition.hp;
		
		resources = 0;
		resources_type = definition.resources_type;
		
		action = null;
		last_action_success = false;
		seen_dead = false;
		
		killed_by = -1;
		
		legalActions = new ArrayList<UnitAction>();
	}
	/**
	 * Creates a new unit's stats
	 * @param _player
	 * @param _x
	 * @param _y
	 * @param _definition
	 */
	public UnitStats(int _player, int _x, int _y, UnitDefinition _definition) {
		this(_player, _x, _y, _definition, false);	
	}
	
	/**
	 * Makes a copy of the stats
	 * @return a copy of the stats
	 */
	public UnitStats copy() {
		UnitStats u = new UnitStats(player, x, y, definition, true);
		
		u.id = id;
		u.hp = hp;
		u.resources = resources;
		u.resources_type = resources_type;
		u.last_action_success = last_action_success;
		u.killed_by = killed_by;
		u.action = action != null ? action.copy() : null;
		
		return u;
	}
}
