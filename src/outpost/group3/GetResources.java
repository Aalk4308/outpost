package outpost.group3;

import java.util.ArrayList;

import outpost.group3.Post;

public class GetResources extends outpost.group3.Strategy {
	GetResources() {}
	
	public void run(Board board, ArrayList<Post> outposts) {
		ArrayList<Loc> targets = new ArrayList<Loc>();
		
		for (Post outpost : outposts) {
			double bestVal = 0;
			Loc currentLoc = outpost.getCurrentLoc();
			Loc bestLoc = new Loc(currentLoc);
			
			for (int x = 0; x < Board.dimension; x++) {
				for (int y = 0; y < Board.dimension; y++) {
					Loc loc = new Loc(x, y);
					Cell cell = board.getCell(loc);
					
					double val = ((double) board.numOutpostsSupportableOn(loc)) / Loc.mDistance(loc, currentLoc.x, currentLoc.y);
					if (cell.isLand() && val > bestVal) {
						boolean overlap = false;
						
						for (Loc target : targets) {
							if (Loc.mDistance(target, loc) < 2*board.r) {
								overlap = true;
								break;
							}
						}
						
						if (!overlap) {
							bestVal = val;
							bestLoc = loc;
						}
					}
				}
			}
			
			targets.add(bestLoc);
			outpost.setTargetLoc(bestLoc);
		}
	}
}