package mwgrid.manzikert.action;

import mwgrid.manzikert.agent.MWGridAgent;

public abstract class Action {
    
    public Action() {
    }
    
    public abstract double getCost();
    
    public abstract Action copy();
    
    public abstract boolean performAction(MWGridAgent pAgent);
}
