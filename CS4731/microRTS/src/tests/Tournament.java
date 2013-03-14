package tests;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import ai.AI;

import rts.Game;

/**
 * \brief Runs a Round-Robin Tournament against bots
 */
public class Tournament {
	private static FileWriter out = null;
	
	/**
	 * Main entry point
	 * (note: currently only works for 1v1 games)
	 * @param args
	 * @throws IOException 
	 * @throws JDOMException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static void main(String args[]) throws JDOMException, IOException, InstantiationException, IllegalAccessException {
		out = null;
		if (args.length == 0) {
			System.out.println("Missing required arguments.\n\n");
			System.out.println("\tmicrorts TOURNAMENT_XML [RESULT_OUT]\n\n");
			System.out.println("TOURNAMENT_XML - xml definition of the tournament\n");
			System.out.println("RESULT_OUT - (optional) file to store the results\n");
			System.exit(-1);
		} else if (args.length >= 2) {
			out = new FileWriter(args[1]);
		}
		
		Element tournament = new SAXBuilder().build(args[0]).getRootElement();
		
		Game game = new Game(tournament.getChild("map").getAttributeValue("xml"), tournament.getChild("game").getAttributeValue("xml"), Integer.parseInt(tournament.getAttributeValue("turn_length")), Integer.parseInt(tournament.getAttributeValue("max_game_length")));
		
		if (game.pgs.numberOfPlayers() != 2) {
			System.out.println("Tournament currently only works for 2 player maps\n");
			System.exit(-1);
		}
		
		int games = Integer.parseInt(tournament.getAttributeValue("games"));
		boolean fog = tournament.getAttributeValue("fog").equalsIgnoreCase("on");
		ArrayList<AI> agents = new ArrayList<AI>();
		ArrayList<Integer> lesions = new ArrayList<Integer>();
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<Integer> wins = new ArrayList<Integer>();
		ArrayList<Integer> loses = new ArrayList<Integer>();
		ArrayList<Integer> draws = new ArrayList<Integer>();
		
		for (int i = 0; i < tournament.getChild("players").getChildren().size(); i++) {
			Element player = (Element)tournament.getChild("players").getChildren().get(i);
			try {
				agents.add((AI)Class.forName(player.getAttributeValue("agent")).newInstance());
				try {
					int lesion = Integer.parseInt(player.getAttributeValue("lesion"));
					lesions.add(lesion);
				} catch (NumberFormatException e) {
					lesions.add(0);
				}
				agents.get(agents.size()-1).setLesion(lesions.get(lesions.size()-1));
				names.add(agents.get(agents.size()-1).getLabel());
				wins.add(0);
				loses.add(0);
				draws.add(0);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				System.out.println("\n\nError: "+player.getAttributeValue("agent")+" agent does not exist\n\n");
				System.exit(-1);
			}
		}
		
		for (int i = 0; i < agents.size(); i++) {			
			for (int j = 0; j < agents.size(); j++) {
				if (i != j) {
					AI agentI = agents.get(i).getClass().newInstance();
					agentI.setLesion(lesions.get(i));
					game.addAgent(agentI);
					AI agentJ = agents.get(j).getClass().newInstance();
					agentJ.setLesion(lesions.get(j));
					game.addAgent(agentJ);
					int players[] = {i, j};
					for (int k = 0; k < games; k++) {
						write("-------------------------------------------");
						write("Starting match:");
						write(names.get(players[0])+" vs "+names.get(players[1])+" (Game "+(k+1)+" of "+games+")");
						ArrayList<Integer> winner = game.play(fog);
						if (winner.size() == 0) {
							System.out.println("Game was a draw!");
							draws.set(i, draws.get(i)+1);
							draws.set(j, draws.get(j)+1);
						} else {
							System.out.println(names.get(players[winner.get(0)])+" has won!");
							wins.set(players[winner.get(0)], wins.get(players[winner.get(0)])+1);
							loses.set(players[winner.get(0)==1?0:1], loses.get(players[winner.get(0)==1?0:1])+1);
						}
						write("Scores:");
						write("\t"+names.get(players[0])+": "+game.pgs.scores.get(0));
						write("\t"+names.get(players[1])+": "+game.pgs.scores.get(1));
						
						game.resetGame();
					}
					game.resetPlayers();
				}
			}
		}
		
		write("\n\nResults (Wins/Losses/Draws)");
		for (int i = 0; i < names.size(); i++) {
			write(names.get(i)+": "+wins.get(i)+"/"+loses.get(i)+"/"+draws.get(i));
		}
		
		out.close();
	}
	
	/**
	 * Prints a line of text to the console and the out file, if the outfile exists
	 * @param text
	 */
	private static void write(String text) {
		if (out != null) {
			try {
				out.write(text+"\n\r");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println(text);
	}
}
