package outpost.group3;

import java.util.ArrayList;
import outpost.group3.Outpost;
public class ProtectHome extends outpost.group3.Strategy{

	ProtectHome() {}
	
	public void run(Board board, ArrayList<Outpost> outposts) {
				
			Loc loc0 = new Loc(0,0);
			Cell cell0 = board.getCell(loc0);
			if (cell0.isLand())
				outposts.get(0).setTargetLoc(loc0);
			else
				outposts.get(0).setTargetLoc(board.nearestLand(loc0));
			
			
			Loc loc1 = new Loc(1,0);
			Cell cell1 = board.getCell(loc1);
			if (cell1.isLand())
				outposts.get(1).setTargetLoc(loc1);
			else
				outposts.get(1).setTargetLoc(board.nearestLand(loc1));
			
			Loc loc2 = new Loc(0,1);
			Cell cell2 = board.getCell(loc2);
			if (cell2.isLand())
				outposts.get(2).setTargetLoc(loc2);
			else
				outposts.get(2).setTargetLoc(board.nearestLand(loc2));
	
	}
}
