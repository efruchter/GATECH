package snakemeleon.types;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import snakemeleon.Snakemeleon;
import snakemeleon.SnakemeleonConstants;
import toritools.debug.Debug;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.sprite.AbstractSprite.AbstractSpriteAdapter;
import toritools.math.MidpointChain;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;
import toritools.scripting.ScriptUtils.Direction;

public class Tongue extends Entity {

    private MidpointChain tongueChain;

    private Vector2 mouthPoint = Vector2.ZERO;

    private boolean mouthClosed = true;

    private boolean mouseInRange = false;

    private float currentDist = 0;

    private Vector2 tongueMaxPos = Vector2.ZERO;

    Entity cham;

    public Tongue() {

        this.getVariableCase().setVar("id", "tongue");

        this.setDim(new Vector2(100, 100));
        this.setPos(Snakemeleon.mousePos);

        tongueChain = new MidpointChain(mouthPoint, SnakemeleonConstants.tongueLength);

        this.addScript(new EntityScript() {

            @Override
            public void onSpawn(Entity self, Level level) {
                Debug.print("Tongue spawned");
                Tongue.this.cham = level.getEntityWithId("player");
            }

            Entity dragging = null;

            @Override
            public void onUpdate(Entity self, float time, Level level) {
                if (Tongue.this.cham.isActive()) {

                    mouseInRange = (currentDist = Snakemeleon.mousePos.dist(Tongue.this.mouthPoint)) < SnakemeleonConstants.tongueActualDist;

                    tongueMaxPos = mouthPoint.add(Snakemeleon.mousePos.sub(mouthPoint).unit()
                            .scale(Math.min(currentDist, SnakemeleonConstants.tongueActualDist)));

                    if (mouseInRange && Snakemeleon.isMouseDragging && dragging == null) {
                        for (Entity e : level.getEntitiesWithType(SnakemeleonConstants.dynamicPropType)) {
                            if (ScriptUtils.isPointWithin(e, Snakemeleon.mousePos)) {
                                dragging = e;
                                tongueChain.setA(e.getPos().add(e.getDim().scale(.5f)));
                                mouthClosed = false;
                                break;
                            }
                        }
                    }
                    
                    boolean draggingSomething = Snakemeleon.isMouseDragging && dragging != null && dragging.isActive();

                    if (draggingSomething) {
                        tongueChain.smooth();
                        Vector2 dragAnchor = dragging.getPos().add(dragging.getDim().scale(.5f));
                        tongueChain.setA(dragAnchor);
                        Vector2 dragVector = (tongueMaxPos).sub(dragAnchor).scale(.5f / Snakemeleon.uni.PTM_RATIO);
                        Snakemeleon.uni.setVelocity(dragging, dragVector);
                        //Snakemeleon.uni.setVelocity(level.getEntityWithId("player"), dragVector.scale(0,-1));
                    } else {
                        tongueChain.smoothTowardB();
                        tongueChain.smoothTowardB();
                        tongueChain.smoothTowardB();
                        dragging = null;

                    }

                    tongueChain.setB(mouthPoint);

                    mouthClosed = !draggingSomething &&  (mouthClosed || tongueChain.getA().dist(tongueChain.getB()) < 20);
                }
            }

            @Override
            public void onDeath(Entity self, Level level, boolean isRoomExit) {

            }
        });

        this.setSprite(new AbstractSpriteAdapter() {
            @Override
            public void draw(Graphics2D g, Entity self) {
                if (Tongue.this.cham.isActive()) {
                    if (!mouthClosed) {
                        g.setColor(Color.RED);
                        g.setStroke(new BasicStroke(5));
                        Vector2[] chain = tongueChain.getChain();
                        for (int x = 1; x < SnakemeleonConstants.tongueLength; x++) {
                            g.drawLine(chain[x - 1].getWidth(), chain[x - 1].getHeight(), chain[x].getWidth(),
                                    chain[x].getHeight());
                        }
                    }

                    g.setStroke(new BasicStroke(3));
                    g.setColor(Color.RED);
                    g.drawOval(Snakemeleon.mousePos.getWidth() - 10, Snakemeleon.mousePos.getHeight() - 10, 20, 20);

                    g.setColor(Color.CYAN);
                    g.drawOval(tongueMaxPos.getWidth() - 10, tongueMaxPos.getHeight() - 10, 20, 20);
                }
            }
        });
    }

    public Direction getTongueFacing() {
        Vector2[] chain = tongueChain.getChain();
        float right = chain[chain.length - 1].x - chain[chain.length - 2].x;
        if (right == 0 || mouthClosed)
            return Direction.CENTER;
        if (right < 0)
            return Direction.RIGHT;

        return Direction.LEFT;
    }

    public void setMouthPoint(Vector2 mouthPoint) {
        this.mouthPoint = mouthPoint.add(SnakemeleonConstants.headWidth / 2);
    }
}
