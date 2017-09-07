package com.geo.taxi.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import com.geo.taxi.config.Config;
import com.geo.taxi.recommender.Grid;

public class TestGraph {
	public static void main(String[] args) throws FileNotFoundException {
		HashMap<Integer, HashSet<Integer>> graph = new HashMap<Integer, HashSet<Integer>>();
		Scanner sc = new Scanner(new File(Config.getAnalysisoutputpath()
				+ "\\graph.txt"));
		while (sc.hasNext()) {
			String line = sc.nextLine();
			Scanner lineScanner = new Scanner( line );
			int id1 = lineScanner.nextInt();
			int id2 = lineScanner.nextInt();
			double time = lineScanner.nextDouble();
			if (graph.get(id1) == null)
				graph.put(id1, new HashSet<Integer>());
			graph.get(id1).add(id2);
		}
		printGraph(graph);
	}

	private static void printGraph(HashMap<Integer, HashSet<Integer>> graph) {
		RenderedImage i = myImageCreator(graph);
		try {
			ImageIO.write(i, "png", new File("img/a.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static RenderedImage myImageCreator(
			HashMap<Integer, HashSet<Integer>> graph) {
		final int size = 100;
		final int correction = 10;
		BufferedImage bi = new BufferedImage(Config.getNumoflonbins() * size,
				Config.getNumoflatbins() * size, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		g.setBackground(Color.WHITE);
		g.clearRect(0, 0, Config.getNumoflonbins() * size, Config.getNumoflatbins() * size);
		for (int i : graph.keySet()) {
			int lat1 = getLat(i);
			int lon1 = getLon(i);
			g.setColor(Color.BLACK);
			for (int j : graph.get(i)) {
				int lat2 = getLat(j);
				int lon2 = getLon(j);
				int x1 = (int) ((lon1 + .5) * size);
				int x2 = (int) ((lon2 + .5) * size);
				int y1 = Config.getNumoflatbins()*size - (int) ((lat1 + .5) * size);
				int y2 = Config.getNumoflatbins()*size - (int) ((lat2 + .5) * size);
				if (x1 < x2) {
					x1 += correction;
					x2 -= correction;
				} else if (x2 < x1) {
					x2 += correction;
					x1 -= correction;
				}
				if (y1 < y2) {
					y1 = y1 + correction;
					y2 = y2 - correction;
				} else if (y2 < y1) {
					y2 = y2 + correction;
					y1 = y1 - correction;
				}
				int arrowLength = correction/2;
				g.draw(new Line2D.Double(x1, y1, x2, y2));
				if ( x1 < x2 && y1 == y2 ){
					g.fillPolygon(new int[]{x2,x2-arrowLength,x2-arrowLength,x2}, new int[]{y2, y2-arrowLength,y2+arrowLength,y2}, 4);
				}else if ( x1 > x2 && y1 == y2 ){
					g.fillPolygon(new int[]{x2,x2+arrowLength,x2+arrowLength,x2}, new int[]{y2, y2-arrowLength,y2+arrowLength,y2}, 4);
				}else if ( x1 == x2 && y1 < y2 ){
					g.fillPolygon(new int[]{x2,x2-arrowLength,x2+arrowLength,x2}, new int[]{y2, y2-arrowLength,y2-arrowLength,y2}, 4);
				}else if ( x1 == x2 && y1 > y2 ){
					g.fillPolygon(new int[]{x2,x2-arrowLength,x2+arrowLength,x2}, new int[]{y2, y2+arrowLength,y2+arrowLength,y2}, 4);
				}else if ( x1 < x2 && y1 < y2 ){
					g.fillPolygon(new int[]{x2,x2-arrowLength,x2,x2}, new int[]{y2, y2,y2-arrowLength,y2}, 4);
				}else if ( x1 < x2 && y1 > y2 ){
					g.fillPolygon(new int[]{x2,x2-arrowLength,x2,x2}, new int[]{y2, y2,y2+arrowLength,y2}, 4);
				}else if ( x1 > x2 && y1 < y2 ){
					g.fillPolygon(new int[]{x2,x2+arrowLength,x2,x2}, new int[]{y2, y2,y2-arrowLength,y2}, 4);
				}else if ( x1 > x2 && y1 > y2 ){
					g.fillPolygon(new int[]{x2,x2+arrowLength,x2,x2}, new int[]{y2, y2,y2+arrowLength,y2}, 4);
				}
			}
		}
		g.setColor( Color.RED );
		for ( int i = 0 ; i < Config.getNumoflonbins() ; i ++){
			for ( int j = 0 ; j < Config.getNumoflatbins(); j++ ){
				g.setColor(Color.GRAY);
				//g.drawRect(i*size, j*size,size, size);
				g.setColor(Color.RED);
				g.fillOval((int)((i + .5) * size-correction), (int)(( j + .5) * size-correction), 2*correction, 2*correction);
			}
		}
		return bi;
	}

	private static int getLon(int i) {
		return i % Config.getNumoflonbins();
	}

	private static int getLat(int i) {
		return i / Config.getNumoflonbins();
	}


}
