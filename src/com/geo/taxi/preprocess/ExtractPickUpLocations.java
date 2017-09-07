package com.geo.taxi.preprocess;
import java.io.FileNotFoundException;

import com.geo.taxi.config.Config;


public class ExtractPickUpLocations {
	public static void main(String[] args) throws FileNotFoundException {
		RawInputProcessor rip = new RawInputProcessor(Config.getInputpath(), Config.getExtension());
		rip.processFiles( (byte)0 , (byte)1 );
	}
}
