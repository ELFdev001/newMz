package mwgrid.manzikert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import mwgrid.environment.ExpandedSingletonInitFile;
import mwgrid.manzikert.agent.BaggageHandler;
import mwgrid.manzikert.agent.CavalryOfficer;
import mwgrid.manzikert.agent.ColumnLeader;
import mwgrid.manzikert.agent.MWGridAgent;
import mwgrid.manzikert.agent.Officer;
import mwgrid.middleware.distributedobject.Location;

public final class ContextSingleton {
	public enum CampNeighbours {
		UP(0, -CAMP_SIZE),
		UP_RIGHT(CAMP_SIZE, -CAMP_SIZE),
		RIGHT(CAMP_SIZE, 0),
		DOWN_RIGHT(CAMP_SIZE, CAMP_SIZE),
		DOWN(0, CAMP_SIZE),
		DOWN_LEFT(-CAMP_SIZE, CAMP_SIZE),
		LEFT(-CAMP_SIZE, 0),
		UP_LEFT(-CAMP_SIZE, -CAMP_SIZE);
		private int fXModifier;
		private int fYModifier;

		/**
		 * @param pXModifier
		 *            - fX modifier
		 * @param pYModifier
		 *            - fY modifier
		 */
		private CampNeighbours(final int pXModifier, final int pYModifier) {
			this.fXModifier = pXModifier;
			this.fYModifier = pYModifier;
		}

		/**
		 * @param pLocation
		 *            - location
		 * @return (Location) location
		 */
		public Location getLocation(final Location pLocation) {
			return new Location(pLocation.getX() + this.fXModifier, pLocation
					.getY()
					+ this.fYModifier);
		}

		public CampNeighbours getNeighbouringDirection(boolean pClockwise) {
			LOG.finest("Getting neighbouring direction where pClockwise is " + pClockwise);
			int ord = this.ordinal();
			if (pClockwise) {
				if (ord == CampNeighbours.values().length - 1) {
					ord = 0;
				} else {
					ord++;
				}
			} else {
				if (ord == 0) {
					ord = CampNeighbours.values().length - 1;
				} else {
					ord--;
				}
			}
			LOG.finest("neighbour of " + this + " is " + CampNeighbours.values()[ord] + " clockwise is " + pClockwise);
			return CampNeighbours.values()[ord];
		}

		public CampNeighbours getOppositeDirection() {
			int ord = this.ordinal();
			ord = ord - 4;
			if (ord < 0) {
				ord = ord + 8;
			}
			LOG.finest("Oposite direction of " + this + " is " + CampNeighbours.values()[ord] + " because ord is " + ord);
			return CampNeighbours.values()[ord];
		}
	}

	private static final Logger LOG =
			Logger.getLogger(ContextSingleton.class.getPackage().getName());
	private static List<MWGridAgent> fAllAgents;
	private static List<Officer> fAllOfficers;
	private static Officer fEmperor;
	private static ContextSingleton INSTANCE;
	private static int fTime;
	private static int fDay;
	private static final int CAMP_SIZE = 550;
	private static HashMap<Location, Integer> loclist;


	/**
	 * Constructor
	 */
	private ContextSingleton() {
		LOG.info("Creating Context Singleton");
		fAllAgents = null;
		fAllOfficers = null;
		fEmperor = null;
		fTime = 0;
		loclist = new HashMap<Location, Integer>();
	}

	public static void loadAgents(final List<MWGridAgent> pAllAgents) {
		fAllAgents = pAllAgents;
		fAllOfficers = new ArrayList<Officer>();
		for (MWGridAgent thisagent : fAllAgents) {
			if (thisagent.fClass == ClassType.OFFICER)  {
				fAllOfficers.add((Officer) thisagent);
			} else if (thisagent.fClass == ClassType.CAVALRY_OFFICER) {
				fAllOfficers.add((CavalryOfficer) thisagent);
			} else if (thisagent.fClass == ClassType.BAGGAGE_HANDLER) {
				fAllOfficers.add((BaggageHandler) thisagent);
			} else if (thisagent.fClass == ClassType.COLUMN_LEADER) {
				LOG.info("Setting Emperor");
				fEmperor = (ColumnLeader) thisagent;
			}
		}
		loclist = new HashMap<Location, Integer>();
		fTime = 0;
	}

	public static void resetContext() {
		fAllAgents = null;
		fAllOfficers = null;
		fEmperor = null;
		fTime = 0;
		loclist = new HashMap<Location, Integer>();
	}

	public static List<MWGridAgent> getAllAgents() {
		return fAllAgents;
	}

	public static MWGridAgent getEmperor() {
		return fEmperor;
	}

	public static List<Officer> getAllOfficers() {
		return fAllOfficers;
	}

	public static Officer getOfficerFromUID(final int pUnitID) {
		if (pUnitID <= 0) {
			LOG.info("Returning Emperor");
			return fEmperor;
		}
		for (Officer thisoff : fAllOfficers) {
			if (thisoff.fUnitID == pUnitID) {
				return thisoff;
			}
		}
		return null;
	}

