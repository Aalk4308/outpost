package outpost.group3;

import java.util.*;

import outpost.group3.Loc;

public class JPS {	
	private class Node {
		int x;
		int y;
		boolean allowed;
		boolean opened;
		boolean closed;
		float g;
		float f;
		
		Node (int x, int y, boolean allowed) {
			this.x = x;
			this.y = y;
			this.allowed = allowed;
			this.opened = false;
			this.closed = false;
			this.g = 0;
			this.f = 0;
		}	
	}
	
	private class NodeComparator implements Comparator<Node> {
		@Override
		public int compare(Node n1, Node n2) {
			return (int) (n1.f - n2.f);
		}
	}
	
	private boolean rawGrid[][];
	private Node grid[][];
	private int width;
	private int height;
	private Node startNode;
	private Node endNode;
	
	private PriorityQueue<Node> openList;
	
	JPS (boolean rawGrid[][], int width, int height) {
		this.rawGrid = rawGrid;
		this.width = width;
		this.height = height;
	}
	
	public ArrayList<Loc> findPath(Loc startLoc, Loc endLoc) {
		Comparator<Node> comparator = new NodeComparator();
		openList = new PriorityQueue<Node>(100, comparator);
		
		// Set up the grid of nodes
		grid = new Node[width][height];
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				grid[x][y] = new Node(x, y, rawGrid[x][y]);

		startNode = grid[startLoc.x][startLoc.y];
		endNode = grid[endLoc.x][endLoc.y];
		
		openList.add(startNode);
		startNode.opened = true;
		
		while (!openList.isEmpty()) {
			Node node = openList.poll();
			node.closed = true;
			
			if (node.x == endNode.x && node.y == endNode.y) {
				return new ArrayList<Loc>();
			}
			
			identifySuccessors(node);
		}
		
		return new ArrayList<Loc>();
	}
	
	private void identifySuccessors(Node node) {
		
	}
}
