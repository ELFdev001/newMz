package mwgrid.manzikert.agent;

import mwgrid.manzikert.ClassType;
import mwgrid.middleware.distributedobject.Location;

public class CavalrySoldier extends Soldier {

	public CavalrySoldier(final ClassType pClassType, final Location pLocation, final int pCampID, final int pObjectID) {
		super(pClassType, pLocation, pCampID, pObjectID);
	}

	@Override
	public void firstTick() {
		super.firstTick();
	}

	@Override
	public double getSpeed() {
		return fSuperior.getSpeed();
	}
	
	@Override
	public void step(final long pTime) {
		super.step(pTime);
	}

}
