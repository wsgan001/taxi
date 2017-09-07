package com.geo.taxi.preprocess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;

import com.geo.taxi.config.Config;

public class ProbabilityCalculator2 {
	public ProbabilityCalculator2(File dir) throws FileNotFoundException {
		if (dir.isDirectory()){
			for( File f : dir.listFiles( Config.getFilefilter() )){
				int [][] array = processFile(f);
				double[][] probability = calculateProbability( array );
				print(probability, new PrintStream(new File(Config.getAnalysisoutputpath()+"/probGrids2/"+f.getName())));
			}
		}
	}

	private double[][] calculateProbability(int[][] array) {
		double max = getMax( array );
		double[][] probs = new double[array.length][];
		for (int i = 0 ; i < probs.length; i++ ){
			probs[i] = new double[array[i].length];
			for (int j = 0 ; j < probs[i].length; j++ )
				probs[i][j] = array[i][j]/max;
		}
		return probs;
	}

	private double getMax(int[][] array) {
		double max = 0;
		for (int i = 0 ; i < array.length ;i++ )
			for (int j = 0 ; j < array[i].length;j++)
				max = array[i][j] > max ? array[i][j]: max;
		return max;
	}

	private int[][] processFile(File f) throws FileNotFoundException {
		System.out.println( f.getAbsolutePath() );
		Scanner sc = new Scanner( f );
		String line = sc.nextLine().trim();
		System.out.println( line );
		Scanner lineScanner = new Scanner( line );
		int[][] data = new int[lineScanner.nextInt()][lineScanner.nextInt()];
		for (int i = 0; i < data.length; i++ ){
			lineScanner = new Scanner(sc.nextLine());
			for (int j = 0 ; j < data[i].length ; j++ )
				data[i][j] = lineScanner.nextInt();
		}
		return data;
	}

	public static void main(String[] args) throws FileNotFoundException{
		new ProbabilityCalculator2( new File(Config.getAnalysisoutputpath()+"/pickUpGrid/"));
	}
	
	private void print(double[][] array, PrintStream p) {
		p.println(Config.getNumoflatbins() + " " + Config.getNumoflonbins());
		for (int i = 0; i < Config.getNumoflatbins(); i++) {
			for (int j = 0; j < Config.getNumoflonbins(); j++)
				p.print(array[i][j] + "\t");
			p.println();
		}
	}
}
