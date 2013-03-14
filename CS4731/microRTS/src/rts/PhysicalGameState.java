/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rts;

import java.util.ArrayList;
import rts.units.*;

import org.jdom.Element;


/**
 * \brief Runs all of the logic of the game
 * @author santi, modified Jeff Bernard
 */
public class PhysicalGameState {    
	@Deprecated
	public static final int TERRAIN_NONE = 0;
	@Deprecated
	public static final int TERRAIN_WALL = 1;
	
    public int width = 8;
    public int height = 8;
    public int terrain[] = null;
    public boolean fog; /**< whether or not we're playing with fog of war */
    
    public ArrayList<ArrayList<Integer> > teams; /**< which teams have which players */
    
    // last element is neutral army
    public ArrayList<ArrayList<Unit> > armies; /**< player x list of units */
    public ArrayList<UnitStats> armyStats; /**< player x list of units */
    private ArrayList<UnitStats> corpses; /**< dead people */
    public ArrayList<ArrayList<Integer> > resources; /**< the resources for each player */
    public ArrayList<Integer> scores; /**< how many points each player currently has */
    
    // stats
    public ArrayList<Integer> kills; /**< how many kills each player has made */
    public ArrayList<Integer> deaths; /**< how many deaths each player has suffered */
    
    public UnitStats unit_map[];
    public UnitStats reserved;
    
    public UnitDefinitionManager unitDefinitions; /**< the unit definitions manager */
    
    private ArrayList<Long> timeLimits;
    
    public int current_player;
    
    /**
     * Returns the number of players (minus the neutral player)
     * @return armies.size()-1
     */
    public int numberOfPlayers() {
    	return armies.size()-1;
    }
    
    /**
     * Returns the id of the winning team
     * @return the id of the winning team, or -1 on a draw
     */
    public int winner() {
    	int winner = -1;
    	for (int i = 0; i < teams.size(); i++) {
    		int men = 0;
    		for (int j = 0; j < teams.get(i).size(); j++) {
    			men += armies.get(teams.get(i).get(j)).size();
    		}
    		if (men != 0) {
    			if (winner == -1) {
    				winner = i;
    			} else {
    				return -1;
    			}
    		}
    	}
    	return winner;
    }

    /**
     * Returns the team with the most points
     * @return -1 on a draw on score
     */
    public int winnerByScore() {
    	int winner = -1;
    	int winningScore = -1;
    	boolean draw = false;
    	
    	for (int i = 0; i < teams.size(); i++) {
    		int score = 0;
    		for (int j = 0; j < teams.get(i).size(); j++) {
    			score += scores.get(teams.get(i).get(j));
    		}
    		if (score == winningScore) {
    			draw = true;
    		} else if (score > winningScore) {
    			winningScore = score;
    			winner = i;
    			draw = false;
    		}
    	}
    	if (draw) {
    		return -1;
    	}
    	return winner;
    }
    
    /**
     * Determines whether or not the game is over.
     *@retun true if yesm, otherwise false
     */
    public boolean gameover() {
    	boolean done = true;
    	for (int i = 0; i < teams.size(); i++) {
    		int men = 0;
    		for (int j = 0; j < teams.get(i).size(); j++) {
    			men += armies.get(teams.get(i).get(j)).size();
    		}
    		if (men != 0) {
    			done = false;
    			break;
    		}
    	}
    	if (done || winner() != -1) {
    		return true;
    	}
    	return false;
    }
    
    /**
     * Cycles the game state
     * @return true if the game is over, otherwise false
     */
    public boolean cycle() {
    	for (int i = 0; i < armyStats.size(); i++) {
    		current_player = armyStats.get(i).player;
			UnitAction action = armyStats.get(i).action;
			if (armyStats.get(i).player != -1 && action != null) {
				if (action.getTimestamp() <= timeLimits.get(armyStats.get(i).player) && action.validate(armyStats.get(i), this)) {
					// action must be issued in time
					if (action.ready()) {
						if (action.getType() == UnitAction.BUILD) {
							if (unit_map[action.getTargetX()+action.getTargetY()*width] == null && terrain[action.getTargetX()+action.getTargetY()*width] == TERRAIN_NONE) {
								unit_map[action.getTargetX()+action.getTargetY()*width] = reserved;
							}
						} else {
							action.execute(armyStats.get(i), this);
						}
						
						// prepare to accept another action
						//armyStats.get(i).action = null;
					}
					if (action.cooldown()) {
						if (action.getType() == UnitAction.BUILD){
							/*if (action.getBuild() == 1 && !armyStats.get(i).definition.is_building) {
								System.out.println("built a soldier office: ");
							}*/
							unit_map[action.getTargetX()+action.getTargetY()*width] = null;
							action.execute(armyStats.get(i), this);
						}
						armyStats.get(i).action = null;
					}
				} else {
					// action was issued too late
					//System.out.println("action too late for "+i);
					armyStats.get(i).action = null;
				}
			}
    	}
    	
    	// check for dead units
    	for (int i = 0; i < armyStats.size(); i++) {
    		if (armyStats.get(i).definition.is_resources) {
    			// resources are dead when they run out of resources
    			if (armyStats.get(i).resources <= 0) {
    				removeUnit(armyStats.get(i), i);
    				i--;
    			}
    		} else {
    			// other units are dead when they run out of hp
    			if (armyStats.get(i).hp <= 0) {
					removeUnit(armyStats.get(i), i);
					i--;
    			}
    		}
    	}
    	
    	for (int i = 0; i < corpses.size(); i++) {
    		if (corpses.get(i).seen_dead) {
    			int player = corpses.get(i).player%armies.size();
    	    	if (player == -1) { // Java implements mod incorrectly
    	    		player = armies.size()-1;
    	    	}
    	    	for (int j = 0 ; j < armies.get(player).size(); j++) {
    	    		if (armies.get(player).get(j).getID() == corpses.get(i).id) {
    	    			armies.get(player).remove(j);
    	    			break;
    	    		}
    	    	}
    			
    			corpses.remove(i--);
    		}
    	}
    	
    	// update legal actions
    	for (int i = 0; i < armyStats.size(); i++) {
			determineLegalActions(armyStats.get(i));
    	}
    	
    	return gameover();
    }
    
