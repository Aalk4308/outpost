package outpost.group3;

import java.util.*;

import outpost.sim.Pair;
import outpost.sim.Point;
import outpost.sim.movePair;

import outpost.group3.Board;

public class Player extends outpost.sim.Player {
	static int size = 100;
	static Random random = new Random();
	
	private boolean isInitialized = false;
	private Board board;
	
    public Player(int id) {
		super(id);
	}

	public void init() {}
    
    public int delete(ArrayList<ArrayList<Pair>> king_outpostlist, Point[] gridin) {
    	//System.out.printf("haha, we are trying to delete a outpost for player %d\n", this.id);
    	int del = random.nextInt(king_outpostlist.get(id).size());
    	return del;
    }
    
    public ArrayList<movePair> move(ArrayList<ArrayList<Pair>> simOutpostList, Point[] simGrid, int r, int L, int W, int T){
    	if (!isInitialized) {
    		board = new Board(id, simGrid, r, L, W, T);
    		isInitialized = true;
    	}

    	board.update(simOutpostList);
    	
    	/* Here is where we would select a strategy based on the state of the board (resource scarcity, etc.) */
    	Strategy strategy = new DiagonalStrategy();
    	ArrayList<Loc> targets = strategy.run(board);
    	
    	ArrayList<movePair> moves = new ArrayList<movePair>();
    	
    	for (int i = 0; i < targets.size(); i++) {
    		Loc outpostLoc = board.ourOutposts().get(i);
    		Loc targetLoc = targets.get(i);
    		ArrayList<Loc> path = board.findPath(outpostLoc, targetLoc);
    		
    		if (path == null || path.size() == 0 || path.size() == 1)
    			moves.add(new movePair(i, new Pair(outpostLoc.x, outpostLoc.y)));
    		else {
    			board.simFlip(path.get(1));
    			moves.add(new movePair(i, new Pair(path.get(1).x, path.get(1).y)));
    		}
    	}
    	
    	return moves;
    }
}
