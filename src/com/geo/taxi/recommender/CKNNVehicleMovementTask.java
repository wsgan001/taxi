package com.geo.taxi.recommender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CKNNVehicleMovementTask extends Task {

	private CKNNVehicle v;
	public CKNNVehicleMovementTask(CKNNVehicle v) {
		// TODO Auto-generated constructor stub
		this.v = v;
	}

	@Override
	public void execute(HashMap<Long, ArrayList<Task>> tasks, long currentTime,
			HashMap<Integer, Grid> graph) {
		// TODO Auto-generated method stub
		if (v.isOn()) {
			Grid from = v.getCurrentLocation();
			System.err.println(v.getId() + " reached " + from);
			if (!v.isOccupied() && from.getPassengers() != null
					&& !from.getPassengers().isEmpty()) {
				ArrayList<Grid> path = null;
				Passenger p = null;
				do {
					p = v.getCurrentLocation().getPassengers().get(0);
					v.getCurrentLocation().getPassengers().remove(0);
					path = shortestPath(v.getCurrentLocation(),
							p.getDestination());
				} while (path != null
						&& !v.getCurrentLocation().getPassengers().isEmpty());
				if (path != null) {
					v.hunt();
					System.err.println( v.getId() + " HUNT!" );
					freeCluster(v);
					v.setCluster(new HashSet<Grid>());
					v.getCluster().add(p.getDestination());
					v.setRoute(path);
				}
				// when picked up a passenger, set the currentK to K
				// v.setCurrentK(CKNNVehicle.getK());
			}
			
			if (0 == v.getRoute().size()) {
				return;
			}
			
			Grid to = v.getRoute().get(0);
			double tripTime = from.getTime(to);
			v.setMoveStepTime(v.getMoveStepTime() + 1);
			if (v.getMoveStepTime() >= tripTime) {
				v.setMoveStepTime(v.getMoveStepTime() - tripTime);
				v.getRoute().remove(0);
				v.setCurrentLocation(to);
				double distance = from.getDistance(to);
				v.setDistanceTravelled(v.getDistanceTravelled() + distance);
				v.setTimeTravelled(v.getTimeTravelled() + tripTime);
				if (v.isOccupied()) {
					v.setLiveDistanceTravelled(v.getLiveDistanceTravelled()
							+ distance);
					v.setLiveTimeTravelled(v.getLiveTimeTravelled() + tripTime);
				}
				if (v.isOccupied() && v.getRoute().isEmpty()) {
					v.isOccupied = false;
					v.setCurrentK(CKNNVehicle.getK());
				}
				if (from.isHighProbability() && v.getCluster().contains(from)) {
					v.getCluster().remove(from);
					from.setMaxNumberOfTaxis(from.getMaxNumberOfTaxis() + 1);
				}
				if (!v.isOccupied() && to.isHighProbability() && v.getCluster().contains(to)) {
					v.setCurrentK(v.getCurrentK() - 1);
					if (0 == v.getCurrentK()) {
						v.setOff();
					}
					v.setArrivedHotspot(true);
				}
			}
	
			if (!v.isOn()) {
				return;
			}
			long time = currentTime + (long) from.getTime(to) + 1;
			ArrayList<Task> taskList = tasks.get(time);
			if (taskList == null) {
				taskList = new ArrayList<Task>();
				tasks.put(time, taskList);
			}
			taskList.add(new CKNNVehicleMovementTask(v));
			System.err.println("Task created for time " + time);


		}
	}
	
	private void freeCluster(Vehicle v2) {
		for (Grid g : v2.getCluster())
			g.setMaxNumberOfTaxis(g.getMaxNumberOfTaxis() + 1);
	}

	private ArrayList<Grid> shortestPath(Grid currentLocation, Grid destination) {
		HashMap<Grid, ArrayList<Grid>> routes = new HashMap<Grid, ArrayList<Grid>>();
		HashMap<Grid, Double> objective = new HashMap<Grid, Double>();
		HashSet<Grid> visited = new HashSet<Grid>();
		objective.put(destination, Double.POSITIVE_INFINITY);
		for (Grid g : currentLocation.getNeighbors()) {
			objective.put(g, currentLocation.getDistance(g));
			routes.put(g, new ArrayList<Grid>());
			routes.get(g).add(g);
		}
		while (!visited.contains(destination)) {
			Grid gMin = null;
			double minObjective = Double.POSITIVE_INFINITY;
			for (Grid g : objective.keySet()) {
				if (minObjective > objective.get(g) && !visited.contains(g)) {
					gMin = g;
					minObjective = objective.get(g);
				}
			}
			if (gMin == null) {
				return null;
			}
			visited.add(gMin);
			if (gMin.getNeighbors() != null) {
				for (Grid g : gMin.getNeighbors()) {
					double localObjective = minObjective + gMin.getDistance(g);
					ArrayList<Grid> localRoute = new ArrayList<Grid>();
					localRoute.addAll(routes.get(gMin));
					localRoute.add(g);
					if (objective.get(g) == null
							|| objective.get(g) > localObjective) {
						objective.put(g, localObjective);
						routes.put(g, localRoute);
					}
				}
			}
		}
		return routes.get(destination);
	}


}
