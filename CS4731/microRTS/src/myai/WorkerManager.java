package myai;

import static java.lang.Math.random;
import static myai.MapModule.P;
import static myai.MapModule.manhattan;
import static myai.Tuples.T;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import myai.Tuples.Tuple2;
import myai.Tuples.Tuple3;
import rts.GameState;
import rts.units.Unit;
import rts.units.UnitAction;
import rts.units.UnitDefinition;

public class WorkerManager {

	private LinkedHashMap<Long, TaskedUnit> myWorkers = new LinkedHashMap<Long, TaskedUnit>();

	private Set<Long> resourceMap = new HashSet<Long>();

	private LinkedHashSet<Farm> farms = new LinkedHashSet<Farm>();
	private static final int WITHIN_FARM_DIST = 5;
	private double CHANCE_PREFER_WAR = .1;

	public static Random random = new Random();
	
	public void init(GameState state) {
		//BANK
		for(int i = 0; i < state.getResourceTypes(); i++) {
			moneyOwed.add(0);
		}
	}

	public void preUnitLogic(MapModule map, GameState state) {
		for (Unit resource : map.resources) {
			if (!resourceMap.contains(resource.getID())) {
				// Resource is not associated with a farm
				Farm farmPick = null;
				for (Farm farm : farms) {
					if (manhattan(P(resource), farm.p) < WITHIN_FARM_DIST) {
						farmPick = farm;
						break;
					}
				}
				// Add resource to farm list
				if (farmPick != null) {
					resourceMap.add(resource.getID());
				} else {
					// Build new farm
					Farm farm = new Farm();
					farm.p = P(resource);
					farms.add(farm);
				}
			}
		}

		// Prune dead workers
		List<Long> aliveWorkers = new LinkedList<Long>();
		for (Unit unit : state.getMyUnits()) {
			if (unit.isWorker()) {
				aliveWorkers.add(unit.getID());
			}
		}

		// Prune dead workers
		Iterator<Entry<Long, TaskedUnit>> ite = myWorkers.entrySet().iterator();
		while (ite.hasNext()) {
			Entry<Long, TaskedUnit> s = ite.next();
			if (!aliveWorkers.contains(s.getValue().unit.getID())) {
				ite.remove();
				if (s.getValue().farm != null) {
					s.getValue().farm.workers--;
					s.getValue().farm.buildLock = false;
				}
				if (s.getValue().getLoan() != null) {
					//Unit died while in debt
					s.getValue().getLoan().payBackLoan();
				}
			}
		}
	}

