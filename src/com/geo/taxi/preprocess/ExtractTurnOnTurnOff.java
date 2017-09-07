package com.geo.taxi.preprocess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

import com.geo.taxi.config.Config;

public class ExtractTurnOnTurnOff {
	public static void main(String[] args) throws FileNotFoundException {
		HashMap<Integer, PrintStream> passengerPrintStreams = new HashMap<Integer, PrintStream>();
		File mainDir = new File(Config.getInputpath());
		int cnt = 0;
		if (mainDir.isDirectory()) {
			for (File f : mainDir.listFiles(Config.getFilefilter())) {
				processFile(f, passengerPrintStreams);
				System.out.println(cnt++);
				if (cnt > Config.getMaxnumberoffilestoread())
					break;
			}
		}
	}

	private static void processFile(File f,
			HashMap<Integer, PrintStream> passengerPrintStreams)
			throws FileNotFoundException {
		Scanner sc = new Scanner(f);
		PrintStream p = new PrintStream(new File(Config.getAnalysisoutputpath()
				+ "//TripsWithTurnOffs//" + f.getName()));
		HashMap<Integer, PrintStream> printStreams = new HashMap<Integer, PrintStream>();
		Integer DropOffLocation = null;
		GPSReading lastReading = null;
		while (sc.hasNext()) {
			GPSReading thisReading = GPSReading.parseLine(sc.nextLine().trim());
			if (lastReading == null) {
				if (thisReading.isOccupied())
					DropOffLocation = thisReading.getGrid();
				lastReading = thisReading;
			} else {
				if (map(lastReading.getDate()) != map(thisReading.getDate()))
					p.println(map(lastReading.getDate()) +"\t"+ lastReading.getGrid()+"\t" + lastReading.getTime());
				if (lastReading.getTime() - thisReading.getTime() > Config
						.getMaxtimeinterval()) {
					printTurnOnAndTurnOffs(f, printStreams, lastReading,
							thisReading);
					if (DropOffLocation != null) {
						Date d = lastReading.getDate();
						int id = map(d);
						if (passengerPrintStreams.get(id) == null) {
							passengerPrintStreams.put(id, new PrintStream(
									new File(Config.getAnalysisoutputpath()
											+ "//Passengers//" + id + ".txt")));
						}
						passengerPrintStreams.get(id).println(
								lastReading.getGrid() + "\t" + DropOffLocation
										+ "\t" + lastReading.getTime());
						DropOffLocation = null;
					}

				} else {
					if (thisReading.isOccupied() && !lastReading.isOccupied()) {
						// drop off
						DropOffLocation = lastReading.getGrid();
					} else if (!thisReading.isOccupied()
							&& lastReading.isOccupied()) {
						// pickup
						Date d = lastReading.getDate();
						int id = map(d);
						if (passengerPrintStreams.get(id) == null) {
							passengerPrintStreams.put(id, new PrintStream(
									new File(Config.getAnalysisoutputpath()
											+ "//Passengers//" + id + ".txt")));
						}
						passengerPrintStreams.get(id).println(
								lastReading.getGrid() + "\t" + DropOffLocation
										+ "\t" + lastReading.getTime());
						DropOffLocation = null;
					}
				}
				lastReading = thisReading;
			}
		}
	}

	private static void printTurnOnAndTurnOffs(File f,
			HashMap<Integer, PrintStream> printStreams, GPSReading lastReading,
			GPSReading thisReading) throws FileNotFoundException {
		Date turnOffDate = thisReading.getDate();
		Date turnOnDate = thisReading.getDate();
		int turnOffTime = map(turnOffDate);
		int turnOnTime = map(turnOnDate);
		if (printStreams.get(turnOffTime) == null)
			printStreams.put(turnOffTime,
					new PrintStream(Config.getAnalysisoutputpath()
							+ "//TripsWithTurnOffs//" + f.getName() + "-"
							+ turnOffTime + ".txt"));
//		if (printStreams.get(turnOnTime) == null)
//			printStreams.put(turnOnTime,
//					new PrintStream(Config.getAnalysisoutputpath()
//							+ "//TripsWithTurnOffs//" + f.getName() + "-"
//							+ turnOnTime + ".txt"));
		Date dayOff = new Date(turnOffDate.getYear(), turnOffDate.getMonth(),
				turnOffDate.getDate());
//		Date dayOn = new Date(turnOnDate.getYear(), turnOnDate.getMonth(),
//				turnOnDate.getDate());
//		printStreams.get(turnOnTime).println(
//				1 + "\t" + (lastReading.getTime() - dayOn.getTime() / 1000)
//						+ "\t" + lastReading.getGrid());
		printStreams.get(turnOffTime).println(
				0 + "\t" + (thisReading.getTime() - dayOff.getTime() / 1000)
						+ "\t"
						+ (lastReading.getTime() - thisReading.getTime())
						+ "\t" + lastReading.getGrid());
	}

	private static int map(Date d) {
		return (d.getYear() * 12 + d.getMonth()) * 31 + d.getDate() - 1;
	}
}
