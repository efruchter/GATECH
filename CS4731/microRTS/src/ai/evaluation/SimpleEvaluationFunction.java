/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.evaluation;

import rts.GameState;
import rts.units.*;

/**
 * \package ai.evaluation
 * \brief Evaluation techniques for the AIs that need such a thing.
 */

/**
 * \brief A simple evaluation function, (eg for use with Monte Carlo agents)
 * @author santi
 */
public class SimpleEvaluationFunction {
    public static float VICTORY = 10000;
    
    public static float RESOURCE = 20;
    public static float RESOURCE_IN_WORKER = 10;
    public static float UNIT_BONUS_MULTIPLIER = 40.0f;
    
    
    public static float evaluate(GameState gs) {
        return base_score(true,gs) - base_score(false,gs);
    }
    
    public static float base_score(boolean player, GameState gs) {
    	if (player) {
	    	int resources = 0;
	    	for (int i = 0; i < gs.getResourceTypes(); i++) {
	    		resources += gs.getResources(i);
	    	}
	        float score = resources*RESOURCE;
	        for(Unit u:gs.getMyUnits()) {
	                score += u.getResources() * RESOURCE_IN_WORKER;
	                int cost = 0;
	                for (int i = 0; i < u.getCost().size(); i++) {
	                	cost += u.getCost(i);
	                }
	                score += UNIT_BONUS_MULTIPLIER * (cost*u.getHP())/(float)u.getMaxHP();
	        }
	        
	        return score;
    	}
        
    	int resources = 0;
    	for (int i = 0; i < gs.getNeutralUnits().size(); i++) {
    		resources += gs.getNeutralUnits().get(i).getResources();
    	}
        float score = resources*RESOURCE;
        for(Unit u:gs.getOtherUnits()) {
                score += u.getResources() * RESOURCE_IN_WORKER;
                int cost = 0;
                for (int i = 0; i < u.getCost().size(); i++) {
                	cost += u.getCost(i);
                }
                score += UNIT_BONUS_MULTIPLIER * (cost*u.getHP())/(float)u.getMaxHP();
        }
        
        return score;
    }    
    
    public static float upperBound(GameState gs) {
        //PhysicalGameState pgs = gs.getPhysicalGameState();
        int free_resources = 0;
        int resources = 0;
        for (int i = 0; i < gs.getResourceTypes(); i++) {
        	resources += gs.getResources(i);
        }
        int player_resources[] = {resources,0};
        for(Unit u:gs.getMyUnits()) {
           player_resources[0] += u.getResources();
            for (int i = 0; i < u.getCost().size(); i++) {
            	player_resources[0] += u.getCost(i);
            }
            //player_resources[0] += u.getCost();
        }
        for (Unit u:gs.getOtherUnits()) {
        	player_resources[1] += u.getResources();
            for (int i = 0; i < u.getCost().size(); i++) {
            	player_resources[1] += u.getCost(i);
            }
        }
        for (Unit u:gs.getNeutralUnits()) {
        	free_resources+=u.getResources();
        }
//        System.out.println(free_resources + " + [" + player_resources[0] + " , " + player_resources[1] + "]");
//        if (free_resources + player_resources[0] + player_resources[1]>62) {
//            System.out.println(gs);
//        }
        return (free_resources + Math.max(player_resources[0],player_resources[1]))*UNIT_BONUS_MULTIPLIER;
    }
}