	public static CampNeighbours getCampDirection(final Location pStartLoc, final Location pEndLoc) {
		final int dx = pEndLoc.getX();
		final int dy = pEndLoc.getY();
		final int lx = pStartLoc.getX();
		final int ly = pStartLoc.getY();
		CampNeighbours direction;

		if (dx > (lx + 1100)) {
			if (dy > ly + 1100) {
				direction = CampNeighbours.DOWN_RIGHT;
			} else if (dy < ly - 1100) {
				direction = CampNeighbours.UP_RIGHT;
			} else {
				direction = CampNeighbours.RIGHT;
			}
		} else if (dx < (lx - 1100)) {
			if (dy > ly + 1100) {
				direction = CampNeighbours.DOWN_LEFT;
			} else if (dy < ly - 1100) {
				direction = CampNeighbours.UP_LEFT;
			} else {
				direction = CampNeighbours.LEFT;
			}

		} else if (dx > 0) {
			direction = CampNeighbours.DOWN;
		} else {
			direction = CampNeighbours.UP;
		}
		LOG.info("Returning " + direction);
		return direction;
	}

	public static Location[] getWaypoints(final int pColLdr, final Location pDestLoc, final Location pCentreLoc) {
		final Location[] returnArray = new Location[2];
		CampNeighbours direction;

		Location muster = new Location(Location.NULL_LOCATION);
		Location muster2 = new Location(Location.NULL_LOCATION);

		direction = getCampDirection(pCentreLoc, pDestLoc);

		CampNeighbours destdir = direction.getOppositeDirection();

		Location clock = direction.getNeighbouringDirection(true).getLocation(pCentreLoc);
		LOG.info("Set clock to " + clock);
		Location anticlock = direction.getNeighbouringDirection(false).getLocation(pCentreLoc);
		LOG.info("Set anticlock to " + anticlock);

		boolean nextclock;

		if (clock.distanceTo(pDestLoc) < anticlock.distanceTo(pDestLoc)) {
			nextclock = true;
		} else {
			nextclock = false;
		}

		LOG.info("nextclock is " + nextclock);

		if (pColLdr == 2) {
			LOG.info("Colldr = 2");
			direction = direction.getNeighbouringDirection(nextclock);
		} else if (pColLdr == 3) {
			LOG.info("Colldr = 3");
			direction = direction.getNeighbouringDirection(!nextclock);
		}

		LOG.info("direction = " + direction);
		muster = direction.getLocation(pCentreLoc);
		LOG.info("So muster is " + muster);

		clock = destdir.getNeighbouringDirection(true).getLocation(pDestLoc);
		anticlock = destdir.getNeighbouringDirection(false).getLocation(pDestLoc);

		//This needs to be the opposite way round to the previous version to avoid overlaps
		if (clock.distanceTo(pCentreLoc) > anticlock.distanceTo(pCentreLoc)) {
			nextclock = true;
		} else {
			nextclock = false;
		}

		if (pColLdr == 2) {
			destdir = destdir.getNeighbouringDirection(!nextclock);
		} else if (pColLdr == 3) {
			destdir = destdir.getNeighbouringDirection(nextclock);
		}

		muster2 = destdir.getLocation(pDestLoc);

		returnArray[0] = muster;
		returnArray[1] = muster2;

		return returnArray;
	}
	
	public static void setDay(int pDay) {
		fDay = pDay;
	}
	
	public static int getDay() {
		return fDay;
	}

	public static void step(int pTime) {
		fTime = pTime;
		HashMap<Location, Integer> theselocs = new HashMap<Location, Integer>();
		for (MWGridAgent thisagent : fAllAgents) {
			if (!loclist.containsKey(thisagent.fLocation)) {
				int size = 0;
				for (MWGridAgent thatagent : fAllAgents) {
					if (thisagent.fLocation.equals(thatagent.fLocation)) {
						size = size + thatagent.getSize();
					}
				}
				theselocs.put(thisagent.fLocation, size);
			} 
		}
		loclist = theselocs;
	}

	public static long getTime() {
		return fTime;
	}

	public static boolean hasSpaceForMe(final Location pLocation, final int pSize) {
		if (loclist.containsKey(pLocation)){
			if (ExpandedSingletonInitFile.getSquadMode()) {
				return false;
			}
			int thissize = loclist.get(pLocation);
			if (thissize + pSize > ExpandedSingletonInitFile.getMaxAgentSizeInCell()) {
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	public static boolean hasSpace(final Location pLocation) {
		if (loclist.containsKey(pLocation)){
			if (ExpandedSingletonInitFile.getSquadMode()) {
				return true;
			}
			int thissize = loclist.get(pLocation);
			if (thissize > ExpandedSingletonInitFile.getMaxAgentSizeInCell()) {
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	public static List<Location> getEmptyNeighbours(final Location pLocation, final int pSize) {
		final List<Location> theselocs = pLocation.neighbours();
		final List<Location> emptylocs = new ArrayList<Location>();
		for (Location thisloc : theselocs) {
			if (hasSpaceForMe(thisloc, pSize)) {
				emptylocs.add(thisloc);
			}
		}
		return emptylocs;
	}

	public static ContextSingleton getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ContextSingleton();
		}
		return INSTANCE;
	}
}
