package com.geo.taxi.preprocess;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class PickUpLocationAnalyzerAllDays extends StatisticsAnalyzer {
	final String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri" , "Sat"}; 
	HashMap<Integer, ArrayList<Double>> lats, lons;
	HashMap<Integer, ArrayList<Date>> times;

	public PickUpLocationAnalyzerAllDays() {
		lats = new HashMap<Integer, ArrayList<Double>>();
		lons = new HashMap<Integer, ArrayList<Double>>();
		times = new HashMap<Integer, ArrayList<Date>>();
	}

	@Override
	public int process(double lat, double lon, byte status, long time) {
		Date d = new Date(time * 1000);
		@SuppressWarnings("deprecation")
		int timeKey = (d.getMonth() * 31 + d.getDate()-1) * 24 + d.getHours()/4;
		if (lats.get(timeKey) == null)
			lats.put(timeKey, new ArrayList<Double>());
		if (lons.get(timeKey) == null)
			lons.put(timeKey, new ArrayList<Double>());
		if (times.get(timeKey) == null)
			times.put(timeKey, new ArrayList<Date>());
		lats.get(timeKey).add(lat);
		lons.get(timeKey).add(lon);
		times.get(timeKey).add( d );
		return timeKey;
	}

	@Override
	public void printStats(PrintStream p) {
		for (int i : lats.keySet()) {
			String s = i/31/24 + "-"+i/24%31+"-"+i%24;
			Date d = times.get(i).get(0);
			String caption = daysOfWeek[d.getDay()] +" "+ (d.getYear()+1900)+"-"+(d.getMonth()+1)+"-"+d.getDate()+" "+(4*(i%24))+" to " + (4*(i%24+1)); 
			System.err.println(s);
			new mapDrawer().drawMap(lats.get(i), lons.get(i), "cabspottingdata/Maps/map["
					+ s + "].png",caption, -122.526, -122.35, 37.6, 37.824, 10000);
		}
	}


}
