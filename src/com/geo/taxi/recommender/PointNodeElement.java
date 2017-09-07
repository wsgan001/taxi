package com.geo.taxi.recommender;

import java.awt.Point;

import com.geo.taxi.recommender.AbstractNodeElement;

@SuppressWarnings("serial")
public class PointNodeElement<T> extends AbstractNodeElement<T> {

	public PointNodeElement(Point coordinates, T element) {
		super(coordinates, element);
	}

}
