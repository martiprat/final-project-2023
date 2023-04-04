package fr.isae.mae.ss.y2021.orbitdisplayer;

import fr.cnes.sirius.patrius.bodies.GeodeticPoint;
import fr.cnes.sirius.patrius.utils.exception.PatriusException;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.exception.WWAbsentRequirementException;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.Path;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.markers.BasicMarker;
import gov.nasa.worldwind.render.markers.BasicMarkerAttributes;
import gov.nasa.worldwind.render.markers.Marker;
import gov.nasa.worldwind.util.BasicDragger;
import gov.nasa.worldwind.util.StatisticsPanel;
import gov.nasa.worldwind.util.WWUtil;
import gov.nasa.worldwindx.examples.ApplicationTemplate;
import gov.nasa.worldwindx.examples.FlatWorldPanel;
import gov.nasa.worldwindx.examples.LayerPanel;
import gov.nasa.worldwindx.examples.Paths;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class myPath extends ApplicationTemplate {

    public static class ExtendedAppPanel extends AppPanel {

        public ExtendedAppPanel(Dimension canvasSize, boolean includeStatusBar) {
            super(canvasSize, includeStatusBar);

            

        }

    }

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

        protected ExtendedAppPanel myCreateAppPanel(Dimension canvasSize, boolean includeStatusBar) {
            return new ExtendedAppPanel(canvasSize, includeStatusBar);
        }

        protected void initialize(boolean includeStatusBar, boolean includeLayerPanel, boolean includeStatsPanel) {
            // Create the WorldWindow.
            this.wwjPanel = this.myCreateAppPanel(this.getCanvasSize(), includeStatusBar);
            this.wwjPanel.setPreferredSize(this.getCanvasSize());

            // Put the pieces together.
            this.getContentPane().add(wwjPanel, BorderLayout.CENTER);
            if (includeLayerPanel) {
                this.controlPanel = new JPanel(new BorderLayout(10, 10));
                this.layerPanel = new LayerPanel(this.getWwd());
                this.controlPanel.add(this.layerPanel, BorderLayout.CENTER);
                this.controlPanel.add(new FlatWorldPanel(this.getWwd()), BorderLayout.NORTH);
                this.getContentPane().add(this.controlPanel, BorderLayout.WEST);
            }

            if (includeStatsPanel || System.getProperty("gov.nasa.worldwind.showStatistics") != null) {
                this.statsPanel = new StatisticsPanel(this.wwjPanel.getWwd(), new Dimension(250, this.getCanvasSize().height));
                this.getContentPane().add(this.statsPanel, BorderLayout.EAST);
            }

            // Create and install the view controls layer and register a controller for it with the WorldWindow.
            ViewControlsLayer viewControlsLayer = new ViewControlsLayer();
            insertBeforeCompass(getWwd(), viewControlsLayer);
            this.getWwd().addSelectListener(new ViewControlsSelectListener(this.getWwd(), viewControlsLayer));

            // Register a rendering exception listener that's notified when exceptions occur during rendering.
            this.wwjPanel.getWwd().addRenderingExceptionListener((Throwable t) -> {
                if (t instanceof WWAbsentRequirementException) {
                    String message = "Computer does not meet minimum graphics requirements.\n";
                    message += "Please install up-to-date graphics driver and try again.\n";
                    message += "Reason: " + t.getMessage() + "\n";
                    message += "This program will end when you press OK.";

                    JOptionPane.showMessageDialog(this, message, "Unable to Start Program",
                            JOptionPane.ERROR_MESSAGE);
                    System.exit(-1);
                }
            });

            // Search the layer list for layers that are also select listeners and register them with the World
            // Window. This enables interactive layers to be included without specific knowledge of them here.
            for (Layer layer : this.wwjPanel.getWwd().getModel().getLayers()) {
                if (layer instanceof SelectListener) {
                    this.getWwd().addSelectListener((SelectListener) layer);
                }
            }

            this.pack();

            // Center the application on the screen.
            WWUtil.alignComponent(null, this, AVKey.CENTER);
            this.setResizable(true);
        }

        public AppFrame() {
            initialize(true, true, false);

            showtime();
        }

        public static void main(String[] args) throws PatriusException, IOException, URISyntaxException {
            listOfStates =  TestingPropagation.main(null);

            ApplicationTemplate.start("WorldWind Paths", myPath.AppFrame.class);

        }
    }
}
