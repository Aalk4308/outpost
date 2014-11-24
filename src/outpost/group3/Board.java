package outpost.group3;

import java.util.*;

import outpost.group3.Consts;
import outpost.group3.Cell;
import outpost.group3.Loc;
import outpost.group3.JPS;

import outpost.sim.Pair;
import outpost.sim.Point;

public class Board {
	static final int dimension = Consts.dimension;
	
	private class PlayerSummary {
		public int totalCells;
		public int landCells;
		public int waterCells;
		
		public void reset() {
			totalCells = 0;
			landCells = 0;
			waterCells = 0;
		}
	}
	
	public int playerId;
	public double r;
	public double L;
	public double W;
	
	private Cell[][] cells;
	private boolean landGrid[][];
	private ArrayList<ArrayList<Loc>> outposts;
	private ArrayList<PlayerSummary> playerSummaries;
	
	private JPS jps;
	
	/* Transforms a coordinate to/from the simulator and system where our player is always at (0,0) */ 
	public void simFlip(Loc loc) {
		if (playerId == 1 || playerId == 2)
			loc.x = dimension - loc.x - 1;
		
		if (playerId == 3 || playerId == 2)
			loc.y = dimension - loc.y - 1;
	}
		
	Board(int playerId, Point[] simGrid, double r, double L, double W) {
		if (simGrid.length != dimension*dimension)
			System.err.println("Attempting to create board with wrong number of Points");
		
		this.playerId = playerId;
		this.r = r;
		this.L = L;
		this.W = W;
		cells = new Cell[dimension][dimension];
		landGrid = new boolean[dimension][dimension];
		outposts = new ArrayList<ArrayList<Loc>>();
		playerSummaries = new ArrayList<PlayerSummary>();
		
		for (int i = 0; i < simGrid.length; i++) {
			Point p = simGrid[i];
			Loc loc = new Loc(p.x, p.y);
			simFlip(loc);
			cells[loc.x][loc.y] = new Cell(loc.x, loc.y, p.water ? Cell.CellType.WATER : Cell.CellType.LAND);
			landGrid[loc.x][loc.y] = !p.water;
		}
	
		for (int id = 0; id < Consts.numPlayers; id++) {
			outposts.add(new ArrayList<Loc>());
			playerSummaries.add(new PlayerSummary());
		}
		
		for (int x = 0; x < dimension; x++) {
			for (int y = 0; y < dimension; y++) {
				cells[x][y].setNearestLand(findNearestLand(x, y));
			}
		}
		
		jps = new JPS(landGrid, dimension, dimension);
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
			playerSummaries.get(id).reset();
			
			for (Pair pair : simOutpostList.get(id)) {
				Loc loc = new Loc(pair.x, pair.y);
				simFlip(loc);
				cells[loc.x][loc.y].incNumOutposts(id);
				outposts.get(id).add(loc);
			}
		}
		
		// Update state of each cell
		for (int x = 0; x < dimension; x++) {
			for (int y = 0; y < dimension; y++) {
				Cell cell = cells[x][y];
				cell.setNeutral();
				
				for (int id = 0; id < Consts.numPlayers; id++) {
					for (Loc loc : outposts.get(id)) {
						double d = Loc.mDistance(x, y, loc);
						if (d < r && d == cell.getOwnerDistance() && id != cell.getOwnerId()) {
							cell.setDisputed();
						} else if (d < r && d < cell.getOwnerDistance()) {
							cell.setOwned(id, d);
						}
					}
				}
			}
		}
		
