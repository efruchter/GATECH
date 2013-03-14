package rts.units;

import java.util.ArrayList;

import org.jdom.Element;

/**
 * \brief All of the unit definitions
 * @author Jeff Bernard
 *
 */
public class UnitDefinitionManager {
	// the definitions
	// first listing is neutral army, so it's shifted player+1
	public ArrayList<ArrayList<UnitDefinition> > resource_defs;
	public ArrayList<ArrayList<UnitDefinition> > unit_defs;
	public ArrayList<ArrayList<UnitDefinition> > building_defs;
	
	public ArrayList<ArrayList<UnitUpgrade> > unit_upgrades;
	public ArrayList<ArrayList<UnitUpgrade> > building_upgrades;
	
	public ArrayList<Integer> resource_scores; /**< how many points each resource type gets you */
	
	/**
	 * Loads the unit definitions from xml
	 * @param xmlRoot
	 */
	public UnitDefinitionManager(Element xmlRoot) {
		resource_defs = new ArrayList<ArrayList<UnitDefinition> >();
		unit_defs = new ArrayList<ArrayList<UnitDefinition> >();
		building_defs = new ArrayList<ArrayList<UnitDefinition> >();
		
		unit_upgrades = new ArrayList<ArrayList<UnitUpgrade> >();
		building_upgrades = new ArrayList<ArrayList<UnitUpgrade> >();
		
		resource_scores = new ArrayList<Integer>();
		
		resource_defs.add(new ArrayList<UnitDefinition>());
		unit_defs.add(new ArrayList<UnitDefinition>());
		building_defs.add(new ArrayList<UnitDefinition>());
		
		unit_upgrades.add(new ArrayList<UnitUpgrade>());
		building_upgrades.add(new ArrayList<UnitUpgrade>());
		
		Element resources = xmlRoot.getChild("resources");
		Element units = xmlRoot.getChild("units");
		Element buildings = xmlRoot.getChild("buildings");
		Element upgrades = xmlRoot.getChild("upgrades");

		
		// add resources
		for (int i = 0; i < resources.getChildren().size(); i++) {
			Element child = (Element)resources.getChildren().get(i);
			if (child.getName().equalsIgnoreCase("resource")) {
				UnitDefinition resource = new UnitDefinition();
				resource.is_resources = true; 
				resource.harvest_speed = getInt(child, "harvest_time");
				resource.harvest_amt = getInt(child, "harvest_amt");
				resource.label = child.getAttributeValue("label");
				resource.resources_type = resource_defs.get(0).size();
				resource_defs.get(0).add(resource);
				
				resource_scores.add(getInt(child, "score"));
			}
		}
		
		// get unit definitions
		for (int i = 0; i < units.getChildren().size(); i++) {
			Element child = (Element)units.getChildren().get(i);
			if (child.getName().equalsIgnoreCase("unit-def")) {
				UnitDefinition unit = new UnitDefinition(resourceTypes());
				unit.label = child.getAttributeValue("label");
				for (int j = 0; j < child.getChildren().size(); j++) {
					Element stat = (Element)child.getChildren().get(j);
					if (stat.getName().equalsIgnoreCase("cost")) {
						for (int k = 0; k < stat.getChildren().size(); k++) {
							Element cost = (Element)stat.getChildren().get(k);
							if (cost.getName().equalsIgnoreCase("resource")) {
								int resourceType = getInt(cost, "type");
								if (resourceType >= 0 && resourceType < resourceTypes()) {
									unit.cost.set(resourceType, getInt(cost, "cost"));
								}
							}
						}
					} else if (stat.getName().equalsIgnoreCase("stats")) {
						unit.hp = getInt(stat, "hp");
						unit.vision = getInt(stat, "vision");
						unit.attack_range = getInt(stat, "attack_range");
						unit.attack_min = getInt(stat, "attack_min");
						unit.attack_max = getInt(stat, "attack_max");
						unit.attack_speed = getInt(stat, "attack_time");
						unit.produce_speed = getInt(stat, "produce_time");
						unit.move_speed = getInt(stat, "move_time");
						unit.is_worker = Boolean.parseBoolean(stat.getAttributeValue("is_worker"));
						unit.is_flying = Boolean.parseBoolean(stat.getAttributeValue("is_flying"));
						unit.type = unit_defs.get(0).size();
					}
				}
				unit_defs.get(0).add(unit);
			}
		}
		
		// get building definitions
		for (int i = 0; i < buildings.getChildren().size(); i++) {
			Element child = (Element)buildings.getChildren().get(i);
			if (child.getName().equalsIgnoreCase("building-def")) {
				UnitDefinition building = new UnitDefinition(resourceTypes());
				building.is_building = true;
				building.label = child.getAttributeValue("label");
				if (Boolean.parseBoolean(child.getAttributeValue("stockpile"))) {
					building.is_stockpile_building = true;
				}
				for (int j = 0; j < child.getChildren().size(); j++) {
					Element stat = (Element)child.getChildren().get(j);
					if (stat.getName().equalsIgnoreCase("cost")) {
						for (int k = 0; k < stat.getChildren().size(); k++) {
							Element cost = (Element)stat.getChildren().get(k);
							if (cost.getName().equalsIgnoreCase("resource")) {
								int resourceType = getInt(cost, "type");
								if (resourceType >= 0 && resourceType < resourceTypes()) {
									building.cost.set(resourceType, getInt(cost, "cost"));
								}
							}
						}
					} else if (stat.getName().equalsIgnoreCase("stats")) {
						building.hp = getInt(stat, "hp");
						building.vision = getInt(stat, "vision");
						building.produce_speed = getInt(stat, "produce_time");
						building.type = building_defs.get(0).size();
					} else if (stat.getName().equalsIgnoreCase("produce")) {
						for (int k = 0; k < stat.getChildren().size(); k++) {
							Element produce = (Element)stat.getChildren().get(k);
							if (produce.getName().equalsIgnoreCase("unit")) {
								int unitType = getInt(produce, "type");
								if (unitType >= 0 && unitType < unit_defs.get(0).size()) {
									building.produces.add(unitType);
								}
							}
						}
					}
				}
				building_defs.get(0).add(building);
			}
		}
		
		// get upgrades
		if (upgrades != null) {
			for (int i = 0; i < upgrades.getChildren().size(); i++) {
				Element child = (Element)upgrades.getChildren().get(i);
				if (child.getName().equalsIgnoreCase("unit-upgrade")) {
					// upgrade a unit
					UnitDefinition changes = new UnitDefinition(resourceTypes());
					ArrayList<Integer> cost = new ArrayList<Integer>();
					for (int j = 0; j < resourceTypes(); j++) {
						cost.add(0);
					}
					
					for (int j = 0; j < child.getChildren().size(); j++) {
						Element stat = (Element)child.getChildren().get(j);
						if (stat.getName().equalsIgnoreCase("cost")) {
							for (int k = 0; k < stat.getChildren().size(); k++) {
								Element resource_cost = (Element)stat.getChildren().get(k);
								if (resource_cost.getName().equalsIgnoreCase("resource")) {
									int resourceType = getInt(resource_cost, "type");
									if (resourceType >= 0 && resourceType < resourceTypes()) {
										cost.set(resourceType, getInt(resource_cost, "cost"));
									}
								}
							}
						} else if (stat.getName().equalsIgnoreCase("changes")) {
							for (int k = 0; k < stat.getChildren().size(); k++) {
								Element change = (Element)stat.getChildren().get(k);
								if (change.getName().equalsIgnoreCase("stats")) {
									changes.hp = getInt(change, "hp");
									changes.vision = getInt(change, "vision");
									changes.produce_speed = getInt(change, "produce_speed");
									changes.attack_range = getInt(change, "attack_range");
									changes.attack_min = getInt(change, "attack_min");
									changes.attack_max = getInt(change, "attack_max");
									changes.move_speed = getInt(change, "move_speed");
									changes.attack_speed = getInt(change, "attack_speed");
								} else if (change.getName().equalsIgnoreCase("cost")) {
									for (int l = 0; l < stat.getChildren().size(); l++) {
										Element resource_cost = (Element)stat.getChildren().get(k);
										if (resource_cost.getName().equalsIgnoreCase("resource")) {
											int resourceType = getInt(resource_cost, "type");
											if (resourceType >= 0 && resourceType < resourceTypes()) {
												changes.cost.set(resourceType, getInt(resource_cost, "cost"));
											}
										}
									}
								}
							}
						}
					}
					
					unit_upgrades.get(0).add(new UnitUpgrade(changes, cost, getInt(child, "time"), getInt(child, "type")));
					
				} else if (child.getName().equalsIgnoreCase("building-upgrade")) {
					// upgrade a building
					UnitDefinition changes = new UnitDefinition(resourceTypes());
					ArrayList<Integer> cost = new ArrayList<Integer>();
					for (int j = 0; j < resourceTypes(); j++) {
						cost.add(0);
					}
					
					for (int j = 0; j < child.getChildren().size(); j++) {
						Element stat = (Element)child.getChildren().get(j);
						if (stat.getName().equalsIgnoreCase("cost")) {
							for (int k = 0; k < stat.getChildren().size(); k++) {
								Element resource_cost = (Element)stat.getChildren().get(k);
								if (resource_cost.getName().equalsIgnoreCase("resource")) {
									int resourceType = getInt(resource_cost, "type");
									if (resourceType >= 0 && resourceType < resourceTypes()) {
										cost.set(resourceType, getInt(resource_cost, "cost"));
									}
								}
							}
						} else if (stat.getName().equalsIgnoreCase("changes")) {
							for (int k = 0; k < stat.getChildren().size(); k++) {
								Element change = (Element)stat.getChildren().get(k);
								if (change.getName().equalsIgnoreCase("stats")) {
									changes.hp = getInt(change, "hp");
									changes.vision = getInt(change, "vision");
									changes.produce_speed = getInt(change, "produce_speed");
								} else if (change.getName().equalsIgnoreCase("cost")) {
									for (int l = 0; l < stat.getChildren().size(); l++) {
										Element resource_cost = (Element)stat.getChildren().get(k);
										if (resource_cost.getName().equalsIgnoreCase("resource")) {
											int resourceType = getInt(resource_cost, "type");
											if (resourceType >= 0 && resourceType < resourceTypes()) {
												changes.cost.set(resourceType, getInt(resource_cost, "cost"));
											}
										}
									}
								}
							}
						}
					}
					
					building_upgrades.get(0).add(new UnitUpgrade(changes, cost, getInt(child, "time"), getInt(child, "type")));
					
				}
			}
		}
	}
	
