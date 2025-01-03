/**
 * 
 */
package mwgrid.environment;

import mwgrid.middleware.distributedobject.KernelVariables;
import mwgrid.middleware.distributedobject.PublicVariable;
import mwgrid.middleware.distributedobject.Variable;

/**
 * <p>
 * Notes:
 * <p>
 * The HEIGHT variable is only an example. Different environment slices will
 * probably have different variables assigned.
 * <p>
 * The variable names can be arbitrarily chosen and can be a multi-set, through
 * the use of explicit referencing. This can be confusing though and it is
 * preferable to make variable names exclusive. Exclusive names within each
 * variables enum is mandatory.
 * <p>
 * The variable ID is derived from KernelVariables. With more enums defining
 * variables an order, or chain of references will need to be established, each
 * building their variable IDs (ordinals) from the previous variable enum in the
 * chain.
 * 
 * @author Dr B.G.W. Craenen (b.g.w.craenen@cs.bham.ac.uk)
 * @author Phil Murgatroyd
 */
public enum EnvironmentVariables implements Variable {
    @PublicVariable
    HEIGHT(Integer.class), @PublicVariable
    ROAD(Integer.class), @PublicVariable
    WALLARCH(Integer.class), @PublicVariable
    WALLBISH(Integer.class), @PublicVariable
    WALLNO(Integer.class), @PublicVariable
    CITARCH(Integer.class), @PublicVariable
    CITBISH(Integer.class), @PublicVariable
    CITNO(Integer.class), @PublicVariable
    UNFARCH(Integer.class), @PublicVariable
    UNFBISH(Integer.class), @PublicVariable
    UNFNO(Integer.class), @PublicVariable
    ROAD_CORRIDOR(Integer.class);
    private final Class<?> fType;
    
    /**
     * @param pType
     *            - type
     */
    private EnvironmentVariables(final Class<?> pType) {
        this.fType = pType;
    }
    
    /**
     * @see mwgrid.middleware.distributedobject.Variable#getType()
     * @return (Class<?>) class
     */
    @Override
    public Class<?> getType() {
        return this.fType;
    }
    
    @Override
    public String getName() {
        return this.name();
    }
    
    /**
     * @see mwgrid.middleware.distributedobject.Variable#getVariableId()
     * @return (int) variable ID
     */
    @Override
    public int getVariableId() {
        return KernelVariables.values()[KernelVariables.values().length - 1]
                .getVariableId()
                + 1 + this.ordinal();
    }
}
