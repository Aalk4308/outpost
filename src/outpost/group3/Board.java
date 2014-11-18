package outpost.group3;

import java.util.*;

import outpost.group3.Consts;
import outpost.group3.Cell;
import outpost.group3.Loc;
import outpost.sim.Pair;
import outpost.sim.Point;

public class Board {
	static final int dimension = Consts.dimension;
	
	private double r;
	
	private Cell[][] cells;
	private ArrayList<ArrayList<Loc>> outposts;
	
	Board(Point[] simGrid, double r) {
		if (simGrid.length != dimension*dimension)
			System.err.println("Attempting to create board with wrong number of Points");
		
		this.r = r;
		cells = new Cell[dimension][dimension];
		outposts = new ArrayList<ArrayList<Loc>>();
		
		for (int i = 0; i < simGrid.length; i++) {
			Point p = simGrid[i];
			cells[p.x][p.y] = new Cell(p.water ? Cell.CellType.WATER : Cell.CellType.LAND);
		}
		
		for (int id = 0; id < Consts.numPlayers; id++)
			outposts.add(new ArrayList<Loc>());
	}
	
	public void update(ArrayList<ArrayList<Pair>> simOutpostList) {
		if (simOutpostList.size() != Consts.numPlayers)
			System.err.println("Attempting to update board with wrong size list of player outposts");
		
		// Update number of outposts on each cell and outpost list per player
		for (int id = 0; id < Consts.numPlayers; id++) {
			for (int x = 0; x < dimension; x++) {
				for (int y = 0; y < dimension; y++) {
					cells[x][y].setNumOutposts(id, 0);
				}
			}

			outposts.get(id).clear();
			
			for (Pair pair : simOutpostList.get(id)) {
				cells[pair.x][pair.y].incNumOutposts(id);
				outposts.get(id).add(new Loc(pair.x, pair.y));
			}
		}
		
		// Update state of each cell
		for (int x = 0; x < dimension; x++) {
			for (int y = 0; y < dimension; y++) {
				Cell cell = cells[x][y];
				cell.setNeutral();
				
				for (int id = 0; id < Consts.numPlayers; id++) {
					for (Loc loc : outposts.get(id)) {
						double d = Loc.distance(x, y, loc);
						if (d < r && d == cell.getOwnerDistance() && id != cell.getOwnerId()) {
							cell.setDisputed();
						} else if (d < r && d < cell.getOwnerDistance()) {
							cell.setOwned(id, d);
						}
					}
				}
			}
		}
	}
	
	public boolean hasOutpost(int x, int y) {
		return cells[x][y].hasOutpost();
	}
	
	public int numOutposts(int x, int y) {
		return cells[x][y].getNumOutposts();
	}

	public boolean hasOutpostFor(int id, int x, int y) {
		return cells[x][y].hasOutpost(id);
	}
	
	public int numOutpostsFor(int id, int x, int y) {
		return cells[x][y].getNumOutposts();
	}
	
	/* Debug function to print board to console.  Pass 1 for cellType, pass 2 for cellState, pass 3 for cellOwner */
    public void dump(int dumpType) {
		String s = new String();
    	for (int y = 0; y < dimension; y++) {
			for (int x = 0; x < dimension; x++) {
				if (cells[x][y].hasOutpost()) {
					s = s + "O";
				} else {
					if (dumpType == 1) {
	 					if (cells[x][y].isLand())
	 						s = s + "@";
	 					else if (cells[x][y].isWater())
	 						s = s + "W";
					} else if (dumpType == 2) {
	 					if (cells[x][y].isOwned())
	 						s = s + "+";
	 					else if (cells[x][y].isDisputed())
	 						s = s + "X";
	 					else
	 						s = s + "-";
	 				} else if (dumpType == 3) {
	 					if (cells[x][y].isOwned())
	 						s = s + cells[x][y].getOwnerId();
	 					else
	 						s = s + "-";
	 				}
				}
				s = s + " ";
			}
			
			s = s + "\n";
		}
    	
    	System.out.printf(s);
    }
}
