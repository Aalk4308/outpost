package outpost.group3;

import java.util.*;

import outpost.group3.Loc;

public class JPS {	
	private class Node {
		int x;
		int y;
		boolean allowed;
		boolean open;
		boolean closed;
		float g;
		float f;
		
		Node (int x, int y, boolean allowed) {
			this.x = x;
			this.y = y;
			this.allowed = allowed;
			this.open = false;
			this.closed = false;
			this.g = 0;
			this.f = 0;
		}	
	}
	
	private boolean rawGrid[][];
	private int width;
	private int height;
	
	private PriorityQueue<Node> openList;
	
	JPS (boolean rawGrid[][], int width, int height) {
		this.rawGrid = rawGrid;
		this.width = width;
		this.height = height;
	}
	
	public ArrayList<Loc> findPath(Loc startLoc, Loc endLoc) {
		
		
		return new ArrayList<Loc>();
	}
}
