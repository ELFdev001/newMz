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


public final class ESRIresample {
	private static final Logger LOG =
		Logger.getLogger(ESRIresample.class.getPackage().getName());
	private static final String TICK_FILEPATH = "D:/QGISdata/";
	private static final String TICK_FILENAME = "SquareSelectableArea18764.asc";
	private static final int OUTPUTCOL = 1024;
	private static final int OUTPUTROW = 1024;

	private BufferedWriter fOutputTickFile;
	private float inrows, incols;
	int rows, cols, thisrow, thiscol, lastrow, lastcol;
	float[][] heightdata;
	

	public ESRIresample() throws IOException {
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
		String[][] headerinfo = new String[2][6];
		
		for (int headerline = 0; headerline < 6; headerline++) {
			dataLine = reader.readLine();
			final String[] tokens = dataLine.split("\\s+");
			headerinfo[0][headerline] = tokens[0];
			headerinfo[1][headerline] = tokens[1];
		}

		incols = Float.parseFloat(headerinfo[1][0]);
		System.out.println(incols);
		inrows = Float.parseFloat(headerinfo[1][1]);
		System.out.println(inrows);
		
		cols = (int) incols;
		rows = (int) inrows;
		System.out.println("Input cols " + cols + ", rows " + rows + " ");
		System.out.println("Output cols " + OUTPUTCOL + ", outrows " + OUTPUTROW + " ");
		thisrow = 0;
		lastrow = 0;
		thiscol = 0;
		lastcol = 0;
		float rowfac = (float) rows / (float) OUTPUTROW;
		float colfac = (float) cols / (float) OUTPUTCOL;
		System.out.println("Colfac=" + colfac + " rowfac=" + rowfac);
		float thisrowfl;
		float thiscolfl;
		
		heightdata = new float[OUTPUTCOL][OUTPUTROW];
		
		for (int inline = 0; inline < rows; inline++) {
			dataLine = reader.readLine();
			final String[] heights = dataLine.split("\\s+");
			for (int inval = 0; inval < cols; inval++) {
				if (heights[inval] != null) {
					thisrowfl = inline / rowfac;
					thiscolfl = inval / colfac;
					thisrow = (int) (thisrowfl);
					thiscol = (int) (thiscolfl);
					if (thisrow > lastrow || thiscol > lastcol)
					{
						System.out.println("Thisrow=" + thisrow + " lastrow=" + lastrow + " thiscol=" + thiscol + " lastcol=" + lastcol + " inline=" + inline + " inval=" + inval);
						heightdata[thiscol][thisrow] = Float.parseFloat(heights[inval + 1]);
						lastrow = thisrow;
						lastcol = thiscol;
					}
				}
			}
		}
		
		//Writing outputs
		this.fOutputTickFile.write(headerinfo[0][0] + " " + OUTPUTCOL);
		this.fOutputTickFile.newLine();
		this.fOutputTickFile.flush();

		this.fOutputTickFile.write(headerinfo[0][1] + " " + OUTPUTROW);
		this.fOutputTickFile.newLine();
		this.fOutputTickFile.flush();
		
		this.fOutputTickFile.write(headerinfo[0][2] + " " + headerinfo[1][2]);
		this.fOutputTickFile.newLine();
		this.fOutputTickFile.flush();

		this.fOutputTickFile.write(headerinfo[0][3] + " " + headerinfo[1][3]);
		this.fOutputTickFile.newLine();
		this.fOutputTickFile.flush();

		float cellsize = Float.parseFloat(headerinfo[1][4]);
		this.fOutputTickFile.write(headerinfo[0][4] + " " + (cellsize * ((cols / OUTPUTCOL) + rows / OUTPUTROW) / 2));
		this.fOutputTickFile.newLine();
		this.fOutputTickFile.flush();
		
		this.fOutputTickFile.write(headerinfo[0][5] + " " + headerinfo[1][5]);
		this.fOutputTickFile.newLine();
		this.fOutputTickFile.flush();

		for (int outline = 0; outline < OUTPUTROW; outline++) {
			this.fOutputTickFile.write(getString(outline));
			this.fOutputTickFile.newLine();
			this.fOutputTickFile.flush();
		}
		
	}
	
	String getString(int rowno) {
		String thisString = "";
		for (int x = 0; x < OUTPUTCOL; x++) {
			thisString = thisString + heightdata[x][rowno];
			thisString = thisString + " ";
		}
		return thisString;
	}


	public static void main(final String[] pArguments) throws Exception, IOException {
		ESRIresample thistest = new ESRIresample();
	}

}