	/**
	 * Returns how many resource types there are
	 * @return
	 */
	public int resourceTypes() {
		return resource_defs.get(0).size();
	}
	
	/**
	 * Makes a new resource field
	 * @param units the unit list to add this to
	 * @param stats the stats list to add this to
	 * @param type
	 * @param player
	 * @param x
	 * @param y
	 * @param amount
	 */
	public void makeResource(ArrayList<Unit> units, ArrayList<UnitStats> stats, int type, int player, int x, int y, int amount) {
		if (type >= 0 && type < resourceTypes()) {
			UnitStats resource_stats = new UnitStats(player, x, y, resource_defs.get(player+1).get(type));
			resource_stats.resources = amount;
			Unit resource = new Unit(resource_stats);
			units.add(resource);
			stats.add(resource_stats);
		}
	}
	
	/**
	 * Makes a new unit
	 * @param units
	 * @param stats
	 * @param type
	 * @param player
	 * @param x
	 * @param y
	 */
	public void makeUnit(ArrayList<Unit> units, ArrayList<UnitStats> stats, int type, int player, int x, int y) {
		if (type >= 0 && type < unit_defs.get(player+1).size()) {
			UnitStats unit_stats = new UnitStats(player, x, y, unit_defs.get(player+1).get(type));
			Unit unit = new Unit(unit_stats);
			units.add(unit);
			stats.add(unit_stats);
		} else {
			System.out.println(type+" is not a valid unit type");
		}
	}
	
