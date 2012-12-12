package efruchter.particles.sample.fallsim;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glShadeModel;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Dimension;
import org.lwjgl.util.glu.GLU;

import efruchter.particles.sample.fallsim.gui.SimulationPanel;

/**
 * @author efruchter
 */
public class FallingParticleDriver {

    /** Title */
    static final String GAME_TITLE = "Falling Particle";

    /** Desired frame time */
    static final int FRAMERATE = 60;

    /** Exit the game */
    static boolean finished;

    /** Angle of rotating square */
    static float angle;

    static Dimension screen = new Dimension(800, 600);

    static float aspect = (float) screen.getWidth() / screen.getHeight();

    static float systemDimLength = 50;

    static SimulationPanel simulation;

    /**
     * Application init
     * 
     * @param args
     *            Commandline args
     */
    public static void main(String[] args) {
        try {
            init();
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
    private static void init() throws Exception {

        Display.setTitle(GAME_TITLE);

        Display.setVSyncEnabled(false);

        Display.setDisplayMode(new DisplayMode(screen.getWidth(), screen.getHeight()));

        Display.create();

        /*
         * GL Setup.
         */
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glShadeModel(GL_SMOOTH);
        glDepthFunc(GL_LEQUAL);

        /*
         * Sim Setup
         */
        simulation = new SimulationPanel();
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

        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            angle += 2.0f % 360;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
            angle -= 2.0f % 360;
        }

        /*
         * update the simulation.
         */

        simulation.updateSimTime();
    }

    /**
     * Render the current frame
     */
    private static void render() {
        GL11.glMatrixMode(GL11.GL_PROJECTION);

        GL11.glLoadIdentity();

        GLU.gluPerspective(30f, aspect, 1f, 1000f);

        GLU.gluLookAt(200, 200, systemDimLength * 2, 0, 0, systemDimLength, 0, 0, 1);

        // clear the screen
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        GL11.glPushMatrix();
        {
            // rotate square according to angle
            GL11.glRotatef(angle, 0, 0, 1.0f);

            GL11.glColor3f(0.5f, 0.5f, 0.5f);

            GL11.glBegin(GL11.GL_QUADS);
            {
                GL11.glVertex3f(-systemDimLength, -systemDimLength, -1);
                GL11.glVertex3f(systemDimLength, -systemDimLength, -1);
                GL11.glVertex3f(systemDimLength, systemDimLength, -1);
                GL11.glVertex3f(-systemDimLength, systemDimLength, -1);
            }
            GL11.glEnd();

            GL11.glColor3f(0.0f, 1.0f, 0.0f);

            /*
             * Draw the particle in the system
             */

            GL11.glPointSize(5.0f);

            // Draw the analytical
            GL11.glColor3f(1.0f, 0.0f, 0.0f);
            GL11.glBegin(GL11.GL_POINTS);
            {
                GL11.glVertex3f(simulation.pAnalytical.x.x, simulation.pAnalytical.x.y, simulation.pAnalytical.x.z);
            }
            GL11.glEnd();

            // Draw integrators
            GL11.glColor3f(0.0f, 0.0f, 1.0f);
            GL11.glBegin(GL11.GL_POINTS);
            {
                GL11.glVertex3f(simulation.pExplicit.x.x, simulation.pExplicit.x.y, simulation.pExplicit.x.z);
                GL11.glVertex3f(simulation.pBetter.x.x, simulation.pBetter.x.y, simulation.pBetter.x.z);
            }
            GL11.glEnd();

            /**
             * Shadows!
             */
            GL11.glColor4f(0.0f, 0.0f, 0.0f, .5f);
            GL11.glBegin(GL11.GL_POINTS);
            {
                if (simulation.pAnalytical.x.z >= 0)
                    GL11.glVertex3f(simulation.pAnalytical.x.x, simulation.pAnalytical.x.y, 0);
                if (simulation.pExplicit.x.z >= 0)
                    GL11.glVertex3f(simulation.pExplicit.x.x, simulation.pExplicit.x.y, 0);
                if (simulation.pBetter.x.z >= 0)
                    GL11.glVertex3f(simulation.pBetter.x.x, simulation.pBetter.x.y, 0);
            }
            GL11.glEnd();
        }
        GL11.glPopMatrix();
    }
}
