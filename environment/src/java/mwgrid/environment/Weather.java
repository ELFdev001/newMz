package mwgrid.environment;

import java.util.logging.Logger;


public class Weather {
	public enum WeatherType {
	    HOT, AVERAGE, COOL;
	    public int getClassTypeId() {
	        return this.ordinal();
	    }
	    
	}

	private static final Logger LOG =
		Logger.getLogger(Weather.class.getPackage().getName());
	private static int temps[] = new int[24];
	
	private static Weather INSTANCE = null;

	private Weather(final WeatherType wType) {
		for (int i = 0; i < 24; i++) {
			temps[i] = 0;
		}
		switch(wType) {
		case HOT:
			LOG.info("Initialising weather. It's HOT today, phew!");
			temps[6] = 3;
			temps[7] = 2;
			temps[8] = 5;
			temps[9] = 8;
			temps[10] = 11;
			temps[11] = 13;
			temps[12] = 15;
			temps[13] = 16;
			temps[14] = 17;
			temps[15] = 17;
			temps[16] = 17;
			temps[17] = 17;
			temps[18] = 16;
			temps[19] = 16;
			break;
		case AVERAGE:
			LOG.info("Initialising weather. It's AVERAGE today, meh.");
			temps[6] = 0;
			temps[7] = 0;
			temps[8] = 0;
			temps[9] = 0;
			temps[10] = 0;
			temps[11] = 0;
			temps[12] = 0;
			temps[13] = 0;
			temps[14] = 0;
			temps[15] = 0;
			temps[16] = 0;
			temps[17] = 0;
			temps[18] = 0;
			temps[19] = 0;
			break;
		case COOL:
			LOG.info("Initialising weather. It's COOL today, brrrr!");
			temps[6] = 0;
			temps[7] = 0;
			temps[8] = 0;
			temps[9] = 0;
			temps[10] = 0;
			temps[11] = 0;
			temps[12] = 0;
			temps[13] = 0;
			temps[14] = 0;
			temps[15] = 0;
			temps[16] = 0;
			temps[17] = 0;
			temps[18] = 0;
			temps[19] = 0;
			break;
		}
	}
	
	public static int getTemp(final int pHour) {
		return temps[pHour];
	}
	
	 public static Weather getInstance(final WeatherType wType) {
		 if (INSTANCE == null) {
			 INSTANCE = new Weather(wType);
		 }
		 return INSTANCE;
	 }

	 public static Weather getInstance() {
		 return INSTANCE;
	 }


}
