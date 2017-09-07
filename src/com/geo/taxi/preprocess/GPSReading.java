package com.geo.taxi.preprocess;
import java.util.Date;
import java.util.Scanner;
import java.util.StringTokenizer;

import com.geo.taxi.config.Config;

public class GPSReading {
	double lat, lon;
	long time;
	byte status;

	public GPSReading(double lat, double lon, byte status, long time) {
		this.lat = lat;
		this.lon = lon;
		this.status = status;
		this.time = time;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	public long getTime() {
		return time;
	}

	public byte getStatus() {
		return status;
	}

	public static GPSReading parseLine(String nextLine) {
		Scanner sc = new Scanner(nextLine);
		double lat = sc.nextDouble();
		double lon = sc.nextDouble();
		byte status = (byte) (sc.next().charAt(0) == '1' ? 1 : 0);
		long time = sc.nextLong();
		return new GPSReading(lat, lon, status, time);
	}

	public static double getDistance(GPSReading lastLine, GPSReading thisLine) {
		double conv = Math.PI / 180;
		double phiS = thisLine.lat * conv, lambdaS = thisLine.lon * conv;
		double phiF = lastLine.lat * conv, lambdaF = lastLine.lon * conv;
		double t1 = Math.sin((phiF - phiS) / 2);
		t1 = t1 * t1;
		double t2 = Math.sin((lambdaF - lambdaS) / 2);
		t2 = t2 * t2;
		double d = t1 + Math.cos(phiS) * Math.cos(phiF) * t2;
		d = Math.sqrt(d);
		d = 2 * Math.asin(d);
		return 6372800 * d;
	}
	
	public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
		double conv = Math.PI / 180;
		double phiS = lat1 * conv, lambdaS = lon1 * conv;
		double phiF = lat2 * conv, lambdaF = lon2 * conv;
		double t1 = Math.sin((phiF - phiS) / 2);
		t1 = t1 * t1;
		double t2 = Math.sin((lambdaF - lambdaS) / 2);
		t2 = t2 * t2;
		double d = t1 + Math.cos(phiS) * Math.cos(phiF) * t2;
		d = Math.sqrt(d);
		d = 2 * Math.asin(d);
		return 6372800 * d;
	}

	@Override
	public String toString() {
		return lat + "\t" + lon + "\t" + status + "\t" + time;
	}

	public Date getDate() {
		return new Date(this.getTime() * 1000);
	}
	
	public boolean isOccupied(){
		return status  == 1;
	}

	public int getGrid() {
		return Config.getLatBin(this.getLat()) * Config.getNumoflonbins() + Config.getLonBin(this.getLon());
	}

	public static int decodeLat(int id) {
		return id/Config.getNumoflonbins();
	}

	public static int decodeLon(int id) {
		return id%Config.getNumoflonbins();
	}

	public int getLonBin() {
		return Config.getLonBin(lon);
	}

	public int getLatBin() {
		return Config.getLatBin(lat);
	}
}
