package mwgrid.manzikert;

import mwgrid.manzikert.agent.Officer;
import mwgrid.middleware.distributedobject.Location;

public final class CampHandling {

	private CampHandling() {
	}


	public static Location getPointLocation(final Location pPoint) {
		return randomNeighbour(pPoint, 5);
	}

	public static Location randomNeighbour(final Location thisloc, final int pFactor) {
		final int thisX = thisloc.getX();
		final int thisY = thisloc.getY();
		final int rand = (int) (Math.random() * 8);
		switch (rand) {
		case 0:
			return new Location(thisX - pFactor, thisY - pFactor);
		case 1:
			return new Location(thisX - pFactor, thisY);
		case 2:
			return new Location(thisX - pFactor, thisY + pFactor);
		case 3:
			return new Location(thisX, thisY - pFactor);
		case 4:
			return new Location(thisX, thisY + pFactor);
		case 5:
			return new Location(thisX + pFactor, thisY + pFactor);
		case 6:
			return new Location(thisX + pFactor, thisY);
		default:
			return new Location(thisX + pFactor, thisY - pFactor);
		}
	}

	public static Location getCampLocation(final Location pEmpTent,
			final Officer pAgent) {
		int thisX = pEmpTent.getX();
		int thisY = pEmpTent.getY();
		thisX = thisX + pAgent.fCampOffsetX;
		thisY = thisY + pAgent.fCampOffsetY;
		final Location campLoc = new Location(thisX, thisY);
		return campLoc;
	}


}
