/**
 * 
 */
package com.geo.taxi.recommender;

import java.lang.Math;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collection;
import java.util.Vector;
import java.io.FileNotFoundException;

import com.geo.taxi.config.Config;
import com.geo.taxi.recommender.Recommender;
import com.geo.taxi.recommender.QRTree;

/**
 * @author SL
 * 
 */
public class CKNNRecommender{

	private QRTree<Grid> qrtree;
	private HashMap<Integer, Grid> graph;
	private ArrayList<CKNNVehicle> vehicles;



	/**
	 * 
	 */
	public CKNNRecommender() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.geo.taxi.recommender.Recommender#Recommend(java.util.ArrayList,
	 * java.util.HashMap)
	 */
	public void Recommend(ArrayList<CKNNVehicle> vehiclesNeedRecommendation,
			HashMap<Integer, Grid> graph) {
		// TODO Auto-generated method stub
		this.graph = graph;
		this.vehicles = vehiclesNeedRecommendation;
	
		constructQRTree();
//		System.out.println("QRTree constructed");
		assignKHighProbablityGridsToVehicles();
//		System.out.println("Assign Hotspots successfully");
		assignRoute();
//		System.out.println("Assign route successfully");
	}
	
	/*
	 * Construct the qrtree of grids with high probabilities,
	 * 
	 * by convert the leaf nodes of qtree to rtrees
	 * 
	 */
	
	private void constructQRTree() {
		// construct a qtree with resolution of 2^n*2^n,
		// where 2^n >= max(Config.getNumoflatbins(), Config.getNumoflonbins())
		// in current work, n=6, i.e. 2^n = 64
		int maxLength = Math.max(Config.getNumoflatbins(),
				Config.getNumoflonbins());
		Dimension size = new Dimension();
		int n = 0;
		do {
			++n;
		} while (Math.pow(2, n) < maxLength);
		size.width = (int) Math.pow(2, n);
		size.height = (int) Math.pow(2, n);
		int maxDepth = 3;
		int maxNumOfElementsPerNode = 4;
		qrtree = new QRTree<Grid>(new Point(0, 0),
				size, maxDepth, maxNumOfElementsPerNode);

		for (Grid g : graph.values()) {
			if (g.isHighProbability()) {
				qrtree.insert(
						new Point(g.getLonBin(), g.getLatBin()), g);
			}
		}
		qrtree.buildRTrees();
//		System.err.println("tree");

	}

	/*
	 * assign k-nearest hot spots by querying the surrounding area of the
	 * vehicle
	 */

	private void implementAssginKGrids() {
		for (CKNNVehicle v : vehicles) {
			int vx = v.getCurrentLocation().getLonBin();
			int vy = v.getCurrentLocation().getLatBin();
			
			Vector<Grid> neighbors = new Vector<Grid>();
			
			// find k-nearest neighbors, 
			// the results are stored in neighbors, 
			// and the num of neighbors is neighbors.size(), which is less-equal than currentK
			qrtree.findKNeighbors(vx, vy, v.getCurrentK(), neighbors);
			
			if (neighbors.size() <= v.getCurrentK()) {
				for (Grid g : neighbors) {
					if (g.getMaxNumberOfTaxis() > 0) {
						v.getCluster().add(g);
						g.setMaxNumberOfTaxis(g.getMaxNumberOfTaxis() - 1);
					}

					
				}
			}
			else {
				HashMap<Grid, Double> distance = new HashMap<Grid, Double>();
				for (Grid g : neighbors) {
					try {
						double d = v.getCurrentLocation().getDistance(g);
						distance.put(g, d);
					} catch (NullPointerException e) {
//						System.out.println(v.getId());
//						System.out.println(v.getCurrentLocation() + " " + g);
						throw e;
					}

				}
				for (int i = 0; i < v.getCurrentK(); ++i) {
					if (0 == distance.size()) {
						break;
					}
					Grid gMin = null;
					double dMin = Double.POSITIVE_INFINITY;
					for (Grid g : neighbors) {
						if (distance.get(g) != null && distance.get(g) < dMin) {
							gMin = g;
							dMin = distance.get(g);
						}
					}
					if (gMin == null || gMin.getMaxNumberOfTaxis() < 1) {
						distance.remove(gMin);
						--i;
						break;
					}
					v.getCluster().add(gMin);
					gMin.setMaxNumberOfTaxis(gMin.getMaxNumberOfTaxis() - 1);
					distance.remove(v);
					
				}
			}
			v.setCurrentK(v.getCurrentK() - 1);

		}
	}

	/*
	 * assign k-nearest hot spots to each vehicle
	 */
	private void assignKHighProbablityGridsToVehicles() {
		// clear the cluster of hot spots for each vehicle
		for (Vehicle v : vehicles) {
			if (v.getCluster() == null || v.getCluster().isEmpty()) {
				v.setCluster(new HashSet<Grid>());
			}

		}

		implementAssginKGrids();

		/*
		 * In case the vehicle's cluster is empty
		 */

//		for (CKNNVehicle v : vehicles) {
//			if (v.getCluster().isEmpty()) {
//				int sizeMax = 0;
//				CKNNVehicle vMax = null;
//				for (CKNNVehicle v2 : vehicles) {
//					if (v2.getCluster().size() > sizeMax) {
//						vMax = v2;
//						sizeMax = v2.getCluster().size();
//					}
//				}
//				if (sizeMax > 1) {
//					double minDistance = Double.POSITIVE_INFINITY;
//					Grid minG = null;
//					for (Grid g : vMax.getCluster()) {
//						if (v.getCurrentLocation().getDistance(g) < minDistance) {
//							minDistance = v.getCurrentLocation().getDistance(g);
//							minG = g;
//						}
//					}
//					vMax.getCluster().remove(minG);
//					v.getCluster().add(minG);
//				}
//			}
//		}
	}


	// assign route for each vehicle
	private void assignRoute() {
		for (CKNNVehicle v : vehicles) {
			if (v.getRoute() == null || v.getRoute().isEmpty()) {
				System.err.println("assigning route to vehicle " + v.getId());
				assignRoute(v);
			}
		}
	}

	
	// add k to find the route passing the top-k high probability grids
	private void assignRoute(CKNNVehicle v) {
		HashSet<Grid> cluster = new HashSet<Grid>(v.getCluster());
		ArrayList<Grid> route = new ArrayList<Grid>();
		Grid g0 = v.getCurrentLocation();
		while (!cluster.isEmpty()) {
			Grid g = best_grid(g0, cluster);
			if (g != null) {
				try {
					route.addAll(bestRoute(g0, g));
				} catch (NullPointerException e) {
					throw e;
				}
			}
			g0 = g;
			cluster.remove(g);
		}
		v.setRoute(route);
	}

	
	private Collection<? extends Grid> bestRoute(Grid g0, Grid gd) {
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

	private Grid best_grid(Grid g0, HashSet<Grid> cluster) {
		double objective = Double.POSITIVE_INFINITY;
		Grid bestGrid = null;
		for (Grid g : cluster) {
			double localObjective = g0.getDistance(g);
			if ((bestGrid == null || localObjective < objective)
					&& bestRoute(g0, g) != null) {
				objective = localObjective;
				bestGrid = g;
			}
		}
		return bestGrid;
	}

}
