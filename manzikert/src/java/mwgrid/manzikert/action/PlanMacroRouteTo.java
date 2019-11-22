package mwgrid.manzikert.action;

import java.util.Collections;
import java.util.logging.Logger;

import mwgrid.environment.ExpandedSingletonInitFile;
import mwgrid.manzikert.ContextSingleton;
import mwgrid.manzikert.NullHandling;
import mwgrid.manzikert.agent.MWGridAgent;
import mwgrid.manzikert.agent.Officer;
import mwgrid.manzikert.messages.Message;
import mwgrid.manzikert.messages.Message.MessageType;
import mwgrid.manzikert.planning.MacroRoutePlan;
import mwgrid.manzikert.planning.MacroRoutePlanner;
import mwgrid.middleware.distributedobject.Location;

public class PlanMacroRouteTo extends Action {
	private static final Logger LOG =
			Logger.getLogger(PlanMacroRouteTo.class.getPackage().getName());
	private final boolean fHighDetail;
	private final Location fLocation;

	public PlanMacroRouteTo(final Location pLocation, final boolean pHighDetail) {
		this.fLocation = pLocation;
		this.fHighDetail = pHighDetail;
	}

	protected PlanMacroRouteTo(final PlanMacroRouteTo pPlanRouteTo) {
		this.fLocation = pPlanRouteTo.fLocation;
		this.fHighDetail = pPlanRouteTo.fHighDetail;
	}

	@Override
	public Action copy() {
		return new PlanMacroRouteTo(this);
	}

	@Override
	public double getCost() {
		return 0;
	}

	@Override
	public boolean performAction(final MWGridAgent pAgent) {
		final Officer thisAgent = (Officer) pAgent;
		LOG.info("PlanMacroRouteTo action started to " + this.fLocation + " by agent " + thisAgent.fObjectID);
		final int maxsteps = ExpandedSingletonInitFile.getInitMaxsteps() + (int) (thisAgent.fLocation.distanceTo(this.fLocation));
		LOG.finest("Maxsteps = " + maxsteps);
		double hm = ExpandedSingletonInitFile.getInitHM();

		MacroRoutePlan routePlan = NullHandling.NULL_MACRO_ROUTE_PLAN;

		Location actualdest = this.fLocation;

		LOG.info("Planning route to " + actualdest);
		thisAgent.fDestination = actualdest;
		routePlan = MacroRoutePlanner.planRoute(thisAgent.fLocation, actualdest, hm, maxsteps);
		if (routePlan.equals(NullHandling.NULL_MACRO_ROUTE_PLAN)) {
			LOG.info("Initial route plan for agent " + thisAgent.fObjectID + " failed at tick " + ContextSingleton.getTime() + ". Everything's screwed!");
			return false;
		}
		MacroRoutePlan lastSuccess = routePlan;

		if (fHighDetail) {
			while (!routePlan.equals(NullHandling.NULL_MACRO_ROUTE_PLAN) && hm > ExpandedSingletonInitFile.getMinHM()) {
				lastSuccess = routePlan;
				hm = hm - ExpandedSingletonInitFile.getHMstep();
				routePlan = MacroRoutePlanner.planRoute(thisAgent.fLocation, actualdest, hm, maxsteps);
			}
		}

		thisAgent.fPlanStructure.insertActionPlanToPlanList(new TravelToSolo(actualdest));
		if (fHighDetail) {
			thisAgent.fPlanStructure.insertActionPlanToPlanList(new SendMessage(thisAgent.fSuccessor, new Message(MessageType.FOLLOW, actualdest, true)));
		}

		Collections.reverse(lastSuccess);

		for (Action thisact : lastSuccess) {
			thisAgent.fPlanStructure.insertActionPlanToPlanList(thisact);
			if (fHighDetail) {
				TravelToSolo thisTTS = (TravelToSolo) thisact;
				thisAgent.fPlanStructure.insertActionPlanToPlanList(new SendMessage(thisAgent.fSuccessor, new Message(MessageType.FOLLOW, thisTTS.fLocation, true)));
			}
		}

		return true;
	}

}
