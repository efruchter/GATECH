package ai.general;

import java.util.ArrayList;
import java.util.HashMap;

import ai.AI;

import rts.GameState;
import rts.units.Unit;
import rts.units.UnitAction;

/**
 * \package ai.general
 * \brief The GeneralAI implemented to play generic RTS games
 * @author Jeff Bernard
 */

/**
 * \brief General AI built to play any generically defined RTS game
 * @author Jeff Bernard
 */
public class GeneralAI extends AI {
	public static final int LESION_NONE = 0;
	public static final int LESION_WORKER_ARMY = 1;
	public static final int LESION_NO_DEFENSE = 2;
	public static final int LESION_ONLY_RANGE = 4;
	public static final int LESION_NO_RANGE = 8;
	public static final int LESION_ONLY_FLYING = 16;
	public static final int LESION_NO_FLYING = 32;
	public static final int LESION_CHEAPEST_ARMY = 64;
	public static final int LESION_EXPENSIVE_ARMY = 128;
	public static final int LESION_WEAKEST_ARMY = 256;
	public static final int LESION_STRONGEST_ARMY = 512;
	
	public static final int STRATEGY_NONE = 0; /**< no strategy */ 
	public static final int STRATEGY_BUILD = 1; /**< build */
	public static final int STRATEGY_FARM = 2; /**< gather resources */
	public static final int STRATEGY_ATTACK = 3; /**< attack enemy */
	public static final int STRATEGY_EXPLORE = 4; /**< explore the map strat */
	public static final int STRATEGY_DEFENSE = 5; /**< defend a position */
	
	public static final int DISTANCE_IGNORE = -1; /**< ignore this distance */
	
	private boolean init; /**< whether or not we have init */
	
	private long turn_start; /**< when the turn started */
	private long turn_limit; /**< maximum length for turn */
	
	public TrafficMap traffic_map; /**< a map of our unit traffic */
	
	public ExplorationManager exploration_manager; /**< exploration manager */
	public ProductionManager production_manager; /**< production manager */
	public AttackManager attack_manager; /**< attack manager */
	public FarmManager farm_manager; /**< farm manager */
	public TownManager town_manager; /**< town manager */
	
	
	//private int worker_count; /**< how many workers we have */
	
	public GameState state; /**< the game state */
	public ArrayList<Integer> money; /**< money */
	
	public ArrayList<GeneralAIUnit> units; /**< my units */
	
	public int current_turn; /**< what turn is it */
	public int player_id; /**< the id of the player this ai belongs to */
	
	private int last_unit; /**< the last unit to make a move */
	
	/**
	 * Constructs the ai
	 */
	public GeneralAI() {
		super();
		
		init = false;
		current_turn = 0;
		last_unit = 0;
		
		//worker_count = 0;
		player_id = -1;
		
		farm_manager = new FarmManager();		
		town_manager = new TownManager();
		
		money = new ArrayList<Integer>();
		units = new ArrayList<GeneralAIUnit>();
	}
	
	public GeneralAI(int _lesion) {
		this();
		setLesion(_lesion);
	}
	
	/**
	 * The real constructor, basically
	 */
	private void initialize() {
		traffic_map = new TrafficMap(state.getMap().length);
		
		// I must own a unit...
		player_id = state.getMyUnits().get(0).getPlayer();
		
		// bookkeeping need to run first
		exploration_manager = new ExplorationManager(this);
		production_manager = new ProductionManager(this);
		attack_manager = new AttackManager(production_manager.units_possible);
		
		for (int i = 0; i < state.getResourceTypes(); i++) {
			money.add(state.getResources(i));
		}
		
		init = true;
	}

	@Override
	/**
	 * Issues actions to units
	 *@param gs the game state
	 *@param time_limit how much time is given for this turn (msec)
	 */
	public void getAction(GameState gs, int time_limit) {
		turn_start = System.currentTimeMillis();
		turn_limit = time_limit;
		state = gs;
		
		current_turn++;
		
		if (!init) {
			initialize();
		}
		
		update_unit_list();
		
		traffic_map.update(current_turn);
		
		production_manager.update(this);
		farm_manager.update(this);
		attack_manager.update(this);
		exploration_manager.update(this);
		town_manager.update(this);
		
		production_manager.manage_units(this);
		farm_manager.manage_units(this);
		town_manager.manage_units(this);
		attack_manager.manage_units(this);
		exploration_manager.manage_units(this);
		
		// execute unit orders
		boolean finished = true;
		boolean builders = false;
		for (int i = last_unit; i < units.size(); i++) {
			if (System.currentTimeMillis()-turn_start > turn_limit) {
//				System.out.println(player_id+" out of time on turn "+current_turn);
				last_unit = i;
				builders = false;
				finished = false;
				break; // out of time
			}
			units.get(i).act(this);
			if (units.get(i).stats.hasAction() && units.get(i).stats.getAction().getType() == UnitAction.BUILD) {
				builders = true;
			}
		}
		if (finished) {
			last_unit = 0;
		}
		if (!builders) {
			for (int i = 0; i < money.size(); i++) {
				money.set(i, state.getResources(i));
			}
		}
	}
	
