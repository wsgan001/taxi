package com.geo.taxi.preprocess.analysis;

import java.util.Date;


public class TODTimeMapper extends TimeMapper {

	public TODTimeMapper(int t) {
		super(t);
	}

	@Override
	public int map(Date d) {
		return d.getHours() / timeChunk;
	}

	@Override
	public String getString(int key) {
		return key * timeChunk + "";
	}

	@Override
	public String getName(int key) {
		return key * timeChunk + "";
	}

}
