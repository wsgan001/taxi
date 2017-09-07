package com.geo.taxi.recommender;

import java.util.ArrayList;
import java.util.HashMap;

public class PassengerAppearance extends Task {

	Passenger p;

	public PassengerAppearance(Passenger p) {
		this.p = p;
	}

	@Override
	public void execute(HashMap<Long, ArrayList<Task>> tasks, long currentTime,
			HashMap<Integer, Grid> graph) {
		p.getLocation().getPassengers().add(p);
		System.err.println("Passenger Created in " + p.getLocation());
		System.err.println("Passenger will be removed at " + currentTime
				+ p.getWaitingTime());
		if (tasks.get(currentTime + p.getWaitingTime()) == null)
			tasks.put(currentTime + p.getWaitingTime(), new ArrayList<Task>());
		tasks.get(currentTime + p.getWaitingTime()).add(
				new PassengerRemoveTask(p));
	}
}