	/**
	 * Updates the unit list
	 */
	private void update_unit_list() {
		for (int j = 0; j < units.size(); j++) {
			units.get(j).exists = false;
			units.get(j).wanted_strategy = STRATEGY_NONE;
		}
		
		for (int i = 0; i < state.getMyUnits().size(); i++) {
			boolean found = false;
			for (int j = 0; j < units.size(); j++) {
				if (units.get(j).equals(state.getMyUnits().get(i))) {
					units.get(j).exists = true;
					found = true;
					break;
				}
			}
			if (!found) {
				GeneralAIUnit unit = new GeneralAIUnit(state.getMyUnits().get(i), this);
				units.add(unit);
				if (unit.stats.isBuilding()) {
					// add to a town
					boolean added = false;
					for (int j = 0; j < town_manager.towns.size(); j++) {
						if (town_manager.towns.get(j).add(unit)) {
							added = true;
							break;
						}
					}
					if (!added) {
						// start a new town based around this building
						town_manager.towns.add(new GeneralAITown(unit, production_manager.buildings_possible.size()));
					}
					
					production_manager.units.add(unit);
				} else if (unit.stats.isWorker() && production_manager.workers_queued > 0) {
					production_manager.workers_queued--;
				}
			}
		}
		
		for (int j = 0; j < units.size(); j++) {
			if (!units.get(j).exists) {
				if (units.get(j).object != null) {
					units.get(j).remove(traffic_map);
					units.get(j).object.remove(units.get(j), this);
				}
				if (units.get(j).stats.isBuilding()) {
					// need to remove from the town
					for (int i = 0; i < town_manager.towns.size(); i++) {
						if (town_manager.towns.get(i).remove(units.get(j))) {
							if (town_manager.towns.get(i).population() == 0) {
								town_manager.towns.remove(i);
							}
							break;
						}
					}
					production_manager.remove_unit(units.get(j).stats.getID());
				} else {
					if (units.get(j).stats.isWorker()) {
						farm_manager.remove_unit(units.get(j).stats.getID());
					}
					exploration_manager.remove_unit(units.get(j).stats.getID());
					attack_manager.remove_unit(units.get(j));
					town_manager.remove_unit(units.get(j).stats.getID());
				}
				units.remove(j--);
			}
		}
	}
	
