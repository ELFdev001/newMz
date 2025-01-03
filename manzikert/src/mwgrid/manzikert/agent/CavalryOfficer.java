package mwgrid.manzikert.agent;

import mwgrid.manzikert.ClassType;
import mwgrid.manzikert.ContextSingleton;
import mwgrid.manzikert.DMTimeHandling;
import mwgrid.middleware.distributedobject.Location;

public class CavalryOfficer extends Officer {

	public CavalryOfficer(final ClassType pClassType, final Location pLocation, final int pCampID, final int pObjectID, final int pSetoffSpacing, final int pColLdr) {
		super(pClassType, pLocation, pCampID, pObjectID, pSetoffSpacing, pColLdr, pClassType.getSpeed());
	}

	@Override
	public void firstTick() {
		super.firstTick();
	}

	@Override
	public double getSpeed() {
		return DMTimeHandling.getCavalrySpeed(ContextSingleton.getTime(), 1);
	}
	
	@Override
	public void step(final long pTime) {
		super.step(pTime);
	}

}
