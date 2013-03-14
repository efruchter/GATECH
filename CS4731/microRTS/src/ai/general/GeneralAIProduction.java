package ai.general;

import java.util.ArrayList;

import rts.GameState;
import rts.units.UnitAction;
import rts.units.UnitDefinition;

/**
 * \brief A task that needs to be produced
 * @author Jeff Bernard
 *
 */
public class GeneralAIProduction extends GeneralAIObject {
	public UnitDefinition def; /**< unit definition */
	public int id; /**< id in unit/building list */
	public int x; /**< where to build this */
	public int y; /**< where to build this */
	public int priority; /**< how badly we want this unit. -1 is not all, 0 is most of all, > 0 sort of want */
	
	public float cost_ratio; /**< cost of building this unit, with respect to it's fight utility */
	
	public long builder; /**< id of the builder */ 
	
	/**
	 * Makes a new thingy
	 * @param definition
	 * @param _id the index in unit/building list
	 * @param _x
	 * @param _y
	 */
	public GeneralAIProduction(UnitDefinition definition, int _id) {
		def = definition;
		id = _id;
		priority = GeneralAI.DISTANCE_IGNORE;
		//x = _x;
		//y = _y;
		builder = -1;
		
		x = -1;
		y = -1;
		
		int total_cost = 0;
		for (int i = 0; i < def.cost.size(); i++) {
			total_cost += def.cost.get(i);
		}
		cost_ratio = GeneralAIUnit.evaluate(def)/total_cost;
	}
	
	/**
	 * A production that we want, along with where we want it!
	 * @param product
	 * @param _x
	 * @param _y
	 * @param _priority
	 */
	public GeneralAIProduction(GeneralAIProduction product, int _x, int _y, int _priority) {
		def = product.def;
		id = product.id;
		priority = _priority;
		cost_ratio = product.cost_ratio;
		
		x = _x;
		y = _y;
		builder = -1;
	}
	

	@Override
	public void order_unit(GeneralAIUnit unit, GeneralAI ai) {
		if (def.is_building) {			
			// first make sure path to next to build spot
			ArrayList<Integer> destination = new ArrayList<Integer>();
			if (x > 0 && (ai.exploration_manager.map[x+y*ai.state.getMapWidth()-1]&(GameState.MAP_WALL|GameState.MAP_NEUTRAL|GameState.MAP_NONPLAYER)) == 0) {
				destination.add(x+y*ai.state.getMapWidth()-1);
			}
			if (x < ai.state.getMapWidth()-1 && (ai.exploration_manager.map[x+y*ai.state.getMapWidth()+1]&(GameState.MAP_WALL|GameState.MAP_NEUTRAL|GameState.MAP_NONPLAYER)) == 0) {
				destination.add(x+y*ai.state.getMapWidth()+1);
			}
			if (y > 0 && (ai.exploration_manager.map[x+(y-1)*ai.state.getMapWidth()]&(GameState.MAP_WALL|GameState.MAP_NEUTRAL|GameState.MAP_NONPLAYER)) == 0) {
				destination.add(x+y*ai.state.getMapWidth()-ai.state.getMapWidth());
			}
			if (y < ai.state.getMapHeight()-1 && (ai.exploration_manager.map[x+(y+1)*ai.state.getMapWidth()]&(GameState.MAP_WALL|GameState.MAP_NEUTRAL|GameState.MAP_NONPLAYER)) == 0) {
				destination.add(x+y*ai.state.getMapWidth()+ai.state.getMapWidth());
			}
			
			ArrayList<Integer[]> rpath = ai.get_path(unit.stats, unit.stats.getX()+unit.stats.getY()*ai.state.getMapWidth(), ai.current_turn, destination);
			
			if (rpath != null) { // is possible to reach goal
				// set order queue
				//rpath.add(new Integer[]{unit.stats.getX()+unit.stats.getY()*ai.state.getMapWidth(), ai.current_turn});
				for (int i = rpath.size()-1; i >= 0; i--) {
					//unit.actions.add(new UnitAction(unit.stats, UnitAction.MOVE, rpath.get(i)%ai.state.getMapWidth(), rpath.get(i)/ai.state.getMapWidth(),-1));
					unit.addAction(new UnitAction(unit.stats, UnitAction.MOVE, rpath.get(i)[0]%ai.state.getMapWidth(), rpath.get(i)[0]/ai.state.getMapWidth(),-1), ai.traffic_map, rpath.get(i)[0], rpath.get(i)[1], rpath.get(i)[1]+unit.stats.getMoveSpeed());
				}
				if (rpath.size() == 0) {
					rpath.add(new Integer[]{unit.stats.getX()+unit.stats.getY()*ai.state.getMapWidth(), ai.current_turn});
				}
				
				unit.addAction(new UnitAction(unit.stats, UnitAction.BUILD, x, y, id), ai.traffic_map, rpath.get(0)[0], rpath.get(0)[1], -1);
			} else {
				//System.out.println("could not path to: "+(location%16)+"x"+(location/16));
			}
		} else {
			// easy, just pick the adjacent spot
			for (int i = 0; i < unit.stats.getActions().size(); i++) {
				UnitAction action = unit.stats.getActions().get(i);
				if (action.getBuild() == id && ai.traffic_map.valid(action.getTargetX()+action.getTargetY()*ai.state.getMapWidth(), ai.current_turn, ai.current_turn+def.produce_speed) /*&& action.getTargetX() == x && action.getTargetY() == y*/) {
					//unit.actions.add(action);
					unit.addAction(action, ai.traffic_map, action.getTargetX()+action.getTargetY()*ai.state.getMapWidth(), ai.current_turn, ai.current_turn+def.produce_speed);
					break;
				}
			}
		}
		priority = GeneralAI.DISTANCE_IGNORE;
	}

