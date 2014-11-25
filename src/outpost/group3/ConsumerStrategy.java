package outpost.group3;

import java.util.ArrayList;

import outpost.group3.Outpost;

public class ConsumerStrategy extends outpost.group3.Strategy {

  private int r;
  private int size = 100;
  private int numSoldiers = 0;
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

  public void run(Board board, ArrayList<Outpost> outposts) {
	  /*
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

          consumer[NORTH] = new Loc(consCenter.x,consCenter.y + r);
          consumer[SOUTH] = new Loc(consCenter.x,consCenter.y - r);
          consumer[EAST] = new Loc(consCenter.x + r,consCenter.y);
          consumer[WEST] = new Loc(consCenter.x - r,consCenter.y);

          //Are we in formation?
          for (Loc outpost : outposts)
            for (int i = 0; i < 4; i++)
              if (consumer[i].equals(outpost))
                numInPosition++;
          System.out.println(numInPosition);
          //We have fully formed a consumer
          if(numInPosition >= 4){
            state = State.ATTACK;
            attackMove(targets,board);
          }
          else{
            for (int i = 0; i < 4; i++)
              targets.add(consumer[i]);
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
    */
  }

  //Set the next move for our consumer formation to attack the closest enemy
  private void attackMove(ArrayList<Loc> targets, Board board){

    //TODO: Make it so I create a new consumer every 4 outposts

    //TODO: Fix formation issues

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

    //Ideal target positions for our formation
    Loc[] idealTargets = formationAround(centerTarget);

    //Determine which member of our ideal formation is closest to the target
    Loc closest = new Loc();

    int soldier = 0; 
    int secondarySold = 0; 
    enemyDist = 100;
    for (int i = 0; i < 4; i++){
      System.out.println("Distance for outpost " + i + " is " + outposts.get(i).distance(centerTarget));
      if (outposts.get(i).distance(centerTarget) < enemyDist){
        System.out.println("Outpost " + i + " is closer at location " + outposts.get(i));
        closest = outposts.get(i);
        enemyDist = outposts.get(i).distance(centerTarget);
        secondarySold = soldier;
        soldier = i;
      }
    }


    Loc nextMove = new Loc();
    //TODO: If the soldier direction would put us on water, use secondary soldier direction
    switch(soldier){
      case NORTH: nextMove = board.nearestLand(new Loc(centerTarget.x,centerTarget.y + r));
                  consCenter = new Loc(consCenter.x, consCenter.y + 1);
                  System.out.println("We're moving north");
                  for (int i = 0; i < 4; i ++){
                    consumer[i] = board.nearestLand(new Loc(outposts.get(i).x,outposts.get(i).y + 1));
                  }
                  break;
      case SOUTH: nextMove = board.nearestLand(new Loc(centerTarget.x,centerTarget.y - r));
                  consCenter = new Loc(consCenter.x, consCenter.y - 1);
                  for (int i = 0; i < 4; i ++){
                    consumer[i] = board.nearestLand(new Loc(outposts.get(i).x,outposts.get(i).y - 1));
                  }
                  System.out.println("We're moving south");
                  break;
      case EAST: nextMove = board.nearestLand(new Loc(centerTarget.x + r,centerTarget.y));
                  consCenter = new Loc(consCenter.x + 1, consCenter.y );
                  for (int i = 0; i < 4; i ++){
                    consumer[i] = board.nearestLand(new Loc(outposts.get(i).x + 1,outposts.get(i).y));
                  }
                  System.out.println("We're moving east");
                  break;
      case WEST: nextMove = board.nearestLand(new Loc(centerTarget.x - r,centerTarget.y));
                  consCenter = new Loc(consCenter.x - 1, consCenter.y);
                  for (int i = 0; i < 4; i ++){
                    consumer[i] = board.nearestLand(new Loc(outposts.get(i).x - 1,outposts.get(i).y));
                  }
                  System.out.println("We're moving west");
                  break;
    }

    ArrayList<Loc> path = board.findPath(closest,nextMove);


    System.out.println("New positions are ");

    for (int i = 0; i < 4; i ++){
    System.out.println(consumer[i]);
      targets.add(consumer[i]);
    }
  }

  private Loc[] formationAround(Loc t){
    Loc[] spots = new Loc[4];
    spots[NORTH] = new Loc(t.x,t.y + r);
    spots[SOUTH] = new Loc(t.x,t.y - r);
    spots[EAST] = new Loc(t.x + r,t.y);
    spots[WEST] = new Loc(t.x - r,t.y);

    return spots;
  }

}
