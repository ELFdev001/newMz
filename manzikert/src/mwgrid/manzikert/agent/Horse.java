package mwgrid.manzikert.agent;

import mwgrid.manzikert.ClassType;
import mwgrid.middleware.distributedobject.Location;

public class Horse extends BaggageAnimal{
	
	public Horse(final Location pLocation, final int pCampID, final int pObjectID) {
		super(ClassType.HORSE, pLocation, pCampID, pObjectID);
	}



}
