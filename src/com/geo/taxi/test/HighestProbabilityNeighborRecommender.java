package com.geo.taxi.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.geo.taxi.recommender.Grid;
import com.geo.taxi.recommender.Recommender;
import com.geo.taxi.recommender.Vehicle;

public class HighestProbabilityNeighborRecommender extends Recommender {

	@Override
	public void Recommend(ArrayList<Vehicle> vehicles, HashMap<Integer, Grid> graph) {
		for (Vehicle v : vehicles) {
			if (v.getRoute() == null || v.getRoute().isEmpty()) {
				recommendRouteFor(v, graph);
			}
		}
	}

	private void recommendRouteFor(Vehicle v, HashMap<Integer, Grid> graph) {
		Grid selected = null;
		for (Grid g : v.getCurrentLocation().getNeighbors()) {
			if (selected == null || g.getProbability() > selected.getProbability())
				selected = g;
		}
		v.setCluster(new HashSet<Grid>());
		v.getCluster().add(selected);
		v.setRoute(new ArrayList<Grid>());
		v.getRoute().add(selected);
	}

}
