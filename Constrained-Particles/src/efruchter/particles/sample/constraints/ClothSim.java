package efruchter.particles.sample.constraints;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glPointSize;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.util.glu.GLU.gluLookAt;
import static org.lwjgl.util.glu.GLU.gluPerspective;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.Dimension;

import efruchter.particles.constraints.Constraint;
import efruchter.particles.constraints.soft.DistanceConstraint;
import efruchter.particles.datatypes.Particle;
import efruchter.particles.integrators.NewtonianIntegrators;
import efruchter.particles.sample.snowglobe.UniverseCube;
import efruchter.vectorutils.Vector3;

/**
 * A particle constraint toy.
 * 
 * @author toriscope
 */
public class ClothSim {

    /** Title */
    static final String GAME_TITLE = "Tinker Toy 3D by Eric Fruchter";

    /** Desired frame time */
    static final int FRAMERATE = 60;

    /** Exit the game */
    static boolean finished;

    /** Angle of rotating square */
    static float angle;

    static Dimension screen = new Dimension(800, 600);

    static float aspect = (float) screen.getWidth() / screen.getHeight();

    static Vector3 anchorL;
    static Vector3 anchorR;

    /*
     * TESTBED
     */
    static List<Particle> system = new LinkedList<Particle>();
    static List<Constraint> constraints = new LinkedList<Constraint>();

    /**
     * Standard gravity
     */

    static float gScalar = .5f;

    /**
     * Stuff worth changing
     */

    static Vector3 gravity = new Vector3(0, 0, -9.806f * gScalar);

    static Particle[][][] cloth = new Particle[50][50][1];

    static float stepSize = .3f, moveS = 50;

    static int integrationIterations = 20;

    static UniverseCube cube;
    static {
        cube = new UniverseCube(120);
        cube.pos = new Vector3(cube.pos.x, cube.pos.y, cube.sideLength / 2);
        cube.recalcBoundaries();
        DistanceConstraint.DEFAULT_SQUISH = 1.0f;
    }

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
        for (int k = 0; k < 1; k++)
            for (int i = 0; i < cloth.length; i++) {
                for (int j = 0; j < cloth[0].length; j++) {

                    float x = j * 5 + (k == 0 ? 0 : 10);
                    float y = -200 + i * 30;
                    float z = 500 + j * 30;

                    system.add(cloth[i][j][k] = new Particle(new Vector3(x, y, z)));

                    if (i > 0)
                        constraints.add(new DistanceConstraint(cloth[i][j][k], cloth[i - 1][j][k]));
                    if (j > 0)
                        constraints.add(new DistanceConstraint(cloth[i][j][k], cloth[i][j - 1][k]));
                    if (k > 0) {
                        constraints.add(new DistanceConstraint(cloth[i][j][k], cloth[i][j][k - 1]));
                    }
                }
            }

        // for (int i = 0; i < 1000; i++) {
        // constraints.remove((int) (constraints.size() * Math.random()));
        // }

        anchorL = cloth[0][0][0].x;
        anchorR = cloth[cloth[0].length - 1][0][0].x;
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

        int dx = Mouse.getDX(), dy = Mouse.getDY(), dz = Mouse.getDWheel();

        if (Mouse.isButtonDown(0) && Mouse.isInsideWindow()) {
            Vector3 d = new Vector3(dx, dy, 0);
            anchorL = anchorL.sub(d);
            anchorR = anchorR.sub(d);
        }

        if (Mouse.isButtonDown(1) && Mouse.isInsideWindow()) {
            Vector3 d = new Vector3(0, dy, 0);
            anchorL = anchorL.sub(d);
            anchorR = anchorR.add(d);
        }

        Vector3 zChange = new Vector3(0, 0, dz / 4);
        anchorL = anchorL.add(zChange);
        anchorR = anchorR.add(zChange);

        /*
         * Move those particles!
         */

        // Clear force accumulator
        for (Particle p : system)
            p.clearForces();

