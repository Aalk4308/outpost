package outpost.group3;
import java.util.ArrayList;



public class GetResources extends outpost.group3.Strategy {
	GetResources() {}
	
	public ArrayList<Loc> run(Board board) {
		ArrayList<Loc> targets = new ArrayList<Loc>();
		
		ArrayList<Loc> outposts = board.ourOutposts();
		int numOutposts = outposts.size();
		
		for (int outpostId = 0; outpostId < numOutposts; outpostId++) {
			int x = outposts.get(outpostId).x;
			int y = outposts.get(outpostId).y;
			
			Loc waterlocation= board.nearestWater(new Loc(x, y));
			
			targets.add(outpostId,board.nearestLand(waterlocation));
			
			if(((numOutposts-1)* board.W)*1.25 < board.numWaterCellsFor(board.playerId) )//if watercells are greater than ((n-1)*w)*1.25 , break the loop
				break;
		}
		
		return targets;
	}
}