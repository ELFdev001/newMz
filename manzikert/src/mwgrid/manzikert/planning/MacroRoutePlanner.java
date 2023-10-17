package mwgrid.manzikert.planning;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import mwgrid.manzikert.NullHandling;
import mwgrid.manzikert.action.TravelToSolo;
import mwgrid.middleware.distributedobject.Location;

public final class MacroRoutePlanner {
	private static final Logger LOG =
			Logger
			.getLogger(MacroRoutePlanner.class.getPackage()
					.getName());

	public MacroRoutePlanner() {
	}

	public static MacroRoutePlan planRoute(final Location pStartLocation,
			final Location pEndLocation, final double pHM, final int pMaxSteps) {
		final Location goal = pEndLocation;
		final PriorityQueue<MacroRoutePlan> plans =
				new PriorityQueue<MacroRoutePlan>(100000);
		final MacroRoutePlan root = new MacroRoutePlan(goal, pHM);
		root.add(new TravelToSolo(pStartLocation));
		plans.add(root);
		final Map<TravelToSolo, Double> minimalCosts = new HashMap<TravelToSolo, Double>();
		for (int steps = 0; !plans.isEmpty() && steps < pMaxSteps; steps++) {
			final MacroRoutePlan best = plans.remove();
			if (best.reachedGoal(goal)) {
				best.remove(0);
				if (MacroRoutePlanner.LOG.isLoggable(Level.FINEST)
						&& best.size() < 2)
					MacroRoutePlanner.LOG.finest("Plan from "
							+ pStartLocation.toString() + " -> "
							+ pEndLocation.toString() + " = " + best);
				LOG.info("Route planned in " + steps + " steps");
				return best;
			}
			for (final MacroRoutePlan successor : best.successors()) {
				final TravelToSolo successorHead = successor.head();
				final double cost = successor.getCostEstimate();
				if (minimalCosts.containsKey(successorHead)) {
					if (minimalCosts.get(successorHead).doubleValue() > cost) {
						plans.add(successor);
						minimalCosts.put(successorHead, new Double(cost));
					}
				} else {
					minimalCosts.put(successorHead, new Double(cost));
					plans.add(successor);
				}

			}
		}
		return NullHandling.NULL_MACRO_ROUTE_PLAN;
	}
}
