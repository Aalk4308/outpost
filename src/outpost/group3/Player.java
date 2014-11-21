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

  private Loc consCenter = new Loc(); //center of our consumer at the start
  private ArrayList<Loc> lastMoves = new ArrayList<Loc>();

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

    ArrayList<movePair> nextlist = new ArrayList<movePair>();
    ArrayList<Pair> prarr = new ArrayList<Pair>();
    prarr = king_outpostlist.get(this.id);

    
    lastMoves.clear();
    for (Pair p : prarr){
      lastMoves.add(new Loc(p.x,p.y));
    }

    if (!isInitialized) {
      board = new Board(id, gridin, r, L, W);
      isInitialized = true;
      //Pick a location where we'll form the consumer
      Loc firstOutpost = new Loc(prarr.get(0).x,prarr.get(0).y);
      double mindist = 1000000000.0;

      for (int i = r; i < size - r; i++){
        for (int j = r; j < size - r; j++){
          if (!board.isWaterCell(i,j)){
           if(!board.isWaterCell(i-r,j) &&
              !board.isWaterCell(i,j-r) &&
              !board.isWaterCell(i+r,j) &&
              !board.isWaterCell(i,j+r)){
              Loc temp = new Loc(i,j);
              if (firstOutpost.distance(temp) < mindist){
                mindist = consCenter.distance(temp);
                consCenter = temp;
              }
            }
          }
        }
      } 
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

    System.out.println(consCenter.x + "," + consCenter.y);

    if (prarr.size() >= 4){

      Loc north = new Loc(consCenter.x,consCenter.y + r);
      Loc south = new Loc(consCenter.x,consCenter.y - r);
      Loc east = new Loc(consCenter.x + r,consCenter.y);
      Loc west = new Loc(consCenter.x - r,consCenter.y);
      for (int i = 0; i < 4; i++){
        Loc curpos = new Loc(prarr.get(i).x,prarr.get(i).y);
        switch(i) {
          case 0: nextlist.add(stepTowardsFormation(i,curpos,north));
                  break;
          case 1: nextlist.add(stepTowardsFormation(i,curpos,south));
                  break;
          case 2: nextlist.add(stepTowardsFormation(i,curpos,east));
                  break;
          case 3: nextlist.add(stepTowardsFormation(i,curpos,west));
                  break;
        }
      }


      //Loop over every one of our other outposts
      for (int j =4; j<=prarr.size()-1; j++) {
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
    }
    else{
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
    }

    return nextlist;

  }

  /*
  TODO: Augment with shortest-path call when Andrew encorporates that
  public movePair stepTowardsFormation(int i, Loc curpos, Loc finalPost){
    //Call shortest path method to get move that will get us from curpos to finalPos
    return next;
  }
  */

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
}
