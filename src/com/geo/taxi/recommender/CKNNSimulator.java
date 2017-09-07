package com.geo.taxi.recommender;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

import javax.swing.text.Highlighter.HighlightPainter;

import com.geo.taxi.config.Config;

public class CKNNSimulator {

	private HashMap<Integer, Grid> graph;
	private HashMap<Long, ArrayList<Task>> tasks;
	ArrayList<CKNNVehicle> vehicles;
	long startTime, endTime;
	CKNNRecommender recommender;
	int numberOfVehicles;
	
	public CKNNSimulator() {
		// TODO Auto-generated constructor stub
		graph = new HashMap<Integer, Grid>();
		tasks = new HashMap<Long, ArrayList<Task>>();
	}
	
	private void loadPassengers() throws FileNotFoundException {
		Date d = new Date(startTime * 1000);
		int dateInt = map(d);
		File passengerFile = new File(Config.getAnalysisoutputpath()
				+ "//Passengers//" + dateInt + ".txt");
		Scanner sc = new Scanner(passengerFile);
		while (sc.hasNext()) {
			Scanner line = new Scanner(sc.nextLine().trim());
			try {
				int location = line.nextInt();
				int destination = line.nextInt();
				long time = line.nextInt() - 300;
				if (graph.get(location) != null
						&& graph.get(destination) != null) {
					Passenger p = new Passenger(graph.get(location),
							graph.get(destination), 600);
					if (tasks.get(time) == null)
						tasks.put(time, new ArrayList<Task>());
					tasks.get(time).add(new PassengerAppearance(p));
				}
			} catch (InputMismatchException e) {
				continue;
			}

		}
	}

	public CKNNSimulator(CKNNRecommender recommender, long startTime, long endTime,
			int numberOfVehicles) throws FileNotFoundException {
		this();
		this.recommender = recommender;
		System.setOut(new PrintStream(new File("log/simulator"
				+ recommender.getClass().getSimpleName() +"-"+numberOfVehicles+ ".log")));
		this.startTime = startTime;
		this.endTime = endTime;
		this.numberOfVehicles = numberOfVehicles;
		loadGraph();
		loadVehicles();
		loadPassengers();
	}

	private void loadVehicles() throws FileNotFoundException {
		vehicles = new ArrayList<CKNNVehicle>();
		loadTopVehicles(Math.min(numberOfVehicles / 10, 20));
		loadBottomVehicles(Math.min(numberOfVehicles / 10, 20));
		loadModerateDrivers(numberOfVehicles - vehicles.size());
		System.err.println(vehicles.size());
		Date d = new Date(startTime * 1000);
		int dateInt = map(d);
		for (CKNNVehicle v : vehicles) {
			loadStart(v, dateInt);
			loadTurnOffs(v, dateInt);
		}
	}

