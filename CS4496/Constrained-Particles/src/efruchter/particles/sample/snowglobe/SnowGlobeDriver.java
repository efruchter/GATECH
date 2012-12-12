package efruchter.particles.sample.snowglobe;

import static java.lang.Math.random;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.UIManager;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.Dimension;

import efruchter.particles.constraints.Constraint;
import efruchter.particles.constraints.Constraint.ConstraintListener;
import efruchter.particles.constraints.soft.MaxDistanceSpringConstraint;
import efruchter.particles.constraints.soft.MinDistanceSpringConstraint;
import efruchter.particles.datatypes.Particle;
import efruchter.particles.integrators.NewtonianIntegrators;
import efruchter.vectorutils.Vector3;

/**
 * A snow globe simulation, minus the water and air particles.
 * 
 * @author efruchter
 */
public class SnowGlobeDriver {

	/** Title */
	static final String GAME_TITLE = "Snow Globe";

	/** Desired frame time */
	static final int FRAMERATE = 60;

	/** Exit the game */
	static boolean finished;

	/** Angle of rotating square */
	static float angle;

	static Dimension screen = new Dimension(800, 600);

	static float aspect = (float) screen.getWidth() / screen.getHeight();

	static List<Particle> system = new LinkedList<Particle>();

	// static float systemDimLength = 50;

	static Vector3 gravity = new Vector3(0, 0, -9.806f * .05f);

	static float stepSize = .035f, moveS = 100;

	static int radius = 25 / 2;

	static float shakeAmount = 5;

	static UniverseCube universe = new UniverseCube(radius * 2);

	static List<Constraint> constraints = new LinkedList<Constraint>();

	static int particleCount = 200;

	/**
	 * The one invisible particle to serve as the radial constraint.
	 */
	static Particle anchor = new Particle();

	static Vector3 realUniversePos = Vector3.ZERO;

	/**
	 * Application init
	 * 
	 * @param args
	 *            Commandline args
	 */
	public static void main(String[] args) {
		if (args.length > 0) {
			try {
				int candidate = Integer.parseInt(args[0]);
				if (candidate < 0) {
					throw new NumberFormatException();
				}
				particleCount = candidate;
			} catch (NumberFormatException ec) {
				System.out.println("Please use a real, positive integer. ");
			}
		}

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final Exception e) {
			System.err
					.println("Failed to grab a proper look-and-feel from the OS.");
		}

		System.out.println("Spawning globe with " + particleCount
				+ " particles.");

