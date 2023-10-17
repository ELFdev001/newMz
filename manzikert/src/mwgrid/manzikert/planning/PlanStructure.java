package mwgrid.manzikert.planning;

import java.util.LinkedList;
import java.util.List;

import mwgrid.manzikert.action.Action;

public class PlanStructure {
    private Plan fCurrentPlan;
    private List<Plan> fPlanList;
    private int fPosition;
    
    public PlanStructure() {
        this.fPosition = 0;
        this.fCurrentPlan = new Plan();
        this.fPlanList = new LinkedList<Plan>();
    }
    
    protected PlanStructure(final PlanStructure pPlanStructure) {
        this();
        this.fPosition = pPlanStructure.fPosition;
        this.fCurrentPlan = pPlanStructure.fCurrentPlan.copy();
        for (final Plan plan : pPlanStructure.fPlanList) {
            final Plan newPlan = new Plan();
            for (final Action action : plan)
                newPlan.add(action.copy());
            this.fPlanList.add(newPlan);
        }
    }
    
    public PlanStructure copy() {
        return new PlanStructure(this);
    }
    
    public boolean isEmpty() {
		if ((this.getCurrentPlan().size() < 1 || this.getPosition() >= this.getCurrentPlan().size())
				&& this.getPlanList().size() < 1) {
			return true;
		} else {
			return false;
		}
    }

    public final void addActionPlanToPlanList(final Action pAction) {
        this.fPlanList.add(new Plan(pAction));
    }
    
    public final void addPlan(final Plan pPlan) {
        this.fPlanList.add(pPlan);
    }
    
    public final void clearCurrentPlan() {
        this.fCurrentPlan = new Plan();
    }
    
    public final void collapseCurrentPlan() {
        if (this.fCurrentPlan.isEmpty()) throw new IllegalStateException();
        this.fPosition = 0;
        this.fCurrentPlan = new Plan(this.fCurrentPlan.get(0));
    }
    
    public Plan getCurrentPlan() {
        return this.fCurrentPlan;
    }
    
    public void getNextPlanFromPlanList() {
        if (!this.fPlanList.isEmpty()) {
            this.fCurrentPlan = this.fPlanList.remove(0);
            this.fPosition = 0;
        } else {
            this.fCurrentPlan.clear();
            this.fPosition = 0;
        }
    }
    
    public List<Plan> getPlanList() {
        return this.fPlanList;
    }
    
    public int getPosition() {
        return this.fPosition;
    }
    
    public void insertActionPlanToPlanList(final Action pAction) {
        this.fPlanList.add(0, new Plan(pAction));
    }
    
    public void addPlanToCurrentPlan(final Plan pPlan) {
        for (Action pAction : pPlan) {
            this.fCurrentPlan.add(pAction);
        }
    }
    
    public void setPosition(final int pPosition) {
        this.fPosition = pPosition;
    }
}
