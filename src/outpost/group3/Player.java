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
  }

  public void init() {
    for (int i=0; i<100; i++) {
      theta[i]=random.nextInt(4);
    }
  }

  public int delete(ArrayList<ArrayList<Pair>> king_outpostlist, Point[] gridin) {
    //System.out.printf("haha, we are trying to delete a outpost for player %d\n", this.id);
    int del = random.nextInt(king_outpostlist.get(id).size());
    return del;
  }

  public ArrayList<movePair> move(ArrayList<ArrayList<Pair>> king_outpostlist, Point[] gridin, int r, int L, int W, int T){
    if (!isInitialized) {
      board = new Board(id, gridin, r, L, W);
      isInitialized = true;
    }

    board.update(king_outpostlist);

    //DEBUG
    board.dump(3);

    counter = counter+1;
    if (counter % 10 == 0) {
      for (int i=0; i<100; i++) {
        theta[i]=random.nextInt(4);
      }
    }

    ArrayList<movePair> nextlist = new ArrayList<movePair>();
    ArrayList<Pair> prarr = new ArrayList<Pair>();
    prarr = king_outpostlist.get(this.id);

    //Loop over every one of our outposts
    for (int j =0; j<=prarr.size()-1; j++) {
      ArrayList<Pair> positions = new ArrayList<Pair>();
      positions = surround(prarr.get(j));//Get 4 orthogonal cells adjacent to this outpost
      boolean gotit=false;
      while (!gotit) {
        if (theta[j]<positions.size()){
          //Check if we're in field
          int x = positions.get(theta[j]).x;
          int y = positions.get(theta[j]).y;
          if (x >=0 && y >=0 && x<size && y<size) {
            //Make sure we don't move onto water
            if (!board.isWaterCell(x,y)) {
              movePair next = new movePair(j, positions.get(theta[j]));
              nextlist.add(next);
              //next.printmovePair();
              gotit = true;
              break;
            }
          }
        }
        theta[j] = random.nextInt(positions.size());
      }
    }

    return nextlist;

}


static ArrayList<Pair> surround(Pair start) {
  ArrayList<Pair> prlist = new ArrayList<Pair>();
  for (int i=0; i<4; i++) {
    Pair tmp0 = new Pair(start);
    Pair tmp = new Pair();
    if (i==0) {
      tmp = new Pair(tmp0.x-1,tmp0.y);
    }
    if (i==1) {
      tmp = new Pair(tmp0.x+1,tmp0.y);
    }
    if (i==2) {
      tmp = new Pair(tmp0.x, tmp0.y-1);
    }
    if (i==3) {
      tmp = new Pair(tmp0.x, tmp0.y+1);
    }
    prlist.add(tmp);

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
