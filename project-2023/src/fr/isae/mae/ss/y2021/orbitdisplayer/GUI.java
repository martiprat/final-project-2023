package fr.isae.mae.ss.y2021.orbitdisplayer;

import fr.cnes.sirius.patrius.math.util.FastMath;
import fr.cnes.sirius.patrius.utils.exception.PatriusException;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.*;


public class GUI extends JFrame implements ActionListener {

    public final boolean closeGUI = false;
    private final JButton leoButton;
    private final JButton meoButton;
    private final JButton geoButton;
    private final JButton loadButton;
    private final JButton showButton;
    private final JTextField aField;
    private final JTextField eField;
    private final JTextField iField;
    private final JTextField paField;
    private final JTextField raanField;
    private final JTextField anmField;
    private final JTextField dtField;

    public GUI() {
        // Set up the window
        setTitle("Track my Sat");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create the buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
        leoButton = new JButton("LEO");
        leoButton.addActionListener(this);
        buttonPanel.add(leoButton);

        meoButton = new JButton("MEO");
        meoButton.addActionListener(this);
        buttonPanel.add(meoButton);

        geoButton = new JButton("GEO");
        geoButton.addActionListener(this);
        buttonPanel.add(geoButton);

        add(buttonPanel, BorderLayout.NORTH);

        // Create the text boxes
        JPanel inputPanel = new JPanel(new GridLayout(7, 2));
        inputPanel.add(new JLabel("Semi-Major Axis (km):"));
        aField = new JTextField("7200");
        inputPanel.add(aField);

        inputPanel.add(new JLabel("Eccentricity:"));
        eField = new JTextField("0.01");
        inputPanel.add(eField);

        inputPanel.add(new JLabel("Inclination (deg):"));
        iField = new JTextField("98");
        inputPanel.add(iField);

        inputPanel.add(new JLabel("Argument of Perigee (deg):"));
        paField = new JTextField("0");
        inputPanel.add(paField);

        inputPanel.add(new JLabel("Right Ascension of Ascending Node (deg):"));
        raanField = new JTextField("0");
        inputPanel.add(raanField);

        inputPanel.add(new JLabel("Mean Anomaly (deg):"));
        anmField = new JTextField("0");
        inputPanel.add(anmField);

        inputPanel.add(new JLabel("Propagation time (min):"));
        dtField = new JTextField("20");
        inputPanel.add(dtField);

        add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel2 = new JPanel(new GridLayout(1, 2));

        // Create the "Load from Internet" button
        loadButton = new JButton("Load from Internet");
        loadButton.addActionListener(this);
        buttonPanel2.add(loadButton);

        // Save the data from the orbit
        showButton = new JButton("Show");
        showButton.addActionListener(this);
        buttonPanel2.add(showButton);

        add(buttonPanel2, BorderLayout.SOUTH);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == leoButton) {
            // when LEO button is clicked, it gets the parameters for a LEO orbit and set them on the text boxes
            KeplerianOrbit myOrbit = fillParameters("LEO");
            setTextFieldValues(myOrbit);
        } else if (e.getSource() == meoButton) {
            // when MEO button is clicked, it gets the parameters for a MEO orbit and set them on the text boxes
            KeplerianOrbit myOrbit = fillParameters("MEO");
            setTextFieldValues(myOrbit);
        } else if (e.getSource() == geoButton) {
            // when GEO button is clicked, it gets the parameters for a GEO orbit and set them on the text boxes
            KeplerianOrbit myOrbit = fillParameters("GEO");
            setTextFieldValues(myOrbit);
        }
        if (e.getSource() == showButton) {
            // When the show button is clicked, the orbit is recovered from the text boxes and the visual
            // environment is opened to show the orbit corresponding to the given parameters.
            KeplerianOrbit myOrbit = createOrbit();
            try {
                myPath.AppFrame.plotOrbit(myOrbit);
            } catch (PatriusException | IOException | URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
            if (this.closeGUI){dispose();}

        }

        if (e.getSource() == loadButton) {
            // When the load button is clicked, we should download data from the internet
            // and put it inside the text boxes
            KeplerianOrbit myOrbit = fillParameters("GEO");
            setTextFieldValues(myOrbit);
        }

    }

    private KeplerianOrbit fillParameters(String message) {
        double a = 0.0, e = 0.0, i = 0.0, pa = 0.0, raan = 0.0, anm = 0.0, dt=0.0;
        switch(message) {
            case "LEO":
                a = 7200.e3;
                e = 0.01;
                i = FastMath.toRadians(98.);
                pa = FastMath.toRadians(0.);
                raan = FastMath.toRadians(0.);
                anm = FastMath.toRadians(0.);
                dt = 20;
                break;
            case "MEO":
                a = 20000.e3;
                e = 0.05;
                i = FastMath.toRadians(45.);
                pa = FastMath.toRadians(0.);
                raan = FastMath.toRadians(0.);
                anm = FastMath.toRadians(0.);
                dt = 120;
                break;
            case "GEO":
                a = 42000.e3;
                e = 0.0;
                i = FastMath.toRadians(0.);
                pa = FastMath.toRadians(0.);
                raan = FastMath.toRadians(0.);
                anm = FastMath.toRadians(0.);
                dt = 300;
                break;
        }
        return new KeplerianOrbit(a, e, i, pa, raan, anm, dt);
    }

    private KeplerianOrbit createOrbit() {
        // Get the input values from the text fields
        double a = Double.parseDouble(aField.getText())*1000; // Conversion to meters
        double e = Double.parseDouble(eField.getText());
        double i = FastMath.toRadians(Double.parseDouble(iField.getText()));
        double pa = Double.parseDouble(paField.getText());
        double raan = Double.parseDouble(raanField.getText());
        double anm = Double.parseDouble(anmField.getText());
        double dt = Double.parseDouble(dtField.getText())*60; // Value in seconds for computation

        // Create the KeplerianOrbit object
        return new KeplerianOrbit(a, e, i, pa, raan, anm, dt);
    }
    private void setTextFieldValues(KeplerianOrbit orbit) {
        aField.setText(Double.toString(orbit.getA()/1000));
        eField.setText(Double.toString(orbit.getE()));
        iField.setText(Double.toString(FastMath.toDegrees(orbit.getI())));
        paField.setText(Double.toString(orbit.getPerigeeArgument()));
        raanField.setText(Double.toString(orbit.getRightAscensionOfAscendingNode()));
        anmField.setText(Double.toString(orbit.getAnomaly()));
        dtField.setText(Double.toString(orbit.getDt()));
    }

    public static void main(String[] Args) throws PatriusException, IOException, URISyntaxException {
        new GUI();
    }
}
