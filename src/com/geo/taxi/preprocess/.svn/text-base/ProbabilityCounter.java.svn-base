package com.geo.taxi.preprocess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;

import com.geo.taxi.config.Config;
import com.geo.taxi.preprocess.analysis.TimeMapper;

public class ProbabilityCounter {
	String name;
	HashMap<Integer, int[][]> enteraceCounter;
	HashMap<Integer, int[][]> pickUpCounter;
	TimeMapper mapper;

	public ProbabilityCounter(TimeMapper timeMapper, String string) {
		this.mapper = timeMapper;
		enteraceCounter = new HashMap<Integer, int[][]>();
		pickUpCounter = new HashMap<Integer, int[][]>();
		name = string;
	}

	public void add(int id, long time, boolean pickUp) {
		Date d = new Date(time * 1000);
		int latBin = GPSReading.decodeLat(id);
		int lonBin = GPSReading.decodeLon(id);
		if (latBin < 0 || latBin >= Config.getNumoflatbins())
			return;
		if (lonBin < 0 || lonBin >= Config.getNumoflonbins())
			return;
		int mappedTime = mapper.map(d);
		if (enteraceCounter.get(mappedTime) == null)
			enteraceCounter
					.put(mappedTime, new int[Config.getNumoflatbins()][Config
							.getNumoflonbins()]);
		if (pickUpCounter.get(mappedTime) == null)
			pickUpCounter
					.put(mappedTime, new int[Config.getNumoflatbins()][Config
							.getNumoflonbins()]);
		enteraceCounter.get(mappedTime)[latBin][lonBin]++;
		if (pickUp)
			pickUpCounter.get(mappedTime)[latBin][lonBin]++;
	}

	public void print() throws FileNotFoundException {
		for (int key : enteraceCounter.keySet()) {
			PrintStream enterance = new PrintStream(new File(
					Config.getAnalysisoutputpath() + "enteranceGrid/" + name
							+ "-" + mapper.getName(key)+".txt"));
			PrintStream pickup = new PrintStream(new File(
					Config.getAnalysisoutputpath() + "pickUpGrid/" + name + "-"
							+ mapper.getName(key)+".txt"));
			PrintStream prob = new PrintStream(new File(
					Config.getAnalysisoutputpath() + "probGrid/" + name + "-"
							+ mapper.getName(key)+".txt"));
			print(enteraceCounter.get(key), enterance);
			print(pickUpCounter.get(key), pickup);
			print(probability(enteraceCounter.get(key), pickUpCounter.get(key)),
					prob);
		}
	}

	private void print(double[][] array, PrintStream p) {
		p.println(Config.getNumoflatbins() + " " + Config.getNumoflonbins());
		for (int i = 0; i < Config.getNumoflatbins(); i++) {
			for (int j = 0; j < Config.getNumoflonbins(); j++)
				p.print(array[i][j] + "\t");
			p.println();
		}
	}

	private double[][] probability(int[][] enterance, int[][] pickUp) {
		double[][] res = new double[Config.getNumoflatbins()][Config
				.getNumoflonbins()];
		for (int i = 0; i < res.length; i++)
			for (int j = 0; j < res[i].length; j++)
				if (enterance[i][j] != 0)
					res[i][j] = pickUp[i][j] / ((double)enterance[i][j]);
				else
					res[i][j] = 0;
		return res;
	}

	private void print(int[][] array, PrintStream p) {
		p.println(Config.getNumoflatbins() + " " + Config.getNumoflonbins());
		for (int i = 0; i < Config.getNumoflatbins(); i++) {
			for (int j = 0; j < Config.getNumoflonbins(); j++)
				p.print(array[i][j] + "\t");
			p.println();
		}
	}
}
