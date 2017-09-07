package com.geo.taxi.preprocess;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import com.geo.taxi.config.Config;
import com.geo.taxi.preprocess.analysis.TODOWTimeMapper;
import com.geo.taxi.preprocess.analysis.TODOYTimeMapper;
import com.geo.taxi.preprocess.analysis.TODTimeMapper;

public class GridTripAnalyzer {

	ProbabilityCounter[] probabilityCounters;
	public GridTripAnalyzer(File mainDir) throws FileNotFoundException {
		this();
		int cnt = 0;
		if (mainDir.isDirectory()) {
			System.out.println("salam");
			for (File subDir : mainDir.listFiles()) {
				if (subDir.isDirectory()) {
					cnt++;
					for (File f : subDir.listFiles()) {
						processFile(f);
					}
				}
				System.out.println(cnt);
				if (cnt > Config.getMaxnumberoffilestoread())
					break;
			}
		}
		printStats();
	}

	private void printStats() throws FileNotFoundException {
		for (ProbabilityCounter p : probabilityCounters)
			p.print();
	}

	private void processFile(File f) throws FileNotFoundException {
		Scanner sc = new Scanner(f);
		while (sc.hasNext()) {
			String currentLine = sc.nextLine();
			if (currentLine.length() > 0) {
				Scanner lineScanner = new Scanner(currentLine);
				int id = lineScanner.nextInt();
				long time = lineScanner.nextLong();
				int success;
				try {
					success = lineScanner.nextInt();
				} catch (NoSuchElementException e) {
					success = 0;
				}
				for (ProbabilityCounter e : probabilityCounters)
					e.add(id, time, success > 0);
			}
		}
	}

	public GridTripAnalyzer() {
		probabilityCounters = new ProbabilityCounter[5];
		int timeChunk = 2;
		probabilityCounters[0] = new ProbabilityCounter(new TODOWTimeMapper(
				timeChunk), "TODOWActiveTaxiAnalyzer");
		probabilityCounters[1] = new ProbabilityCounter(new TODTimeMapper(
				timeChunk), "TODActiveTaxiAnalyzer");
		probabilityCounters[2] = new ProbabilityCounter(new TODOYTimeMapper(
				timeChunk), "TODOYActiveTaxiAnalyzer");
		probabilityCounters[3] = new ProbabilityCounter(new TODOWTimeMapper(
				24), "DOWActiveTaxiAnalyzer");
		probabilityCounters[4] = new ProbabilityCounter(new TODOYTimeMapper(
				24), "DOYActiveTaxiAnalyzer");
	}

}
