package rts.units;

import rts.PhysicalGameState;

/**
 * \brief This is an action that a unit can take
 * @author Jeff Bernard
 *
 */
public class UnitAction {
	public static final int NONE    = 0; 
	public static final int MOVE    = 1; /**< action type */
	public static final int ATTACK  = 2; /**< action type */
	public static final int HARVEST = 3; /**< action type */
	public static final int RETURN  = 4; /**< action type */
	public static final int BUILD   = 5; /**< action type */
	public static final int UPGRADE = 6;
	public static final int ATTACK_KILL = 7;
	
	public static final int DEFAULT_COOLDOWN = 2; /**< the default time for an action */
	
	private long timestamp; /**< when this action was issued */
	private int type; /**< what type of move this is */
	private long unitID; /**< the id of the unit this is for */
	
	private int targetX; /**< where this action culminates */
	private int targetY; /**< where this action culminates */
	
	private int build; /**< what to build */
	
	private int cooldown; /**< how long this action has before it goes */
	private boolean is_ready; /**< whether or not this action is ready to execute */
	
	private boolean isValidated; /**< whether or not this action has been validated as legal */
	
	/**
	 * Constructs a new unit action
	 * @param unit the unit this action is for
	 * @param action_type what type of action this
	 * @param target_x x target coordinate
	 * @param target_y y target coordinate
	 * @param production (only for action_type == BUILD) what to build
	 */
	public UnitAction(Unit unit, int action_type, int target_x, int target_y, int production) {
		this(unit.getID(), action_type, target_x, target_y, production);
	}
	
	private UnitAction(long id, int action_type, int target_x, int target_y, int production) {
		unitID = id;
		type = action_type;
		
		targetX = target_x;
		targetY = target_y;
		
		build = production;
		
		isValidated = false;
		is_ready = false;
	}
	
	public UnitAction(UnitStats unit, int action_type, int target_x, int target_y, int production) {
		unitID = unit.id;
		type = action_type;
		
		targetX = target_x;
		targetY = target_y;
		
		build = production;
		
		isValidated = false;
		is_ready = false;
	}
	
	/**
	 * Gets type
	 * @return
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * Gets target x
	 * @return
	 */
	public int getTargetX() {
		return targetX;
	}
	
	/**
	 * Gets target y
	 * @return
	 */
	public int getTargetY() {
		return targetY;
	}
	
	/**
	 * Gets build
	 * @return
	 */
	public int getBuild() {
		return build;
	}
	
	/**
	 * Returns the timestamp for this action
	 * @return timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}
	
	/**
	 * Sets the timestamp to now
	 */
	public void setTimestamp() {
		timestamp = System.currentTimeMillis();
	}
	
	/**
	 * Returns the unit ID
	 * @return
	 */
	public long getUnitID() {
		return unitID;
	}
	
	/**
	 * Returns whether or not this action is ready to execute
	 * @return
	 */
	public boolean ready() {
		boolean ret = is_ready;
		is_ready = false;
		return ret;
	}
	
	public boolean cooldown() {
		return(--cooldown<=0);
	}
	
	/**
	 * Validates this action as legal or not
	 * @param unit the unit who is doing this action
	 * @param pgs the game state
	 * @return
	 */
	public boolean validate(UnitStats unit, PhysicalGameState pgs) {
		if (isValidated) {
			return true;
		}
		is_ready = true;
		if (unit.id == unitID) {
			cooldown = DEFAULT_COOLDOWN;
			switch (type) {
				case MOVE:
					cooldown = unit.definition.move_speed;
					isValidated = true;
					break;
				case ATTACK:
					cooldown = unit.definition.attack_speed;
					isValidated = true;
					break;
				case HARVEST:
					cooldown = pgs.harvestTime(targetX, targetY);
					isValidated = true;
					break;
				case RETURN:
					isValidated = true;
					break;
				case BUILD:
					cooldown = pgs.buildTime(unit.player, !unit.definition.is_building, build);
					isValidated = true;
					break;
				case UPGRADE:
					cooldown = pgs.upgradeTime(unit.player, build);
					isValidated = true;
					break;
			}
		}
		return isValidated;
	}
	
	/**
	 * Executes this action
	 * @param unit the unit this action is for
	 * @param pgs the phsyical game state
	 * @return whether or not this action succeeded
	 */
	public boolean execute(UnitStats unit, PhysicalGameState pgs) {
		// obviously, but no harm checking for the umpteenth time
		if (unit.id == unitID) {
			boolean result = false;
			switch (type) {
				case MOVE:
					result = pgs.moveUnit(unit, targetX, targetY);
					break;
				case ATTACK:
					result = pgs.attackUnit(unit, targetX, targetY);
					break;
				case HARVEST:
					result = pgs.harvestUnit(unit, targetX, targetY);
					break;
				case RETURN:
					result = pgs.returnUnitHarvest(unit, targetX, targetY);
					break;
				case BUILD:
					result = pgs.buildUnit(unit, targetX, targetY, build);
					if (!result) {
						//pgs.restoreResources(unit.player, !unit.definition.is_building, build);
					}
					break;
				case UPGRADE:
					result = pgs.upgradeUnits(unit.player, build);
					break;
			}
			//if (!result) { System.out.println(targetX+"x"+targetY);System.exit(-1);}
			unit.last_action_success = result;
//			if (!result) {
//				System.out.println(unit.definition.label+"("+unit.id+") failed to execute action of type: "+type);
//			}
			return result;
		}
		return false;
	}
	
	/**
	 * Compares two actions to see if they are equal
	 * @param other the other action to compare with
	 * @return true or false
	 */
	public boolean equals(UnitAction other) {
		return(unitID == other.unitID && type == other.type && targetX == other.targetX && targetY == other.targetY && build == other.build);
	}
	
	public UnitAction copy() {
		return new UnitAction(unitID, type, targetX, targetY, build);
	}
	
	public String toString() {
		return type+" => "+targetX+"x"+targetY+"  ("+build+")";
	}
}
