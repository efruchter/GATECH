/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * 
 */
/**
 * 
 */
package ai;

/**
 * \package ai
 * \brief Contains basic AIs, including the abstract AI
 */

import rts.GameState;

/**
 * \brief Abstract class which AI agents must extend
 * @author santi
 */
public abstract class AI {
	private int lesion; /**< paramaterizable lesion */
	// user should define their own lesions */
	
	public AI() {
		lesion = 0;
	}
	
	/**
	 * Requests actions from the AI
	 * @param gs the game state
	 * @param time_limit how many milliseconds this turn is
	 */
    public abstract void getAction(GameState gs, int time_limit);
    
    /**
     * Sets the lesion
     * @param l the new lesion
     */
    public void setLesion(int l) {
    	lesion = l;
    }
    
    /**
     * Returns the lesion
     * @return lesion
     */
    public int getLesion() {
    	return lesion;
    }
    
    /**
     * Allows the agent to label itself
     * @return a label
     */
    public String getLabel() {
    	return "AI";
    }
} 
