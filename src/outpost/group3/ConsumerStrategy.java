package outpost.group3;

import java.util.ArrayList;

import outpost.group3.Outpost;

public class ConsumerStrategy extends outpost.group3.Strategy {

  private int size = 100;
  enum State { ASSIGN, BUILD, ATTACK };
  static boolean unassigned = true;

  //These are the 4 outposts composing a consumer
  final int NORTH = 0;
  final int EAST = 1;
  final int SOUTH = 2;
  final int WEST = 3;
  final int CENTER = 4;

  private class Consumer {
    public ArrayList<Outpost> members;
    public Loc targetCenter;
    public Loc myCenter;
    public State state;

    Consumer(){
      members = new ArrayList<Outpost>();
      targetCenter = null;
      myCenter = null;
      state = State.ASSIGN;
    }

    public boolean isConnected(){
      return true;
    }
  }

  public static ArrayList<Consumer> consumers = new ArrayList<Consumer>();


  private boolean isInConsumer(Outpost outpost){
    for (Consumer consumer : consumers){
      if (consumer.members.contains(outpost))
        return true;
    }
    return false;
  }


  ConsumerStrategy() {}

  public void run(Board board, ArrayList<Outpost> outposts) {
    //System.out.println("Given a total of " + outposts.size() + " outposts to play with " );


    if (outposts.size() >= 5){
      //System.out.println("At start of run method, we have " + consumers.size() + " consumers");
      for (Consumer consumer : consumers) {
        ArrayList<Outpost> originalMembers = (ArrayList<Outpost>) consumer.members.clone();
        for (Outpost member : originalMembers) {
          if (!outposts.contains(member))
            consumer.members.remove(member);  // Probably need to define equals method on outpost
        }
      }

      // Get the outposts that can be added to an existing or new consumer
      ArrayList<Outpost> availableOutposts = new ArrayList<Outpost>();
      for (Outpost outpost : outposts) {
        if (!isInConsumer(outpost))
          availableOutposts.add(outpost);
      }

      //System.out.println("We have " + availableOutposts.size() + " available outposts");

      // Fill up existing consumers that have too few members, if possible
      for (Consumer consumer : consumers) {
        while (consumer.members.size() > 0 && consumer.members.size() < 5 && availableOutposts.size() > 0)
          consumer.members.add(availableOutposts.remove(availableOutposts.size() - 1));
      }

      // Assign remaining outposts to new consumers and add fully formed consumers to global list
      while(availableOutposts.size() > 0){
        Consumer consumer = new Consumer();
        while (consumer.members.size() >= 0 && consumer.members.size() < 5 && availableOutposts.size() > 0){
          //System.out.println("Removing outpost from available list");
          consumer.members.add(availableOutposts.remove(0));
        }
        consumers.add(consumer);
      }

      //System.out.println("After forming consumers, we have " + consumers.size() + " and " + availableOutposts.size() + " extra outposts");

      // For any consumer that still doesn't have enough members, destroy it
      ArrayList<Consumer> builtThisTurn = (ArrayList<Consumer>)consumers.clone();
      for (Consumer consumer : builtThisTurn) {
        if (consumer.members.size() < 5) {
          for (Outpost outpost : consumer.members)
            outpost.setStrategy(null);
          consumers.remove(consumer);   // Be careful about doing this inside a loop
        }
      }

      //System.out.println("After destroying some, we have " + consumers.size());

      //Run strategy for each consumer
      for (Consumer consumer : consumers) {
        runConsumer(consumer,board);
      }
    }
    else{
      for (Outpost outpost : outposts)
        outpost.setStrategy(null);
    }
  }

