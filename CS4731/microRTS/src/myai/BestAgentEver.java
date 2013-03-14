package myai;

import rts.GameState;
import ai.AI;

public class BestAgentEver extends AI {

	boolean firstRun = true;
	MapModule mapModule = new MapModule();
	UnitGeneral dudeModule = new UnitGeneral();
	int lastUnit = 0;
	
	@Override
	public void getAction(GameState state, int delta) {
		long startMilli = System.currentTimeMillis();

		if (firstRun) {
			firstRun = false;
			mapModule.init(state);
			dudeModule.init(state, mapModule);
		}
		
		mapModule.update(state, dudeModule);
		
		if (!state.getMyUnits().isEmpty())
			dudeModule.update(state, mapModule, delta - (System.currentTimeMillis() - startMilli));
	}
}
