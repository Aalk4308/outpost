package outpost.group3;

import java.util.ArrayList;

public class ConsumerStrategy extends outpost.group3.Strategy {

  private int r;
  private int size = 100;
  enum State { BUILD, ATTACK };
  static State state = State.BUILD;
  
  

  ConsumerStrategy() {}

  ConsumerStrategy(int radius){
    this.r = radius;
  }

  public ArrayList<Loc> run(Board board) {
    ArrayList<Loc> targets = new ArrayList<Loc>();

    ArrayList<Loc> outposts = board.ourOutposts();
    int numOutposts = outposts.size();

    Loc consCenter = new Loc(); //center of our consumer at the start
    Loc north = new Loc();
    Loc east = new Loc();
    Loc south = new Loc();
    Loc west = new Loc();

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

          for (Loc outpost : outposts){
            north = new Loc(consCenter.x,consCenter.y + r);
            if (north.equals(outpost))
              numInPosition++;
            south = new Loc(consCenter.x,consCenter.y - r);
            if (south.equals(outpost))
              numInPosition++;
            east = new Loc(consCenter.x + r,consCenter.y);
            if (east.equals(outpost))
              numInPosition++;
            west = new Loc(consCenter.x - r,consCenter.y);
            if (west.equals(outpost))
              numInPosition++;
          }
          System.out.println(numInPosition);

          //We have fully formed a consumer
          if(numInPosition >= 4){
            state = State.ATTACK;
          }
          break;

        case ATTACK: 

          //Find closest enemy

          double enemyDist = 100;

          Loc centerTarget = new Loc();
          ArrayList<Loc> enemyOutposts = new ArrayList<Loc>();

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

          north = board.nearestLand(new Loc(centerTarget.x,centerTarget.y + r));
          south = board.nearestLand(new Loc(centerTarget.x,centerTarget.y - r));
          east = board.nearestLand(new Loc(centerTarget.x + r,centerTarget.y));
          west = board.nearestLand(new Loc(centerTarget.x - r,centerTarget.y));

          break;

        
      }

      targets.add(north);
      targets.add(south);
      targets.add(east);
      targets.add(west);

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

}
