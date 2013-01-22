package game.entries.ghosts;

import game.controllers.GhostController;
import game.core.Game;
import game.core.GameView;
import game.entries.ghosts.GD.Direction;

import java.awt.Color;
import java.awt.Point;
import java.util.Collections;
import java.util.List;

/**
 * Ghost Controller for AI
 * @author toriscope
 *
 */
public class MyGhosts implements GhostController {
	private boolean Debugging = false;

	final Point[] targets = new Point[Game.NUM_GHOSTS];

	private GhostState allState = GhostState.CHASE;

	public final static Point BLINKY_SCATTER_POINT = new Point(104, 4);
	public final static Point PINKY_SCATTER_POINT = new Point(4, 4);
	public final static Point INKY_SCATTER_POINT = new Point(104, 116);
	public final static Point CLYDE_SCATTER_POINT = new Point(4, 116);
	final private GD grid = new GD();
	private Direction pacLook = Direction.NONE;

	public MyGhosts(boolean debugging) {
		Debugging = debugging;
	}

	public int[] getActions(Game game, long timeDue) {

		grid.rebuildMap(game);
		
		updateStates(game);

		final Direction possibleLook = Direction.getDIR(game.getCurPacManDir());
		if (possibleLook != Direction.NONE) {
			pacLook = possibleLook;
		}

		int[] directions = new int[Game.NUM_GHOSTS];

		for (int i = 0; i < directions.length; i++) {
			if (true) { // more logic used to be here, leave for now.

				GhostState state = allState;
				if (game.getEdibleTime(i) > 0) {
					state = GhostState.SCARED;
				}

				// BLINKY
				if (i == 0) {
					switch (state) {
					case CHASE: {
						directions[i] = ghostMove_target(i, game,
								grid.toPoint(game.getCurPacManLoc()));
						break;
					}
					case SCARED: {
						directions[i] = (int) (Math.random() * 4);
						targets[i] = null;
						break;
					}
					case SCATTER: {
						directions[i] = ghostMove_target(i, game,
								BLINKY_SCATTER_POINT);
						break;
					}
					}
				}
				// PINKY
				else if (i == 1) {
					switch (state) {
					case CHASE: {
						final Point pDir = GD.scale(new Point(pacLook.offset),
								4);
						final Point pac4 = GD.add(
								grid.toPoint(game.getCurPacManLoc()), pDir);
						directions[i] = ghostMove_target(i, game, pac4);
						break;
					}
					case SCARED: {
						directions[i] = (int) (Math.random() * 4);
						targets[i] = null;
						break;
					}
					case SCATTER: {
						directions[i] = ghostMove_target(i, game,
								PINKY_SCATTER_POINT);
						break;
					}
					}
				}
				// CLYDE
				else if (i == 2) {
					switch (state) {
					case SCARED: {
						directions[i] = (int) (Math.random() * 4);
						targets[i] = null;
						break;
					}
					case CHASE: {
						final Point pos = grid.toPoint(game.getCurGhostLoc(i));
						final Point pacPos = grid.toPoint(game
								.getCurPacManLoc());
						final float tilesAppart = GD.distance(pos, pacPos)
								/ GD.CELL_PIXEL_SEPERATION;
						if (tilesAppart > 8) {
							directions[i] = ghostMove_target(i, game,
									grid.toPoint(game.getCurPacManLoc()));
							break;
						}
					}
					case SCATTER: {
						directions[i] = ghostMove_target(i, game,
								CLYDE_SCATTER_POINT);
						break;
					}
					}
				}
				// INKY
				else if (i == 3) {
					switch (state) {
					case SCARED: {
						directions[i] = (int) (Math.random() * 4);
						targets[i] = null;
						break;
					}
					case CHASE: {
						final Point pac2 = GD.add(
								grid.toPoint(game.getCurPacManLoc()),
								GD.scale(new Point(pacLook.offset), 2));
						final Point blinkyVect = GD.scale(
								GD.sub(pac2,
										grid.toPoint(game.getCurGhostLoc(0))),
								2);
						final Point target = GD.add(
								grid.toPoint(game.getCurGhostLoc(0)),
								blinkyVect);

						directions[i] = ghostMove_target(i, game, target);
						break;
					}
					case SCATTER: {
						directions[i] = ghostMove_target(i, game,
								INKY_SCATTER_POINT);
						break;
					}
					}
				}
			}
			if (Debugging) {
				Color color = Color.GRAY;
				if (i == 0) {
					color = Color.RED;
				} else if (i == 1) {
					color = Color.PINK;
				} else if (i == 2) {
					color = Color.ORANGE;
				} else {
					color = Color.BLUE;
				}
				if (targets[i] != null) {
					GameView.addLines(game, color,
							grid.toPoint(game.getCurGhostLoc(i)), targets[i]);
				}
			}
		}

		return directions;
	}

	private int ghostMove_target(int i, Game game, Point target) {
		targets[i] = target;
		Point ghostLoc = grid.toPoint(game.getCurGhostLoc(i));
		final List<Direction> p = grid.getAllowedCellsSurrounding(ghostLoc);
		for (Direction d : p) {
			GameView.addLines(game, Color.GREEN,
					grid.toPoint(game.getCurGhostLoc(i)),
					GD.add(d.offset, grid.toPoint(game.getCurGhostLoc(i))));
		}
		p.remove(Direction.getOpDIR(game.getCurGhostDir(i)));
		Collections.sort(p, GD.buildTargetComparator(ghostLoc, target));
		if (p.isEmpty()) {
			return -1;
		} else {
			return p.get(0).num;
		}
	}

	public enum GhostState {
		CHASE, SCATTER, SCARED;
	}

	private long milliElapsed = 0;
	private long lastTime = 0;
	private int lives = -1, level = -1;

	private void updateStates(Game game) {
		long c = System.currentTimeMillis();
		
		if (game.getLivesRemaining() != lives || level != game.getCurLevel()) {
			lives = game.getLivesRemaining();
			level = game.getCurLevel();
			lastTime = System.currentTimeMillis();
			milliElapsed = 0;
		}
		
		boolean anyGhostScared = false;
		for (int i = 0; i < game.NUM_GHOSTS && !anyGhostScared; i++) {
			if (game.getEdibleTime(i) > 0) {
				anyGhostScared = true;
			}
		}
		if(!anyGhostScared) {
			milliElapsed += c - lastTime;
		}
		
		lastTime = c;

		float time = (float) milliElapsed / 1000f;
		
		float[] table = null;
		
		if (level >= 4) {
			table = l5;
		} else if (level >= 1) {
			table = l2;
		} else if (level >= 0) {
			table = l1;
		}
		
		float sum = 0;
		int i = 0;
		for (; i < table.length; i++) {
			sum += table[i];
			if (sum > time ) {
				break;
			}	
		}
		
		final GhostState newState = i % 2 == 0 ? GhostState.SCATTER : GhostState.CHASE;
		if (allState != newState) {
			GHOST_REVERSAL = 1f;
			allState = newState;
		} else {
			GHOST_REVERSAL = 0f;
		}
		
		//System.out.println(time + ": " + allState);
	}
	
	/**
	 * Wave Tables
	 */
	final private float[] l1 = new float[]{	7, 20,
											7, 20,
											5, 20,
											5, 0};
	final private float[] l2 = new float[]{	7, 20,
											7, 20,
											5, 1033,
											1f/60f, 0};
	final private float[] l5 = new float[]{ 5, 20,
											5, 20,
											5, 1037,
											1f/60f, 0};
	//Force reversals
	public static float GHOST_REVERSAL = 0;
}
