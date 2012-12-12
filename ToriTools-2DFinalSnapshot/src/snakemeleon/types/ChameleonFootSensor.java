package snakemeleon.types;

import java.awt.Color;
import java.awt.Graphics2D;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.contacts.Contact;

import snakemeleon.Snakemeleon;
import toritools.debug.Debug;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.sprite.AbstractSprite.AbstractSpriteAdapter;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;

public class ChameleonFootSensor extends Entity {

    private int jumpTouchQueue = 0;

    public ChameleonFootSensor(final Entity chameleon) {
        setDim(new Vector2(20, 20));
        addScript(new EntityScript() {

            @Override
            public void onSpawn(Entity self, Level level) {

                /*
                 * Register the contact listener with the universe.
                 */
                Snakemeleon.uni.addContactListener(new ContactListener() {
                    @Override
                    public void beginContact(Contact c) {
                        boolean playerisA = ((Entity) c.m_fixtureA.m_userData).getType().equals("player");
                        boolean playerisB = ((Entity) c.m_fixtureB.m_userData).getType().equals("player");

                        if (c.m_fixtureA.m_userData instanceof ChameleonFootSensor && !playerisB) {
                            ChameleonFootSensor.this.jumpTouchQueue++;
                        } else if (c.m_fixtureB.m_userData instanceof ChameleonFootSensor && !playerisA) {
                            ChameleonFootSensor.this.jumpTouchQueue++;
                        }

                    }

                    @Override
                    public void endContact(Contact c) {

                        boolean playerisA = ((Entity) c.m_fixtureA.m_userData).getType().equals("player");
                        boolean playerisB = ((Entity) c.m_fixtureB.m_userData).getType().equals("player");

                        if (c.m_fixtureA.m_userData instanceof ChameleonFootSensor && !playerisB) {
                            ChameleonFootSensor.this.jumpTouchQueue--;
                        } else if (c.m_fixtureB.m_userData instanceof ChameleonFootSensor && !playerisA) {
                            ChameleonFootSensor.this.jumpTouchQueue--;
                        }
                    }

                    @Override
                    public void postSolve(Contact arg0, ContactImpulse arg1) {

                    }

                    @Override
                    public void preSolve(Contact arg0, Manifold arg1) {

                    }
                });

            }

            @Override
            public void onUpdate(Entity self, float time, Level level) {
                Snakemeleon.uni.setTransform(self, chameleon.getPos().add(chameleon.getDim().scale(.5f, 1)), 0);
            }

            @Override
            public void onDeath(Entity self, Level level, boolean isRoomExit) {

            }

        });

        setSprite(new AbstractSpriteAdapter() {
            @Override
            public void draw(Graphics2D g, Entity self) {
                if (Debug.showDebugPrintouts) {
                    g.setColor(canJump() ? Color.GREEN : Color.RED);
                    g.drawRect(self.getPos().getWidth(), self.getPos().getHeight(), self.getDim().getWidth(), self
                            .getDim().getHeight());
                }
            }
        });

        setPos(chameleon.getPos().add(chameleon.getDim()));
        Snakemeleon.uni.addEntity(this, BodyType.DYNAMIC, true, true, 1f, .3f, null, true);
    }

    public boolean canJump() {
        return jumpTouchQueue > 0 && !ChameleonStickyScript.isGrabbing;
    }
}
