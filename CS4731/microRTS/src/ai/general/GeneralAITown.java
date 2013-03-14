package ai.general;

import java.util.ArrayList;

import rts.GameState;
import rts.units.UnitAction;

/**
 * \brief A group of buildings that are all together. Generally, towns are a stockpile, resources, and other buildings nearby
 *
 */
public class GeneralAITown extends GeneralAIObject {
	private static final int CITY_LIMIT2 = 25; /**< all structures should be within this many squares (root) of each other */
	private static final int CITY_LIMIT = 5; /**< root CITY_LIMIT2 */
	private static final int LOCATION_SCOUTING = 1000; /**< how many times to try and scout a location */
	public static final int NO_VACANCY = -1; /**< could not find a place in this town to build */
	
	public ArrayList<GeneralAIFarm> farms; /**< the farms belonging to this town */
	public ArrayList<GeneralAIUnit> buildings; /**< the buildings belonging to this town */
	public ArrayList<GeneralAIEnemy> enemy_buildings; /**< buildings belonging to the enemy */
	
	public int owner; /**< id of whoever owns this town */

	public int defenders;
	public int defenders_wanted; /**< how many defenders this town wants */
	private boolean[] building_requested; /**< whether or not we've requested a location for each building */
	
	/**
	 * Constructs a new town around a building
	 * @param building
	 * @param building_count how many building types there are
	 */
	public GeneralAITown(GeneralAIUnit building, int building_count) {		
		farms = new ArrayList<GeneralAIFarm>();
		buildings = new ArrayList<GeneralAIUnit>();
		enemy_buildings = new ArrayList<GeneralAIEnemy>();
		buildings.add(building);
		owner = building.stats.getPlayer();
		defenders_wanted = 2;
		defenders = 0;
		
		building_requested = new boolean[building_count];
		for (int i = 0; i < building_count; i++) {
			building_requested[i] = false;
		}
	}
	
	/**
	 * Constructs a new town around a farm
	 * @param farm
	 * @param building_count how many building types there are
	 */
	public GeneralAITown(GeneralAIFarm farm, int building_count) {
		farms = new ArrayList<GeneralAIFarm>();
		buildings = new ArrayList<GeneralAIUnit>();
		enemy_buildings = new ArrayList<GeneralAIEnemy>();
		farms.add(farm);
		owner = -1;
		defenders_wanted = 0;
		defenders = 0;
		
		building_requested = new boolean[building_count];
		for (int i = 0; i < building_count; i++) {
			building_requested[i] = false;
		}
	}
	
	/**
	 * Constructs a new town around an enemy building
	 * @param enemy
	 * @param building_count how many building types there are
	 */
	public GeneralAITown(GeneralAIEnemy enemy, int building_count) {
		farms = new ArrayList<GeneralAIFarm>();
		buildings = new ArrayList<GeneralAIUnit>();
		enemy_buildings = new ArrayList<GeneralAIEnemy>();
		enemy_buildings.add(enemy);
		owner = enemy.stats.getPlayer();
		defenders_wanted = 0;
		defenders = 0;
		
		building_requested = new boolean[building_count];
		for (int i = 0; i < building_count; i++) {
			building_requested[i] = false;
		}
	}
	