	/**
	 * Checks whether or not a unit can enter a location
	 * @param unit the unit
	 * @param location the location
	 * @return whether or not
	 */
	private boolean can_enter(Unit unit, int location, int turn_start, int turn_end) {
		if ((exploration_manager.map[location]&(GameState.MAP_NEUTRAL|GameState.MAP_NONPLAYER)) == 0 && ((exploration_manager.map[location]&GameState.MAP_WALL) == 0 || unit.isFlying()) && traffic_map.valid(location, turn_start, turn_end)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Calculates the h from start to (a) goal
	 * @param start
	 * @param goals
	 * @return
	 */
	private int h_score(int start, ArrayList<Integer> goals) {
		int h = DISTANCE_IGNORE;
		for (int i = 0; i < goals.size(); i++) {
			int dx = goals.get(i)%state.getMapWidth()-start%state.getMapWidth();
			int dy = goals.get(i)/state.getMapWidth()-start/state.getMapWidth();
			int dh = dx*dx+dy*dy;
			if (h == DISTANCE_IGNORE || dh < h) {
				h = dh;
			}
		}
		return h;
	}
	
	/**
	 * Gets the path to a location (reversed)
	 * @param unit
	 * @param destinations
	 */
	public ArrayList<Integer[]> get_path(Unit unit, int start, int turn_start, ArrayList<Integer> destinations) {
		// multi-destination A*
		ArrayList<Integer> closed = new ArrayList<Integer>();
		ArrayList<Integer> open = new ArrayList<Integer>();
		open.add(start);
		HashMap<Integer, Integer> came_from = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> g_score = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> f_score = new HashMap<Integer, Integer>();
		g_score.put(start, 0);
		f_score.put(start, h_score(start, destinations));
		
		while (open.size() > 0) {
			int m = 0;
			int current = open.get(m);
			for (int i = 1; i < open.size(); i++) {
				if (f_score.get(open.get(i)) < f_score.get(current)) {
					current = open.get(i);
					m = i;
				}
			}
			int time = g_score.get(current)*unit.getMoveSpeed()+turn_start;
			int end = time+unit.getMoveSpeed();
			
			if (destinations.contains(current)) {
				ArrayList<Integer[]> path = new ArrayList<Integer[]>();
				if (current == start) {
					return path;
				}
				path.add(new Integer[]{current, time});
				while (came_from.get(current) != null && came_from.get(current) != start) {
					current = came_from.get(current);
					time = g_score.get(current)*unit.getMoveSpeed()+turn_start;
					path.add(new Integer[]{current, time});
				}
				return path;
			}
			
			open.remove(m);
			closed.add(current);
			int next_g = g_score.get(current)+1;
			int cx = current%state.getMapWidth();
			int cy = current/state.getMapWidth();
			int next = current-1;
			if (cx > 0 && (destinations.contains(next) || (!closed.contains(next) && can_enter(unit, next, time, end)))) { // left exists
				if (!open.contains(next) || next_g < g_score.get(next)) {
					came_from.put(next, current);
					g_score.put(next, next_g);
					f_score.put(next, next_g+h_score(next, destinations));
					if (!open.contains(next)) {
						open.add(next);
					}
				}
			}
			next = current+1;
			if (cx < state.getMapWidth()-1 && (destinations.contains(next) || (!closed.contains(next) && can_enter(unit, next, time, end)))) { // right exists
				if (!open.contains(next) || next_g < g_score.get(next)) {
					came_from.put(next, current);
					g_score.put(next, next_g);
					f_score.put(next, next_g+h_score(next, destinations));
					if (!open.contains(next)) {
						open.add(next);
					}
				}
			}
			next = current-state.getMapWidth();
			if (cy > 0 && (destinations.contains(next) || (!closed.contains(next) && can_enter(unit, next, time, end)))) { // up exists
				if (!open.contains(next) || next_g < g_score.get(next)) {
					came_from.put(next, current);
					g_score.put(next, next_g);
					f_score.put(next, next_g+h_score(next, destinations));
					if (!open.contains(next)) {
						open.add(next);
					}
				}
			}
			next = current+state.getMapWidth();
			if (cy < state.getMapHeight()-1 && (destinations.contains(next) || (!closed.contains(next) && can_enter(unit, next, time, end)))) { // down exists
				if (!open.contains(next) || next_g < g_score.get(next)) {
					came_from.put(next, current);
					g_score.put(next, next_g);
					f_score.put(next, next_g+h_score(next, destinations));
					if (!open.contains(next)) {
						open.add(next);
					}
				}
			}
		}
		return null;
	}
	
	/**
     * Allows the agent to label itself
     * @return a label
     */
	@Override
	public String getLabel() {		
		String label = "gAI";
		if ((getLesion()&LESION_WORKER_ARMY) != 0) {
			label += "+WAtk";
		}
		if ((getLesion()&LESION_NO_DEFENSE) != 0) {
			label += "-Def";
		}
		if ((getLesion()&LESION_ONLY_RANGE) != 0) {
			label += "+R";
		}
		if ((getLesion()&LESION_NO_RANGE) != 0) {
			label += "-R";
		}
		if ((getLesion()&LESION_ONLY_FLYING) != 0) {
			label += "+F";
		}
		if ((getLesion()&LESION_NO_FLYING) != 0) {
			label += "-F";
		}
		if ((getLesion()&LESION_CHEAPEST_ARMY) != 0) {
			label += "-X";
		}
		if ((getLesion()&LESION_EXPENSIVE_ARMY) != 0) {
			label += "+X";
		}
		if ((getLesion()&LESION_WEAKEST_ARMY) != 0) {
			label += "-x";
		}
		if ((getLesion()&LESION_STRONGEST_ARMY) != 0) {
			label += "+x";
		}
		return label;
	}
}
