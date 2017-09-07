package com.geo.taxi.preprocess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Scanner;

import com.geo.taxi.config.Config;

public class FindDailyPerformance {
	public static void main(String[] args) throws FileNotFoundException {
		File mainDir = new File(Config.getAnalysisoutputpath() + "/Trips/");
		PrintStream p = new PrintStream(new File(Config.getAnalysisoutputpath()
				+ "\\dailyPerformance.txt"));
		int cnt = 0;
		if (mainDir.isDirectory()) {
			System.out.println("salam");
			for (File subDir : mainDir.listFiles()) {
				HashMap<Integer, Double> distanceOnDay = new HashMap<Integer, Double>();
				HashMap<Integer, Double> libeDistanceOnDay = new HashMap<Integer, Double>();
				HashMap<Integer, Double> timeOnDay = new HashMap<Integer, Double>();
				HashMap<Integer, Double> libeTimeOnDay = new HashMap<Integer, Double>();
				if (subDir.isDirectory()) {
					cnt++;
					for (File f : subDir.listFiles()) {
						analyzeFile(f, distanceOnDay, libeDistanceOnDay,
								timeOnDay, libeTimeOnDay);
					}
					for (int i : distanceOnDay.keySet())
						p.println(subDir.getName() + "\t" + (i / 31 + 1) + "\t"
								+ (i % 31 + 1) + "\t" + distanceOnDay.get(i)
								+ "\t" + libeDistanceOnDay.get(i) + "\t"
								+ timeOnDay.get(i) + "\t"
								+ libeTimeOnDay.get(i));
				}
				System.out.println(cnt);
				if (cnt > Config.getMaxnumberoffilestoread())
					break;
			}
		}
	}

	private static void analyzeFile(File f,
			HashMap<Integer, Double> distanceOnDay,
			HashMap<Integer, Double> libeDistanceOnDay,
			HashMap<Integer, Double> timeOnDay,
			HashMap<Integer, Double> libeTimeOnDay)
			throws FileNotFoundException {
		Scanner sc = new Scanner(f);
		GPSReading lastGpsReading = GPSReading.parseLine(sc.nextLine().trim());
		while (sc.hasNext()) {
			GPSReading thisGpsReading = GPSReading.parseLine(sc.nextLine());
			int time = thisGpsReading.getDate().getMonth() * 31
					+ thisGpsReading.getDate().getDate() - 1;
			if (distanceOnDay.get(time) == null)
				distanceOnDay.put(time, 0.0);
			if (libeDistanceOnDay.get(time) == null)
				libeDistanceOnDay.put(time, 0.0);
			if (timeOnDay.get(time) == null)
				timeOnDay.put(time, 0.0);
			if (libeTimeOnDay.get(time) == null)
				libeTimeOnDay.put(time, 0.0);
			double distance = GPSReading.getDistance(lastGpsReading,
					thisGpsReading);
			double interval = Math.abs(thisGpsReading.getTime()
					- lastGpsReading.getTime());
			distanceOnDay.put(time, distanceOnDay.get(time) + distance);
			timeOnDay.put(time, timeOnDay.get(time) + interval);
			if (thisGpsReading.isOccupied()) {
				libeDistanceOnDay.put(time, libeDistanceOnDay.get(time)
						+ distance);
				libeTimeOnDay.put(time, libeTimeOnDay.get(time) + interval);
			}
			lastGpsReading = thisGpsReading;
		}
	}
}
