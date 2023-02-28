package fr.isae.mae.ss.y2021.orbitdisplayer;

import fr.cnes.sirius.patrius.bodies.GeodeticPoint;
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

        public void showtime() {

            // Add a dragger to enable shape dragging
            getWwd().addSelectListener(new BasicDragger(getWwd()));

            RenderableLayer layer = new RenderableLayer();

            // Create and set an attribute bundle.
            ShapeAttributes attrs = new BasicShapeAttributes();
            attrs.setOutlineMaterial(new Material(Color.YELLOW));
            attrs.setOutlineWidth(2d);

            // Create a path, set some of its properties and set its attributes.
            ArrayList<Position> pathPositions = geo2pos(listOfStates);
            Path path = new Path(pathPositions);
            path.setAttributes(attrs);
            path.setVisible(true);
            path.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
            path.setPathType(AVKey.GREAT_CIRCLE);
            layer.addRenderable(path);


            // Add the layer to the model.
            insertBeforeCompass(getWwd(), layer);

            List<Marker> markers = new ArrayList<>(1);
            markers.add(new BasicMarker(Position.fromDegrees(90, 0), new BasicMarkerAttributes()));
            MarkerLayer markerLayer = new MarkerLayer();
            markerLayer.setMarkers(markers);
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

        public static void main(String[] args) throws PatriusException, IOException, URISyntaxException {
            listOfStates =  TestingPropagation.main(null);

            ApplicationTemplate.start("WorldWind Paths", myPath.AppFrame.class);

        }
    }
}
