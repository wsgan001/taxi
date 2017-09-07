package com.geo.taxi.preprocess.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;

import com.geo.taxi.config.Config;
import com.geo.taxi.preprocess.GPSReading;
import com.geo.taxi.preprocess.Trip;

public class PickUpAnalyzer {
	
	HashMap<Integer, PrintStream> printStreams;
	String path;
	TimeMapper mapper;
	String name;
	public PickUpAnalyzer(TimeMapper mapper, String string) {
		this.mapper = mapper;
		this.name = string;
		this.path = Config.getAnalysisoutputpath()+"pickUps/"+name+"/";
		printStreams = new HashMap<Integer, PrintStream>();
	}
	
	public void add(GPSReading pickup) throws FileNotFoundException {
		int time = mapper.map( pickup.getDate() );
		if (printStreams.get(time) == null )
			printStreams.put(time, new PrintStream(new File(path+mapper.getName(time)+".txt")));
		printStreams.get(time).println( pickup );
	}
}
