package mwgrid.manzikert.agent;

import java.util.logging.Logger;

import mwgrid.manzikert.ClassType;
import mwgrid.manzikert.ContextSingleton;
import mwgrid.manzikert.DMTimeHandling;
import mwgrid.middleware.distributedobject.Location;

public class BaggageHandler extends Officer {

	public BaggageHandler(final ClassType pClassType, final Location pLocation, final int pCampID, final int pObjectID, final int pSetoffSpacing, final int pColLdr, final double pSpeed) {
		super(pClassType, pLocation, pCampID, pObjectID, pSetoffSpacing, pColLdr, pSpeed);
	}

	@Override
	public void firstTick() {
		super.firstTick();
		LOG.info("Baggage handler has speed " + this.getSpeed());
	}

	@Override
	public void step(final long pTime) {
		super.step(pTime);
	}

}
