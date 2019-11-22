package mwgrid.manzikert.agent;

import mwgrid.manzikert.ClassType;
import mwgrid.middleware.distributedobject.Location;

public class Mule extends BaggageAnimal{

	public Mule(final Location pLocation, final int pCampID, final int pObjectID) {
		super(ClassType.MULE, pLocation, pCampID, pObjectID);
	}



}
