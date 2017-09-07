package com.geo.taxi.recommender;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Vector;

import com.geo.taxi.recommender.PointNode;

import com.geo.taxi.recommender.RTree;
import com.geo.taxi.recommender.Grid;


public class QRNode<T> extends PointNode<T> {

	private RTree<T> rtree;
	
	
	public QRNode(Point startCoordinates, Dimension bounds, int depth) {
		super(startCoordinates, bounds, depth);
		// TODO Auto-generated constructor stub
	}
	
	public QRNode(Point startCoordinates, Dimension bounds, int depth,
			int maxDepth, int maxChildren) {
		super(startCoordinates, bounds, depth, maxDepth, maxChildren);
	}
	
	
	/*
	 * build rtree for the leaf nodes
	 */
	public void buildRTree() {
		// for a leaf node, build a rtree
		if (0 == nodes.size()) {
			rtree = new RTree<T>(5, 2, 2);
			for (PointNodeElement<T> element : elements) {
				float coords[] = {((Grid) element.getElement()).getLonBin(), ((Grid) element.getElement()).getLatBin()};
				rtree.insert(coords, element.getElement());
//				System.err.println("insert element");
			}
		}
		// for a non-leaf node, build tree for its children
		else {
			((QRNode<T>)(this.nodes.get(Cell.TOP_LEFT))).buildRTree();
//			System.err.println("topleft");
			((QRNode<T>)(this.nodes.get(Cell.TOP_RIGHT))).buildRTree();
//			System.err.println("topright");
			((QRNode<T>)(this.nodes.get(Cell.BOTTOM_LEFT))).buildRTree();
//			System.err.println("bl");
			((QRNode<T>)(this.nodes.get(Cell.BOTTOM_RIGHT))).buildRTree();
//			System.err.println("br");
		}
	}
	
	
	/**
	 * Subdivide the current node and add subnodes
	 */
	public void subdivide() {
		//log.debug("Subdividing node at depth " + depth);
//		System.out.println("Subdividing node at depth " + depth);
		int depth = this.depth + 1;

		int bx = this.startCoordinates.x;
		int by = this.startCoordinates.y;

		// Create the bounds for the new cell
		Dimension newBounds = new Dimension(this.bounds.width / 2,
				this.bounds.height / 2);

		// Add new bounds to current start coordinates to calculate the new
		// start coordinates
		int newXStartCoordinate = bx + newBounds.width;
		int newYStartCoordinate = by + newBounds.height;

		PointNode<T> cellNode = null;

		// top left
		cellNode = new QRNode<T>(new Point(bx, by), newBounds, depth,
				this.maxDepth, this.maxElements);
		this.nodes.put(Cell.TOP_LEFT, cellNode);

		// top right
		cellNode = new QRNode<T>(new Point(newXStartCoordinate, by),
				newBounds, depth, this.maxDepth, this.maxElements);
		this.nodes.put(Cell.TOP_RIGHT, cellNode);

		// bottom left
		cellNode = new QRNode<T>(new Point(bx, newYStartCoordinate),
				newBounds, depth, this.maxDepth, this.maxElements);
		this.nodes.put(Cell.BOTTOM_LEFT, cellNode);

		// bottom right
		cellNode = new QRNode<T>(new Point(newXStartCoordinate,
				newYStartCoordinate), newBounds, depth, this.maxDepth,
				this.maxElements);
		this.nodes.put(Cell.BOTTOM_RIGHT, cellNode);
	}
	
	
	public RTree<T> getRTree() {
		return rtree;
	}
	
	public QRNode<T> getLeafNode(Point coordinates) {
		if (nodes.size() > 0) {
			Cell index = findIndex(coordinates);
			PointNode<T> node = this.nodes.get(index);
			return ((QRNode<T>) node).getLeafNode(coordinates);
		}
		else {
			return (QRNode<T>) this;
		}
	}
	
	public  Vector<T> getAllElements() {
		Vector<T> allElements = new Vector<T>();
		if (0 == nodes.size()) {
			for (int i = 0; i < this.getElements().size(); ++i) {
				allElements.add(getElements().get(i).getElement());
			}
		}
		else {
			Vector<T> elems;
			elems = ((QRNode<T>)(this.nodes.get(Cell.TOP_LEFT))).getAllElements();
			for (int i = 0; i < elems.size(); ++i) {
				allElements.add(elems.get(i));
			}
			elems = ((QRNode<T>)(this.nodes.get(Cell.TOP_RIGHT))).getAllElements();
			for (int i = 0; i < elems.size(); ++i) {
				allElements.add(elems.get(i));
			}
			elems = ((QRNode<T>)(this.nodes.get(Cell.BOTTOM_LEFT))).getAllElements();
			for (int i = 0; i < elems.size(); ++i) {
				allElements.add(elems.get(i));
			}
			elems = ((QRNode<T>)(this.nodes.get(Cell.BOTTOM_RIGHT))).getAllElements();
			for (int i = 0; i < elems.size(); ++i) {
				allElements.add(elems.get(i));
			}
			
		}
		
		return allElements;
	}

}
