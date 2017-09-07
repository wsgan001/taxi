package com.geo.taxi.preprocess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;

import com.geo.taxi.config.Config;

public class TaxiDriverAnalyzer {
	HashMap<String, Integer> mapper;
	double[] distance;
	double[] liveDistance;
	long[] time;
	long[] liveTime;
	int[] numOfTrips;
	public TaxiDriverAnalyzer() {
		mapper= new HashMap<String, Integer>();
		distance = new double[1000];
		liveDistance = new double[1000];
		time = new long[1000];
		liveTime = new long[1000];
		numOfTrips = new int[1000];
	}
	public void add(Trip t) {
		if ( !mapper.containsKey(t.getVehicleID()) ){
			mapper.put(t.vehicleID, mapper.size() );
		}
		int id = mapper.get(t.vehicleID);
		distance[id] += t.getDistance();
		liveDistance[id]+=  t.getLiveDistance();
		time[id] += t.getTime();
		liveTime[id]+= t.getLiveTime();
		numOfTrips[id] ++;
	}
	public void print() throws FileNotFoundException {
		PrintStream p = new PrintStream( new File(Config.getAnalysisoutputpath()+"taxiDriverAnalysis.txt"));
		p.println( "id\ttime\tliveTime\tdistance\tliveDistance\tnumOfTrips");
		for ( String s : mapper.keySet() ){
			int id = mapper.get(s);
			p.println(s+"\t"+time[id]+"\t"+liveTime[id]+"\t"+distance[id]+"\t"+liveDistance[id]+"\t"+numOfTrips[id]);
		}
	}
}
