package ai.general;

import java.util.ArrayList;

import rts.units.Unit;
import rts.units.UnitAction;

/**
 * \brief Model of enemy units
 * @author Jeff Bernard
 */
public class GeneralAIEnemy extends GeneralAIObject {
	public Unit stats; /**< the last known unit object for this enemy */
	public boolean dead; /**< whether or not this guy is dead */
	public boolean seen; /**< whether or not this guy has been seen before */
	public int priority; /**< additionaly priority to kill this unit */
	
	/**
	 * Constructs a new enemy knowledge thingy
	 * @param unit
	 */
	public GeneralAIEnemy(Unit unit) {
		stats = unit;
		dead = false;
		seen = true;
		priority = 0;
	}

	@Override
	public void order_unit(GeneralAIUnit unit, GeneralAI ai) {
		if (dead) {			
			unit.clearActions(ai.traffic_map);
			unit.object = null;
		} else if (seen) {
			// check if we can just attack...
			for (int i = 0; i < unit.stats.getActions().size(); i++) {
				UnitAction action = unit.stats.getActions().get(i);
				if (action.getType() == UnitAction.ATTACK && action.getTargetX() == stats.getX() && action.getTargetY() == stats.getY()) {
					unit.addAction(new UnitAction(unit.stats, UnitAction.ATTACK, stats.getX(), stats.getY(), -1), ai.traffic_map, unit.stats.getX()+unit.stats.getY()*ai.state.getMapWidth(), ai.current_turn, -1);
					return;
				}
			}
			
			ArrayList<Integer> openings = new ArrayList<Integer>();
			for (int j = stats.getY()-unit.stats.getAttackRange(); j <= stats.getY()+unit.stats.getVision(); j++) {
				if (j >= 0 && j < ai.state.getMapHeight()) {  
					for (int k = stats.getX()-unit.stats.getAttackRange(); k <= stats.getX()+unit.stats.getAttackRange(); k++) {
						if (k >= 0 && k < ai.state.getMapWidth() && (j-stats.getY())*(j-stats.getY())+(k-stats.getX())*(k-stats.getX()) < unit.stats.getAttackRange()*unit.stats.getAttackRange()) {
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
					//rpath.add(new Integer[]{unit.stats.getX()+unit.stats.getY()*ai.state.getMapWidth(), ai.current_turn});
					for (int i = rpath.size()-1; i >= 0; i--) {
						//unit.actions.add(new UnitAction(unit.stats, UnitAction.MOVE, rpath.get(i)%ai.state.getMapWidth(), rpath.get(i)/ai.state.getMapWidth(),-1));
						//System.out.println("adding MOVE");
						unit.addAction(new UnitAction(unit.stats, UnitAction.MOVE, rpath.get(i)[0]%ai.state.getMapWidth(), rpath.get(i)[0]/ai.state.getMapWidth(),-1), ai.traffic_map, rpath.get(i)[0], rpath.get(i)[1], rpath.get(i)[1]+unit.stats.getMoveSpeed());
					}
				}
				int now_at = rpath.get(0)[0];
				int now_start = rpath.get(0)[1];
				
				//System.out.println("adding ATTACK");
				unit.addAction(new UnitAction(unit.stats, UnitAction.ATTACK, stats.getX(), stats.getY(), -1), ai.traffic_map, now_at, now_start, -1);
			} else {
				// oh no, can't reach this enemey!?
				remove(unit, ai);
				unit.dont_attack.add(stats.getID());
			}
		} else {
			// can't reach this enemy
			remove(unit, ai);
		}
	}

	@Override
	public int distance(GeneralAIUnit unit, GeneralAI ai) {
		if (dead || !seen || unit.dont_attack.contains(stats.getID())) {
			return GeneralAI.DISTANCE_IGNORE;
		}
		int distance = (stats.getX()-unit.stats.getX())*(stats.getX()-unit.stats.getX())+(stats.getY()-unit.stats.getY())*(stats.getY()-unit.stats.getY());
		if (distance < unit.stats.getVision()*unit.stats.getVision()) {
			// ensure this unit is actually on my unit list
			boolean found = false;
			for (int i = 0; i < ai.state.getOtherUnits().size(); i++) {
				if (ai.state.getOtherUnits().get(i).getID() == stats.getID()) {
					found = true;
					break;
				}
			}
			if (!found) {
				seen = false;
				return GeneralAI.DISTANCE_IGNORE; // this unit may very well be dead, but we missed the signal somehow
			}
		}
		distance -= priority;
		if (!stats.isBuilding()) { // I prefer to kill units before buildings
			if (distance < 0) {
				distance *= 2;
			} else {
				distance /= 2;
			}
		}
		return distance <= GeneralAI.DISTANCE_IGNORE ? distance-1 : distance;
	}

	@Override
	public void action_succeeded(GeneralAIUnit unit, GeneralAI ai, int type) {
		// NaN
	}

	@Override
	public void remove(GeneralAIUnit unit, GeneralAI ai) {
		unit.clearActions(ai.traffic_map);
		unit.object = null;
		
		for (int i = 0; i < ai.attack_manager.units.size(); i++) {
			for (int j = 0; j < ai.attack_manager.units.get(i).dont_attack.size(); j++) {
				if (ai.attack_manager.units.get(i).dont_attack.get(j) == stats.getID()) {
					ai.attack_manager.units.get(i).dont_attack.remove(j);
					break;
				}
			}
		}
	}
	
	/**
	 * Evaluates how good of a unit this is
	 * @return
	 */
	public float evaluate() {
		if (stats.isBuilding()) {
			return 1;
		}
		return(((stats.getAttackMax()*stats.getAttackRange()+stats.getVision())*((float)stats.getHP()/(float)stats.getMaxHP())+(stats.isFlying()?1:0))/(float)(stats.getAttackSpeed()+stats.getMoveSpeed()));
	}

	@Override
	public void update_orders(GeneralAIUnit unit, GeneralAI ai) {
		// double check we are still next to the enemy
		//if (unit.actions.size() != 0 && unit.actions.get(0).getType() == UnitAction.ATTACK) {
			unit.clearActions(ai.traffic_map);
			order_unit(unit, ai);
		//}
	}

}