	/**
	 * Makes a new building
	 * @param units
	 * @param stats
	 * @param type
	 * @param player
	 * @param x
	 * @param y
	 */
	public void makeBuilding(ArrayList<Unit> units, ArrayList<UnitStats> stats, int type, int player, int x, int y) {
		if (type >= 0 && type < building_defs.get(player+1).size()) {
			UnitStats unit_stats = new UnitStats(player, x, y, building_defs.get(player+1).get(type));
			Unit unit = new Unit(unit_stats);
			units.add(unit);
			stats.add(unit_stats);
		}
	}
	
	/**
	 * Sets the number of players
	 * @param players how many players there are
	 */
	public void setPlayers(int players) {
		for (int i = 0; i < players; i++) {
			resource_defs.add(new ArrayList<UnitDefinition>());
			unit_defs.add(new ArrayList<UnitDefinition>());
			building_defs.add(new ArrayList<UnitDefinition>());
			
			unit_upgrades.add(new ArrayList<UnitUpgrade>());
			building_upgrades.add(new ArrayList<UnitUpgrade>());
			
			for (int j = 0; j < resource_defs.get(0).size(); j++) {
				resource_defs.get(i+1).add(resource_defs.get(0).get(j).clone());
			}
			for (int j = 0; j < unit_defs.get(0).size(); j++) {
				unit_defs.get(i+1).add(unit_defs.get(0).get(j).clone());
			}
			for (int j = 0; j < building_defs.get(0).size(); j++) {
				building_defs.get(i+1).add(building_defs.get(0).get(j).clone());
			}
			
			for (int j = 0; j < unit_upgrades.get(0).size(); j++) {
				unit_upgrades.get(i+1).add(unit_upgrades.get(0).get(j).clone());
			}
			for (int j = 0; j < building_upgrades.get(0).size(); j++) {
				building_upgrades.get(i+1).add(unit_upgrades.get(0).get(j).clone());
			}
		}
	}
	
	/**
	 * gets an int from the xml
	 * @param e
	 * @param attr
	 * @return
	 */
	private int getInt(Element e, String attr) {
		try {
			return Integer.parseInt(e.getAttributeValue(attr));
		} catch (NumberFormatException x) {
			return 0;
		}
	}
	
