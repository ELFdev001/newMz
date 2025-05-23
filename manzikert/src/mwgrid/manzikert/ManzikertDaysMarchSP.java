package mwgrid.manzikert;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import mwgrid.environment.Environment;
import mwgrid.environment.EnvironmentVariables;
import mwgrid.environment.ExpandedSingletonInitFile;
import mwgrid.environment.PartEnvHeightOnlyImplementation;
import mwgrid.environment.Weather;
import mwgrid.environment.Weather.WeatherType;
import mwgrid.manzikert.ContextSingleton.CampNeighbours;
import mwgrid.manzikert.agent.MWGridAgent;
import mwgrid.middleware.distributedobject.Location;
import mwgrid.middleware.distributedobject.Value;

public class ManzikertDaysMarchSP {
	private static final Logger LOG = Logger
			.getLogger(ManzikertDaysMarchSP.class.getPackage()
					.getName());
	private BufferedWriter fOutputTickFile;
	private BufferedWriter fOutputDayFile;
	private BufferedWriter fOutputTerrainFile;
	private List<MWGridAgent> fAllAgents;
	private String[] fDayFileNames;
	public static ExpandedSingletonInitFile initFile = null;
	public static ContextSingleton initContext = null;
	public static Weather weather = null;
	public int radiusOfLargestSquare;
	public int radiusOfOuterSectors;
	public int radiusOfOfficerSector;
	public int agentObjectID;
	public int unitID;
	public int largestSector;
	public int colLdrNumber;
	private int maxX, minX,
	maxY, minY;


	public ManzikertDaysMarchSP(final String pInitFile) {
		LOG.info("Read init file");
		weather = Weather.getInstance(WeatherType.HOT);
		initFile = ExpandedSingletonInitFile.getInstance(pInitFile);
		initContext = ContextSingleton.getInstance();
		fDayFileNames = new String[ExpandedSingletonInitFile.getNumberOfDaysMarch()];
		
		for (int dayofmarch = 0; dayofmarch < ExpandedSingletonInitFile.getNumberOfDaysMarch(); dayofmarch++) {
			LOG.info("Day " + dayofmarch + " of " + ExpandedSingletonInitFile.getNumberOfDaysMarch());
			ContextSingleton.setDay(dayofmarch);
			initialiseOutputFiles(dayofmarch);
			createAgents(dayofmarch);
			terrainLogging(dayofmarch);
			runSimulation(dayofmarch);
			resetSimulation(dayofmarch);
		}
		LOG.info("End of final simulation");
	}

