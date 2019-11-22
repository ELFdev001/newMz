/**
 * 
 */
package mwgrid.environment;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import mwgrid.middleware.distributedobject.Location;
import mwgrid.middleware.distributedobject.Value;
import mwgrid.middleware.distributedobject.Variable;


/**
 * @author Dr B.G.W. Craenen <b.g.w.craenen@cs.bham.ac.uk>
 */
public final class PartEnvHeightOnlyImplementation implements
        Environment {
    private static final Logger LOG =
            Logger.getLogger(PartEnvHeightOnlyImplementation.class
                    .getPackage().getName());
    private static final ExpandedSingletonInitFile INITFILE = ExpandedSingletonInitFile.getInstance();
    private static final String ZIPPED_HEIGHT_FILE_NAME = "gdemMED001.zip";
    private static final int NUMBER_OF_COLUMNS = 28070;
    private static final int NUMBER_OF_ROWS = 8889;
    private static final Location LOWER_LEFT_LOCATION = new Location(0, 0);
    private static final Location UPPER_RIGHT_LOCATION =
            new Location(NUMBER_OF_COLUMNS, NUMBER_OF_ROWS);
    private static final Map<Variable, Slice> ENVIRONMENT_MAP =
            new HashMap<Variable, Slice>();
    // WARNING: This instantiation always has to happen last!
    private static final Environment INSTANCE =
        new PartEnvHeightOnlyImplementation();
    
    
    /**
     * Constructor
     */
    private PartEnvHeightOnlyImplementation() {
        LOG.finest("Constructor");
        final String resLoc = INITFILE.getResourceLoc();
        ENVIRONMENT_MAP.put(EnvironmentVariables.HEIGHT,
            new PartitionedSliceArray(resLoc + ZIPPED_HEIGHT_FILE_NAME,
                    LOWER_LEFT_LOCATION, UPPER_RIGHT_LOCATION));
    }
    
    /**
     * @return (Environment) instance of environment
     */
    public static Environment getInstance() {
        return INSTANCE;
    }
    

    /**
     * @see mwgrid.environment.Environment#getEnvironmentValue(mwgrid.middleware.distributedobject.Location,
     *      mwgrid.middleware.distributedobject.Variable)
     * @param pLocation
     *            - location
     * @param pVariable
     *            - variable
     * @return (Value<?>) value
     */
    @Override
    public Value<?> getEnvironmentValue(final Location pLocation,
            final Variable pVariable) {
    	if (!INITFILE.getFlatTerrain()) {
            if (!ENVIRONMENT_MAP.containsKey(pVariable)) { return null; }
            final Slice slice = ENVIRONMENT_MAP.get(pVariable);
            //As overall environment is 10x bigger than height map, divide vals by 10 to access correct cell
            final Location expandedLoc = new Location(pLocation.getX() / 10, pLocation.getY() / 10);
            LOG.finest("Location given = " + pLocation + ". Cell retrived = " + expandedLoc);
            return slice.get(expandedLoc);
    	} else {
    		return new Value<Integer>(1);
    	}
    }
    
    /**
     * @see mwgrid.environment.Environment#inEnvironment(mwgrid.middleware.distributedobject.Location)
     * @param pLocation
     *            - location
     * @return (boolean) is in environment?
     */
    @Override
    public boolean inEnvironment(final Location pLocation) {
        return pLocation.in(LOWER_LEFT_LOCATION, UPPER_RIGHT_LOCATION);
    }
}
