/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rts;

import java.util.*;

import rts.units.Unit;
import rts.units.UnitDefinition;
import rts.units.UnitUpgrade;

/**
 * \brief Provides read access to game elements used by AIs
 * @author santi
 */
public class GameState {
	// these defs should probably be moved into a Map class
	public static final int MAP_FOG       = 1;  /**< (bitmask) the tile has fog of war flag */
	public static final int MAP_WALL      = 2;  /**< (bitmask) the tile has the wall flag */
	public static final int MAP_PLAYER    = 4;  /**< (bitmask) the tile has the current player's unit */
	public static final int MAP_NEUTRAL   = 8;  /**< (bitmask) the tile has the neutral player's unit */
	public static final int MAP_NONPLAYER = 16; /**< (bitmask) the tile has a unit that isn't the current or neutral player's */ 
	
	
	
    private PhysicalGameState pgs = null;
    private ArrayList<Unit> myUnits; /**< current player's units */
    private ArrayList<Unit> otherUnits; /**< other units that the current player sees */
    private ArrayList<Unit> neutralUnits; /**< neutral units */
    
    private ArrayList<UnitDefinition> unitList; /**< list of units */
    private ArrayList<UnitDefinition> buildingList; /**< list of buildings */
    
    private int[] map; /**< visible map */

    public GameState(PhysicalGameState a_pgs) {
        pgs = a_pgs;
        
        myUnits = new ArrayList<Unit>();
        otherUnits = new ArrayList<Unit>();
        neutralUnits = new ArrayList<Unit>();
        
        unitList = new ArrayList<UnitDefinition>();
        buildingList = new ArrayList<UnitDefinition>();
        
        map = new int[pgs.width*pgs.height];
        for (int i = 0; i < map.length; i++) {
        	map[i] = 0;
        }
    }
    
    /**
     * Returns the current player's units
     * @return
     */
    public ArrayList<Unit> getMyUnits() {
    	return myUnits;
    }
    
    /**
     * Non-player units
     * @return
     */
    public ArrayList<Unit> getOtherUnits() {
    	return otherUnits;
    }
    
    /**
     * Neutral units
     * @return
     */
    public ArrayList<Unit> getNeutralUnits() {
    	return neutralUnits;
    }
    
    /**
     * Returns the map
     * @return
     */
    public int[] getMap() {
    	return map;
    }
    
    /**
     * Returns the resources for the current player
     * @param type
     * @return
     */
    public int getResources(int type) {
    	return pgs.getResources(type);
    }
    
    /**
     * Returns a copy of the list of resources
     * @return
     */
    public ArrayList<Integer> getResources() {
    	ArrayList<Integer> r = new ArrayList<Integer>();
    	for (int i = 0; i < pgs.resources.get(pgs.current_player).size(); i++) {
    		r.add(pgs.resources.get(pgs.current_player).get(i));
    	}
    	return r;
    }
    
    /**
     * Returns how many resource types there are
     * @return
     */
    public int getResourceTypes() {
    	return pgs.resources.size();
    }
    
    /**
     * Returns the width of the map
     * @return
     */
    public int getMapWidth() {
    	return pgs.width;
    }
    
    /**
     * Returns the height of the map
     * @return
     */
    public int getMapHeight() {
    	return pgs.height;
    }
    
    /**
     * Returns the list of upgrades for units
     * @return unit upgrades
     */
    public ArrayList<UnitUpgrade> getUnitUpgrades() {
    	return pgs.unitDefinitions.unit_upgrades.get(pgs.current_player+1);
    }
    
    /**
     * Returns the list of building upgrades
     * @return
     */
    public ArrayList<UnitUpgrade> getBuildingUpgrades() {
    	return pgs.unitDefinitions.building_upgrades.get(pgs.current_player+1);
    }
    
    /**
     * Returns the list of units that can be built
     * @return
     */
    public ArrayList<UnitDefinition> getUnitList() {
    	return unitList;
    }
    
    /**
     * Retrusn the list of buildings that can be built
     * @return
     */
    public ArrayList<UnitDefinition> getBuildingList() {
    	return buildingList;
    }
    
    /**
     * Whether or not there is fog of war
     * @return
     */
    public boolean isFog() {
    	return pgs.fog;
    }
    
    /**
     * Returns how many different players there are
     * @return
     */
    public int getPlayers() {
    	return pgs.armies.size()-1;
    }
    
    /**
     * Returns the current player's id
     * @return
     */
    public int getPlayerID() {
    	return pgs.current_player;
    }
    
    /**
     * Returns the number of teams
     * @return the number of teams
     */
    public int numberOfTeams() {
    	return pgs.teams.size();
    }
    
