package soundfriend.types;

import java.io.File;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.sprite.ImageSprite;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;

public class Food extends Entity implements EntityScript {

    private int timer = 0;

    final static private int MAX_TIME = 1000;

    public Food() {
        this.addScript(this);

        this.setType("food");

        this.setDim(new Vector2(32, 32));
        this.setSprite(new ImageSprite(new File("tamodatchi/food.png"), 12, 4));
        this.getSprite().setCycle((int) (Math.random() * 4));
        this.getSprite().setFrame((int) (Math.random() * 12));
    }

    @Override
    public void onSpawn(Entity self, Level level) {
        timer = 0;
        self.setPos(new Vector2(100, 100).add(ScriptUtils.getCurrentLevel().getDim().sub(new Vector2(200, 200))
                .scale((float) Math.random(), (float) Math.random())));
    }

    @Override
    public void onUpdate(Entity self, float time, Level level) {
        if (timer++ > MAX_TIME) {
            level.despawnEntity(self);
        }
    }

    @Override
    public void onDeath(Entity self, Level level, boolean isRoomExit) {

    }

}
