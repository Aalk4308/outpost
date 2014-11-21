package outpost.group3;

import java.util.*;

import outpost.sim.Pair;
import outpost.sim.Point;
import outpost.sim.movePair;

import outpost.group3.Board;

public class Player extends outpost.sim.Player {
	 static int size =100;
	static Point[] grid = new Point[size*size];
	static Random random = new Random();
	static int[] theta = new int[100];
	static int counter = 0;
	
	private boolean isInitialized = false;
	private Board board;
	
    public Player(int id_in) {
		super(id_in);
		// TODO Auto-generated constructor stub
	}

	public void init() {
    	for (int i=0; i<100; i++) {
    		theta[i]=random.nextInt(4);
    	}
    }
    
    static double distance(Point a, Point b) {
        return Math.sqrt((a.x-b.x) * (a.x-b.x) +
                         (a.y-b.y) * (a.y-b.y));
    }
    
    // Return: the next position
    // my position: dogs[id-1]

    
    public int delete(ArrayList<ArrayList<Pair>> king_outpostlist, Point[] gridin) {
    	//System.out.printf("haha, we are trying to delete a outpost for player %d\n", this.id);
    	int del = random.nextInt(king_outpostlist.get(id).size());
    	return del;
    }
	//public movePair move(ArrayList<ArrayList<Pair>> king_outpostlist, int noutpost, Point[] grid) {
    public ArrayList<movePair> move(ArrayList<ArrayList<Pair>> king_outpostlist, Point[] gridin, int r, int L, int W, int T){
    	int numOutposts = king_outpostlist.get(id).size();
    	    	
    	double angle = 90 / (numOutposts + 1);
    	double sideLength = Math.min(65, Math.sqrt(Math.pow(2*r*numOutposts * 0.7,2) / 2));
    	
    	
    	ArrayList<movePair> moves = new ArrayList<movePair>();
    	
    	for (int outpostId = 0; outpostId < numOutposts; outpostId++) {
    		int xTarget = (int) Math.round((outpostId + 1) * (sideLength / (numOutposts + 1)));
    		int yTarget = (int) Math.round((numOutposts - outpostId) * (sideLength / (numOutposts + 1)));
    		
    		// Transform depending on where we are on the board
    		if (id == 1) {
    			xTarget = size - xTarget - 1;
    		} else if (id == 2) {
    			xTarget = size - xTarget - 1;
    			yTarget = size - yTarget - 1;
    		} else if (id == 3) {
    			yTarget = size - yTarget - 1;
    		}
    		
    		int xCurrent = king_outpostlist.get(id).get(outpostId).x;
    		int yCurrent = king_outpostlist.get(id).get(outpostId).y;
    		
    		if (xCurrent == xTarget && yCurrent == yTarget) {
    			moves.add(new movePair(outpostId, new Pair(xCurrent, yCurrent)));
    		} else if (xCurrent == xTarget && yCurrent != yTarget) {
    			moves.add(new movePair(outpostId, new Pair(xCurrent, yCurrent + (int) Math.signum(yTarget - yCurrent))));
    		} else if (xCurrent != xTarget && yCurrent == yTarget) {
    			moves.add(new movePair(outpostId, new Pair(xCurrent + (int) Math.signum(xTarget - xCurrent), yCurrent)));
    		} else {
    			if ((Math.abs(yTarget - yCurrent) / Math.abs(xTarget - xCurrent)) > 0) {
	    			if (counter % ((int) (Math.abs(yTarget - yCurrent) / Math.abs(xTarget - xCurrent))) == 0) {
	        			moves.add(new movePair(outpostId, new Pair(xCurrent + (int) Math.signum(xTarget - xCurrent), yCurrent)));    				
	    			} else {
	        			moves.add(new movePair(outpostId, new Pair(xCurrent, yCurrent + (int) Math.signum(yTarget - yCurrent))));
	    			}
    			} else {
	    			if (counter % ((int) (Math.abs(xTarget - xCurrent) / Math.abs(yTarget - yCurrent))) == 0) {
	        			moves.add(new movePair(outpostId, new Pair(xCurrent + (int) Math.signum(xTarget - xCurrent), yCurrent)));    				
	    			} else {
	        			moves.add(new movePair(outpostId, new Pair(xCurrent, yCurrent + (int) Math.signum(yTarget - yCurrent))));
	    			}    				
    			}
    		}
    	}
    	
    	counter = counter+1;
    	
    	return moves;
    	
    	/*
    	counter = counter+1;
    	if (counter % 10 == 0) {
    		for (int i=0; i<100; i++) {
        		theta[i]=random.nextInt(4);
        	}
    	}
    	ArrayList<movePair> nextlist = new ArrayList<movePair>();
    	//System.out.printf("Player %d\n", this.id);
    	for (int i=0; i<gridin.length; i++) {
    		grid[i]=new Point(gridin[i]);
    	}
    	ArrayList<Pair> prarr = new ArrayList<Pair>();
    	prarr = king_outpostlist.get(this.id);
    	for (int j =0; j<prarr.size()-1; j++) {
    		ArrayList<Pair> positions = new ArrayList<Pair>();
    		positions = surround(prarr.get(j));
    		boolean gotit=false;
    		while (!gotit) {
    			//Random random = new Random();
				//int theta = random.nextInt(positions.size());
				//System.out.println(theta);
    		//if (!PairtoPoint(positions.get(theta[j])).water && positions.get(theta[j]).x>0 && positions.get(theta[j]).y>0 && positions.get(theta[j]).x<size && positions.get(theta[j]).y<size) {
    			if (theta[j]<positions.size()){
        			if (positions.get(theta[j]).x>=0 && positions.get(theta[j]).y>=0 && positions.get(theta[j]).x<size && positions.get(theta[j]).y<size) {
        		
        				if (!PairtoPoint(positions.get(theta[j])).water) {
    			movePair next = new movePair(j, positions.get(theta[j]));
    			nextlist.add(next);
    			//next.printmovePair();
    			gotit = true;
    			break;
    		}
        			}
    			}
    		//System.out.println("we need to change the direction???");
    		theta[j] = random.nextInt(positions.size());
    		}
    	}
    	*/
    	/*if (prarr.size()>noutpost) {
			movePair mpr = new movePair(prarr.size()-1, new Pair(0,0));
			nextlist.add(mpr);
			//mpr.printmovePair();
		}*/
    	/*
    	//else {
    		ArrayList<Pair> positions = new ArrayList<Pair>();
    		positions = surround(prarr.get(prarr.size()-1));
    		boolean gotit=false;
    		while (!gotit) {
    			//Random random = new Random();
				//int theta = random.nextInt(positions.size());
				//System.out.println("we are here!!!");
    			if (theta[0]<positions.size()){
    			if (positions.get(theta[0]).x>=0 && positions.get(theta[0]).y>=0 && positions.get(theta[0]).x<size && positions.get(theta[0]).y<size) {
    		
    				if (!PairtoPoint(positions.get(theta[0])).water) {
    			movePair next = new movePair(prarr.size()-1, positions.get(theta[0]));
    			nextlist.add(next);
    			//next.printmovePair();
    			gotit = true;
    			break;
    		}
    			}
    			}
    		//System.out.println("outpost 0 need to change the direction???");
    		theta[0] = random.nextInt(positions.size());
    		}
    		
    	//}
    	
    	
    	return nextlist;
    	 */
    }
    
    
    static ArrayList<Pair> surround(Pair start) {
   // 	System.out.printf("start is (%d, %d)", start.x, start.y);
    	ArrayList<Pair> prlist = new ArrayList<Pair>();
    	for (int i=0; i<4; i++) {
    		Pair tmp0 = new Pair(start);
    		Pair tmp;
    		if (i==0) {
    			//if (start.x>0) {
    			tmp = new Pair(tmp0.x-1,tmp0.y);
    	//		if (!PairtoPoint(tmp).water)
    			prlist.add(tmp);
    		//	}
    		}
    		if (i==1) {
    			//if (start.x<size-1) {
    			tmp = new Pair(tmp0.x+1,tmp0.y);
    		//	if (!PairtoPoint(tmp).water)
    			prlist.add(tmp);
    			//}
    		}
    		if (i==2) {
    			//if (start.y>0) {
    			tmp = new Pair(tmp0.x, tmp0.y-1);
    			//if (!PairtoPoint(tmp).water)
    			prlist.add(tmp);
    			//}
    		}
    		if (i==3) {
    			//if (start.y<size-1) {
    			tmp = new Pair(tmp0.x, tmp0.y+1);
    			//if (!PairtoPoint(tmp).water)
    			prlist.add(tmp);
    			//}
    		}
    		
    	}
    	
    	return prlist;
    }
    
    static Point PairtoPoint(Pair pr) {
    	return grid[pr.x*size+pr.y];
    }
    static Pair PointtoPair(Point pt) {
    	return new Pair(pt.x, pt.y);
    }
}
