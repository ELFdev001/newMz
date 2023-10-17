package mwgrid.manzikert;

import java.util.logging.Logger;

import mwgrid.environment.ExpandedSingletonInitFile;
import mwgrid.environment.Weather;

public final class DMTimeHandling {
	public enum TickType {
		MARCH, OPTIONAL_REST, MANDATORY_REST, WEATHER_REST;
		public int getTickTypeId() {
			return this.ordinal();
		}

		public static TickType getTickType(final int pTickTypeId) {
			for (final TickType tickType : TickType.values())
				if (tickType.getTickTypeId() == pTickTypeId)
					return tickType;
			return MARCH;
		}
	}

	private static final Logger LOG =
			Logger.getLogger(DMTimeHandling.class.getPackage().getName());
	private static final double LENGTH_OF_TICK_IN_SECS = 3.72855;
	private static final int MINUTE_OF_OPTIONAL_REST = 50;
	private static final int MINUTE_OF_MANDATORY_REST = 55;
	private static final int MINUTES_IN_HOUR = 60;
	private static final int SECONDS_IN_MINUTE = 60;
	private static final int START_HOUR = 6;
	private static final int START_MINUTE = 11;
	private static final int CAV_CYCLE_IN_MINUTES = 60;
	private static final int CAV_TROT_TIME_IN_MINUTES = 30;
	private static final int CAV_WALK_TIME_IN_MINUTES = 50;





	/**
	 * Constructor
	 */
	 private DMTimeHandling() {
		// do nothing
	}

	public static double ticksPerHour() {
		final double tph = (MINUTES_IN_HOUR * SECONDS_IN_MINUTE) / LENGTH_OF_TICK_IN_SECS;
		return tph;
	}

	public static int ticksPerMinute() {
		final int tpm =  (int) (SECONDS_IN_MINUTE / LENGTH_OF_TICK_IN_SECS);
		return tpm;
	}

	public static int getHour(final long tick) {
		int hour = START_HOUR;
		hour = (int) (hour + ((((tick * LENGTH_OF_TICK_IN_SECS) / SECONDS_IN_MINUTE) + START_MINUTE) / MINUTES_IN_HOUR));
		return hour;
	}

	public static int getMinute(final long tick) {
		int minute = START_MINUTE;
		minute = (int) ((minute + (tick * LENGTH_OF_TICK_IN_SECS) / SECONDS_IN_MINUTE) % MINUTES_IN_HOUR);
		return minute;
	}

	public static int getMinuteSinceSimStarted(final long tick) {
		int minute;
		minute = (int) (tick * LENGTH_OF_TICK_IN_SECS) / SECONDS_IN_MINUTE;
		return minute;
	}

	public static int getMinuteSinceArbitraryPoint(final long thisTick, final long startTick) {
		final long timebetweenticks = thisTick - startTick;
		int minute;
		minute = (int) (timebetweenticks * LENGTH_OF_TICK_IN_SECS) / SECONDS_IN_MINUTE;
		return minute;
	}


	public static int getSecond(final long tick) {
		final int second = (int) ((tick * LENGTH_OF_TICK_IN_SECS) % SECONDS_IN_MINUTE);
		return second;
	}

	public static String getTimeString(final long tick) {
		final String time = getHour(tick) + ":" + getMinute(tick) + ":" + getSecond(tick);
		return time;
	}
	public static boolean isLastTickOfSimulation(final long time) {
		if (time >= ExpandedSingletonInitFile.getEndTime()) { return true; }
		return false;
	}

	//Determines rest periods based on time since start rather than point in hour
	//so first rest is 50 minutes after Emperor starts, not on the 50th min of the hour
	public static TickType getTickType(final long pTime) {
		final double optionalboundary = ticksPerHour() * MINUTE_OF_OPTIONAL_REST / MINUTES_IN_HOUR;
		final double mandatoryboundary = ticksPerHour() * MINUTE_OF_MANDATORY_REST / MINUTES_IN_HOUR;
		if (Weather.getTemp(getHour(pTime)) >= ExpandedSingletonInitFile.getMarchCutoffTemp()) {
			LOG.finest("Current temp is " + Weather.getTemp(getHour(pTime)) + " and cutoff is " + ExpandedSingletonInitFile.getMarchCutoffTemp() + " so resting");
			return TickType.WEATHER_REST;
		}
		if (pTime % ticksPerHour() > mandatoryboundary) {
			LOG.finest("tick is " + pTime + " so returning mandatory rest");
			return TickType.MANDATORY_REST;
		} else if (pTime % ticksPerHour() > optionalboundary) {
			LOG.finest("tick is " + pTime + " so returning optional rest");
			return TickType.OPTIONAL_REST;
		} else {
			LOG.finest("tick is " + pTime + " so returning march");
			return TickType.MARCH;
		}
	}

	public static double getCavalrySpeed(final long pTime, final long pTickMarchStarted) {
		int minute = getMinuteSinceArbitraryPoint(pTime, pTickMarchStarted);
		if (minute % CAV_CYCLE_IN_MINUTES < CAV_TROT_TIME_IN_MINUTES) {
			LOG.finest("we're in " + minute + " of cycle of " + CAV_CYCLE_IN_MINUTES + " so TROTing");
			return ExpandedSingletonInitFile.getCavalryTrot();
		} else if (minute % CAV_CYCLE_IN_MINUTES < CAV_WALK_TIME_IN_MINUTES) {
			LOG.finest("we're in " + minute + " of cycle of " + CAV_CYCLE_IN_MINUTES + " so WALKing");
			return ExpandedSingletonInitFile.getCavalryWalk();
		} else {
			LOG.finest("we're in " + minute + " of cycle of " + CAV_CYCLE_IN_MINUTES + " so LEADing");
			return ExpandedSingletonInitFile.getCavalryLead();
		}
	}

}
