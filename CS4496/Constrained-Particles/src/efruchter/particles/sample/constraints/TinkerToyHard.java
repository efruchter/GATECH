package efruchter.particles.sample.constraints;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.Dimension;

import efruchter.particles.constraints.Constraint;
import efruchter.particles.datatypes.Particle;
import efruchter.particles.integrators.CommonForceFunctions;
import efruchter.particles.integrators.NewtonianIntegrators;
import efruchter.particles.integrators.NewtonianIntegrators.AccelerationFunction;
import efruchter.particles.integrators.NewtonianIntegrators.RKState;
import efruchter.vectorutils.Vector3;

/**
 * A tinker toy with a hard constraint, rather than soft springs.
 * 
 * @author Eric
 * 
 */
public class TinkerToyHard {

    /** Title */
    static final String GAME_TITLE = "Tinker Toy 3D by Eric Fruchter";

    /** Desired frame time */
    static final int FRAMERATE = 60;

    /** Exit the game */
    static boolean finished;

    static Dimension screen = new Dimension(600, 600);

    static float aspect = (float) screen.getWidth() / screen.getHeight();

    /**
     * Standard gravity
     */

    static float gScalar = 1f;

    /**
     * Stuff worth changing
     */

    static Vector3 gravity = new Vector3(0, -9.806f * gScalar, 0);

    static float stepSize = .1f;

    static Particle anchor;

    static List<Particle> system = new ArrayList<Particle>();

    static List<Constraint> constraints = new ArrayList<Constraint>();

    /**
     * Application init
     * 
     * @param args
     *            Commandline args
     */
    public static void main(String[] args) {
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

        Display.setTitle(GAME_TITLE);

        Display.setVSyncEnabled(true);

        Display.setDisplayMode(new DisplayMode(screen.getWidth(), screen.getHeight()));

        Display.create();

        /*
         * GL Setup.
         */
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        /*
         * Add some particles!
         */
        final Particle anchor = new Particle(new Vector3(0, 0, 0));
        system.add(anchor);

        /*
         * Build the point that will swing
         */
        final Particle p = new Particle(new Vector3(200, 200, 0));
        constraints.add(new Constraint(anchor, p));
        final float correctorRestlength = p.x.distance(anchor.x);
        p.accelerationFunction = new AccelerationFunction() {
            public Vector3 getAcceleration(final RKState state, float time) {
                Vector3 force = gravity;
                force = force.add(CommonForceFunctions.radialVirtual(state, anchor, force, p.m));
                force = force.add(CommonForceFunctions.anchoredSpring(state, anchor, p.m, 1f, correctorRestlength));
                return force;
            }
        };
        system.add(p);
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

        // Clear
        for (Particle p : system) {
            p.clearForces();
        }

        // Apply current accel function
        for (Particle p : system) {
            p.accumulateAccelFunction();
        }

        // Accumulate / Integrate
        for (Particle p : system) {
            NewtonianIntegrators.rungeKutta4(p, stepSize);
        }

        /*
         * if (p.x.z > 100) { System.out.println("Energy gain : " + (p.x.z -
         * 100)); } System.out.println("Constraint diffe : " +
         * (p.x.distance(anchor.x)));
         */
    }

    /**
     * Render the current frame
     */
    private static void render() {
        glMatrixMode(GL_PROJECTION);

        glLoadIdentity();

        glOrtho(0, screen.getWidth(), 0, screen.getHeight(), 0, 100);

        glMatrixMode(GL_MODELVIEW);

        glClearColor(.5f, 0.5f, 0.5f, .5f);

        // clear the screen
        glClear(GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glPushMatrix();
        {
            glTranslatef(screen.getWidth() / 2, screen.getHeight() / 2, 0);

            /*
             * Draw the particle in the system
             */
            glColor3f(1.0f, 0.0f, 0.0f);
            glPointSize(5.0f);

            glBegin(GL_POINTS);
            {
                for (Particle p : system)
                    glVertex3f(p.x.x, p.x.y, p.x.z);
            }
            glEnd();

            /*
             * Draw the lines
             */
            glColor3f(0.0f, 0.0f, 1.0f);
            glLineWidth(2.0f);

            glBegin(GL_LINES);
            {
                for (Constraint p : constraints) {
                    glVertex3f(p.getA().x.x, p.getA().x.y, p.getA().x.z);
                    glVertex3f(p.getB().x.x, p.getB().x.y, p.getB().x.z);
                }

            }
            glEnd();

        }
        glPopMatrix();
    }
}