    public PhysicalGameState(int start_player, GameState gs, UnitDefinitionManager unitDefs) {
    	width = gs.getMapWidth();
    	height = gs.getMapHeight();
    	fog = gs.isFog();
    	
    	terrain = new int[width*height];
    	unit_map = new UnitStats[width*height];
    	for (int i = 0; i < terrain.length; i++) {
    		unit_map[i] = null;
    		terrain[i] = (gs.getMap()[i]&GameState.MAP_WALL)==0?0:1;
    	}
    	
    	teams = new ArrayList<ArrayList<Integer> >();
    	for (int i = 0; i < gs.numberOfTeams(); i++) {
    		teams.add(gs.getTeam(i));
    	}
    	
    	armies = new ArrayList<ArrayList<Unit> >();
        armyStats = new ArrayList<UnitStats>();
        corpses = new ArrayList<UnitStats>();
        resources = new ArrayList<ArrayList<Integer> >();
        timeLimits = new ArrayList<Long>();
        scores = new ArrayList<Integer>();
        
        kills = new ArrayList<Integer>();
        deaths = new ArrayList<Integer>();
        
        for (int i = 0; i < gs.getPlayers(); i++) {
        	armies.add(new ArrayList<Unit>());
    		resources.add(new ArrayList<Integer>());
    		timeLimits.add(0L);
    		scores.add(0);
    		for (int j = 0; j < unitDefs.resourceTypes(); j++) {
    			resources.get(i).add(0);
    		}
    		
    		kills.add(0);
    		deaths.add(0);
        }
        
        for (int i = 0; i < gs.getResourceTypes(); i++) {
        	resources.get(start_player).set(i, gs.getResources(i));
        }
        
        unitDefinitions = unitDefs.copy();
        unitDefinitions.setPlayers(armies.size());
        
        reserved = new UnitStats(-2, -1, -1, unitDefinitions.resource_defs.get(0).get(0));
        
        // neutral army
        armies.add(new ArrayList<Unit>());
        
        for (int i = 0; i < gs.getMyUnits().size(); i++) {
        	Unit u = gs.getMyUnits().get(i);
        	
        	UnitStats stats = u.copyStats();
        	armyStats.add(stats);
        	unit_map[stats.x+stats.y*width] = stats;
        	armies.get(stats.player).add(new Unit(stats));
        }
        for (int i = 0; i < gs.getOtherUnits().size(); i++) {
        	Unit u = gs.getOtherUnits().get(i);
        	
        	UnitStats stats = u.copyStats();
        	armyStats.add(stats);
        	unit_map[stats.x+stats.y*width] = stats;
        	armies.get(stats.player).add(new Unit(stats));
        }
        for (int i = 0; i < gs.getNeutralUnits().size(); i++) {
        	Unit u = gs.getNeutralUnits().get(i);
        	
        	UnitStats stats = u.copyStats();
        	armyStats.add(stats);
        	unit_map[stats.x+stats.y*width] = stats;
        	armies.get(armies.size()-1).add(new Unit(stats));
        }
        
        current_player = start_player;
        
        for (int i = 0; i < armyStats.size(); i++) {
        	determineLegalActions(armyStats.get(i));
        }
    }
    
