package toritools.leveleditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import toritools.entity.Entity;
import toritools.io.Importer;
import toritools.math.Vector2;

@SuppressWarnings("serial")
public class BackgroundEditor extends JPanel {

    private File imageFile;

    private Dimension grid = new Dimension(32, 32);

    private Vector2 selStart = new Vector2();
    private Vector2 selEnd = new Vector2();
    private Vector2 imageDim = new Vector2();

    private LevelEditor editor;

    private JFrame frame;

    public BackgroundEditor(final LevelEditor editor) {
        this.editor = editor;
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent m) {
                selStart = selEnd = LevelEditor.getClosestGridPoint(getGrid(), new Vector2(m.getPoint()));
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent m) {
                selEnd = LevelEditor.getClosestGridPoint(getGrid(), new Vector2(m.getPoint()));
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent m) {
                mouseDragged(m);
                if (selEnd.x < selStart.x) {
                    float temp = selStart.x;
                    selStart = new Vector2(selEnd.x, selStart.y);
                    selEnd = new Vector2(temp, selEnd.y);
                }
                if (selEnd.y < selStart.y) {
                    float temp = selStart.y;
                    selStart = new Vector2(selStart.x, selEnd.y);
                    selEnd = new Vector2(selEnd.x, temp);
                }
                if (selStart.x == selEnd.x && selStart.y == selEnd.y) {
                    selEnd = selEnd.add(getGrid().width, getGrid().height);
                }
                repaint();
            }
        };
        this.addMouseListener(mouseAdapter);
        this.addMouseMotionListener(mouseAdapter);

        frame = new JFrame("Background Tile Selector");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JScrollPane(this));
    }

    public void paintComponent(Graphics g) {
        if (imageFile != null) {
            ImageIcon icon;
            g.drawImage((icon = new ImageIcon(imageFile.getPath())).getImage(), 0, 0, null);
            setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));

            imageDim = new Vector2(icon.getIconWidth(), icon.getIconHeight());

            g.setColor(Color.BLACK);
            // Draw grid
            for (int x = 0; x <= imageDim.x; x += grid.width)
                g.drawLine(x, 0, x, icon.getIconHeight());
            for (int y = 0; y <= imageDim.y; y += grid.height)
                g.drawLine(0, y, icon.getIconWidth(), y);

            // draw Selected
            g.setColor(Color.RED);
            g.draw3DRect((int) selStart.x, (int) selStart.y, (int) selEnd.x - (int) selStart.x, (int) selEnd.y
                    - (int) selStart.y, true);
        }
    }

    public void setImageFile(final File file) {
        this.imageFile = file;
        ImageIcon icon = new ImageIcon(imageFile.getPath());
        setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
        frame.pack();
        frame.setVisible(true);
        repaint();
    }

    public void setupBg() {
        try {
            String result = JOptionPane.showInputDialog("Input an integer tile width, height (ex. 32, 64):");
            String vals[] = result.split(",");
            int width = Integer.parseInt(vals[0].trim());
            int height = vals.length == 2 ? Integer.parseInt(vals[1].trim()) : width;

            if (imageDim.x % width != 0 || imageDim.y % height != 0) {
                JOptionPane.showMessageDialog(null, "The tile size must divide the image cleanly.");
            } else {
                grid.setSize(width, height);
            }
            repaint();
        } catch (final Exception i) {
            return;
        }
    }

    private Dimension getGrid() {
        return grid;
    }

    public List<Entity> makeEntities(final Vector2 pos) {
        if (imageFile == null)
            return null;

        String relativeLink = imageFile.getPath().replace(editor.workingDirectory.getPath(), "");

        int numX = (int) (selEnd.x - selStart.x) / grid.width;
        int numY = (int) (selEnd.y - selStart.y) / grid.width;

        List<Entity> bgs = new ArrayList<Entity>(numX * numY);

        for (int x = 0; x < numX; x++) {
            for (int y = 0; y < numY; y++) {
                Entity bg = Importer.makeBackground(pos.add(x * grid.width, y * grid.width), new Vector2(grid.width,
                        grid.height), imageFile, relativeLink, (int) (selStart.x / grid.width) + x,
                        (int) (selStart.y / grid.width) + y, (int) (imageDim.x / grid.width),
                        (int) (imageDim.y / grid.height));
                bgs.add(bg);
            }
        }

        return bgs;
    }
}