	@Override
	public int distance(GeneralAIUnit unit, GeneralAI ai) {
		// ensure unit can build this guy
		if (unit.stats.isBuilding() && !def.is_building) {
			if (!unit.stats.getProduce().contains(id)) {
				return GeneralAI.DISTANCE_IGNORE; // can't even build
			}
		} else if (!(unit.stats.isWorker() && def.is_building)){
			return GeneralAI.DISTANCE_IGNORE;
		}
		for (int i = 0; i < def.cost.size(); i++) {
			if (ai.money.get(i) < def.cost.get(i)) {
				return GeneralAI.DISTANCE_IGNORE;
			}
		}
		if (def.is_worker) {
			return priority-GeneralAI.DISTANCE_IGNORE; // special priority for workers
		} else if (priority == GeneralAI.DISTANCE_IGNORE) {
			return GeneralAI.DISTANCE_IGNORE;
		}
		int distance = (unit.stats.getX()-x)*(unit.stats.getX()-x)+(unit.stats.getY()-y)*(unit.stats.getY()-y);
		return (int)(priority*cost_ratio*def.produce_speed)+distance;
	}

	@Override
	public void action_succeeded(GeneralAIUnit unit, GeneralAI ai, int type) {
		if (type == UnitAction.BUILD){
			unit.clearActions(ai.traffic_map);
			unit.object = null;
		}
	}

	@Override
	public void remove(GeneralAIUnit unit, GeneralAI ai) {
		if (def.is_building) {
			builder = -1;
			ai.production_manager.buildings_wanted.add(this);
		}
		unit.clearActions(ai.traffic_map);
	}

	/**
	 * evaluates this unit
	 * @return
	 */
	public float evaluate() {
		return GeneralAIUnit.evaluate(def);
	}


	@Override
	public void update_orders(GeneralAIUnit unit, GeneralAI ai) {
		unit.clearActions(ai.traffic_map);
		order_unit(unit, ai);
	}
}
