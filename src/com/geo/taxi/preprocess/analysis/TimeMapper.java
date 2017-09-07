package com.geo.taxi.preprocess.analysis;

import java.util.Date;

public abstract class TimeMapper {
	protected int timeChunk;

	public TimeMapper(int t) {
		timeChunk = t;
	}

	public abstract int map(Date d);

	public abstract String getString(int key);

	public abstract String getName(int key);
}
