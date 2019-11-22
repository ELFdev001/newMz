package mwgrid.manzikert.action;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import mwgrid.environment.ExpandedSingletonInitFile;
import mwgrid.manzikert.ContextSingleton;
import mwgrid.manzikert.agent.MWGridAgent;
import mwgrid.manzikert.planning.Plan;
import mwgrid.middleware.distributedobject.Location;

public class TravelToSolo extends Action {
    
    private static final Logger LOG =
            Logger.getLogger(TravelToSolo.class.getPackage().getName());
    public final Location fLocation;
    
    /**
     * @param pLocation
     *            - location
     */
    public TravelToSolo(final Location pLocation) {
        this.fLocation = pLocation;
    }
    
    /**
     * @param pTravelTo
     *            - travel to
     */
    protected TravelToSolo(final TravelToSolo pTravelTo) {
        this.fLocation = pTravelTo.fLocation;
    }
    
    @Override
    public Action copy() {
        return new TravelToSolo(this);
    }
    
    @Override
    public double getCost() {
        return 0;
    }
    
    public double getCost(final TravelToSolo pAct) {
        double cost = 0;
        if (pAct.fLocation.equals(Location.NULL_LOCATION)) {
            return 0;
        } else {
            LOG.finest("Getting cost from " + this.fLocation + " to " + pAct.fLocation);
            
            final Location startLoc = pAct.fLocation;
            final Location destLoc = this.fLocation;
            cost = Move.getZDir(startLoc, destLoc).get();
            if (ExpandedSingletonInitFile.getDiagMod() > 0) {
                if (startLoc.distanceTo(destLoc) > 10) {
                    cost = cost + (cost * ExpandedSingletonInitFile.getDiagMod());
                } 
            } else if (startLoc.distanceTo(destLoc) > 10) {
                cost = Math.sqrt((cost * cost) + (ExpandedSingletonInitFile.getCostLevel() * ExpandedSingletonInitFile.getCostLevel()));
            }
        }
        return cost;
    }
    
    public List<TravelToSolo> neighbours() {
        final List<TravelToSolo> result = new ArrayList<TravelToSolo>();
        for (final Location neighbour : this.fLocation.macroNeighbours()) {
                result.add(new TravelToSolo(neighbour));
        }
        return result;
    }
    
    @Override
    public boolean performAction(final MWGridAgent pAgent) {
        if (pAgent.fLocation.equals(fLocation)) {
        	LOG.info("TTS to current location so returning true");
        	return true;
        }
		if (pAgent.fFirstTickActuallyMoved == 99999) {
			pAgent.fFirstTickActuallyMoved = ContextSingleton.getTime();
		}
		
		pAgent.fPlanStructure.addPlanToCurrentPlan(new Plan(pAgent.flockTowardLocation(fLocation)));
        return true;
    }

}