    public PhysicalGameState(Element e, UnitDefinitionManager unitDefs) {
        Element terrain_e = e.getChild("terrain");
        Element players_e = e.getChild("players");
        Element units_e = e.getChild("units");
        
        width = Integer.parseInt(e.getAttributeValue("width"));
        height = Integer.parseInt(e.getAttributeValue("height"));
        
        terrain = new int[width*height];
        unit_map = new UnitStats[width*height];
        String terrainString = terrain_e.getValue();
        for(int i = 0;i<width*height;i++) {
            String c = terrainString.substring(i, i+1);
            terrain[i] = Integer.parseInt(c);
            unit_map[i] = null;
        }
        
        teams = new ArrayList<ArrayList<Integer> >();
        
        armies = new ArrayList<ArrayList<Unit> >();
        armyStats = new ArrayList<UnitStats>();
        corpses = new ArrayList<UnitStats>();
        resources = new ArrayList<ArrayList<Integer> >();
        timeLimits = new ArrayList<Long>();
        scores = new ArrayList<Integer>();
        
        kills = new ArrayList<Integer>();
        deaths = new ArrayList<Integer>();
        
        for (int i = 0; i < players_e.getChildren().size(); i++) {
        	Element child = (Element)players_e.getChildren().get(i);
        	if (child.getName().equalsIgnoreCase("rts.Player")) {
        		try {
        			int team = Integer.parseInt(child.getAttributeValue("team"));
        			if (team < 0) {
        				team = 0;
        			} else while (team >= teams.size()) {
        				teams.add(new ArrayList<Integer>());
        			}
        			teams.get(team).add(armies.size());
        		} catch (NumberFormatException ee) {
        			teams.add(new ArrayList<Integer>());
        			teams.get(teams.size()-1).add(armies.size());
        		}
        		
        		armies.add(new ArrayList<Unit>());
        		resources.add(new ArrayList<Integer>());
        		timeLimits.add(0L);
        		scores.add(0);
        		for (int j = 0; j < unitDefs.resourceTypes(); j++) {
        			resources.get(i).add(0);
        		}
        		for (int j = 0; j < child.getChildren().size(); j++) {
        			Element start_resource = (Element)child.getChildren().get(j);
        			if (start_resource.getName().equalsIgnoreCase("resource")) {
        				resources.get(i).set(Integer.parseInt(start_resource.getAttributeValue("type")), Integer.parseInt(start_resource.getAttributeValue("amount")));
        			}
        		}
        		
        		kills.add(0);
        		deaths.add(0);
        	}
        }
        
        unitDefinitions = unitDefs;
        unitDefinitions.setPlayers(armies.size());
        
        reserved = new UnitStats(-2, -1, -1, unitDefinitions.resource_defs.get(0).get(0));
        
        // neutral army
        armies.add(new ArrayList<Unit>());
        
        for (int i = 0; i < units_e.getChildren().size(); i++) {
        	Element child = (Element)units_e.getChildren().get(i);
        	if (child.getName().equalsIgnoreCase("resource")) {
        		int x = Integer.parseInt(child.getAttributeValue("x"));
        		int y = Integer.parseInt(child.getAttributeValue("y"));
        		if (unit_map[x+y*width] == null) {
	        		unitDefinitions.makeResource(armies.get(armies.size()-1), armyStats,
	        				Integer.parseInt(child.getAttributeValue("type")),
	        				-1,
	        				x,
	        				y,
	        				Integer.parseInt(child.getAttributeValue("amount")));
	        		unit_map[x+y*width] = armyStats.get(armyStats.size()-1);
        		}
        	} else if (child.getName().equalsIgnoreCase("unit")) {
        		int player = Integer.parseInt(child.getAttributeValue("player"));
        		int x = Integer.parseInt(child.getAttributeValue("x"));
        		int y = Integer.parseInt(child.getAttributeValue("y"));
        		if (unit_map[x+y*width] == null) {
	        		unitDefinitions.makeUnit(armies.get(player), armyStats,
	        				Integer.parseInt(child.getAttributeValue("type")),
	        				Integer.parseInt(child.getAttributeValue("player")),
	        				x,
	        				y);
	        		unit_map[x+y*width] = armyStats.get(armyStats.size()-1);
        		}
        	} else if (child.getName().equalsIgnoreCase("building")) {
        		int player = Integer.parseInt(child.getAttributeValue("player"));
        		int x = Integer.parseInt(child.getAttributeValue("x"));
        		int y = Integer.parseInt(child.getAttributeValue("y"));
        		if (unit_map[x+y*width] == null) {
	        		unitDefinitions.makeBuilding(armies.get(player), armyStats,
	        				Integer.parseInt(child.getAttributeValue("type")),
	        				Integer.parseInt(child.getAttributeValue("player")),
	        				x,
	        				y);
	        		unit_map[x+y*width] = armyStats.get(armyStats.size()-1);
        		}
        	}
        }
        
        current_player = -1;
        
        for (int i = 0; i < armyStats.size(); i++) {
        	determineLegalActions(armyStats.get(i));
        }
    }
    