	private void loadTurnOffs(CKNNVehicle v, int dateInt) {
		Scanner sc;
		try {
			sc = new Scanner(new File(Config.getAnalysisoutputpath()
					+ "//TripsWithTurnOffs//" + v.getId() + "-" + dateInt
					+ ".txt"));
			while (sc.hasNext()) {
				Scanner linecanner = new Scanner(sc.nextLine().trim());
				linecanner.nextInt();
				long time = linecanner.nextLong();
				long offTime = linecanner.nextLong();
				int grid = linecanner.nextInt();
				if (tasks.get(time) == null)
					tasks.put(time, new ArrayList<Task>());
				if (graph.get(grid) != null)
					tasks.get(time)
							.add(new VehicleTurnOffTask(v, offTime, graph
									.get(grid)));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void loadStart(CKNNVehicle v, int dateInt) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(Config.getAnalysisoutputpath()
				+ "//TripsWithTurnOffs//" + v.getId()));
		long time = -1;
		int grid = -1;
		while (sc.hasNext()) {
			Scanner linecanner = new Scanner(sc.nextLine().trim());
			if (linecanner.nextInt() == dateInt) {
				grid = linecanner.nextInt();
				time = linecanner.nextLong();
			}
		}
		if (time > -1 && time > startTime && graph.get(grid) != null) {
			if (tasks.get(time) == null) {
				tasks.put(time, new ArrayList<Task>());
			}
			tasks.get(time).add(new CKNNVehicleTurnOnTask(v, graph.get(grid)));
			System.err.println(v.getId() + " will turn on at " + time);
		}
	}

	private void loadModerateDrivers(int howMany) throws FileNotFoundException {
		File f = new File(Config.getAnalysisoutputpath()
				+ "ModerateDrivers.txt");
		loadVehiclesFromFile(f, howMany);
	}

	private void loadBottomVehicles(int howMany) throws FileNotFoundException {
		File f = new File(Config.getAnalysisoutputpath() + "BottomDrivers.txt");
		loadVehiclesFromFile(f, howMany);
	}

	private void loadTopVehicles(int howMany) throws FileNotFoundException {
		File f = new File(Config.getAnalysisoutputpath() + "TopDrivers.txt");
		loadVehiclesFromFile(f, howMany);
	}

	private void loadVehiclesFromFile(File f, int howMany)
			throws FileNotFoundException {
		System.err.println( howMany );
		Scanner sc = new Scanner(f);
		for (int i = 0; i < howMany; i++) {
			vehicles.add(new CKNNVehicle(sc.nextLine().trim(), null));
		}
		sc.close();
	}

	private void loadGraph() throws FileNotFoundException {
		Scanner sc = new Scanner(new File(Config.getGraphpath()));
		while (sc.hasNext()) {
			String line = sc.nextLine();
			Scanner lineScanner = new Scanner(line);
			int from = lineScanner.nextInt();
			int to = lineScanner.nextInt();
			double time = lineScanner.nextDouble();
			if (graph.get(from) == null)
				graph.put(from, new Grid(from));
			if (graph.get(to) == null)
				graph.put(to, new Grid(to));
			graph.get(from).addNeighbor(graph.get(to), time);
		}
		final int count = Config.getNumoflatbins() * Config.getNumoflonbins();
		for (int i = 0; i < count; i++) {
			Grid from = graph.get(i);
			if (from == null)
				continue;
			for (int j = 0; j < count; j++) {
				if (i == j)
					continue;
				Grid to = graph.get(j);
				if (to == null)
					continue;
				if (from.getNeighbors().contains(to)
						&& !to.getNeighbors().contains(from)) {
					to.addNeighbor(from, from.getTime(to));
				}
			}
		}
	}

	public void start() throws FileNotFoundException {
		for (long t = startTime; t < endTime; t++) {
			ArrayList<CKNNVehicle> vehiclesNeedRecommendation = new ArrayList<CKNNVehicle>();
			for (CKNNVehicle v : vehicles)
				if (v.isOn()
						&& (v.isArrivedHotspot() || v.getRoute() == null || v.getRoute().isEmpty())) {
					if (v.isArrivedHotspot()) {
						v.setArrivedHotspot(false);
					}
					vehiclesNeedRecommendation.add(v);
				}

			if (!vehiclesNeedRecommendation.isEmpty()) {
				loadProbabilities(t);
				System.err.println(vehiclesNeedRecommendation);
				System.err.println("recommending");
				recommender.Recommend(vehiclesNeedRecommendation, graph);
			}
			if (tasks.get(t) != null && tasks.get(t).size() > 0) {
				System.err.println(new Date(t * 1000));
				for (Task task : tasks.get(t))
					task.execute(tasks, t, graph);
			}
		}
		System.err.println("Now Printing! :)");
		for (CKNNVehicle v : vehicles) {
			System.out.print(v.getId() + "\t");
			System.out.print(v.getDistanceTravelled() + "\t"
					+ v.getLiveDistanceTravelled() + "\t");
			System.out.print(v.getDistancePerformance() + "\t");
			System.out.print(v.getTimeTravelled() + "\t"
					+ v.getLiveTimeTravelled() + "\t");
			System.out.print(v.getTimePerformance() + "\t");
			System.out.println(v.getNumOfHunts() + "\t");
		}
	}

	private void loadProbabilities(long t) throws FileNotFoundException {
		Date d = new Date(t * 1000);
		int time = d.getHours() - d.getHours() % 2;
		Scanner sc = new Scanner(new File(
				"cabspottingdata/processedData/probGrid/" + time
						+ ".txt"));
		sc.nextLine();
		for (int i = 0; i < Config.getNumoflatbins(); i++) {
			Scanner lineScanner = new Scanner(sc.nextLine());
			for (int j = 0; j < Config.getNumoflonbins(); j++) {
				int id = i * Config.getNumoflonbins() + j;
				double probability = lineScanner.nextDouble();
				if (graph.get(id) != null) {
					graph.get(id).setProbability(probability);
					graph.get(id).setMaxNumberOfTaxis(numberOfVehicles/6);
				}
			}
		}
	}

	private static int map(Date d) {
		return (d.getYear() * 12 + d.getMonth()) * 31 + d.getDate() - 1;
	}

}
