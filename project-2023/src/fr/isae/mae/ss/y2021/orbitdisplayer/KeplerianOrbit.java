package fr.isae.mae.ss.y2021.orbitdisplayer;

public class KeplerianOrbit {
    public double a;
    public double e;
    public double i;
    public double pa;
    public double raan;
    public double ta;
    public KeplerianOrbit(double semiMajorAxis, double eccentricity, double inclination, double perigeeArgument,
                            double rAAN, double trueAnomaly) {

        this.a = semiMajorAxis;
        this.e = eccentricity;
        this.i = inclination;
        this.pa = perigeeArgument;
        this.raan = rAAN;
        this.ta = trueAnomaly;
    }
}
