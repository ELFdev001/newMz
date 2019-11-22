package mwgrid.manzikert.agent;

import java.util.logging.Logger;

import mwgrid.environment.ExpandedSingletonInitFile;
import mwgrid.manzikert.CampHandling;
import mwgrid.manzikert.ClassType;
import mwgrid.manzikert.ContextSingleton;
import mwgrid.manzikert.DMTimeHandling;
import mwgrid.manzikert.DMTimeHandling.TickType;
import mwgrid.manzikert.action.PlanMacroRouteTo;
import mwgrid.manzikert.action.SendMessage;
import mwgrid.manzikert.messages.Message;
import mwgrid.manzikert.messages.Message.MessageType;
import mwgrid.manzikert.planning.Plan;
import mwgrid.manzikert.planning.PlanStructure;
import mwgrid.middleware.distributedobject.Location;

public class Officer extends MWGridAgent {
	private static final Logger LOG =
			Logger.getLogger(Officer.class.getPackage().getName());
	private boolean fFollow;
	public boolean fResting;
	public int fSetoffSpacing;
	public int fColumnLeader;
	public int fSetoffTick;
	public Officer fPreceder;
	public Officer fSuccessor;
	public final int fCampOffsetX;
	public final int fCampOffsetY;

	public Officer(final ClassType pClassType, final Location pLocation, final int pCampID, final int pObjectID, final int pSetoffSpacing, final int pColLdr, final double pSpeed) {
		super(pClassType, pCampID, pLocation, new PlanStructure(), pObjectID, pSpeed);
		this.fFollow = false;
		this.fSetoffSpacing = pSetoffSpacing;
		this.fColumnLeader = pColLdr;
		this.fResting = false;
		this.fCampOffsetX = pLocation.getX() - ExpandedSingletonInitFile.getStartLocation(ContextSingleton.getDay()).getX();
		this.fCampOffsetY = pLocation.getY() - ExpandedSingletonInitFile.getStartLocation(ContextSingleton.getDay()).getY();
	}

	public void setFollow(final boolean pFollow) {
		this.fFollow = pFollow;
	}

	protected Officer initialisePreceder() {
		return ContextSingleton.getOfficerFromUID(fUnitID - 1);
	}

	protected Officer initialiseSucceeder() {
		final MWGridAgent thisoff = ContextSingleton.getOfficerFromUID(fUnitID + 1);
		if (thisoff != null) {
			return (Officer) thisoff;
		} else {
			LOG.info("Agent# " + this.fObjectID + " is returning itself as it's last in line");
			return this;
		}
	}


	public void firstTick() {
		this.fPreceder = initialisePreceder();
		this.fSuccessor = initialiseSucceeder();
		this.fSetoffTick = 1;
		if (this.fColumnLeader > 0) {
			final Location fStartLocation = ExpandedSingletonInitFile.getStartLocation(ContextSingleton.getDay());
			final Location fFinalDestination = ExpandedSingletonInitFile.getDestinationLocation(ContextSingleton.getDay());
			if (!fPreceder.equals(this)) {
				this.fPreceder.fSuccessor = fPreceder;
				LOG.info("Agent number " + this.fObjectID + " is Column Leader #" + fColumnLeader);
			}
			if (!fFinalDestination.equals(Location.NULL_LOCATION)) {
				if (this.fLocation.distanceTo(fFinalDestination) > 800) {

//					final Location[] muster = ContextSingleton.getWaypoints(fColumnLeader, fFinalDestination, this.fLocation);
					final Location[] muster = ContextSingleton.getWaypoints(fColumnLeader, fFinalDestination, fStartLocation);

					LOG.info("Splitting route into 3. Dest is " + fFinalDestination + "muster is " + muster[0] + " and muster2 is " + muster[1]);
					fPlanStructure.addActionPlanToPlanList(new SendMessage(fSuccessor, new Message(MessageType.FOLLOW, muster[0], false)));
					fPlanStructure.addActionPlanToPlanList(new PlanMacroRouteTo(muster[0], false));
					fPlanStructure.addActionPlanToPlanList(new PlanMacroRouteTo(muster[1], true));
					fPlanStructure.addActionPlanToPlanList(new SendMessage(fSuccessor, new Message(MessageType.GO_TO_CAMP, fFinalDestination, false)));
					fPlanStructure.addActionPlanToPlanList(new PlanMacroRouteTo(fFinalDestination, false));

				} else {
					LOG.fine("Single route. Dest is " + fFinalDestination);
					fPlanStructure.addActionPlanToPlanList(new SendMessage(fSuccessor, new Message(MessageType.GO_TO_CAMP, fFinalDestination, false)));
					fPlanStructure.addActionPlanToPlanList(new PlanMacroRouteTo(fFinalDestination, true));
				}
			} else {
				LOG.fine("No destination so not doing anything");
			}
		} else {
			this.fSetoffTick = fPreceder.fSetoffTick + fSetoffSpacing;
		}
	}

