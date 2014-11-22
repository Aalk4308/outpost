package outpost.group3;

import java.util.ArrayList;

import outpost.group3.Consts;

public class Cell {
	public static enum CellType { LAND, WATER };
	public static enum CellState { NEUTRAL, OWNED, DISPUTED };
	
	public int x;
	public int y;
	
	private CellType cellType;
	private CellState cellState;
	private int cellOwnerId;					// -1 for NEUTRAL or DISPUTED cells
	private double cellOwnerDistance;			// Double.MAX_VALUE for NEUTRAL or DISPUTED cells
	private int[] numOutposts;					// Number of outposts on this cell by playerId
	private Loc nearestLand;
	
	Cell (int x, int y, CellType cellType) {
		this.x = x;
		this.y = y;
		this.cellType = cellType;
		this.setNeutral();
		this.numOutposts = new int[Consts.numPlayers];
	}

	
	Cell (Cell cell) {
		this.cellType = cell.cellType;
		this.cellState = cell.cellState;
		this.cellOwnerId = cell.cellOwnerId;
		this.cellOwnerDistance = cell.cellOwnerDistance;
		this.numOutposts = new int[Consts.numPlayers];
		System.arraycopy(cell.numOutposts, 0, this.numOutposts, 0, cell.numOutposts.length);
		this.nearestLand = new Loc(cell.nearestLand);
	}
	
	public CellType getType() {
		return this.cellType;
	}
	
	public boolean isLand() {
		return (cellType == CellType.LAND);
	}
	
	public boolean isWater() {
		return (cellType == CellType.WATER);
	}
	
	public CellState getState() {
		return cellState;
	}
	
	public boolean isNeutral() {
		return (cellState == CellState.NEUTRAL);
	}
	
	public boolean isDisputed() {
		return (cellState == CellState.DISPUTED);
	}
	
	public boolean isOwned() {
		return (cellState == CellState.OWNED);
	}
	
	public int getOwnerId() {
		return cellOwnerId;
	}
	
	public double getOwnerDistance() {
		return cellOwnerDistance;
	}
	
	public void setNeutral() {
		this.cellState = CellState.NEUTRAL;
		this.cellOwnerId = -1;
		this.cellOwnerDistance = Double.MAX_VALUE;
	}
	
	public void setDisputed() {
		this.cellState = CellState.DISPUTED;
		this.cellOwnerId = -1;
		this.cellOwnerDistance = Double.MAX_VALUE;
	}
	
	public void setOwned(int cellOwnerId, double distance) {
		this.cellState = CellState.OWNED;
		this.cellOwnerId = cellOwnerId;
		this.cellOwnerDistance = distance;
	}
	
	public Loc getNearestLand() {
		return nearestLand;
	}
	
	public void setNearestLand(Loc nearestLand) {
		this.nearestLand = nearestLand;
	}
	
	public boolean hasOutpost() {
		return getNumOutposts() > 0;
	}
	
	public int getNumOutposts() {
		int n = 0;
		
		for (int id = 0; id < Consts.numPlayers; id++)
			n += getNumOutposts(id);
		
		return n;
	}
	
	public boolean hasOutpost(int id) {
		return getNumOutposts(id) > 0;
	}
	
	public int getNumOutposts(int id) {
		return numOutposts[id];
	}
	
	public void setNumOutposts(int id, int num) {
		numOutposts[id] = num;
	}
	
	public void incNumOutposts(int id) {
		numOutposts[id]++;
	}
}