		// Calculate scores and other metrics, and perhaps also analyze paths and so forth
		// For each player, want to know: total territory controlled, land controlled, water controlled, supportable outposts
		for (int x = 0; x < dimension; x++) {
			for (int y = 0; y < dimension; y++) {
				Cell cell = cells[x][y];
				if (cell.isOwned()) {
					int id = cell.getOwnerId();
					playerSummaries.get(id).totalCells += 1;
					if (cell.isLand())
						playerSummaries.get(id).landCells += 1;
					else
						playerSummaries.get(id).waterCells += 1;
				}
			}
		}
	}
	
	private Cell findNearestLand(int xStart, int yStart) {
		for (int d = 0; d < dimension; d++) {
			int x = xStart - d;
			int y = yStart;
			
			for (int j = 0; j < 4; j++) {
				for (int i = 0; i <= d; i ++) {
					x += i * (j <= 1 ? 1 : -1);
					y += i * (j == 2 || j == 3 ? 1 : -1);
					
					if (isInside(x, y) && cells[x][y].isLand())
						return cells[x][y];
				}
			}
		}
		
		return null;
	}
	
	private Cell findNearestWater(int xStart, int yStart){
		for (int d = 0; d < dimension; d++) {
			int x = xStart - d;
			int y = yStart;
			
			for (int j = 0; j < 4; j++) {
				for (int i = 0; i <= d; i ++) {
					x += i * (j <= 1 ? 1 : -1);
					y += i * (j == 2 || j == 3 ? 1 : -1);
					
					if (isInside(x, y) && !cells[x][y].isLand())
						return cells[x][y];
				}
			}
		}
		
		return null;
		
	}
	
	public Cell getCell(int x, int y) {
		return cells[x][y];
	}
	
	public Cell getCell(Loc loc) {
		return getCell(loc.x, loc.y);
	}
	
	public ArrayList<Loc> findPath(int xStart, int yStart, int xEnd, int yEnd) {
		return findPath(new Loc(xStart, yStart), new Loc(xEnd, yEnd));
	}
	
	public ArrayList<Loc> findPath(Loc start, Loc end) {
		return jps.findPath(start, end);
	}
	
	public Loc nearestLand(Loc loc) {
                Loc l = new Loc(loc);
                if (l.x > dimension)
                  l.x = dimension;
                else if (l.x < 0)
                  l.x = 0;
                if (l.y > dimension)
                  l.y = dimension;
                else if (l.y < 0)
                  l.y = 0;
		Cell c = cells[l.x][l.y].getNearestLand();
		return new Loc(c.x, c.y);
	}
	

	
	public ArrayList<Loc> ourOutposts() {
		return outposts.get(playerId);
	}

        public ArrayList<Loc> theirOutposts(int id){
                return outposts.get(id);
        }
	
	public boolean cellHasOutpost(int x, int y) {
		return cells[x][y].hasOutpost();
	}
	
	public int numOutpostsOnCell(int x, int y) {
		return cells[x][y].getNumOutposts();
	}

	public boolean cellHasOutpostFor(int id, int x, int y) {
		return cells[x][y].hasOutpost(id);
	}
	
	public int numOutpostsOnCellFor(int id, int x, int y) {
		return cells[x][y].getNumOutposts();
	}
	
	public int scoreFor(int id) {
		return playerSummaries.get(id).totalCells;
	}
	
	public int numOutpostsFor(int id) {
		return outposts.get(id).size();
	}
	
	public int numOutpostsSupportableFor(int id) {
		return (int) Math.min(playerSummaries.get(id).landCells / L, playerSummaries.get(id).waterCells / W) + 1;
	}
	
	public static class DumpInfo {
		public static enum DumpType { TYPE, STATE, OWNER };
		
		private DumpType dumpType;
		ArrayList<Loc> path;
		
		DumpInfo(DumpType dumpType) {
			this.dumpType = dumpType;
		}
		
		DumpInfo(DumpType dumpType, ArrayList<Loc> path) {
			this.dumpType = dumpType;
			this.path = path;
		}
	}
	
	/* Debug function to print board to console.  Pass 1 for cellType, pass 2 for cellState, pass 3 for cellOwner */
    public void dump(DumpInfo dumpInfo) {
		String s = new String();
    	for (int y = 0; y < dimension; y++) {
			for (int x = 0; x < dimension; x++) {
				if (cells[x][y].hasOutpost()) {
					s = s + "O";
				} else if (dumpInfo.path != null && dumpInfo.path.contains(new Loc(x, y))) {
					s = s + "#";
				} else {
					if (dumpInfo.dumpType == DumpInfo.DumpType.TYPE) {
	 					if (cells[x][y].isLand())
	 						s = s + ".";
	 					else if (cells[x][y].isWater())
	 						s = s + "W";
					} else if (dumpInfo.dumpType == DumpInfo.DumpType.STATE) {
	 					if (cells[x][y].isOwned())
	 						s = s + "+";
	 					else if (cells[x][y].isDisputed())
	 						s = s + "X";
	 					else
	 						s = s + "-";
	 				} else if (dumpInfo.dumpType == DumpInfo.DumpType.OWNER) {
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
    
	private boolean isInside(int x, int y) {
		return (x >= 0 && x < dimension) && (y >= 0 && y < dimension);
	}
	
	public int numLandCellsFor(int id) {
		return playerSummaries.get(id).landCells;
	}

	public int numWaterCellsFor(int id) {
		return playerSummaries.get(id).waterCells;
	}

	public Loc nearestWater(Loc loc) {
		Cell c = cells[loc.x][loc.y].getNearestWater();
		return new Loc(c.x, c.y);
	}
}
