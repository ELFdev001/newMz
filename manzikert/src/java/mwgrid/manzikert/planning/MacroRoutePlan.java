package mwgrid.manzikert.planning;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import mwgrid.environment.ExpandedSingletonInitFile;
import mwgrid.manzikert.action.Action;
import mwgrid.manzikert.action.Move;
import mwgrid.manzikert.action.TravelToSolo;
import mwgrid.middleware.distributedobject.Location;

public class MacroRoutePlan extends Plan {
    private static final Logger LOG =
            Logger.getLogger(MacroRoutePlan.class.getPackage().getName());
    private double fHeuristicModifier;
    private double fCost;
    private final Location fGoal;
    
    public MacroRoutePlan(final Location pGoal, final double pHeuristicModifier) {
        super();
        this.fCost = -1.0;
        this.fGoal = pGoal;
        this.fHeuristicModifier = pHeuristicModifier;
        LOG.finest("Created routeplan with HM = " + pHeuristicModifier);
    }
    
    public MacroRoutePlan(final Location pGoal) {
        super();
        this.fCost = -1.0;
        this.fGoal = pGoal;
        this.fHeuristicModifier = ExpandedSingletonInitFile.getDefaultHM();
    }
    
    public MacroRoutePlan(final Location pGoal, final int pInitialCapacity,
            final double pHeuristicModifier) {
        super(pInitialCapacity);
        this.fCost = -1.0;
        this.fGoal = pGoal;
        this.fHeuristicModifier = pHeuristicModifier;
    }
    
    public MacroRoutePlan(final MacroRoutePlan pRoutePlan) {
        super(pRoutePlan);
        this.fCost = pRoutePlan.fCost;
        this.fGoal = pRoutePlan.fGoal;
        this.fHeuristicModifier = pRoutePlan.fHeuristicModifier;
    }
    
    @Override
    public int compareTo(final Plan pPlan) {
        if (!(pPlan instanceof MacroRoutePlan))
            throw new IllegalArgumentException();
        if (super.compareTo(pPlan) != 0) return super.compareTo(pPlan);
        final double costDifference =
                this.getCostEstimate()
                        - ((MacroRoutePlan) pPlan).getCostEstimate();
        if (costDifference > 0) return 1;
        else if (costDifference < 0) return -1;
        return 0;
    }
    
    @Override
    public MacroRoutePlan copy() {
        return new MacroRoutePlan(this);
    }
    
    public double getCostEstimate() {
        if (this.size() == 0) this.fCost = Integer.MAX_VALUE;
        else this.fCost =
                getDiagonalDistance(this.headLocation(), this.fGoal)
                        + this.getCostMove();
        this.fCost = this.fCost * this.fHeuristicModifier;
        return this.fCost;
    }
    
    // An alternative to using the Euclidean distance as a* heuristic
    public double getDiagonalDistance(final Location pLocA,
            final Location pLocB) {
        double result = 0;
        final double maxNo =
                Math.max(Math.abs(pLocA.getX() - pLocB.getX()), Math
                        .abs(pLocA.getY() - pLocB.getY()));
        final double minNo =
                Math.min(Math.abs(pLocA.getX() - pLocB.getX()), Math
                        .abs(pLocA.getY() - pLocB.getY()));
        result = Math.sqrt((minNo * minNo) * 2) + (maxNo - minNo);
        result = result * fHeuristicModifier;
        return result / 10;
    }
    
    public TravelToSolo head() {
        return (TravelToSolo) this.get(this.size() - 1);
    }
    
    public Location headLocation() {
        return this.head().fLocation.copy();
    }
    
    public boolean reachedGoal(final Location pGoal) {
        LOG.finest("Goal is " + pGoal + " and this headloc is " + this.head().fLocation);
        if (this.head().fLocation.distanceTo(pGoal) < 30) {
            return true;
        }
        return false;
    }
    
    public List<MacroRoutePlan> successors() {
        final List<MacroRoutePlan> result = new ArrayList<MacroRoutePlan>();
        for (final TravelToSolo child : this.head().neighbours()) {
            LOG.finest("RoutePlan size: " + this.size());
            final MacroRoutePlan successor = this.copy();
            LOG.finest("Successor size: " + successor.size());
            successor.add(child);
            successor.getCostEstimate();
            LOG.finest("Added successor size: " + successor.size());
            result.add(successor);
        }
        return result;
    }
    
    public Move tail() {
        return (Move) this.get(0);
    }
    
    public Location tailLocation() {
        return this.tail().fLocation.copy();
    }
    
    private double getCostMove() {
        double cost = 0;
        TravelToSolo pastAction = new TravelToSolo(Location.NULL_LOCATION);
        for (final Action pMove : this) {
            if (!pastAction.fLocation.equals(Location.NULL_LOCATION)) {
                final TravelToSolo pThisAction = (TravelToSolo) pMove;
                cost += pThisAction.getCost(pastAction);
            }
            pastAction = (TravelToSolo) pMove;
        }
        return cost;
    }
}
