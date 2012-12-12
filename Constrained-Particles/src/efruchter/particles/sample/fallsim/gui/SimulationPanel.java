package efruchter.particles.sample.fallsim.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import efruchter.particles.datatypes.Particle;
import efruchter.particles.integrators.NewtonianIntegrators;
import efruchter.particles.integrators.NewtonianIntegrators.AccelerationFunction;
import efruchter.particles.integrators.NewtonianIntegrators.RKState;
import efruchter.vectorutils.Vector3;

@SuppressWarnings("serial")
public class SimulationPanel extends JPanel {

    /**
     * Time increment
     */
    private final float increment = .01f;

    /**
     * Standard gravity
     */
    static Vector3 gravity = new Vector3(0, 0, -9.81f);

    /*
     * The three particles
     */
    public Particle pAnalytical = new Particle(), pExplicit = new Particle(), pBetter = new Particle();

    public Particle[] system = new Particle[] { pAnalytical, pExplicit, pBetter };

    /*
     * Boilerplate.
     */
    private DecimalFormat formatter = new DecimalFormat("0.00000");
    private JLabel timeLabel = new JLabel();
    private JLabel aLabel = new JLabel();
    private JLabel bLabel = new JLabel();
    private JLabel resultLabel = new JLabel("-");
    private JButton runSimButton = new JButton("Start Sim");
    private JButton pauseSimButton = new JButton("Pause Sim");
    private JFrame frame = new JFrame("Simulation Control");

    private float time = 0;

    private boolean active = false;

    final private Vector3 anaStart = new Vector3(-50 / 2, 0, 80);
    
    final AccelerationFunction acc = new AccelerationFunction(){
        @Override
        public Vector3 getAcceleration(RKState state, float time) {
                return SimulationPanel.gravity;
        }
    };

    public SimulationPanel() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final Exception e) {
            System.err.println("Failed to grab a proper look-and-feel from the OS.");
        }

        setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        /**
         * Add the GUI components.
         */

        runSimButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                runSim();
            }
        });

        pauseSimButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                pauseSim();
            }
        });

        add(timeLabel);
        add(Box.createRigidArea(new Dimension(10, 5)));
        add(runSimButton);
        add(Box.createRigidArea(new Dimension(10, 5)));
        add(pauseSimButton);
        add(Box.createRigidArea(new Dimension(10, 5)));
        add(new JLabel("Positional Error of Particles:"));
        add(Box.createRigidArea(new Dimension(10, 2)));
        add(aLabel);
        add(Box.createRigidArea(new Dimension(10, 2)));
        add(bLabel);
        add(Box.createRigidArea(new Dimension(10, 5)));
        add(resultLabel);

        resultLabel.setForeground(Color.RED);
        resultLabel.setVisible(false);
        aLabel.setForeground(Color.BLUE);
        bLabel.setForeground(Color.BLUE);

        updateUILabels();

        setPreferredSize(new Dimension(300, 200));
        frame.pack();
        frame.setVisible(true);
    }

    private void runSim() {
        time = 0;
        active = true;

        pAnalytical.x = anaStart;
        pExplicit.x = new Vector3(0, 0, 80);
        pBetter.x = new Vector3(50 / 2, 0, 80);

        for (Particle p : system) {
            p.reset();
        }

        runSimButton.setText("Restart Sim");

        resultLabel.setVisible(true);
    }

    private void pauseSim() {
        active = !active && pAnalytical.x.z > 0;
    }

    public float getSimTime() {
        return time;
    }

    public void updateSimTime() {
        if (active) {
            time += increment;
            // Clear force accumulator
            for (Particle p : system)
                p.clearForces();

            // Accumulate forces
            for (Particle p : system)
                p.addForce(gravity);

            // Integrate
            if (pAnalytical.x.z > 0) {
                pAnalytical.x = posAnalyticalConstant(anaStart, Vector3.ZERO, gravity, time);
                NewtonianIntegrators.rungeKutta4(pExplicit, increment, acc);
                NewtonianIntegrators.trapazoidal(pBetter, increment);
                
                System.out.println("RK4: " + pExplicit.v);
            } else {
                pauseSim();
            }
        }

        updateUILabels();
    }

    private void updateUILabels() {
        timeLabel.setText("Time: " + time);
        aLabel.setText("RK4 : " + formatter.format(Math.abs(pAnalytical.x.z - pExplicit.x.z)) + " units");
        bLabel.setText("Trapazoidal    : " + formatter.format(Math.abs(pAnalytical.x.z - pBetter.x.z)) + " units");
        resultLabel.setText((pAnalytical.x.z > 0) ? "---" : "Analytical particle has hit ground!");
        if (pAnalytical.x.z > 0) {
            pauseSimButton.setText((active) ? "Pause Sim" : "Unpause Sim");
            pauseSimButton.setEnabled(true);
        } else {
            pauseSimButton.setEnabled(false);
        }
    }

    /**
     * Analytically find position at time for given inputs
     * 
     * @param x0
     *            initial position
     * @param v0
     *            initial velocity
     * @param a
     *            accleration
     * @param t
     *            time
     * @return the position at time t
     */
    private Vector3 posAnalyticalConstant(final Vector3 x0, final Vector3 v0, final Vector3 a, final float t) {
        return x0.add(v0.scale(t)).add(a.scale(t * t * .5f));
    }
}
