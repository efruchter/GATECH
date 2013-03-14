/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ai;

import java.util.Random;

import ai.util.PlayerAction;
import rts.*;
import rts.units.Unit;
import rts.units.UnitAction;
import util.Sampler;

/**
 *
 * @author santi
 * 
 * \brief Chooses random moves, but without a uniform probability for each move
 * 
 */
public class RandomBiasedAI extends AI {
    Random r = new Random();

    public void reset() {   
    }    
    
    public AI clone() {
        return new RandomBiasedAI();
    }
    
    public void getAction(GameState gs, int time_limit) {
        getRandomPlayerAction(gs);
    }
    
    public PlayerAction getRandomPlayerAction(GameState gs) {
        //PhysicalGameState pgs = gs.getPhysicalGameState();
        PlayerAction pa = new PlayerAction(gs.getMapWidth(), gs.getMapHeight());
        
        // Generate the reserved resources:
        for(Unit u:gs.getMyUnits()) {
            if (u.hasAction()) {
                //ResourceUsage ru = uaa.action.resourceUsage(u, gs);
                //pa.getResourceUsage().merge(ru);
            	pa.mergeResourceUsage(u, u.getAction());
            }
        }
        
        for(Unit u:gs.getMyUnits()) {
            if (!u.hasAction()) {
                UnitAction none = null;
                int nActions = u.getActions().size();
                double []distribution = new double[nActions];

                // Implement "bias":
                int i = 0;
                for(UnitAction a:u.getActions()) {
                    if (a.getType()==UnitAction.ATTACK ||
                        a.getType()==UnitAction.HARVEST ||
                        a.getType()==UnitAction.RETURN) {
                        distribution[i]=5;
                    } else {
                        distribution[i]=1;  // attack, harvest and return have 5 times the probability
                    }
                    i++;
                }
                    
                try {
                	if (u.getActions().size() > 0) {
	                    UnitAction ua = u.getActions().get(Sampler.weighted(distribution));
	                    if (pa.consistentResourceUsage(ua)) {
	                        pa.mergeResourceUsage(u, ua);                        
	                        pa.addUnitAction(u, ua);
	                    } else {
	                        pa.addUnitAction(u, none);
	                    }
                	}
                } catch (Exception ex) {
                    ex.printStackTrace();
                    pa.addUnitAction(u, none);
                }
            }
        }
        
        return pa;
    }
    
    @Override
    public String getLabel() {
    	return "Random Biased AI";
    }
}
