package mwgrid.manzikert;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

import mwgrid.environment.Environment;
import mwgrid.environment.EnvironmentVariables;
import mwgrid.environment.ExpandedSingletonInitFile;
import mwgrid.environment.PartEnvHeightOnlyImplementation;
import mwgrid.middleware.distributedobject.Location;
import mwgrid.middleware.distributedobject.Value;


/**
 * @author Phil
 */
public final class TimeConversionTest {

    private static final Logger LOG =
        Logger.getLogger(TimeConversionTest.class.getPackage().getName());
    private static final int TICK = 9388;
    private static final int STARTHOUR = 6;
    private static final double STARTMINUTE = 11;
    private static final double TICKLENGTH = 3.729;
    private static final int SECSINMINUTE = 60;
    private static final int MINSINHOUR = 60;
    private int secsinhour = SECSINMINUTE * MINSINHOUR;
    private static final Location pFirstLoc = new Location(34600,27200);
    private static final Location pSecLoc = new Location(30000,34500);
//	public static ExpandedSingletonInitFile initFile = null;

    


    /**
     * Constructor
     * @throws IOException 
     * @throws NumberFormatException 
     */
    public TimeConversionTest(String pinit) {
/*    	double secsfromstartofday = (TICK * TICKLENGTH) + (STARTMINUTE * SECSINMINUTE) + (STARTHOUR * secsinhour);
    	int hourofday = (int) (secsfromstartofday / secsinhour);
    	int minsofhour = (int) ((secsfromstartofday / SECSINMINUTE) % MINSINHOUR);
    	int secsinmin = (int) (secsfromstartofday  % MINSINHOUR);
    	System.out.println("Tick is " + TICK + " so time is " + hourofday + ":" + minsofhour + ":" + secsinmin);
		initFile = ExpandedSingletonInitFile.getInstance(pinit);

	    final Environment ENVIRONMENT =
	            PartEnvHeightOnlyImplementation.getInstance();

        final Value<?> pStartHeightVal =
                ENVIRONMENT.getEnvironmentValue(pFirstLoc,
                    EnvironmentVariables.HEIGHT);
        final Value<?> pDestHeightVal =
                ENVIRONMENT.getEnvironmentValue(pSecLoc,
                    EnvironmentVariables.HEIGHT);
        final Integer pStartInteger = (Integer) pStartHeightVal.get();
        final Integer pDestInteger = (Integer) pDestHeightVal.get();*/
        
        double dist = pFirstLoc.distanceTo(pSecLoc);
        dist = (dist * 5) / 1000;
        System.out.println("dist " + dist);
        
//        System.out.println("first= " + pStartInteger + " abd 2nd=" + pDestInteger);

    }
    
    public static void main(String[] args) {
    	TimeConversionTest thistest = new TimeConversionTest(args[0]);
    }
}

