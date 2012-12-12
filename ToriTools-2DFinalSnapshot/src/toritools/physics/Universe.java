package toritools.physics;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.contacts.ContactEdge;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import org.jbox2d.dynamics.joints.WeldJointDef;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.EntityScript.EntityScriptAdapter;

/**
 * A wrapper for Box2d's world.
 * 
 * @author toriscope
 * 
 */
public class Universe {

    final private World world;
    final private HashMap<Entity, Body> bodyMap;
    final private List<ContactListener> contactListeners;

    final public float PTM_RATIO = 32;

    public Universe(final Vector2 gravity) {
        world = new World(new Vec2(gravity.x, gravity.y), true);
        bodyMap = new HashMap<Entity, Body>();
        contactListeners = new LinkedList<ContactListener>();

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact arg0) {
                for (ContactListener c : contactListeners)
                    c.beginContact(arg0);
            }

            @Override
            public void endContact(Contact arg0) {
                for (ContactListener c : contactListeners)
                    c.endContact(arg0);
            }

            @Override
            public void postSolve(Contact arg0, ContactImpulse arg1) {
                for (ContactListener c : contactListeners)
                    c.postSolve(arg0, arg1);
            }

            @Override
            public void preSolve(Contact arg0, Manifold arg1) {
                for (ContactListener c : contactListeners)
                    c.preSolve(arg0, arg1);
            }
        });
    }

    /**
     * Add a contact listener to the physics universe.
     * 
     * @param listener
     */
    public void addContactListener(final ContactListener listener) {
        contactListeners.add(listener);
    }

    public void step(final float totaldt, int numSteps) {

        /*
         * Step the world
         */
        float tinyStep = totaldt / numSteps;
        for (float i = 0; i < totaldt; i += tinyStep)
            world.step(tinyStep, 20, 20);
    }

    public Body addEntity(final Entity ent, final BodyType bodyType, final boolean allowRotation,
            final boolean spherical, final float density, final float friction, final Vector2[] points,
            final boolean isSensor) {
        BodyDef bd = new BodyDef();
        bd.fixedRotation = !allowRotation;
        bd.type = bodyType;

        Shape p = null;
        if (points == null) {
            bd.position.set(ent.getPos().x / PTM_RATIO + ent.getDim().x / 2 / PTM_RATIO, ent.getPos().y / PTM_RATIO
                    + ent.getDim().y / 2 / PTM_RATIO);
            if (!spherical) {
                p = new PolygonShape();
                ((PolygonShape) p).setAsBox(ent.getDim().x / PTM_RATIO / 2, ent.getDim().y / PTM_RATIO / 2);
            } else {
                p = new CircleShape();
                p.m_radius = ent.getDim().x / PTM_RATIO / 2;
            }
        } else {
            bd.position.set(ent.getPos().add(ent.getDim().scale(.5f)).scale(1f / PTM_RATIO).toVec());
            PolygonShape ss = new PolygonShape();
            ss.setAsEdge(points[0].scale(1f / PTM_RATIO).toVec(), points[1].scale(1f / PTM_RATIO).toVec());
            p = ss;
        }

        // Create a fixture for ball
        FixtureDef fd = new FixtureDef();
        fd.shape = p;
        fd.density = density;
        fd.friction = friction;
        fd.restitution = .3f;
        // fd.filter.categoryBits = cat;
        fd.userData = ent;
        fd.isSensor = isSensor;

        fd.filter.categoryBits = 1;

        Body body = world.createBody(bd);
        body.createFixture(fd);
        body.m_userData = ent;

        bodyMap.put(ent, body);

        if (bodyType == BodyType.DYNAMIC)
            ent.addScript(serviceScript);

        return body;
    }

    /**
     * Add an entity to the simulation. The entity will be given an additional
     * script that keeps it synced with its simulation representation..
     * 
     * @param ent
     *            the entity to add.
     * @param bodyType
     *            the Body type
     * @param allowRotation
     *            true if rotation should be allowed.
     * @param density
     *            default is 1, adjust accordingly.
     */
    public Body addEntity(final Entity ent, final BodyType bodyType, final boolean allowRotation,
            final boolean spherical, final float density, final float friction) {
        return addEntity(ent, bodyType, allowRotation, spherical, density, friction, null, false);
    }

    /**
     * Construct a hinge. Be sure the shapes are overlapping at the given point.
     * 
     * @param a
     * @param b
     * @param hingePosition
     */
    public Joint addHinge(Entity a, Entity b, final Vector2 hingePosition) {
        RevoluteJointDef def = new RevoluteJointDef();
        def.initialize(bodyMap.get(a), bodyMap.get(b), hingePosition.scale(1f / PTM_RATIO).toVec());
        return world.createJoint(def);
    }

    public Joint addWeld(Entity a, Entity b) {
        WeldJointDef def = new WeldJointDef();
        def.initialize(bodyMap.get(a), bodyMap.get(b), bodyMap.get(a).getWorldCenter());
        return world.createJoint(def);
    }

    /**
     * This script keeps the entity model synced with the physics model.
     */
    private final EntityScript serviceScript = new EntityScriptAdapter() {

        @Override
        public void onDeath(Entity self, Level level, boolean isRoomExit) {
            if (!isRoomExit) {
                removeEntity(self);
            }
        }

        @Override
        public void onUpdate(Entity self, float time, Level level) {
            Transform body = bodyMap.get(self).getTransform();
            Vector2 newPos = new Vector2((body.position.x * PTM_RATIO) - self.getDim().x / 2, body.position.y
                    * PTM_RATIO - self.getDim().y / 2);
            // System.out.println(newPos + " | " + self.getPos());
            self.setPos(newPos);
            self.setDirection((int) (body.getAngle() * 57.3));
        }
    };

    /**
     * Remove an entity, and corresponding body from the map and the simulation.
     * 
     * @param ent
     */
    public void removeEntity(Entity ent) {
        if (bodyMap.containsKey(ent)) {
            world.destroyBody(bodyMap.get(ent));
            bodyMap.remove(ent);
        }
    }

    public void applyForce(final Entity e, final Vector2 force) {
        Body b = bodyMap.get(e);
        if (b != null)
            b.applyForce(force.scale(1f / b.m_mass).toVec(), b.getWorldCenter());
    }

    public void applyLinearImpulse(final Entity e, final Vector2 force) {
        Body b = bodyMap.get(e);
        if (b != null)
            b.applyLinearImpulse(force.scale(1f / b.m_mass).toVec(), b.getWorldCenter());
    }

    public void setVelocity(Entity dragging, Vector2 scale) {
        Body b = bodyMap.get(dragging);
        if (b != null)
            b.setLinearVelocity(scale.toVec());
    }

    public boolean isCollidingWithType(final Entity e, final String type) {
        ContactEdge edge = bodyMap.get(e).getContactList();
        while (edge != null) {
            if (((Entity) edge.other.m_userData).getType().equals(type))
                return true;
            edge = edge.next;
        }
        return false;
    }

    public void setAngularDamping(final Entity e, final int damping) {
        bodyMap.get(e).setAngularDamping(.5f);
    }

    public void setRotationDeg(final Entity e, final float rot) {
        Body b = bodyMap.get(e);
        b.setTransform(b.getWorldCenter(), rot / 57.3f);
    }

    public void destroyJoint(final Joint joint) {
        world.destroyJoint(joint);
    }

    public void setTransform(final Entity e, final Vector2 pos, final float angleDeg) {
        bodyMap.get(e).setTransform(pos.scale(1f / PTM_RATIO).toVec(), angleDeg / 57.3f);
    }
}
