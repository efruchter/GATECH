package toritools.entity.sprite;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.VolatileImage;
import java.io.File;

import toritools.entity.Entity;
import toritools.entrypoint.Binary;
import toritools.math.Vector2;
import toritools.scripting.ScriptUtils;

public class ImageSprite implements AbstractSprite {

    private int xSplit = 1, ySplit = 1, x = 0, y = 0;// w, h;
    private File imageIndex;

    private Vector2 bRight;
    private int timeStretch = 1;
    private float sizeOffset = 0;

    /**
     * Create a sprite with given image;
     * 
     * @param image
     *            an image
     * @param xTiles
     *            tiles win x direction
     * @param yTiles
     *            tiles in y direction
     */
    public ImageSprite(final File imageIndex, final int xTiles, final int yTiles) {
        this.imageIndex = imageIndex;
        this.xSplit = xTiles;
        this.ySplit = yTiles;
        Image image = ScriptUtils.fetchImage(imageIndex);
        bRight = new Vector2(image.getWidth(null) / xSplit, image.getHeight(null) / ySplit);
    }

    /**
     * Use this constructor if you plan on overriding draw(); Everything will be
     * null.
     */
    public ImageSprite() {

    }

    public void nextFrame() {
        x = ++x % (xSplit * timeStretch);
    }

    public void nextFrameAbsolute() {
        int timeStretch = 1;
        x = ++x % (xSplit * timeStretch);
    }

    public void setFrame(final int frame) {
        x = frame * timeStretch % (xSplit * timeStretch);
    }

    public void setCycle(final int cycle) {
        y = cycle % ySplit;
    }

    public void set(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    VolatileImage i;

    /**
     * Override this to implement your own drawing mechanism!
     * 
     * @param g
     * @param self
     * @param position
     * @param dimension
     */
    public void draw(final Graphics2D g, final Entity self) {
        int x = this.x / timeStretch;
        Vector2 dim = self.getDim().add(sizeOffset * 2);
        Vector2 pos = self.getPos().sub(sizeOffset);

        if (self.getDirection() != 0) {

            if (i == null)
                i = Binary.gc.createCompatibleVolatileImage(dim.getWidth(), dim.getHeight(), VolatileImage.TRANSLUCENT);
            i.validate(Binary.gc);

            Graphics2D gr = (Graphics2D) i.getGraphics();
            gr.setColor(new Color(0, 0, 0, 0));
            gr.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OUT));
            gr.fillRect(0, 0, i.getWidth(), i.getHeight());

            gr.drawImage(ScriptUtils.fetchImage(imageIndex), (int) 0, (int) 0, (int) dim.x, (int) dim.y, x
                    * (int) bRight.x, y * (int) bRight.y, x * (int) bRight.x + (int) bRight.x, y * (int) bRight.y
                    + (int) bRight.y, null);

            AffineTransform affineTransform = new AffineTransform();
            // rotate with the anchor point as the mid of the image
            affineTransform.translate(pos.x, pos.y);
            affineTransform.rotate(Math.toRadians(self.getDirection()), dim.x / 2, dim.y / 2);

            ((Graphics2D) g).drawImage(i, affineTransform, null);
        } else {
            g.drawImage(ScriptUtils.fetchImage(imageIndex), (int) pos.x, (int) pos.y, (int) (pos.x + dim.x),
                    (int) (pos.y + dim.y), x * (int) bRight.x, y * (int) bRight.y, x * (int) bRight.x + (int) bRight.x,
                    y * (int) bRight.y + (int) bRight.y, null);
        }
    }

    public Dimension getTileDimension() {
        return new Dimension(x, y);
    }

    @Override
    public void setTimeStretch(int timeStretch) {
        this.timeStretch = timeStretch;
    }

    @Override
    public void setsizeOffset(int sizeOffset) {
        this.sizeOffset = sizeOffset;
    }

    @Override
    public File getImageIndex() {
        return imageIndex;
    }
}
