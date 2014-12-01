package outpost.group3;

import java.util.ArrayList;

import outpost.group3.Outpost;

public class ConsumerStrategy extends outpost.group3.Strategy {

  private int r;
  private int size = 100;
  enum State { ASSIGN, BUILD, ATTACK };
  static ArrayList<State> states = new ArrayList<State>();
  static boolean unassigned = true;

  //center of our consumer formation
  static ArrayList<Loc> consCenters = new ArrayList<Loc>(); 
  static ArrayList<Loc> targets = new ArrayList<Loc>();

  //These are the 4 outposts composing a consumer
  final int NORTH = 0;
  final int EAST = 1;
  final int SOUTH = 2;
  final int WEST = 3;
  final int CENTER = 4;


  ConsumerStrategy() {}

  ConsumerStrategy(int radius){
    this.r = radius;
    states.add(State.ASSIGN);
    consCenters.add(new Loc());
    targets.add(new Loc());
  }

  public void run(Board board, ArrayList<Outpost> outposts) {
    //System.out.println("Given a total of " + outposts.size() + " outposts to play with " );


    ArrayList<Outpost> consumer = new ArrayList<Outpost>(5);
    if (outposts.size() >= 5){
      if (unassigned){

        //Do this the first time we run consumer strategy on a move;
        unassigned = false;

        for (int i = 0; i < outposts.size() / 5; i++){
          for (int j = 5*i; j < 5 + 5*i; j++){
            consumer.add(outposts.get(j));
          }
          runConsumer(i,board, consumer);
          consumer.clear();
        }
      }
      else{
        //TODO: Fix issues occuring when outposts die
        for (int i = 0; i < outposts.size() / 5; i++){
          //Ensure the same outposts stay in a formation if they all still exist
          for (Outpost outpost : outposts){
            if (outpost.memory.get("formation") == i)
              consumer.add(outpost);
          }
          if (consumer.size() < 5){
            //Need to dole execess outposts to next consumer
            int components = consumer.size();
            for (Outpost outpost : outposts){
              if (outpost.memory.get("formation") == null){
                consumer.add(outpost);
                components++;
              }
              if (components == 5)
                break;
            }
          }
          if (consumer.size() < 5){
            //If formation still isn't complete, we don't have enough outposts
            for (Outpost outpost : consumer)
              outpost.setStrategy(null);
            consumer.clear();
          }
          else{
            //System.out.println("Running consumer " + i + " with " + consumer.size() + " outposts");
            runConsumer(i,board,consumer);
            //System.out.println("");
            consumer.clear();
          }
        }
      }


      for (Outpost outpost : outposts)
        if (!outpost.memory.containsKey("formation"))
          outpost.setStrategy(null);
    }
    else{
      //Less than 4 outposts, so don't make a consumer
      for (Outpost outpost : outposts)
        outpost.setStrategy(null);
    }
  }

