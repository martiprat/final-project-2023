package fr.isae.mae.ss.y2021.orbitdisplayer;

import fr.cnes.sirius.patrius.math.util.FastMath;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Objects;

public class GUI {

    public static KeplerianOrbit getInfoFromInternet(){

        // URL of website to scrape
        String url = "https://heavens-above.com/orbit.aspx?satid=27386";

        // Mean radius of the Earth in km
        final double rEarth = 6371;

        try {
            // Use Jsoup to connect to the website and extract the HTML document
            Document document = Jsoup.connect(url).get();

            // Extract data of interest from the document using unique IDs
            String eccentricityT = Objects.requireNonNull(document.getElementById("ctl00_cph1_lblEccentricity")).text();
            String inclinationT = Objects.requireNonNull(document.getElementById("ctl00_cph1_lblInclination")).text();
            String perigeeHT = Objects.requireNonNull(document.getElementById("ctl00_cph1_lblPerigee")).text();
            String apogeeHT = Objects.requireNonNull(document.getElementById("ctl00_cph1_lblApogee")).text();
            String RAANT = Objects.requireNonNull(document.getElementById("ctl00_cph1_lblNode")).text();
            String argPerigeeT = Objects.requireNonNull(document.getElementById("ctl00_cph1_lblArgP")).text();
            String MeanAnomT = Objects.requireNonNull(document.getElementById("ctl00_cph1_lblMA")).text();

            // Convert data of interest into numerical values
            final double e = Double.parseDouble(eccentricityT);
            final double inc = Double.parseDouble(inclinationT);
            final double perigeeH = Double.parseDouble(perigeeHT.split(" ")[0]);
            final double apogeeH = Double.parseDouble(apogeeHT.split(" ")[0]);

            // Calculate the semi-major axis of the orbit in meters
            final double a = 1e3 * (perigeeH + apogeeH + rEarth * 2) / 2;

            final double RAAN = Double.parseDouble(RAANT);
            final double argPerigee = Double.parseDouble(argPerigeeT);
            final double MeanAnom = Double.parseDouble(MeanAnomT);

            // Convert values to radians
            final double i = FastMath.toRadians(inc);
            final double pa = FastMath.toRadians(argPerigee);
            final double raan = FastMath.toRadians(RAAN);
            final double anm = FastMath.toRadians(MeanAnom);

            // Create a new KeplerianOrbit object using the parsed data
            return new KeplerianOrbit(a, e, i, pa, raan, anm);

        } catch (IOException e) {
            // Print the stack trace if an IOException occurs
            e.printStackTrace();
        }

        // Return null if the method fails to create a KeplerianOrbit object
        return null;
    }

    public static KeplerianOrbit main(String[] args) {

        // If information is taken from Internet
        KeplerianOrbit orbitFromInternet = getInfoFromInternet();
        return orbitFromInternet;

        // If the orbit is directly hardcoded
        /*final double a = 7200.e3;
        final double e = 0.01;
        final double i = FastMath.toRadians(98.);
        final double pa = FastMath.toRadians(0.);
        final double raan = FastMath.toRadians(0.);
        final double anm = FastMath.toRadians(0.);

        return new KeplerianOrbit(a, e, i, pa, raan, anm);*/
    }
}