    /**
     * Gets the resources for the current player
     * @param type the type of resource to get
     * @return
     */
    public int getResources(int type) {
    	if (type >= 0 && type < resources.get(current_player).size()) {
    		return resources.get(current_player).get(type);
    	}
    	return 0;
    }
    
    /**
     * Sets the time limit for the current player
     * @param timelimit
     */
    public void setTimeLimit(long timelimit) {
    	timeLimits.set(current_player, timelimit);
    }
    
    /**
     * Sets the current player
     * @param player
     */
    public void setCurrentPlayer(int player) {
    	if (current_player != -1) {
    		for (int i = 0; i < unitDefinitions.resource_defs.get(current_player+1).size(); i++) {
    			unitDefinitions.resource_defs.get(current_player+1).get(i).active_player = false;
    		}
    		for (int i = 0; i < unitDefinitions.unit_defs.get(current_player+1).size(); i++) {
    			unitDefinitions.unit_defs.get(current_player+1).get(i).active_player = false;
    		}
    		for (int i = 0; i < unitDefinitions.building_defs.get(current_player+1).size(); i++) {
    			unitDefinitions.building_defs.get(current_player+1).get(i).active_player = false;
    		}
    	}
    	
    	current_player = player;
    	for (int i = 0; i < unitDefinitions.resource_defs.get(current_player+1).size(); i++) {
			unitDefinitions.resource_defs.get(current_player+1).get(i).active_player = true;
		}
		for (int i = 0; i < unitDefinitions.unit_defs.get(current_player+1).size(); i++) {
			unitDefinitions.unit_defs.get(current_player+1).get(i).active_player = true;
		}
		for (int i = 0; i < unitDefinitions.building_defs.get(current_player+1).size(); i++) {
			unitDefinitions.building_defs.get(current_player+1).get(i).active_player = true;
		}
    }
    
    /**
     * how long it'll take to harvest from this square
     * @param x
     * @param y
     * @return
     */
    public int harvestTime(int x, int y) {
    	if (x >= 0 && x < width && y >= 0 && y < height) {
    		if (unit_map[x+y*width] != null && unit_map[x+y*width].definition.is_resources) {
    			return unit_map[x+y*width].definition.harvest_speed;
    		}
    	}
    	/*for (int i = 0; i < armies.get(numberOfPlayers()).size(); i++) {
    		if (armies.get(numberOfPlayers()).get(i).isResources() &&
    			armies.get(numberOfPlayers()).get(i).getX() == x && armies.get(numberOfPlayers()).get(i).getY() == y) {
    			return armies.get(numberOfPlayers()).get(i).getHarvestSpeed();
    		}
    	}*/
    	return UnitAction.DEFAULT_COOLDOWN;
    }
    
    /**
     * Gets the build time for the thign you want to build
     * @param player the player that wants to build
     * @param is_building whether or not you want to build a building
     * @param build the thing you want to build
     * @return
     */
    public int buildTime(int player, boolean is_building, int build) {
    	if (is_building) {
    		if (build >= 0 && build < unitDefinitions.building_defs.get(current_player).size()) {
    			//System.out.println(current_player+" cooldown: "+unitDefinitions.building_defs.get(current_player).get(build).produce_speed);
    			return unitDefinitions.building_defs.get(current_player).get(build).produce_speed;
    		}
    	} else {
    		if (build >= 0 && build < unitDefinitions.unit_defs.get(current_player).size()) {
    			return unitDefinitions.unit_defs.get(current_player).get(build).produce_speed;
    		}
    	}
    	return UnitAction.DEFAULT_COOLDOWN;
    }
    
    /**
     * Reserves the resources to build something
     * @param player the player who is building
     * @param is_building whether or not the thing being built is a building
     * @param build the thing you want to build
     */
    /*public void reserveResources(int player, boolean is_building, int build) {
    	ArrayList<Integer> cost = null;
    	if (is_building) {
    		if (build >= 0 && build < unitDefinitions.building_defs.get(player).size()) {
    			cost = unitDefinitions.building_defs.get(player).get(build).cost;
    		}
    	} else {
    		if (build >= 0 && build < unitDefinitions.unit_defs.get(player).size()) {
    			cost = unitDefinitions.unit_defs.get(player).get(build).cost;
    		}
    	}
    	if (cost != null) {
	    	for (int i = 0; i < cost.size(); i++) {
				resources.get(player).set(i, resources.get(player).get(i)-cost.get(i));
			}
    	}
    }*/
    
    /**
     * Restores the resources to build something because somethign went wrong
     * @param player the player who is building
     * @param is_building whether or not the thing being built is a building
     * @param build the thing you want to build
     */
    /*public void restoreResources(int player, boolean is_building, int build) {
    	ArrayList<Integer> cost = null;
    	if (is_building) {
    		if (build >= 0 && build < unitDefinitions.building_defs.get(player).size()) {
    			cost = unitDefinitions.building_defs.get(player).get(build).cost;
    		}
    	} else {
    		if (build >= 0 && build < unitDefinitions.unit_defs.get(player).size()) {
    			cost = unitDefinitions.unit_defs.get(player).get(build).cost;
    		}
    	}
    	if (cost != null) {
	    	for (int i = 0; i < cost.size(); i++) {
				resources.get(player).set(i, resources.get(player).get(i)+cost.get(i));
			}
    	}
    }*/
    
