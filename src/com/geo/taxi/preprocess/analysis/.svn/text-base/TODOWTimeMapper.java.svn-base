package com.geo.taxi.preprocess.analysis;

import java.util.Date;

public class TODOWTimeMapper extends TimeMapper {

	public TODOWTimeMapper(int t) {
		super(t);
	}

	@Override
	public int map(Date d) {
		return d.getDay() * 24 + d.getHours() / timeChunk;
	}

	@Override
	public String getString(int key) {
		return (key / 24) + "\t" + ((key % 24)*timeChunk);
	}

	@Override
	public String getName(int key) {
		return (key / 24) + "-" + ((key % 24)*timeChunk);
	}
	
	
}
