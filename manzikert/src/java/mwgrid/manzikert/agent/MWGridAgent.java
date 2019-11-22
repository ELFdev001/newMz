package mwgrid.manzikert.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


import mwgrid.environment.Environment;
import mwgrid.environment.EnvironmentVariables;
import mwgrid.environment.ExpandedSingletonInitFile;
import mwgrid.environment.PartEnvHeightOnlyImplementation;
import mwgrid.manzikert.CampHandling;
import mwgrid.manzikert.ContextSingleton;
import mwgrid.manzikert.DMTimeHandling;
import mwgrid.manzikert.NullHandling;
import mwgrid.manzikert.ClassType;
import mwgrid.manzikert.action.Action;
import mwgrid.manzikert.action.Move;
import mwgrid.manzikert.messages.Message;
import mwgrid.manzikert.planning.Plan;
import mwgrid.manzikert.planning.PlanStructure;
import mwgrid.middleware.distributedobject.Location;
import mwgrid.middleware.distributedobject.Value;
import mwgrid.middleware.distributedobject.Location.Neighbours;


public abstract class MWGridAgent {

	private static final Logger LOG = Logger.getLogger(MWGridAgent.class
			.getPackage().getName());
	public Location fLocation;
	public Location fLastLocation;
	public PlanStructure fPlanStructure;
	public final ClassType fClass;
	public int fRestTicks;
	public MWGridAgent fSuperior;
	public List<Message> fMessageInbox;
	public final int fUnitID;
	public long fLastTickMoved;
	public final long fObjectID;
	public Location fDestination;
	public double fDistTravelled;
	public double fMovePoints;
	public long fFirstTickActuallyMoved;
	public double fCaloriesExpended;
	public int lastMinHeight, tickHeight;
	public double lastMinDistance;
	private double speed;
	private static final Environment ENVIRONMENT =
			PartEnvHeightOnlyImplementation.getInstance();



	public MWGridAgent(final ClassType pClassType, final int pCampId,
			final Location pLocation, final PlanStructure pPlanStructure,
			final int pObjectID, final double pSpeed) {
		MWGridAgent.LOG.finest("Constructor");
		this.fClass = pClassType;
		this.fLastTickMoved = 0;
		this.fLocation = pLocation;
		this.fLastLocation = pLocation;
		this.fPlanStructure = pPlanStructure;
		this.fMessageInbox = new ArrayList<Message>();
		this.fSuperior = null;
		this.fObjectID = pObjectID;
		this.fUnitID = pCampId;
		this.fDestination = Location.NULL_LOCATION;
		this.fDistTravelled = 0;
		this.fRestTicks = 0;
		this.fMovePoints = 0;
		this.lastMinDistance = 0;
		this.fFirstTickActuallyMoved = 99999;
		this.speed = pSpeed;
		final Value<?> thisHeightVal =
				ENVIRONMENT.getEnvironmentValue(this.fLocation,
						EnvironmentVariables.HEIGHT);
		this.lastMinHeight = this.tickHeight = (Integer) thisHeightVal.get();
	}

	public int getClassTypeID() {
		return fClass.ordinal();
	}

	public Message getMessage() {
		if (fMessageInbox.size() > 0) {
			return fMessageInbox.remove(0);
		}
		else {
			return NullHandling.NULL_MESSAGE;
		}
	}

	public int getSize() {
		return this.fClass.getSize();
	}
	
	public double getSpeed() {
		return this.speed;
	}
	
	public void receiveMessage(final Message pMessage) {
		fMessageInbox.add(pMessage);
	}


	public Message viewMessage() {
		if (fMessageInbox.size() > 0) {
			return fMessageInbox.get(0);
		}
		else {
			return NullHandling.NULL_MESSAGE;
		}
	}

	public boolean isCavalry() {
		if (this instanceof CavalryOfficer || this instanceof CavalrySoldier || this instanceof ColumnLeader) {
			return true;
		}
		return false;
	}