  private void runConsumer(int formationNum, Board board, ArrayList<Outpost> outposts){

    //Check if any outposts haven't beenassigned a role
    ArrayList<String> filledRoles = new ArrayList<String>();
    for (Outpost outpost : outposts){
      if (outpost.memory.containsKey("role"))
        filledRoles.add((String)outpost.memory.get("role"));
    }
    if (filledRoles.size() < 5){
      //Go back to original assignment point to reform the formation
      states.set(formationNum,State.ASSIGN);
      for (Outpost outpost : outposts){
        if (outpost.memory.containsKey("role"))
          filledRoles.remove((String)outpost.memory.get("role"));
      }
      //Assign unassigned roles to new outposts
      for (Outpost outpost : outposts){
        if(!outpost.memory.containsKey("role") && !filledRoles.isEmpty()){
          outpost.memory.put("formation",formationNum);
          outpost.memory.put("role",filledRoles.remove(0));
        }
      }
    }

    switch (states.get(formationNum)){

      case ASSIGN:

        int numOutposts = outposts.size();

        Loc corner = new Loc(0,0);
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
                if (corner.distance(temp) < minDistance){
                  minDistance = consCenters.get(formationNum).distance(temp);
                  consCenters.set(formationNum,temp);
                }
              }
            }
          }
        } 

        //Put first four outposts in first outpost
        for (int i = 0; i < 5; i ++)
          outposts.get(i).memory.put("formation",formationNum);

        //Assign each outpost its position
        outposts.get(NORTH).memory.put("role","north");
        outposts.get(SOUTH).memory.put("role","south");
        outposts.get(WEST).memory.put("role","west");
        outposts.get(EAST).memory.put("role","east");
        outposts.get(CENTER).memory.put("role","center");

        //Set state to BUILD and fall through
        states.set(formationNum,State.BUILD);

      case BUILD: 
        buildMove(formationNum,outposts,board);
        break;

      case ATTACK: 
        attackMove(formationNum,outposts,board);
        break;

    }

  }

  //Build a formation centered at consCenter
  private void buildMove(int formationNum, ArrayList<Outpost> outposts, Board board){
    int numInPosition = 0;
    //System.out.println("Building formation " + formationNum);

    //set the target locations
    setFormationLocs(outposts,consCenters.get(formationNum),board);

    //Are we in formation?
    for (Outpost outpost : outposts){
      if (outpost.getCurrentLoc().equals(outpost.getTargetLoc()))
        numInPosition++;
    }

    if(numInPosition == 5){
      //We have fully formed a consumer, let's attack!
      states.set(formationNum,State.ATTACK);
      for (Outpost outpost : outposts)
        outpost.memory.put("expectedSpot",outpost.getExpectedLoc());
      attackMove(formationNum,outposts,board);
    }
  }

  //Update each outpost's memory of the expected location for the next turn
  private void updateExpectations(ArrayList<Outpost> outposts, String coord, int change){
    for (Outpost outpost : outposts){
      Loc newspot = new Loc(outpost.getCurrentLoc());
      if (coord.equals("y"))
        newspot.y += change;
      else
        newspot.x += change;
      outpost.memory.put("expectedSpot",newspot);
    }
  }

  //Can we rebuild without water being in the way?
  private boolean formationIsClear(int formationNum,Board board){
    Loc c = new Loc(consCenters.get(formationNum));
    if (board.getCell(c).isWater())
      return false;
    c.x++;
    if (board.getCell(c).isWater())
      return false;
    c.x-=2;
    if (board.getCell(c).isWater())
      return false;
    c.x++;
    c.y++;
    if (board.getCell(c).isWater())
      return false;
    c.y-=2;
    if (board.getCell(c).isWater())
      return false;
    c.y++;
    return true;
  }


  //Set the next move for our consumer formation to attack the closest enemy
  private void attackMove(int formationNum, ArrayList<Outpost> outposts, Board board){

    double enemyDist = 100;

    ArrayList<Loc> enemyOutposts = new ArrayList<Loc>();
    ArrayList<Outpost> outOfFormation = new ArrayList<Outpost>();

    for (Outpost outpost : outposts){
      Loc spot = (Loc)outpost.memory.get("expectedSpot");
      Loc realSpot = outpost.getExpectedLoc();
      if(spot.x != realSpot.x || spot.y != realSpot.y){
        outOfFormation.add(outpost);
      }
    }

    //If either coordinate of an outpost's location are incorrect
    //with respect to its formation assignment, and we can rebuild on land cells, we need to rebuild
    if (!outOfFormation.isEmpty() && formationIsClear(formationNum,board)){
      //System.out.println("rebuild");
      states.set(formationNum,State.BUILD);
      buildMove(formationNum,outposts,board);
    }
    else{
      //Find closest enemy
      for (int i = 0; i < 4; i ++){
        enemyOutposts = board.theirOutposts(i);
        if (enemyOutposts == board.ourOutposts())
          continue;
        for (Loc outpost : enemyOutposts){
          double tempDist = outpost.distance(consCenters.get(formationNum));
          if (tempDist < enemyDist && !targets.contains(outpost)){
            enemyDist = tempDist;
            targets.set(formationNum,outpost);
          }
        }
      }
      //System.out.println("Formation " + formationNum + " going for enemy " + targets.get(formationNum));

      ArrayList<Loc> path = board.findPath(consCenters.get(formationNum), targets.get(formationNum));
      Loc newCenter = new Loc();
      if (path == null || path.size() == 0 || path.size() == 1) {
        newCenter.x = consCenters.get(formationNum).x;
        newCenter.y = consCenters.get(formationNum).y;
      } else {
        newCenter.x = path.get(1).x;
        newCenter.y = path.get(1).y;
      }

      //Set new formation center
      consCenters.set(formationNum, newCenter);

      //System.out.println("Moving center to " + consCenters.get(formationNum));
      setFormationLocs(outposts,consCenters.get(formationNum),board);
    }
  }

  //Given the center of a formation, assign the consumer outposts to their associated positions
  private void setFormationLocs(ArrayList<Outpost> outposts, Loc formationCenter, Board board){
    for (Outpost outpost : outposts){
      if(outpost.memory.get("role").equals("north"))
        outpost.setTargetLoc(board.nearestLand(new Loc(formationCenter.x,formationCenter.y + 1)));
      else if(outpost.memory.get("role").equals("south"))
        outpost.setTargetLoc(board.nearestLand(new Loc(formationCenter.x,formationCenter.y - 1)));
      else if(outpost.memory.get("role").equals("east"))
        outpost.setTargetLoc(board.nearestLand(new Loc(formationCenter.x + 1,formationCenter.y)));
      else if(outpost.memory.get("role").equals("west"))
        outpost.setTargetLoc(board.nearestLand(new Loc(formationCenter.x - 1,formationCenter.y)));
      else if(outpost.memory.get("role").equals("center"))
        outpost.setTargetLoc(board.nearestLand(new Loc(formationCenter.x,formationCenter.y)));
      outpost.memory.put("expectedSpot",outpost.getTargetLoc());
      //System.out.println("Outpost " + (String) outpost.memory.get("role") + " expects to be at " + outpost.getTargetLoc());
    }
  }


}
