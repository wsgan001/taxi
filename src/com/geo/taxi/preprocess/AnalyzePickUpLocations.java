package com.geo.taxi.preprocess;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Scanner;

import com.geo.taxi.config.Config;

public class AnalyzePickUpLocations {
	public static void main(String[] args) throws FileNotFoundException {
		processIndividualPickUps(Config.getOutputpath());
	}

	private static void processIndividualPickUps(String outputpath)
			throws FileNotFoundException {
		File dir = new File(outputpath);
		if (dir.isDirectory()) {
			FilenameFilter filter = new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith("." + Config.getExtension());
				}
			};
			File[] files = dir.listFiles(filter);
			int cnt = 1;
			// DayOfWeekStatisticalAnalyzer dow = new
			// DayOfWeekStatisticalAnalyzer();
			// TimeOfDayStatisticalAnalyzer tod = new
			// TimeOfDayStatisticalAnalyzer();
			// TimeOfDayOfWeekStatisticalAnalyzer2 todow2 = new
			// TimeOfDayOfWeekStatisticalAnalyzer2();
			// minMaxAnalyzer minMax = new minMaxAnalyzer();
			PickUpLocationAnalyzerAllDays locAnalyze = new PickUpLocationAnalyzerAllDays();
			for (File f : files) {
				processFile(f, locAnalyze);
				System.out.println("processed " + cnt++ + " out of "
						+ files.length);
			}
			// dow.printStats(new PrintStream(new File("k:\\dow.txt")));
			// tod.printStats(new PrintStream(new File("k:\\tod.txt")));
			// todow2.printStats(new PrintStream(new File("k:\\todow2.txt")));
			// minMax.printStats( System.out );
			locAnalyze
					.printStats(new PrintStream(new File("location.txt")));
		}
	}

	private static void processFile(File f, StatisticsAnalyzer... analyzers)
			throws FileNotFoundException {
		Scanner sc = new Scanner(f);
		while (sc.hasNext()) {
			String line = sc.nextLine();
			Scanner lineScanner = new Scanner(line);
			double lat = lineScanner.nextDouble();
			double lon = lineScanner.nextDouble();
			byte status = lineScanner.nextByte();
			long time = lineScanner.nextLong();
			for (StatisticsAnalyzer a : analyzers)
				a.process(lat, lon, status, time);
		}
	}
}

class DayOfWeekStatisticalAnalyzer extends StatisticsAnalyzer {
	int[] dayOfWeekPickupCount;
	HashSet<Integer>[] dayOfWeekPickupDaySet;

	public DayOfWeekStatisticalAnalyzer() {
		dayOfWeekPickupCount = new int[7];
		dayOfWeekPickupDaySet = new HashSet[7];
		for (int i = 0; i < 7; i++)
			dayOfWeekPickupDaySet[i] = new HashSet<Integer>();
	}

	@Override
	public int process(double lat, double lon, byte status, long time) {
		Date d = new Date(time * 1000);
		int dayOfWeek = d.getDay();
		dayOfWeekPickupCount[dayOfWeek]++;
		dayOfWeekPickupDaySet[dayOfWeek].add(d.getMonth() * 31 + d.getDate()
				- 1);
		return dayOfWeek;
	}

	@Override
	public void printStats(PrintStream p) {
		for (int i = 0; i < dayOfWeekPickupCount.length; i++)
			p.println(i
					+ "\t"
					+ (dayOfWeekPickupCount[i] / ((double) (dayOfWeekPickupDaySet[i]
							.size()))));
	}
}

class TimeOfDayStatisticalAnalyzer extends StatisticsAnalyzer {
	int[] timeOfDayPickupCount;
	HashSet<Integer>[] timeOfDayPickupDaySet;

	public TimeOfDayStatisticalAnalyzer() {
		timeOfDayPickupCount = new int[24];
		timeOfDayPickupDaySet = new HashSet[24];
		for (int i = 0; i < 24; i++)
			timeOfDayPickupDaySet[i] = new HashSet<Integer>();
	}

	@Override
	public int process(double lat, double lon, byte status, long time) {
		Date d = new Date(time * 1000);
		int timeOfDay = d.getHours();
		timeOfDayPickupCount[timeOfDay]++;
		timeOfDayPickupDaySet[timeOfDay].add(d.getMonth() * 31 + d.getDate()
				- 1);
		return timeOfDay;
	}

	@Override
	public void printStats(PrintStream p) {
		for (int i = 0; i < timeOfDayPickupCount.length; i++)
			p.println(i
					+ "\t"
					+ (timeOfDayPickupCount[i] / ((double) (timeOfDayPickupDaySet[i]
							.size()))));
	}
}

class TimeOfDayOfWeekStatisticalAnalyzer extends StatisticsAnalyzer {
	int[] TimeOfDayOfWeekPickupCount;
	HashSet<Integer>[] TimeOfDayOfWeekPickupDays;

	public TimeOfDayOfWeekStatisticalAnalyzer() {
		TimeOfDayOfWeekPickupCount = new int[24 * 7];
		TimeOfDayOfWeekPickupDays = new HashSet[24 * 7];
		for (int i = 0; i < 24 * 7; i++)
			TimeOfDayOfWeekPickupDays[i] = new HashSet<Integer>();
	}

