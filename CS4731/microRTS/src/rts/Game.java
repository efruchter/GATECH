package rts;

import gui.PhysicalGameStatePanel;

import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import rts.units.UnitDefinitionManager;

import ai.AI;

/**
 * \package rts
 * \brief The RTS game engine
 */

/**
 * \brief  Used to set up and run games between agents.
 * @author Jeff Bernard
 *
 */
public class Game {
	public static final int FOLLOW_ALL_TEAMS = -1;
	
	public GameState gs; /**< the current game state */
	public PhysicalGameState pgs; /**< the current physical game state */
	
	private ArrayList<AI> agents; /**< the ai agents playing the game */
	
	private int turn_length; /**< how long (msec) a cycle should last */
    private int max_game_cycles; /**< maximum number of cycles a game should be */
    public int cycle;
    
    static private UnitDefinitionManager unit_definitions; /**< the unit definitions for each player */
    
    private String level_xml;
    private String game_xml;
	
	/**
     * Loads the physical game state from XML
     * @param xml XML map file
     * @param gameXML game definition XML file
     * @param turn_time turn time limit in milliseconds
     * @param cycles max number of game cycles
     */
    public Game(String xml, String gameXML, int turn_time, int cycles) {
    	level_xml = xml;
    	game_xml = gameXML;
    	
    	agents = new ArrayList<AI>();
    	resetGame();
    	
    	turn_length = turn_time;
    	max_game_cycles = cycles;
    }
    
    /**
     * Clones the game state so you can run simulations
     * @param state the state you are cloning
     * @param start_player id of the player who should start first
     * @param turn_time milliseconds per turn
     * @param cycles max number of cycles to run
     */
    public Game(GameState state, int start_player, int turn_time, int cycles) {
    	turn_length = turn_time;
    	max_game_cycles = cycles;
    	agents = new ArrayList<AI>();
    	
    	pgs = new PhysicalGameState(start_player, state, unit_definitions);
    	gs = new GameState(pgs);
    }
    
    /**
     * Adds an AI to control a player
     * @param agent
     */
    public void addAgent(AI agent) {
    	agents.add(agent);
    }
    
    /**
     * Gets an agent
     * @param i index of the agent
     * @return null or the agent
     */
    public AI getAgent(int i) {
    	if (i < 0 || i >= agents.size()) {
    		return null;
    	}
    	return agents.get(i);
    }
    
    /**
     * Resets the game (keeping the same players and game)
     */
    public void resetGame() {
    	try {
			unit_definitions = new UnitDefinitionManager(new SAXBuilder().build(game_xml).getRootElement());
			pgs = new PhysicalGameState(new SAXBuilder().build(level_xml).getRootElement(), unit_definitions);
		} catch (JDOMException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    	
    	gs = new GameState(pgs);
    	ArrayList<AI> ais = new ArrayList<AI>();
    	for (int i = 0; i < agents.size(); i++) {
    		try {
				ais.add(agents.get(i).getClass().newInstance());
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
    	}
    	agents = ais;
    }
    
    /**
     * Resets the players
     */
    public void resetPlayers() {
    	agents.clear();
    }
    
    /**
     * Players the game (no visualization)
     * @param fog whether or not to play the game with fog of war
     * @return the winner(s) (empty on draw)
     */
    public ArrayList<Integer> play(boolean fog) {
    	if (agents.size() != pgs.numberOfPlayers()) {
			System.out.println("Not the right number of players. Expected "+pgs.numberOfPlayers()+" but got "+agents.size()+" players");
			return new ArrayList<Integer>();
		}
    	
    	pgs.fog = fog;
    	
    	boolean gameover = false;
		cycle = 0;
        do {       	
        	for (int i = 0; i < agents.size(); i++) {
        		// update GS here
        		pgs.setCurrentPlayer(i);
        		gs.update();
        		// ...
        		pgs.setTimeLimit(System.currentTimeMillis()+turn_length);
        		agents.get(i).getAction(gs, turn_length);
        	}

            // simulate:
            gameover = pgs.cycle();
            cycle++;
        }while(!gameover && cycle < max_game_cycles);
        
        int winner = pgs.winner();
        if (winner == -1) {
        	winner = pgs.winnerByScore();
        }
        if (winner != -1) {
        	return pgs.teams.get(winner);
        }
        return new ArrayList<Integer>();
    }
    
    /**
     * Plays the game, displaying a visualization
     * @param frame_height
     * @param fog whether or not the play the game with fog of war
     * @param show_fog whether or not to render fog in the visualization
     * @param team the team to follow
     * @return the winners (empty on draw)
     */
	public ArrayList<Integer> playVisual(int frame_height, boolean fog, boolean show_fog, int team) {
		if (agents.size() != pgs.numberOfPlayers()) {
			System.out.println("Not the right number of players. Expected "+pgs.numberOfPlayers()+" but got "+agents.size()+" players");
			return new ArrayList<Integer>();
		}
		pgs.fog = fog;
		
		JFrame window = PhysicalGameStatePanel.newVisualizer(this, frame_height, show_fog, team);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		boolean gameover = false;
		cycle = 0;
		long cycle_end = System.currentTimeMillis()+turn_length;
        do{
            if (System.currentTimeMillis() >= cycle_end) {
            	for (int i = 0; i < agents.size(); i++) {
            		// update GS here
            		pgs.setCurrentPlayer(i);
            		gs.update();
            		// ...
            		pgs.setTimeLimit(System.currentTimeMillis()+turn_length);
            		agents.get(i).getAction(gs, turn_length);
            	}

                // simulate:
                gameover = pgs.cycle();
                window.repaint();
                cycle_end += turn_length;
                cycle++;
            } else {
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }while(!gameover && cycle < max_game_cycles);
        
        int winner = pgs.winner();
        if (winner == -1) {
        	winner = pgs.winnerByScore();
        }
        if (winner != -1) {
        	return pgs.teams.get(winner);
        }
        return new ArrayList<Integer>();
	}
}
