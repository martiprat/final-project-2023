package fr.isae.mae.ss.y2021.orbitdisplayer;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;


public class CustomOrbitView extends BasicOrbitView {
    public CustomOrbitView() {
        super();
    }

    @Override
    public double computeFarDistance(Position dc) {
        // Multiply the current value by 2 to make it further out

        return super.computeFarDistance(dc) * 2.0;
    }
}
