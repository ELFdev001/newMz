package mwgrid.manzikert.agent;

import mwgrid.manzikert.ClassType;
import mwgrid.manzikert.ContextSingleton;
import mwgrid.middleware.distributedobject.Location;

public class ColumnLeader extends CavalryOfficer {
	private static final int CAMP_ID = 0;


	public ColumnLeader(final Location pLocation, final int pObjectID) {
		super(ClassType.COLUMN_LEADER, pLocation, CAMP_ID, pObjectID, 0, 1);
	}

	protected Officer initialiseSucceeder() {
		return ContextSingleton.getOfficerFromUID(1);
	}
}
