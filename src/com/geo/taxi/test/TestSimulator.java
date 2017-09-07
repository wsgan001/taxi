package com.geo.taxi.test;

import java.io.FileNotFoundException;
import java.util.Date;

import com.geo.taxi.recommender.MyRecommender;
import com.geo.taxi.recommender.Simulator;

public class TestSimulator {
	public static void main(String[] args) throws FileNotFoundException {
		int[] numOfTaxisArray = new int[] {  30,50,70,90 };
		for (int numberOfTaxis : numOfTaxisArray) {
			Date d = new Date(2008 - 1900, 5, 7);
			System.err.println(d);
			long date = d.getTime() / 1000;
			Simulator sim2 = new Simulator(
					new HighestProbabilityNeighborRecommender(), date,
					date + 24 * 3600, numberOfTaxis);
			sim2.start();
			Simulator sim = new Simulator(new MyRecommender(), date,
					date + 24*3600, numberOfTaxis);
			sim.start();
		}
	}
}
