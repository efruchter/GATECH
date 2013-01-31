package toritools.leveleditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import toritools.entity.Entity;
import toritools.math.Vector2;

@SuppressWarnings("serial")
public class VariableEditor extends JPanel {
    private JButton addVarButton = new JButton(" + "), resetVarButton = new JButton("Reset All");
    private JPanel buttonPanel = new JPanel();
    private Entity entity;
    private HashMap<String, JTextField> keys = new HashMap<String, JTextField>();
    private JLabel statusLabel = new JLabel("Variables");
    private LevelEditor editor;

    public VariableEditor(final LevelEditor editor) {
        setAlignmentY(Component.TOP_ALIGNMENT);
        setBackground(Color.cyan);
        buttonPanel.setBackground(Color.cyan);
        setBorder(BorderFactory.createRaisedBevelBorder());
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
        add(statusLabel);
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (getEntity() != null) {
                    saveCurrent();
                    JOptionPane.showMessageDialog(null, "Instance variables saved to entity!");
                }
            }
        });
        JPanel p = new JPanel();
        p.setBackground(Color.cyan);
        p.add(addVarButton);
        p.add(saveButton);
        p.add(resetVarButton);
        add(p);
        add(new JScrollPane(buttonPanel));
        this.editor = editor;
        addVarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (getEntity() == null)
                    return;
                String s = JOptionPane.showInputDialog("New variable name?");
                if (s != null && !s.isEmpty()) {
                    getEntity().getVariableCase().setVar(s, "DEFAULT");
                    Entity e = getEntity();
                    clear();
                    setEntity(e);
                }
            }
        });
        resetVarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (getEntity() == null)
                    return;
                try {
                    getEntity().getVariableCase().clear();
                    getEntity().getVariableCase().setVariables(
                            editor.importEntity(new File(getEntity().getFile())).getVariableCase().getVariables());
                    setEntity(getEntity());
                    JOptionPane.showMessageDialog(null, "Instance variables reset and saved to entity!");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(final Entity e) {
        clear();
        this.entity = e;
        loadVariables();
        setEnabled(entity != null);
        editor.repaint();
    }

    public void loadVariables() {
        if (entity != null)
            for (Entry<String, String> s : entity.getVariableCase().getVariables().entrySet()) {
                if (s.getKey().equals("layer") || s.getKey().startsWith("position.") || s.getKey().equals("template"))
                    continue;
                JPanel micro = new JPanel();
                micro.setBackground(Color.CYAN);
                String value = s.getValue();
                value = value != null ? value : "";
                JTextField field = new JTextField(value, 10);
                keys.put(s.getKey(), field);
                micro.add(new JLabel(s.getKey() + ":"));
                micro.add(field);
                buttonPanel.add(micro);
            }
        revalidate();
    }

    public void saveCurrent() {
        if (entity != null) {
            for (Entry<String, JTextField> s : keys.entrySet()) {
                String data = s.getValue().getText();
                if (!data.isEmpty()) {
                    entity.getVariableCase().getVariables().put(s.getKey(), s.getValue().getText());
                } else {
                    entity.getVariableCase().getVariables().remove(s.getKey());
                }

            }

            if (entity.getVariableCase().getVar("dimensions.x") != null)
                entity.setDim(new Vector2(entity.getVariableCase().getFloat("dimensions.x"), entity.getDim().y));
            if (entity.getVariableCase().getVar("dimensions.y") != null)
                entity.setDim(new Vector2(entity.getDim().x, entity.getVariableCase().getFloat("dimensions.y")));
            if (entity.getVariableCase().getVar("direction") != null)
                entity.setDirection((int) entity.getVariableCase().getFloat("direction"));
        }
        editor.repaint();
    }

    public void clear() {
        entity = null;
        buttonPanel.removeAll();
        keys.clear();
    }
}
