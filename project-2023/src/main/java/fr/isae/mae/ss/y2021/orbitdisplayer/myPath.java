package fr.isae.mae.ss.y2021.orbitdisplayer;

import fr.cnes.sirius.patrius.bodies.GeodeticPoint;
import fr.cnes.sirius.patrius.math.util.FastMath;
import fr.cnes.sirius.patrius.utils.exception.PatriusException;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.Path;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.util.BasicDragger;
import gov.nasa.worldwindx.examples.ApplicationTemplate;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class myPath extends ApplicationTemplate {
    public static class AppFrame extends ApplicationTemplate.AppFrame {

        private static ArrayList<GeodeticPoint> listOfStates;

        private void setZoom(GeodeticPoint state, View view){
            double lat = FastMath.toDegrees(state.getLatitude());
            double lon = FastMath.toDegrees(state.getLongitude());
            double alt = state.getAltitude();
            double zoomAlt = 2 * alt + 10000000;
            view.setEyePosition(Position.fromDegrees (lat, lon, zoomAlt));
        }

        /**

         Displays a path with specified attributes and sets required zoom.
         */
        public void displayPathWithMarkersAndZoom() {

            // Add a dragger to enable shape dragging
            getWwd().addSelectListener(new BasicDragger(getWwd()));

            // Create a new renderable layer
            RenderableLayer layer = new RenderableLayer();

            // Create and set an attribute bundle.
            // Set the outline material to yellow and outline width to 2d.
            ShapeAttributes attrs = new BasicShapeAttributes();
            attrs.setOutlineMaterial(new Material(Color.YELLOW));
            attrs.setOutlineWidth(2d);

            // Create a path using a list of positions converted from geo coordinates
            ArrayList<Position> pathPositions = geo2pos(listOfStates);
            Path path = new Path(pathPositions);

            // Set the path's attributes
            path.setAttributes(attrs);
            path.setVisible(true);
            path.setDragEnabled(false);
            path.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
            path.setPathType(AVKey.GREAT_CIRCLE);

            // Add the path to the renderable layer
            layer.addRenderable(path);

            // Insert the renderable layer before the compass in the WorldWind window
            insertBeforeCompass(getWwd(), layer);

            // Set the zoom
            int mid_length = (int)listOfStates.size()/2;
            this.setZoom(listOfStates.get(mid_length), getWwd().getView());
        }

        private ArrayList<Position> geo2pos(ArrayList<GeodeticPoint> listOfStates) {
            ArrayList<Position> pathPositions = new ArrayList<>();

            for (GeodeticPoint sc : listOfStates) {
                pathPositions.add(Position.fromRadians(sc.getLatitude(), sc.getLongitude(), sc.getAltitude()));
            }

            return pathPositions;
        }

        public AppFrame() {
            super(true, true, false);

            displayPathWithMarkersAndZoom();
        }

        public static void plotOrbit(KeplerianOrbit myOrbit) throws PatriusException, IOException, URISyntaxException {

            listOfStates =  TestingPropagation.main(myOrbit);
            ApplicationTemplate.start("WorldWind Paths", myPath.AppFrame.class);

        }
    }
}
