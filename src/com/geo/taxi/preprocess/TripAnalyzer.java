package com.geo.taxi.preprocess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;

import javax.jws.Oneway;

import com.geo.taxi.config.Config;
import com.geo.taxi.preprocess.analysis.ActiveTaxiAnalyzer;
import com.geo.taxi.preprocess.analysis.PickUpAnalyzer;
import com.geo.taxi.preprocess.analysis.TODOWTimeMapper;
import com.geo.taxi.preprocess.analysis.TODOYTimeMapper;
import com.geo.taxi.preprocess.analysis.TODTimeMapper;
import com.geo.taxi.test.TestGraph;

public class TripAnalyzer {
	public static void main(String[] args) throws FileNotFoundException {
//		new TripAnalyzer(new File(Config.getAnalysisoutputpath() + "/Trips/"));
//		new GridTripAnalyzer(new File(Config.getAnalysisoutputpath()
//				+ "/GridTrips/"));
		TestGraph.main(null);
	}

	ActiveTaxiAnalyzer[] activeTaxiAnalyzers;
	PickUpAnalyzer[] pickUpAnalyzers;
	TaxiDriverAnalyzer taxiDriverAnalyzer;
	private GraphBuilder graphBuilder;

	public TripAnalyzer(File mainDir) throws FileNotFoundException {
		this();
		int cnt = 0;
		if (mainDir.isDirectory()) {
			for (File subDir : mainDir.listFiles()) {
				if (subDir.isDirectory()) {
					cnt++;
					for (File f : subDir.listFiles()) {
						Trip t = new Trip(subDir.getName(), f);
						tripToGridAnalyzer(t, subDir.getName(), f.getName());
						analyze(t);
					}
				}
				System.out.println(cnt);
				if (cnt > Config.getMaxnumberoffilestoread())
					break;
			}
		}
		printStats();

	}

	private void tripToGridAnalyzer(Trip t, String dir, String fname)
			throws FileNotFoundException {
		File f = new File(Config.getAnalysisoutputpath() + "GridTrips/" + dir
				+ "/");
		f.mkdir();
		PrintStream p = new PrintStream(f.getAbsolutePath() + "/" + fname
				+ ".txt");
		int lastGrid = Integer.MAX_VALUE;
		GPSReading lastGpsReading = null;
		int cnt = 0;
		for (GPSReading g : t.getTrajectory()) {
			int thisGrid = g.getGrid();
			if (cnt > 0) {
				if (thisGrid != lastGrid) {
					if (lastGpsReading != null) {
						p.print('\n');
					}
					p.print(thisGrid + "\t" + g.getTime());
				}
				if (g.isOccupied() && !lastGpsReading.isOccupied())
					p.print("\t1");
				else if (g.isOccupied() && lastGpsReading.isOccupied() && cnt == 1)
					p.print("\t1");
			}
			cnt++;
			lastGpsReading = g;
			lastGrid = thisGrid;
		}
	}

	private void printStats() throws FileNotFoundException {

		printActiveTaxiStats();
		printTaxiDriverAnalyzerStats();
		printGraph();
	}

	private void printGraph() throws FileNotFoundException {
		graphBuilder.print(new File(Config.getAnalysisoutputpath()
				+ "graph.txt"));
	}

	private void printTaxiDriverAnalyzerStats() throws FileNotFoundException {
		taxiDriverAnalyzer.print();
	}

	private void printActiveTaxiStats() throws FileNotFoundException {
		for (ActiveTaxiAnalyzer a : activeTaxiAnalyzers)
			a.print(Config.getAnalysisoutputpath() + "ActiveTaxis/");
	}

	private void analyze(Trip t) throws FileNotFoundException {
		analyzeActiveTaxis(t);
		analyzePickUps(t);
		analyzeTaxiDrivers(t);
		graphBuilder.analyze(t);
	}

	private void analyzeTaxiDrivers(Trip t) {
		taxiDriverAnalyzer.add(t);
	}

	private void analyzePickUps(Trip t) throws FileNotFoundException {
		for (GPSReading pickup : t.getPickUps())
			for (PickUpAnalyzer p : pickUpAnalyzers)
				p.add(pickup);
	}

	private void analyzeActiveTaxis(Trip t) {
		for (ActiveTaxiAnalyzer a : activeTaxiAnalyzers)
			a.add(t);
	}

	public TripAnalyzer() {
		makeActiveTaxiAnalyzers();
		makePickUpAnalyzers();
		taxiDriverAnalyzer = new TaxiDriverAnalyzer();
		graphBuilder = new GraphBuilder();
	}

	private void makePickUpAnalyzers() {
		pickUpAnalyzers = new PickUpAnalyzer[5];
		pickUpAnalyzers[0] = new PickUpAnalyzer(new TODOWTimeMapper(timeChunk),
				"TODOWPickUpAnalyzer");
		pickUpAnalyzers[1] = new PickUpAnalyzer(new TODTimeMapper(timeChunk),
				"TODPickUpAnalyzer");
		pickUpAnalyzers[2] = new PickUpAnalyzer(new TODOYTimeMapper(timeChunk),
				"TODOYPickUpAnalyzer");
		pickUpAnalyzers[3] = new PickUpAnalyzer(new TODOYTimeMapper(24),
				"DOYPickUpAnalyzer");
		pickUpAnalyzers[4] = new PickUpAnalyzer(new TODOWTimeMapper(24),
				"DOWPickUpAnalyzer");
	}

	final int timeChunk = 2;

	private void makeActiveTaxiAnalyzers() {
		activeTaxiAnalyzers = new ActiveTaxiAnalyzer[5];

		activeTaxiAnalyzers[0] = new ActiveTaxiAnalyzer(new TODOWTimeMapper(
				timeChunk), "TODOWActiveTaxiAnalyzer");
		activeTaxiAnalyzers[1] = new ActiveTaxiAnalyzer(new TODTimeMapper(
				timeChunk), "TODActiveTaxiAnalyzer");
		activeTaxiAnalyzers[2] = new ActiveTaxiAnalyzer(new TODOYTimeMapper(
				timeChunk), "TODOYActiveTaxiAnalyzer");
		activeTaxiAnalyzers[3] = new ActiveTaxiAnalyzer(
				new TODOYTimeMapper(24), "DOYActiveTaxiAnalyzer");
		activeTaxiAnalyzers[4] = new ActiveTaxiAnalyzer(
				new TODOWTimeMapper(24), "DOWActiveTaxiAnalyzer");
	}

}
