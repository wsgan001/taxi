package com.geo.taxi.preprocess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import com.geo.taxi.config.Config;

public class GraphMaker {
	private File inputDir;
	HashMap<Integer, HashSet<Integer>> graph;

	public GraphMaker(String inputpath) {
		inputDir = new File(inputpath);
		graph = new HashMap<Integer, HashSet<Integer>>();
	}

	public static void main(String[] args) throws FileNotFoundException {
		new GraphMaker(Config.getInputpath()).makeGraph();
	}

	private void makeGraph() throws FileNotFoundException {
		int cnt = 0;
		if (inputDir.isDirectory()) {
			for (File f : inputDir.listFiles(Config.getFilefilter())) {
				processFile(f, graph);
				System.out.println(++cnt);
			}
		}
		PrintStream p = new PrintStream(new File(Config.getAnalysisoutputpath()
				+ "\\graph"));
		print(graph, p);
	}

	private void print(HashMap<Integer, HashSet<Integer>> graph, PrintStream p) {
		for (int i : graph.keySet()) {
			p.print(unHash(i) + ":");
			for (int j : graph.get(i)) {
				p.print("\t" + unHash(j));
			}
			p.println();
		}
	}

	private String unHash(int i) {
		return "(" + i / 1000 + "," + i % 1000 + ")";
	}

	private void processFile(File f, HashMap<Integer, HashSet<Integer>> graph)
			throws FileNotFoundException {
		Scanner sc = new Scanner(f);
		GPSReading lastGpsReading = null;
		while (sc.hasNext()) {

			GPSReading thisGpsReading = GPSReading.parseLine(sc.nextLine());
			if (lastGpsReading == null
					|| Math.abs(lastGpsReading.getTime()
							- thisGpsReading.getTime()) > 600) {
				lastGpsReading = thisGpsReading;
			} else {
				if (thisGpsReading.getLat() <= Config.getMaxlat()
						&& thisGpsReading.getLat() >= Config.getMinlat()
						&& thisGpsReading.getLon() <= Config.getMaxlon()
						&& thisGpsReading.getLon() >= Config.getMinlon()
						&& lastGpsReading.getLat() <= Config.getMaxlat()
						&& lastGpsReading.getLat() >= Config.getMinlat()
						&& lastGpsReading.getLon() <= Config.getMaxlon()
						&& lastGpsReading.getLon() >= Config.getMinlon()) {
					int i = hash(lastGpsReading);
					int j = hash(thisGpsReading);
					if (isAdjacent(lastGpsReading, thisGpsReading)) {
						if (graph.get(j) == null)
							graph.put(j, new HashSet<Integer>());
						graph.get(j).add(i);
					}
				}
				lastGpsReading = thisGpsReading;
			}

		}
	}

	private boolean isAdjacent(GPSReading lastGpsReading,
			GPSReading thisGpsReading) {
		if (hash(lastGpsReading) == hash(thisGpsReading))
			return false;
		if (Math.abs(Config.getLatBin(lastGpsReading.getLat())
				- Config.getLatBin(thisGpsReading.getLat())) <= 1)
			if (Math.abs(Config.getLonBin(lastGpsReading.getLon())
					- Config.getLonBin(thisGpsReading.getLon())) <= 1)
				return true;
		return false;
	}

	private int hash(GPSReading lastGpsReading) {
		if (Config.getLatBin(lastGpsReading.getLat()) >= 0
				&& Config.getLatBin(lastGpsReading.getLat()) < Config
						.getNumoflatbins()
				&& Config.getLonBin(lastGpsReading.getLon()) >= 0
				&& Config.getLonBin(lastGpsReading.getLon()) < Config
						.getNumoflonbins())
			return Config.getLatBin(lastGpsReading.getLat()) * 1000
					+ Config.getLonBin(lastGpsReading.getLon());
		else
			return Integer.MAX_VALUE;
	}
}
