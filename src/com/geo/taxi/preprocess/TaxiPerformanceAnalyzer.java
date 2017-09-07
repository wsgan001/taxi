package com.geo.taxi.preprocess;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;

import com.geo.taxi.config.Config;

public class TaxiPerformanceAnalyzer {
	public TaxiPerformanceAnalyzer(String inputpath)
			throws FileNotFoundException {
		File dir = new File(inputpath);
		HashMap<Integer, int[][]> inGrid = new HashMap<Integer, int[][]>();
		HashMap<Integer, int[][]> huntInGrid = new HashMap<Integer, int[][]>();
		PrintStream p = new PrintStream(Config.getAnalysisoutputpath()
				+ "Performance.txt");
		p.println("Name\tTimePerformance\tDistancePerformance\tNumOfHunts\tliveTime\tTotalTime\tLiveDistance\tTotalDistance\tNumOfTrips");
		int cnt = 0;
		if (dir.isDirectory()) {
			File[] files = dir.listFiles(Config.getFilefilter());
			for (File f : files) {
				new IndividualTaxiPerformanceAnalyzer(f, p, inGrid, huntInGrid);
				System.out.println(++cnt + " out of " + files.length);
				if (cnt == 1000)
					break;
			}
		}
		p.close();
		for (int i : inGrid.keySet()) {
			print(Config.getAnalysisoutputpath() + "\\Prob\\", i,
					inGrid.get(i), huntInGrid.get(i));
		}
	}

	private void print(String string, int time, int[][] count, int[][] huntCount)
			throws FileNotFoundException {
		int t = time % 24;
		int d = time / 24;
		PrintStream p = new PrintStream(new File(string + d + "-" + t + ".txt"));
		p.println(count.length+"\t"+count[0].length);
		for (int i = 0; i < count.length; i++) {
			for (int j = 0; j < count[i].length-1; j++)
				p.print(probability(count, huntCount, i, j) + "\t");
			p.println(probability(count, huntCount,i,count[i].length-1 ));
		}
	}

	private double probability(int[][] count, int[][] huntCount, int i, int j) {
		if (count[i][j] <= 0)
			return 0;
		return huntCount[i][j] / (double) count[i][j];
	}

	public static void main(String[] args) throws FileNotFoundException {
		new TaxiPerformanceAnalyzer(Config.getInputpath());
	}
}
