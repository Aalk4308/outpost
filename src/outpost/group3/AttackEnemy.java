package outpost.group3;

import java.util.ArrayList;
import outpost.group3.Outpost;
public class AttackEnemy extends outpost.group3.Strategy{

	AttackEnemy() {}
	
	public void run(Board board, ArrayList<Outpost> outposts) {
				
			Loc loc0 = new Loc(Board.dimension-1,Board.dimension-1);
			Cell cell0 = board.getCell(loc0);
			if (cell0.isLand())
				outposts.get(10).setTargetLoc(loc0); //index 10, 11 and 12 are random
			else
				outposts.get(10).setTargetLoc(board.nearestLand(loc0));
			
			
			Loc loc1 = new Loc(Board.dimension-1,0);
			Cell cell1 = board.getCell(loc1);
			if (cell1.isLand())
				outposts.get(11).setTargetLoc(loc1);
			else
				outposts.get(11).setTargetLoc(board.nearestLand(loc1));
			
			Loc loc2 = new Loc(0,Board.dimension-1);
			Cell cell2 = board.getCell(loc2);
			if (cell2.isLand())
				outposts.get(12).setTargetLoc(loc2);
			else
				outposts.get(12).setTargetLoc(board.nearestLand(loc2));
	
	}
}