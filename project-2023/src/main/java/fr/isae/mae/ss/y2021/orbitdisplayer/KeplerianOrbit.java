package fr.isae.mae.ss.y2021.orbitdisplayer;

public class KeplerianOrbit {
    private double a;
    private double e;
    private double i;
    private double pa;
    private double raan;
    private double ta;
    private double dt;

    public KeplerianOrbit(double semiMajorAxis, double eccentricity, double inclination, double perigeeArgument,
                            double rAAN, double trueAnomaly, double dt) {

        this.a = semiMajorAxis;
        this.e = eccentricity;
        this.i = inclination;
        this.pa = perigeeArgument;
        this.raan = rAAN;
        this.ta = trueAnomaly;
        this.dt = dt;
    }

    public double getA() {
        return a;
    }
    public double getE() {
        return e;
    }
    public double getI() {
        return i;
    }

    public double getPerigeeArgument() {
        return pa;
    }

    public double getRightAscensionOfAscendingNode() {
        return raan;
    }

    public double getAnomaly() {
        return ta;
    }

    public double getDt() {
        return dt;
    }
}
