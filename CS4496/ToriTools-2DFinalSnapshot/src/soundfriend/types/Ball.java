package soundfriend.types;

import java.io.File;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.physics.PhysicsModule;
import toritools.entity.sprite.ImageSprite;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;

public class Ball extends Entity implements EntityScript {

    private PhysicsModule physics;

    // private int timer = 0;

    // final static private int MAX_TIME = 2 * 1000;

    public Ball() {
        this.addScript(this);

        this.setType("ball");

        this.setDim(new Vector2(32, 32));
        this.setSprite(new ImageSprite(new File("tamodatchi/ball.png"), 1, 1));
    }

    @Override
    public void onSpawn(Entity self, Level level) {
        physics = new PhysicsModule(Vector2.ZERO, new Vector2(.047f, .047f), self);
        self.setPos(new Vector2(100, 100).add(ScriptUtils.getCurrentLevel().getDim().sub(new Vector2(200, 200))
                .scale((float) Math.random(), (float) Math.random())));
    }

    @Override
    public void onUpdate(Entity self, float time, Level level) {

        self.setPos(self.getPos().add(physics.onUpdate(time)));

        for (Entity pet : level.getEntitiesWithType("pet")) {
            if (ScriptUtils.isColliding(pet, self)) {
                physics.addVelocity(self.getPos().add(self.getDim().scale(.5f))
                        .sub(pet.getPos().add(pet.getDim().scale(.5f))));
            }
        }

        // if (timer++ > MAX_TIME) {
        // level.despawnEntity(self);
        // }

        if (!ScriptUtils.isColliding(level, self)) {
            level.despawnEntity(self);
        }
    }

    @Override
    public void onDeath(Entity self, Level level, boolean isRoomExit) {

    }

}