	public void commandUnit(Unit unit, GameState state, MapModule map, UnitGeneral men) {
		
		// ASSIGN DEFAULT TASK
		if (!myWorkers.containsKey(unit.getID())) {
			myWorkers.put(unit.getID(), new TaskedUnit(unit, TaskType.SCOUT));
		}

		// FETCH CURRENT TASK

		boolean isPoor = unit.getResources() == 0;

		TaskedUnit UNIT = myWorkers.get(unit.getID());
		
		//System.out.println(UNIT.task);

		switch (UNIT.task) {
			case BUILD_NEW_AIRPORT:{
				//See below
			}
			case BUILD_NEW_STOCKPILE: {
				// See below
			}
			case BUILD_NEW_SOLDIER_OFFICE: {
				if (UNIT.goalPos != null) {
					// PATHFIND THERE AND BUILD
					Tuple3<Boolean, Double, List<Point>> path = map.findPath(P(unit), UNIT.goalPos, true, false);
					if (path.a && path.b < MapModule.NAV_THRESH) {
						// IF FAR, MOVE
						if (path.c.size() > 2) {
							men.findAndSetAction(unit, path.c.get(1), UnitAction.MOVE, true);
						} else {
							// IF CLOSE, DO
							UnitAction s = null;
							if (UNIT.task == TaskType.BUILD_NEW_STOCKPILE) {
								s = men.action_buildStockpile(unit.getActions(), path.c.get(1));
							} else if (UNIT.task == TaskType.BUILD_NEW_SOLDIER_OFFICE) {
								s = men.action_buildSoldierOffice(unit.getActions(), path.c.get(1));
							} if (UNIT.task == TaskType.BUILD_NEW_AIRPORT) {
								s = men.action_buildAirport(unit.getActions(), path.c.get(1));
							} 
							if (s != null) {
								unit.setAction(s);
								// Clear task
								setTask(unit, TaskType.HARVEST);
								UNIT.getLoan().payBackLoan();
								UNIT.clearBuildGoals();
							}
						}
					}
				} else {
					System.err.println("Worker told to build office but not given location!");
					setTask(unit, TaskType.HARVEST);
				}
				break;
			}
			case SCOUT: {
				if (findClosestOpenFarm(unit, farms).a) {
					UNIT.clearScoutGoals();
					setTask(unit, isPoor ? TaskType.HARVEST : TaskType.RETURN);
				} else {
					if (UNIT.goalPos == null) {
						UNIT.goalPos = P(random.nextInt(map.mapDim.width), random.nextInt(map.mapDim.height));
					}

					Tuple3<Boolean, Double, List<Point>> g = map.findPath(P(unit), UNIT.goalPos, true, unit.isFlying());
					if (g.a && g.c.size() > 2) {
						// MOVE
						men.findAndSetAction(unit, g.c.get(1), UnitAction.MOVE, true);
					} else {
						UNIT.clearScoutGoals();
					}
				}
				break;
			}
			case HARVEST: {
				// See Return thing below
				if (!isPoor || map.stockpile.isEmpty()) {
					setTask(unit, TaskType.RETURN);
				}

				// ASSIGN FARM IF DOESN'T HAVE ONE
				if (UNIT.farm == null) {
					// FIND A WILLING FARM
					Tuple2<Boolean, Farm> result = findClosestOpenFarm(unit, farms);
					if (result.a) {
						Farm mejorGranja = result.b;
						mejorGranja.workers++;
						UNIT.farm = mejorGranja;
					} else {
						// NO MORE FARMS
						setTask(unit, TaskType.SCOUT);
					}
				}
				if (UNIT.farm != null) {
					Farm farm = UNIT.farm;
					// Go to assigned farm if too far away
					boolean inFarm = manhattan(P(unit), farm.p) <= WITHIN_FARM_DIST;
					Point target = null;
					if (!inFarm) {
						target = farm.p;
					} else {
						Tuple2<Boolean, Unit> searchResult = map.findClosest(unit, map.resources);
						if (searchResult.a) {
							target = P(searchResult.b);
							if (manhattan(P(unit), target) > WITHIN_FARM_DIST) {
								// FARM IS A QUACK, REMOVE!
								farms.remove(farm);
								UNIT.farm = null;
								target = null;
							}
						}
					}
					if (target != null) {
						Tuple3<Boolean, Double, List<Point>> path = map.findPath(P(unit), target, true, unit.isFlying());
						if (path.a && path.b < MapModule.NAV_THRESH && path.c.size() > 1) {
							// IF FAR, MOVE
							if (path.c.size() > 2) {
								men.findAndSetAction(unit, path.c.get(1), UnitAction.MOVE, true);
							} else {
								// IF CLOSE, DO
								men.findAndSetAction(unit, path.c.get(1), UnitAction.HARVEST, false);
								// Check if there is a stockpile anywhere near
								// here.

								// POSSIBLY build a soldier office.
								Tuple2<Boolean, Unit> nearestStock = map.findClosest(unit, map.stockpile);
								if (!UNIT.farm.buildLock && nearestStock.b != null && manhattan(UNIT.farm.p, P(nearestStock.b)) > WITHIN_FARM_DIST) {
									// Assign somebody to build farm
									UnitDefinition ss = state.getBuildingList().get(men.STOCKPILE_BUILD_ID);
									if (ss.is_stockpile_building && canAfford(ss.cost, state)) {
										Tuple3<Boolean, Double, List<Point>> newPath = map.findPath(UNIT.farm.p,
												P(random.nextInt(map.mapDim.width), random.nextInt(map.mapDim.height)), true, 6, unit.isFlying());
										UNIT.goalFarm = UNIT.farm;
										UNIT.task = TaskType.BUILD_NEW_STOCKPILE;
										UNIT.goalPos = newPath.c.get(newPath.c.size() - 1);
										UNIT.farm.buildLock = true;
										UNIT.setLoan(new ConstructionLoan(ss.cost));
										UNIT.getLoan().takeOutLoan();
									}
								}
							}
						}
					}
				}
				break;
			}
			case RETURN: {

				if (isPoor) {
					setTask(unit, TaskType.HARVEST);
				} 
				if (map.stockpile.isEmpty()) {
					setTask(unit, TaskType.SCOUT);
				}

				// GO TO NEAREST THING
				Tuple2<Boolean, Unit> searchResult = map.findClosest(unit, map.stockpile);
				if (searchResult.a) {
					Tuple3<Boolean, Double, List<Point>> path = map.findPath(P(unit), P(searchResult.b), true, unit.isFlying());
					if (path.a && path.b < MapModule.NAV_THRESH) {
						// IF FAR, MOVE
						if (path.c.size() > 2) {
							men.findAndSetAction(unit, path.c.get(1), UnitAction.MOVE, false);
						} else {
							// IF CLOSE, DO
							men.findAndSetAction(unit, path.c.get(1), UnitAction.RETURN, false);

							// POSSIBLY build a soldier office.
							Tuple2<Boolean, Unit> nearestOffice = map.findClosest(unit, map.soldierOffices);
							if (UNIT.farm != null && (nearestOffice.b == null || manhattan(UNIT.farm.p, P(nearestOffice.b)) > WITHIN_FARM_DIST)) {
								// Assign somebody to build farm
								UnitDefinition ss = state.getBuildingList().get(men.SOLDIER_OFFICE_BUILD_ID);
								if (canAfford(ss.cost, state) && random() < CHANCE_PREFER_WAR) {
									Tuple3<Boolean, Double, List<Point>> newPath = map.findPath(UNIT.farm.p,
											P(random.nextInt(map.mapDim.width), random.nextInt(map.mapDim.height)), true, 6, unit.isFlying());
									UNIT.goalFarm = UNIT.farm;
									UNIT.task = Math.random() < .60 ? TaskType.BUILD_NEW_SOLDIER_OFFICE : TaskType.BUILD_NEW_AIRPORT;
									UNIT.goalPos = newPath.c.get(newPath.c.size() - 1);
									UNIT.farm.buildLock = true;
									UNIT.setLoan(new ConstructionLoan(ss.cost));
									UNIT.getLoan().takeOutLoan();
								}
							}
						}
					}
				}
				break;
			}
			default:
				System.err.println("Unit " + unit + " is in a bad state!");
				break;
		}
	}

