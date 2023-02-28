package fr.isae.mae.ss.y2021.orbitdisplayer;

import fr.cnes.sirius.patrius.math.util.FastMath;

public class GUI {
    public static KeplerianOrbit main(String[] args) {
        final double a = 7200.e+3;
        final double e = 0.01;
        final double i = FastMath.toRadians(98.);
        final double pa = FastMath.toRadians(0.);
        final double raan = FastMath.toRadians(0.);
        final double anm = FastMath.toRadians(0.);

        return new KeplerianOrbit(a, e, i, pa, raan, anm);
    }
}