	protected Location randomEmptyNeighbour(Location pLocation) {
		LOG.info("Random empty neighbour from agent " + this.fObjectID + " at tick " + ContextSingleton.getTime());
		final List<Location> usedneighbours = new ArrayList<Location>();
		while (usedneighbours.size() <= 8) {
			final Location thisneighbour = CampHandling.randomNeighbour(pLocation, 1);
			if (ContextSingleton.hasSpaceForMe(thisneighbour, getSize())) {
				return thisneighbour;
			} else {
				usedneighbours.add(thisneighbour);
			}
		}
		return Location.NULL_LOCATION;
	}

	protected Location getShuffleLoc() {
		final Location shuffleloc;
		final int rand = (int) (Math.random() * 100);
		if (fLastLocation.equals(Location.NULL_LOCATION)) {
			shuffleloc = randomEmptyNeighbour(fLocation);
		} else {
			if (rand < 50) {
				shuffleloc = fLastLocation;
			} else if (fLocation.distanceTo(fLastLocation) > 1.1) {
				if (rand < 75) {
					shuffleloc = new Location(fLastLocation.getX(), fLocation.getY());
				} else {
					shuffleloc = new Location(fLocation.getX(), fLastLocation.getY());
				}
			} else {
				if (fLastLocation.getX() == fLocation.getX()) {
					//updown
					if (rand < 70) {
						shuffleloc = new Location(fLastLocation.getX() - 1, fLastLocation.getY());
					} else if (rand < 90) {
						shuffleloc = new Location(fLastLocation.getX() + 1, fLastLocation.getY());
					} else if (rand < 95) {
						shuffleloc = new Location(fLastLocation.getX() - 1, fLocation.getY());
					} else {
						shuffleloc = new Location(fLastLocation.getX() + 1, fLocation.getY());
					}
				} else {
					//sideside
					if (rand < 70) {
						shuffleloc = new Location(fLastLocation.getX(), fLastLocation.getY() - 1);
					} else if (rand < 90) {
						shuffleloc = new Location(fLastLocation.getX(), fLastLocation.getY() + 1);

					} else if (rand < 95) {
						shuffleloc = new Location(fLocation.getX(), fLastLocation.getY() - 1);
					} else {
						shuffleloc = new Location(fLocation.getX(), fLastLocation.getY() + 1);
					}
				}
			}
		}
		return shuffleloc;
	}

	protected void shuffle() {
		LOG.finest("On step " + ContextSingleton.getTime() + " Agent " + this.fObjectID + " is shuffling");
		final Location shuffleloc;
		if (fLastLocation.equals(Location.NULL_LOCATION) || fLastLocation.equals(fLocation) || !fLastLocation.neighbours().contains(fLocation)) {
			shuffleloc = this.randomEmptyNeighbour(this.fLocation);
		} else {
			shuffleloc = getShuffleLoc();
		}
		if (!shuffleloc.equals(Location.NULL_LOCATION)) {
			fPlanStructure.addActionPlanToPlanList(new Move(shuffleloc));
			LOG.finer("Shuffling from " + this.fLocation + "to " + shuffleloc);
		}
	}


	public Plan flockTowardAgent(final MWGridAgent pTarget) {
		final Location targetLoc = pTarget.fLocation;
		Location destloc = targetLoc;
		final Plan thisplan = new Plan();
		if (targetLoc.equals(Location.NULL_LOCATION)) {
			LOG.info("Invalid Target specified!");
		} else {
			if (!this.fLocation.equals(targetLoc)){
				if (!ContextSingleton.hasSpaceForMe(targetLoc, getSize())) {
					if (!pTarget.fLastLocation.equals(Location.NULL_LOCATION)){
						if (ContextSingleton.hasSpaceForMe(pTarget.fLastLocation, getSize())) {
							destloc = pTarget.fLastLocation;
						} else {
							destloc = randomEmptyNeighbour(pTarget.fLastLocation);
						}
					} else {
						destloc = randomEmptyNeighbour(targetLoc);
					}
				}
				Location nextloc = fLocation;
				while (destloc.distanceTo(targetLoc) < nextloc.distanceTo(targetLoc)) {
					nextloc = flockNeighbour(nextloc, destloc);
					thisplan.add(new Move(nextloc));
				}
			}
		}
		return thisplan;
	}

