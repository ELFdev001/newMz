package mwgrid.manzikert;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.logging.Logger;

import com.sun.xml.internal.ws.api.message.HeaderList;

public final class ESRIsurfaceclean {
	private static final Logger LOG =
		Logger.getLogger(ESRIsurfaceclean.class.getPackage().getName());
	private static final String TICK_FILEPATH = "D:/ManzikertSP/newMz/";
	private static final String TICK_FILENAME = "surface.asc";
	private static final int STARTCOL = 1800;
	private static final int ENDCOL = 7198;
	private static final int STARTROW = 3300;
	private static final int ENDROW = 15648;

	private BufferedWriter fOutputTickFile;
	private float inrows, incols, innodataval;
	int rows, cols, nodataval;
	float[][] heightdata;
	

	public ESRIsurfaceclean() throws IOException {
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
		String[] headerinfo = new String[6];
		
		for (int headerline = 0; headerline < 6; headerline++) {
			dataLine = reader.readLine();
			final String[] tokens = dataLine.split("\\s+");
			if (headerline == 0) {
				incols = Float.parseFloat(tokens[1]);
				System.out.println(tokens[1]);
			} else if (headerline == 1) {
				inrows = Float.parseFloat(tokens[1]);
				System.out.println(tokens[1]);
			} else if (headerline == 5) {
				innodataval = Float.parseFloat(tokens[1]);
				System.out.println(tokens[1]);
			}
			headerinfo[headerline] = dataLine;
		}
		
		cols = (int) incols;
		rows = (int) inrows;
		nodataval = (int) innodataval;
		System.out.println(cols + " " + rows + " " + nodataval);

		heightdata = new float[cols][rows];
		
		for (int inline = 0; inline < rows; inline++) {
			LOG.info("Line is " + inline);
			dataLine = reader.readLine();
			final String[] heights = dataLine.split("\\s+");
//			System.out.println("Length of line is " + heights.length);
			for (int inval = 1; inval < cols; inval++) {
//				System.out.println("Col" + inval + " " + heights[inval]);
				if (heights[inval] != null) {
					if (Float.parseFloat(heights[inval]) == nodataval) {
						heightdata[inval][inline] = 0;
					} else {
						heightdata[inval][inline] = Float.parseFloat(heights[inval]);
					}
				}
			}
		}
		
		//Writing outputs
		for (int x = 0; x < 6; x++) {
			this.fOutputTickFile.write(headerinfo[x]);
			this.fOutputTickFile.newLine();
			this.fOutputTickFile.flush();
		}
		
		for (int outline = STARTROW; outline < ENDROW; outline++) {
			this.fOutputTickFile.write(getString(outline));
			this.fOutputTickFile.newLine();
			this.fOutputTickFile.flush();
		}
		
	}
	
	String getString(int rowno) {
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		String thisString = "";
		for (int x = STARTCOL; x < ENDCOL; x++) {
			thisString = thisString + df.format(heightdata[x][rowno]);
			thisString = thisString + " ";
		}
		return thisString;
	}


	public static void main(final String[] pArguments) throws Exception, IOException {
		ESRIsurfaceclean thistest = new ESRIsurfaceclean();
	}

}

