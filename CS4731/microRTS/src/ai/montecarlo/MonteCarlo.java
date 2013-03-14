/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.montecarlo;

import ai.AI;
import ai.RandomBiasedAI;
//import ai.evaluation.SimpleEvaluationFunction;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import rts.Game;
import rts.GameState;
import ai.evaluation.SimpleEvaluationFunction;
import ai.util.PlayerAction;
import ai.util.PlayerActionGenerator;
//import rts.PlayerActionGenerator;
/**
 * \package ai.montecarlo
 * \brief The Monte Carlo AI
 */

/**
 * \brief Chooses moves based on a Monte Carlo simulation
 * @author santi
 */

//currently broken because state representation is changing

public class MonteCarlo extends AI {
    public static final int DEBUG = 1;
    
    Random r = new Random();
    AI randomAI = new RandomBiasedAI();
    long max_actions_so_far = 0;
        
    int NSIMULATIONS = 100;
    int MAXSIMULATIONTIME = 100;
    
    public MonteCarlo() {
    	
    }
    
    public MonteCarlo(int simulations, int lookahead, AI policy) {
        NSIMULATIONS = simulations;
        MAXSIMULATIONTIME = lookahead;
        randomAI = policy;
    }


    public void reset() {        
    }    
        
    
    public AI clone() {
        return new MonteCarlo(NSIMULATIONS, MAXSIMULATIONTIME, randomAI);
    }
    
    public void getAction(GameState gs, int time_limit) {
    	//MAXSIMULATIONTIME = time_limit;
    	
    	PlayerActionGenerator pag;
		pag = new PlayerActionGenerator(gs);
		if (pag.choices.size() > 0) {
	        List<PlayerAction> l = new LinkedList<PlayerAction>();
	        {
	            PlayerAction pa = null;
	            do{
					pa = pag.getNextAction(-1);
					if (pa!=null) l.add(pa);
	            }while(pa!=null);
	            max_actions_so_far = Math.max(l.size(),max_actions_so_far);
	            //if (DEBUG>=1) System.out.println("MontCarloAI for player " + player + " chooses between " + l.size() + " actions [maximum so far " + max_actions_so_far + "] (cycle " + gs.getTime() + ")");
	        }
	                
	        PlayerAction best = null;
	        float best_score = 0;
	        int SYMS_PER_ACTION = NSIMULATIONS/l.size();
	        for(PlayerAction pa:l) {
	            float score = 0;
	            for(int i = 0;i<SYMS_PER_ACTION;i++) {
	            	Game g2 = new Game(gs, gs.getPlayerID(), time_limit, MAXSIMULATIONTIME);
	            	g2.addAgent(randomAI);
	            	g2.addAgent(randomAI);
	            	g2.play(gs.isFog());
	            	g2.pgs.current_player = gs.getPlayerID();
	            	g2.gs.update();
	                // Discount factor:
	                score += SimpleEvaluationFunction.evaluate(g2.gs)*Math.pow(0.99,g2.cycle/10.0);
	            }
	            if (best==null || score>best_score) {
	                best = pa;
	                best_score = score;
	            }
	            //if (DEBUG>=2) System.out.println("child " + pa + " explored " + SYMS_PER_ACTION + " Avg evaluation: " + (score/((double)NSIMULATIONS)));
	        }
		}
    }
    
//    public void simulate(GameState gs, int time) throws Exception {
//        boolean gameover = false;
//
//        do{
//            if (gs.isComplete()) {
//                gameover = gs.cycle();
//            } else {
//                gs.issue(randomAI.getAction(0, gs));
//                gs.issue(randomAI.getAction(1, gs));
//            }
//        }while(!gameover && gs.getTime()<time);   
//    }
    
    public String toString() {
        return "MonteCarlo(" + NSIMULATIONS + "," + MAXSIMULATIONTIME + ")";
    }
    
}
