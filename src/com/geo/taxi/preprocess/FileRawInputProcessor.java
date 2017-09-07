package com.geo.taxi.preprocess;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.Stack;

import com.geo.taxi.config.Config;

public class FileRawInputProcessor {
	public FileRawInputProcessor(File f, byte fromStatus, byte toStatus) throws FileNotFoundException {
		Scanner sc = new Scanner(f);
		Stack<GPSReading> pickUpLocations = new Stack<GPSReading>();
		GPSReading lastReading = new GPSReading(0, 0, (byte) 0, 0);
		while (sc.hasNext()) {
			String line = sc.nextLine();
			GPSReading thisReading = GPSReading.parseLine( line );
			// the condition is reversed because the file is in the reverse order of time!! :)
			if (lastReading.getStatus() == toStatus && thisReading.getStatus() == fromStatus ) {
				pickUpLocations.push( lastReading );
			}
			lastReading = thisReading;
		}
		saveResults(Config.getOutputpath(), f.getName(), pickUpLocations);
	}

	private void saveResults(String outputpath, String name,
			Stack<GPSReading> pickUpLocations) throws FileNotFoundException {
		PrintStream p = new PrintStream(new File(outputpath + name));
		while (!pickUpLocations.isEmpty()) {
			p.println( pickUpLocations.pop() );
		}
	}
}
