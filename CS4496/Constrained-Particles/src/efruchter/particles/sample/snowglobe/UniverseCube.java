package efruchter.particles.sample.snowglobe;

import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3f;

import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

import efruchter.vectorutils.Vector3;


/**
 * A universe cube. A rough struct for SnowGlobe, but can be used for a bunch
 * of silly stuff.
 * 
 * @author toriscope
 */
public class UniverseCube {
	public float    xL, xH, yL, yH, zL, zH;
	public int      sideLength;
	public Vector3 pos;

	/**
	 * Cube instantiated to (0,0) with side sL
	 * 
	 * @param sideLength
	 */
	public UniverseCube(final int sideLength) {
		this.sideLength = sideLength;
		pos = Vector3.ZERO;
		recalcBoundaries();
		sphere.setDrawStyle(GLU.GLU_SILHOUETTE);
	}

	public void recalcBoundaries() {
		xL = pos.x - sideLength / 2;
		xH = pos.x + sideLength / 2;

		yL = pos.y - sideLength / 2;
		yH = pos.y + sideLength / 2;

		zL = pos.z - sideLength / 2;
		zH = pos.z + sideLength / 2;
	}

	public boolean isWithin(final Vector3 p) {
		return p.x > xL && p.x < xH && p.y > yL && p.y < yH && p.z > zL && p.z < zH;
	}
	
	public void glSurfaceDraw() {
		glBegin(GL_TRIANGLE_STRIP);
		{
			// Top
			glVertex3f(xL, yH, zH);
			glVertex3f(xH, yH, zH);
			glVertex3f(xH, yL, zH);
			glVertex3f(xL, yL, zH);
			glVertex3f(xL, yH, zH);

			// right
			glVertex3f(xH, yL, zH);
			glVertex3f(xH, yH, zH);
			glVertex3f(xH, yH, zL);
			glVertex3f(xH, yL, zL);
			glVertex3f(xH, yL, zH);

			// Left
			glVertex3f(xL, yL, zH);
			glVertex3f(xL, yH, zH);
			glVertex3f(xL, yH, zL);
			glVertex3f(xL, yL, zL);
			glVertex3f(xL, yL, zH);

			// Back
			glVertex3f(xL, yH, zH);
			glVertex3f(xH, yH, zH);
			glVertex3f(xH, yH, zL);
			glVertex3f(xL, yH, zL);
			glVertex3f(xL, yH, zH);

			// Front
			glVertex3f(xL, yL, zH);
			glVertex3f(xH, yL, zH);
			glVertex3f(xH, yL, zL);
			glVertex3f(xL, yL, zL);
			glVertex3f(xL, yL, zH);
		}
		glEnd();
	}

	private Sphere sphere = new Sphere();

	public void glSphereDraw() {

		glColor4f(1f, 1f, 1f, .1f);
		glPushMatrix();
		{
			glTranslatef(pos.x, pos.y, pos.z);
			sphere.draw(sideLength / 2, 15, 15);
		}
		glPopMatrix();
	}

	public static enum Side {
		XL, XH, YL, YH, ZL, ZH, NONE;
	}
}
