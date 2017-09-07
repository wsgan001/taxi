package com.geo.taxi.test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

import com.geo.taxi.config.Config;

public class TestGraphProbability {
	public static void main(String[] args) throws FileNotFoundException {
		File dir = new File("cabspottingdata/processedData/probGrid");
		if ( dir.isDirectory() ) {
			File[] files = dir.listFiles( Config.getFilefilter() );
			for (File f : files )
				processFile(f);
		}
	}

	private static void processFile(File f) throws FileNotFoundException {
		double[][] graph= new double[Config.getNumoflonbins()][Config.getNumoflatbins()];
		readFile(f, graph);
		System.out.println(f.getName());
		RenderedImage i = myImageCreator(graph);
		try {
			String name = f.getName().substring(0,f.getName().indexOf('.'));
			ImageIO.write(i, "png", new File(Config.getAnalysisoutputpath()+"/ProbGraph/"+name+".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static RenderedImage myImageCreator(double[][] graph) {
		final int size = 100;
		final int correction = 10;
		BufferedImage bi = new BufferedImage(Config.getNumoflonbins() * size,
				Config.getNumoflatbins() * size, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		g.setBackground(Color.WHITE);
		g.clearRect(0, 0, Config.getNumoflonbins() * size, Config.getNumoflatbins() * size);
		for (int i = 0 ; i < Config.getNumoflonbins(); i++ ) {
			for (int j = 0; j < Config.getNumoflatbins() ; j++ ) {
				g.setColor( new Color((int) (255*graph[i][j]), 0, 0));
				g.fillRect(i*size, j*size, size, size);
			}
		}
		return bi;
	}

	private static void readFile(File f, double[][] graph)
			throws FileNotFoundException {
		Scanner sc = new Scanner( f );
		Scanner firstLineScanner = new Scanner( sc.nextLine().trim() );
		assert ( firstLineScanner.nextInt() == Config.getNumoflatbins() && firstLineScanner.nextInt() == Config.getNumoflonbins() );
		int j = 1;
		while(sc.hasNext()){
			Scanner line = new Scanner(sc.nextLine().trim());
			for(int i = 0 ; i < Config.getNumoflonbins() ; i++ ){
				graph[i][Config.getNumoflatbins()-j] = line.nextDouble();
				if (graph[i][Config.getNumoflatbins()-j] > 1 ){
					System.out.println( f.getName() );
					System.out.println(i + " " + j);
					return;
				}
			}
			j++;
		}
	}
}
