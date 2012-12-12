package snakemeleon.types;

import snakemeleon.SnakemeleonConstants;
import toritools.debug.Debug;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;

public class KeyTriggerEntityAction implements EntityScript {
    
    int number = 0;
    @Override
    public void onSpawn(Entity self, Level level) {
        number = (int) self.getVariableCase().getFloat("key");
            
    }

    @Override
    public void onUpdate(Entity self, float time, Level level) {
        for(Entity e: level.getEntitiesWithType(SnakemeleonConstants.triggerZoneType)) {
            if(ScriptUtils.isColliding(self, e)) {
                if(number == e.getVariableCase().getFloat("trigger")) {
                    Debug.print("Trigger: " + number);
                }
            }
        }
    }

    @Override
    public void onDeath(Entity self, Level level, boolean isRoomExit) {

    }

}
