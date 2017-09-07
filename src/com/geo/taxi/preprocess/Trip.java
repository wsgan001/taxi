package com.geo.taxi.preprocess;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Trip {
	String vehicleID;
	ArrayList<GPSReading> trajectory;

	public Trip(String id) {
		this.vehicleID = id;
		trajectory = new ArrayList<GPSReading>();
	}

	public Trip(String name, File f) throws FileNotFoundException {
		this(name);
		processFile(f);
	}

	private void processFile(File f) throws FileNotFoundException {
		Scanner sc = new Scanner( f );
		while (sc.hasNext())
			this.add( GPSReading.parseLine(sc.nextLine().trim()));
	}

	private void add(GPSReading g) {
		trajectory.add(g);
	}

	public double getDistance() {
		double distance = 0;
		for (int i = 1; i < trajectory.size(); i++)
			distance += GPSReading.getDistance(trajectory.get(i - 1),
					trajectory.get(i));
		return distance;
	}

	public ArrayList<GPSReading> getPickUps() {
		ArrayList<GPSReading> pickups = new ArrayList<GPSReading>();
		if (trajectory.size() >1 && trajectory.get(0).isOccupied() && trajectory.get(1).isOccupied())
			pickups.add(trajectory.get(1));
		for (int i = 2; i < trajectory.size(); i++)
			if (trajectory.get(i).isOccupied()
					&& !trajectory.get(i - 1).isOccupied())
				pickups.add(trajectory.get(i));
		return pickups;
	}

	public ArrayList<GPSReading> getDropOffs() {
		ArrayList<GPSReading> dropOffs = new ArrayList<GPSReading>();
		for (int i = 0; i < trajectory.size(); i++)
			if (!trajectory.get(i).isOccupied()
					&& trajectory.get(i - 1).isOccupied())
				dropOffs.add(trajectory.get(i));
		return dropOffs;
	}

	public double getLiveDistance() {
		double distance = 0;
		for (int i = 1; i < trajectory.size(); i++)
			if (trajectory.get(i - 1).isOccupied())
				distance += GPSReading.getDistance(trajectory.get(i - 1),
						trajectory.get(i));
		return distance;
	}
	
	public long getTime(){
		return trajectory.get(trajectory.size()-1).getTime()-trajectory.get(0).getTime();
	}
	
	public long getLiveTime(){
		long liveTime = 0;
		for (int i = 1; i < trajectory.size(); i++)
			if (trajectory.get(i - 1).isOccupied())
				liveTime += trajectory.get(i).getTime()-trajectory.get(i-1).getTime();
		return liveTime;
	}
	
	public double getDistancePerformance(){
		return this.getLiveDistance()/this.getDistance();
	}
	
	public double getTimePerformance(){
		return this.getLiveTime()/this.getTime();
	}
	
	public ArrayList<Trip> getLiveSubTrips(){
		ArrayList<Trip> liveSubTrips = new ArrayList<Trip>();
		for (int i = 0 ; i < trajectory.size() ; i++ ){
			if (trajectory.get(i).isOccupied())
			{
				Trip t = new Trip(vehicleID);
				t.add( trajectory.get(i));
				for ( ;i < trajectory.size() ; i++ ){
					if ( trajectory.get(i-1).isOccupied() )
						t.add( trajectory.get(i));
					else
						break;
				}
				liveSubTrips.add(t);
			}
		}
		return liveSubTrips;
	}

	public int size() {
		return trajectory.size();
	}
	
	public ArrayList<GPSReading> getTrajectory() {
		return trajectory;
	}
	
	public String getVehicleID() {
		return vehicleID;
	}
}
