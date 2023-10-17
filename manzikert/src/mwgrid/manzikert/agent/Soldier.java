package mwgrid.manzikert.agent;

import java.util.List;

import mwgrid.manzikert.ClassType;
import mwgrid.manzikert.ContextSingleton;
import mwgrid.manzikert.planning.Plan;
import mwgrid.manzikert.planning.PlanStructure;
import mwgrid.middleware.distributedobject.Location;

public class Soldier extends MWGridAgent {

	public Soldier(final ClassType pClassType, final Location pLocation, final int pCampID, final int pObjectID) {
		super(pClassType, pCampID, pLocation, new PlanStructure(), pObjectID, pClassType.getSpeed());
	}


	public void firstTick() {
		this.fSuperior = initialiseSuperior();
	}


	protected MWGridAgent initialiseSuperior() {
		final List<Officer> theseoff = ContextSingleton.getAllOfficers();
		for (Officer thisoff : theseoff) {
			if (thisoff.fUnitID == this.fUnitID) {
				return thisoff;
			}
		}
		return null;
	}

	@Override
	public void step(final long pTime) {
		if (pTime == 1) {
			firstTick();
		} else {
			super.step(pTime);
			if (fPlanStructure.isEmpty()) {
				Plan thisplan = flockTowardAgent(fSuperior);
				if (!thisplan.isEmpty()) {
					fPlanStructure.addPlan(thisplan);
				}
			}
		}
}
}

