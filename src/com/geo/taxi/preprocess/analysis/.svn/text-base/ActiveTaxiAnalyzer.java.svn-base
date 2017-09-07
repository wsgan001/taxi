package com.geo.taxi.preprocess.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;

import com.geo.taxi.config.Config;
import com.geo.taxi.preprocess.GPSReading;
import com.geo.taxi.preprocess.Trip;

public class ActiveTaxiAnalyzer {
	String name;
	HashMap<Integer,HashSet<String>> activeTaxis;
	TimeMapper timeMapper;
	public ActiveTaxiAnalyzer( TimeMapper mapper, String name ) {
		activeTaxis = new HashMap<Integer, HashSet<String>>();
		this.timeMapper = mapper;
		this.name = name;
	}
	public void add(Trip t) {
		for (GPSReading g : t.getTrajectory()){
			int time = timeMapper.map(g.getDate());
			if ( activeTaxis.get(time) == null )
				activeTaxis.put(time, new HashSet<String>());
			activeTaxis.get(time).add(t.getVehicleID());
		}
	}
	public void print(String outputpath) throws FileNotFoundException {
		PrintStream p = new PrintStream( new File(outputpath+this.name+".txt"));
		for (int key : activeTaxis.keySet() )
			p.println( timeMapper.getString( key ) +"\t"+activeTaxis.get(key).size());
	}
	
}
