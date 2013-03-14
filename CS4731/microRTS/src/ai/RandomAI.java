///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
package ai;

import rts.GameState;
import rts.units.Unit;

//
//import java.util.List;
//import java.util.Random;
//import rts.*;
//import rts.units.Unit;
//

/**
 * \brief Makes random moves
 *
 */
public class RandomAI extends AI {
	@Override
	public void getAction(GameState gs, int time_limit) {
		for (Unit unit: gs.getMyUnits()) {
			if (!unit.hasAction() && unit.getActions().size() > 0) {
				unit.setAction(unit.getActions().get((int)(Math.random()*unit.getActions().size())));
			}
		}
	}
	
	@Override
	public String getLabel() {
		return "Random AI";
	}
}
