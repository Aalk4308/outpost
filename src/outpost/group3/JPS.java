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
		Node parent;
		
		Node (int x, int y, boolean allowed) {
			this.x = x;
			this.y = y;
			this.allowed = allowed;
			this.opened = false;
			this.closed = false;
			this.g = 0;
			this.f = 0;
			this.parent = null;
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
	
	private ArrayList<Loc> orthogonalNodes(Node node) {
		ArrayList<Loc> locs = new ArrayList<Loc>();
		
		if (node.x > 0)
			locs.add(new Loc(node.x - 1, node.y));
		
		if (node.y > 0)
			locs.add(new Loc(node.x, node.y - 1));
		
		if (node.x < width - 1)
			locs.add(new Loc(node.x + 1, node.y));
		
		if (node.y < height - 1)
			locs.add(new Loc(node.x, node.y + 1));
		
		return locs;
	}
	
	private ArrayList<Loc> findNeighbors(Node node) {
		if (node.parent != null) {
			ArrayList<Loc> neighbors = new ArrayList<Loc>();			
			
			int dx = (node.x - node.parent.x) / Math.max(Math.abs(node.x - node.parent.x), 1);
			int dy = (node.y - node.parent.y) / Math.max(Math.abs(node.y - node.parent.y), 1);
			
			if (dx != 0) {
				if (grid[node.x][node.y - 1].allowed)
					neighbors.add(new Loc(node.x, node.y - 1));
				
				if (grid[node.x][node.y + 1].allowed)
					neighbors.add(new Loc(node.x, node.y + 1));
				
				if (grid[node.x + dx][node.y].allowed)
					neighbors.add(new Loc(node.x + dx, node.y));
			} else if (dy != 0) {
				if (grid[node.x - 1][node.y].allowed)
					neighbors.add(new Loc(node.x - 1, node.y));
				
				if (grid[node.x + 1][node.y].allowed)
					neighbors.add(new Loc(node.x + 1, node.y));
				
				if (grid[node.x][node.y + dy].allowed)
					neighbors.add(new Loc(node.x, node.y + dy));
			}
			
			return neighbors;
		} else {
			return orthogonalNodes(node);
		}
	}
	
	private Loc jump(int x, int y, int px, int py) {
		return new Loc();
	}
	
	private void identifySuccessors(Node node) {
        ArrayList<Loc> neighbors = findNeighbors(node);
        
        for (Loc neighbor : neighbors) {
        	Loc jumpPoint = jump(neighbor.x, neighbor.y, node.x, node.y);
        	
        	
        }
        
        /*for(i = 0, l = neighbors.length; i < l; ++i) {
            neighbor = neighbors[i];
            jumpPoint = this._jump(neighbor[0], neighbor[1], x, y);
            if (jumpPoint) {

                jx = jumpPoint[0];
                jy = jumpPoint[1];
                jumpNode = grid.getNodeAt(jx, jy);

                if (jumpNode.closed) {
                    continue;
                }

                // include distance, as parent may not be immediately adjacent:
                d = Heuristic.octile(abs(jx - x), abs(jy - y));
                ng = node.g + d; // next `g` value

                if (!jumpNode.opened || ng < jumpNode.g) {
                    jumpNode.g = ng;
                    jumpNode.h = jumpNode.h || heuristic(abs(jx - endX), abs(jy - endY));
                    jumpNode.f = jumpNode.g + jumpNode.h;
                    jumpNode.parent = node;

                    if (!jumpNode.opened) {
                        openList.push(jumpNode);
                        jumpNode.opened = true;
                    } else {
                        openList.updateItem(jumpNode);
                    }
                }
            }
        }*/
	}
}
