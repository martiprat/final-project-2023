package fr.isae.mae.ss.y2021.orbitdisplayer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import fr.cnes.sirius.patrius.bodies.GeodeticPoint;
import fr.cnes.sirius.patrius.bodies.OneAxisEllipsoid;
import fr.cnes.sirius.patrius.frames.Frame;
import fr.cnes.sirius.patrius.frames.FramesFactory;
import fr.cnes.sirius.patrius.frames.transformations.Transform;
import fr.cnes.sirius.patrius.math.geometry.euclidean.threed.Vector3D;
import fr.cnes.sirius.patrius.math.ode.FirstOrderIntegrator;
import fr.cnes.sirius.patrius.math.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import fr.cnes.sirius.patrius.math.util.FastMath;
import fr.cnes.sirius.patrius.orbits.ApsisOrbit;
import fr.cnes.sirius.patrius.orbits.Orbit;
import fr.cnes.sirius.patrius.orbits.OrbitType;
import fr.cnes.sirius.patrius.orbits.PositionAngle;
import fr.cnes.sirius.patrius.orbits.orbitalparameters.ApsisRadiusParameters;
import fr.cnes.sirius.patrius.orbits.pvcoordinates.PVCoordinates;
import fr.cnes.sirius.patrius.propagation.SpacecraftState;
import fr.cnes.sirius.patrius.propagation.numerical.NumericalPropagator;
import fr.cnes.sirius.patrius.propagation.sampling.PatriusFixedStepHandler;
import fr.cnes.sirius.patrius.time.AbsoluteDate;
import fr.cnes.sirius.patrius.time.TimeScale;
import fr.cnes.sirius.patrius.time.TimeScalesFactory;
import fr.cnes.sirius.patrius.utils.Constants;
import fr.cnes.sirius.patrius.utils.exception.PatriusException;
import fr.cnes.sirius.patrius.utils.exception.PropagationException;

public class TestingPropagation {


    public static ArrayList<GeodeticPoint> main(KeplerianOrbit myOrbit) throws PatriusException, IOException, URISyntaxException {
		 
        // Patrius Dataset initialization (needed for example to get the UTC time)
        //PatriusDataset.addResourcesFromPatriusDataset() ;
 
        // Recovery of the UTC timescale using a "factory" (not to duplicate such unique object)
        final TimeScale TAI = TimeScalesFactory.getTAI();
 
        // Date of the orbit given in UTC timescale)
        final AbsoluteDate date = new AbsoluteDate("2010-01-01T12:00:00.000", TAI);
 
        // Getting the frame with which will define the orbit parameters
        // As for timescale, we will use also a "factory".
        final Frame GCRF = FramesFactory.getGCRF();
 
        // Initial orbit
        final double sma = myOrbit.getA();
        final double exc = myOrbit.getE();
        final double per = sma*(1.-exc);
        final double apo = sma*(1.+exc);
        final double inc = myOrbit.getI();
        final double pa = myOrbit.getPerigeeArgument();
        final double raan = myOrbit.getRightAscensionOfAscendingNode();
        final double anm = myOrbit.getAnomaly();
        final double dt = myOrbit.getDt();
        final double MU = Constants.WGS84_EARTH_MU;
 
        final ApsisRadiusParameters par = new ApsisRadiusParameters(per, apo, inc, pa, raan, anm, PositionAngle.MEAN, MU);
        final Orbit iniOrbit = new ApsisOrbit(par, GCRF, date);
 
        // We create a spacecraft state
        final SpacecraftState iniState = new SpacecraftState(iniOrbit);
 
        // Initialization of the Runge Kutta integrator with a 2 s step
        final double pasRk = 2.;
        final FirstOrderIntegrator integrator = new ClassicalRungeKuttaIntegrator(pasRk);
 
        // Initialization of the propagator
        final NumericalPropagator propagator = new NumericalPropagator(integrator);
        propagator.resetInitialState(iniState);
 
        // Forcing integration using cartesian equations
        propagator.setOrbitType(OrbitType.CARTESIAN);
 
//SPECIFIC
        OneAxisEllipsoid earth = new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
                Constants.WGS84_EARTH_FLATTENING, GCRF);

        
        // Creation of a fixed step handler
        final ArrayList<GeodeticPoint> listOfStates = new ArrayList<GeodeticPoint>();
        PatriusFixedStepHandler myStepHandler = new PatriusFixedStepHandler() {
            private static final long serialVersionUID = 1L;
            public void init(SpacecraftState s0, AbsoluteDate t) {
                // Nothing to do ...
            }
            public void handleStep(SpacecraftState currentState, boolean isLast)
                    throws PropagationException {
                // Adding S/C to the list
                
                
                // compute sub-satellite track
                AbsoluteDate  date    = currentState.getDate();
                PVCoordinates pvInert = currentState.getPVCoordinates();
                Transform t;
				try {
					t = currentState.getFrame().getTransformTo(earth.getBodyFrame(), date);
	                Vector3D      p       = t.transformPosition(pvInert.getPosition());
	                Vector3D      v       = t.transformVector(pvInert.getVelocity());
	                GeodeticPoint center  = earth.transform(p, earth.getBodyFrame(), date);
	                listOfStates.add(center);
				} catch (PatriusException e) {
					throw new PropagationException(e);
				}

            }
        };
        // The handler frequency is set to 10S
        propagator.setMasterMode(10., myStepHandler);
//SPECIFIC
 
        // Propagating
        final AbsoluteDate finalDate = date.shiftedBy(dt);
        final SpacecraftState finalState = propagator.propagate(finalDate);
 
        // Display data at each step
        System.out.println(iniState.getDate().toString(TAI)+" ; LV = "+FastMath.toDegrees(iniState.getLv())+ " deg");
        for (GeodeticPoint sc : listOfStates) {
            System.out.println("Alti: " + sc.getAltitude() + ", Lon: " + sc.getLongitude() + ", Lat: " + sc.getLatitude() );
        }
        System.out.println(finalState.getDate().toString(TAI)+" ; LV = "+FastMath.toDegrees(finalState.getLv())+ " deg");


        return listOfStates;
    }

}
