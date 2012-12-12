package toritools.entity;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import toritools.entity.sprite.AbstractSprite;
import toritools.map.VariableCase;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;

/**
 * A scripted entity that exists in the game world. Designed to start with some
 * basic settings. Be sure to set up the specific things you want!
 * 
 * @author toriscope
 * 
 */
public class Entity {

    /**
     * Static vars for optimization.
     */
    protected static Vector2 BASE_VECT = Vector2.ZERO;;
    protected static AbstractSprite BASE_SPRITE = new AbstractSprite.AbstractSpriteAdapter();

    /**
     * Holds all variables for the entity.
     */
    protected VariableCase variables = new VariableCase();

    /**
     * The control script for this entity.
     */
    protected List<EntityScript> scripts = new ArrayList<EntityScript>();

    /*
     * INSTANCE ENTITY VARIABLES
     */
    protected Vector2 pos = BASE_VECT, dim = BASE_VECT;
    protected boolean solid = false;
    protected String type = "";
    protected int layer = 0;
    protected boolean visible = true;
    protected boolean active = true;

    protected AbstractSprite sprite = BASE_SPRITE;

    protected boolean inView = true;

    /*
     * Editor variables!
     */
    protected String file = "";

    protected int direction = 0;

    /*
     * CONTROL METHODS
     */

    public void onSpawn(final Level level) {
        for (EntityScript script : new ArrayList<EntityScript>(scripts))
            script.onSpawn(this, level);
    }

    public void onUpdate(final float time, final Level level) {
        for (EntityScript script : new ArrayList<EntityScript>(scripts))
            script.onUpdate(this, time, level);
    }

    public void onDeath(final Level level, final boolean isRoomExit) {
        for (EntityScript script : new ArrayList<EntityScript>(scripts))
            script.onDeath(this, level, isRoomExit);
    }

    public void draw(final Graphics2D g) {
        sprite.draw(g, this);
    }

    public void addVariables(final HashMap<String, String> variables) {
        this.variables.getVariables().putAll(variables);
    }

    public Vector2 getPos() {
        return pos;
    }

    public void setPos(Vector2 pos) {
        this.pos = pos;
    }

    public Vector2 getDim() {
        return dim;
    }

    public void setDim(Vector2 dim) {
        this.dim = dim;
    }

    public boolean isSolid() {
        return solid;
    }

    public void setSolid(boolean solid) {
        this.solid = solid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public AbstractSprite getSprite() {
        return sprite;
    }

    public void setSprite(AbstractSprite sprite) {
        this.sprite = sprite;
    }

    public boolean isInView() {
        return inView;
    }

    public void setInView(boolean inView) {
        this.inView = inView;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction % 360;
    }

    public VariableCase getVariableCase() {
        return variables;
    }

    public int getLayer() {
        return layer;
    }

    public String getFile() {
        return file;
    }

    public void setFile(final String file) {
        this.file = file;
    }

    public void addScript(EntityScript script) {
        scripts.add(script);
    }

    public void clearScripts() {
        scripts.clear();
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }
}
