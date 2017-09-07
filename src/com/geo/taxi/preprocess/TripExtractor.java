package com.geo.taxi.preprocess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.Stack;

import com.geo.taxi.config.Config;

public class TripExtractor {
	public static void main(String[] args) throws FileNotFoundException {
		File dir = new File(Config.getInputpath());
		int cnt = 0;
		if (dir.isDirectory()) {
			File[] files = dir.listFiles(Config.getFilefilter());
			for (File f : files) {
				processFile( f );
				System.out.println(++cnt + " out of " + files.length);
				if (cnt >= Integer.MAX_VALUE)
					break;

			}
		}
	}

	private static void processFile(File f) throws FileNotFoundException {
		Scanner sc = new Scanner( f );
		String name = f.getName().substring(0,f.getName().indexOf('.'));
		File resultsFolder = new File(Config.getAnalysisoutputpath()+"/Trips/");
		if (!resultsFolder.isDirectory() )
			return;
		else{
			resultsFolder = new File( Config.getAnalysisoutputpath()+"/Trips/"+name+"/" );
			if (! resultsFolder.isDirectory() )
				resultsFolder.mkdir();
		}
		
		Stack<GPSReading> trip = new Stack<GPSReading>(); 
		while (sc.hasNext()){
			GPSReading thisReading = GPSReading.parseLine(sc.nextLine().trim());
			if ( trip.isEmpty() || Math.abs(trip.peek().getTime()-thisReading.getTime()) < Config.getMaxtimeinterval() ){
				trip.add( thisReading );
			}else{
				printTrip(trip, name);
				trip = new Stack<GPSReading>();
				trip.add(thisReading);
			}
		}
		printTrip(trip,name);
	}

	private static void printTrip(Stack<GPSReading> trip, String name) throws FileNotFoundException {
		String fileName ="";
		fileName += trip.peek().getDate().getYear();
		fileName += "-"+trip.peek().getDate().getMonth();
		fileName += "-"+trip.peek().getDate().getDate();
		fileName += "-"+trip.peek().getDate().getHours();
		fileName += "-"+trip.peek().getDate().getMinutes();
		PrintStream p = new PrintStream(new File(Config.getAnalysisoutputpath()+"/Trips/"+name+"/"+fileName+"."+Config.getExtension()));
		while (!trip.isEmpty()){
			p.println( trip.pop() );
		}
	}
}