	enum TaskType {
		HARVEST, SCOUT, RETURN, BUILD_NEW_STOCKPILE, BUILD_NEW_SOLDIER_OFFICE, BUILD_NEW_AIRPORT;
	}

	private void setTask(Unit unit, TaskType type) {
		myWorkers.get(unit.getID()).task = type;
	}

	public class Farm {
		Point p = null;
		int workers = 0;
		int workerLimit = 4;
		boolean buildLock = false;
	}

	public Tuple2<Boolean, Farm> findClosestOpenFarm(Unit unit, Collection<Farm> farms) {
		double b = Double.MAX_VALUE;
		Farm closest = null;
		for (Farm farm : farms) {
			// VALID TARGET FOUND
			double dist = manhattan(P(unit), farm.p);
			if (farm.workers < farm.workerLimit && (dist < b || (dist == b && .5 < random()))) {
				b = dist;
				closest = farm;
			}
		}

		return T(closest != null, closest);
	}

	public class TaskedUnit {
		Farm farm;
		TaskType task;
		Point goalPos;
		Farm goalFarm;
		final Unit unit;
		private ConstructionLoan loan;

		public TaskedUnit(Unit unit, TaskType task) {
			this.unit = unit;
			this.task = task;
		}

		public void clearBuildGoals() {
			goalPos = null;
			goalFarm.buildLock = false;
			goalFarm = null;
			loan = null;
		}

		public void clearScoutGoals() {
			goalPos = null;
		}

		public ConstructionLoan getLoan() {
			return loan;
		}

		public void setLoan(ConstructionLoan loasn) {
			this.loan = loasn;
		}
	}

	class ConstructionLoan {
		private final List<Integer> cost;

		public ConstructionLoan(List<Integer> cost) {
			this.cost = cost;
		}

		public void takeOutLoan() {
			for (int o = 0; o < cost.size(); o++){
				moneyOwed.set(o, moneyOwed.get(o) + cost.get(o));
			}
			//System.err.println("Loan taken");
		}

		public void payBackLoan() {
			for (int o = 0; o < cost.size(); o++){
				moneyOwed.set(o, moneyOwed.get(o) - cost.get(o));
			}
			//System.err.println("Loan returned");
		}
	}
	
	private final List<Integer> moneyOwed = new ArrayList<Integer>();

	private boolean canAfford(ArrayList<Integer> cost, GameState state) {
		for (int i = 0; i < state.getResourceTypes(); i++) {
			if (cost.get(i) + moneyOwed.get(i)> state.getResources(i)) {
				return false;
			}
		}
		return true;
	}
}
