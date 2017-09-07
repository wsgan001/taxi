package com.geo.taxi.preprocess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;

import com.geo.taxi.config.Config;

public class GraphBuilder {
	int[][] time;
	int[][] count;

	public GraphBuilder() {
		int number = Config.getNumoflatbins() * Config.getNumoflonbins();
		time = new int[number][number];
		count = new int[number][number];
	}

	public void analyze(Trip t) {
		for (int i = 1; i < t.getTrajectory().size(); i++) {
			int g1 = t.getTrajectory().get(i - 1).getGrid();
			int g2 = t.getTrajectory().get(i).getGrid();

			if (isAdjacent(t.getTrajectory().get(i - 1),
					t.getTrajectory().get(i))
					&& g1 >= 0 && g2 >= 0) {
				if (g1 == 44 * Config.getNumoflonbins())
					System.out.println(g1 + " " + g2);
				time[g1][g2] += t.getTrajectory().get(i).getTime()
						- t.getTrajectory().get(i - 1).getTime();
				count[g1][g2]++;
			}

		}
	}

	public void print(File file) throws FileNotFoundException {
		PrintStream p = new PrintStream(file);
		for (int i = 0; i < time.length; i++) {
			for (int j = 0; j < time[i].length; j++) {
				if (count[i][j] > 0)
					p.println(i + "\t" + j + "\t"
							+ (((double) time[i][j] / count[i][j])));
			}
		}
	}

	private boolean isAdjacent(GPSReading lastGpsReading,
			GPSReading thisGpsReading) {
		if ( lastGpsReading.getLatBin() >= Config.getNumoflatbins() || lastGpsReading.getLatBin() < 0)
			return false;
		if ( thisGpsReading.getLatBin() >= Config.getNumoflatbins() || thisGpsReading.getLatBin() < 0)
			return false;
		if ( lastGpsReading.getLonBin() >= Config.getNumoflonbins() || lastGpsReading.getLonBin() < 0)
			return false;
		if ( thisGpsReading.getLonBin() >= Config.getNumoflonbins() || thisGpsReading.getLonBin() < 0)
			return false;
		if (Math.abs(Config.getLatBin(lastGpsReading.getLat())
				- Config.getLatBin(thisGpsReading.getLat())) <= 1)
			if (Math.abs(Config.getLonBin(lastGpsReading.getLon())
					- Config.getLonBin(thisGpsReading.getLon())) <= 1) {
				return true;
			}
		return false;
	}

}
