package mwgrid.manzikert;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

import mwgrid.middleware.distributedobject.Location;

public final class DistancesProcessingTest {
    public class TickEntry {
        int tickNo;
        int agentNo;
        int locX;
        int locY;
        
        public TickEntry(int pTickNo, int pAgentNo, int pLocX, int pLocY) {
            this.tickNo = pTickNo;
            this.agentNo = pAgentNo;
            this.locX = pLocX;
            this.locY = pLocY;
        }
    }
    
    private static final Logger LOG =
        Logger.getLogger(DistancesProcessingTest.class.getPackage().getName());
    private static final String TICK_FILEPATH = "D:/DaysMarch tickfiles/DM005/";
    private static final String TICK_FILENAME = "10-10-0-180-30000-34500-31830-35352-99-false-SPtickfile.txt";
    private static final int MAX_TICKS = 12810;
    private static final int MAX_AGENTS = 1862;
    private static final Location refloc = new Location(0, 0);
    
    public DistancesProcessingTest() throws NumberFormatException, IOException {
        TickEntry[][] ticks = new TickEntry[MAX_TICKS][MAX_AGENTS];
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(TICK_FILEPATH + TICK_FILENAME));
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        String dataLine = null;
        int maxtick = 0;
        int maxagent = 0;
        for (int row = 0; (dataLine = reader.readLine()) != null; row++) {
            LOG.finest(dataLine);
            final String[] tokens = dataLine.split("\\s+");
            assert tokens.length == 9;
            int pTickNo = Integer.parseInt(tokens[0]);
            int pAgentNo = Integer.parseInt(tokens[1]);
            int pLocX = Integer.parseInt(tokens[3]);
            int pLocY = Integer.parseInt(tokens[4]);
            TickEntry thistick = new TickEntry(pTickNo, pAgentNo, pLocX, pLocY);
            LOG.fine("Tick # " + pTickNo + " Agent # " + pAgentNo);
            ticks[pTickNo][pAgentNo] = thistick;
            if (pTickNo > maxtick) {
                maxtick = pTickNo;
            }
            if (pAgentNo > maxagent) {
                maxagent = pAgentNo;
            }
        }
        
        double totalmaxdist = 0;
        int totalmaxtick = 0;
        int totalminag = 0;
        int totalmaxag = 0;
        Location thisloc = Location.NULL_LOCATION;
        for (int i = 1; i <= maxtick; i++) {
            double mindist = 999999999;
            int minag = 0;
            Location minloc = Location.NULL_LOCATION;
            Location maxloc = Location.NULL_LOCATION;
            double maxdist = 0;
            int maxag = 0;
            for (int j = 1; j <= maxagent; j++) {
                thisloc = new Location(ticks[i][j].locX, ticks[i][j].locY);
                if (thisloc.distanceTo(refloc) < mindist) {
                    mindist = thisloc.distanceTo(refloc);
                    minag = j;
                    minloc = thisloc;
                }
                if (thisloc.distanceTo(refloc) > maxdist) {
                    maxdist = thisloc.distanceTo(refloc);
                    maxag = j;
                    maxloc = thisloc;
                }
            }
            double thismaxdist = minloc.distanceTo(maxloc);
            if (thismaxdist > totalmaxdist) {
                totalmaxdist = thismaxdist;
                totalmaxtick = i;
                totalmaxag = maxagent;
                totalminag = minag;
            }
        }
        System.out.println("Maximum distance between agents is " + totalmaxdist * 5 + " metres");
        System.out.println("Between agents " + totalmaxag + " and " + totalminag);
        System.out.println("At tick# " + totalmaxtick);
        
    }
    
    public static void main(final String[] pArguments) throws Exception, IOException {
    	DistancesProcessingTest thistest = new DistancesProcessingTest();
    }
    
}

