package ai.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import rts.GameState;
import rts.units.Unit;
import rts.units.UnitAction;
import util.Pair;

/**
 * \brief Generates a list of all possible PlayerAction
 */
public class PlayerActionGenerator {
	GameState gs;
	PlayerAction base_ru;
    public List<Pair<Unit,List<UnitAction> > > choices;
    PlayerAction lastAction = null;
    long size = 1;
    long generated = 0;
    int choiceSizes[] = null;
    int currentChoice[] = null;
    boolean moreActions = true;
    
    public long getGenerated() {
        return generated;
    }
    
    public long getSize() {
        return size;
    }
    
    public PlayerAction getLastAction() {
        return lastAction;
    }
    
    public List<Pair<Unit,List<UnitAction> > > getChoices() {
        return choices;
    }

    public PlayerActionGenerator(GameState a_gs) {
        // Generate the reserved resources:
        base_ru = new PlayerAction(a_gs.getMapWidth(), a_gs.getMapHeight());
        gs = a_gs;
        
        for(Unit u:gs.getMyUnits()) {
            if (u.hasAction()) {
                base_ru.mergeResourceUsage(u, u.getAction());
            }
        }
        
        choices = new ArrayList<Pair<Unit,List<UnitAction> > >();
        for(Unit u:gs.getMyUnits()) {
            if (!u.hasAction() && u.getActions().size() > 0) {
                choices.add(new Pair<Unit,List<UnitAction>>(u,u.getActions()));
                size*=u.getActions().size();
            }
        }

        if (choices.size() == 0) {
        	return;
        }
        //if (choices.size()==0) throw new Exception("Move generator created with no units that can execute actions!");

        choiceSizes = new int[choices.size()];
        currentChoice = new int[choices.size()];
        int i = 0;
        for(Pair<Unit,List<UnitAction> > choice:choices) {
            choiceSizes[i] = choice.m_b.size();
            currentChoice[i] = 0;
            i++;
        }
    } 
    
    
    public void incrementCurrentChoice(int startPosition) {
        for(int i = 0;i<startPosition;i++) currentChoice[i] = 0;
        currentChoice[startPosition]++;
        if (currentChoice[startPosition]>=choiceSizes[startPosition]) {
            if (startPosition<currentChoice.length-1) {
                incrementCurrentChoice(startPosition+1);
            } else {
                moreActions = false;
            }
        }
    }

    
    public PlayerAction getNextAction(long cutOffTime) {
        int count = 0;
        while(moreActions) {
            boolean consistent = true;
            PlayerAction pa = new PlayerAction(base_ru);
            int i = choices.size();
            if (i == 0) {
            	return null;
            }
            //if (i==0) throw new Exception("Move generator created with no units that can execute actions!");
            while(i>0) {
                i--;
                Pair<Unit,List<UnitAction> > unitChoices = choices.get(i);
                int choice = currentChoice[i];
                Unit u = unitChoices.m_a;
                UnitAction ua = unitChoices.m_b.get(choice);
                
                if (pa.consistentResourceUsage(ua)) {
                	pa.mergeResourceUsage(u, ua);
                    pa.addUnitAction(u, ua);
                } else {
                    consistent = false;
                    break;
                }
            }
            
            incrementCurrentChoice(i);
            if (consistent) {
                lastAction = pa;
                generated++;                
                return pa;
            }
            
            // check if we are over time (only check once every 1000 actions, since currenttimeMillis is a slow call):
            if (cutOffTime!=-1 && (count%1000==0) && System.currentTimeMillis()>cutOffTime) {
                lastAction = null;
                return null;
            }
            count++;
        }
        lastAction = null;
        return null;
    }
    
    
    public PlayerAction getRandom() {
        Random r = new Random();
        PlayerAction pa = new PlayerAction(base_ru);
        for(Pair<Unit,List<UnitAction> > unitChoices:choices) {
            LinkedList<UnitAction> l = new LinkedList<UnitAction>();
            l.addAll(unitChoices.m_b);
            Unit u = unitChoices.m_a;
            
            boolean consistent = false;
            do{
                UnitAction ua = l.remove(r.nextInt(l.size()));
                if (pa.consistentResourceUsage(ua)) {
                    pa.mergeResourceUsage(u, ua);
                    pa.addUnitAction(u, ua);
                    consistent = true;
                }
            }while(!consistent);
        }      
        return pa;
    }
    
    
    public String toString() {
        String ret = "PlayerActionGenerator ";
        for(Pair<Unit,List<UnitAction> > choice:choices) {
            ret = ret + "(" + choice.m_a + "," + choice.m_b.size() + ") ";
        }
        return ret;
    }
}
