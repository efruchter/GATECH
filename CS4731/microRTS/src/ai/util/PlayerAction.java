package ai.util;

import rts.units.Unit;
import rts.units.UnitAction;
/**
 * \package ai.util
 * \brief Utilities for AIs. OLD. Used by Santi's AIs which did not survive the changes made to the game engine.
 */

/**
 * \brief Abstraction of "player actions" for some AIs
 *
 */
public class PlayerAction {
	public int[] resourceUsage;
	private int width;
	
	/**
	 * 
	 * @param w
	 * @param h
	 */
	public PlayerAction(int w, int h) {
		width = w;
		resourceUsage = new int[w*h];
		for (int i = 0; i < resourceUsage.length; i++) {
			resourceUsage[i] = 0;
		}
	}
	
	/**
	 * 
	 * @param a
	 */
	public PlayerAction(PlayerAction a) {
		width = a.width;
		resourceUsage = new int[a.resourceUsage.length];
		for (int i = 0; i < resourceUsage.length; i++) {
			resourceUsage[i] = 0;
		}
	}
	
	/**
	 * 
	 * @param u
	 * @param action
	 */
	public void mergeResourceUsage(Unit u, UnitAction action) {
		resourceUsage[u.getX()+u.getY()*width] = 1;
		if (action.getType() == UnitAction.MOVE) {
			resourceUsage[action.getTargetX()+action.getTargetY()*width] = 1;
		}
	}
	
	/**
	 * 
	 * @param action
	 * @return
	 */
	public boolean consistentResourceUsage(UnitAction action) {
		if (action.getType() == UnitAction.MOVE && resourceUsage[action.getTargetX()+action.getTargetY()*width] != 0) {
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @param u
	 * @param action
	 */
	public void addUnitAction(Unit u, UnitAction action) {
		if (action != null) {
			u.setAction(action);
		}
	}
}
