package outpost.group3;

import java.util.ArrayList;

import outpost.group3.Outpost;

public class ConsumerStrategy extends outpost.group3.Strategy {

  private int r;
  private int size = 100;
  private int numSoldiers = 0;
  enum State { ASSIGN, BUILD, ATTACK };
  static State state = State.ASSIGN;

  //center of our consumer at the start
  static Loc consCenter = new Loc(); 

  //These are the 4 outposts composing a consumer
  Outpost[] consumer = new Outpost[4];
  final int NORTH = 0;
  final int EAST = 1;
  final int SOUTH = 2;
  final int WEST = 3;


  ConsumerStrategy() {}

  ConsumerStrategy(int radius){
    this.r = radius;
  }

  public void run(Board board, ArrayList<Outpost> outposts) {

    if (outposts.size() >= 4){

      //TODO: Strategically reassign the outposts that aren't assigned
      //and add a new RELOCATE state
      ArrayList<String> filledRoles = new ArrayList<String>();
      for (Outpost outpost : outposts){
        if (outpost.memory.containsKey("role"))
          filledRoles.add((String)outpost.memory.get("role"));
      }
      if (filledRoles.size() < 4){
        state = State.ASSIGN;
        for (Outpost outpost : outposts){
          if (outpost.memory.containsKey("role"))
            filledRoles.remove((String)outpost.memory.get("role"));
        }
        for (Outpost outpost : outposts){
          if(!outpost.memory.containsKey("role") && !filledRoles.isEmpty())
            outpost.memory.put("role",filledRoles.remove(0));
        }
      }

      switch (state){

        case ASSIGN:

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

          //Put first four outposts in first outpost
          for (int i = 0; i < 4; i ++)
            outposts.get(i).memory.put("formation",1);

          //Assign each outpost its position
          outposts.get(NORTH).memory.put("role","north");
          outposts.get(SOUTH).memory.put("role","south");
          outposts.get(WEST).memory.put("role","west");
          outposts.get(EAST).memory.put("role","east");

          //Set state to BUILD and fall through
          state = State.BUILD;

        case BUILD: 
          buildMove(outposts,board);
          break;

        case ATTACK: 
          attackMove(outposts,board);
          break;

      }

    }
    else{
      for (Outpost outpost : outposts)
        outpost.setStrategy(null);
    }
  }

  //Given the center of a formation, assign the consumer outposts to their associated positions
  private void setFormationLocs(ArrayList<Outpost> outposts, Loc formationCenter, Board board){
    for (Outpost outpost : outposts){
      if(outpost.memory.get("role").equals("north"))
        outpost.setTargetLoc(board.nearestLand(new Loc(formationCenter.x,formationCenter.y + r)));
      else if(outpost.memory.get("role").equals("south"))
        outpost.setTargetLoc(board.nearestLand(new Loc(formationCenter.x,formationCenter.y - r)));
      else if(outpost.memory.get("role").equals("east"))
        outpost.setTargetLoc(board.nearestLand(new Loc(formationCenter.x + r,formationCenter.y)));
      else if(outpost.memory.get("role").equals("west"))
        outpost.setTargetLoc(board.nearestLand(new Loc(formationCenter.x - r,formationCenter.y)));
    }
  }

  private void buildMove(ArrayList<Outpost> outposts, Board board){
    int numInPosition = 0;

    setFormationLocs(outposts,consCenter,board);

    //Are we in formation?
    for (Outpost outpost : outposts){
      if (outpost.getCurrentLoc().equals(outpost.getTargetLoc()))
        numInPosition++;
    }

    //We have fully formed a consumer
    if(numInPosition >= 4){
      state = State.ATTACK;
      for (Outpost outpost : outposts)
        outpost.memory.put("expectedSpot",outpost.getExpectedLoc());
      attackMove(outposts,board);
    }
  }

  //Set the next move for our consumer formation to attack the closest enemy
  private void attackMove(ArrayList<Outpost> outposts, Board board){

    //TODO: Make it so I create a new consumer every 4 outposts

    //TODO: Fix formation issues

    double enemyDist = 100;

    Loc centerTarget = new Loc();
    ArrayList<Loc> enemyOutposts = new ArrayList<Loc>();
    ArrayList<Outpost> outOfFormation = new ArrayList<Outpost>();

    for (Outpost outpost : outposts){
      Loc spot = (Loc)outpost.memory.get("expectedSpot");
      Loc realSpot = outpost.getExpectedLoc();
      //System.out.println("Oupost " + outpost.memory.get("role") + " should be at " + spot + " but is at " + realSpot);
      if(spot.x != realSpot.x && spot.y != realSpot.y){
        outOfFormation.add(outpost);
      }
    }

    if (!outOfFormation.isEmpty()){
      //System.out.println("Out of formation is not empty");
      //If the current state of the formation is not what was expected after the last move
      //keep all correct soldiers in their spots and move all others so they're on the correct
      //axis of their formation spot
      state = State.BUILD;
      buildMove(outposts,board);
    }
    else{
      //Find closest enemy
      for (int i = 0; i < 4; i ++){
        enemyOutposts = board.theirOutposts(i);
        if (enemyOutposts == board.ourOutposts())
          continue;
        for (Loc outpost : enemyOutposts){
          double tempDist = outpost.distance(consCenter);
          if (tempDist < enemyDist){
            enemyDist = tempDist;
            centerTarget = outpost;
          }
        }
      }

      //Determine which member of our ideal formation is closest to the target
      String closest = "";
      String secondClosest = ""; 
      enemyDist = 100;
      for (Outpost outpost : outposts){
        if (outpost.getCurrentLoc().distance(centerTarget) < enemyDist){
          secondClosest = closest;
          closest = (String) outpost.memory.get("role");
          enemyDist = outpost.getCurrentLoc().distance(centerTarget);
        }
      }

      Loc nextMoveTarget = new Loc();
      //TODO: If the soldier direction would put us on water, use secondary soldier direction

      if(closest.equals("north")){
        nextMoveTarget = board.nearestLand(new Loc(centerTarget.x,centerTarget.y + r));
        consCenter = new Loc(consCenter.x, consCenter.y + 1);
        for (Outpost outpost : outposts){
          Loc newspot = new Loc(outpost.getCurrentLoc());
          newspot.y++;
          outpost.memory.put("expectedSpot",newspot);
        }
      }
      else if(closest.equals("south")){
        nextMoveTarget = board.nearestLand(new Loc(centerTarget.x,centerTarget.y - r));
        consCenter = new Loc(consCenter.x, consCenter.y - 1);
        for (Outpost outpost : outposts){
          Loc newspot = new Loc(outpost.getCurrentLoc());
          newspot.y--;
          outpost.memory.put("expectedSpot",newspot);
        }
      }
      else if(closest.equals("east")){
        nextMoveTarget = board.nearestLand(new Loc(centerTarget.x + r,centerTarget.y));
        consCenter = new Loc(consCenter.x + 1, consCenter.y);
        for (Outpost outpost : outposts){
          Loc newspot = new Loc(outpost.getCurrentLoc());
          newspot.x++;
          outpost.memory.put("expectedSpot",newspot);
        }
      }
      else if(closest.equals("west")){
        nextMoveTarget = board.nearestLand(new Loc(centerTarget.x - r,centerTarget.y));
        consCenter = new Loc(consCenter.x - 1, consCenter.y);
        for (Outpost outpost : outposts){
          Loc newspot = new Loc(outpost.getCurrentLoc());
          newspot.x--;
          outpost.memory.put("expectedSpot",newspot);
        }
      }

      setFormationLocs(outposts,consCenter,board);
    }

  }

}
