package ai.general;

import java.util.ArrayList;

import rts.units.UnitAction;

/**
 * \brief Model of the map that needs to be explored
 * @author Jeff Bernard
 *
 */
public class GeneralAIExploration extends GeneralAIObject {
	private static final int MAX_EFFORT = 50; /**< honestly, don't try too hard to explore... */
	
	public int location; /**< where this explore spot is */ 
	public int last_seen; /**< the turn where we last saw this square */
	
	private boolean claimed; /**< whether or not someone is already exploring here */
	private int claim_start;
	private int effort; /**< effort put into exploration */
	
	/**
	 * Constructs a new object of this
	 * @param spot
	 */
	public GeneralAIExploration(int spot) {
		location = spot;
		last_seen = -1;
		claimed = false;
		claim_start = -1;
		effort = 0;
	}

	@Override
	public void order_unit(GeneralAIUnit unit, GeneralAI ai) {
		if (!claimed) {
			claimed = true;
			claim_start = last_seen;
			effort = 0;
		} else if (++effort > MAX_EFFORT) {
			remove(unit, ai);
		}
		
		/*if (last_seen != -1) {
			claimed = false;
			//unit.actions.clear();
			unit.clearActions(ai.traffic_map);
			unit.object = null;
			return;
		}*/
		
		ArrayList<Integer> destination = new ArrayList<Integer>();
		destination.add(location);
		
		ArrayList<Integer[]> rpath = ai.get_path(unit.stats, unit.stats.getX()+unit.stats.getY()*ai.state.getMapWidth(), ai.current_turn, destination);
		
		if (rpath != null) { // is possible to reach goal
			// set order queue
			//rpath.add(new Integer[]{unit.stats.getX()+unit.stats.getY()*ai.state.getMapWidth(), ai.current_turn});
			for (int i = rpath.size()-1; i >= 0; i--) {
				//unit.actions.add(new UnitAction(unit.stats, UnitAction.MOVE, rpath.get(i)%ai.state.getMapWidth(), rpath.get(i)/ai.state.getMapWidth(),-1));
				unit.addAction(new UnitAction(unit.stats, UnitAction.MOVE, rpath.get(i)[0]%ai.state.getMapWidth(), rpath.get(i)[0]/ai.state.getMapWidth(),-1), ai.traffic_map, rpath.get(i)[0], rpath.get(i)[1], rpath.get(i)[1]+unit.stats.getMoveSpeed());
			}
		} else {

		}
	}

	@Override
	public int distance(GeneralAIUnit unit, GeneralAI ai) {						
//		if (claimed) {
//			return GeneralAI.DISTANCE_IGNORE;
//		}
		
		int x = location%ai.state.getMapWidth();
		int y = location/ai.state.getMapWidth();
		
		int unreward = last_seen;
		if (unreward == -1) {			
			unreward = unit.stats.getVision()*unit.stats.getVision();
			for (int i = y-unit.stats.getVision(); i <= y+unit.stats.getVision(); i++) {
				if (i >= 0 && i < ai.state.getMapHeight()) {
					for (int j = x-unit.stats.getVision(); j <= x+unit.stats.getVision(); j++) {
						if (j >= 0 && j < ai.state.getMapWidth()) {
							if ((x-j)*(x-j)+(y-i)*(y-i) <= unit.stats.getVision()*unit.stats.getVision()) {
								if (((GeneralAIExploration)ai.exploration_manager.exploration.get(j+i*ai.state.getMapWidth())).last_seen != -1) {
									unreward--;
								}
							}
						}
					}
				}
			}
		}
		return unreward+(x-unit.stats.getX())*(x-unit.stats.getX())+(y-unit.stats.getY())*(y-unit.stats.getY())+effort+(claimed?ai.state.getMap().length:0);
	}

	@Override
	public void action_succeeded(GeneralAIUnit unit, GeneralAI ai, int type) {
		if (last_seen != claim_start) {
			//unit.actions.clear();
			unit.clearActions(ai.traffic_map);
			unit.object = null;
			claimed = false;
		}
	}

	@Override
	public void remove(GeneralAIUnit unit, GeneralAI ai) {
		claimed = false;
		unit.clearActions(ai.traffic_map);
	}
	
	@Override
	public void update_orders(GeneralAIUnit unit, GeneralAI ai) {
		unit.clearActions(ai.traffic_map);
		order_unit(unit, ai);
	}

}
