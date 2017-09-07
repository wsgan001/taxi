/**
 * 
 */
package com.geo.taxi.recommender;

import java.awt.Point;
import java.awt.Dimension;

import java.util.List;
//import java.util.ArrayList;
import java.util.Vector;

import com.geo.taxi.recommender.PointQuadTree;
import com.geo.taxi.recommender.QRNode;



/**
 * @author SL
 *
 */
public class QRTree<T> extends PointQuadTree<T> {


	public QRTree(Point startCoordinates, Dimension size) {
		super(startCoordinates, size);
		rootNode = new QRNode<T>(startCoordinates, size, 0);
		// TODO Auto-generated constructor stub
	}

	
	public QRTree(Point startCoordinates, Dimension size, int maxDepth, int maxChildren) {
		super(startCoordinates, size, maxDepth, maxChildren);
		rootNode = new QRNode<T>(startCoordinates, size, 0,maxDepth,maxChildren);
	}
	
	/*
	 * build rtrees for each leaf nodes of the qtree
	 */
	public void buildRTrees() {
		((QRNode<T>)rootNode).buildRTree();
	}
	
	public void findKNeighbors(int x, int y, int k, Vector<T> neighbors) {

		
		Vector<? extends AbstractNodeElement<T>> elements = getElements(new Point(x, y)); 
		QRNode<T> leafNode = ((QRNode<T>)rootNode).getLeafNode(new Point(x, y));
		
		if (elements.size() == k) {
			for (int i = 0; i < k; ++i) {
				neighbors.add(elements.get(i).getElement());
			}
		}
		else if (elements.size() > k) {
			RTree<T> rtree = leafNode.getRTree();
			List<T> elemList = null;
			for (int i = 1; i < Math.max(size.width, size.height); ++i) {
				elemList = rtree.search(new float [] {(float)x - i, (float)y - i}, new float [] {2.f * i, 2.f * i});
				if (elemList.size() >= k ) {
					break;
				}
			}
			for (int i = 0; i < elemList.size(); ++i) {
				neighbors.add(elemList.get(i));
			}
		}
		else {
			QRNode<T> node = (QRNode<T>)rootNode;
			QRNode<T> parentNode = (QRNode<T>)rootNode;
			while(node.getNumElements() >= k) {
				parentNode = node;
				node = (QRNode<T>) node.nodes.get(node.findIndex(new Point(x, y)));
			}
			neighbors = parentNode.getAllElements();
		}
		
	}
	

}
