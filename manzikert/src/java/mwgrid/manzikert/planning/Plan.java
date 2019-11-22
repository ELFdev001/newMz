package mwgrid.manzikert.planning;

import java.util.ArrayList;
import java.util.logging.Logger;

import mwgrid.manzikert.action.Action;

public class Plan extends ArrayList<Action> implements Comparable<Plan> {
	private static final Logger LOG =
			Logger.getLogger(Plan.class.getPackage().getName());
	private int fPriority;

	public Plan() {
		super();
		this.fPriority = 0;
	}

	public Plan(final Action pAction) {
		this();
		this.add(pAction);
	}

	public Plan(final int pInitialCapacity) {
		super(pInitialCapacity);
		this.fPriority = 0;
	}

	public Plan(final Plan pPlan) {
		super();
		this.fPriority = 0;
		LOG.finest("Plan size: " + pPlan.size());
		for (final Action action : pPlan)
			this.add(action.copy());
		LOG.finest("Copied plan size: " + this.size());
	}

	public Plan copy() {
		return new Plan(this);
	}

	public int compareTo(final Plan pPlan) {
		if (this == pPlan) return 0;
		if (this.fPriority < pPlan.fPriority) return -1;
		if (this.fPriority > pPlan.fPriority) return 1;
		return 0;
	}

	public int getPriority() {
		return this.fPriority;
	}

	public void setPriority(final int pPriority) {
		this.fPriority = pPriority;
	}

}
