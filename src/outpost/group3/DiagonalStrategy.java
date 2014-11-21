package outpost.group3;

import java.util.ArrayList;

public class DiagonalStrategy extends outpost.group3.Strategy {
	DiagonalStrategy() {}

	public ArrayList<Loc> run(Board board) {
		ArrayList<Loc> targets = new ArrayList<Loc>();
		
		ArrayList<Loc> outposts = board.ourOutposts();
		int numOutposts = outposts.size();
		
    	double sideLength = Math.min(65, Math.sqrt(Math.pow(2*board.r*numOutposts * 1, 2) / 2));
		
    	for (int outpostId = 0; outpostId < numOutposts; outpostId++) {
    		int xTarget = (int) Math.round((outpostId + 1) * (sideLength / (numOutposts + 1)));
    		int yTarget = (int) Math.round((numOutposts - outpostId) * (sideLength / (numOutposts + 1)));
    		
    		targets.add(board.nearestLand(new Loc(xTarget, yTarget)));
    	}
		
		return targets;
	}
}
