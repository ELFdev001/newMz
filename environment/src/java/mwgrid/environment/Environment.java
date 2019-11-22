/**
 * 
 */
package mwgrid.environment;

import mwgrid.middleware.distributedobject.Location;
import mwgrid.middleware.distributedobject.Value;
import mwgrid.middleware.distributedobject.Variable;

/**
 * @author Dr B.G.W. Craenen (b.g.w.craenen@cs.bham.ac.uk)
 */
public interface Environment {
    /**
     * @param pLocation
     *            - location
     * @param pVariable
     *            - variable
     * @return (Value<?>) value
     */
    Value<?> getEnvironmentValue(Location pLocation, Variable pVariable);

    /**
     * @param pLocation
     *            - location
     * @return (boolean) is location in location?
     */
    boolean inEnvironment(Location pLocation);
}