    /**
     * Returns the specified team
     * @param team the team
     * @return the team
     */
    public ArrayList<Integer> getTeam(int team) {
    	ArrayList<Integer> t = new ArrayList<Integer>();
    	if (team >= 0 && team < pgs.teams.size()) {
    		t.addAll(pgs.teams.get(team));
    	}
    	return t;
    }
    
    /**
     * Updates the game state for the current player
     */
    public void update() {
    	myUnits.clear();
    	otherUnits.clear();
    	neutralUnits.clear();
    	
    	unitList.clear();
    	buildingList.clear();
    	
    	unitList = pgs.unitDefinitions.getCopyOfUnits(pgs.current_player);
    	buildingList = pgs.unitDefinitions.getCopyOfBuildings(pgs.current_player);
    	
    	for (int i = 0; i < map.length; i++) {
    		map[i] = 0;
    	}
    	
    	for (int i = 0; i < pgs.armies.get(pgs.current_player).size(); i++) {
    		Unit u = pgs.armies.get(pgs.current_player).get(i);
    		if (u.getHP() > 0) {
	    		myUnits.add(u);
	    		map[u.getX()+u.getY()*pgs.width] |= MAP_PLAYER;
    		}
    	}
    	
    	// if we aren't playing with fog, dude can see everything
    	if (!pgs.fog) {
    		for (int i = 0; i < pgs.armies.size(); i++) {
    			if (i != pgs.current_player) {
    				for (int j = 0; j < pgs.armies.get(i).size(); j++) {
    					Unit u = pgs.armies.get(i).get(j);
    					if (i == pgs.numberOfPlayers()) { // neutral player
    						neutralUnits.add(u.copy());
    						if (u.getResources() <= 0) {
    							u.seen_dead();
    						}
    						map[u.getX()+u.getY()*pgs.width] |= MAP_NEUTRAL; 
    	    			} else {
    	    				otherUnits.add(u.copy());
    	    				if (u.getHP() <= 0) {
    	    					u.seen_dead();
    	    				} else {
	    	    				map[u.getX()+u.getY()*pgs.width] |= MAP_NONPLAYER;
    	    				}
    	    			}
    				}
    			}
    		}
    		for (int i = 0; i < pgs.terrain.length; i++) {
    			if (pgs.terrain[i] == PhysicalGameState.TERRAIN_WALL || pgs.unit_map[i] == pgs.reserved) {
    				map[i] |= MAP_WALL;
    			}
    		}
    	} else { // we can't see everything
    		int my_team = 0;
    		for (int i = 0; i < pgs.teams.size(); i++) {
    			if (pgs.teams.get(i).contains(pgs.current_player)) {
    				my_team = i;
    				break;
    			}
    		}
    		
    		for (int i = 0; i < map.length; i++) {
    			map[i] |= MAP_FOG;
    		}
    		// you'd think this could be a lot more efficient
    		for (int i = 0; i < myUnits.size(); i++) {
    			Unit u = myUnits.get(i);
    			for (int j = u.getY()-u.getVision(); j <= u.getY()+u.getVision(); j++) {
    				if (j >= 0 && j < pgs.height) {  
	    				for (int k = u.getX()-u.getVision(); k <= u.getX()+u.getVision(); k++) {
	    					if (k >= 0 && k < pgs.width && k+j*pgs.width >= 0 && k+j*pgs.width < map.length && (j-u.getY())*(j-u.getY())+(k-u.getX())*(k-u.getX()) < u.getVision()*u.getVision()) {
	    						map[k+j*pgs.width] &= ~MAP_FOG;
	    						if (pgs.terrain[k+j*pgs.width] == PhysicalGameState.TERRAIN_WALL || pgs.unit_map[k+j*pgs.width] == pgs.reserved) {
	    							map[k+j*pgs.width] |= MAP_WALL;
	    						}
	    					}
	    				}
    				}
    			}
    		}
    		
    		for (int i = 0; i < pgs.armies.size(); i++) {
    			if (i != pgs.current_player) {
    				for (int j = 0; j < pgs.armies.get(i).size(); j++) {
    					Unit u = pgs.armies.get(i).get(j);
    					if ((map[u.getX()+u.getY()*pgs.width]&MAP_FOG) == 0) {
    						if (i == pgs.numberOfPlayers() || pgs.teams.get(my_team).contains(i)) {
    							neutralUnits.add(u);
    							if (u.isResources() && u.getResources() <= 0) {
        							u.seen_dead();
        						}
    							map[u.getX()+u.getY()*pgs.width] |= MAP_NEUTRAL;
    						} else {
    							otherUnits.add(u.copy());
    							if (u.getHP() <= 0) {
        	    					u.seen_dead();
        	    				} else {
    	    	    				map[u.getX()+u.getY()*pgs.width] |= MAP_NONPLAYER;
        	    				}
    						}
    					}
    				}
    			}
    		}
    	}
    }
}
