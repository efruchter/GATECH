package toritools.entity;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.VolatileImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import toritools.debug.Debug;
import toritools.entrypoint.Binary;
import toritools.math.Vector2;
import toritools.scripting.ScriptUtils;

public class Level extends Entity {

    private HashMap<String, Entity> idMap = new HashMap<String, Entity>();
    private List<List<Entity>> layers = new ArrayList<List<Entity>>();

    /**
     * The entity lists.
     */
    private List<Entity> solids = new ArrayList<Entity>(), nonSolids = new ArrayList<Entity>();

    /**
     * The type map
     */
    private HashMap<String, List<Entity>> typeMap = new HashMap<String, List<Entity>>();

    private List<Entity> trash = new ArrayList<Entity>();
    private List<Entity> newEntities = new ArrayList<Entity>();

    private Entity viewPort;

    private void addEntityUnsafe(final Entity e) {
        layers.get(e.getLayer()).add(e);
        if (e.isSolid()) {
            solids.add(e);
        } else {
            nonSolids.add(e);
        }
    }

    private void removeEntityUnsafe(final Entity e) {
        e.setActive(false);
        layers.get(e.getLayer()).remove(e);
        if (e.isSolid())
            solids.remove(e);
        else
            nonSolids.remove(e);
        String id;
        if ((id = e.getVariableCase().getVar("id")) != null) {
            idMap.remove(id);
        }
        typeMap.get(e.getType()).remove(e);
    }

    public void spawnEntity(final Entity entity) {
        getNewEntities().add(entity);
        String id;
        if ((id = entity.getVariableCase().getVar("id")) != null) {
            idMap.put(id, entity);
        }
        if (!typeMap.containsKey(entity.getType())) {
            typeMap.put(entity.getType(), new ArrayList<Entity>());
        }
        typeMap.get(entity.getType()).add(entity);
    }

    public void despawnEntity(final Entity entity) {
        trash.add(entity);
    }

    private void spawnNewEntities() {
        List<Entity> tempList = new ArrayList<Entity>(getNewEntities());
        getNewEntities().clear();
        for (Entity e : tempList) {
            addEntityUnsafe(e);
        }
        for (Entity e : tempList) {
            e.onSpawn(this);
        }
    }

    private void takeOutTrash() {
        List<Entity> tempList = new ArrayList<Entity>(trash);
        trash.clear();
        for (Entity e : tempList) {
            removeEntityUnsafe(e);
        }
        for (Entity e : tempList) {
            e.onDeath(this, false);
        }
    }

    @Override
    public void onSpawn(final Level level) {
        spawnNewEntities();
    }

    public void onUpdate(final float time) {
        spawnNewEntities();
        for (Entity e : solids) {
            e.onUpdate(time, this);
        }
        for (Entity e : nonSolids) {
            e.onUpdate(time, this);
        }
        takeOutTrash();

        if (viewPort != null) {
            for (Entity e : solids) {
                e.setInView(ScriptUtils.isColliding(viewPort, e));
            }
            for (Entity e : nonSolids) {
                e.setInView(ScriptUtils.isColliding(viewPort, e));
            }
        }
    }

    public void onDeath(final boolean isRoomExit) {
        for (Entity e : solids) {
            e.onDeath(this, isRoomExit);
        }
        for (Entity e : nonSolids) {
            e.onDeath(this, isRoomExit);
        }
    }

    public Level() {
        super();
        for (int i = 0; i < 10; i++) {
            layers.add(new ArrayList<Entity>());
        }
    }

    public Entity getEntityWithId(final String id) {
        return idMap.get(id);
    }

    public List<Entity> getEntitiesWithType(final String type) {
        if (typeMap.containsKey(type))
            return typeMap.get(type);
        return new ArrayList<Entity>();
    }

    public void setViewportData(final Vector2 pos, final Vector2 dim) {
        if (viewPort == null) {
            viewPort = new Entity();
        }
        viewPort.setPos(pos);
        viewPort.setDim(dim);
    }

    private Image baked;

    /**
     * Draw every BACKGROUND object to a large volatile image, and delete all
     * the objects. This speeds up performance by removing pointless iterations
     * and lookups, but prevents background objects from being in the
     * foreground.
     * 
     * @return the baked image.
     */
    public Image bakeBackground() {
        baked = Binary.gc.createCompatibleVolatileImage((int) dim.x, (int) dim.y, VolatileImage.TRANSLUCENT);
        ((VolatileImage) baked).validate(Binary.gc);

        Graphics2D gr = (Graphics2D) baked.getGraphics();
        gr.setColor(new Color(0, 0, 0, 0));
        gr.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OUT));
        gr.fillRect(0, 0, (int) dim.x, (int) dim.y);
        for (Entity e : getEntitiesWithType(ReservedTypes.BACKGROUND.toString())) {
            despawnEntity(e);
            e.draw((Graphics2D) baked.getGraphics());
        }
        Debug.print("Baking Backgrounds...");
        return baked;
    }

    /**
     * Returns the baked background. Make sure you bake it first!
     * 
     * @return the baked bg, or null if none exists. DO NOT BAKE WHILE DRAWING.
     */
    public Image getBakedBackground() {
        return baked;
    }

    public List<List<Entity>> getLayers() {
        return layers;
    }

    public List<Entity> getSolids() {
        return solids;
    }

    public List<Entity> getNonSolids() {
        return nonSolids;
    }

    /**
     * Grab a highly volatile list of the entities queued for spawn. Avoid!
     */
    public List<Entity> getNewEntities() {
        return newEntities;
    }
}
