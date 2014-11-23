package outpost.group3;

import java.util.ArrayList;

public class ConsumerStrategy extends outpost.group3.Strategy {

  private int r;
  private int size = 100;
  enum State { BUILD, ATTACK };
  static State state = State.BUILD;

  //These will represent our current outpost locations
  ArrayList<Loc> outposts = new ArrayList<Loc>();

  //center of our consumer at the start
  Loc consCenter = new Loc(); 

  //These represent the positions of the 4 outposts composing a consumer
  Loc[] consumer = new Loc[4];
  final int NORTH = 0;
  final int EAST = 1;
  final int SOUTH = 2;
  final int WEST = 3;


  ConsumerStrategy() {}

  ConsumerStrategy(int radius){
    this.r = radius;
  }

  public ArrayList<Loc> run(Board board) {
    ArrayList<Loc> targets = new ArrayList<Loc>();

    outposts = board.ourOutposts();
    int numOutposts = outposts.size();


    Loc firstOutpost = new Loc(0,0);
    double minDistance = 1000000000.0;

    //Pick a location where we'll form the consumer that's close to our corner
    for (int i = r; i < size - r; i++){
      for (int j = r; j < size - r; j++){
        if (!board.getCell(i,j).isWater()){
          if(!board.getCell(i-r,j).isWater() &&
              !board.getCell(i,j-r).isWater() &&
              !board.getCell(i+r,j).isWater() &&
              !board.getCell(i,j+r).isWater()){
            Loc temp = new Loc(i,j);
            if (firstOutpost.distance(temp) < minDistance){
              minDistance = consCenter.distance(temp);
              consCenter = temp;
            }
          }
        }
      }
    } 

    if (outposts.size() >= 4){
      switch (state){
        case BUILD: 
          int numInPosition = 0;

          Loc[NORTH] = new Loc(consCenter.x,consCenter.y + r);
          Loc[SOUTH] = new Loc(consCenter.x,consCenter.y - r);
          Loc[EAST] = new Loc(consCenter.x + r,consCenter.y);
          Loc[WEST] = new Loc(consCenter.x - r,consCenter.y);

          //Are we in formation?
          for (Loc outpost : outposts)
            for (int i = 0; i < 4; i++)
              if (Loc[i].equals(outpost))
                numInPosition++;
          //We have fully formed a consumer
          if(numInPosition >= 4){
            state = State.ATTACK;
            attackMove(targets,board);
          }
          else{
            for (int i = 0; i < 4; i++)
              targets.add(Loc[i]);
          }

          break;

        case ATTACK: 
          attackMove(targets,board);
          break;

      }

      for (int i = 4; i < outposts.size(); i++){
        targets.add(outposts.get(i));
      }

    }
    else{
      double sideLength = Math.min(65, Math.sqrt(Math.pow(2*board.r*numOutposts * 1, 2) / 2));

      for (int outpostId = 0; outpostId < numOutposts; outpostId++) {
        int xTarget = (int) Math.round((outpostId + 1) * (sideLength / (numOutposts + 1)));
        int yTarget = (int) Math.round((numOutposts - outpostId) * (sideLength / (numOutposts + 1)));

        targets.add(board.nearestLand(new Loc(xTarget, yTarget)));
      }
    }
    return targets;
  }

  //Set the next move for our consumer formation to attack the closest enemy
  private void attackMove(ArrayList<Loc> targets, Board board){

    double enemyDist = 100;

    Loc centerTarget = new Loc();
    ArrayList<Loc> enemyOutposts = new ArrayList<Loc>();

    //Find closest enemy
    for (int i = 0; i < 4; i ++){
      enemyOutposts = board.theirOutposts(i);
      if (enemyOutposts == outposts)
        continue;
      for (Loc outpost : enemyOutposts){
        double tempDist = outpost.distance(consCenter);
        if (tempDist < enemyDist){
          enemyDist = tempDist;
          centerTarget = outpost;
        }
      }
    }
    System.out.println("Moving to target " + centerTarget);

    //Determine which member of our formation is closest
    Loc closest = new Loc();

    north = board.nearestLand(new Loc(centerTarget.x,centerTarget.y + r));
    if (north.distance(centerTarget) < enemyDist){
        enemyDist = north.distance(centerTarget);
        closest = north;
    }
    south = board.nearestLand(new Loc(centerTarget.x,centerTarget.y - r));
    if (north.distance(centerTarget) < enemyDist){
        enemyDist = north.distance(centerTarget);
        closest = north;
    }
    east = board.nearestLand(new Loc(centerTarget.x + r,centerTarget.y));
    west = board.nearestLand(new Loc(centerTarget.x - r,centerTarget.y));

    targets.add(north);
    targets.add(south);
    targets.add(east);
    targets.add(west);
  }

}