        // Accumulate forces
        for (Particle p : system)
            p.addForce(gravity);

        // Integrate
        for (Particle p : system)
            NewtonianIntegrators.verlet(p, stepSize);

        /*
         * Formal and informal constraint check.
         */

        for (int i = 0; i < integrationIterations; i++) {
            cloth[0][0][0].x = anchorL;
            cloth[cloth[0].length / 2][0][0].x = anchorL.add(anchorR).scale(.5f);
            cloth[cloth[0].length - 1][0][0].x = anchorR;
            // bounds check
            float dragFloat = 10;
            Vector3 zFloor = new Vector3(0, 0, 1);
            for (Particle p : system) {
                if (p.x.z <= 0 + 1) {
                    p.x = p.x.add(zFloor);
                    p.xOld = p.x.add(p.xOld.scale(dragFloat)).scale(1f / (dragFloat + 1));
                }
            }
            // for (Particle p : system) {
            // if (cube.isWithin(p.x)) {
            // cube.escape(p);
            // }
            // }
            for (Constraint c : constraints)
                c.satisfy();
        }

    }

    /**
     * Render the current frame
     */
    private static void render() {
        glMatrixMode(GL_PROJECTION);

        glLoadIdentity();

        gluPerspective(30f, aspect, 1f, 10000000f);

        float look = anchorL.z * 5;

        gluLookAt(look, look, anchorL.z, 0, 0, 0, 0, 0, 1);

        glClearColor(.5f, 0.5f, 0.5f, .5f);

        // clear the screen
        glClear(GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glPushMatrix();
        {
            // int systemDimLength = cube.sideLength / 2;
            // rotate square according to angle
            glRotatef(angle, 0, 0, 1.0f);

            // glColor3f(1f, 1f, 1f);
            // cube.glSurfaceDraw();

            /*
             * Draw the particle in the system
             */
            glColor3f(0.0f, 1.0f, 0.0f);
            glPointSize(3.0f);
            /*
             * glBegin(GL_POINTS); { for (Particle p : system) {
             * glVertex3f(p.x.x, p.x.y, p.x.z); } } glEnd();
             */

            /*
             * Draw the lines
             */
            glLineWidth(2.0f);
            for (Constraint p : constraints) {
                glBegin(GL_LINES);
                {
                    glVertex3f(p.getA().x.x, p.getA().x.y, p.getA().x.z);
                    glVertex3f(p.getB().x.x, p.getB().x.y, p.getB().x.z);
                }
                glEnd();
            }

            /*
             * Shadows, just for effect!
             */
            glColor4f(0, 0, 0, .5f);
            /*
             * glBegin(GL_POINTS); { for (Particle p : system) {
             * glVertex3f(p.x.x, p.x.y, -systemDimLength); } } glEnd();
             */

            /*
             * Draw the line shadows
             */
            for (Constraint p : constraints) {
                glBegin(GL_LINES);
                {
                    glVertex3f(p.getA().x.x, p.getA().x.y, -0);
                    glVertex3f(p.getB().x.x, p.getB().x.y, -0);
                }
                glEnd();
            }

            glBegin(GL_LINES);
            {
                glVertex3f(anchorL.x, anchorL.y, -0);
                glVertex3f(anchorR.x, anchorR.y, -0);
            }
            glEnd();

            glColor3f(1, 0, 0);

            /*
             * Draw the tension line
             */
            glBegin(GL_LINES);
            {
                glVertex3f(anchorL.x, anchorL.y, -0);
                glVertex3f(anchorL.x, anchorL.y, anchorL.z);
                glVertex3f(anchorL.x, anchorL.y, anchorL.z);
                glVertex3f(anchorR.x, anchorR.y, anchorR.z);
                glVertex3f(anchorR.x, anchorR.y, anchorR.z);
                glVertex3f(anchorR.x, anchorR.y, -0);
            }
            glEnd();

        }
        glPopMatrix();
    }
}
