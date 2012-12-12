package snakemeleon.types;

import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.Joint;

import snakemeleon.Snakemeleon;
import snakemeleon.SnakemeleonConstants;
import toritools.debug.Debug;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.scripting.EntityScript.EntityScriptAdapter;
import toritools.scripting.ScriptUtils;

/**
 * This script allows the chameleon to stick to things! Make sure to add it
 * after the entity is added to the physics world, so it can apply the direction
 * negation. It also has the necessary stuff to handle contact detection.
 * 
 * @author toriscope
 * 
 */
public class ChameleonStickyScript extends EntityScriptAdapter {

    public static boolean isGrabbing = false;

    public static Entity grabbingEntity = null;

    private List<Entity> touchQueue = new LinkedList<Entity>();

    Joint weld = null;

    @Override
    public void onSpawn(Entity self, Level level) {
        isGrabbing = false;
        grabbingEntity = null;

        Snakemeleon.uni.addContactListener(new ContactListener() {

            @Override
            public void beginContact(Contact c) {

                Entity a = (Entity) c.m_fixtureA.m_userData, b = (Entity) c.m_fixtureB.m_userData;
                boolean playerisA = ((Entity) c.m_fixtureA.m_userData).getType().equals("player");
                boolean playerisB = ((Entity) c.m_fixtureB.m_userData).getType().equals("player");

                if (playerisA && b.getType().equals(SnakemeleonConstants.dynamicPropType)) {
                    touchQueue.add(b);
                } else if (playerisB && a.getType().equals(SnakemeleonConstants.dynamicPropType)) {
                    touchQueue.add(a);
                }
            }

            @Override
            public void endContact(Contact c) {

                Entity a = (Entity) c.m_fixtureA.m_userData, b = (Entity) c.m_fixtureB.m_userData;
                boolean playerisA = ((Entity) c.m_fixtureA.m_userData).getType().equals("player");
                boolean playerisB = ((Entity) c.m_fixtureB.m_userData).getType().equals("player");

                if (playerisA && b.getType().equals(SnakemeleonConstants.dynamicPropType)) {
                    touchQueue.remove(b);
                } else if (playerisB && a.getType().equals(SnakemeleonConstants.dynamicPropType)) {
                    touchQueue.remove(a);
                }
            }

            @Override
            public void postSolve(Contact arg0, ContactImpulse arg1) {

            }

            @Override
            public void preSolve(Contact arg0, Manifold arg1) {

            }
        });

        self.addScript(new EntityScriptAdapter() {
            @Override
            public void onUpdate(Entity self, float time, Level level) {
                if (!isGrabbing)
                    self.setDirection(0);
            }
        });
    }

    @Override
    public void onUpdate(Entity self, float time, Level level) {

        boolean grabKey = ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_SPACE) || Snakemeleon.rightSticking;

        // Activate weld
        if (grabKey && !isGrabbing && !touchQueue.isEmpty()) {
            Snakemeleon.uni.setRotationDeg(self, 0);
            weld = Snakemeleon.uni.addWeld(self, grabbingEntity = touchQueue.get(0));
            isGrabbing = true;
            Debug.print("Joint created");
        }

        // Destroy weld
        if (!grabKey && isGrabbing && weld != null) {
            Snakemeleon.uni.destroyJoint(weld);
            isGrabbing = false;
            Debug.print("Joint Destroyed");
            grabbingEntity = null;
        }
    }
}
