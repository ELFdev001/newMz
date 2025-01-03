/**
 * 
 */
package mwgrid.environment;

import mwgrid.middleware.distributedobject.Location;
import mwgrid.middleware.distributedobject.Value;

/**
 * @author craenbgw
 */
public interface Slice {
    /**
     * @param pLocation - location
     * @return (Value<?>) value
     */
    Value<?> get(final Location pLocation);
}
