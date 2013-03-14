/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rts.units;

import java.util.ArrayList;

/**
 * \package rts.units
 * \brief Units in the RTS game
 */

/**
 * \brief Read access to a unit
 * @author Jeff Bernard
 *
 */
public class Unit {
	private UnitStats stats;
	
	public Unit(UnitStats _stats) {
		stats = _stats;
	}
	
	/**
	 * Checks if the unit has an order already
	 * always returns false if this is not your unit
	 * @return bool
	 */
	public boolean hasAction() {
		return(stats.action != null);
	}
	
	/**
	 * Orders the unit
	 */
	public void setAction(UnitAction act) {
		if (stats.action == null && stats.definition.active_player && act.getUnitID() == stats.id) {
			stats.action = act;
			stats.action.setTimestamp();
		}
	}
	
	/**
	 * Returns whether or not the last action executed successfully
	 * always returns false if this is not your unit
	 * @return
	 */
	public boolean lastActionSucceeded() {
		if (stats.definition.active_player) {
			return stats.last_action_success;
		}
		return false;
	}
	
	/**
	 * Returns a list of legal actions for this unit
	 * @return
	 */
	public ArrayList<UnitAction> getActions() {
		return stats.legalActions;
	}
	
	/**
	 * Returns the unit's current action
	 * @return
	 */
	public UnitAction getAction() {
		return stats.action;
	}
	
	public long getID() {
		return stats.id;
	}
	
	public int getPlayer() {
		return stats.player;
	}
	
	public int getX() {
		return stats.x;
	}
	
	public int getY() {
		return stats.y;
	}
	
	public int getHP() {
		return stats.hp;
	}
	
	public int getMaxHP() {
		return stats.definition.hp;
	}
	
	/**
	 * Workers can build buildings, harvest resources, and return resources to stockpiles
	 * @return
	 */
	public boolean isWorker() {
		return stats.definition.is_worker;
	}
	
	/**
	 * Flying units can move over obstructed terrain. Non-flying units cannot.
	 * @return
	 */
	public boolean isFlying() {
		return stats.definition.is_flying;
	}
	
	public boolean isResources() {
		return stats.definition.is_resources;
	}
	
	public boolean isBuilding() {
		return stats.definition.is_building;
	}
	
	/**
	 * Stockpiles are buildings that resources can be returned to
	 * @return
	 */
	public boolean isStockpile() {
		return stats.definition.is_stockpile_building;
	}
	
	public int getResources() {
		return stats.resources;
	}
	
	public int getResourcesType() {
		return stats.resources_type;
	}
	
	public String getLabel() {
		return stats.definition.label;
	}
	
	public int getVision() {
		return stats.definition.vision;
	}
	
	public int getAttackRange() {
		return stats.definition.attack_range;
	}
	
	public int getAttackMin() {
		return stats.definition.attack_min;
	}
	
	public int getAttackMax() {
		return stats.definition.attack_max;
	}
	
	public int getMoveSpeed() {
		return stats.definition.move_speed;
	}
	
	public int getAttackSpeed() {
		return stats.definition.attack_speed;
	}
	
	public int getBuildSpeed() {
		return stats.definition.produce_speed;
	}
	
	public int getHarvestSpeed() {
		return stats.definition.harvest_speed;
	}
	
	public int getHarvestAmount() {
		return stats.definition.harvest_amt;
	}
	
	public ArrayList<Integer> getCost() {
		return new ArrayList<Integer>(stats.definition.cost);
	}
	
	public int getCost(int resource_type) {
		if (resource_type >= 0 && resource_type < stats.definition.cost.size()) {
			return stats.definition.cost.get(resource_type);
		}
		return -1;
	}
	
	public ArrayList<Integer> getProduce() {
		return new ArrayList<Integer>(stats.definition.produces);
	}
	
	public ArrayList<Integer> getUnitUpgrades() {
		return new ArrayList<Integer>(stats.definition.unit_upgrades);
	}
	
	public ArrayList<Integer> getBuildingUpgrades() {
		return new ArrayList<Integer>(stats.definition.building_upgrades);
	}
	
	public int getType() {
		return stats.definition.type;
	}
	
	public Unit copy() {
		Unit u = new Unit(stats.copy());
		return u;
	}
	
	public UnitStats copyStats() {
		return stats.copy();
	}
	
	public void seen_dead() {
		stats.seen_dead = true;
	}
}
