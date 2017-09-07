package com.geo.taxi.preprocess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;

import com.geo.taxi.config.Config;

public class ProbabilityCalculator {
	public static void main(String[] args) throws FileNotFoundException {
		int[] year = new int[] { 108, 108, 108 };
		int[] month = new int[] { 5, 5, 5 };
		int[] day = new int[] { 17, 24, 31 };
		for (int time = 0; time < 24; time += 2) {
			double[][][] probs = new double[year.length][Config
					.getNumoflatbins()][Config.getNumoflonbins()];
			for (int i = 0; i < year.length; i++) {
				File f = new File(Config.getAnalysisoutputpath()
						+ "probGrid/TODOYActiveTaxiAnalyzer-" + year[i] + "-"
						+ month[i] + "-" + day[i] + "-" + time + ".txt");
				probabilityOfTime(f, probs[i]);
			}
			calculateProb(probs, time);
		}
	}

	private static void calculateProb(double[][][] probs, int time)
			throws FileNotFoundException {
		double[][] prob = new double[Config.getNumoflatbins()][Config
				.getNumoflonbins()];
		PrintStream p = new PrintStream(new File(Config.getAnalysisoutputpath()
				+ "/probGrid/" + time + ".txt"));
		p.println(prob.length + "\t" + prob[0].length);
		for (int i = 0; i < prob.length; i++) {
			for (int j = 0; j < prob[i].length; j++) {
				prob[i][j] = 1;
				for (int k = 0; k < probs.length; k++) {
					prob[i][j] *= probs[k][i][j];
				}
				prob[i][j] = Math.pow(prob[i][j], 1.0 / probs.length);
				p.print(prob[i][j]);
				if (j < prob[i].length - 1)
					p.print("\t");
			}
			p.println();
		}
	}

	private static void probabilityOfTime(File f, double[][] ds) {
		try {
			Scanner sc = new Scanner(f);
			sc.nextLine();
			for (int i = 0; i < ds.length; i++) {
				Scanner lineScanner = new Scanner(sc.nextLine());
				for (int j = 0; j < ds[i].length; j++)
					ds[i][j] = lineScanner.nextDouble();
			}

		} catch (FileNotFoundException e) {
			for (int i = 0; i < ds.length; i++)
				for (int j = 0; j < ds[i].length; j++)
					ds[i][j] = 1;
		}
	}
}
