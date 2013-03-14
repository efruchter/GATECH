package ai.general;

import java.util.ArrayList;

/**
 * \brief Task managers manage the tasks that units can be assigned to
 * @author Jeff Bernard
 *
 */
public abstract class TaskManager {
	public ArrayList<GeneralAIUnit> units; /**< the units this manager has control over */
	public ArrayList<GeneralAIUnit> units_scouted; /**< the units that want, but currently belong to another manager */
	
	/**
	 * Constructs a new task manager
	 */
	public TaskManager() {
		units = new ArrayList<GeneralAIUnit>();
		units_scouted = new ArrayList<GeneralAIUnit>();
	}
	
	/**
	 * Updates the data of the task manager
	 * @param ai
	 */
	public abstract void update(GeneralAI ai);
	
	/**
	 * Requests units to be assigned to this task
	 * @param ai
	 */
	public abstract void manage_units(GeneralAI ai);
	
	/**
	 * Removes a unit because it has been killed
	 * @param id
	 */
	public void remove_unit(long id) {
		for (int i = 0; i < units.size(); i++) {
			if (units.get(i).stats.getID() == id) {
				units.remove(i);
				return;
			}
		}
		for (int i = 0; i < units_scouted.size(); i++) {
			if (units_scouted.get(i).stats.getID() == id) {
				units_scouted.remove(i);
				return;
			}
		}
	}
}
