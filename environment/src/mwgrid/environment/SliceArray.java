/**
 * 
 */
package mwgrid.environment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

import mwgrid.middleware.distributedobject.Location;
import mwgrid.middleware.distributedobject.Value;

/**
 * @author Dr B.G.W. Craenen (b.g.w.craenen@cs.bham.ac.uk)
 */
public class SliceArray implements Slice {
    private static final Logger LOG =
            Logger.getLogger(SliceArray.class.getPackage().getName());
    private int[][] fSliceArray;
    private String fFileName;
    private Location fLowerLeftLocation;
    private Location fUpperRightLocation;
    private int fNumberOfColumns;
    private int fNumberOfRows;
    
    /**
     * @param pFileName
     *            - file name
     * @param pLowerLeftLocation
     *            - lower left location
     * @param pUpperRightLocation
     *            - upper right location
     */
    public SliceArray(final String pFileName,
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
        this.fSliceArray = new int[this.fNumberOfColumns][this.fNumberOfRows];
        try {
            final BufferedReader reader =
                    new BufferedReader(new FileReader(this.fFileName));
            // Read metadata
            final int numberOfColumns =
                    Integer.parseInt(reader.readLine().split("\\s+")[1]);
            assert numberOfColumns == this.fNumberOfColumns;
            final int numberOfRows =
                    Integer.parseInt(reader.readLine().split("\\s+")[1]);
            assert numberOfRows == this.fNumberOfRows;
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
            /*
             * final Value<Integer> noDataValue = new Value<Integer>(new
             * Integer(Integer.parseInt(reader.readLine().split("\\s+")[1])));
             */
            reader.readLine();
            // Read actual data
            String dataLine = null;
            for (int row = 0; (dataLine = reader.readLine()) != null; row++) {
                // LOG.finest("Row: " + row);
                assert row < numberOfRows;
                final String[] tokens = dataLine.split("\\s+");
                assert tokens.length == numberOfColumns;
                for (int column = 0; column < tokens.length; column++) {
                    final int value = Integer.parseInt(tokens[column]);
                    // LOG.finest("(" + column + "," + row + "): " + value);
                    this.fSliceArray[column][row] = value;
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
        return new Value<Integer>(new Integer(this.fSliceArray[pLocation
                .getX()][pLocation.getY()]));
    }
}