    /**
     * Moves the unit to the new location
     * @param unit
     * @param x
     * @param y
     * @return whether or not the move was valid
     */
    public boolean moveUnit(UnitStats unit, int x, int y) {
    	if (!unit.definition.is_building && !unit.definition.is_resources) { 
	    	if (x >= 0 && x < width && y >= 0 && y < height &&
			    ((x == unit.x && (y == unit.y-1 || y == unit.y+1)) ||
				 (y == unit.y && (x == unit.x-1 || x == unit.x+1)))) {
				// and must be empty
				if (unit_map[x+y*width] == null && 
					(terrain[x+y*width] == TERRAIN_NONE ||
					 unit.definition.is_flying)) {
					unit_map[unit.x+unit.y*width] = null;
					unit_map[x+y*width] = unit;
					unit.x = x;
					unit.y = y;
					return true;
				} //else System.out.println("move target is occupied");
			} //else System.out.println("move target is off map");
    	} //else System.out.println("building/resource can't move");
    	return false;
    }
    
    /**
     * A unit attacks a square
     * @param attacker
     * @param x
     * @param y
     * @return whether or not the attack was valid
     */
    public boolean attackUnit(UnitStats attacker, int x, int y) {
    	if (!attacker.definition.is_building && !attacker.definition.is_resources) {
	    	if (x >= 0 && x < width && y >= 0 && y < height) {
	    		// target needs to be within range of attacker
	    		if ((x-attacker.x)*(x-attacker.x)+(y-attacker.y)*(y-attacker.y) <= attacker.definition.attack_range*attacker.definition.attack_range) {
	    			UnitStats victim = unit_map[x+y*width];
		    		if (victim != null && !victim.definition.is_resources) {
		    			victim.hp -= attacker.definition.getDamage();
		    			if (victim.hp <= 0) {
		    				kills.set(attacker.player, kills.get(attacker.player)+1);
		    				victim.killed_by = attacker.player;
		    				deaths.set(victim.player, deaths.get(victim.player)+1);
		    			}
		    			return true;
		    		} //else System.out.println("no target to attack "+x+"x"+y);
	    		} //else System.out.println("target is too far away");
	    	} //else System.out.println("target is off map");
    	} //else System.out.println("attacker is building or resources");
    	return false;
    }
    
    /**
     * A unit attempts to harvest from some location
     * @param unit
     * @param x
     * @param y
     * @return
     */
    public boolean harvestUnit(UnitStats unit, int x, int y) {
    	if (x >= 0 && x < width && y >= 0 && y < height &&
		    ((x == unit.x && (y == unit.y-1 || y == unit.y+1)) ||
			 (y == unit.y && (x == unit.x-1 || x == unit.x+1)))) {
    		// can only harvest from resources
    		UnitStats resource = unit_map[x+y*width];
    		if (resource != null && resource.definition.is_resources) {
    			// can't be already holding resources either
    			if (unit.resources == 0 && unit.definition.is_worker) {
    				int harvestAmt = resource.definition.harvest_amt;
    				if (harvestAmt > resource.resources) {
    					harvestAmt = resource.resources;
    				}
    				resource.resources -= harvestAmt;
    				unit.resources = harvestAmt;
    				unit.resources_type = resource.resources_type;
    				return true;
    			} //else System.out.println("harvester is not worker or already has resources");
    		} //else System.out.println("harvest target is not resources");
    	} //else System.out.println("harvest target is off map");
    	return false;
    }
    
    /**
     * Attempts to return the harvest
     * @param unit
     * @param x
     * @param y
     * @return
     */
    public boolean returnUnitHarvest(UnitStats unit, int x, int y) {
    	if (unit.definition.is_worker) {
	    	if (x >= 0 && x < width && y >= 0 && y < height &&
			    ((x == unit.x && (y == unit.y-1 || y == unit.y+1)) ||
				 (y == unit.y && (x == unit.x-1 || x == unit.x+1)))) {
	    		UnitStats stockpile = unit_map[x+y*width];
	    		if (stockpile != null && stockpile.definition.is_stockpile_building && unit.resources > 0) {
	    			resources.get(unit.player).set(unit.resources_type, resources.get(unit.player).get(unit.resources_type)+unit.resources);
	    			// add resources collected to score
	    			//System.out.println("a return occured");
	    			scores.set(unit.player, scores.get(unit.player)+unit.resources);
	    			unit.resources = 0;
	    			return true;
	    		} //else System.out.println("return target is not stockpile"); 
	    	} //else System.out.println("return target is off map");
    	} //else System.out.println("only workers can return");
    	return false;
    }
    