  private void runConsumer(Consumer consumer, Board board){
    //System.out.println("Running consumer with state " + consumer.state + ", center " + consumer.myCenter + ", and target " + consumer.targetCenter);

    //Check if any outposts haven't beenassigned a role
    ArrayList<String> filledRoles = new ArrayList<String>();
    for (Outpost outpost : consumer.members){
      if (outpost.memory.containsKey("role"))
        filledRoles.add((String)outpost.memory.get("role"));
    }
    if (filledRoles.size() < 5){
      //Go back to original assignment point to reform the formation
      consumer.state = State.ASSIGN;
      for (Outpost outpost : consumer.members){
        if (outpost.memory.containsKey("role"))
          filledRoles.remove((String)outpost.memory.get("role"));
      }
      //Assign unassigned roles to new outposts
      for (Outpost outpost : consumer.members){
        if(!outpost.memory.containsKey("role") && !filledRoles.isEmpty()){
          outpost.memory.put("role",filledRoles.remove(0));
        }
      }
    }

    switch (consumer.state){

      case ASSIGN:

        int numOutposts = consumer.members.size();

        //Pick a location where we'll form the consumer that's close to all outposts
        double avg_x = 0.0;
        double avg_y = 0.0;
        for (Outpost outpost : consumer.members){
          Loc l = outpost.getCurrentLoc();
          avg_x += l.x;
          avg_y += l.y;
        }
        avg_x /= consumer.members.size();
        avg_y /= consumer.members.size();
        consumer.myCenter = board.nearestLand(new Loc((int)avg_x,(int)avg_y));
        //System.out.println("Average location is " + consumer.myCenter);
        /*
           for (int i = 1; i < size - 1; i++){
           for (int j = 1; j < size - 1; j++){
           if (!board.getCell(i,j).isWater()){
           if(!board.getCell(i-1,j).isWater() &&
           !board.getCell(i,j-1).isWater() &&
           !board.getCell(i+1,j).isWater() &&
           !board.getCell(i,j+1).isWater()){
           Loc temp = new Loc(i,j);
           if (corner.distance(temp) < minDistance){
           minDistance = consCenters.get(formationNum).distance(temp);
           consCenters.set(formationNum,temp);
           }
           }
           }
           }
           } 
         */

        //Assign each outpost its position
        consumer.members.get(NORTH).memory.put("role","north");
        consumer.members.get(SOUTH).memory.put("role","south");
        consumer.members.get(WEST).memory.put("role","west");
        consumer.members.get(EAST).memory.put("role","east");
        consumer.members.get(CENTER).memory.put("role","center");

        //Set state to BUILD and fall through
        consumer.state = State.BUILD;
        //System.out.println("Just set consumer state to " + consumer.state);

      case BUILD: 
        buildMove(consumer,board);
        break;

      case ATTACK: 
        attackMove(consumer,board);
        break;

    }

  }

  //Build a formation centered at consCenter
  private void buildMove(Consumer consumer, Board board){
    int numInPosition = 0;
    //System.out.println("Building formation " + formationNum);

    //set the target locations
    setFormationLocs(consumer, board);

    //Are we in formation?
    for (Outpost outpost : consumer.members){
      if (outpost.getCurrentLoc().equals(outpost.getTargetLoc()))
        numInPosition++;
    }

    if(numInPosition == 5){
      //We have fully formed a consumer, let's attack!
      consumer.state = State.ATTACK;
      for (Outpost outpost : consumer.members)
        outpost.memory.put("expectedSpot",outpost.getExpectedLoc());
      attackMove(consumer,board);
    }
  }

  /*
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
  */

  //Can we rebuild without water being in the way?
  private boolean formationIsClear(Consumer consumer,Board board){
    Loc c = consumer.myCenter;
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
  private void attackMove(Consumer consumer, Board board){

    double enemyDist = 100;

    ArrayList<Loc> enemyOutposts = new ArrayList<Loc>();
    ArrayList<Outpost> outOfFormation = new ArrayList<Outpost>();

    for (Outpost outpost : consumer.members){
      Loc spot = (Loc)outpost.memory.get("expectedSpot");
      Loc realSpot = outpost.getExpectedLoc();
      if(spot.x != realSpot.x || spot.y != realSpot.y){
        outOfFormation.add(outpost);
      }
    }

    //If either coordinate of an outpost's location are incorrect
    //with respect to its formation assignment, and we can rebuild on land cells, we need to rebuild
    if (!outOfFormation.isEmpty() && formationIsClear(consumer,board)){
      //System.out.println("rebuild");
      consumer.state = State.BUILD;
      buildMove(consumer,board);
    }
    else{
      //Find closest enemy
      for (int i = 0; i < 4; i ++){
        enemyOutposts = board.theirOutposts(i);
        if (enemyOutposts == board.ourOutposts())
          continue;
        for (Loc outpost : enemyOutposts){
          double tempDist = outpost.distance(consumer.myCenter);
          boolean targeted = false;
          for (Consumer c : consumers){
            if (c.targetCenter != null && c.targetCenter.equals(outpost))
              targeted = true;
          }
          if (tempDist < enemyDist && !targeted){
            enemyDist = tempDist;
            consumer.targetCenter = outpost;
          }
        }
      }
      //System.out.println("Formation " + formationNum + " going for enemy " + targets.get(formationNum));

      ArrayList<Loc> path = board.findPath(consumer.myCenter, consumer.targetCenter);
      Loc newCenter = new Loc();
      if (path == null || path.size() == 0 || path.size() == 1) {
        newCenter.x = consumer.myCenter.x;
        newCenter.y = consumer.myCenter.y;
      } else {
        newCenter.x = path.get(1).x;
        newCenter.y = path.get(1).y;
      }

      //Set new formation center
      consumer.myCenter = newCenter;

      //System.out.println("Moving center to " + consCenters.get(formationNum));
      setFormationLocs(consumer,board);
    }
  }

  //Given the center of a formation, assign the consumer outposts to their associated positions
  private void setFormationLocs(Consumer consumer, Board board){
    Loc formationCenter = consumer.myCenter;
    for (Outpost outpost : consumer.members){
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
