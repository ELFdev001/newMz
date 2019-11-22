/**
 * 
 */
package mwgrid.environment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import mwgrid.middleware.distributedobject.Location;
import mwgrid.middleware.distributedobject.Value;

/**
 * @author Dr B.G.W. Craenen <b.g.w.craenen@cs.bham.ac.uk>
 */
public class PartitionedSliceArray implements Slice {
    private class PartitionedSlice implements Slice {
        private static final String HEIGHT_FILE_NAME = "gdemMED001.txt";
        private final String fFileName;
        private final Location fLowerLeftLocation;
        private final Location fUpperRightLocation;
        private int[][] fSliceArray;
        
        /**
         * Constructor
         * 
         * @param pFileName
         *            - file name
         * @param pLowerLeftLocation
         *            - lower left location
         * @param pUpperRightLocation
         *            - upper right location
         */
        public PartitionedSlice(final String pFileName,
                final Location pLowerLeftLocation,
                final Location pUpperRightLocation) {
            // LOG.info("Constructor");
            this.fFileName = pFileName;
            this.fLowerLeftLocation = pLowerLeftLocation;
            this.fUpperRightLocation = pUpperRightLocation;
            final int numberOfColumns =
                    this.fUpperRightLocation.getX()
                            - this.fLowerLeftLocation.getX() + 1;
            final int numberOfRows =
                    this.fUpperRightLocation.getY()
                            - this.fLowerLeftLocation.getY() + 1;
            this.fSliceArray = new int[numberOfColumns][numberOfRows];
            try {
                final ZipFile zipFile = new ZipFile(this.fFileName);
                final Enumeration<? extends ZipEntry> zipEntries =
                        zipFile.entries();
                ZipEntry zipEntry = null;
                while (zipEntries.hasMoreElements()) {
                    final ZipEntry tempZipEntry =
                            (ZipEntry) zipEntries.nextElement();
                    if (HEIGHT_FILE_NAME.equals(tempZipEntry.getName()))
                        zipEntry = tempZipEntry;
                }
                if (zipEntry == null)
                    throw new IOException("Couldn't find zip entry!");
                final BufferedReader reader =
                        new BufferedReader(new InputStreamReader(zipFile
                                .getInputStream(zipEntry)));
                final int readNumberOfColumns =
                        Integer.parseInt(reader.readLine().split("\\s+")[1]);
                final int readNumberOfRows =
                        Integer.parseInt(reader.readLine().split("\\s+")[1]);
                reader.readLine();
                reader.readLine();
                reader.readLine();
                reader.readLine();
                String dataLine = null;
                int localRow = 0;
                for (int row = 0; (dataLine = reader.readLine()) != null; row++) {
                    assert row < readNumberOfRows;
                    if (row < this.fLowerLeftLocation.getY()) continue;
                    if (row > this.fUpperRightLocation.getY()) break;
                    final String[] tokens = dataLine.split("\\s+");
                    int localColumn = 0;
                    assert tokens.length == readNumberOfColumns;
                    for (int column = this.fLowerLeftLocation.getX(); column <= this.fUpperRightLocation
                            .getX()
                            && column < tokens.length; column++) {
                        final int value = Integer.parseInt(tokens[column]);
                        this.fSliceArray[localColumn][localRow] = value;
                        // LOG.info("Row: " + row + ", Column: " + column
                        // + ", localRow: " + localRow
                        // + ", localColumn: " + localColumn
                        // + ", value: " + value);
                        localColumn++;
                    }
                    localRow++;
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        
        /**
         * @param pLocation
         *            - location
         * @return (boolean) true if within slice, false otherwise
         */
        public boolean inSlice(final Location pLocation) {
            return pLocation.in(this.fLowerLeftLocation,
                this.fUpperRightLocation);
        }
        
        @Override
        public Value<?> get(final Location pLocation) {
            return new Value<Integer>(new Integer(this.fSliceArray[pLocation
                    .getX()
                    - this.fLowerLeftLocation.getX()][pLocation.getY()
                    - this.fLowerLeftLocation.getY()]));
        }
    }
    
    private static final Logger LOG =
            Logger.getLogger(PartitionedSliceArray.class.getPackage()
                    .getName());
    private List<PartitionedSlice> fSliceList;
    private String fFileName;
    private Location fLowerLeftLocation;
    private Location fUpperRightLocation;
    private final int fHorizonalPartition;
    private final int fVerticalPartition;
    private final int fSliceListMaximumSize;
    private static final ExpandedSingletonInitFile INITFILE = ExpandedSingletonInitFile.getInstance();
    
    /**
     * Constructor
     * 
     * @param pFileName
     *            - file name
     * @param pLowerLeftLocation
     *            - lower left location
     * @param pUpperRightLocation
     *            - upper right location
     */
    public PartitionedSliceArray(final String pFileName,
            final Location pLowerLeftLocation,
            final Location pUpperRightLocation) {
        LOG.finest("Constructor");
        this.fFileName = pFileName;
        this.fLowerLeftLocation = pLowerLeftLocation;
        this.fUpperRightLocation = pUpperRightLocation;
        this.fHorizonalPartition = INITFILE.getEnviroXparts();
        this.fVerticalPartition = INITFILE.getEnviroYparts();
        this.fSliceListMaximumSize = INITFILE.getEnviroPartListSize();
        this.fSliceList = new ArrayList<PartitionedSlice>();
    }
    
    @Override
    public Value<?> get(final Location pLocation) {
        assert pLocation.in(this.fLowerLeftLocation,
            this.fUpperRightLocation);
        for (PartitionedSlice slice : this.fSliceList) {
            if (slice.inSlice(pLocation)) {
                this.fSliceList.remove(slice);
                this.fSliceList.add(0, slice);
                return slice.get(pLocation);
            }
        }
        final PartitionedSlice slice =
                new PartitionedSlice(this.fFileName, this
                        .getLowerLeftPartitionLocation(pLocation), this
                        .getUpperRightPartitionLocation(pLocation));
        this.fSliceList.add(0, slice);
        if (this.fSliceList.size() > this.fSliceListMaximumSize)
            this.fSliceList.remove(this.fSliceList.size() - 1);
        return slice.get(pLocation);
    }
    
    /**
     * @param pLocation
     *            - location
     * @return (Location) lower left partition location
     */
    private Location getLowerLeftPartitionLocation(final Location pLocation) {
        final int partitionX = pLocation.getX() / this.fHorizonalPartition;
        final int partitionY = pLocation.getY() / this.fVerticalPartition;
        return new Location(partitionX * this.fHorizonalPartition, partitionY
                * this.fVerticalPartition);
    }
    
    /**
     * @param pLocation
     *            - location
     * @return (Location) upper right partition location
     */
    private Location getUpperRightPartitionLocation(final Location pLocation) {
        final int partitionX = pLocation.getX() / this.fHorizonalPartition;
        final int partitionY = pLocation.getY() / this.fVerticalPartition;
        return new Location((partitionX * this.fHorizonalPartition)
                + this.fHorizonalPartition - 1,
                (partitionY * this.fVerticalPartition)
                        + this.fVerticalPartition - 1);
    }
}
