package com.geo.taxi.recommender;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Task {
	public abstract void execute( HashMap<Long, ArrayList<Task>> tasks, long currentTime, HashMap<Integer, Grid> graph );
}
