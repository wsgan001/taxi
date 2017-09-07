package com.geo.taxi.recommender;

public class CKNNVehicle extends Vehicle {

	private static final int K = 5;
	private int currentK;
	// used to accumulate time, 
	// when it is larger than the time between two grids, 
	// movement happens by one step (form one grid to another
	private double moveStepTime;
	
	private boolean isNewCycle;
	
	private boolean isArrivedHotspot;
	
	public CKNNVehicle(String id, Grid currentGrid) {
		super(id, currentGrid);
		// TODO Auto-generated constructor stub
		currentK = K;
		moveStepTime = 0.;
		isNewCycle = true;
		isArrivedHotspot = false;
	}
	
	public void setCurrentK(int k) {
		currentK = k;
	}
	
	public int getCurrentK() {
		return currentK;
	}
	
	public void setMoveStepTime(double time) {
		moveStepTime = time;
	}

	public double getMoveStepTime() {
		return moveStepTime;
	}
	
	public void setIsNewCycle(boolean b) {
		isNewCycle = b;
	}
	
	public boolean getIsNewCycle() {
		return isNewCycle;
	}
	
	public static int getK() {
		return K;
	}
	
	public void setArrivedHotspot(boolean b) {
		isArrivedHotspot = b;
	}
	
	public boolean isArrivedHotspot() {
		return isArrivedHotspot;
	}
}
