package mwgrid.environment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

import mwgrid.middleware.distributedobject.Location;

public final class ExpandedSingletonInitFile {
	private static final Logger LOG =
			Logger.getLogger(ExpandedSingletonInitFile.class.getPackage().getName());
	private static int endTime;
	private static int officers;
	private static int officerCavalrySquads;
	private static int cavalrySquads;
	private static int infantrySquads;
	private static int muleSquads;
	private static int donkeySquads;
	private static int horseSquads;
	private static int camelSquads;
	private static int cartSquads;
	private static int officerCavalrySquadSize;
	private static int cavalrySquadSize;
	private static int infantrySquadSize;
	private static int muleSquadSize;
	private static int donkeySquadSize;
	private static int horseSquadSize;
	private static int camelSquadSize;
	private static int cartSquadSize;
	private static int campSpaceBetweenSquads;
	private static int gapBetweenSectors;
	private static String outputTickFilename;
	private static String outputDayFilename;
	private static Location startLocation;
	private static Location[] destinationLocation;
	private static String resourceLocation;
	private static int maxAgentSizeInCell;
	private static int marchSpacing;
	private static double initHM;
	private static double minHM;
	private static double HMstep;
	private static double costLevel;
	private static double costUShall;
	private static double costUMed;
	private static double costUSteep;
	private static double costDShall;
	private static double costDMed;
	private static double costDSteep;
	private static double defaultHM;
	private static int initMaxsteps;
	private static int cutoffMedSteep;
	private static int cutoffShallMed;
	private static int cutoffLevel;
	private static int enviroXparts;
	private static int enviroYparts;
	private static int enviroPartListSize;
	private static boolean rest;
	private static double cavalryLead;
	private static double cavalryWalk;
	private static double cavalryTrot;
	private static int marchCutoffTemp;
	private static int secondaryUnitSize;
	private static int setoffspacing;
	private static int secondarysetoffspacing;
	private static int sectionsetoffspacing;
	private static int columns;
	private static String textID;
	private static boolean terrainLogging;
	private static boolean heightCrawler;
	private static boolean trace3D;
	private static boolean flatterrain;
	private static double agentWeight;
	private static int terrainBorderSize;
	private static boolean squadMule;
	private static int sectorCMuleSquads;
	private static int sectorNMuleSquads;
	private static int sectorWMuleSquads;
	private static int sectorEMuleSquads;
	private static int sectorSMuleSquads;
	private static boolean squadMode;


	private static ExpandedSingletonInitFile INSTANCE = null;

