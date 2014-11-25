package outpost.group3;

import java.util.ArrayList;

import outpost.group3.Post;

public class DiagonalStrategy extends outpost.group3.Strategy {
	DiagonalStrategy() {}

	public void run(Board board, ArrayList<Post> outposts) {
		int numOutposts = outposts.size();
		
    	double sideLength = Math.min(65, Math.sqrt(Math.pow(2*board.r*numOutposts * 1, 2) / 2));
		
    	for (int i = 0; i < numOutposts; i++) {
    		int xTarget = (int) Math.round((i + 1) * (sideLength / (numOutposts + 1)));
    		int yTarget = (int) Math.round((numOutposts - i) * (sideLength / (numOutposts + 1)));
    		
    		outposts.get(i).setTargetLoc(new Loc(xTarget, yTarget));
    	}
	}
}
