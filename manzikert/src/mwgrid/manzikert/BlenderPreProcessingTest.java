package mwgrid.manzikert;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

public final class BlenderPreProcessingTest {
	public class TickEntry {
		int tickNo;
		int agentNo;
		int agentType;
		int locX;
		int locY;

		public TickEntry(int pTickNo, int pAgentNo, int pAgentType, int pLocX, int pLocY) {
			this.tickNo = pTickNo;
			this.agentNo = pAgentNo;
			this.agentType = pAgentType;
			this.locX = pLocX;
			this.locY = pLocY;
		}

		public String getString() {
			final StringBuilder outputString = new StringBuilder();
			outputString.append(tickNo);
			outputString.append(" ");
			outputString.append(agentNo);
			outputString.append(" ");
			outputString.append(agentType);
			outputString.append(" ");
			outputString.append(locX);
			outputString.append(" ");
			outputString.append(locY);
			return outputString.toString();
		}
	}

	private static final Logger LOG =
		Logger.getLogger(BlenderPreProcessingTest.class.getPackage().getName());
	private static final String TICK_FILEPATH = "D:/DaysMarch tickfiles/";
	private static final String TICK_FILENAME = "1-1-4-4-232900-46400-234365-47765-99-false-heightcrawler-theman-cl3-SPtickfile.txt";
	private static final int MAX_AGENTS = 20100;
	private static final int TICKS = 12810;
	private BufferedWriter fOutputTickFile;

	public BlenderPreProcessingTest() throws IOException {
		final TickEntry NULL_TICK = new TickEntry(0, 0, 0, 0, 0);
		TickEntry[][] ticks = new TickEntry[2][MAX_AGENTS];
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(TICK_FILEPATH + TICK_FILENAME));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			this.fOutputTickFile =
				new BufferedWriter(new FileWriter(new File(
						TICK_FILEPATH + "Proc" + TICK_FILENAME)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String dataLine = null;
		int thistickno = 1;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < MAX_AGENTS; j++) {
				ticks[i][j] = NULL_TICK;
			}
		}

		for (int tick = 1; tick <= TICKS; tick++) {
			LOG.info("Tick is " + tick);
			while (thistickno == tick) {
				dataLine = reader.readLine();
				final String[] tokens = dataLine.split("\\s+");
				assert tokens.length == 5;
				int pTickNo = Integer.parseInt(tokens[0]);
				int pAgentNo = Integer.parseInt(tokens[1]);
				int pAgentType = Integer.parseInt(tokens[2]);
				int pLocX = Integer.parseInt(tokens[3]);
				int pLocY = Integer.parseInt(tokens[4]);
				TickEntry thistick = new TickEntry(pTickNo, pAgentNo, pAgentType, pLocX, pLocY);
				if (pTickNo != tick) {
					thistickno = pTickNo;
					for (int i = 1; i < MAX_AGENTS; i++) {
						if (ticks[0][i].locX != ticks[1][i].locX || ticks[0][i].locY != ticks[1][i].locY) {
							if (!ticks[0][i].equals(NULL_TICK)) {
								this.fOutputTickFile.write(ticks[0][i].getString());
								this.fOutputTickFile.newLine();
								this.fOutputTickFile.flush();
							}
						}
						ticks[1][i] = ticks[0][i];
						ticks[0][i] = NULL_TICK;
					}
				}
				ticks[0][pAgentNo] = thistick;
			}
		}
	}


	public static void main(final String[] pArguments) throws Exception, IOException {
		BlenderPreProcessingTest thistest = new BlenderPreProcessingTest();
	}

}

