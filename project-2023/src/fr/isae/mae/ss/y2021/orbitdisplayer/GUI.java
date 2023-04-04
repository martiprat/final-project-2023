package fr.isae.mae.ss.y2021.orbitdisplayer;

import fr.cnes.sirius.patrius.math.util.FastMath;
import fr.cnes.sirius.patrius.utils.exception.PatriusException;
import gov.nasa.worldwindx.examples.ApplicationTemplate;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.*;


public class GUI extends JFrame implements ActionListener {

    private JButton leoButton, meoButton, geoButton, loadButton, saveButton;
    private JTextField aField, eField, iField,
            paField, raanField, anmField;

    public GUI() {
        // Set up the window
        setTitle("Keplerian Orbit");
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
        JPanel inputPanel = new JPanel(new GridLayout(6, 2));
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

        add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel2 = new JPanel(new GridLayout(1, 2));

        // Create the "Load from Internet" button
        loadButton = new JButton("Load from Internet");
        loadButton.addActionListener(this);
        buttonPanel2.add(loadButton);

        // Save the data from the orbit
        saveButton = new JButton("Save");
        saveButton.addActionListener(this);
        buttonPanel2.add(saveButton);

        add(buttonPanel2, BorderLayout.SOUTH);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == leoButton) {
            // Do something when button1 is clicked
            KeplerianOrbit myOrbit = fillParameters("LEO");
            setTextFieldValues(myOrbit);
        } else if (e.getSource() == meoButton) {
            // Do something when button2 is clicked
            KeplerianOrbit myOrbit = fillParameters("MEO");
            setTextFieldValues(myOrbit);
        } else if (e.getSource() == geoButton) {
            // Do something when button3 is clicked
            KeplerianOrbit myOrbit = fillParameters("GEO");
            setTextFieldValues(myOrbit);
        }
        if (e.getSource() == saveButton) {
            // Do something when button2 is clicked
            KeplerianOrbit myOrbit = getOrbit();
            try {
                myPath.AppFrame.plotOrbit(myOrbit);
            } catch (PatriusException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private KeplerianOrbit fillParameters(String message) {
        double a = 0.0, e = 0.0, i = 0.0, pa = 0.0, raan = 0.0, anm = 0.0;
        switch(message) {
            case "LEO":
                a = 7200.e3;
                e = 0.01;
                i = FastMath.toRadians(98.);
                pa = FastMath.toRadians(0.);
                raan = FastMath.toRadians(0.);
                anm = FastMath.toRadians(0.);
                break;
            case "MEO":
                a = 20000.e3;
                e = 0.05;
                i = FastMath.toRadians(45.);
                pa = FastMath.toRadians(0.);
                raan = FastMath.toRadians(0.);
                anm = FastMath.toRadians(0.);
                break;
            case "GEO":
                a = 42000.e3;
                e = 0.0;
                i = FastMath.toRadians(0.);
                pa = FastMath.toRadians(0.);
                raan = FastMath.toRadians(0.);
                anm = FastMath.toRadians(0.);
                break;
        }
        return new KeplerianOrbit(a, e, i, pa, raan, anm);
    }

    private KeplerianOrbit getOrbit() {
        // Get the input values from the text fields
        double a = Double.parseDouble(aField.getText())*1000;
        double e = Double.parseDouble(eField.getText());
        double i = FastMath.toRadians(Double.parseDouble(iField.getText()));
        double pa = Double.parseDouble(paField.getText());
        double raan = Double.parseDouble(raanField.getText());
        double anm = Double.parseDouble(anmField.getText());

        // Create the KeplerianOrbit object
        return new KeplerianOrbit(a, e, i, pa, raan, anm);
    }
    private void setTextFieldValues(KeplerianOrbit orbit) {
        aField.setText(Double.toString(orbit.getA()/1000));
        eField.setText(Double.toString(orbit.getE()));
        iField.setText(Double.toString(FastMath.toDegrees(orbit.getI())));
        paField.setText(Double.toString(orbit.getPerigeeArgument()));
        raanField.setText(Double.toString(orbit.getRightAscensionOfAscendingNode()));
        anmField.setText(Double.toString(orbit.getAnomaly()));
    }
    public static KeplerianOrbit openGUI() {
        GUI gui = new GUI();
        KeplerianOrbit orbit = gui.getOrbit();
        return orbit;
    }

    public static void main(String[] Args) throws PatriusException, IOException, URISyntaxException {
        GUI gui = new GUI();
    }
}
