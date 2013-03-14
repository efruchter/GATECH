/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import myai.BestAgentEver;
import rts.Game;
import ai.general.GeneralAI;

/**
 * \package tests
 * \brief Provides methods for testing AIs against each other
 */

/**
 * \brief Runs a visualization of the game. This is probably the best place for
 * debugging.
 * 
 * @author santi
 */
public class GameVisualSimulationTest {
	public static void main(String args[]) throws Exception {
		int MAXCYCLES = 50000;
		int PERIOD = 10;
		
		System.out.println("Transcending history, and the world, a tale of souls and swords, eternally retold.");

		Game game = new Game("maps/32x32-resources.xml", "game/gamedef.xml", PERIOD, MAXCYCLES);

		//game.addAgent(new GeneralAI());
		//game.addAgent(new GeneralAI());

		game.addAgent(new BestAgentEver());
		game.addAgent(new BestAgentEver());
		//game.addAgent(new GeneralAI(GeneralAI.LESION_WEAKEST_ARMY));
		
		//game.addAgent(new MyAgent());

//		game.addAgent(new AI() {
//			public void getAction(GameState gs, int time_limit) {
//			}
//		});
		// game.addAgent(new MyAgent());
//		game.addAgent(new
//		GeneralAI());
//		game.addAgent(new
//				GeneralAI());
		// game.addAgent(new GeneralAI(GeneralAI.LESION_NONE));

		game.playVisual(600, true, true, Game.FOLLOW_ALL_TEAMS);
	}
}