	public Plan flockTowardLocation(final Location pLocation) {
		final Plan thisplan = new Plan();
		if (pLocation.equals(Location.NULL_LOCATION)) {
			LOG.info("Invalid Target specified!");
		} else {
			Location nextloc = fLocation;
			while (!nextloc.equals(pLocation)) {
				nextloc = flockNeighbour(nextloc, pLocation);
				thisplan.add(new Move(nextloc));
			}
		}
		return thisplan;
	}

	protected Location flockNeighbour(final Location pStartLocation, final Location pDestLocation) {
		final int FLOCKFACTOR = 4;
		final Location tl = pStartLocation;
		final Location fl = pDestLocation;
		boolean xbigger = false;
		boolean ybigger = false;
		Neighbours neighbour;
		final int xdiff = tl.getX() - fl.getX();
		LOG.finest("xdiff = " + xdiff);
		final int ydiff = tl.getY() - fl.getY();
		LOG.finest("ydiff = " + ydiff);
		final int absx = Math.abs(xdiff);
		final int absy = Math.abs(ydiff);
		if (xdiff > 0) {
			xbigger = true;
			LOG.finest("so xbigger = " + xbigger);
		}
		if (ydiff > 0) {
			ybigger = true;
			LOG.finest("so ybigger = " + ybigger);
		}
		if (ybigger && absx * FLOCKFACTOR < absy) {
			LOG.finest("Setting UP");
			neighbour = Neighbours.UP;
		} else if (!ybigger && absx * FLOCKFACTOR < absy) {
			LOG.finest("Setting DOWN");
			neighbour = Neighbours.DOWN;
		} else if (xbigger && absy * FLOCKFACTOR < absx) {
			LOG.finest("Setting LEFT");
			neighbour = Neighbours.LEFT;
		} else if (!xbigger && absy * FLOCKFACTOR < absx) {
			LOG.finest("Setting RIGHT");
			neighbour = Neighbours.RIGHT;
		} else if (xbigger && ybigger) {
			LOG.finest("Setting UPLEFT");
			neighbour = Neighbours.UP_LEFT;
		} else if (!xbigger && ybigger) {
			LOG.finest("Setting UPRIGHT");
			neighbour = Neighbours.UP_RIGHT;
		} else if (xbigger && !ybigger) {
			LOG.finest("Setting DOWNLEFT");
			neighbour = Neighbours.DOWN_LEFT;
		} else {
			LOG.finest("Setting DOWNRIGHT");
			neighbour = Neighbours.DOWN_RIGHT;
		}
		LOG.finest("Agent " + this.fObjectID + " loc = " + tl + " and officerloc = " + fl + " so dest = " + neighbour);
		return neighbour.getLocation(tl);
	}


	public void step(final long pTime) {
		if (fMovePoints > getSpeed() * 2) {
			fMovePoints = getSpeed() * 2;
		} else if (fMovePoints < 0 - (getSpeed() * 2)) {
			fMovePoints = 0 - (getSpeed() * 2);
		}
		// Handle messages
		Message message = NullHandling.NULL_MESSAGE;
		message = this.viewMessage();
		if (message != NullHandling.NULL_MESSAGE) {
			LOG.fine("Agent " + this.fObjectID + " has a message on tick " + ContextSingleton.getTime());
			if (this.processMessage(message)) {
				message = this.getMessage();
			}
		} 

		boolean actionSuccessful = true;

		while (fMovePoints >= 0 && actionSuccessful && !fPlanStructure.isEmpty()) {
			if (fPlanStructure.getCurrentPlan().isEmpty() || fPlanStructure.getPosition() >= fPlanStructure.getCurrentPlan().size()) {
				fPlanStructure.getNextPlanFromPlanList();
			}

			if (!fPlanStructure.getCurrentPlan().isEmpty()) {
				final Action action = fPlanStructure.getCurrentPlan().get(fPlanStructure.getPosition());
				actionSuccessful = this.processAction(action);
				if (actionSuccessful) {
					fPlanStructure.setPosition(fPlanStructure.getPosition() + 1);
				} 
			}
		}
		fMovePoints = fMovePoints + getSpeed();

		//work out cals expended
		if (ContextSingleton.getTime() % DMTimeHandling.ticksPerMinute() == 0) {
			if (fDistTravelled != lastMinDistance) {
				final double dist = fDistTravelled - lastMinDistance;
				final Value<?> thisHeightVal =
						ENVIRONMENT.getEnvironmentValue(this.fLocation,
								EnvironmentVariables.HEIGHT);
				final Integer thisHeight = (Integer) thisHeightVal.get();
				if (!isCavalry()) {
					final int heightdiff = thisHeight - lastMinHeight;
					fCaloriesExpended += caloriesExpended(dist, heightdiff, ExpandedSingletonInitFile.getAgentWeight());
				} else {
					fCaloriesExpended += cavCaloriesExpended(dist);
				}
				lastMinHeight = thisHeight;
				lastMinDistance = fDistTravelled;
			}
		}
	}


