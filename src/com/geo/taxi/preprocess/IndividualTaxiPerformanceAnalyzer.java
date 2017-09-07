package com.geo.taxi.preprocess;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Scanner;

import com.geo.taxi.config.Config;

public class IndividualTaxiPerformanceAnalyzer {

	public IndividualTaxiPerformanceAnalyzer(File f, PrintStream p,
			HashMap<Integer, int[][]> inGrid,
			HashMap<Integer, int[][]> huntInGrid) throws FileNotFoundException {
		Scanner sc = new Scanner(f);
		PrintStream pickUps = new PrintStream(Config.getOutputpath()
				+ f.getName());
		GPSReading lastReading = null;
		double distance = 0, liveDistance = 0;
		long time = 0, liveTime = 0;
		int numOfHunts = 0;
		int numOfTrips = 0;
		while (sc.hasNext()) {
			String line = sc.nextLine();
			GPSReading thisReading = GPSReading.parseLine(line);
			if (lastReading == null
					|| Math.abs(lastReading.time - thisReading.time) > Config
							.getMaxtimeinterval()) {

				if (lastReading != null) {
					putToGrid(lastReading, inGrid);
					if (lastReading.getStatus() == 1) {
						numOfHunts = processHunt(pickUps, lastReading,
								numOfHunts, huntInGrid);
					}
				}
				lastReading = thisReading;
				numOfTrips++;
				continue;
			} else {
				if (Config.getLatBin(thisReading.getLat()) != Config
						.getLatBin(lastReading.getLat())
						|| Config.getLonBin(thisReading.getLon()) != Config
								.getLonBin(lastReading.getLon())) {
					putToGrid(lastReading, inGrid);
				}
				long timeDifferential = lastReading.getTime()
						- thisReading.getTime();
				double distanceDiffrential = GPSReading.getDistance(
						lastReading, thisReading);
				distance += distanceDiffrential;
				time += timeDifferential;
				if (thisReading.getStatus() == 1) {
					liveDistance += distanceDiffrential;
					liveTime += timeDifferential;
				}
				if (thisReading.getStatus() == 0
						&& lastReading.getStatus() == 1) {
					numOfHunts = processHunt(pickUps, lastReading, numOfHunts,
							huntInGrid);
				}
				lastReading = thisReading;
			}
		}
		if (lastReading != null && lastReading.getStatus() == 1) {
			numOfHunts = processHunt(pickUps, lastReading, numOfHunts,
					huntInGrid);
		}
		// Printings
		double timePerformance = liveTime / ((double) time);
		double distancePerformance = liveDistance / distance;
		p.println(f.getName() + "\t" + timePerformance + "\t"
				+ distancePerformance + "\t" + numOfHunts + "\t" + liveTime
				+ "\t" + time + "\t" + liveDistance + "\t" + distance + "\t"
				+ numOfTrips);
	}

	private void putToGrid(GPSReading thisReading,
			HashMap<Integer, int[][]> grid) {
		int key = Config.hashTime(thisReading.getDate());
		if (grid.get(key) == null)
			grid.put(key,
					new int[Config.getNumoflatbins()][Config.getNumoflonbins()]);
		int x = Config.getLatBin(thisReading.getLat());
		int y = Config.getLonBin(thisReading.getLon());
		if (x >= 0 && y >= 0 && x < Config.getNumoflatbins() && y < Config.getNumoflonbins())
			grid.get(key)[x][y]++;
	}

	private int processHunt(PrintStream pickUps, GPSReading lastReading,
			int numOfHunts, HashMap<Integer, int[][]> huntInGrid) {
		numOfHunts++;
		putToGrid(lastReading, huntInGrid);
		pickUps.println(lastReading.getLat() + "\t" + lastReading.getLon()
				+ "\t" + lastReading.getStatus() + "\t" + lastReading.getTime());
		return numOfHunts;
	}
}
