package fr.isae.mae.ss.y2021.orbitdisplayer;

import fr.cnes.sirius.patrius.bodies.GeodeticPoint;
import fr.cnes.sirius.patrius.math.util.FastMath;
import fr.cnes.sirius.patrius.orbits.Orbit;
import fr.cnes.sirius.patrius.utils.exception.PatriusException;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.MarkerLayer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.Path;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.markers.BasicMarker;
import gov.nasa.worldwind.render.markers.BasicMarkerAttributes;
import gov.nasa.worldwind.render.markers.Marker;
import gov.nasa.worldwind.util.BasicDragger;
import gov.nasa.worldwindx.examples.ApplicationTemplate;
import gov.nasa.worldwindx.examples.Paths;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class myPath extends ApplicationTemplate {
    public static class AppFrame extends ApplicationTemplate.AppFrame {

        private static ArrayList<GeodeticPoint> listOfStates;

        /**

         Displays a path with specified attributes and enables shape dragging.

         Adds a marker layer with a single marker at position (90, 0).
         */
        public void showtime() {

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

            // Create a list of markers and add a single marker at position (90, 0)
            List<Marker> markers = new ArrayList<>(1);
            markers.add(new BasicMarker(Position.fromDegrees(90, 0), new BasicMarkerAttributes()));

            // Create a marker layer and set its markers to the list of markers
            MarkerLayer markerLayer = new MarkerLayer();
            markerLayer.setMarkers(markers);

            // Insert the marker layer and the renderable layer before the compass in the WorldWind window
            insertBeforeCompass(getWwd(), layer);
            insertBeforeCompass(getWwd(), markerLayer);
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

            showtime();
        }

        public static void plotOrbit(KeplerianOrbit myOrbit) throws PatriusException, IOException, URISyntaxException {

            listOfStates =  TestingPropagation.main(myOrbit);
            ApplicationTemplate.start("WorldWind Paths", myPath.AppFrame.class);

        }
    }
}
