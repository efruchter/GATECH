package audioProject.entities;

import java.awt.Graphics2D;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.sprite.AbstractSprite.AbstractSpriteAdapter;
import toritools.math.Vector2;
import toritools.scripting.EntityScript.EntityScriptAdapter;
import toritools.scripting.ScriptUtils;
import audioProject.AudioProject;

public class GoodBullet extends Entity {

    /**
     * Build a good booooleeeeet
     * 
     * @param position
     *            the starting position of bullet.
     * @param spreadFactor
     *            the spread factor. 0 is no spread.
     */
    public GoodBullet(final Vector2 position, final Vector2 delta) {
        type = "GoodBullet";

        layer = 1;

        pos = position;
        dim = new Vector2(10, 10);

        addScript(new EntityScriptAdapter() {

            float damage = 20;

            boolean explodeDeath = false;

            @Override
            public void onUpdate(Entity self, float time, Level level) {

                if (!ScriptUtils.isColliding(level, self)) {
                    level.despawnEntity(self);
                }

                self.setPos(self.getPos().add(delta.scale(time)));

                for (Entity enemy : level.getEntitiesWithType("enemy")) {
                    if (ScriptUtils.isCollidingRad(self, enemy)) {
                        level.despawnEntity(self);
                        enemy.getVariableCase().setVar("health",
                                enemy.getVariableCase().getFloat("health") - damage + "");
                        explodeDeath = true;
                        break;
                    }
                }
            }

            @Override
            public void onDeath(Entity self, Level level, boolean isRoomExit) {
                if (explodeDeath)
                    level.spawnEntity(new Explosion(self.getPos(), AudioProject.shipColor, self.getDim().x, 20));
            }
        });

        setSprite(new AbstractSpriteAdapter() {
            @Override
            public void draw(Graphics2D g, Entity self) {
                g.setColor(AudioProject.shipColor);
                g.drawOval((int) self.getPos().x, (int) (self.getPos().y), (int) self.getDim().x, (int) self.getDim().y);
            }
        });
    }
}
