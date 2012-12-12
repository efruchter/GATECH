package snakemeleon.types;

import java.io.File;
import java.io.FileNotFoundException;

import maryb.player.Player;

import org.jbox2d.dynamics.BodyType;

import snakemeleon.Snakemeleon;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.io.Importer;
import toritools.scripting.EntityScript;

public class Collectable implements EntityScript {

    private static int collectablesRemaining = 0;
    
    public static Player munchSound;
    
    static {        
        munchSound = new Player();
        munchSound.setSourceLocation("snakemeleon/sounds/bite.mp3");
    }

    @Override
    public void onSpawn(Entity self, Level level) {
        collectablesRemaining++;
    }

    @Override
    public void onUpdate(Entity self, float time, Level level) {
        
    }

    @Override
    public void onDeath(Entity self, Level level, boolean isRoomExit) {
        
        if(!isRoomExit)
            munchSound.play();
        
        collectablesRemaining--;

        try {
            for (int i = 0; i < 2; i++)
                for (int i2 = 0; i2 < 2; i2++) {
                    Entity e = Importer.importEntity(new File("snakemeleon/objects/collectable/appleBit.entity"), null);
                    e.setPos(self.getPos());
                    e.getSprite().set(i, i2);

                    e.addScript(new EntityScriptAdapter() {
                        int killClock = 120;

                        @Override
                        public void onUpdate(Entity self, float time, Level level) {
                            if (killClock-- < 0) {
                                level.despawnEntity(self);
                            }
                        }

                    });

                    level.spawnEntity(e);
                    Snakemeleon.uni.addEntity(e, BodyType.DYNAMIC, true, true, .01f, .03f);
                }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static int getCollectablesRemaining() {
        return collectablesRemaining;
    }

}
