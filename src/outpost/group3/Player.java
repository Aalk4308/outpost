package outpost.group3;

import java.util.*;

import outpost.sim.Pair;
import outpost.sim.Point;
import outpost.sim.movePair;
import outpost.group3.Board;
import outpost.group3.Post;

public class Player extends outpost.sim.Player {

    static int size = 100;
    static Random random = new Random();

    private boolean isInitialized = false;
    private Board board;
    
    ArrayList<Post> outposts;
    int nextOutpostId;
    
    public Player(int id) {
      super(id);
    }

    public void init() {}

    public int delete(ArrayList<ArrayList<Pair>> king_outpostlist, Point[] gridin) {
      //System.out.printf("haha, we are trying to delete a outpost for player %d\n", this.id);
      int del = random.nextInt(king_outpostlist.get(id).size());
      return del;
    }

    public ArrayList<movePair> move(ArrayList<ArrayList<Pair>> simOutpostList, Point[] simGrid, int r, int L, int W, int T) {
    	if (!isInitialized) {
    		board = new Board(id, simGrid, r, L, W, T);
    		outposts = new ArrayList<Post>();
    		nextOutpostId = 0;
    		isInitialized = true;
    	}
    	
    	// For each of our outposts in the list, find and update it in our persistent list, or add it if not
    	for (Post outpost : outposts)
    		outpost.setUpdated(false);
    	
    	for (int i = 0; i < simOutpostList.get(id).size(); i++) {
    		Pair pair = simOutpostList.get(id).get(i);
			Loc loc = new Loc(pair.x, pair.y);
			board.simFlip(loc);
			
			boolean existing = false;
			for (Post outpost : outposts) {
				if (!outpost.isUpdated() && outpost.isAlive() && Loc.equals(outpost.getExpectedLoc(), loc)) {
					outpost.setCurrentLoc(loc);
					outpost.setUpdated(true);
					outpost.setSimIndex(i);
					existing = true;
					break;
				}
			}
			
			if (!existing) {
				outposts.add(new Post(nextOutpostId, loc, i));
				nextOutpostId++;
			}
		}
    	
    	for (Post outpost : outposts)
    		if (!outpost.isUpdated() && outpost.isAlive())
    			outpost.destroy();
    	
    	// Update the board object
    	board.update(simOutpostList);
    	
    	/* Here is where we would select a strategy based on the state of the board (resource scarcity, etc.) */
    	
    	//Strategy strategy = new DiagonalStrategy();
    	//targets = strategy.run(board);
    	
    	// PASS ONLY LIVE OUTPOSTS HERE
    	Strategy resources = new GetResources(); //call when resources are scarce
    	resources.run(board, outposts);
    	
    	// Pass back to the simulator where we want our outposts to go
    	ArrayList<movePair> moves = new ArrayList<movePair>();
    	
    	for (Post outpost : outposts) {
    		if (outpost.isAlive()) {
    			Loc currentLoc = outpost.getCurrentLoc();
    			Loc targetLoc = outpost.getTargetLoc();
        		ArrayList<Loc> path = board.findPath(currentLoc, targetLoc);
        		
        		if (path == null || path.size() == 0 || path.size() == 1) {
        			outpost.setExpectedLoc(new Loc(currentLoc));
        			board.simFlip(currentLoc);
        			moves.add(new movePair(outpost.getSimIndex(), new Pair(currentLoc.x, currentLoc.y)));
        		} else {
        			outpost.setExpectedLoc(new Loc(path.get(1)));
        			Loc expectedLoc = path.get(1);
        			board.simFlip(expectedLoc);
        			moves.add(new movePair(outpost.getSimIndex(), new Pair(expectedLoc.x, expectedLoc.y)));
        		}
    		}
    	}
    	
    	/*
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
    	*/
    	
    	return moves;
    }
}
