package outpost.group3;

import java.util.ArrayList;
import outpost.group3.Outpost;
public class AttackEnemy extends outpost.group3.Strategy{

	AttackEnemy() {}
	
	public void run(Board board, ArrayList<Outpost> outposts) {
		for (int i = 0; i < outposts.size(); i++) {
			Outpost outpost = outposts.get(i);
			if (outpost.getTargetLoc() == null) {
				
				
				
				if (i % (Consts.numPlayers + 2) == 0)
					outpost.setTargetLoc(board.nearestLand(new Loc(Board.dimension/2, Board.dimension/2)));
				else if (i % (Consts.numPlayers +2) == 1)
					outpost.setTargetLoc(board.nearestLand(new Loc(Board.dimension/2-1, Board.dimension/2-1)));
				
				if ((i % (Consts.numPlayers + 2) == 0) && (outpost.getCurrentLoc()==board.nearestLand(new Loc(Board.dimension/2, Board.dimension/2))))
					outpost.setTargetLoc(board.nearestLand(new Loc(Board.dimension - 1, Board.dimension - 1)));
				else if ((i % (Consts.numPlayers +2) == 1) && (outpost.getCurrentLoc()==board.nearestLand(new Loc(Board.dimension/2-1, Board.dimension/2-1))))
					outpost.setTargetLoc(board.nearestLand(new Loc(Board.dimension - 2, Board.dimension - 2)));
				
				
				
				if (i % (Consts.numPlayers +2) == 2)
					outpost.setTargetLoc(new Loc(Board.dimension - 1, 2));
				else if (i % (Consts.numPlayers + 2) == 3)
					outpost.setTargetLoc(new Loc(Board.dimension - 1, 1));
				else if (i % (Consts.numPlayers + 2) == 4)
					outpost.setTargetLoc(new Loc(2, Board.dimension - 1));
				else if (i % (Consts.numPlayers + 2) == 5)
					outpost.setTargetLoc(new Loc(1, Board.dimension - 1));
			}
		}	
	}
}