	private void resetSimulation(int dayofmarch) {
		ContextSingleton.resetContext();
		try {
			fOutputDayFile.close();
			fOutputTickFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void runSimulation(int dayofmarch) {
		// TODO Auto-generated method stub
		LOG.info("Starting simulation of day " + dayofmarch);
		for (int i = 1; i <= ExpandedSingletonInitFile.getEndTime(); i++) {
			LOG.info("Starting step " + i);
			ContextSingleton.step(i);
			for (MWGridAgent thisAgent : fAllAgents) {
				thisAgent.step(i);
			}
			for (MWGridAgent thisAgent : fAllAgents) {
				collectReport(thisAgent);
			}
		}
		LOG.info("End of simulation day " + dayofmarch);
	}

	private void terrainLogging(int dayofmarch) {
		//If terrain logging on, write terrain file
		if (ExpandedSingletonInitFile.getTerrainLogging()) {
			final Environment ENVIRONMENT =
					PartEnvHeightOnlyImplementation.getInstance();
			int tbs = ExpandedSingletonInitFile.getTerrainBorderSize();

			final int startX = ExpandedSingletonInitFile.getStartLocation(dayofmarch).getX();
			final int startY = ExpandedSingletonInitFile.getStartLocation(dayofmarch).getY();
			int destX = ExpandedSingletonInitFile.getDestinationLocation(dayofmarch).getX();
			int destY = ExpandedSingletonInitFile.getDestinationLocation(dayofmarch).getY();
			int terrMaxX, terrMaxY, terrMinX, terrMinY;
			
			if (startX >= destX) {
				terrMaxX = startX;
			} else {
				terrMaxX = destX;
			}

			if (startY >= destY) {
				terrMaxY = startY;
			} else {
				terrMaxY = destY;
			}

			if (startX <= destX) {
				terrMinX = startX;
			} else {
				terrMinX = destX;
			}

			if (startY <= destY) {
				terrMinY = startY;
			} else {
				terrMinY = destY;
			}

			terrMaxX = terrMaxX + tbs;
			terrMaxY = terrMaxY + tbs;
			terrMinX = terrMinX - tbs;
			terrMinY = terrMinY - tbs;
			
			if (terrMinX % 10 != 0) {
				terrMinX = terrMinX - (terrMinX % 10);
			}
			if (terrMinY % 10 != 0) {
				terrMinY = terrMinY - (terrMinY % 10);
			}
			if (terrMaxX % 10 != 0) {
				terrMaxX = terrMaxX - (terrMaxX % 10);
			}
			if (terrMaxY % 10 != 0) {
				terrMaxY = terrMaxY - (terrMaxY % 10);
			}

			LOG.info("Writing terrain file from " + terrMinX + "," + terrMinY + " to " + terrMaxX + "," + terrMaxY);
			try {
				this.fOutputTerrainFile.write(terrMinX + " " + terrMinY + " " + (int)((terrMaxX - terrMinX) / 10  + 1) + " " + (int) ((terrMaxY - terrMinY) / 10 + 1));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			for (int y = terrMinY; y <= terrMaxY; y = y + 10) {
				try {
					this.fOutputTerrainFile.newLine();
					this.fOutputTerrainFile.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for (int x = terrMinX; x <= terrMaxX; x = x + 10) {
					Location thisloc = new Location(x,y);
					final Value<?> thisHeightVal =
							ENVIRONMENT.getEnvironmentValue(thisloc,
									EnvironmentVariables.HEIGHT);
					int thisheight = (Integer) thisHeightVal.get();
					LOG.finest("Trying to write " + x + "," + y + " " + thisheight);
					try {
						this.fOutputTerrainFile.write(thisheight + " ");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			try {
				this.fOutputTerrainFile.newLine();
				this.fOutputTerrainFile.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				fOutputTerrainFile.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void createAgents(int dayofmarch) {
		final int officers = ExpandedSingletonInitFile.getOfficers();
		final int officersquads = ExpandedSingletonInitFile.getOfficerCavalrySquads();
		final int officersquadsize = ExpandedSingletonInitFile.getOfficerCavalrySquadSize();
		final int campspacebetweensquads = ExpandedSingletonInitFile.getCampSpaceBetweenSquads();

		int officerCount = 0;
		int officerSquadCount = 0;
		int spacing = 0;

		final int outsideSoldierTotal = ExpandedSingletonInitFile.getCavalrySquads() + ExpandedSingletonInitFile.getInfantrySquads();
		final int outsideBaggageTotal = ExpandedSingletonInitFile.getBaggageTot();

		LOG.info("Checking for previous day's march info");
		if (dayofmarch > 1) {
			String ssfilename = initFile.getResourceLoc() + ExpandedSingletonInitFile.getOutputDayFilename(dayofmarch) + (dayofmarch - 1) + ".txt";
			LOG.finest("Finding Previous Dayfile" + ssfilename);
			try {
				final BufferedReader reader =
						new BufferedReader(new FileReader(ssfilename));

				String dataLine = null;
				for (int row = 0; (dataLine = reader.readLine()) != null; row++) {
					LOG.finest(dataLine);
					final String[] tokens = dataLine.split("\\s+");
					if (tokens.length == 6) {
						final String thisParam = tokens[0];
						final String thisVal = tokens[1];
						LOG.info("First two readings are " + thisParam + " " + thisVal);
					}
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		LOG.info("Creating agents");
		this.fAllAgents = new ArrayList<MWGridAgent>();
		agentObjectID = 1;
		unitID = 1;
		
		int largestsectormulesquads = 0;
		
		if (ExpandedSingletonInitFile.getSectorCMuleSquads() > largestsectormulesquads) {
			largestsectormulesquads = ExpandedSingletonInitFile.getSectorCMuleSquads();
		}

		if (ExpandedSingletonInitFile.getSectorNMuleSquads() > largestsectormulesquads) {
			largestsectormulesquads = ExpandedSingletonInitFile.getSectorNMuleSquads();
		}

		if (ExpandedSingletonInitFile.getSectorWMuleSquads() > largestsectormulesquads) {
			largestsectormulesquads = ExpandedSingletonInitFile.getSectorWMuleSquads();
		}
		
		if (ExpandedSingletonInitFile.getSectorEMuleSquads() > largestsectormulesquads) {
			largestsectormulesquads = ExpandedSingletonInitFile.getSectorEMuleSquads();
		}

		if (ExpandedSingletonInitFile.getSectorSMuleSquads() > largestsectormulesquads) {
			largestsectormulesquads = ExpandedSingletonInitFile.getSectorSMuleSquads();
		}

		LOG.info("Calculating largest sector");
		if (officers + officersquads + largestsectormulesquads > (outsideSoldierTotal / 4) + largestsectormulesquads && officers + officersquads + largestsectormulesquads > (outsideBaggageTotal / 4)) {
			largestSector = (int) (officers + officersquads + largestsectormulesquads);
			LOG.info("Largest sector is Officers");
		} else if (outsideSoldierTotal + largestsectormulesquads > outsideBaggageTotal){
			largestSector = (outsideSoldierTotal / 4) + largestsectormulesquads;
			LOG.info("Largest sector is Troops");
		} else {
			largestSector = outsideBaggageTotal / 4;
			LOG.info("Largest sector is Baggage");
		}

		radiusOfLargestSquare = (int) ((0.5 * Math.sqrt(largestSector)) * campspacebetweensquads);
		int lrgout = 0;
		if (outsideBaggageTotal > outsideSoldierTotal) {
			lrgout = outsideBaggageTotal / 4;
		} else {
			lrgout = (outsideSoldierTotal / 4) + largestsectormulesquads;
		}
		radiusOfOuterSectors = (int) ((0.5 * Math.sqrt(lrgout)) * campspacebetweensquads);
		LOG.info("Radius of each camp spot is " + radiusOfLargestSquare);

		LOG.info("Creating Officer Sector");
		final int radiusOfOfficerSector = (int) ((0.5 * Math.sqrt(officers + officersquads + ExpandedSingletonInitFile.getSectorCMuleSquads() + 1)) * campspacebetweensquads);
		LOG.info("Radius of Officer sector is " + radiusOfOfficerSector);
		final int startX = ExpandedSingletonInitFile.getStartLocation(ContextSingleton.getDay()).getX();
		final int startY = ExpandedSingletonInitFile.getStartLocation(ContextSingleton.getDay()).getY();
		minX = startX;
		minY = startY;
		maxX = startX;
		maxY = startY;


		LOG.info("Creating Officer agents");
		int offsecmul = ExpandedSingletonInitFile.getSectorCMuleSquads();
		int offmul = 0;
		
		for (int locX = startX + radiusOfOfficerSector; locX >= startX - radiusOfOfficerSector; locX = locX - (int) campspacebetweensquads) {
			for (int locY = startY + radiusOfOfficerSector; locY >= startY - radiusOfOfficerSector; locY = locY - (int) campspacebetweensquads) {
				if (locX > maxX) {
					maxX = locX;
				} else if (locX < minX) {
					minX = locX;
				}
				if (locY > maxY) {
					maxY = locY;
				} else if (locY < minY) {
					minY = locY;
				}
				if (agentObjectID == 1) {
					LOG.info("Adding ColumnLeader at loc " + locX + ":" + locY);
					this.fAllAgents.add(LocalObjectFactory.createColumnLeader(new Location(locX, locY), agentObjectID));
					agentObjectID++;
				} else {
					if (officerCount < officers) {
						LOG.finest("Adding Officer at loc " + locX + ":" + locY);
						if (unitID % ExpandedSingletonInitFile.getSecondaryUnitSize() == 0) {
							spacing = ExpandedSingletonInitFile.getSecondarySetoffSpacing();
						} else {
							spacing = ExpandedSingletonInitFile.getSetoffSpacing();
						}
						this.fAllAgents.add(LocalObjectFactory.createCavalryOfficer(new Location(locX, locY), unitID, agentObjectID, spacing, 0));
						agentObjectID++;
						unitID++;
						officerCount++;
					} else if (officerSquadCount < officersquads) {
						LOG.finest("Adding Cavalry Squad Officer at loc " + locX + ":" + locY);
						if (unitID % ExpandedSingletonInitFile.getSecondaryUnitSize() == 0) {
							spacing = ExpandedSingletonInitFile.getSecondarySetoffSpacing();
						} else {
							spacing = ExpandedSingletonInitFile.getSetoffSpacing();
						}

						this.fAllAgents.add(LocalObjectFactory.createCavalryOfficer(new Location(locX, locY), unitID, agentObjectID, spacing, 0));
						agentObjectID++;
						for (int j = 1; j <= officersquadsize; j++) {
							LOG.finest("Adding Officer Cavalry Squad Soldier at loc " + locX + ":" + locY);
							this.fAllAgents.add(LocalObjectFactory.createCavalrySoldier(
									new Location(locX, locY), unitID, agentObjectID));
							agentObjectID++;
						}
						if (ExpandedSingletonInitFile.getSquadMule() && !ExpandedSingletonInitFile.getSquadMode()) {
							LOG.finest("Adding Squad Mule at loc " + locX + ":" + locY);
							this.fAllAgents.add(LocalObjectFactory.createMule(new Location(locX, locY), unitID, agentObjectID, ClassType.CAVALRY_SOLDIER.getSpeed()));
							agentObjectID++;
						}
						officerSquadCount++;
						unitID++;
					} else if (offmul < offsecmul) {
						LOG.info("Adding Mule Officer at loc " + locX + ":" + locY);

						spacing = ExpandedSingletonInitFile.getSetoffSpacing();

						this.fAllAgents.add(LocalObjectFactory.createBaggageHandler(new Location(locX, locY), unitID, agentObjectID, spacing, 0, ClassType.MULE.getSpeed()));
//						this.fAllAgents.add(LocalObjectFactory.createCavalryOfficer(new Location(locX, locY), unitID, agentObjectID, spacing, 0));
						agentObjectID++;

						for (int j = 1; j <= ExpandedSingletonInitFile.getMuleSquadSize(); j++) {
							LOG.finest("Adding Mule at loc " + locX + ":" + locY);
							this.fAllAgents.add(LocalObjectFactory.createMule(new Location(locX, locY), unitID, agentObjectID));
							agentObjectID++;
						}

						offmul++;
						unitID++;
					} 
				}
			}
		}

		final CampNeighbours direction = ContextSingleton.getCampDirection(ExpandedSingletonInitFile.getStartLocation(ContextSingleton.getDay()), ExpandedSingletonInitFile.getDestinationLocation(dayofmarch));
		colLdrNumber = 2;

		final int sectorTot = outsideSoldierTotal / 4;
		final int bagtot = ExpandedSingletonInitFile.getMuleSquads() + ExpandedSingletonInitFile.getDonkeySquads() + ExpandedSingletonInitFile.getHorseSquads() + ExpandedSingletonInitFile.getCamelSquads() + ExpandedSingletonInitFile.getCartSquads();
		int bagsecTot = bagtot / 4;
		final int[] inf = new int[4];
		final int[] cav = new int[4];
		final int[] mul = new int[4];
		final int[] don = new int[4];
		final int[] hor = new int[4];
		final int[] cam = new int[4];
		final int[] car = new int[4];
		int cavsofar = 0;
		int mulsofar = 0;
		int donsofar = 0;
		int horsofar = 0;
		int camsofar = 0;
		int carsofar = 0;


		for (int i = 0; i < 4; i++) {
			inf[i] = 0;
			cav[i] = 0;
			if (cavsofar < ExpandedSingletonInitFile.getCavalrySquads()) {
				if (cavsofar + sectorTot <= ExpandedSingletonInitFile.getCavalrySquads()) {
					cav[i] = sectorTot;
					cavsofar = cavsofar + sectorTot;
				} else {
					cav[i] = ExpandedSingletonInitFile.getCavalrySquads() - cavsofar;
					inf[i] = sectorTot - cav[i];
					cavsofar = ExpandedSingletonInitFile.getCavalrySquads();
				}
			} else {				
				inf[i] = sectorTot;
			}

		}

		
		//Sweep for fractions. If inf then add to last sector and if cav then add to first
		int tempinftot = 0;
		for (int x = 0; x < 4; x++) {
			tempinftot = tempinftot + inf[x];
		}
		if (tempinftot < ExpandedSingletonInitFile.getInfantrySquads()) {
			inf[3] = inf[3] + (ExpandedSingletonInitFile.getInfantrySquads() - tempinftot);
		}

		int tempcavtot = 0;
		for (int x = 0; x < 4; x++) {
			tempcavtot = tempcavtot + cav[x];
		}
		if (tempcavtot < ExpandedSingletonInitFile.getCavalrySquads()) {
			cav[0] = cav[0] + (ExpandedSingletonInitFile.getCavalrySquads() - tempcavtot);
		}

		//Determine how many baggage squads go in each sector
		for (int k = 0; k < 4; k++) {
			mul[k] = 0;
			don[k] = 0;
			hor[k] = 0;
			cam[k] = 0;
			car[k] = 0;
			int bagsofar = 0;

			//Setting bagsecTot unrealistically high ensures any remaining squads get added in the final sector
			if (k == 3) {
				bagsecTot = 999999999;
			}

			//These have to go in speed order or the column won't work properly
			while (mulsofar < ExpandedSingletonInitFile.getMuleSquads() && bagsofar < bagsecTot) {
				mul[k]++;
				mulsofar++;
				bagsofar++;
			}
			while (horsofar < ExpandedSingletonInitFile.getHorseSquads() && bagsofar < bagsecTot) {
				hor[k]++;
				horsofar++;
				bagsofar++;
			}
			while (carsofar < ExpandedSingletonInitFile.getCartSquads() && bagsofar < bagsecTot) {
				car[k]++;
				carsofar++;
				bagsofar++;
			}
			while (camsofar < ExpandedSingletonInitFile.getCamelSquads() && bagsofar < bagsecTot) {
				cam[k]++;
				camsofar++;
				bagsofar++;
			}
			while (donsofar < ExpandedSingletonInitFile.getDonkeySquads() && bagsofar < bagsecTot) {
				don[k]++;
				donsofar++;
				bagsofar++;
			}

			LOG.info("Baggagesector " + k + " has " + mul[k] + " mule, " + don[k] + " donkey, " + hor[k] + " horse, " + cam[k] + " camel, " + car[k] + " cart squads.");
		}
		
		int sec1mule = ExpandedSingletonInitFile.getSectorNMuleSquads();
		int sec2mule = ExpandedSingletonInitFile.getSectorWMuleSquads();
		int sec3mule = ExpandedSingletonInitFile.getSectorEMuleSquads();
		int sec4mule = ExpandedSingletonInitFile.getSectorSMuleSquads();

		if (direction == CampNeighbours.RIGHT) {
			if (ExpandedSingletonInitFile.getColumns() == 1) {
				createSector(0, cav[0], inf[0], sec3mule, 0, 0, 0, 0, 3);
				createSector(0, cav[1], inf[1], sec4mule, 0, 0, 0, 0, 4);
				createSector(0, cav[2], inf[2], sec1mule, 0, 0, 0, 0, 1);
				createSector(0, cav[3], inf[3], sec2mule, 0, 0, 0, 0, 2);
				createSector(0, 0, 0, mul[0], don[0], hor[0], cam[0], car[0], 6);
				createSector(0, 0, 0, mul[1], don[1], hor[1], cam[1], car[1], 8);
				createSector(0, 0, 0, mul[2], don[2], hor[2], cam[2], car[2], 5);
				createSector(0, 0, 0, mul[3], don[3], hor[3], cam[3], car[3], 7);
			} else if (ExpandedSingletonInitFile.getColumns() == 2) {
				createSector(0, cav[0], inf[0], sec3mule, 0, 0, 0, 0, 3);
				createSector(0, cav[1], inf[1], sec1mule, 0, 0, 0, 0, 1);
				createSector(0, 0, 0, mul[0], don[0], hor[0], cam[0], car[0], 6);
				createSector(0, 0, 0, mul[1], don[1], hor[1], cam[1], car[1], 8);
				createSector(1, cav[2], inf[2], sec4mule, 0, 0, 0, 0, 4);
				createSector(0, cav[3], inf[3], sec2mule, 0, 0, 0, 0, 2);
				createSector(0, 0, 0, mul[2], don[2], hor[2], cam[2], car[2], 5);
				createSector(0, 0, 0, mul[3], don[3], hor[3], cam[3], car[3], 7);
			} else if (ExpandedSingletonInitFile.getColumns() == 3) {
				createSector(0, 0, 0, mul[0], don[0], hor[0], cam[0], car[0], 6);
				createSector(0, 0, 0, mul[1], don[1], hor[1], cam[1], car[1], 8);
				createSector(1, cav[0], inf[0], sec4mule, 0, 0, 0, 0, 4);
				createSector(0, cav[1], inf[1], sec2mule, 0, 0, 0, 0, 2);
				createSector(0, 0, 0, mul[2], don[2], hor[2], cam[2], car[2], 7);
				createSector(1, cav[2], inf[2], sec3mule, 0, 0, 0, 0, 3);
				createSector(0, cav[3], inf[3], sec1mule, 0, 0, 0, 0, 1);
				createSector(0, 0, 0, mul[3], don[3], hor[3], cam[3], car[3], 5);
			}
		} else if (direction == CampNeighbours.DOWN_RIGHT) {
			if (ExpandedSingletonInitFile.getColumns() == 1) {
				createSector(0, cav[0], inf[0], sec3mule, 0, 0, 0, 0, 3);
				createSector(0, cav[1], inf[1], sec4mule, 0, 0, 0, 0, 4);
				createSector(0, cav[2], inf[2], sec1mule, 0, 0, 0, 0, 1);
				createSector(0, cav[3], inf[3], sec2mule, 0, 0, 0, 0, 2);
				createSector(0, 0, 0, mul[0], don[0], hor[0], cam[0], car[0], 8);
				createSector(0, 0, 0, mul[1], don[1], hor[1], cam[1], car[1], 6);
				createSector(0, 0, 0, mul[2], don[2], hor[2], cam[2], car[2], 7);
				createSector(0, 0, 0, mul[3], don[3], hor[3], cam[3], car[3], 5);
			} else if (ExpandedSingletonInitFile.getColumns() == 2) {
				createSector(0, cav[0], inf[0], sec3mule, 0, 0, 0, 0, 3);
				createSector(0, cav[1], inf[1], sec4mule, 0, 0, 0, 0, 4);
				createSector(0, 0, 0, mul[0], don[0], hor[0], cam[0], car[0], 8);
				createSector(0, 0, 0, mul[1], don[1], hor[1], cam[1], car[1], 6);
				createSector(1, cav[2], inf[2], sec1mule, 0, 0, 0, 0, 1);
				createSector(0, cav[3], inf[3], sec2mule, 0, 0, 0, 0, 2);
				createSector(0, 0, 0, mul[2], don[2], hor[2], cam[2], car[2], 7);
				createSector(0, 0, 0, mul[3], don[3], hor[3], cam[3], car[3], 5);
			} else if (ExpandedSingletonInitFile.getColumns() == 3) {
				createSector(0, 0, 0, mul[0], don[0], hor[0], cam[0], car[0], 8);
				createSector(0, 0, 0, mul[1], don[1], hor[1], cam[1], car[1], 6);
				createSector(1, cav[0], inf[0], sec3mule, 0, 0, 0, 0, 3);
				createSector(0, cav[1], inf[1], sec1mule, 0, 0, 0, 0, 1);
				createSector(0, 0, 0, mul[2], don[2], hor[2], cam[2], car[2], 5);
				createSector(1, cav[2], inf[2], sec4mule, 0, 0, 0, 0, 4);
				createSector(0, cav[3], inf[3], sec2mule, 0, 0, 0, 0, 2);
				createSector(0, 0, 0, mul[3], don[3], hor[3], cam[3], car[3], 7);
			}
		} else if (direction == CampNeighbours.DOWN) {
			if (ExpandedSingletonInitFile.getColumns() == 1) {
				createSector(0, cav[0], inf[0], sec4mule, 0, 0, 0, 0, 4);
				createSector(0, cav[1], inf[1], sec3mule, 0, 0, 0, 0, 3);
				createSector(0, cav[2], inf[2], sec2mule, 0, 0, 0, 0, 2);
				createSector(0, cav[3], inf[3], sec1mule, 0, 0, 0, 0, 1);
				createSector(0, 0, 0, mul[0], don[0], hor[0], cam[0], car[0], 7);
				createSector(0, 0, 0, mul[1], don[1], hor[1], cam[1], car[1], 8);
				createSector(0, 0, 0, mul[2], don[2], hor[2], cam[2], car[2], 5);
				createSector(0, 0, 0, mul[3], don[3], hor[3], cam[3], car[3], 6);
			} else if (ExpandedSingletonInitFile.getColumns() == 2) {
				createSector(0, cav[0], inf[0], sec4mule, 0, 0, 0, 0, 4);
				createSector(0, cav[1], inf[1], sec3mule, 0, 0, 0, 0, 3);
				createSector(0, 0, 0, mul[0], don[0], hor[0], cam[0], car[0], 8);
				createSector(0, 0, 0, mul[1], don[1], hor[1], cam[1], car[1], 6);
				createSector(1, cav[2], inf[2], sec2mule, 0, 0, 0, 0, 2);
				createSector(0, cav[3], inf[3], sec1mule, 0, 0, 0, 0, 1);
				createSector(0, 0, 0, mul[2], don[2], hor[2], cam[2], car[2], 7);
				createSector(0, 0, 0, mul[3], don[3], hor[3], cam[3], car[3], 5);
			} else if (ExpandedSingletonInitFile.getColumns() == 3) {
				createSector(0, 0, 0, mul[0], don[0], hor[0], cam[0], car[0], 7);
				createSector(0, 0, 0, mul[1], don[1], hor[1], cam[1], car[1], 8);
				createSector(1, cav[0], inf[0], sec4mule, 0, 0, 0, 0, 4);
				createSector(0, cav[1], inf[1], sec3mule, 0, 0, 0, 0, 3);
				createSector(0, 0, 0, mul[3], don[3], hor[3], cam[3], car[3], 6);
				createSector(1, cav[2], inf[2], sec2mule, 0, 0, 0, 0, 2);
				createSector(0, cav[3], inf[3], sec1mule, 0, 0, 0, 0, 1);
				createSector(0, 0, 0, mul[2], don[2], hor[2], cam[2], car[2], 5);
			}
		} else if (direction == CampNeighbours.DOWN_LEFT) {
			if (ExpandedSingletonInitFile.getColumns() == 1) {
				createSector(0, cav[0], inf[0], sec4mule, 0, 0, 0, 0, 4);
				createSector(0, cav[1], inf[1], sec2mule, 0, 0, 0, 0, 2);
				createSector(0, cav[2], inf[2], sec3mule, 0, 0, 0, 0, 3);
				createSector(0, cav[3], inf[3], sec1mule, 0, 0, 0, 0, 1);
				createSector(0, 0, 0, mul[0], don[0], hor[0], cam[0], car[0], 7);
				createSector(0, 0, 0, mul[1], don[1], hor[1], cam[1], car[1], 8);
				createSector(0, 0, 0, mul[2], don[2], hor[2], cam[2], car[2], 5);
				createSector(0, 0, 0, mul[3], don[3], hor[3], cam[3], car[3], 6);
			} else if (ExpandedSingletonInitFile.getColumns() == 2) {
				createSector(0, cav[0], inf[0], sec4mule, 0, 0, 0, 0, 4);
				createSector(0, cav[1], inf[1], sec3mule, 0, 0, 0, 0, 3);
				createSector(0, 0, 0, mul[0], don[0], hor[0], cam[0], car[0], 7);
				createSector(0, 0, 0, mul[1], don[1], hor[1], cam[1], car[1], 8);
				createSector(1, cav[2], inf[2], sec2mule, 0, 0, 0, 0, 2);
				createSector(0, cav[3], inf[3], sec1mule, 0, 0, 0, 0, 1);
				createSector(0, 0, 0, mul[2], don[2], hor[2], cam[2], car[2], 5);
				createSector(0, 0, 0, mul[3], don[3], hor[3], cam[3], car[3], 6);
			} else if (ExpandedSingletonInitFile.getColumns() == 3) {
				createSector(0, 0, 0, mul[0], don[0], hor[0], cam[0], car[0], 7);
				createSector(0, 0, 0, mul[1], don[1], hor[1], cam[1], car[1], 8);
				createSector(1, cav[0], inf[0], sec4mule, 0, 0, 0, 0, 4);
				createSector(0, cav[1], inf[1], sec3mule, 0, 0, 0, 0, 3);
				createSector(0, 0, 0, mul[2], don[2], hor[2], cam[2], car[2], 6);
				createSector(1, cav[2], inf[2], sec2mule, 0, 0, 0, 0, 2);
				createSector(0, cav[3], inf[3], sec1mule, 0, 0, 0, 0, 1);
				createSector(0, 0, 0, mul[3], don[3], hor[3], cam[3], car[3], 5);
			}
		} else if (direction == CampNeighbours.LEFT) {
			if (ExpandedSingletonInitFile.getColumns() == 1) {
				createSector(0, cav[0], inf[0], sec2mule, 0, 0, 0, 0, 2);
				createSector(0, cav[1], inf[1], sec1mule, 0, 0, 0, 0, 1);
				createSector(0, cav[2], inf[2], sec4mule, 0, 0, 0, 0, 4);
				createSector(0, cav[3], inf[3], sec3mule, 0, 0, 0, 0, 3);
				createSector(0, 0, 0, mul[0], don[0], hor[0], cam[0], car[0], 7);
				createSector(0, 0, 0, mul[1], don[1], hor[1], cam[1], car[1], 5);
				createSector(0, 0, 0, mul[2], don[2], hor[2], cam[2], car[2], 8);
				createSector(0, 0, 0, mul[3], don[3], hor[3], cam[3], car[3], 6);
			} else if (ExpandedSingletonInitFile.getColumns() == 2) {
				createSector(0, cav[0], inf[0], sec2mule, 0, 0, 0, 0, 2);
				createSector(0, cav[1], inf[1], sec1mule, 0, 0, 0, 0, 1);
				createSector(0, 0, 0, mul[0], don[0], hor[0], cam[0], car[0], 7);
				createSector(0, 0, 0, mul[1], don[1], hor[1], cam[1], car[1], 5);
				createSector(1, cav[2], inf[2], sec4mule, 0, 0, 0, 0, 4);
				createSector(0, cav[3], inf[3], sec3mule, 0, 0, 0, 0, 3);
				createSector(0, 0, 0, mul[2], don[2], hor[2], cam[2], car[2], 8);
				createSector(0, 0, 0, mul[3], don[3], hor[3], cam[3], car[3], 6);
			} else if (ExpandedSingletonInitFile.getColumns() == 3) {
				createSector(0, 0, 0, mul[0], don[0], hor[0], cam[0], car[0], 7);
				createSector(0, 0, 0, mul[1], don[1], hor[1], cam[1], car[1], 5);
				createSector(1, cav[0], inf[0], sec1mule, 0, 0, 0, 0, 1);
				createSector(0, cav[1], inf[1], sec3mule, 0, 0, 0, 0, 3);
				createSector(0, 0, 0, mul[2], don[2], hor[2], cam[2], car[2], 6);
				createSector(1, cav[2], inf[2], sec2mule, 0, 0, 0, 0, 2);
				createSector(0, cav[3], inf[3], sec4mule, 0, 0, 0, 0, 4);
				createSector(0, 0, 0, mul[3], don[3], hor[3], cam[3], car[3], 8);
			}
		} else if (direction == CampNeighbours.UP_LEFT) {
			if (ExpandedSingletonInitFile.getColumns() == 1) {
				createSector(0, cav[0], inf[0], sec2mule, 0, 0, 0, 0, 2);
				createSector(0, cav[1], inf[1], sec1mule, 0, 0, 0, 0, 1);
				createSector(0, cav[2], inf[2], sec4mule, 0, 0, 0, 0, 4);
				createSector(0, cav[3], inf[3], sec3mule, 0, 0, 0, 0, 3);
				createSector(0, 0, 0, mul[0], don[0], hor[0], cam[0], car[0], 5);
				createSector(0, 0, 0, mul[1], don[1], hor[1], cam[1], car[1], 6);
				createSector(0, 0, 0, mul[2], don[2], hor[2], cam[2], car[2], 7);
				createSector(0, 0, 0, mul[3], don[3], hor[3], cam[3], car[3], 8);
			} else if (ExpandedSingletonInitFile.getColumns() == 2) {
				createSector(0, cav[0], inf[0], sec2mule, 0, 0, 0, 0, 2);
				createSector(0, cav[1], inf[1], sec4mule, 0, 0, 0, 0, 4);
				createSector(0, 0, 0, mul[0], don[0], hor[0], cam[0], car[0], 7);
				createSector(0, 0, 0, mul[1], don[1], hor[1], cam[1], car[1], 8);
				createSector(1, cav[2], inf[2], sec1mule, 0, 0, 0, 0, 1);
				createSector(0, cav[3], inf[3], sec3mule, 0, 0, 0, 0, 3);
				createSector(0, 0, 0, mul[2], don[2], hor[2], cam[2], car[2], 5);
				createSector(0, 0, 0, mul[3], don[3], hor[3], cam[3], car[3], 6);
			} else if (ExpandedSingletonInitFile.getColumns() == 3) {
				createSector(0, 0, 0, mul[0], don[0], hor[0], cam[0], car[0], 5);
				createSector(0, 0, 0, mul[1], don[1], hor[1], cam[1], car[1], 6);
				createSector(1, cav[0], inf[0], sec2mule, 0, 0, 0, 0, 2);
				createSector(0, cav[1], inf[1], sec4mule, 0, 0, 0, 0, 4);
				createSector(0, 0, 0, mul[2], don[2], hor[2], cam[2], car[2], 7);
				createSector(1, cav[2], inf[2], sec1mule, 0, 0, 0, 0, 1);
				createSector(0, cav[3], inf[3], sec3mule, 0, 0, 0, 0, 3);
				createSector(0, 0, 0, mul[3], don[3], hor[3], cam[3], car[3], 8);
			}
		} else if (direction == CampNeighbours.UP) {
			if (ExpandedSingletonInitFile.getColumns() == 1) {
				createSector(0, cav[0], inf[0], sec1mule, 0, 0, 0, 0, 1);
				createSector(0, cav[1], inf[1], sec2mule, 0, 0, 0, 0, 2);
				createSector(0, cav[2], inf[2], sec3mule, 0, 0, 0, 0, 3);
				createSector(0, cav[3], inf[3], sec4mule, 0, 0, 0, 0, 4);
				createSector(0, 0, 0, mul[0], don[0], hor[0], cam[0], car[0], 5);
				createSector(0, 0, 0, mul[1], don[1], hor[1], cam[1], car[1], 6);
				createSector(0, 0, 0, mul[2], don[2], hor[2], cam[2], car[2], 7);
				createSector(0, 0, 0, mul[3], don[3], hor[3], cam[3], car[3], 8);
			} else if (ExpandedSingletonInitFile.getColumns() == 2) {
				createSector(0, cav[0], inf[0], sec2mule, 0, 0, 0, 0, 2);
				createSector(0, cav[1], inf[1], sec4mule, 0, 0, 0, 0, 4);
				createSector(0, 0, 0, mul[0], don[0], hor[0], cam[0], car[0], 5);
				createSector(0, 0, 0, mul[1], don[1], hor[1], cam[1], car[1], 7);
				createSector(1, cav[2], inf[2], sec1mule, 0, 0, 0, 0, 1);
				createSector(0, cav[3], inf[3], sec3mule, 0, 0, 0, 0, 3);
				createSector(0, 0, 0, mul[2], don[2], hor[2], cam[2], car[2], 6);
				createSector(0, 0, 0, mul[3], don[3], hor[3], cam[3], car[3], 8);
			} else if (ExpandedSingletonInitFile.getColumns() == 3) {
				createSector(0, 0, 0, mul[0], don[0], hor[0], cam[0], car[0], 5);
				createSector(0, 0, 0, mul[1], don[1], hor[1], cam[1], car[1], 6);
				createSector(1, cav[0], inf[0], sec1mule, 0, 0, 0, 0, 1);
				createSector(0, cav[1], inf[1], sec2mule, 0, 0, 0, 0, 2);
				createSector(0, 0, 0, mul[2], don[2], hor[2], cam[2], car[2], 7);
				createSector(1, cav[2], inf[2], sec3mule, 0, 0, 0, 0, 3);
				createSector(0, cav[3], inf[3], sec4mule, 0, 0, 0, 0, 4);
				createSector(0, 0, 0, mul[3], don[3], hor[3], cam[3], car[3], 8);
			}
		} else if (direction == CampNeighbours.UP_RIGHT) {
			if (ExpandedSingletonInitFile.getColumns() == 1) {
				createSector(0, cav[0], inf[0], sec1mule, 0, 0, 0, 0, 1);
				createSector(0, cav[1], inf[1], sec3mule, 0, 0, 0, 0, 3);
				createSector(0, cav[2], inf[2], sec2mule, 0, 0, 0, 0, 2);
				createSector(0, cav[3], inf[3], sec4mule, 0, 0, 0, 0, 4);
				createSector(0, 0, 0, mul[0], don[0], hor[0], cam[0], car[0], 6);
				createSector(0, 0, 0, mul[1], don[1], hor[1], cam[1], car[1], 5);
				createSector(0, 0, 0, mul[2], don[2], hor[2], cam[2], car[2], 8);
				createSector(0, 0, 0, mul[3], don[3], hor[3], cam[3], car[3], 7);
			} else if (ExpandedSingletonInitFile.getColumns() == 2) {
				createSector(0, cav[0], inf[0], sec2mule, 0, 0, 0, 0, 3);
				createSector(0, cav[1], inf[1], sec4mule, 0, 0, 0, 0, 1);
				createSector(0, 0, 0, mul[0], don[0], hor[0], cam[0], car[0], 6);
				createSector(0, 0, 0, mul[1], don[1], hor[1], cam[1], car[1], 5);
				createSector(1, cav[2], inf[2], sec3mule, 0, 0, 0, 0, 2);
				createSector(0, cav[3], inf[3], sec1mule, 0, 0, 0, 0, 4);
				createSector(0, 0, 0, mul[2], don[2], hor[2], cam[2], car[2], 8);
				createSector(0, 0, 0, mul[3], don[3], hor[3], cam[3], car[3], 7);
			} else if (ExpandedSingletonInitFile.getColumns() == 3) {
				createSector(0, 0, 0, mul[0], don[0], hor[0], cam[0], car[0], 6);
				createSector(0, 0, 0, mul[1], don[1], hor[1], cam[1], car[1], 5);
				createSector(1, cav[0], inf[0], sec1mule, 0, 0, 0, 0, 1);
				createSector(0, cav[1], inf[1], sec2mule, 0, 0, 0, 0, 2);
				createSector(0, 0, 0, mul[2], don[2], hor[2], cam[2], car[2], 7);
				createSector(1, cav[2], inf[2], sec3mule, 0, 0, 0, 0, 3);
				createSector(0, cav[3], inf[3], sec4mule, 0, 0, 0, 0, 4);
				createSector(0, 0, 0, mul[3], don[3], hor[3], cam[3], car[3], 8);
			}
		} else {
			LOG.info("Direction ERROR!");
		}
		ContextSingleton.loadAgents(fAllAgents);
	}

	private void initialiseOutputFiles(final int pDay) {
		LOG.info("Initialising output files");
		try {
			this.fOutputTickFile =
					new BufferedWriter(new FileWriter(new File(
							initFile.getResourceLoc()
							+ ExpandedSingletonInitFile.getOutputTickFilename(pDay))));
			this.fOutputDayFile =
					new BufferedWriter(new FileWriter(new File(
							initFile.getResourceLoc()
							+ ExpandedSingletonInitFile.getOutputDayFilename(pDay))));
			if (ExpandedSingletonInitFile.getTerrainLogging()) {
				this.fOutputTerrainFile =
						new BufferedWriter(new FileWriter(new File(
								initFile.getResourceLoc()
								+ ExpandedSingletonInitFile.getOutputTerrainFilename(pDay))));
			}
		} catch (final IOException e) {
			LOG.severe("IOException caught while initialising output file");
			e.printStackTrace();
			System.exit(1);
		}
		fDayFileNames[pDay] = ExpandedSingletonInitFile.getOutputDayFilename(pDay);
	}

	private void createSector(final int pColLdrs, final int pCavSquads, final int pInfSquads, final int pMulSquads, final int pDonSquads, final int pHorSquads, final int pCamSquads, final int pCarSquads, final int pSector) {
		int startX;
		int startY;
		int cavcount = 0;
		int infcount = 0;
		int mulcount = 0;
		int doncount = 0;
		int horcount = 0;
		int camcount = 0;
		int carcount = 0;
		int bagcount = 0;
		int spacing = 0;
		int columnleaders = pColLdrs;
		final int gapbetweensectors = ExpandedSingletonInitFile.getGapBetweenSectors();
		final int campspacebetweensquads = ExpandedSingletonInitFile.getCampSpaceBetweenSquads();

		switch (pSector) {
		case 1:
			LOG.info("Creating Sector1 (North)");
			startX = ExpandedSingletonInitFile.getStartLocation(ContextSingleton.getDay()).getX();
			startY = (int) (ExpandedSingletonInitFile.getStartLocation(ContextSingleton.getDay()).getY() - radiusOfLargestSquare - gapbetweensectors - radiusOfOuterSectors);
			break;
		case 2:
			LOG.info("Creating Sector2 (West)");
			startX = (int) (ExpandedSingletonInitFile.getStartLocation(ContextSingleton.getDay()).getX() - radiusOfLargestSquare - gapbetweensectors - radiusOfOuterSectors);
			startY = ExpandedSingletonInitFile.getStartLocation(ContextSingleton.getDay()).getY();
			break;
		case 3:
			LOG.info("Creating Sector3 (East)");
			startX = (int) (ExpandedSingletonInitFile.getStartLocation(ContextSingleton.getDay()).getX() + radiusOfLargestSquare + gapbetweensectors + radiusOfOuterSectors);
			startY = ExpandedSingletonInitFile.getStartLocation(ContextSingleton.getDay()).getY();
			break;
		case 4:
			LOG.info("Creating Sector4 (South)");
			startX = ExpandedSingletonInitFile.getStartLocation(ContextSingleton.getDay()).getX();
			startY = (int) (ExpandedSingletonInitFile.getStartLocation(ContextSingleton.getDay()).getY() + radiusOfLargestSquare + gapbetweensectors + radiusOfOuterSectors);
			break;
		case 5:
			LOG.info("Creating Sector5 (NorthWest)");
			startX = (int) (ExpandedSingletonInitFile.getStartLocation(ContextSingleton.getDay()).getX() - radiusOfLargestSquare - gapbetweensectors - radiusOfOuterSectors);
			startY = (int) (ExpandedSingletonInitFile.getStartLocation(ContextSingleton.getDay()).getY() - radiusOfLargestSquare - gapbetweensectors - radiusOfOuterSectors);
			break;
		case 6:
			LOG.info("Creating Sector6 (NorthEast)");
			startX = (int) (ExpandedSingletonInitFile.getStartLocation(ContextSingleton.getDay()).getX() + radiusOfLargestSquare + gapbetweensectors + radiusOfOuterSectors);
			startY = (int) (ExpandedSingletonInitFile.getStartLocation(ContextSingleton.getDay()).getY() - radiusOfLargestSquare - gapbetweensectors - radiusOfOuterSectors);
			break;
		case 7:
			LOG.info("Creating Sector7 (SouthWest)");
			startX = (int) (ExpandedSingletonInitFile.getStartLocation(ContextSingleton.getDay()).getX() - radiusOfLargestSquare - gapbetweensectors - radiusOfOuterSectors);
			startY = (int) (ExpandedSingletonInitFile.getStartLocation(ContextSingleton.getDay()).getY() + radiusOfLargestSquare + gapbetweensectors + radiusOfOuterSectors);
			break;
		case 8:
			LOG.info("Creating Sector8 (SouthEast)");
			startX = (int) (ExpandedSingletonInitFile.getStartLocation(ContextSingleton.getDay()).getX() + radiusOfLargestSquare + gapbetweensectors + radiusOfOuterSectors);
			startY = (int) (ExpandedSingletonInitFile.getStartLocation(ContextSingleton.getDay()).getY() + radiusOfLargestSquare + gapbetweensectors + radiusOfOuterSectors);
			break;
		default:
			startX = 0;
			startY = 0;
		}

		LOG.info("Creating sector " + pSector + " agents");
		LOG.info("Creating " + pCavSquads + " cav squads, " + pInfSquads + " inf squads, " + pMulSquads + " mule squads, " + pDonSquads + " donkey squads, " + pHorSquads + " horse squads, " + pCamSquads + " camels squads & " + pCarSquads + " cart squads.");
		for (int locX = startX + radiusOfOuterSectors; locX >= startX - radiusOfOuterSectors; locX = locX - (int) campspacebetweensquads) {
			for (int locY = startY + radiusOfOuterSectors; locY >= startY - radiusOfOuterSectors; locY = locY - (int) campspacebetweensquads) {
				if (locX > maxX) {
					maxX = locX;
				} else if (locX < minX) {
					minX = locX;
				}
				if (locY > maxY) {
					maxY = locY;
				} else if (locY < minY) {
					minY = locY;
				}
				if (cavcount < pCavSquads) {
					LOG.finest("Adding Cavalry Squad Officer at loc " + locX + ":" + locY);

					if (cavcount == 0) {
						spacing = ExpandedSingletonInitFile.getSectionSetoffSpacing();
					} else if (cavcount % ExpandedSingletonInitFile.getSecondaryUnitSize() == 1) {
						spacing = ExpandedSingletonInitFile.getSecondarySetoffSpacing();
					} else {
						spacing = ExpandedSingletonInitFile.getSetoffSpacing();
					}

					if (columnleaders != 0) {
						this.fAllAgents.add(LocalObjectFactory.createCavalryOfficer(new Location(locX, locY), unitID, agentObjectID, spacing, colLdrNumber));
						agentObjectID++;
						colLdrNumber++;
						columnleaders = 0;
					} else {
						this.fAllAgents.add(LocalObjectFactory.createCavalryOfficer(new Location(locX, locY), unitID, agentObjectID, spacing, columnleaders));
						agentObjectID++;
					}

					for (int j = 1; j <= ExpandedSingletonInitFile.getCavalrySquadSize(); j++) {
						LOG.finest("Adding Officer Cavalry Squad Soldier at loc " + locX + ":" + locY);
						this.fAllAgents.add(LocalObjectFactory.createCavalrySoldier(
								new Location(locX, locY), unitID, agentObjectID));
						agentObjectID++;
					}
					if (ExpandedSingletonInitFile.getSquadMule() && !ExpandedSingletonInitFile.getSquadMode()) {
						LOG.finest("Adding Squad Mule at loc " + locX + ":" + locY);
						this.fAllAgents.add(LocalObjectFactory.createMule(new Location(locX, locY), unitID, agentObjectID, ClassType.CAVALRY_SOLDIER.getSpeed()));
						agentObjectID++;
					}
					cavcount++;
					unitID++;

				} else if (infcount < pInfSquads) {
					LOG.finest("Adding Infantry Officer at loc " + locX + ":" + locY);
					if (pCavSquads == 0 && infcount == 0) {
						spacing = ExpandedSingletonInitFile.getSectionSetoffSpacing();
					} else if ((pCavSquads + infcount) % ExpandedSingletonInitFile.getSecondaryUnitSize() == 1) {
						spacing = ExpandedSingletonInitFile.getSecondarySetoffSpacing();
					} else {
						spacing = ExpandedSingletonInitFile.getSetoffSpacing();
					}

					if (columnleaders != 0) {
						this.fAllAgents.add(LocalObjectFactory.createOfficer(new Location(locX, locY), unitID, agentObjectID, spacing, colLdrNumber));
						agentObjectID++;
						colLdrNumber++;
						columnleaders = 0;
					} else {
						this.fAllAgents.add(LocalObjectFactory.createOfficer(new Location(locX, locY), unitID, agentObjectID, spacing, columnleaders));
						agentObjectID++;
					}					


					for (int j = 1; j <= ExpandedSingletonInitFile.getInfantrySquadSize(); j++) {
						LOG.finest("Adding Infantry Squad Soldier at loc " + locX + ":" + locY);
						this.fAllAgents.add(LocalObjectFactory.createSoldier(
								new Location(locX, locY), unitID, agentObjectID));
						agentObjectID++;
					}
					if (ExpandedSingletonInitFile.getSquadMule() && !ExpandedSingletonInitFile.getSquadMode()) {
						LOG.finest("Adding Squad Mule at loc " + locX + ":" + locY);
						this.fAllAgents.add(LocalObjectFactory.createMule(new Location(locX, locY), unitID, agentObjectID, ClassType.SOLDIER.getSpeed()));
						agentObjectID++;
					}
					infcount++;
					unitID++;

				} else if (mulcount < pMulSquads) {
					LOG.info("Adding Mule Officer at loc " + locX + ":" + locY + ". Mule squad " + mulcount + " of " + pMulSquads);

					if (bagcount == 0) {
//	predm112					spacing = ExpandedSingletonInitFile.getSectionSetoffSpacing();
						spacing = ExpandedSingletonInitFile.getSecondarySetoffSpacing();
					} else if (bagcount % ExpandedSingletonInitFile.getSecondaryUnitSize() == 1) {
						spacing = ExpandedSingletonInitFile.getSecondarySetoffSpacing();
					} else {
						spacing = ExpandedSingletonInitFile.getSetoffSpacing();
					}

					if (columnleaders != 0) {
						this.fAllAgents.add(LocalObjectFactory.createBaggageHandler(new Location(locX, locY), unitID, agentObjectID, spacing, colLdrNumber, ClassType.MULE.getSpeed()));
						agentObjectID++;
						colLdrNumber++;
						columnleaders = 0;
					} else {
						this.fAllAgents.add(LocalObjectFactory.createBaggageHandler(new Location(locX, locY), unitID, agentObjectID, spacing, columnleaders, ClassType.MULE.getSpeed()));
						agentObjectID++;
					}

					for (int j = 1; j <= ExpandedSingletonInitFile.getMuleSquadSize(); j++) {
						LOG.finest("Adding Mule at loc " + locX + ":" + locY);
						this.fAllAgents.add(LocalObjectFactory.createMule(new Location(locX, locY), unitID, agentObjectID));
						agentObjectID++;
					}

					mulcount++;
					bagcount++;
					unitID++;
				} else if (doncount < pDonSquads) {
					LOG.finest("Adding Donkey Officer at loc " + locX + ":" + locY);

					if (bagcount == 0) {
//						predm112						spacing = ExpandedSingletonInitFile.getSectionSetoffSpacing();
						spacing = ExpandedSingletonInitFile.getSecondarySetoffSpacing();
					} else if (bagcount % ExpandedSingletonInitFile.getSecondaryUnitSize() == 1) {
						spacing = ExpandedSingletonInitFile.getSecondarySetoffSpacing();
					} else {
						spacing = ExpandedSingletonInitFile.getSetoffSpacing();
					}

					if (columnleaders != 0) {
						this.fAllAgents.add(LocalObjectFactory.createBaggageHandler(new Location(locX, locY), unitID, agentObjectID, spacing, colLdrNumber, ClassType.DONKEY.getSpeed()));
						agentObjectID++;
						colLdrNumber++;
						columnleaders = 0;
					} else {
						this.fAllAgents.add(LocalObjectFactory.createBaggageHandler(new Location(locX, locY), unitID, agentObjectID, spacing, columnleaders, ClassType.DONKEY.getSpeed()));
						agentObjectID++;
					}

					for (int j = 1; j <= ExpandedSingletonInitFile.getDonkeySquadSize(); j++) {
						LOG.finest("Adding Donkey at loc " + locX + ":" + locY);
						this.fAllAgents.add(LocalObjectFactory.createDonkey(new Location(locX, locY), unitID, agentObjectID));
						agentObjectID++;
					}

					doncount++;
					bagcount++;
					unitID++;

				} else if (horcount < pHorSquads) {
					LOG.finest("Adding Horse Officer at loc " + locX + ":" + locY);

					if (bagcount == 0) {
//						predm112						spacing = ExpandedSingletonInitFile.getSectionSetoffSpacing();
						spacing = ExpandedSingletonInitFile.getSecondarySetoffSpacing();
					} else if (bagcount % ExpandedSingletonInitFile.getSecondaryUnitSize() == 1) {
						spacing = ExpandedSingletonInitFile.getSecondarySetoffSpacing();
					} else {
						spacing = ExpandedSingletonInitFile.getSetoffSpacing();
					}

					if (columnleaders != 0) {
						this.fAllAgents.add(LocalObjectFactory.createBaggageHandler(new Location(locX, locY), unitID, agentObjectID, spacing, colLdrNumber, ClassType.HORSE.getSpeed()));
						agentObjectID++;
						colLdrNumber++;
						columnleaders = 0;
					} else {
						this.fAllAgents.add(LocalObjectFactory.createBaggageHandler(new Location(locX, locY), unitID, agentObjectID, spacing, columnleaders, ClassType.HORSE.getSpeed()));
						agentObjectID++;
					}

					for (int j = 1; j <= ExpandedSingletonInitFile.getHorseSquadSize(); j++) {
						LOG.finest("Adding Horse at loc " + locX + ":" + locY);
						this.fAllAgents.add(LocalObjectFactory.createHorse(new Location(locX, locY), unitID, agentObjectID));
						agentObjectID++;
					}

					horcount++;
					bagcount++;
					unitID++;

				} else if (camcount < pCamSquads) {
					LOG.finest("Adding Camel Officer at loc " + locX + ":" + locY);

					if (bagcount == 0) {
//						predm112						spacing = ExpandedSingletonInitFile.getSectionSetoffSpacing();
						spacing = ExpandedSingletonInitFile.getSecondarySetoffSpacing();
					} else if (bagcount % ExpandedSingletonInitFile.getSecondaryUnitSize() == 1) {
						spacing = ExpandedSingletonInitFile.getSecondarySetoffSpacing();
					} else {
						spacing = ExpandedSingletonInitFile.getSetoffSpacing();
					}

					if (columnleaders != 0) {
						this.fAllAgents.add(LocalObjectFactory.createBaggageHandler(new Location(locX, locY), unitID, agentObjectID, spacing, colLdrNumber, ClassType.CAMEL.getSpeed()));
						agentObjectID++;
						colLdrNumber++;
						columnleaders = 0;
					} else {
						this.fAllAgents.add(LocalObjectFactory.createBaggageHandler(new Location(locX, locY), unitID, agentObjectID, spacing, columnleaders, ClassType.CAMEL.getSpeed()));
						agentObjectID++;
					}

					for (int j = 1; j <= ExpandedSingletonInitFile.getCamelSquadSize(); j++) {
						LOG.finest("Adding Camel at loc " + locX + ":" + locY);
						this.fAllAgents.add(LocalObjectFactory.createCamel(new Location(locX, locY), unitID, agentObjectID));
						agentObjectID++;
					}

					camcount++;
					bagcount++;
					unitID++;

				} else if (carcount < pCarSquads) {
					LOG.finest("Adding Cart Officer at loc " + locX + ":" + locY);

					if (bagcount == 0) {
//						predm112						spacing = ExpandedSingletonInitFile.getSectionSetoffSpacing();
						spacing = ExpandedSingletonInitFile.getSecondarySetoffSpacing();
					} else if (bagcount % ExpandedSingletonInitFile.getSecondaryUnitSize() == 1) {
						spacing = ExpandedSingletonInitFile.getSecondarySetoffSpacing();
					} else {
						spacing = ExpandedSingletonInitFile.getSetoffSpacing();
					}

					if (columnleaders != 0) {
						this.fAllAgents.add(LocalObjectFactory.createBaggageHandler(new Location(locX, locY), unitID, agentObjectID, spacing, colLdrNumber, ClassType.CART.getSpeed()));
						agentObjectID++;
						colLdrNumber++;
						columnleaders = 0;
					} else {
						this.fAllAgents.add(LocalObjectFactory.createBaggageHandler(new Location(locX, locY), unitID, agentObjectID, spacing, columnleaders, ClassType.CART.getSpeed()));
						agentObjectID++;
					}

					for (int j = 1; j <= ExpandedSingletonInitFile.getCartSquadSize(); j++) {
						LOG.finest("Adding Cart at loc " + locX + ":" + locY);
						this.fAllAgents.add(LocalObjectFactory.createCart(new Location(locX, locY), unitID, agentObjectID));
						agentObjectID++;
					}

					carcount++;
					bagcount++;
					unitID++;
				}
				
				// Add Mule Sector squads onto sectors with either infantry or cavalry
/*				if (infcount > 0 || cavcount > 0) {
					for (int x = 0; x <= ExpandedSingletonInitFile.getSectorMuleSquads(); x++) {
						LOG.info("Adding Sector Mule Officer at loc " + locX + ":" + locY);

						spacing = ExpandedSingletonInitFile.getSetoffSpacing();

						this.fAllAgents.add(LocalObjectFactory.createBaggageHandler(new Location(locX, locY), unitID, agentObjectID, spacing, columnleaders, ClassType.MULE.getSpeed()));
						agentObjectID++;

						for (int j = 1; j <= ExpandedSingletonInitFile.getMuleSquadSize(); j++) {
							LOG.finest("Adding Mule at loc " + locX + ":" + locY);
							this.fAllAgents.add(LocalObjectFactory.createMule(new Location(locX, locY), unitID, agentObjectID));
							agentObjectID++;
						}
						unitID++;
					}
				}*/
			}
		}
	}

	public static void main(final String[] pArguments) {
		new ManzikertDaysMarchSP(pArguments[0]);
	}

	public void collectReport(final MWGridAgent pAgent) {
		final String[] splitReport = pAgent.report().split(NullHandling.SEPARATOR);
		try {
			this.fOutputTickFile.write(splitReport[0]);
			this.fOutputTickFile.newLine();
			this.fOutputTickFile.flush();
		} catch (final IOException e) {
			LOG.severe("IOException caught while writing to output file");
			e.printStackTrace();
			System.exit(1);
		}
		if (DMTimeHandling.isLastTickOfSimulation(ContextSingleton.getTime())) {
			try {
				this.fOutputDayFile.write(splitReport[1]);
				this.fOutputDayFile.newLine();
				this.fOutputDayFile.flush();
			} catch (final IOException e) {
				LOG.severe("IOException caught while writing to output file");
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}