		try {
			init(false);
			run();
		} catch (Exception e) {
			e.printStackTrace(System.err);
			Sys.alert(GAME_TITLE, "An error occured and the game will exit.");
		} finally {
			cleanup();
		}
		System.exit(0);
	}

	/**
	 * Initialize the game
	 * 
	 * @throws Exception
	 *             if init fails
	 */
	private static void init(boolean fullscreen) throws Exception {

		/*
		 * Add some particles!
		 */

		String instructions = "<LMouse> drag to move. <MouseWheel> to raise/lower. <RMouse> to auto-shake.";

		anchor.x = universe.pos;

		ProgressMonitor pM = new ProgressMonitor(null,
				"Storing particle constraints for inter- particle collisions.",
				"", 0, particleCount);

		/*
		 * This constraint action will simulate fake drag. Not physically
		 * accurate, but it looks good.
		 */
		ConstraintListener fakeDrag = new ConstraintListener() {
			float dragFloat = 500;

			@Override
			public void onSatisfy(Particle a, Particle b) {
				b.xOld = b.x.add(b.xOld.scale(dragFloat)).scale(
						1f / (dragFloat + 1));
			}
		};

		for (int i = 0; i < particleCount; i++) {

			/*
			 * Update the GUI, check for cancel.
			 */
			if (pM.isCanceled()) {
				System.exit(0);
			}
			pM.setProgress(i);

			/*
			 * Give it a random starting location.
			 */
			float x = universe.xL + (float) random() * universe.sideLength;
			float y = universe.yL + (float) random() * universe.sideLength;
			float z = universe.zL + (float) random() * universe.sideLength;

			Particle nP = new Particle(new Vector3(x, y, z));

			/*
			 * Build the in-globe constraint.
			 */
			Constraint inBound = new MaxDistanceSpringConstraint(anchor, nP, radius);
			inBound.setConstraintListener(fakeDrag);
			constraints.add(inBound);

			for (Particle p : system) {
				constraints.add(new MinDistanceSpringConstraint(p, nP, .5f));
			}
			system.add(nP);
		}
		pM.close();

		JOptionPane.showMessageDialog(null, instructions);

		/*
		 * LWJGL and OpenGL setup.
		 */
		Display.setTitle(GAME_TITLE);

		Display.setVSyncEnabled(true);

		Display.setDisplayMode(new DisplayMode(screen.getWidth(), screen
				.getHeight()));

		Display.create();

		/*
		 * GL Setup.
		 */
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glShadeModel(GL_SMOOTH);
		glDepthFunc(GL_LEQUAL);
	}

	/**
	 * Runs the game (the "main loop")
	 */
	private static void run() {
		while (!finished) {
			Display.update();
			if (Display.isCloseRequested()) {
				finished = true;
			} else {
				logic();
				render();
				Display.sync(FRAMERATE);
			}
		}
	}

	/**
	 * Do any game-specific cleanup
	 */
	private static void cleanup() {
		Display.destroy();
	}

	/**
	 * Do all calculations, handle input, etc.
	 */
	private static void logic() {

		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			finished = true;
		}

		int deltaX = Mouse.getDX(), deltaY = Mouse.getDY(), deltaZ = Mouse
				.getDWheel();

		/*
		 * Handle the movement of the world via input.
		 */
		if (Mouse.isButtonDown(0) && Mouse.isInsideWindow()) {
			realUniversePos = new Vector3(realUniversePos.x - (float) deltaX
					/ 10, realUniversePos.y - (float) deltaY / 10,
					realUniversePos.z);
		}
		if (deltaZ != 0)
			realUniversePos = new Vector3(realUniversePos.x, realUniversePos.y,
					realUniversePos.z - (float) deltaZ / 100);
		universe.pos = realUniversePos;
		if (Mouse.isButtonDown(1)) {
			float xC = (float) (-.5f + Math.random()) * shakeAmount;
			float yC = (float) (-.5f + Math.random()) * shakeAmount;
			float zC = (float) (-.5f + Math.random()) * shakeAmount;
			universe.pos = new Vector3(realUniversePos.x + xC,
					realUniversePos.y + yC, realUniversePos.z + zC);
		}
		universe.recalcBoundaries();

		// Clear force accumulators
		for (Particle p : system)
			p.clearForces();

		// Accumulate forces
		for (Particle p : system)
			p.addForce(gravity);

		// Integrate
		for (Particle p : system)
			NewtonianIntegrators.verlet(p, stepSize);

		/*
		 * Satisfy constraints.
		 */
		Vector3 stablePos = universe.pos;
		for (int i = 0; i < 4; i++) {
			for (Constraint constraint : constraints) {
				universe.pos = anchor.x = stablePos;
				constraint.satisfy();
			}
		}
	}

	/**
	 * Render the current frame
	 */
	private static void render() {
		glMatrixMode(GL_PROJECTION);

		glLoadIdentity();

		gluPerspective(30f, aspect, 1f, 1000f);

		gluLookAt(50, 50, 10, 0, 0, 0, 0, 0, 1);

		// clear the screen
		glClear(GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT
				| GL_DEPTH_BUFFER_BIT);

		glPushMatrix();
		{
			glColor3f(0.5f, 0.5f, 0.5f);

			glBegin(GL_TRIANGLE_STRIP);
			{
				// Top
				glVertex3f(universe.xL, universe.yH, universe.zL);
				glVertex3f(universe.xH, universe.yH, universe.zL);
				glVertex3f(universe.xH, universe.yL, universe.zL);
				glVertex3f(universe.xL, universe.yL, universe.zL);
				glVertex3f(universe.xL, universe.yH, universe.zL);
			}
			glEnd();

			universe.glSphereDraw();

			/*
			 * Draw the particle in the system
			 */
			glColor3f(1.0f, 1.0f, 1.0f);
			glPointSize(2.4f);
			glBegin(GL_POINTS);
			{
				for (Particle p : system) {
					glVertex3f(p.x.x, p.x.y, p.x.z);
				}
			}
			glEnd();

			/*
			 * Shadows, just for effect!
			 */
			glColor4f(0, 0, 0, .5f);
			glBegin(GL_POINTS);
			{
				for (Particle p : system) {
					glVertex3f(p.x.x, p.x.y, universe.zL);
				}
			}
			glEnd();
		}
		glPopMatrix();
	}
}