	/**
	 * Returns the score for a unit
	 * @param unit
	 * @return
	 */
	public int getScore(UnitStats unit) {
		int score = 0;
		for (int i = 0; i < unit.definition.cost.size(); i++) {
			score += resource_scores.get(i)*unit.definition.cost.get(i);
		}
		return score;
	}
	
	/**
	 * Gets a copy of the unit definitions for the specified player
	 * @param player which player
	 * @return
	 */
	public ArrayList<UnitDefinition> getCopyOfUnits(int player) {
		ArrayList<UnitDefinition> defs = new ArrayList<UnitDefinition>();
		
		if (player >= 0 && player < unit_defs.size()) {
			player++;
			for (int i = 0; i < unit_defs.get(player).size(); i++) {
				defs.add(unit_defs.get(player).get(i).clone());
			}
		}
		
		return defs;
	}
	
	/**
	 * Gets a copy of the building definitions for the specified player
	 * @param player which player
	 * @return
	 */
	public ArrayList<UnitDefinition> getCopyOfBuildings(int player) {
		ArrayList<UnitDefinition> defs = new ArrayList<UnitDefinition>();
		
		if (player >= 0 && player < building_defs.size()) {
			player++;
			for (int i = 0; i < building_defs.get(player).size(); i++) {
				defs.add(building_defs.get(player).get(i).clone());
			}
		}
		
		return defs;
	}
	
	/**
	 * Executes a unit upgrade
	 * @param player
	 * @param upgrade_id
	 * @return
	 */
	public boolean upgrade(int player, int upgrade_id) {
		player++;
		if (player >= 0 && player < unit_upgrades.size()) {
			for (int i = 0; i < unit_upgrades.get(player).size(); i++) {
				UnitUpgrade upgrade = unit_upgrades.get(player).get(i);
				if (upgrade.getID() == upgrade_id) {
					upgrade.upgrade(unit_defs.get(player).get(upgrade.getUnit()));
					unit_upgrades.get(player).remove(i);
					return true;
				}
			}
			for (int i = 0; i < building_upgrades.get(player).size(); i++) {
				UnitUpgrade upgrade = building_upgrades.get(player).get(i);
				if (upgrade.getID() == upgrade_id) {
					upgrade.upgrade(building_defs.get(player).get(upgrade.getUnit()));
					building_upgrades.get(player).remove(i);
					return true;
				}
			}
		}
		
		return false;
	}
	
	private UnitDefinitionManager() {
		
	}
	
	/**
	 * 
	 * @return
	 */
	public UnitDefinitionManager copy() {
		UnitDefinitionManager u = new UnitDefinitionManager();
		
		u.resource_defs = new ArrayList<ArrayList<UnitDefinition> >();
		u.unit_defs = new ArrayList<ArrayList<UnitDefinition> >();
		u.building_defs = new ArrayList<ArrayList<UnitDefinition> >();
		
		u.unit_upgrades = new ArrayList<ArrayList<UnitUpgrade> >();
		u.building_upgrades = new ArrayList<ArrayList<UnitUpgrade> >();
		
		u.resource_scores = new ArrayList<Integer>();
		
		for (int i = 0; i < resource_defs.size(); i++) {
			u.resource_defs.add(new ArrayList<UnitDefinition>());
			for (int j = 0; j < resource_defs.get(i).size(); j++) {
				u.resource_defs.get(i).add(resource_defs.get(i).get(j).clone());
			}
		}
		for (int i = 0; i < unit_defs.size(); i++) {
			u.unit_defs.add(new ArrayList<UnitDefinition>());
			for (int j = 0; j < unit_defs.get(i).size(); j++) {
				u.unit_defs.get(i).add(unit_defs.get(i).get(j).clone());
			}
		}
		for (int i = 0; i < building_defs.size(); i++) {
			u.building_defs.add(new ArrayList<UnitDefinition>());
			for (int j = 0; j < building_defs.get(i).size(); j++) {
				u.building_defs.get(i).add(building_defs.get(i).get(j).clone());
			}
		}
		
		for (int i = 0; i < unit_upgrades.size(); i++) {
			u.unit_upgrades.add(new ArrayList<UnitUpgrade>());
			for (int j = 0; j < unit_upgrades.get(i).size(); j++) {
				u.unit_upgrades.get(i).add(unit_upgrades.get(i).get(j).clone());
			}
		}
		for (int i = 0; i < building_upgrades.size(); i++) {
			u.building_upgrades.add(new ArrayList<UnitUpgrade>());
			for (int j = 0; j < building_upgrades.get(i).size(); j++) {
				u.building_upgrades.get(i).add(building_upgrades.get(i).get(j).clone());
			}
		}
		
		for (int i = 0; i < resource_scores.size(); i++) {
			u.resource_scores.add(resource_scores.get(i));
		}
		
		return u;
	}
}