	@Override
	public int process(double lat, double lon, byte status, long time) {
		Date d = new Date(time * 1000);
		int timeOfDay = d.getHours();
		int dayOfWeek = d.getDay();
		TimeOfDayOfWeekPickupCount[timeOfDay + 24 * dayOfWeek]++;
		TimeOfDayOfWeekPickupDays[timeOfDay + 24 * dayOfWeek].add(d.getMonth()
				* 31 + (d.getDate() - 1));
		return timeOfDay * dayOfWeek;
	}

	@Override
	public void printStats(PrintStream p) {
		for (int i = 0; i < 24; i++) {
			p.print(i + "\t");
			for (int j = 0; j < 7; j++)
				p.print((TimeOfDayOfWeekPickupCount[24 * j + i] / (double) (TimeOfDayOfWeekPickupDays[24
						* j + i].size()))
						+ "\t");
			p.println();
		}
	}
}

class TimeOfDayOfWeekStatisticalAnalyzer2 extends StatisticsAnalyzer {
	int[] TimeOfDayOfWeekPickupCount;
	HashSet<Integer>[] TimeOfDayOfWeekPickupDays;

	public TimeOfDayOfWeekStatisticalAnalyzer2() {
		TimeOfDayOfWeekPickupCount = new int[6 * 7];
		TimeOfDayOfWeekPickupDays = new HashSet[6 * 7];
		for (int i = 0; i < 6 * 7; i++)
			TimeOfDayOfWeekPickupDays[i] = new HashSet<Integer>();
	}

	@Override
	public int process(double lat, double lon, byte status, long time) {
		Date d = new Date(time * 1000);
		int timeOfDay = d.getHours();
		int dayOfWeek = d.getDay();
		TimeOfDayOfWeekPickupCount[timeOfDay / 4 + 6 * dayOfWeek]++;
		TimeOfDayOfWeekPickupDays[timeOfDay / 4 + 6 * dayOfWeek].add(d
				.getMonth() * 31 + (d.getDate() - 1));
		return timeOfDay * dayOfWeek;
	}

	@Override
	public void printStats(PrintStream p) {
		for (int i = 0; i < 6; i++) {
			p.print(i + "\t");
			for (int j = 0; j < 7; j++)
				p.print((TimeOfDayOfWeekPickupCount[6 * j + i] / (double) (TimeOfDayOfWeekPickupDays[6
						* j + i].size()))
						+ "\t");
			p.println();
		}
	}
}

class minMaxAnalyzer extends StatisticsAnalyzer {

	double minLat, minLon, maxLat, maxLon;

	public minMaxAnalyzer() {
		minLat = minLon = Double.POSITIVE_INFINITY;
		maxLat = maxLon = Double.NEGATIVE_INFINITY;
	}

	@Override
	public int process(double lat, double lon, byte status, long time) {
		if (lat > maxLat)
			maxLat = lat;
		if (lat < minLat)
			minLat = lat;
		if (lon > maxLon)
			maxLon = lon;
		if (lon < minLon)
			minLon = lon;
		return 1;
	}

	@Override
	public void printStats(PrintStream p) {
		p.println(minLat + " < lat < " + maxLat);
		p.println(minLon + " < lon < " + maxLon);
	}

}

class PickUpLocationAnalyzer extends StatisticsAnalyzer {

	ArrayList<Double>[] lats;
	ArrayList<Double>[] lons;

	public PickUpLocationAnalyzer() {
		lats = new ArrayList[7];
		lons = new ArrayList[7];
		for (int i = 0; i < 7; i++) {
			lats[i] = new ArrayList<Double>();
			lons[i] = new ArrayList<Double>();
		}
	}

	@Override
	public int process(double lat, double lon, byte status, long time) {
		int dayOfWeek = new Date(time * 1000).getDay();
		lats[dayOfWeek].add(lat);
		lons[dayOfWeek].add(lon);
		return dayOfWeek;
	}

	@Override
	public void printStats(PrintStream p) {
		for (int i = 0; i < 7; i++) {
			new mapDrawer().drawMap(lats[i], lons[i],"map", "cabspottingdata/Maps/map["
					+ i + "].png", -122.526, -122.35, 37.6, 37.824, 100);
		}
	}

}


class PickUpLocationAnalyzerDOW extends StatisticsAnalyzer {

	ArrayList<Double>[] lats;
	ArrayList<Double>[] lons;

	public PickUpLocationAnalyzerDOW() {
		lats = new ArrayList[24];
		lons = new ArrayList[24];
		for (int i = 0; i < 24; i++) {
			lats[i] = new ArrayList<Double>();
			lons[i] = new ArrayList<Double>();
		}
	}

	@Override
	public int process(double lat, double lon, byte status, long time) {
		@SuppressWarnings("deprecation")
		int timeOfDay = new Date(time * 1000).getHours();
		lats[timeOfDay].add(lat);
		lons[timeOfDay].add(lon);
		return timeOfDay;
	}

	@Override
	public void printStats(PrintStream p) {
		for (int i = 0; i < 24; i++) {
			new mapDrawer().drawMap(lats[i], lons[i],"map", "cabspottingdata/Maps/map["
					+ i + "].png", -122.556, -122.35, 37.6, 37.824, 100);
		}
	}
}

