package com.geo.taxi.recommender;

import java.util.ArrayList;
import java.util.HashMap;

public class VehicleTurnOnTask extends Task {
	Vehicle v;
	Grid location;
	public VehicleTurnOnTask(Vehicle vehicle, Grid g) {
		this.v = vehicle;
		this.location = g;
	}

	@Override
	public void execute(HashMap<Long, ArrayList<Task>> tasks, long currentTime,
			HashMap<Integer, Grid> graph) {
		v.setCurrentLocation( location );
		v.setOn();
		System.err.println( v.getId() + " Turned On in " + v.getCurrentLocation() );
		if (tasks.get( currentTime+1 ) == null)
			tasks.put(currentTime+1, new ArrayList<Task>());
		tasks.get(currentTime+1).add( new VehicleMovementTask(v));
	}

}