	@Override
	public void step(final long pTime) {
		//Does each agent need to know their time when it's passed to each step method anyway?
		if (pTime == 1) {
			firstTick();
		} else if (pTime >= fSetoffTick){
			if (!fResting || !ExpandedSingletonInitFile.getRest()) {
				doStep(pTime);
			} else {
				final TickType thistick = DMTimeHandling.getTickType(pTime);
				if (thistick == TickType.MARCH) {
					doStep(pTime);
				} else if (thistick == TickType.OPTIONAL_REST) {
					if (this.fLocation.distanceTo(fPreceder.fLocation) > ExpandedSingletonInitFile.getMarchSpacing(this.isCavalry())) {
						doStep(pTime);
					} else {
						fRestTicks++;
					}
				} else if (thistick == TickType.MANDATORY_REST || thistick == TickType.WEATHER_REST) {
					fRestTicks++;
				}
			}
		}
		LOG.finer("Officer of unit " + Integer.toString(this.fUnitID, 5) + " at " + this.fLocation + ", time: " + ContextSingleton.getTime());
	}

	protected void doStep(final long pTime) {
		super.step(pTime);
		if (fPlanStructure.isEmpty()) {
			if (fFollow) {
				
				if (fFirstTickActuallyMoved == 99999) {
					fFirstTickActuallyMoved = ContextSingleton.getTime();
				}

				if (!fPreceder.fDestination.equals(fDestination)){
					fPlanStructure.addPlan(new Plan(flockTowardLocation(fDestination)));
				} else {
					Plan thisplan = flockTowardAgent(fPreceder);
					if (!thisplan.isEmpty()) {
						fPlanStructure.addPlan(thisplan);
					}
				}
			} else if (!ContextSingleton.hasSpace(this.fLocation)) {
				shuffle();
			}
		}
		if (fLocation.distanceTo(fDestination) < 2) {
			fDestination = Location.NULL_LOCATION;
		}
	}

	@Override
	protected boolean processMessage(final Message pMessage) {
		if (!fDestination.equals(Location.NULL_LOCATION)) {
			return false;
		} else {
			if (pMessage.getMsgType() == MessageType.FOLLOW) {
				this.setFollow(true);
				this.fDestination = pMessage.getMsgLocation();
			} else if (pMessage.getMsgType() == MessageType.GO_TO_CAMP) {
				this.setFollow(false);
				this.fDestination = CampHandling.getCampLocation(pMessage.getMsgLocation(), this);
				fPlanStructure.addActionPlanToPlanList(new PlanMacroRouteTo(this.fDestination, false));
			} else if (pMessage.getMsgType() == MessageType.GO_TO_POINT) {
				this.setFollow(false);
				this.fDestination = pMessage.getMsgLocation();
				fPlanStructure.addActionPlanToPlanList(new PlanMacroRouteTo(this.fDestination, false));
			} else {
				LOG.info("Invalid message for agent " + this.fObjectID);
				return false;
			}
			if (!this.fSuccessor.equals(this)) {
				fSuccessor.receiveMessage(pMessage);
			}
			this.fResting = pMessage.fResting;
			return true;
		}
	}
}
