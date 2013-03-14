/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;

import rts.Game;
import rts.PhysicalGameState;
import rts.units.Unit;
import rts.units.UnitAction;

/**
 * \package gui
 * \brief A graphical display of the game
 */

/**
 * \brief A graphical display of the game
 * @author santi
 */
public class PhysicalGameStatePanel extends JPanel {
	private static final int HUD_W = 150;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Game game = null;
	private boolean fog;
	private int team;
	private int WIDTH;

	// colors for the players
	private static final Color RESOURCE_COLORS[] = {new Color(212, 175, 55), new Color(192, 192, 192)};

	private static final Color PLAYER_COLORS[] = {Color.cyan, Color.red, Color.green, Color.yellow, Color.blue,
		Color.pink, Color.magenta, Color.orange, Color.darkGray, Color.lightGray
	};

	public PhysicalGameStatePanel(Game _game) {
		game = _game;
		setBackground(Color.darkGray);
	}

	public static JFrame newVisualizer(Game _game, int dy, boolean show_fog, int follow_team) {
		/*PhysicalGameStatePanel w = *///new PhysicalGameStatePanel(_game);

		PhysicalGameStatePanel ad = new PhysicalGameStatePanel(_game);
		JFrame frame = new JFrame("Game State Visuakizer");
		frame.getContentPane().add(ad);
		frame.pack();
		frame.setResizable(false);
		ad.WIDTH = dy;
		ad.fog = show_fog;
		ad.team = follow_team;
		frame.setSize(dy+HUD_W*2,dy);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		return frame;
	}

	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D)g;
		int grid = WIDTH/game.pgs.width-1;
		int sizex = grid*game.pgs.width;
		int sizey = grid*game.pgs.height;

		g2d.translate(getWidth()/2 - sizex/2, getHeight()/2 - sizey/2);

		Color playerColor = null;

		boolean[] visibility = new boolean[game.pgs.width*game.pgs.height];

		// draw grid:
		g.setColor(Color.GRAY);
		for(int i = 0;i<=game.pgs.width;i++) 
			g.drawLine(i*grid, 0, i*grid, game.pgs.height*grid);
		for(int i = 0;i<=game.pgs.height;i++) 
			g.drawLine(0, i*grid, game.pgs.width*grid, i*grid);

		// draw the units:
		synchronized(this) {
			// this list copy is to prevent a concurrent modification exception
			List<Unit> l = new LinkedList<Unit>();
			for (int i = 0; i < game.pgs.armies.size(); i++) {
				l.addAll(game.pgs.armies.get(i));
			}

			for (int i = 0; i < visibility.length; i++) {
				visibility[i] = !fog|!game.pgs.fog;
			}

			for (Unit u:l) {
				if (u.getPlayer() != -1 && (team == -1 || game.pgs.teams.get(team).contains(u.getPlayer()))) {
					for (int i = u.getY()-u.getVision(); i <= u.getY()+u.getVision(); i++) {
						if (i >= 0 && i < game.pgs.height) {
							for (int j = u.getX()-u.getVision(); j <= u.getX()+u.getVision(); j++) {
								if (j >= 0 && j < game.pgs.width) {
									if ((u.getY()-i)*(u.getY()-i)+(u.getX()-j)*(u.getX()-j) <= u.getVision()*u.getVision()) {
										visibility[j+i*game.pgs.width] = true; 
									}
								}
							}
						}
					}
				}
			}

			for(int j = 0;j<game.pgs.width;j++) {
				for(int i = 0;i<game.pgs.height;i++) {
					if (visibility[j+i*game.pgs.width] && game.pgs.terrain[j+i*game.pgs.width]==PhysicalGameState.TERRAIN_WALL) {
						g.setColor(Color.lightGray);
						g.fillRect(j*grid, i*grid, grid, grid);
					} else if (!visibility[j+i*game.pgs.width]) {
						g.setColor(Color.black);
						g.fillRect(j*grid, i*grid, grid, grid);
					}
				}            
			}

			//l.addAll(pgs.getUnits());
			for(Unit u:l) {
				if ((u.getPlayer() == -1 || (u.getPlayer() != -1 && team != -1 && !game.pgs.teams.get(team).contains(u.getPlayer()))) && !visibility[u.getX()+u.getY()*game.pgs.width]) {
					continue;
				}
				if (u.getHP() > 0 || u.isResources()) {

					int reduction = 0;
					playerColor = PLAYER_COLORS[u.getPlayer()+1];

					if (u.isBuilding() || u.isResources()) {

						if (u.isStockpile()) {
							g.setColor(Color.yellow);
							g.fillRect(u.getX()*grid+reduction, u.getY()*grid+reduction, grid-reduction*2, grid-reduction*2);
						} else if (u.isResources()) {
							g.setColor(RESOURCE_COLORS[u.getResourcesType()]);
							g.fillRect(u.getX()*grid+reduction, u.getY()*grid+reduction, grid-reduction*2, grid-reduction*2);
						} else {
							g.setColor(new Color(100,125,255));
							g.fillRect(u.getX()*grid+reduction, u.getY()*grid+reduction, grid-reduction*2, grid-reduction*2);
						}
						g.setColor(playerColor);
						g.drawRect(u.getX()*grid+reduction, u.getY()*grid+reduction, grid-reduction*2, grid-reduction*2);
						//g.drawRect(u.getX()*grid+reduction, u.getY()*grid+reduction, grid-reduction*2, grid-reduction*2);
						if (u.hasAction() && (u.getAction().getType() == UnitAction.ATTACK || u.getAction().getType() == UnitAction.BUILD)) {
							g.setColor(Color.white);
							g.drawOval(u.getAction().getTargetX()*grid+3*grid/8, u.getAction().getTargetY()*grid+3*grid/8, grid/4, grid/4);
							g.drawLine(u.getX()*grid+grid/2, u.getY()*grid+grid/2, u.getAction().getTargetX()*grid+grid/2, u.getAction().getTargetY()*grid+grid/2);
						}

					} else {
						g.setColor(Color.decode("#AAAAAA"));
						/// TODO: set a different color based on some kind of unit attributes??
						if (u.isFlying()) {
							g.fillPolygon(new int[]{u.getX()*grid, u.getX()*grid+grid/2, u.getX()*grid+grid}, new int[]{u.getY()*grid+grid/2, u.getY()*grid, u.getY()*grid+grid/2}, 3);
						}
						if (u.getAttackRange() > 1) {
							g.fillRect(u.getX()*grid, u.getY()*grid+3*grid/4, grid, grid/8);
						}
						if (u.isWorker()) {
							g.fillRoundRect(u.getX()*grid+grid/4, u.getY()*grid, grid/2, grid, 1, 1);
							g.setColor(playerColor);
							g.drawRoundRect(u.getX()*grid+grid/4, u.getY()*grid, grid/2, grid, 1, 1);
						} else {
							g.fillOval(u.getX()*grid+grid/4, u.getY()*grid, grid/2, grid);
							g.setColor(playerColor);
							g.drawOval(u.getX()*grid+grid/4, u.getY()*grid, grid/2, grid);
						}

						if (u.hasAction() && (u.getAction().getType() == UnitAction.ATTACK || u.getAction().getType() == UnitAction.BUILD)) {
							g.setColor(Color.white);
							g.drawOval(u.getAction().getTargetX()*grid+3*grid/8, u.getAction().getTargetY()*grid+3*grid/8, grid/4, grid/4);
							if (u != null && u.getAction() != null) { // huh?
								g.drawLine(u.getX()*grid+grid/2, u.getY()*grid+grid/2, u.getAction().getTargetX()*grid+grid/2, u.getAction().getTargetY()*grid+grid/2);
							}
						}
					}	

					if (u.getResources()!=0) {
						String txt = "" + u.getResources();
						g.setColor(Color.black);

						FontMetrics fm = getFontMetrics( g.getFont() );
						int width = fm.stringWidth(txt);
						g2d.drawString(txt, u.getX()*grid + grid/2 - width/2, u.getY()*grid + grid/2);
					} else {
						g.setColor(Color.black);
						FontMetrics fm = getFontMetrics( g.getFont() );
						int width = fm.stringWidth(""+u.getLabel().charAt(0));
						g2d.drawString(""+u.getLabel().charAt(0), u.getX()*grid + grid/2 - width/2, u.getY()*grid + grid/2);

						//USEFUL DEBUG: draws unit IDs
						//	                    g.setColor(Color.white);
						//	                    fm = getFontMetrics( g.getFont() );
						//	                    width = fm.stringWidth(""+u.getID());
						//	                    g2d.drawString(""+u.getID(), u.getX()*grid + grid/2 - width/2, u.getY()*grid + grid/2);
					}

					if (u.getHP()<u.getMaxHP()) {
						g.setColor(Color.RED);
						g.fillRect(u.getX()*grid+reduction, u.getY()*grid+reduction, grid, 2);
						g.setColor(Color.GREEN);
						g.fillRect(u.getX()*grid+reduction, u.getY()*grid+reduction, (int)(grid*(((float)u.getHP())/u.getMaxHP())), 2);
					}
				}

				g.setColor(Color.WHITE);
				g.drawString(game.cycle + "", 0, 0);

				int offset = 0;
				for (int i = 0; i < game.pgs.numberOfPlayers(); i+=2) { // left side
					offset = draw_stats(g, i, -HUD_W, offset);
				}
				offset = 0;
				for (int i = 1; i < game.pgs.numberOfPlayers(); i+=2) { // right side
					offset = draw_stats(g, i, getWidth()-HUD_W*2, offset);
				}
			}
		}      
	}

	private int draw_stats(Graphics g, int i, int w, int offset) {
		g.setColor(PLAYER_COLORS[(i+1)%PLAYER_COLORS.length]);
		offset += 10;
		g.drawString("Player "+i+": "+game.getAgent(i).getLabel(), w, offset);
		int team = 0;
		for (int j = 0; j < game.pgs.teams.size(); j++) {
			if (game.pgs.teams.get(j).contains(i)) {
				team = j;
				break;
			}
		}
		offset += 20;
		g.setColor(PLAYER_COLORS[(1+team)%PLAYER_COLORS.length]);
		g.drawString("Team: "+team, w, offset);

		g.setColor(Color.white);
		offset += 20;
		g.drawString("Score: "+game.pgs.scores.get(i), w, offset);
		offset += 20;
		g.drawString("Resources: "+game.pgs.resources.get(i), w, offset);
		offset += 20;
		g.drawString("Kills: "+game.pgs.kills.get(i), w, offset);
		offset += 20;
		g.drawString("Deaths: "+game.pgs.deaths.get(i), w, offset);
		offset += 20;
		g.drawString("Units:", w, offset);
		ArrayList<Integer> units = new ArrayList<Integer>();
		ArrayList<Integer> buildings = new ArrayList<Integer>();
		for (int j = 0; j < game.pgs.unitDefinitions.building_defs.get(i).size(); j++) {
			buildings.add(0);
		}
		for (int j = 0; j < game.pgs.unitDefinitions.unit_defs.get(i).size(); j++) {
			units.add(0);
		}
		for (int j = 0; j < game.pgs.armies.get(i).size(); j++) {
			if (game.pgs.armies.get(i).get(j).isBuilding()) {
				buildings.set(game.pgs.armies.get(i).get(j).getType(), buildings.get(game.pgs.armies.get(i).get(j).getType())+1);
			} else {
				units.set(game.pgs.armies.get(i).get(j).getType(), units.get(game.pgs.armies.get(i).get(j).getType())+1);
			}
		}
		for (int j = 0; j < units.size(); j++) {
			offset += 20;
			g.drawString("       "+game.pgs.unitDefinitions.unit_defs.get(i).get(j).label+": "+units.get(j), w, offset);
		}
		offset += 20;
		g.drawString("Buildings:", w, offset);
		for (int j = 0; j < buildings.size(); j++) {
			offset += 20;
			g.drawString("       "+game.pgs.unitDefinitions.building_defs.get(i).get(j).label+": "+buildings.get(j), w, offset);
		}

		return offset+10;
	}
}
