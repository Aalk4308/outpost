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
		
		PlayerSummary() {
			reset();
		}
		
		PlayerSummary(PlayerSummary ps) {
			totalCells = ps.totalCells;
			landCells = ps.landCells;
			waterCells = ps.waterCells;
		}
		
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
	public int T;
	
	private int ticks;
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
	
	// Constructor
	Board(int playerId, Point[] simGrid, double r, double L, double W, int T) {
		if (simGrid.length != dimension*dimension)
			System.err.println("Attempting to create board with wrong number of Points");
		
		this.playerId = playerId;
		this.r = r;
		this.L = L;
		this.W = W;
		this.T = T;
		
		ticks = 0;
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
				
				int numLandCellsNearby = 0;
				int numWaterCellsNearby = 0;
				ArrayList<Loc> nearbyLocs = getNearbyLocs(x, y);
				for (Loc loc : nearbyLocs) {
					if (cells[loc.x][loc.y].isLand())
						numLandCellsNearby++;
					else
						numWaterCellsNearby++;
				}
				
				cells[x][y].setNumLandCellsNearby(numLandCellsNearby);
				cells[x][y].setNumWaterCellsNearby(numWaterCellsNearby);
			}
		}
		
		jps = new JPS(landGrid, dimension, dimension);
	}

	// Copy constructor
	Board(Board board) {
		this.playerId = board.playerId;
		this.r = board.r;
		this.L = board.L;
		this.W = board.W;
		this.T = board.T;
		
		ticks = board.ticks;
		cells = new Cell[dimension][dimension];
		landGrid = board.landGrid;
		outposts = new ArrayList<ArrayList<Loc>>();
		playerSummaries = new ArrayList<PlayerSummary>();
		
		for (int x = 0; x < dimension; x++)
			for (int y = 0; y < dimension; y++)
				cells[x][y] = new Cell(board.cells[x][y]);
		
		for (int id = 0; id < Consts.numPlayers; id++) {
			outposts.add(new ArrayList<Loc>());
			
			for (int j = 0; j < board.outposts.get(id).size(); j++)
				outposts.get(id).add(new Loc(board.outposts.get(id).get(j)));
			
			playerSummaries.add(new PlayerSummary(board.playerSummaries.get(id)));
		}
		
		jps = new JPS(landGrid, dimension, dimension);
	}
	
	public void update(ArrayList<ArrayList<Pair>> simOutpostList) {
		if (simOutpostList.size() != Consts.numPlayers)
			System.err.println("Attempting to update board with wrong size list of player outposts");
		
		ticks++;
		
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
	
	private int getTicksRemaining() {
		return T - ticks;
	}
	
	private Loc findNearestLand(int xStart, int yStart) {
		for (int d = 0; d < dimension; d++) {
			int x = xStart - d;
			int y = yStart;
			
			for (int j = 0; j < 4; j++) {
				for (int i = 0; i <= d; i ++) {
					x += i * (j <= 1 ? 1 : -1);
					y += i * (j == 2 || j == 3 ? 1 : -1);
					
					if (isInside(x, y) && cells[x][y].isLand())
						return new Loc(x, y);
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
	
	// Returns the diamond of locations centered around (x,y) at given radius (which will typically be r)
	public ArrayList<Loc> getNearbyLocs(int xCenter, int yCenter, double radius) {
		ArrayList<Loc> nearbyLocs = new ArrayList<Loc>();
		
		for (int d = 0; d < radius; d++) {
			int x = xCenter - d;
			int y = yCenter;
			
			for (int j = 0; j < 4; j++) {
				for (int i = 0; i <= d; i ++) {
					x += i * (j <= 1 ? 1 : -1);
					y += i * (j == 2 || j == 3 ? 1 : -1);
					
					if (isInside(x, y))
						nearbyLocs.add(new Loc(x, y));
				}
			}
		}
		
		return nearbyLocs;
	}

	public ArrayList<Loc> getNearbyLocs(int x, int y) {
		return getNearbyLocs(x, y, r);
	}
	
	public ArrayList<Loc> getNearbyLocs(Loc l) {
		return getNearbyLocs(l.x, l.y, r);
	}
	
	public ArrayList<Loc> getNearbyLocs(Loc l, double radius) {
		return getNearbyLocs(l.x, l.y, radius);
	}
	
	public ArrayList<Loc> findPath(int xStart, int yStart, int xEnd, int yEnd) {
		return findPath(new Loc(xStart, yStart), new Loc(xEnd, yEnd));
	}
	
	public ArrayList<Loc> findPath(Loc start, Loc end) {
		return jps.findPath(start, end);
	}
	
	public Loc nearestLand(Loc loc) {
		return cells[loc.x][loc.y].getNearestLand();
	}
	
	public ArrayList<Loc> ourOutposts() {
		return outposts.get(playerId);
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
	
	public int numOutpostsSupportableOn(int x, int y) {
		Cell cell = cells[x][y];
		return (int) Math.min(cell.getNumLandCellsNearby() / L, cell.getNumWaterCellsNearby() / W);
	}
	
	public int numOutpostsSupportableOn(Loc l) {
		return numOutpostsSupportableOn(l.x, l.y);
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
}
