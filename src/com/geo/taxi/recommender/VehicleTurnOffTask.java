package com.geo.taxi.recommender;

import java.util.ArrayList;
import java.util.HashMap;

public class VehicleTurnOffTask extends Task {

	private Vehicle v;
	private long period;
	private Grid location;
	public VehicleTurnOffTask(Vehicle vehicle, long time, Grid location) {
		this.v = vehicle;
		this.period = time;
	}

	@Override
	public void execute(HashMap<Long, ArrayList<Task>> tasks, long currentTime,
			HashMap<Integer, Grid> graph) {
		v.setOff();
		v.setRoute(null);
		if (v.getCluster() != null) {
			for (Grid g : v.getCluster())
				g.setMaxNumberOfTaxis(g.getMaxNumberOfTaxis() + 1);
			v.setCluster(null);
		}
		System.err.println(v.getId() + " turned off");
		long time = currentTime + period;
		if (tasks.get(time) == null)
			tasks.put(time, new ArrayList<Task>());
		tasks.get(time).add(new VehicleTurnOnTask(v, location));
		System.err.println("Will turn on again at : " + time);
	}

}
