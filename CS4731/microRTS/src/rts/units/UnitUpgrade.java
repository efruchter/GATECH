package rts.units;

import java.util.ArrayList;

/**
 * \brief The changes that get made to a unit when it upgrades
 */
public class UnitUpgrade {
	private UnitDefinition changes; /**< the changes that get made to a unit when it upgrades */
	
	private ArrayList<Integer> cost; /**< the cost of this upgrade */
	
	private static int next_id = 0; /**< next upgrade id */
	private int id;
	
	private int upgrade_time; /**< how long to make the upgrade */
	private int unit_id; /**< unit id this is for */
	
	/**
	 * Creates a new unit upgrade
	 * @param statChanges the stats that get changed
	 * @param upgradeCost the cost of making the upgrade
	 * @param time how long the upgrade takes
	 */
	public UnitUpgrade(UnitDefinition statChanges, ArrayList<Integer> upgradeCost, int time, int unit) {
		changes = statChanges;
		cost = upgradeCost;
		id = next_id++;
		upgrade_time = time;
		unit_id = unit;
	}
	
	/**
	 * Gets the cost of the upgrade (in a certain resource type)
	 * @param resourceType the resource type
	 * @return the cost or 0 if invalid resource type
	 */
	public int getCost(int resourceType) {
		if (resourceType >= 0 && resourceType < cost.size()) {
			return cost.get(resourceType);
		}
		return 0;
	}
	
	/**
	 * returns how long this upgrade takes
	 * @return
	 */
	public int getUpgradeTime() {
		return upgrade_time;
	}
	
	/**
	 * Returns the id of this upgrade
	 * @return
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * returns what unit this upgrade is for
	 * @return
	 */
	public int getUnit() {
		return unit_id;
	}
	
	/**
	 * Upgrades a unit definition
	 * @param definition the definition to upgrade
	 */
	public void upgrade(UnitDefinition definition) {
		for (int i = 0; i < changes.cost.size(); i++) {
			definition.cost.set(i, definition.cost.get(i)+changes.cost.get(i));
		}
		definition.vision += changes.vision;
		definition.attack_range += changes.attack_range;
		definition.hp += changes.hp;
		definition.attack_min += changes.attack_min;
		definition.attack_max += changes.attack_max;
		definition.produce_speed += changes.produce_speed;
		definition.move_speed += changes.move_speed;
		definition.attack_speed += changes.attack_speed;
	}
	
	/**
	 * Gets the various upgrade differences
	 */
	public int getVisionChange() {
		return changes.vision;
	}
	
	public int getAttackRangeChange() {
		return changes.attack_range;
	}
	
	public int getHPChange() {
		return changes.hp;
	}
	
	public int getAttackMinChange() {
		return changes.attack_min;
	}
	
	public int getAttackMaxChange() {
		return changes.attack_max;
	}
	
	public int getProduceSpeedChange() {
		return changes.produce_speed;
	}
	
	public int getMoveSpeedChange() {
		return changes.move_speed;
	}
	
	public int getAttackSpeedChange() {
		return changes.attack_speed;
	}
	
	public int getCostChange(int resourceType) {
		if (resourceType >= 0 && resourceType < changes.cost.size()) {
			return changes.cost.get(resourceType);
		}
		return 0;
	}
	
	/**
	 * Clones this upgrade
	 */
	public UnitUpgrade clone() {
		UnitUpgrade upgrade = new UnitUpgrade(changes, cost, upgrade_time, unit_id);
		upgrade.id = id;
		next_id--;
		return upgrade;
	}
}
