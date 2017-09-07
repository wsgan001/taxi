package com.geo.taxi.recommender;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

import com.geo.taxi.config.Config;
import com.geo.taxi.preprocess.GPSReading;

public class Grid {
	private int latBin, lonBin;
	/*
	 * Stores the neighboring grids of this grid
	 */
	private HashSet<Grid> neighbors;
	/*
	 * Stores the average time to go from this grid to a neighboring grid
	 */
	private HashMap<Grid, Double> time;
	/*
	 * Stores the distance between the center point of this grid and the center
	 * point of a neighboring grid.
	 */
	private HashMap<Grid, Double> distance;

	private double probability;

	private int maxNumberOfTaxis;

	private ArrayList<Passenger> passengers;

	private Grid() {
		neighbors = new HashSet<Grid>();
		time = new HashMap<Grid, Double>();
		distance = new HashMap<Grid, Double>();
		passengers = new ArrayList<Passenger>();
	}

	public Grid(int latBin, int lonBin) {
		this();
		this.latBin = latBin;
		this.lonBin = lonBin;
	}

	public Grid(int id) {
		this();
		latBin = unHashLatBin(id);
		lonBin = unHashLonBin(id);
	}

	private int unHashLonBin(int id) {
		return id % Config.getNumoflonbins();
	}

	private int unHashLatBin(int id) {
		return id / Config.getNumoflonbins();
	}

	/*
	 * adds a grid to the neighbors of this grid.
	 */
	public void addNeighbor(Grid g, double time) {
		if (this.hashCode() != g.hashCode()) {
			this.neighbors.add(g);
			this.time.put(g, time);
			double distance = GPSReading.getDistance(this.getCenterPoint(),
					g.getCenterPoint());
			this.distance.put(g, distance);
		}
	}

	/*
	 * returns the center point of the grid
	 */
	public GPSReading getCenterPoint() {
		double lat = (this.latBin + .5) * Config.getAnglechunk()
				+ Config.getMaxlat();
		double lon = (this.lonBin + .5) * Config.getAnglechunk()
				+ Config.getMaxlat();
		return new GPSReading(lat, lon, (byte) 0, 0);
	}

	@Override
	public int hashCode() {
		return latBin * Config.getNumoflonbins() + lonBin;
	}

	@Override
	public String toString() {
		return this.getName();
	}

	private String getName() {
		return "(" + latBin + "," + lonBin + ")";
	}

	public boolean isHighProbability() {
		if ( this.latBin == 40 && this.lonBin == 31 )
			try {
				new PrintStream("ali.txt").println(this + " " + this.getProbability() );
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		if (this.probability > Config.getProbabilityThreshold())
			return true;
		else
			return false;
	}

	public int getLatBin() {
		return latBin;
	}

	public void setLatBin(int latBin) {
		this.latBin = latBin;
	}

	public int getLonBin() {
		return lonBin;
	}

	public void setLonBin(int lonBin) {
		this.lonBin = lonBin;
	}

	public HashSet<Grid> getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(HashSet<Grid> neighbors) {
		this.neighbors = neighbors;
	}

	public HashMap<Grid, Double> getTime() {
		return time;
	}

	public void setTime(HashMap<Grid, Double> time) {
		this.time = time;
	}

	public HashMap<Grid, Double> getDistance() {
		return distance;
	}

	public void setDistance(HashMap<Grid, Double> distance) {
		this.distance = distance;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

	public int getMaxNumberOfTaxis() {
		return maxNumberOfTaxis;
	}

	public void setMaxNumberOfTaxis(int maxNumberOfTaxis) {
		this.maxNumberOfTaxis = maxNumberOfTaxis;
	}

	public double getDistance(Grid g) {
		return GPSReading
				.getDistance(this.getCenterPoint(), g.getCenterPoint());
	}

	public double getObjective(Grid g) {
		return this.getDistance(g);
	}

	public ArrayList<Passenger> getPassengers() {
		return passengers;
	}

	public double getTime(Grid g) {
		return this.getTime().get(g);
	}

}
