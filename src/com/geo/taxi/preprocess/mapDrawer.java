package com.geo.taxi.preprocess;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.imageio.ImageIO;


public class mapDrawer {
	public void drawMap(ArrayList<Double> lats, ArrayList<Double> lons, String file, String caption, double minX, double maxX, double minY, double maxY, double scale) {
		RenderedImage i = myImageCreator(caption, lats, lons, minX, maxX, minY, maxY , scale);
		try {
			ImageIO.write(i, "png", new File(file));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private RenderedImage myImageCreator(String caption, ArrayList<Double> lats,
			ArrayList<Double> lons,  double minX, double maxX, double minY, double maxY, double scale) {
//		double minY = 37.6;
//		double minX = -122.55;
//		double maxY = 37.85;
//		double maxX = -122.3;
//		double scale = 300;
		int width = (int) ((maxX - minX) * scale);
		int height = (int) ((maxY - minY) * scale);
		BufferedImage bufferedImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bufferedImage.createGraphics();
		g2d.setBackground(Color.WHITE);
		g2d.clearRect(0, 0, width, height);
		g2d.setColor(Color.BLUE );
		for (int i = 0; i < lats.size(); i++) {
			int x = (int) ((lons.get(i) - minX) * scale);
			int y = height - (int) ((lats.get(i) - minY) * scale);
			g2d.drawRect(x, y, 1, 1);
		}
		g2d.setFont( new Font("Arial", 0, (int) (scale/200)));
		g2d.drawString(caption, 0, height);
		return bufferedImage;
	}
}