    /**
     * Attempts to build a unit somewhere
     * @param unit
     * @param x
     * @param y
     * @param build what to build
     * @return
     */
    public boolean buildUnit(UnitStats unit, int x, int y, int build) {
    	if (x >= 0 && x < width && y >= 0 && y < height &&
		    ((x == unit.x && (y == unit.y-1 || y == unit.y+1)) ||
			 (y == unit.y && (x == unit.x-1 || x == unit.x+1)))) {
    		if (unit_map[x+y*width] == null) {
    			if (unit.definition.is_worker) {
    				if (build >= 0 && build < unitDefinitions.building_defs.get(unit.player).size()) {
	    				UnitDefinition def = unitDefinitions.building_defs.get(unit.player).get(build);
	    				for (int i = 0; i < def.cost.size(); i++) {
	    					if (resources.get(unit.player).get(i) < def.cost.get(i)) {
	    						//System.out.println("not enough resources to build: "+build);
	    						return false;
	    					}
	    				}
	    				// now that we have enough resources, decrement the cost
	    				for (int i = 0; i < def.cost.size(); i++) {
	    					resources.get(unit.player).set(i, resources.get(unit.player).get(i)-def.cost.get(i));
	    				}	    		
	    				
	    				unitDefinitions.makeBuilding(armies.get(unit.player), armyStats, build, unit.player, x, y);
	    				unit_map[x+y*width] = armyStats.get(armyStats.size()-1);
	    				
	    				// add score
	    				scores.set(unit.player, scores.get(unit.player)+unitDefinitions.getScore(armyStats.get(armyStats.size()-1)));
	    				return true;
    				} //else System.out.println("invalid building id: "+build);
    				
    			} else if (unit.definition.is_building) {
    				// ensure this building can build this unit
    				for (int i = 0; i < unit.definition.produces.size(); i++) {
    					if (build == unit.definition.produces.get(i)) {
    						UnitDefinition def = unitDefinitions.unit_defs.get(unit.player).get(build);
    	    				for (int j = 0; j < def.cost.size(); j++) {
    	    					if (resources.get(unit.player).get(j) < def.cost.get(j)) {
    	    						return false;
    	    					}
    	    				}
    	    				// now that we have enough resources, decrement the cost
    	    				for (int j = 0; j < def.cost.size(); j++) {
    	    					resources.get(unit.player).set(j, resources.get(unit.player).get(j)-def.cost.get(j));
    	    				}
    						
    						unitDefinitions.makeUnit(armies.get(unit.player), armyStats, build, unit.player, x, y);
    						unit_map[x+y*width] = armyStats.get(armyStats.size()-1);
    						
    						// add score
    	    				scores.set(unit.player, scores.get(unit.player)+unitDefinitions.getScore(armyStats.get(armyStats.size()-1)));
    						return true;
    					} //else System.out.println(unit.id+" cannot build "+build);
    				}
    			} //else System.out.println("only buildings and workers can build");
    		} //else System.out.println("build target is already occupied");
    	} //else System.out.println("build target is off map");
    	return false;
    }
    
