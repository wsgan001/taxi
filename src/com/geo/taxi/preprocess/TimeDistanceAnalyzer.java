package com.geo.taxi.preprocess;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Scanner;

import com.geo.taxi.config.Config;

public class TimeDistanceAnalyzer {
	HashMap<Long, Integer> timeIntervalCounter;
	HashMap<Integer, Integer> distanceCounter;
	private final int countLimit = 1000;
	public TimeDistanceAnalyzer() {
		timeIntervalCounter = new HashMap<Long, Integer>();
		distanceCounter = new HashMap<Integer, Integer>();
	}

	public static void main(String[] args) throws Exception {
		File inputFolder = new File(Config.getInputpath());
		new TimeDistanceAnalyzer().processFolder(inputFolder);

	}

	private void processFolder(File inputFolder) throws Exception {
		if (inputFolder.isDirectory()) {
			File[] files = inputFolder.listFiles(Config.getFilefilter());
			int counter = 1;
			PrintStream p = new PrintStream(new File("time-dist.txt"));
			for (File f : files) {
				System.out.println(f.getName() + " " + counter);
				processFile(f, p);
				if (++counter > countLimit)
					break;
			}
			printResults(new PrintStream("time.txt"), new PrintStream(
					"dist.txt"));
		} else {
			throw new Exception("input path is not a folder");
		}

	}

	private void printResults(PrintStream p1, PrintStream p2) {
		printTimeIntervalResults(p1);
		printDistanceIntervalResults(p2);
	}

	private void printDistanceIntervalResults(PrintStream p) {
		for (int i : distanceCounter.keySet())
			p.println((i + 1) * distChunk + "\t" + distanceCounter.get(i));
	}

	private void printTimeIntervalResults(PrintStream p) {
		for (long l : timeIntervalCounter.keySet())
			p.println((l + 1) * timeChunk + "\t" + timeIntervalCounter.get(l));
	}

	private void processFile(File f, PrintStream p) throws FileNotFoundException {
		Scanner fileScanner = new Scanner(f);
		
		GPSReading lastLine = GPSReading.parseLine(fileScanner.nextLine());
		while (fileScanner.hasNext()) {
			GPSReading thisLine = GPSReading.parseLine(fileScanner.nextLine());
			long timeInterval = timeInterval(Math.abs(lastLine.getTime()
					- thisLine.getTime()));
			int distanceInterval = distanceInterval(GPSReading.getDistance(
					lastLine, thisLine));
			p.println(Math.abs(lastLine.getTime() - thisLine.getTime()) + "\t"
					+ GPSReading.getDistance(lastLine, thisLine));
			if (timeIntervalCounter.get(timeInterval) == null)
				timeIntervalCounter.put(timeInterval, 1);
			else
				timeIntervalCounter.put(timeInterval,
						timeIntervalCounter.get(timeInterval) + 1);

			if (distanceCounter.get(distanceInterval) == null)
				distanceCounter.put(distanceInterval, 1);
			else
				distanceCounter.put(distanceInterval,
						distanceCounter.get(distanceInterval) + 1);
			lastLine = thisLine;
		}
	}

	private final int timeChunk = 20;
	private final int distChunk = 50;

	private long timeInterval(long l) {
		return l / timeChunk;
	}

	private int distanceInterval(double distance) {
		return (int) (distance / distChunk);
	}
}