	public double cavCaloriesExpended(final double pDist) {
		double vo2;
		if (pDist > (ExpandedSingletonInitFile.getCavalryWalk() * DMTimeHandling.ticksPerMinute())) {
			LOG.finest("Cavalry trotting");
			vo2 = 25.51;
		} else {
			LOG.finest("Cavalry walking");
			vo2 = 12.02;
		}
		//Cavalry calories don't change with agentWeight as it's not the human who carries it
		return vo2ToCals(vo2, 70);
	}

	public double caloriesExpended(final double pDist, final int pHeightM, final double pWeightKg) {
		final double R = 3.5;
		final double H = 0.1 * pDist;
		final double length = Math.sqrt((Math.pow(pDist, 2)) - Math.pow(pHeightM, 2));
		double grade = pHeightM / length;
		if (grade < 0) {
			grade = 0;
		}
		final double V = 1.8 * pDist * grade;
		final double vo2 = (int) (R + H + V);
		LOG.finest("pDist=" + pDist + " height = " + pHeightM + " weight=" + pWeightKg + " H="
				+ H + " R=" + R + " V=" + V + " grade=" + grade + " vo2=" + vo2);
		return vo2ToCals(vo2, pWeightKg);
	}

	public double vo2ToCals(final double vo2, final double pWeightKg) {
		final double vo2weight = vo2 * pWeightKg;
		final double vo2l = vo2weight / 1000;
		final double cals = (int) (vo2l * 5.047);
		LOG.finest("Weight=" + pWeightKg + " vo2=" + vo2 + " vo2w=" + vo2weight + " vo2l=" 
				+ vo2l + " cals=" + cals);
		return cals;
	}

	public String report() {
		final StringBuilder report = new StringBuilder();
		final long time = ContextSingleton.getTime();
		report.append(time);
		report.append(" ");
		final long objectId = this.fObjectID;
		report.append(objectId);
		report.append(" ");
		final int classTypeId = this.getClassTypeID();
		report.append(classTypeId);
		report.append(" ");
		final Location location = this.fLocation;
		report.append(location.getX());
		report.append(" ");
		report.append(location.getY());
		if (ExpandedSingletonInitFile.getHeightcrawler()) {
			report.append(" ");
			report.append(lastMinDistance);
			report.append(" ");
			report.append(lastMinHeight);
		} else if (ExpandedSingletonInitFile.get3DTrace()) {
			final Value<?> thisHeightVal =
					ENVIRONMENT.getEnvironmentValue(this.fLocation,
							EnvironmentVariables.HEIGHT);
			this.tickHeight = (Integer) thisHeightVal.get();
			report.append(" ");
			report.append(tickHeight);
		}
		report.append(NullHandling.SEPARATOR);
		if (DMTimeHandling.isLastTickOfSimulation(time)) {
			report.append(objectId);
			report.append(" ");
			report.append(classTypeId);
			report.append(" ");
			final long timeArrived = this.fLastTickMoved;
			report.append(timeArrived);
			report.append(" ");
			final int distTravelled = (int) this.fDistTravelled;
			report.append(distTravelled);
			report.append(" ");
			report.append(fCaloriesExpended);
			report.append(" ");
			report.append(fFirstTickActuallyMoved);
		}
		return report.toString();
	}

	protected boolean processAction(final Action pAction) {
		return pAction.performAction(this);
	}

	protected boolean processMessage(final Message pMessage) {
		return true;
	}
}
