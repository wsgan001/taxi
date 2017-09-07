package com.geo.taxi.config;
import java.io.File;
import java.io.FileFilter;
import java.util.Date;

public class Config {
	private static final String inputPath = "cabspottingdata";
	private static final String extension = "txt";
	private static final String outputPath = "processedData/individualPickUps/";
	private static final String analysisOutputPath = "processedData/";
	private static final String graphPath = analysisOutputPath+"graph.txt";
	private static final double minLat = 37.6;
	private static final double maxLat = 37.824;
	private static final double minLon = -122.526;
	private static final double maxLon = -122.35;
	private static final double angleChunk = 0.005;
	private static final int numOfLatBins = (int) ((maxLat - minLat) / angleChunk) + 1;
	private static final int numOfLonBins = (int) ((maxLon - minLon) / angleChunk) + 1;
	private static final double timeChunk = 2;
	private static final double probabilityThreshold = 0.2;
	private static final int maxNumberOfFilesToRead = 1000;
	
	public static String getGraphpath() {
		return graphPath;
	}
	
	public static double getProbabilitythreshold() {
		return probabilityThreshold;
	}
	
	public static int getMaxnumberoffilestoread() {
		return maxNumberOfFilesToRead;
	}
	
	public static double getTimechunk() {
		return timeChunk;
	}

	private static final long MaxTimeInterval = 420;
	public static long getMaxtimeinterval() {
		return MaxTimeInterval;
	}

	private static final FileFilter fileFilter = new FileFilter() {

		@Override
		public boolean accept(File pathname) {
			if (pathname.getName().endsWith("." + extension))
				return true;
			else
				return false;
		}
	};

	public static String getInputpath() {
		return inputPath;
	}

	public static String getExtension() {
		return extension;
	}

	public static String getOutputpath() {
		return outputPath;
	}

	public static FileFilter getFilefilter() {
		return fileFilter;
	}

	public static double getMinlat() {
		return minLat;
	}

	public static double getMaxlat() {
		return maxLat;
	}

	public static double getMinlon() {
		return minLon;
	}

	public static double getMaxlon() {
		return maxLon;
	}

	public static double getAnglechunk() {
		return angleChunk;
	}

	public static int getNumoflatbins() {
		return numOfLatBins;
	}

	public static int getNumoflonbins() {
		return numOfLonBins;
	}
	
	public static int getLatBin(double lat){
		return (int) ((lat-minLat)/angleChunk);
	}
	
	public static int getLonBin(double lon){
		return (int) ((lon-minLon)/angleChunk);
	}
	
	@SuppressWarnings("unused")
	private int hashPoint( final double lat , final double lon) {
		return numOfLonBins * getLatBin(lat) + getLonBin(lon);
	}

	public static String getAnalysisoutputpath() {
		return analysisOutputPath;
	}
	
	public static int getTimeChunk (int h){
		return (int) (h/timeChunk);
	}
	
	public static int hashTime (Date d ){
		return d.getDay() * 24 + getTimeChunk( d.getHours() );
	}

	public static double getProbabilityThreshold() {
		return probabilityThreshold;
	}

	public static int getMaxGrid() {
		return numOfLatBins * numOfLonBins;
	}
}