    /**
     * Builds a list of legal actions for the specified unit
     * @param unit
     */
    private void determineLegalActions(UnitStats unit) {
    	if (unit.player != -1) {
	    	unit.legalActions.clear();
	    	if (unit.definition.is_building) {
	    		// consider UPGRADE
	    		for (int i = 0; i < unit.definition.unit_upgrades.size(); i++) {
	    			UnitUpgrade def = null;
	    			for (int j = 0; j < unitDefinitions.unit_upgrades.get(unit.player+1).size(); j++) {
	    				if (unit.definition.unit_upgrades.get(i) == unitDefinitions.unit_upgrades.get(unit.player+1).get(i).getID()) {
	    					def = unitDefinitions.unit_upgrades.get(unit.player+1).get(i);
	    					break;
	    				}
	    			}
	    			if (def != null) {
		    			boolean enoughMoney = true;
		    			for (int j = 0; j < resources.size(); j++) {
		    				if (resources.get(unit.player).get(j) < def.getCost(j)) {
		    					enoughMoney = false;
		    					break;
		    				}
		    			}
		    			if (enoughMoney) {
			    			unit.legalActions.add(new UnitAction(unit, UnitAction.UPGRADE, unit.x, unit.y, unit.definition.unit_upgrades.get(i)));
			    		}
	    			}
	    		}
	    		for (int i = 0; i < unit.definition.unit_upgrades.size(); i++) {
	    			UnitUpgrade def = null;
	    			for (int j = 0; j < unitDefinitions.building_upgrades.get(unit.player+1).size(); j++) {
	    				if (unit.definition.unit_upgrades.get(i) == unitDefinitions.building_upgrades.get(unit.player+1).get(i).getID()) {
	    					def = unitDefinitions.building_upgrades.get(unit.player+1).get(i);
	    					break;
	    				}
	    			}
	    			if (def != null) {
		    			boolean enoughMoney = true;
		    			for (int j = 0; j < resources.size(); j++) {
		    				if (resources.get(unit.player).get(j) < def.getCost(j)) {
		    					enoughMoney = false;
		    					break;
		    				}
		    			}
		    			if (enoughMoney) {
			    			unit.legalActions.add(new UnitAction(unit, UnitAction.UPGRADE, unit.x, unit.y, unit.definition.building_upgrades.get(i)));
			    		}
	    			}
	    		}
	    		
	    		// consider BUILD
	    		for (int i = 0; i < unit.definition.produces.size(); i++) {
	    			UnitDefinition def = unitDefinitions.unit_defs.get(unit.player+1).get(unit.definition.produces.get(i));
	    			boolean enoughMoney = true;
	    			for (int j = 0; j < def.cost.size(); j++) {
	    				if (resources.get(unit.player).get(j) < def.cost.get(j)) {
	    					enoughMoney = false;
	    					break;
	    				}
	    			}
	    			if (enoughMoney) {
	    				// horizontal
		    			for (int x = unit.x-1; x < unit.x+2; x+=2) {
		    				if (x >= 0 && x < width && unit_map[x+unit.y*width] == null && (unitDefinitions.unit_defs.get(unit.player+1).get(unit.definition.produces.get(i)).is_flying || terrain[x+unit.y*width] == TERRAIN_NONE)) {
		    					unit.legalActions.add(new UnitAction(unit, UnitAction.BUILD, x, unit.y, unit.definition.produces.get(i)));
		    				}
		    			}
		    			// vertical
		    			for (int y = unit.y-1; y < unit.y+2; y+=2) {
		    				if (y >= 0 && y < height && unit_map[unit.x+y*width] == null && (unitDefinitions.unit_defs.get(unit.player+1).get(unit.definition.produces.get(i)).is_flying || terrain[unit.x+y*width] == TERRAIN_NONE)) {
		    					unit.legalActions.add(new UnitAction(unit, UnitAction.BUILD, unit.x, y, unit.definition.produces.get(i)));
		    				}
		    			}
	    			}
	    		}
	    	} else {
		    	if (unit.definition.is_worker) {
		    		// consider HARVEST
		    		// horizontal
		    		if (unit.resources == 0) {
		    			for (int x = unit.x-1; x < unit.x+2; x+=2) {
		    				if (x >= 0 && x < width) {
		    					UnitStats resource = unit_map[x+unit.y*width];
		    					if (resource != null && resource.definition.is_resources && resource.resources > 0) {
		    						unit.legalActions.add(new UnitAction(unit, UnitAction.HARVEST, x, unit.y, -1));
		    					}
		    				}
		    			}
		    			// vertical
		    			for (int y = unit.y-1; y < unit.y+2; y+=2) {
		    				if (y >= 0 && y < height) {
		    					UnitStats resource = unit_map[unit.x+y*width];
		    					if (resource != null && resource.definition.is_resources && resource.resources > 0) {
		    						unit.legalActions.add(new UnitAction(unit, UnitAction.HARVEST, unit.x, y, -1));
		    					}
		    				}
		    			}
		    		}
		    		
		    		// consider RETURN
	    			if (unit.resources > 0) {
	    				// horizontal
	        			for (int x = unit.x-1; x < unit.x+2; x+=2) {
	        				if (x >= 0 && x < width) {
	        					UnitStats stockpile = unit_map[x+unit.y*width];
	        					if (stockpile != null && stockpile.definition.is_stockpile_building) {
	        						unit.legalActions.add(new UnitAction(unit, UnitAction.RETURN, x, unit.y, -1));
	        					}
	        				}
	        			}
	        			// vertical
	        			for (int y = unit.y-1; y < unit.y+2; y+=2) {
	        				if (y >= 0 && y < height) {
	        					UnitStats stockpile = unit_map[unit.x+y*width];
	        					if (stockpile != null && stockpile.definition.is_stockpile_building) {
	        						unit.legalActions.add(new UnitAction(unit, UnitAction.RETURN, unit.x, y, -1));
	        					}
	        				}
	        			}
	    			}
		    		
		    		// consider BUILD
	    			for (int i = 0; i < unitDefinitions.building_defs.get(unit.player+1).size(); i++) {
	    				UnitDefinition def = unitDefinitions.building_defs.get(unit.player+1).get(i);
	        			boolean enoughMoney = true;
	        			for (int j = 0; j < def.cost.size(); j++) {
	        				if (resources.get(unit.player).get(j) < def.cost.get(j)) {
	        					enoughMoney = false;
	        					break;
	        				}
	        			}
	        			if (enoughMoney) {
	        				// horizontal
	    	    			for (int x = unit.x-1; x < unit.x+2; x+=2) {
	    	    				if (x >= 0 && x < width && unit_map[x+unit.y*width] == null && terrain[x+unit.y*width] == TERRAIN_NONE) {
	    	    					unit.legalActions.add(new UnitAction(unit, UnitAction.BUILD, x, unit.y, i));
	    	    				}
	    	    			}
	    	    			// vertical
	    	    			for (int y = unit.y-1; y < unit.y+2; y+=2) {
	    	    				if (y >= 0 && y < height && unit_map[unit.x+y*width] == null && terrain[unit.x+y*width] == TERRAIN_NONE) {
	    	    					unit.legalActions.add(new UnitAction(unit, UnitAction.BUILD, unit.x, y, i));
	    	    				}
	    	    			}
	        			}
	    			}
		    	}
	    			
		    	// consider MOVE
		    	// horizontal
		    	for (int x = unit.x-1; x < unit.x+2; x+=2) {
					if (x >= 0 && x < width && unit_map[x+unit.y*width] == null &&
						(unit.definition.is_flying || terrain[x+unit.y*width] == TERRAIN_NONE)) {
						unit.legalActions.add(new UnitAction(unit, UnitAction.MOVE, x, unit.y, -1));
					}
				}
				// vertical
				for (int y = unit.y-1; y < unit.y+2; y+=2) {
					if (y >= 0 && y < height && unit_map[unit.x+y*width] == null &&
						(unit.definition.is_flying || terrain[unit.x+y*width] == TERRAIN_NONE)) {
						unit.legalActions.add(new UnitAction(unit, UnitAction.MOVE, unit.x, y, -1));
					}
				}
		    	
		    	// consider ATTACK
				for (int y = unit.y-unit.definition.attack_range; y <= unit.y+unit.definition.attack_range; y++) {
					for (int x = unit.x-unit.definition.attack_range; x <= unit.x+unit.definition.attack_range; x++) {
						// ensure on map and in range
						int distance2 = (x-unit.x)*(x-unit.x)+(y-unit.y)*(y-unit.y);
						if (x >= 0 && x < width && y >= 0 && y < height && distance2 <= unit.definition.attack_range*unit.definition.attack_range && distance2 > 0) {
							UnitStats victim = unit_map[x+y*width];
							if (victim != null && !victim.definition.is_resources) {
								unit.legalActions.add(new UnitAction(unit, UnitAction.ATTACK, x, y, -1));
							}
						}
					}
				}
	    	}
    	}
    }
    
