package com.geo.taxi.recommender;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class MyRecommender extends Recommender {
	public static void main(String[] args) throws FileNotFoundException {
		new MyRecommender();
	}

	private void assignRoute(ArrayList<Vehicle> vehicles,
			HashMap<Integer, Grid> graph) {
		for (Vehicle v : vehicles) {
			if (v.getRoute() == null || v.getRoute().isEmpty()) {
				System.err.println("assigning route to vehicle " + v.getId());
				assignRoute(v, graph);
			}
		}
	}

	private void assignRoute(Vehicle v, HashMap<Integer, Grid> graph) {
		HashSet<Grid> cluster = new HashSet<Grid>(v.getCluster());
		ArrayList<Grid> route = new ArrayList<Grid>();
		Grid g0 = v.getCurrentLocation();
		while (!cluster.isEmpty()) {
			Grid g = best_grid(g0, cluster, graph);
			if (g != null) {
				try {
					route.addAll(bestRoute(g0, g, graph));
				} catch (NullPointerException e) {
					throw e;
				}
			}
			g0 = g;
			cluster.remove(g);
		}
		v.setRoute(route);
	}

	private Collection<? extends Grid> bestRoute(Grid g0, Grid gd,
			HashMap<Integer, Grid> graph) {
		HashMap<Grid, ArrayList<Grid>> routes = new HashMap<Grid, ArrayList<Grid>>();
		HashMap<Grid, Double> objective = new HashMap<Grid, Double>();
		HashMap<Grid, Double> probabilitySuccess = new HashMap<Grid, Double>();
		HashMap<Grid, Double> probabilityFailure = new HashMap<Grid, Double>();
		HashMap<Grid, Double> distance = new HashMap<Grid, Double>();
		HashSet<Grid> visited = new HashSet<Grid>();
		objective.put(gd, Double.POSITIVE_INFINITY);
		for (Grid g : g0.getNeighbors()) {
			probabilitySuccess.put(g, g.getProbability());
			probabilityFailure.put(g, 1 - g.getProbability());
			distance.put(g, g0.getDistance(g));
			objective.put(g, probabilitySuccess.get(g) * distance.get(g));
			routes.put(g, new ArrayList<Grid>());
			routes.get(g).add(g);
		}
		while (!visited.contains(gd)) {
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
					double localObjective = minObjective
							+ probabilityFailure.get(gMin) * g.getProbability()
							* (distance.get(gMin) + gMin.getDistance(g));
					ArrayList<Grid> localRoute = new ArrayList<Grid>();
					localRoute.addAll(routes.get(gMin));
					localRoute.add(g);
					if (objective.get(g) == null
							|| objective.get(g) > localObjective) {
						objective.put(g, localObjective);
						routes.put(g, localRoute);
						probabilitySuccess.put(g, probabilityFailure.get(gMin)
								* g.getProbability());
						probabilityFailure.put(g, probabilityFailure.get(gMin)
								* (1 - g.getProbability()));
						distance.put(g,
								distance.get(gMin) + gMin.getDistance(g));
					}
				}
			}
		}
		return routes.get(gd);

	}

	private Grid best_grid(Grid g0, HashSet<Grid> cluster,
			HashMap<Integer, Grid> graph) {
		double objective = Double.POSITIVE_INFINITY;
		Grid bestGrid = null;
		for (Grid g : cluster) {
			double localObjective = g0.getDistance(g);
			if ((bestGrid == null || localObjective < objective)
					&& bestRoute(g0, g, graph) != null) {
				objective = localObjective;
				bestGrid = g;
			}
		}
		return bestGrid;
	}

	private void assignHighProbabilityGridsToVehicles(
			ArrayList<Vehicle> vehicles, HashSet<Grid> highProbabilityGrids) {
		for (Vehicle v : vehicles)
			if (v.getCluster() == null || v.getCluster().isEmpty())
				v.setCluster(new HashSet<Grid>());
		for (Grid g : highProbabilityGrids) {
			HashMap<Vehicle, Double> distance = new HashMap<Vehicle, Double>();
			for (Vehicle v : vehicles) {
				try {
					double d = v.getCurrentLocation().getDistance(g);
					distance.put(v, d);
				} catch (NullPointerException e) {
					System.out.println(v.getId());
					System.out.println(v.getCurrentLocation() + " " + g);
					throw e;
				}

			}
			while (g.getMaxNumberOfTaxis() > 0) {
				Vehicle vMin = null;
				double dMin = Double.POSITIVE_INFINITY;
				for (Vehicle v : vehicles) {
					if (distance.get(v) != null && distance.get(v) < dMin) {
						vMin = v;
						dMin = distance.get(v);
					}
				}
				if (vMin == null)
					break;
				vMin.getCluster().add(g);
				g.setMaxNumberOfTaxis(g.getMaxNumberOfTaxis() - 1);
				distance.remove(vMin);
			}
		}
		/*
		 * In case the vehicle's cluster is empty
		 */

		for (Vehicle v : vehicles) {
			if (v.getCluster().isEmpty()) {
				int sizeMax = 0;
				Vehicle vMax = null;
				for (Vehicle v2 : vehicles) {
					if (v2.getCluster().size() > sizeMax) {
						vMax = v2;
						sizeMax = v2.getCluster().size();
					}
				}
				if (sizeMax > 1) {
					double minDistance = Double.POSITIVE_INFINITY;
					Grid minG = null;
					for (Grid g : vMax.getCluster()) {
						if (v.getCurrentLocation().getDistance(g) < minDistance) {
							minDistance = v.getCurrentLocation().getDistance(g);
							minG = g;
						}
					}
					vMax.getCluster().remove(minG);
					v.getCluster().add(minG);
				}
			}
		}

	}

	private HashSet<Grid> getHighProbabilityGrids(HashMap<Integer, Grid> graph) {
		HashSet<Grid> highProbabilityGrids = new HashSet<Grid>();
		for (Grid g : graph.values()) {
			if (g.isHighProbability())
				highProbabilityGrids.add(g);
		}
		return highProbabilityGrids;
	}

	@Override
	public void Recommend(ArrayList<Vehicle> vehicles,
			HashMap<Integer, Grid> graph) {
		HashSet<Grid> highProbabilityGrids = getHighProbabilityGrids(graph);
		assignHighProbabilityGridsToVehicles(vehicles, highProbabilityGrids);
		assignRoute(vehicles, graph);
	}

}
