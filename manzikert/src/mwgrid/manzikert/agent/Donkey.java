package mwgrid.manzikert.agent;

import mwgrid.manzikert.ClassType;
import mwgrid.middleware.distributedobject.Location;

public class Donkey extends BaggageAnimal{

	public Donkey(final Location pLocation, final int pCampID, final int pObjectID) {
		super(ClassType.DONKEY, pLocation, pCampID, pObjectID);
	}



}