    /**
     * Removes this unit because it is dead
     * @param unit
     * @param index
     */
    private void removeUnit(UnitStats unit, int index) {
//    	int player = unit.player%armies.size();
//    	if (player == -1) {
//    		player = armies.size()-1;
//    	}
//    	for (int i = 0 ; i < armies.get(player).size(); i++) {
//    		if (armies.get(player).get(i).getID() == unit.id) {
//    			armies.get(player).remove(i);
//    			break;
//    		}
//    	}
    	// if this unit was building...
    	if (unit.action != null && unit.action.getType() == UnitAction.BUILD) {
    		unit_map[unit.action.getTargetX()+unit.action.getTargetY()*width] = null;
    	}
    	
    	corpses.add(unit);
    	
    	// check score
    	if (unit.killed_by != -1) {
	    	if (unit.player != unit.killed_by) {
	    		scores.set(unit.killed_by, scores.get(unit.killed_by)+unitDefinitions.getScore(unit));
	    	} else {
	    		scores.set(unit.killed_by, scores.get(unit.killed_by)-unitDefinitions.getScore(unit));
	    	}
    	}
    	
    	unit_map[unit.x+unit.y*width] = null;
    	armyStats.remove(index);
    }
    
    /**
     * Returns how long an upgrade is going to take
     * @param player
     * @param upgrade_id
     * @return
     */
    public int upgradeTime(int player, int upgrade_id) {
    	if (player >= 0 && player < numberOfPlayers()) {
    		player++;
    		for (int i = 0; i < unitDefinitions.unit_upgrades.get(player).size(); i++) {
    			if (upgrade_id == unitDefinitions.unit_upgrades.get(player).get(i).getID()) {
    				return unitDefinitions.unit_upgrades.get(player).get(i).getUpgradeTime();
    			}
    		}
    		for (int i = 0; i < unitDefinitions.building_upgrades.get(player).size(); i++) {
    			if (upgrade_id == unitDefinitions.building_upgrades.get(player).get(i).getID()) {
    				return unitDefinitions.building_upgrades.get(player).get(i).getUpgradeTime();
    			}
    		}
    	}
    	
    	return UnitAction.DEFAULT_COOLDOWN;
    }
    
    /**
     * Executes an upgrade
     * @param upgrade_id which upgrade to do
     * @return
     */
    public boolean upgradeUnits(int player, int upgrade_id) {
    	return unitDefinitions.upgrade(player, upgrade_id);
    }
}
