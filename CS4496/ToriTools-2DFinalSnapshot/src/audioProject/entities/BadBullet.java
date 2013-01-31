package audioProject.entities;

import java.awt.Graphics2D;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.sprite.AbstractSprite.AbstractSpriteAdapter;
import toritools.math.Vector2;
import toritools.scripting.EntityScript.EntityScriptAdapter;
import toritools.scripting.ScriptUtils;
import audioProject.AudioProject;

public class BadBullet extends Entity {

    /**
     * Build a bad booooleeeeet
     * 
     * @param position
     *            the starting position of bullet.
     * @param speed
     *            the delta per update.
     */
    public BadBullet(final Vector2 position, final Vector2 speed, final float radius) {
        type = "BadBullet";

        getVariableCase().setVar("damage", "5");

        layer = 2;

        pos = position;
        dim = Vector2.ONE.scale(radius);

        addScript(new EntityScriptAdapter() {

            boolean explodeDeath = true;

            @Override
            public void onUpdate(Entity self, float time, Level level) {

                if (!ScriptUtils.isColliding(level, self)) {
                    level.despawnEntity(self);
                    explodeDeath = false;
                }

                self.setPos(self.getPos().add(speed.scale(time)));
            }

            @Override
            public void onDeath(Entity self, Level level, boolean isRoomExit) {
                if (explodeDeath)
                    level.spawnEntity(new Explosion(self.getPos(), AudioProject.enemyColor, self.getDim().x, 20));
            }
        });

        setSprite(new AbstractSpriteAdapter() {
            @Override
            public void draw(Graphics2D g, Entity self) {
                g.setColor(AudioProject.enemyColor);
                g.fillOval((int) self.getPos().x, (int) (self.getPos().y), (int) self.getDim().x, (int) self.getDim().y);
            }
        });
    }
}