	/**
	 * Constructor
	 */
	private ExpandedSingletonInitFile(String pFilename) {
		LOG.finest("Reading Init File" + pFilename);
		try {
			final BufferedReader reader =
					new BufferedReader(new FileReader(pFilename));

			String dataLine = null;
			for (int row = 0; (dataLine = reader.readLine()) != null; row++) {
				LOG.finest(dataLine);
				final String[] tokens = dataLine.split("\\s+");
				if (tokens.length == 2) {
					final String thisParam = tokens[0];
					final String thisVal = tokens[1];

					if (thisParam.equals("OUTPUT_TICK_FILENAME")) {
						outputTickFilename = thisVal;
					} else if (thisParam.equals("OUTPUT_DAY_FILENAME")) {
						outputDayFilename = thisVal;
					} else if (thisParam.equals("START_LOCATION")) {
						startLocation = getLocationFromString(thisVal);
					} else if (thisParam.equals("DESTINATION_lOCATION")) {
						destinationLocation = getLocationArrayFromString(thisVal);
					} else if (thisParam.equals("RESOURCE_LOCATION")) {
						resourceLocation = thisVal;
					} else if (thisParam.equals("END_TIME")) {
						endTime = Integer.parseInt(thisVal);
					} else if (thisParam.equals("INIT_HEURISTIC_MOD")) {
						initHM = Double.parseDouble(thisVal);
					} else if (thisParam.equals("MIN_HEURISTIC_MOD")) {
						minHM = Double.parseDouble(thisVal);
					} else if (thisParam.equals("INIT_MAXSTEPS")) {
						initMaxsteps = Integer.parseInt(thisVal);
					} else if (thisParam.equals("HM_STEP")) {
						HMstep = Double.parseDouble(thisVal);
					} else if (thisParam.equals("CUTOFF_MED_STEEP")) {
						cutoffMedSteep = Integer.parseInt(thisVal);
					} else if (thisParam.equals("CUTOFF_SHALLOW_MED")) {
						cutoffShallMed = Integer.parseInt(thisVal);
					} else if (thisParam.equals("CUTOFF_LEVEL")) {
						cutoffLevel = Integer.parseInt(thisVal);
					} else if (thisParam.equals("MULE_SQUADS")) {
						muleSquads = Integer.parseInt(thisVal);
					} else if (thisParam.equals("DONKEY_SQUADS")) {
						donkeySquads = Integer.parseInt(thisVal);
					} else if (thisParam.equals("HORSE_SQUADS")) {
						horseSquads = Integer.parseInt(thisVal);
					} else if (thisParam.equals("CAMEL_SQUADS")) {
						camelSquads = Integer.parseInt(thisVal);
					} else if (thisParam.equals("CART_SQUADS")) {
						cartSquads = Integer.parseInt(thisVal);
					} else if (thisParam.equals("MULE_SQUAD_SIZE")) {
						muleSquadSize = Integer.parseInt(thisVal);
					} else if (thisParam.equals("DONKEY_SQUAD_SIZE")) {
						donkeySquadSize = Integer.parseInt(thisVal);
					} else if (thisParam.equals("HORSE_SQUAD_SIZE")) {
						horseSquadSize = Integer.parseInt(thisVal);
					} else if (thisParam.equals("CAMEL_SQUAD_SIZE")) {
						camelSquadSize = Integer.parseInt(thisVal);
					} else if (thisParam.equals("CART_SQUAD_SIZE")) {
						cartSquadSize = Integer.parseInt(thisVal);
					} else if (thisParam.equals("COST_LEVEL")) {
						costLevel = Double.parseDouble(thisVal);
					} else if (thisParam.equals("COST_UP_SHALLOW")) {
						costUShall = Double.parseDouble(thisVal);
					} else if (thisParam.equals("COST_UP_MED")) {
						costUMed = Double.parseDouble(thisVal);
					} else if (thisParam.equals("COST_UP_STEEP")) {
						costUSteep = Double.parseDouble(thisVal);
					} else if (thisParam.equals("COST_DOWN_SHALLOW")) {
						costDShall = Double.parseDouble(thisVal);
					} else if (thisParam.equals("COST_DOWN_MED")) {
						costDMed = Double.parseDouble(thisVal);
					} else if (thisParam.equals("COST_DOWN_STEEP")) {
						costDSteep = Double.parseDouble(thisVal);
					} else if (thisParam.equals("DEFAULT_HEURISTIC_MOD")) {
						defaultHM = Double.parseDouble(thisVal);
					} else if (thisParam.equals("NUMBER_OF_X_PARTS")) {
						LOG.finest("enviroXparts Val = " + thisVal);
						enviroXparts = Integer.parseInt(thisVal);
					} else if (thisParam.equals("NUMBER_OF_Y_PARTS")) {
						LOG.finest("enviroYparts Val = " + thisVal);
						enviroYparts = Integer.parseInt(thisVal);
					} else if (thisParam.equals("SIZE_OF_PART_LIST")) {
						enviroPartListSize = Integer.parseInt(thisVal);
					} else if (thisParam.equals("TERRAIN_LOGGING")) {
						terrainLogging = Boolean.parseBoolean(thisVal);
					} else if (thisParam.equals("MAX_AGENT_SIZE_IN_CELL")) {
						maxAgentSizeInCell = Integer.parseInt(thisVal);
					} else if (thisParam.equals("MARCH_SPACING")) {
						marchSpacing = Integer.parseInt(thisVal);
					} else if (thisParam.equals("REST")) {
						rest = Boolean.parseBoolean(thisVal);
					} else if (thisParam.equals("CAVALRY_LEAD")) {
						cavalryLead = Double.parseDouble(thisVal);
					} else if (thisParam.equals("CAVALRY_WALK")) {
						cavalryWalk = Double.parseDouble(thisVal);
					} else if (thisParam.equals("CAVALRY_TROT")) {
						cavalryTrot = Double.parseDouble(thisVal);
					} else if (thisParam.equals("MARCH_CUTOFF_TEMP")) {
						marchCutoffTemp = Integer.parseInt(thisVal);
					} else if (thisParam.equals("OFFICERS")) {
						officers = Integer.parseInt(thisVal);
					} else if (thisParam.equals("OFFICER_CAVALRY_SQUADS")) {
						officerCavalrySquads = Integer.parseInt(thisVal);
					} else if (thisParam.equals("CAVALRY_SQUADS")) {
						cavalrySquads = Integer.parseInt(thisVal);
					} else if (thisParam.equals("INFANTRY_SQUADS")) {
						infantrySquads = Integer.parseInt(thisVal);
					} else if (thisParam.equals("OFFICER_CAVALRY_SQUAD_SIZE")) {
						officerCavalrySquadSize = Integer.parseInt(thisVal);
					} else if (thisParam.equals("CAVALRY_SQUAD_SIZE")) {
						cavalrySquadSize = Integer.parseInt(thisVal);
					} else if (thisParam.equals("INFANTRY_SQUAD_SIZE")) {
						infantrySquadSize = Integer.parseInt(thisVal);
					} else if (thisParam.equals("CAMP_SPACE_BETWEEN_SQUADS")) {
						campSpaceBetweenSquads = Integer.parseInt(thisVal);
					} else if (thisParam.equals("GAP_BETWEEN_SECTORS")) {
						gapBetweenSectors = Integer.parseInt(thisVal);
					} else if (thisParam.equals("SECONDARY_UNIT_SIZE")) {
						secondaryUnitSize = Integer.parseInt(thisVal);
					} else if (thisParam.equals("SETOFF_SPACING")) {
						setoffspacing = Integer.parseInt(thisVal);
					} else if (thisParam.equals("SECONDARY_SETOFF_SPACING")) {
						secondarysetoffspacing = Integer.parseInt(thisVal);
					} else if (thisParam.equals("SECTION_SETOFF_SPACING")) {
						sectionsetoffspacing = Integer.parseInt(thisVal);
					} else if (thisParam.equals("COLUMN_LEADERS")) {
						columns = Integer.parseInt(thisVal);
					} else if (thisParam.equals("TEXT_ID")) {
						textID = thisVal;
					} else if (thisParam.equals("HEIGHTCRAWLER")) {
						heightCrawler = Boolean.parseBoolean(thisVal);
					} else if (thisParam.equals("AGENT_WEIGHT")) {
						agentWeight = Double.parseDouble(thisVal);
					} else if (thisParam.equals("TERRAIN_BORDER_SIZE")) {
						terrainBorderSize = Integer.parseInt(thisVal);
					} else if (thisParam.equals("3D_TRACE")) {
						trace3D = Boolean.parseBoolean(thisVal);
					} else if (thisParam.equals("FLAT_TERRAIN")) {
						flatterrain = Boolean.parseBoolean(thisVal);
					} else if (thisParam.equals("SQUAD_MULE")) {
						squadMule = Boolean.parseBoolean(thisVal);
					} else if (thisParam.equals("SECTOR_C_MULE_SQUADS")) {
						sectorCMuleSquads = Integer.parseInt(thisVal);
					} else if (thisParam.equals("SECTOR_N_MULE_SQUADS")) {
						sectorNMuleSquads = Integer.parseInt(thisVal);
					} else if (thisParam.equals("SECTOR_W_MULE_SQUADS")) {
						sectorWMuleSquads = Integer.parseInt(thisVal);
					} else if (thisParam.equals("SECTOR_E_MULE_SQUADS")) {
						sectorEMuleSquads = Integer.parseInt(thisVal);
					} else if (thisParam.equals("SECTOR_S_MULE_SQUADS")) {
						sectorSMuleSquads = Integer.parseInt(thisVal);
					} else if (thisParam.equals("SQUAD_MODE")) {
						squadMode = Boolean.parseBoolean(thisVal);
					}
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getResourceLoc() {
		return resourceLocation;
	}

	public static Location getLocationFromString(final String pLoc) {
		final String[] locString = pLoc.split(",");
		final int thisX = Integer.parseInt(locString[0]);
		final int thisY = Integer.parseInt(locString[1]);
		return new Location(thisX, thisY);
	}

	public static Location[] getLocationArrayFromString(final String pLocs) {
		final String[] locString = pLocs.split(";");
		Location[] locArray;
		locArray = new Location[locString.length];
		for (int j = 0; j < locString.length; j++) {
			final String[] thisLoc = locString[j].split(",");
			final int thisX = Integer.parseInt(thisLoc[0]);
			final int thisY = Integer.parseInt(thisLoc[1]);
			locArray[j] = new Location(thisX, thisY);
		}
		return locArray;
	}

	public static int getEndTime() {
		return endTime;
	}

	public static int getColumns() {
		return columns;
	}

	public static boolean getRest() {
		return rest;
	}

	public static boolean getTerrainLogging() {
		return terrainLogging;
	}

	public static boolean getSquadMule() {
		return squadMule;
	}

	public static boolean getFlatTerrain() {
		return flatterrain;
	}

	public static boolean get3DTrace() {
		return trace3D;
	}

	public static int getSectorCMuleSquads() {
		return sectorCMuleSquads;
	}

	public static int getSectorNMuleSquads() {
		return sectorNMuleSquads;
	}

	public static int getSectorWMuleSquads() {
		return sectorWMuleSquads;
	}

	public static int getSectorEMuleSquads() {
		return sectorEMuleSquads;
	}

	public static int getSectorSMuleSquads() {
		return sectorSMuleSquads;
	}

	public static int getTerrainBorderSize() {
		return terrainBorderSize;
	}
	public static int getCutoffLevel() {
		return cutoffLevel;
	}

	public static String getOutputTickFilename(final int pDay) {
		final String oTF = (int) getOfficers() + "-" + (int) getOfficerCavalrySquads() + "-" + (int) getCavalrySquads() + "-" + (int) getInfantrySquads() + "-" + getBaggageTot() + "-" + getStartLocation(pDay).getX() + "-" + getStartLocation(pDay).getY() + "-" + getDestinationLocation(pDay).getX() + "-" + getDestinationLocation(pDay).getY() + "-" + getRest() + "-" + textID + "-" + outputTickFilename;
		return oTF;
	}

	public static String getOutputTerrainFilename(final int pDay) {
		final String oTF = getStartLocation(pDay).getX() + "-" + getStartLocation(pDay).getY() + "-" + getDestinationLocation(pDay).getX() + "-" + getDestinationLocation(pDay).getY() + "-" + textID + "-TERRAIN.txt";
		return oTF;
	}

	public static String getOutputDayFilename(final int pDay) {
		final String oDF = (int) getOfficers() + "-" + (int) getOfficerCavalrySquads() + "-" + (int) getCavalrySquads() + "-" + (int) getInfantrySquads() + "-" + getBaggageTot() + "-" + getStartLocation(pDay).getX() + "-" + getStartLocation(pDay).getY() + "-" + getDestinationLocation(pDay).getX() + "-" + getDestinationLocation(pDay).getY() + "-" + getRest() + "-" + textID + "-" + outputDayFilename;
		return oDF;
	}

	public static Location getStartLocation(final int pDay) {
		if (pDay == 0) {
			return startLocation;
		} else {
			return getDestinationLocation(pDay -1);
		}
	}

	public static Location getDestinationLocation(final int pDay) {
		return destinationLocation[pDay];
	}

	public static int getNumberOfDaysMarch() {
		return destinationLocation.length;
	}

	public static double getInitHM() {
		return initHM;
	}

	public static int getEnviroXparts() {
		return enviroXparts;
	}

	public static int getEnviroYparts() {
		return enviroYparts;
	}

	public static int getEnviroPartListSize() {
		return enviroPartListSize;
	}

	public static double getMinHM() {
		return minHM;
	}

	public static double getHMstep() {
		return HMstep;
	}

	public static double getCostLevel() {
		return costLevel;
	}

	public static int getMaxAgentSizeInCell() {
		return maxAgentSizeInCell;
	}

	public static int getMarchSpacing(final boolean pIsCavalry) {
		if (pIsCavalry) {
			return marchSpacing;
		} else {
			return 1;
		}
	}


	public static double getCostUShall() {
		return costUShall;
	}

	public static double getCostUMed() {
		return costUMed;
	}

	public static double getCostUSteep() {
		return costUSteep;
	}

	public static double getCostDShall() {
		return costDShall;
	}

	public static double getCostDMed() {
		return costDMed;
	}

	public static double getCostDSteep() {
		return costDSteep;
	}

	public static double getDefaultHM() {
		return defaultHM;
	}

	public static int getInitMaxsteps() {
		return initMaxsteps;
	}

	public static int getCutoffMedSteep() {
		return cutoffMedSteep;
	}

	public static int getCutoffShallMed() {
		return cutoffShallMed;
	}

	public static double getCavalryLead() {
		return cavalryLead;
	}

	public static double getCavalryWalk() {
		return cavalryWalk;
	}

	public static double getCavalryTrot() {
		return cavalryTrot;
	}

	public static int getOfficers() {
		return officers;
	}

	public static int getOfficerCavalrySquadSize() {
		if (squadMode) {
			return 0;
		} else {
			return officerCavalrySquadSize;
		}
	}

	public static int getOfficerCavalrySquads() {
		return officerCavalrySquads;
	}

	public static int getGapBetweenSectors() {
		return gapBetweenSectors;
	}

	public static int getCavalrySquads() {
		return cavalrySquads;
	}

	public static int getCavalrySquadSize() {
		if (squadMode) {
			return 0;
		} else {
			return cavalrySquadSize;
		}
	}

	public static int getCampSpaceBetweenSquads() {
		return campSpaceBetweenSquads;
	}

	public static int getInfantrySquads() {
		return infantrySquads;
	}

	public static int getMuleSquads() {
		return muleSquads;
	}

	public static int getDonkeySquads() {
		return donkeySquads;
	}

	public static int getHorseSquads() {
		return horseSquads;
	}

	public static int getCamelSquads() {
		return camelSquads;
	}

	public static int getCartSquads() {
		return cartSquads;
	}

	public static int getBaggageTot() {
		return muleSquads + donkeySquads + horseSquads + camelSquads + cartSquads;
	}
	public static int getInfantrySquadSize() {
		if (squadMode) {
			return 0;
		} else {
			return infantrySquadSize;
		}
	}

	public static int getMuleSquadSize() {
		if (squadMode) {
			return 0;
		} else {
			return muleSquadSize;
		}
	}

	public static int getDonkeySquadSize() {
		if (squadMode) {
			return 0;
		} else {
			return donkeySquadSize;
		}
	}

	public static int getHorseSquadSize() {
		if (squadMode) {
			return 0;
		} else {
			return horseSquadSize;
		}
	}

	public static int getCamelSquadSize() {
		if (squadMode) {
			return 0;
		} else {
			return camelSquadSize;
		}
	}

	public static int getCartSquadSize() {
		if (squadMode) {
			return 0;
		} else {
			return cartSquadSize;
		}
	}

	public static int getMarchCutoffTemp() {
		return marchCutoffTemp;
	}

	public static int getSecondaryUnitSize() {
		return secondaryUnitSize;
	}

	public static int getSetoffSpacing() {
		return setoffspacing;
	}

	public static int getSecondarySetoffSpacing() {
		return secondarysetoffspacing;
	}

	public static int getSectionSetoffSpacing() {
		return sectionsetoffspacing;
	}

	public static boolean getHeightcrawler() {
		return heightCrawler;
	}

	public static double getAgentWeight() {
		return agentWeight;
	}
	
	public static boolean getSquadMode() {
		return squadMode;
	}

	public static ExpandedSingletonInitFile getInstance(String pFilename) {
		if (INSTANCE == null) {
			INSTANCE = new ExpandedSingletonInitFile(pFilename);
		}
		return INSTANCE;
	}

	public static ExpandedSingletonInitFile getInstance() {
		return INSTANCE;
	}

}
