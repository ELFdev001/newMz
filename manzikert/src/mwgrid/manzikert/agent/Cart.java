package mwgrid.manzikert.agent;

import mwgrid.manzikert.ClassType;
import mwgrid.middleware.distributedobject.Location;

public class Cart extends BaggageAnimal{
	
	public Cart(final Location pLocation, final int pCampID, final int pObjectID) {
		super(ClassType.CART, pLocation, pCampID, pObjectID);
	}



}
