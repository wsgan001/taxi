package com.geo.taxi.recommender;

import java.util.ArrayList;
import java.util.HashSet;

public class Vehicle {
	private String id;
	private HashSet<Grid> cluster;
	private ArrayList<Grid> route;
	private Grid currentLocation;
	boolean isOccupied;
	private double distanceTravelled;
	private double liveDistanceTravelled;
	private double timeTravelled;
	private double liveTimeTravelled;
	private int numOfHunts;
	private boolean isOn;

	public boolean isOn() {
		return isOn;
	}
	public void setOn() {
		this.isOn = true;
	}
	public void setOff() {
		this.isOn = false;
	}
	public Vehicle(String id, Grid currentGrid) {
		this.setId(id);
		this.setCurrentLocation(currentGrid);
		distanceTravelled = 0;
		liveDistanceTravelled = 0;
		numOfHunts= 0;
		this.setOff();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Grid getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(Grid currentLocation) {
		this.currentLocation = currentLocation;
	}

	@Override
	public String toString() {
		return id + " " + currentLocation;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	public HashSet<Grid> getCluster() {
		return cluster;
	}

	public void setCluster(HashSet<Grid> cluster) {
		this.cluster = cluster;
	}

	public ArrayList<Grid> getRoute() {
			return route;
	}

	public void setRoute(ArrayList<Grid> route) {
		this.route = route;
	}

	public double getRouteTime() {
		if (this.getRoute() == null || this.getRoute().isEmpty())
			return -1;
		else {
			Grid g0 = currentLocation;
			double sum = 0;
			for (Grid g : this.getRoute()) {
				sum += g0.getTime().get(g);
				g0 = g;
			}
			return sum;
		}
	}
	
	public void setDistanceTravelled(double distanceTravelled) {
		this.distanceTravelled = distanceTravelled;
	}
	
	public void setLiveDistanceTravelled(double liveDistanceTravelled) {
		this.liveDistanceTravelled = liveDistanceTravelled;
	}
	
	public double getDistanceTravelled() {
		return distanceTravelled;
	}
	
	public double getLiveDistanceTravelled() {
		return liveDistanceTravelled;
	}
	
	public boolean isOccupied() {
		return isOccupied;
	}

	public void hunt() {
		numOfHunts++;
		isOccupied = true;
	}
	public int getNumOfHunts() {
		return numOfHunts;
	}

	public Grid getRoute(int i) {
		return this.getRoute().get(i);
	}

	public double getDistancePerformance() {
		return this.getLiveDistanceTravelled()/this.getDistanceTravelled();
	}
	public double getLiveTimeTravelled() {
		return liveTimeTravelled;
	}
	public double getTimeTravelled() {
		return timeTravelled;
	}
	public void setLiveTimeTravelled(double liveTimeTravelled) {
		this.liveTimeTravelled = liveTimeTravelled;
	}
	public void setTimeTravelled(double timeTravelled) {
		this.timeTravelled = timeTravelled;
	}
	public double getTimePerformance() {
		return this.getLiveTimeTravelled()/this.getTimeTravelled();
	}
}
