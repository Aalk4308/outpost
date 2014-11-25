package outpost.group3;
import java.util.ArrayList;



public class GetResources extends outpost.group3.Strategy {
	GetResources() {}
	
	public ArrayList<Loc> run(Board board) {
		ArrayList<Loc> targets = new ArrayList<Loc>();
		
		ArrayList<Loc> outposts = board.ourOutposts();
		int numOutposts = outposts.size();
		
		for (int outpostId = 0; outpostId < numOutposts; outpostId++) {
			int ox = outposts.get(outpostId).x;
			int oy = outposts.get(outpostId).y;
			
			double bestVal = 0;
			Loc bestLoc = new Loc(ox, oy);
			
			for (int x = 0; x < Board.dimension; x++) {
				for (int y = 0; y < Board.dimension; y++) {
					Loc loc = new Loc(x, y);
					Cell cell = board.getCell(loc);
					
					double val = ((double) board.numOutpostsSupportableOn(loc)) / Loc.mDistance(loc, ox, oy);
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
		}
		
		return targets;
	}
}