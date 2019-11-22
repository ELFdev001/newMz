/**
 * 
 */
package mwgrid.environment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import mwgrid.middleware.distributedobject.Location;
import mwgrid.middleware.distributedobject.Value;

/**
 * @author Dr B.G.W. Craenen (b.g.w.craenen@cs.bham.ac.uk)
 */
public class SliceMap implements Slice {
    private static final Logger LOG =
            Logger.getLogger(SliceMap.class.getPackage().getName());
    private Map<Location, Integer> fSliceMap;
    private String fFileName;
    private Location fLowerLeftLocation;
    private Location fUpperRightLocation;
    private int fNumberOfColumns;
    private int fNumberOfRows;
    private Value<Integer> fNoDataValue;
    
    /**
     * @param pFileName
     *            - file name
     * @param pLowerLeftLocation
     *            - lower left location
     * @param pUpperRightLocation
     *            - upper right location
     */
    public SliceMap(final String pFileName,
            final Location pLowerLeftLocation,
            final Location pUpperRightLocation) {
        LOG.finest("Constructor");
        this.fFileName = pFileName;
        this.fLowerLeftLocation = pLowerLeftLocation;
        this.fUpperRightLocation = pUpperRightLocation;
        this.fNumberOfColumns =
                this.fUpperRightLocation.getX()
                        - this.fLowerLeftLocation.getX();
        this.fNumberOfRows =
                this.fUpperRightLocation.getY()
                        - this.fLowerLeftLocation.getY();
        this.fSliceMap =
            new HashMap<Location, Integer>();
        try {
            final BufferedReader reader =
                    new BufferedReader(new FileReader(this.fFileName));
            final int numberOfColumns =
                    Integer.parseInt(reader.readLine().split("\\s+")[1]);
            assert numberOfColumns == this.fNumberOfColumns : "incorrect number of columns";
            final int numberOfRows =
                    Integer.parseInt(reader.readLine().split("\\s+")[1]);
            assert numberOfRows == this.fNumberOfRows : "incorrect number of rows";
            /*
             * final int xLowerLeftCorner = (int)
             * Double.parseDouble(reader.readLine().split("\\s+")[1]);
             */
            reader.readLine();
            /*
             * final int yLowerLeftCorner = (int)
             * Double.parseDouble(reader.readLine().split("\\s+")[1]);
             */
            reader.readLine();
            /*
             * final double cellSize =
             * Double.parseDouble(reader.readLine().split("\\s+")[1]);
             */
            reader.readLine();
            this.fNoDataValue =
                    new Value<Integer>(new Integer(Integer.parseInt(reader
                            .readLine().split("\\s+")[1])));
            String dataLine = null;
            for (int row = 0; (dataLine = reader.readLine()) != null; row++) {
                // LOG.finest("Row: " + row);
                assert row < numberOfRows;
                final String[] tokens = dataLine.split("\\s+");
                assert tokens.length == numberOfColumns;
                for (int column = 0; column < tokens.length; column++) {
                    final int value = Integer.parseInt(tokens[column]);
                    if (value == this.fNoDataValue.get().intValue())
                        continue;
                    // LOG.finest("(" + column + "," + row + "): " + value);
                    this.fSliceMap.put(new Location(column, row),
                        new Integer(value));
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * @param pLocation
     *            - location
     * @return (Value<?>) value
     */
    public Value<?> get(final Location pLocation) {
        if (!this.fSliceMap.containsKey(pLocation)) return this.fNoDataValue;
        return new Value<Integer>(this.fSliceMap.get(pLocation));
    }
}