	/**
	 * Adds an object to this town (maybe)
	 * @param obj
	 * @return true if the object was within city limits and added, otherwise false
	 */
	public boolean add(GeneralAIFarm farm) {
		for (int i = 0; i < farms.size(); i++) {
			if ((farms.get(i).resources.getX()-farm.resources.getX())*(farms.get(i).resources.getX()-farm.resources.getX())+(farms.get(i).resources.getY()-farm.resources.getY())*(farms.get(i).resources.getY()-farm.resources.getY()) <= CITY_LIMIT2) {
				farms.add(farm);
				return true;
			}
		}
		for (int i = 0; i < buildings.size(); i++) {
			if ((buildings.get(i).stats.getX()-farm.resources.getX())*(buildings.get(i).stats.getX()-farm.resources.getX())+(buildings.get(i).stats.getY()-farm.resources.getY())*(buildings.get(i).stats.getY()-farm.resources.getY()) <= CITY_LIMIT2) {
				farms.add(farm);
				return true;
			}
		}
		for (int i = 0; i < enemy_buildings.size(); i++) {
			if ((enemy_buildings.get(i).stats.getX()-farm.resources.getX())*(enemy_buildings.get(i).stats.getX()-farm.resources.getX())+(enemy_buildings.get(i).stats.getY()-farm.resources.getY())*(enemy_buildings.get(i).stats.getY()-farm.resources.getY()) <= CITY_LIMIT2) {
				farms.add(farm);
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Adds an object to this town (maybe)
	 * @param obj
	 * @return true if the object was within city limits and added, otherwise false
	 */
	public boolean add(GeneralAIUnit building) {
		defenders_wanted += 1;
		for (int i = 0; i < farms.size(); i++) {
			if ((farms.get(i).resources.getX()-building.stats.getX())*(farms.get(i).resources.getX()-building.stats.getX())+(farms.get(i).resources.getY()-building.stats.getY())*(farms.get(i).resources.getY()-building.stats.getY()) <= CITY_LIMIT2) {
				buildings.add(building);
				if (buildings.size() > enemy_buildings.size()) {
					owner = building.stats.getPlayer();
				}
				return true;
			}
		}
		for (int i = 0; i < buildings.size(); i++) {
			if ((buildings.get(i).stats.getX()-building.stats.getX())*(buildings.get(i).stats.getX()-building.stats.getX())+(buildings.get(i).stats.getY()-building.stats.getY())*(buildings.get(i).stats.getY()-building.stats.getY()) <= CITY_LIMIT2) {
				buildings.add(building);
				if (buildings.size() > enemy_buildings.size()) {
					owner = building.stats.getPlayer();
				}
				return true;
			}
		}
		for (int i = 0; i < enemy_buildings.size(); i++) {
			if ((enemy_buildings.get(i).stats.getX()-building.stats.getX())*(enemy_buildings.get(i).stats.getX()-building.stats.getX())+(enemy_buildings.get(i).stats.getY()-building.stats.getY())*(enemy_buildings.get(i).stats.getY()-building.stats.getY()) <= CITY_LIMIT2) {
				buildings.add(building);
				if (buildings.size() > enemy_buildings.size()) {
					owner = building.stats.getPlayer();
				}
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Adds an object to this town (maybe)
	 * @param obj
	 * @return true if the object was within city limits and added, otherwise false
	 */
	public boolean add(GeneralAIEnemy enemy) {
		for (int i = 0; i < farms.size(); i++) {
			if ((farms.get(i).resources.getX()-enemy.stats.getX())*(farms.get(i).resources.getX()-enemy.stats.getX())+(farms.get(i).resources.getY()-enemy.stats.getY())*(farms.get(i).resources.getY()-enemy.stats.getY()) <= CITY_LIMIT2) {
				enemy_buildings.add(enemy);
				if (buildings.size() < enemy_buildings.size()) {
					owner = enemy.stats.getPlayer();
				}
				return true;
			}
		}
		for (int i = 0; i < enemy_buildings.size(); i++) {
			if ((enemy_buildings.get(i).stats.getX()-enemy.stats.getX())*(enemy_buildings.get(i).stats.getX()-enemy.stats.getX())+(enemy_buildings.get(i).stats.getY()-enemy.stats.getY())*(enemy_buildings.get(i).stats.getY()-enemy.stats.getY()) <= CITY_LIMIT2) {
				enemy_buildings.add(enemy);
				if (buildings.size() < enemy_buildings.size()) {
					owner = enemy.stats.getPlayer();
				}
				return true;
			}
		}
		for (int i = 0; i < buildings.size(); i++) {
			if ((buildings.get(i).stats.getX()-enemy.stats.getX())*(buildings.get(i).stats.getX()-enemy.stats.getX())+(buildings.get(i).stats.getY()-enemy.stats.getY())*(buildings.get(i).stats.getY()-enemy.stats.getY()) <= CITY_LIMIT2) {
				enemy_buildings.add(enemy);
				if (buildings.size() < enemy_buildings.size()) {
					owner = enemy.stats.getPlayer();
				}
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Removes a farm from this town
	 * @param farm
	 * @return true if this farm was in this town, otherwise false
	 */
	public boolean remove(GeneralAIFarm farm) {
		for (int i = 0; i < farms.size(); i++) {
			if (farms.get(i).resources.getID() == farm.resources.getID()) {
				farms.remove(i);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Removes a building from this town
	 * @param building
	 * @return true if this building was in this town, otherwise false
	 */
	public boolean remove(GeneralAIUnit building) {
		defenders_wanted -= 2;
		for (int i = 0; i < buildings.size(); i++) {
			if (buildings.get(i).stats.getID() == building.stats.getID()) {
				buildings.remove(i);
				building_requested[building.stats.getType()] = false;
				if (buildings.size() < enemy_buildings.size()) {
					owner = enemy_buildings.get(0).stats.getPlayer();
				} else if (buildings.size() == 0 && enemy_buildings.size() == 0) {
					owner = -1;
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Removes a enemy building from this town
	 * @param building
	 * @return true if this building was in this town, otherwise false
	 */
	public boolean remove(GeneralAIEnemy enemy) {
		for (int i = 0; i < enemy_buildings.size(); i++) {
			if (enemy_buildings.get(i).stats.getID() == enemy.stats.getID()) {
				enemy_buildings.remove(i);
				if (buildings.size() > enemy_buildings.size()) {
					owner = buildings.get(0).stats.getPlayer();
				} else if (buildings.size() == 0 && enemy_buildings.size() == 0) {
					owner = -1;
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the population of the town
	 * @return
	 */
	public int population() {
		return farms.size()+buildings.size()+enemy_buildings.size();
	}
	
	/**
	 * Returns where we want to build a stock pile in this town
	 * @param product
	 * @param ai
	 * @return -1 if don't want a stockpile here (could be, already returned the wish location)
	 */
	public int get_location(GeneralAIProduction product, GeneralAI ai) {
		if (!product.def.is_stockpile_building && !has_stockpile() && farms.size() > 0) {
			return NO_VACANCY;
		}
		if (!building_requested[product.def.type]) {
			building_requested[product.def.type] = true;
			for (int i = 0; i < buildings.size(); i++) {
				if (buildings.get(i).stats.getType() == product.def.type) {
					return NO_VACANCY; // turns out we already have this building here
				}
			}
			// pick a spot for this building
			if (product.def.is_stockpile_building) {
				// build near resources
				int best_score = -1;
				int best = NO_VACANCY;
				if (farms.size() != 0) {
					int farmX = farms.get(0).resources.getX();
					int farmY = farms.get(0).resources.getY();
					for (int i = farmY-CITY_LIMIT; i <= farmY+CITY_LIMIT; i++) {
						if (i >= 0 && i < ai.state.getMapHeight()-1 && (i < farmY-1 || i > farmY+1)) {
							for (int j = farmX-CITY_LIMIT; j <= farmX+CITY_LIMIT; j++) {
								if (j >= 0 && j < ai.state.getMapWidth()-1 && (j < farmX-1 || j > farmX+1)) {
									if ((ai.exploration_manager.map[j+i*ai.state.getMapWidth()]&(GameState.MAP_WALL|GameState.MAP_NEUTRAL|GameState.MAP_FOG)) != 0) {
										continue; // can't build on a wall
									}
									if (((j > 0 && (ai.exploration_manager.map[j+i*ai.state.getMapWidth()-1]&GameState.MAP_WALL) != 0) || j == 0) &&
										((j < ai.state.getMapWidth()-1 && (ai.exploration_manager.map[j+i*ai.state.getMapWidth()+1]&GameState.MAP_WALL) != 0) || j == ai.state.getMapWidth()-1) &&
										((i > 0 && (ai.exploration_manager.map[j+(i-1)*ai.state.getMapWidth()]&GameState.MAP_WALL) != 0) || i == 0) &&
										((i < ai.state.getMapHeight()-1 && (ai.exploration_manager.map[j+(i+1)*ai.state.getMapWidth()]&GameState.MAP_WALL) != 0) || i == ai.state.getMapHeight()-1)) {
										continue; // there are no non-wall tiles adjacent to this spot
									}
									
									// check if there's already a building here
									boolean building_here = false;
									for (int k = 0; k < buildings.size(); k++) {
										if (buildings.get(k).stats.getX() == j && buildings.get(k).stats.getY() == i) {
											building_here = true;
											break;
										}
									}
									if (building_here) {
										continue;
									}
									for (int k = 0; k < enemy_buildings.size(); k++) {
										if (enemy_buildings.get(k).stats.getX() == j && enemy_buildings.get(k).stats.getY() == i) {
											building_here = true;
											break;
										}
									}
									if (building_here) {
										continue;
									}
									
									// finally, evaluate how good this location is
									int score = (j-farmX)*(j-farmX)+(i-farmY)*(i-farmY);
									for (int k = 1; k < farms.size(); k++) {
										int farmKX = farms.get(k).resources.getX();
										int farmKY = farms.get(k).resources.getY();
										score += (j-farmKX)*(j-farmKX)+(i-farmKY)*(i-farmKY);
									}
									if (best == NO_VACANCY || score < best_score) {
										best_score = score;
										best = i*ai.state.getMapWidth()+j;
									}
								}
							}
						}
					}
				}
				if (best != NO_VACANCY) {
					return best;
				}
			} 
			// default rule, build practically anywhere
			// pick a random spot within city limits
			for (int i = 0; i < LOCATION_SCOUTING; i++) {
				int location = (int)(Math.random()*ai.state.getMapWidth()*ai.state.getMapHeight());
				int locationX = location%ai.state.getMapWidth();
				int locationY = location/ai.state.getMapWidth();
				
				// confirm this is a valid build location
				if ((ai.exploration_manager.map[location]&(GameState.MAP_WALL|GameState.MAP_NEUTRAL|GameState.MAP_FOG)) != 0) {
					continue; // can't build on a wall
				}
				
				if (((locationX > 0 && (ai.exploration_manager.map[location-1]&GameState.MAP_WALL) != 0) || locationX == 0) &&
					((locationX < ai.state.getMapWidth()-1 && (ai.exploration_manager.map[location+1]&GameState.MAP_WALL) != 0) || locationX == ai.state.getMapWidth()-1) &&
					((locationY > 0 && (ai.exploration_manager.map[location-ai.state.getMapWidth()]&GameState.MAP_WALL) != 0) || locationY == 0) &&
					((locationY < ai.state.getMapHeight()-1 && (ai.exploration_manager.map[location+ai.state.getMapWidth()]&GameState.MAP_WALL) != 0) || locationY == ai.state.getMapHeight()-1)) {
					continue; // there are no non-wall tiles adjacent to this spot
				}
				
				if ((locationX > 0 && (ai.exploration_manager.map[location-1]&GameState.MAP_NEUTRAL) != 0) || 
					(locationX < ai.state.getMapWidth()-1 && (ai.exploration_manager.map[location+1]&GameState.MAP_NEUTRAL) != 0) || 
					(locationY > 0 && (ai.exploration_manager.map[location-ai.state.getMapWidth()]&GameState.MAP_NEUTRAL) != 0) || 
					(locationY < ai.state.getMapHeight()-1 && (ai.exploration_manager.map[location+ai.state.getMapWidth()]&GameState.MAP_NEUTRAL) != 0)) {
					continue; // there are resources next to this spot
				}
				
				// confirm this location is within city limits
				if (in_limits(location, ai.state.getMapWidth())) {
					return location;
				}
			}
			building_requested[product.def.type] = false;
		}
		return NO_VACANCY;
	}
	
	/**
	 * Checks if a location is within the city limits
	 * @param location the location to check
	 * @param map_w width of the map
	 * @return true or false
	 */
	public boolean in_limits(int location, int map_w) {
		int locationX = location%map_w;
		int locationY = location/map_w;
		for (int j = 0; j < farms.size(); j++) {
			if ((locationX-farms.get(j).resources.getX())*(locationX-farms.get(j).resources.getX())+(locationY-farms.get(j).resources.getY())*(locationY-farms.get(j).resources.getY()) < CITY_LIMIT2) {
				return true;
			}
		}
		for (int j = 0; j < buildings.size(); j++) {
			if ((locationX-buildings.get(j).stats.getX())*(locationX-buildings.get(j).stats.getX())+(locationY-buildings.get(j).stats.getY())*(locationY-buildings.get(j).stats.getY()) < CITY_LIMIT2) {
				return true;
			}
		}
		for (int j = 0; j < enemy_buildings.size(); j++) {
			if ((locationX-enemy_buildings.get(j).stats.getX())*(locationX-enemy_buildings.get(j).stats.getX())+(locationY-enemy_buildings.get(j).stats.getY())*(locationY-enemy_buildings.get(j).stats.getY()) < CITY_LIMIT2) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if there is a stockpile in this town
	 * @return
	 */
	public boolean has_stockpile() {
		for (int i = 0; i < buildings.size(); i++) {
			if (buildings.get(i).stats.isStockpile()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void update_orders(GeneralAIUnit unit, GeneralAI ai) {
		unit.clearActions(ai.traffic_map);
		order_unit(unit, ai);
	}

	@Override
	public void order_unit(GeneralAIUnit unit, GeneralAI ai) {
		if (farms.size() == 0) { // nothing really to protect...
			remove(unit, ai);
			return;
		}
		
		// randomly patrol, or attack nearby enemies
		boolean patrol = true;
		// see if enemy is close by
		int distance = GeneralAI.DISTANCE_IGNORE;
		GeneralAIEnemy enemy = null;
		for (int j = 0; j < ai.attack_manager.enemies.size(); j++) {
			int d = ai.attack_manager.enemies.get(j).distance(unit, ai);
			if (d != GeneralAI.DISTANCE_IGNORE && (distance == GeneralAI.DISTANCE_IGNORE || d < distance)) {
				distance = d;
				enemy = ai.attack_manager.enemies.get(j);
			}
		}
		if (distance != GeneralAI.DISTANCE_IGNORE && distance < CITY_LIMIT2) {
			patrol = false;
			// attack this enemy!!!
			// check if we can just attack...
			for (int i = 0; i < unit.stats.getActions().size(); i++) {
				UnitAction action = unit.stats.getActions().get(i);
				if (action.getType() == UnitAction.ATTACK && action.getTargetX() == enemy.stats.getX() && action.getTargetY() == enemy.stats.getY()) {
					unit.addAction(new UnitAction(unit.stats, UnitAction.ATTACK, enemy.stats.getX(), enemy.stats.getY(), -1), ai.traffic_map, unit.stats.getX()+unit.stats.getY()*ai.state.getMapWidth(), ai.current_turn, -1);
					return;
				}
			}
			// else try and path to enemy
			ArrayList<Integer> openings = new ArrayList<Integer>();
			for (int j = enemy.stats.getY()-unit.stats.getAttackRange(); j <= enemy.stats.getY()+unit.stats.getVision(); j++) {
				if (j >= 0 && j < ai.state.getMapHeight()) {  
					for (int k = enemy.stats.getX()-unit.stats.getAttackRange(); k <= enemy.stats.getX()+unit.stats.getAttackRange(); k++) {
						if (k >= 0 && k < ai.state.getMapWidth() && (j-enemy.stats.getY())*(j-enemy.stats.getY())+(k-enemy.stats.getX())*(k-enemy.stats.getX()) < unit.stats.getAttackRange()*unit.stats.getAttackRange()) {
							openings.add(k+j*ai.state.getMapWidth());
						}
					}
				}
			}
			
			ArrayList<Integer[]> rpath = ai.get_path(unit.stats, unit.stats.getX()+unit.stats.getY()*ai.state.getMapWidth(), ai.current_turn, openings);
			
			if (rpath != null) { // is possible to reach goal				
				boolean there = false;
				if (rpath.size() == 0) {
					rpath.add(new Integer[]{unit.stats.getX()+unit.stats.getY()*ai.state.getMapWidth(), ai.current_turn});
					there = true;
				}
				
				// set order queue
				if (!there) {
					for (int i = rpath.size()-1; i >= 0; i--) {
						unit.addAction(new UnitAction(unit.stats, UnitAction.MOVE, rpath.get(i)[0]%ai.state.getMapWidth(), rpath.get(i)[0]/ai.state.getMapWidth(),-1), ai.traffic_map, rpath.get(i)[0], rpath.get(i)[1], rpath.get(i)[1]+unit.stats.getMoveSpeed());
					}
				}
				int now_at = rpath.get(0)[0];
				int now_start = rpath.get(0)[1];
				
				//System.out.println("adding ATTACK");
				unit.addAction(new UnitAction(unit.stats, UnitAction.ATTACK, enemy.stats.getX(), enemy.stats.getY(), -1), ai.traffic_map, now_at, now_start, -1);
			} else {
				// oh no, can't reach this enemey!?
				// go back to patrol
				patrol = true;
			}
		} 
		if (patrol && in_limits(unit.stats.getX()+unit.stats.getY()*ai.state.getMapWidth(), ai.state.getMapWidth())) { // patrol
			// patrol the town
			// pick a random point within city limits...
			int[] dir = new int[]{0, 0, 0, 0};
			int t = 0;
			for (int i = 0; i < unit.stats.getActions().size(); i++) {
				UnitAction action = unit.stats.getActions().get(i);
				if (action.getType() == UnitAction.MOVE) {
					if (action.getTargetX() < unit.stats.getX()) {
						dir[0] += distance(unit, ai, 1, 0);
						t += dir[0];
					} else if (action.getTargetX() > unit.stats.getX()) {
						dir[1] += distance(unit, ai, -1, 0);
						t += dir[1];
					} else if (action.getTargetY() < unit.stats.getY()) {
						dir[2] += distance(unit, ai, 0, 1);
						t += dir[2];
					} else if (action.getTargetY() > unit.stats.getY()) {
						dir[3] += distance(unit, ai, 0, -1);
						t += dir[3];
					}
				}
			}
			int r = (int)(Math.random()*t);
			for (int i = 0; i < dir.length; i++) {
				r -= dir[i];
				if (r < 0) {
					switch (i) {
					case 0:
						unit.addAction(new UnitAction(unit.stats, UnitAction.MOVE, unit.stats.getX()-1, unit.stats.getY(), -1), ai.traffic_map, unit.stats.getX()-1+unit.stats.getY()*ai.state.getMapWidth(), ai.current_turn, ai.current_turn+unit.stats.getMoveSpeed());
						break;
					case 1:
						unit.addAction(new UnitAction(unit.stats, UnitAction.MOVE, unit.stats.getX()+1, unit.stats.getY(), -1), ai.traffic_map, unit.stats.getX()+1+unit.stats.getY()*ai.state.getMapWidth(), ai.current_turn, ai.current_turn+unit.stats.getMoveSpeed());
						break;
					case 2:
						unit.addAction(new UnitAction(unit.stats, UnitAction.MOVE, unit.stats.getX(), unit.stats.getY()-1, -1), ai.traffic_map, unit.stats.getX()+(unit.stats.getY()-1)*ai.state.getMapWidth(), ai.current_turn, ai.current_turn+unit.stats.getMoveSpeed());
						break;
					default:
						unit.addAction(new UnitAction(unit.stats, UnitAction.MOVE, unit.stats.getX(), unit.stats.getY()+1, -1), ai.traffic_map, unit.stats.getX()+(unit.stats.getY()+1)*ai.state.getMapWidth(), ai.current_turn, ai.current_turn+unit.stats.getMoveSpeed());
						break;
					}
					break;
				}
			}
		} else if (patrol) {
			// return to the city
			int location = -1;
			for (int i = 0; i < LOCATION_SCOUTING; i++) {
				location = (int)(Math.random()*ai.state.getMapWidth()*ai.state.getMapHeight());
				int locationX = location%ai.state.getMapWidth();
				int locationY = location/ai.state.getMapWidth();
				
				// confirm this is a valid build location
				if ((ai.exploration_manager.map[location]&(GameState.MAP_WALL|GameState.MAP_NEUTRAL|GameState.MAP_FOG)) != 0) {
					continue; // can't build on a wall
				}
				
				if (((locationX > 0 && (ai.exploration_manager.map[location-1]&GameState.MAP_WALL) != 0) || locationX == 0) &&
					((locationX < ai.state.getMapWidth()-1 && (ai.exploration_manager.map[location+1]&GameState.MAP_WALL) != 0) || locationX == ai.state.getMapWidth()-1) &&
					((locationY > 0 && (ai.exploration_manager.map[location-ai.state.getMapWidth()]&GameState.MAP_WALL) != 0) || locationY == 0) &&
					((locationY < ai.state.getMapHeight()-1 && (ai.exploration_manager.map[location+ai.state.getMapWidth()]&GameState.MAP_WALL) != 0) || locationY == ai.state.getMapHeight()-1)) {
					continue; // there are no non-wall tiles adjacent to this spot
				}
				
				if ((locationX > 0 && (ai.exploration_manager.map[location-1]&GameState.MAP_NEUTRAL) != 0) || 
					(locationX < ai.state.getMapWidth()-1 && (ai.exploration_manager.map[location+1]&GameState.MAP_NEUTRAL) != 0) || 
					(locationY > 0 && (ai.exploration_manager.map[location-ai.state.getMapWidth()]&GameState.MAP_NEUTRAL) != 0) || 
					(locationY < ai.state.getMapHeight()-1 && (ai.exploration_manager.map[location+ai.state.getMapWidth()]&GameState.MAP_NEUTRAL) != 0)) {
					continue; // there are resources next to this spot
				}
				
				// confirm this location is within city limits
				if (in_limits(location, ai.state.getMapWidth())) {
					break;
				}
			}
			if (location != -1) {
				ArrayList<Integer> openings = new ArrayList<Integer>();
				openings.add(location);
				ArrayList<Integer[]> rpath = ai.get_path(unit.stats, unit.stats.getX()+unit.stats.getY()*ai.state.getMapWidth(), ai.current_turn, openings);
				
				if (rpath != null) { // is possible to reach goal				
					boolean there = false;
					if (rpath.size() == 0) {
						rpath.add(new Integer[]{unit.stats.getX()+unit.stats.getY()*ai.state.getMapWidth(), ai.current_turn});
						there = true;
					}
					
					// set order queue
					if (!there) {
						for (int i = rpath.size()-1; i >= 0; i--) {
							unit.addAction(new UnitAction(unit.stats, UnitAction.MOVE, rpath.get(i)[0]%ai.state.getMapWidth(), rpath.get(i)[0]/ai.state.getMapWidth(),-1), ai.traffic_map, rpath.get(i)[0], rpath.get(i)[1], rpath.get(i)[1]+unit.stats.getMoveSpeed());
						}
					}
				} else {
					// impossible to reach town !?
				}
			} else {
				// impossible to reach town !?
			}
		}
	}
	
	public int distance(GeneralAIUnit unit, GeneralAI ai, int dx, int dy) {
		if (ai.player_id != owner) {
			return GeneralAI.DISTANCE_IGNORE;
		}
		if (buildings.size() > 0) {
			return (unit.stats.getX()+dx-buildings.get(0).stats.getX())*(unit.stats.getX()+dx-buildings.get(0).stats.getX())+(unit.stats.getY()+dy-buildings.get(0).stats.getY())*(unit.stats.getY()+dy-buildings.get(0).stats.getY());
		}
		return 0; // erhmmm...???
	}

	@Override
	public int distance(GeneralAIUnit unit, GeneralAI ai) {
		if (ai.player_id != owner || defenders >= defenders_wanted || farms.size() == 0) {
			return GeneralAI.DISTANCE_IGNORE;
		}
		if (buildings.size() > 0) {
			return (unit.stats.getX()-buildings.get(0).stats.getX())*(unit.stats.getX()-buildings.get(0).stats.getX())+(unit.stats.getY()-buildings.get(0).stats.getY())*(unit.stats.getY()-buildings.get(0).stats.getY());
		}
		return 0; // erhmmm...???
	}

	@Override
	public void action_succeeded(GeneralAIUnit unit, GeneralAI ai, int type) {
		// NaN
	}

	@Override
	public void remove(GeneralAIUnit unit, GeneralAI ai) {
		unit.clearActions(ai.traffic_map);
		unit.object = null;
		defenders--;
	}
	
	public void add_defender() {
		defenders++;
	}
}
