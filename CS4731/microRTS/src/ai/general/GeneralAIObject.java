package ai.general;

/**
 * \brief Objects that ai units can interact with
 * @author Jeff Bernard
 *
 */
public abstract class GeneralAIObject {
	/**
	 * Updates the orders of a unit
	 * * @param unit the unit
	 * @param ai the ai
	 */
	public abstract void update_orders(GeneralAIUnit unit, GeneralAI ai);
	
	/**
	 * Orders a unit to interact with this object
	 * @param unit the unit
	 * @param ai the ai
	 */
	public abstract void order_unit(GeneralAIUnit unit, GeneralAI ai);
	
	/**
	 * Calculates the distance from the unit to this object
	 * @param unit the unit
	 * @param ai the ai
	 * @return the distance
	 */
	public abstract int distance(GeneralAIUnit unit, GeneralAI ai);
	
	/**
	 * Some kind of action succeeded
	 * @param unit the unit
	 * @param ai the ai
	 * @param type the type of the action
	 */
	public abstract void action_succeeded(GeneralAIUnit unit, GeneralAI ai, int type);
	
	/**
	 * This unit has been removed from the object
	 * @param unit
	 */
	public abstract void remove(GeneralAIUnit unit, GeneralAI ai);
}
