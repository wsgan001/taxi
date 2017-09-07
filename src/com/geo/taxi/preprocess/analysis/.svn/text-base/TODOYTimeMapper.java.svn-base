package com.geo.taxi.preprocess.analysis;

import java.util.Date;


public class TODOYTimeMapper extends TimeMapper {

	public TODOYTimeMapper(int t) {
		super(t);
	}

	@Override
	public int map(Date d) {
		return ((d.getYear()*12 + d.getMonth())*31+d.getDate()-1)*24+d.getHours()/this.timeChunk;
	}

	@Override
	public String getString(int key) {
		int hour = (key%24) * timeChunk;
		key = key / 24;
		int day = (key % 31) + 1;
		key = key / 31;
		int month = key % 12 + 1;
		int year = key / 12;
		return year + "\t"+month+"\t"+day+"\t"+hour;
	}

	@Override
	public String getName(int key) {
		int hour = (key%24) * timeChunk;
		key = key / 24;
		int day = (key % 31) + 1;
		key = key / 31;
		int month = key % 12 + 1;
		int year = key / 12;
		return year + "-"+month+"-"+day+"-"+hour;	
	}
	
}
