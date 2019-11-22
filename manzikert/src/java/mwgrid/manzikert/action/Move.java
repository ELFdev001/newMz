package mwgrid.manzikert.action;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import mwgrid.environment.Environment;
import mwgrid.environment.EnvironmentVariables;
import mwgrid.environment.ExpandedSingletonInitFile;
import mwgrid.environment.PartEnvHeightOnlyImplementation;
import mwgrid.manzikert.ContextSingleton;
import mwgrid.manzikert.agent.MWGridAgent;
import mwgrid.middleware.distributedobject.Location;
import mwgrid.middleware.distributedobject.Value;

public class Move extends Action {
    public enum Cost {
        LEVEL(ExpandedSingletonInitFile.getCostLevel()),
        UP_SHALLOW(ExpandedSingletonInitFile.getCostUShall()),
        UP_MED(ExpandedSingletonInitFile.getCostUMed()),
        UP_STEEP(ExpandedSingletonInitFile.getCostUSteep()),
        DOWN_SHALLOW(ExpandedSingletonInitFile.getCostDShall()),
        DOWN_MED(ExpandedSingletonInitFile.getCostDMed()),
        DOWN_STEEP(ExpandedSingletonInitFile.getCostDSteep()),
        ERROR(Double.MAX_VALUE);
        private final double fCost;
        
        private Cost(final double pCost) {
            this.fCost = pCost;
        }
        
        public double get() {
            return fCost;
        }
    }
    
    private static final Logger LOG =
            Logger.getLogger(Move.class.getPackage().getName());
    private static final Environment ENVIRONMENT =
            PartEnvHeightOnlyImplementation.getInstance();
    public final Location fLocation;
    
    public Move(final Location pLocation) {
        this.fLocation = pLocation;
    }
    
    protected Move(final Move pMove) {
        this.fLocation = pMove.fLocation;
    }
    
    public Move copy() {
        return new Move(this);
    }
    
    @Override
    public boolean equals(final Object pObject) {
        final Move other = (Move) pObject;
        return this.fLocation.equals(other);
    }
    
    // A function that returns the category of movement involved in moving
    // between 2 locations
    // Movement categories as specified in Cost
    public static Cost getZDir(final Location pStartLoc, final Location pDestLoc) {
        Cost returnInt = Cost.ERROR;
        final Value<?> pStartHeightVal =
                ENVIRONMENT.getEnvironmentValue(pStartLoc,
                    EnvironmentVariables.HEIGHT);
        final Value<?> pDestHeightVal =
                ENVIRONMENT.getEnvironmentValue(pDestLoc,
                    EnvironmentVariables.HEIGHT);
        final Integer pStartInteger = (Integer) pStartHeightVal.get();
        final Integer pDestInteger = (Integer) pDestHeightVal.get();
        final Integer heightdiff = Math.abs(pStartInteger - pDestInteger);
        if (pStartInteger > (pDestInteger + ExpandedSingletonInitFile.getCutoffLevel())) {
            if (heightdiff > ExpandedSingletonInitFile.getCutoffMedSteep()) {
                returnInt = Cost.DOWN_STEEP;
                LOG.finest("down steep. cost is " + returnInt);
            } else if (heightdiff < ExpandedSingletonInitFile.getCutoffShallMed()) {
                returnInt = Cost.DOWN_SHALLOW;
                LOG.finest("down shallow. cost is " + returnInt);
            } else {
                returnInt = Cost.DOWN_MED;
                LOG.finest("down med. cost is " + returnInt);
            }
        } else if (pDestInteger > (pStartInteger + ExpandedSingletonInitFile.getCutoffLevel())) {
            if (heightdiff > ExpandedSingletonInitFile.getCutoffMedSteep()) {
                returnInt = Cost.UP_STEEP;
                LOG.finest("up steep. cost is " + returnInt);
            } else if (heightdiff < ExpandedSingletonInitFile.getCutoffShallMed()) {
                returnInt = Cost.UP_SHALLOW;
                LOG.finest("up shallow. cost is " + returnInt);
            } else {
                returnInt = Cost.UP_MED;
                LOG.finest("up med. cost is " + returnInt);
            }
        } else {
            returnInt = Cost.LEVEL;
            LOG.finest("start height = " + pStartInteger + " end height = " + pDestInteger + "level. cost is " + returnInt);
        }
        return returnInt;
    }
    
    @Override
    public double getCost() {
        return 1;
    }
    
    public List<Move> neighbours() {
        final List<Move> result = new ArrayList<Move>();
        for (final Location neighbour : this.fLocation.neighbours()) {
                result.add(new Move(neighbour));
        }
        return result;
    }
    
    @Override
    public boolean performAction(final MWGridAgent pAgent) {
    	Move.LOG.finest("Move from: " + pAgent.fLocation + " to: "
    			+ this.fLocation);
    	final Location agentLoc = pAgent.fLocation;
    	if (this.fLocation.equals(agentLoc)) {
    		LOG.info("Trying to move to current loc");
    		return true;
    	}
    	// Check to see if target location is a neighbour of current location
    	if (!this.fLocation.neighbours().contains(agentLoc)) {
    		LOG.info("Trying to move more than one cell from " + agentLoc + " to "
    				+ this.fLocation);
    		pAgent.fPlanStructure.collapseCurrentPlan();
    		return false;
    	}
    	// Target location is not empty, deal with movement blockage
    	if (!ContextSingleton.hasSpaceForMe(this.fLocation, pAgent.getSize())) {
    		LOG.finer("Path blocked!");
    		return false;
    	}
    	final double i3dm = get3DMoveDist(agentLoc);
    	pAgent.fDistTravelled = pAgent.fDistTravelled + i3dm;
    	pAgent.fMovePoints = pAgent.fMovePoints - i3dm;
    	pAgent.fLastLocation = pAgent.fLocation;
    	pAgent.fLocation = this.fLocation;
		pAgent.fLastTickMoved = ContextSingleton.getTime();
    	return true;
    }


    private double get3DMoveDist(final Location pAgentLoc) {
    	final double flatdisttrav = pAgentLoc.distanceTo(fLocation) * 5;
    	final Value<?> pStartHeightVal =
    		ENVIRONMENT.getEnvironmentValue(pAgentLoc,
    				EnvironmentVariables.HEIGHT);
    	final Value<?> pDestHeightVal =
    		ENVIRONMENT.getEnvironmentValue(this.fLocation,
    				EnvironmentVariables.HEIGHT);
    	final Integer pStartInteger = (Integer) pStartHeightVal.get();
    	final Integer pDestInteger = (Integer) pDestHeightVal.get();
    	final Integer heightdiff = Math.abs(pStartInteger - pDestInteger);
    	final double totaldist = Math.sqrt((Math.pow(flatdisttrav, 2)) + Math.pow(heightdiff, 2));
    	return totaldist;
    }
    
}
