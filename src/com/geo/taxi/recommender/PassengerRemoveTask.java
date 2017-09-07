package com.geo.taxi.recommender;

import java.util.ArrayList;
import java.util.HashMap;

public class PassengerRemoveTask extends Task {

	Passenger p;
	public PassengerRemoveTask(Passenger p) {
		this.p = p;
	}

	@Override
	public void execute(HashMap<Long, ArrayList<Task>> tasks, long currentTime,
			HashMap<Integer, Grid> graph) {
		p.getLocation().getPassengers().remove( p );
		System.err.println( "Passenger removed from " + p.getLocation() );
	}

}
