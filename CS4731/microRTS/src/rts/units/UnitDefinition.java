package rts.units;

import java.util.ArrayList;


/**
 * \brief A general unit defintiion that could turn out to be anything
 * @author Jeff Bernard
 *
 */
public class UnitDefinition {
	
	// unit stats
	public ArrayList<Integer> cost;
	public int vision;
	public int attack_range;
	public int hp;
	public int attack_min, attack_max;
	public int produce_speed, move_speed, attack_speed;
	public boolean is_worker;
	public boolean is_flying; /**< flying units can move over obstructed terrain. Non-flying units can not */
	public boolean is_resources;
	public boolean is_building;
	public boolean is_stockpile_building;
	
	public String label;
	
	public int resources_type;
	public int harvest_speed;
	public int harvest_amt;
	
	public ArrayList<Integer> produces;
	public ArrayList<Integer> unit_upgrades;
	public ArrayList<Integer> building_upgrades;
	
	public int type;
	
	public boolean active_player; /**< whether or not this is the active player */
	
	
	/**
	 * Creates a new definition
	 */
	public UnitDefinition() {
		cost = new ArrayList<Integer>();
		produces = new ArrayList<Integer>();
		unit_upgrades = new ArrayList<Integer>();
		building_upgrades = new ArrayList<Integer>();
		
		vision = 0;
		attack_range = 0;
		hp = 0;
		attack_min = 0;
		attack_max = 0;
		produce_speed = 0;
		move_speed = 0;
		attack_speed = 0;
		is_worker = false;
		is_flying = false;
		is_resources = false;
		is_building = false;
		is_stockpile_building = false;
		
		resources_type = 0;
		harvest_speed = 0;
		harvest_amt = 0;
		
		type = 0;
		
		active_player = false;
	}
	
	/**
	 * Creates a new definition
	 * @param resourceTypes how many types of resources there are
	 */
	public UnitDefinition(int resourceTypes) {
		this();
		for (int i = 0; i < resourceTypes; i++) {
			cost.add(0);
		}
	}
	
	/**
	 * Creates a clone of this unit definition
	 * @return clone
	 */
	public UnitDefinition clone() {
		UnitDefinition def = new UnitDefinition(cost.size());
		for (int i = 0; i < cost.size(); i++) {
			def.cost.set(i, cost.get(i));
		}
		for (int i = 0; i < produces.size(); i++) {
			def.produces.add(produces.get(i));
		}
		for (int i = 0; i < unit_upgrades.size(); i++) {
			def.unit_upgrades.add(unit_upgrades.get(i));
		}
		for (int i = 0; i < building_upgrades.size(); i++) {
			def.building_upgrades.add(building_upgrades.get(i));
		}
		
		def.vision = vision;
		def.attack_range = attack_range;
		def.hp = hp;
		def.attack_min = attack_min;
		def.attack_max = attack_max;
		def.produce_speed = produce_speed;
		def.move_speed = move_speed;
		def.attack_speed = attack_speed;
		def.is_worker = is_worker;
		def.is_flying = is_flying;
		def.is_resources = is_resources;
		def.is_building = is_building;
		def.is_stockpile_building = is_stockpile_building;
		
		def.resources_type = resources_type;
		
		def.active_player = false;
		def.label = label;
		def.harvest_amt = harvest_amt;
		def.harvest_speed = harvest_speed;
		
		def.type = type;
		
		return def;
	}
	
	/**
	 * Returns a value in [attack_min, attack_max]
	 * @return
	 */
	public int getDamage() {
		return attack_min+(int)(Math.random()*(attack_max-attack_min));
	}
}
