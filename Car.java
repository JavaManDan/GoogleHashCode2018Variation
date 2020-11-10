import java.util.ArrayList;
import java.util.HashMap;
/**
 * The Car class is the workhorse of the simulation does the computing and completes
 * rides with the assitance of the World class and supllies info to Scoring class 
 * using the Ride class for info.
 * 
 * Version 7
 * 
 * Daniel Knight
 */
public class Car {
    //The id for the ride (current) and dynamic
    private int ID;
    //Carrying a person or not , is the ride free?
    private boolean passenger;
    //The start location where the car needs to be X
    private int  startLocX;
    //The start location where the car needs to be Y
    private int  startLocY;
    //The end location where the car needs to be X
    private int  endLocX;
    //The end location where the car needs to be Y
    private int  endLocY;
    //The current location where the car is on X axis
    private int  currLocX;
    //The current location where the car is on Y axis
    private int  currLocY;
    //The latest finish
    private int latestFinish;
    //The current location where the car is on Y axis
    private int  turnPickup;
    //String list of rides and the order they need to be in
    private String ridesCompleted;
    //Curr ride
    private Ride currRide;
    //The cars ID number
    private int carNum;
    //Spaces moved
    private int spacesMoved;
    //Will tehy recieve a bonus for the current ride
    private boolean bonus;
    //Has the car moved yet
    private boolean hasNotMoved;
    //Are they at the start location?
    private boolean startLoc;
    //Is this car in use
    private boolean inUse;
    //The ride allocation this car is assigned (stage 1 usage)
    private String rideAllocation;
    //Number of rides this car must complete
    private int numberOfRides;
    //The list of ride Indexs where this car can get the ifnormation that it requires for the project 
    private ArrayList<Integer> rideIndexs;
    //The curr index value 
    int currIndex = 0;

    private boolean inRide;
    //the curr car pool of ride indexs
    private ArrayList<Integer> currCarrPoolRides;
    //the curr rides
    private ArrayList<Ride> currRides;
    //for bonus of the 4 rides
    private ArrayList<Boolean> multiBonus;
    //the multi passenger pick up
    private ArrayList<Boolean> multiPassenger;
    //the score of the 4 rides      
    private ArrayList<Integer> multiSpacesMoved;
    //what rides are complete uo to 4 so far 
    private ArrayList<Boolean> multiComplete;
    //multi what index we on
    private int currentRideIndex;
    //the list of order to drop off rides
    private ArrayList<Integer> dropOffOrder;
    //start order
    private ArrayList<Integer> pickUpOrder;

    //ride  umber completeed 
    private int ridesTotal = 0;

    /**
     * The first cosntructor for the Car where the supplied info is given
     * @carNum             the ID for this car
     * @rideNum            the ride number
     * @startLocX          the start x location
     * @startLocY          the start y location
     * @endLocX the        end x location
     * @endLocY the        end y location
     * @currLocX           the curr x location
     * @currLocY           the curr y location
     * @turnPickup         the turn ride should start
     * @latestFinish       the latest finish 
     * @inUse              true or false is being used
     */

    public Car(int carNum ,int ID , int startLocX, int startLocY, int endLocX, int endLocY, int currLocX, int currLocY, int turnPickup, boolean inUse, int latestFinish, String pickUpFormat) {

        //assume first ride is the first index
        currentRideIndex = 0;
        //ID, passenger , start x , start y , end x, end y , curr x, curr y, turn pickup (earliest start), car in  use
        this.carNum = carNum;
        this.passenger = passenger;
        this.startLocX = startLocX;
        this.startLocY = startLocY;
        this.endLocX = endLocX;
        this.endLocY = endLocY;
        this.currLocX = currLocX;
        this.currLocY = currLocY;
        this.ID = ID;
        this.turnPickup = turnPickup;
        this.inUse = inUse;
        this.latestFinish = latestFinish;
        currCarrPoolRides.add(ID);
        //has not moved 
        hasNotMoved = true;
        //passenger yes or no
        passenger = false;
        //pickup sometimes the curr x and y is the same as the rides pickup location 
        //pickedUp();
        //no rides done yet
        ridesCompleted = pickUpFormat;
        //no bonus yet
        bonus = false;
        inRide = true;
    }

    public void changeCurrentRide(int rideIndex){
        currentRideIndex = rideIndex;
    }

    public boolean bothBonus(){

        if(multiBonus.get(0) == true && multiBonus.get(1) == true && multiBonus.get(2) == true && multiBonus.get(3) == true){
            return true;
        }

        return false;
    }

    public boolean checkTheFile(){
        int pCounter = 0;
        int dCounter = 0;
        ArrayList<String> pS = new ArrayList();
        ArrayList<String> dS = new ArrayList();

        ArrayList<String> malformedRides = new ArrayList();

        String[] allocations = ridesCompleted.split(" ");
        for(int x = 0; x < allocations.length ; x++){
            //start of the file
            String s = allocations[x];
            if(allocations[x] == "");
            //ends with d
            else if(s.endsWith("d")) {
                dCounter++;
                dS.add(s);
            }
            else if(s.endsWith("p")){
                pCounter++;
                pS.add(s);
            }

            System.out.println(s);
        }


        if(pCounter == dCounter) return true;
        //they dont add up
        else return false;

    }

    public ArrayList<Boolean> getBonuses(){
        return multiBonus;
    }

    public int getScore(int index){
        return multiSpacesMoved.get(index);
    }

    /**
     * Car constructor 2 used for the Stage 1 allocation task
     * @carNum             the ID for this car
     * @rideAllocation     the rides it has to do
     */
    public Car(int carNum , String rideAllocation){        
        this.carNum = carNum;
        this.rideAllocation = rideAllocation;
        getRides();
    }

    /**
     * Car constructor 3 used for the Stage 1 allocation task
     * @carNum             the ID for this car
     * @rideAllocation     the rides it has to do
     */
    public Car(int carNum , ArrayList<Ride> rides){        
        this.carNum = carNum;
        currRides = rides;
        int size = currRides.size();
        currentRideIndex = 0;
        multiBonus = new ArrayList();
        multiBonus.add(false);

        multiPassenger = new ArrayList();
        multiPassenger.add(false);

        multiSpacesMoved = new ArrayList();
        multiSpacesMoved.add(calcScore(0));

        multiComplete = new ArrayList();
        multiComplete.add(false);

        dropOffOrder = new ArrayList();
        pickUpOrder = new ArrayList();
        setInUse();
        StartOrder();

    }

    public void populateRideData(int rideIndex){
        Ride r = currRides.get(rideIndex);
        //if the arraylist is tottally populated with data then basically set the values else populate by adding
        if(multiPassenger.size() == 4 && currRides.size() == 4){
            multiBonus.set(rideIndex, false);
            multiSpacesMoved.set(rideIndex, calcScore(rideIndex));
            multiPassenger.set(rideIndex , false);
            multiComplete.set(rideIndex , false);
        }
        else {
            multiBonus.add(false);
            multiSpacesMoved.add(calcScore(rideIndex));
            multiPassenger.add(false);
            multiComplete.add(false);
        }

    }

    public void setNull(int rideIndex){
        Ride r = currRides.set(rideIndex, null);
    }

    public int calcScore(int rideIndex){
        Ride r = currRides.get(rideIndex);
        int x = r.getStartX();
        int y = r.getStartY();
        int ex = r.getEndX();
        int ey = r.getEndY();

        int xTotal = x -  ex;
        int yTotal = y -  ey;
        xTotal = retNonNegative(xTotal);
        yTotal = retNonNegative(yTotal);
        int Score = xTotal + yTotal; 

        return Score;
    }

    public void StartOrder(){
        int[] distanceForRides = new int[4];
        //ride one is the first ride and therefore we go from its sx and sy
        int cx = getCurrLocX();
        int cy = getCurrLocY();

        int counter = 0;

        for(Ride r : currRides){
            int xDiff = cx - r.getStartX();
            int yDiff = cy - r.getStartY();

            // negatives
            if(xDiff < 0){
                xDiff = xDiff * -1;
            }
            if(yDiff < 0){
                yDiff = yDiff * -1;
            }
            //which is closest is the smaller number for total
            int total = xDiff + yDiff;
            distanceForRides[counter] = total;

            counter++;
        }
        //check which is bigger   , asnd sorts the array    
        int[] ordered =  insertionSort(distanceForRides);

        //get their indexs
        for(int x = 0; x < ordered.length; x ++){
            for(int y = 0; y < distanceForRides.length; y++){
                if(ordered[x] == distanceForRides[y]){
                    //add the order of the rides in the shortest distance
                    pickUpOrder.add(y);
                }
            }
        }

    }

    public boolean allPickedUp(){
        for(Boolean b : multiPassenger){
            boolean pb = b;
            if(pb == false){
                return true;
            }
        }
        return false;
    }

    public void resetGroupsOfFour(ArrayList<Ride> rides){
        currRides = rides;

        int size = currRides.size() ;
        currentRideIndex = 0;
        multiBonus = new ArrayList();
        multiPassenger = new ArrayList();
        multiPassenger.add( false);
        if(size > 1){
            multiPassenger.add( false);
            if(size > 2){
                if(size >= 3){
                    multiPassenger.add( false);
                }
                multiPassenger.add( false);
            }
        }

        multiSpacesMoved = new ArrayList();

        addScores();
        multiComplete = new ArrayList();
        multiComplete.add( false);
        if(size > 1){
            multiComplete.add( false);
            if(size > 2){
                if(size >= 3){
                    multiComplete.add( false);
                }
                multiComplete.add( false);
            }
        }

        dropOffOrder = new ArrayList();
        pickUpOrder = new ArrayList();
        setInUse();
        StartOrder();
    }

    public int retNonNegative(int val){
        if(val < 0){
            return val * -1;
        }
        return val;
    }

    public void addScores(){
        for(Ride r : currRides){
            int x = r.getStartX();
            int y = r.getStartY();
            int ex = r.getEndX();
            int ey = r.getEndY();

            int xTotal = x -  ex;
            int yTotal = y -  ey;
            xTotal = retNonNegative(xTotal);
            yTotal = retNonNegative(yTotal);
            int Score = xTotal + yTotal; 

            multiSpacesMoved.add(Score);

        }
    }

    public boolean allDroppedOff(){
        for(Boolean b : multiComplete){
            boolean pb = b;
            if(pb == false){
                return true;
            }
        }
        return true;
    }

    public ArrayList<Ride> getCurrRides(){
        return currRides;
    }

    public int getCurrentIndex(){

        return currentRideIndex;
    }

    public int getCurrentEndIndex(int index){
        int orderIndex = dropOffOrder.get(index);
        return orderIndex;
    }

    public void multiPickUp(int rideIndex){

        //check if ride can start and if correct x and y

        if(World.getTurn() >= currRides.get(rideIndex).earlyStart()){
            if(currRides.get(rideIndex).getStartX() == getCurrLocX() && currRides.get(rideIndex).getStartY() == getCurrLocY()){
                //add to the ride (up to 4 ) if p is pickedup

                multiPassenger.set(rideIndex, true);

                //set the p in allocation string
                String pickUp = currRides.get(rideIndex).getID() + "p";
                if( ridesCompleted == null){
                    ridesCompleted = " " + pickUp + " ";
                }
                else{
                    ridesCompleted =  ridesCompleted + pickUp + " ";
                }

                //the check if a bonus is valid for one of the 4 rides
                if(multiPassenger.size() == 4 && currRides.size() == 4){
                    if(World.getTurn() == currRides.get(rideIndex).earlyStart()){
                        multiBonus.set(rideIndex, true);
                    }             
                    else{
                        multiBonus.set(rideIndex, false);
                    }
                }
                else{
                    if(World.getTurn() == currRides.get(rideIndex).earlyStart()){
                        multiBonus.add(rideIndex, true);
                    }             
                    else{
                        multiBonus.add(rideIndex, false);
                    }
                }
                currentRideIndex ++;
                //will need somin here to stop goin above 4 rides
            }
        }

    }

    public void calcDropOffOrder(){
        int size = currRides.size();
        int[] distanceForRides = new int[size];//
        //stores the ride number order uses index from other method to get the id for closest end loc
        HashMap<Integer, Integer> rideIDs = new HashMap<Integer, Integer>();
        ArrayList<Integer> rideNum =  new ArrayList();
        rideNum.add(currRides.get(0).getID());
        if(size >= 1){
            rideNum.add(currRides.get(1).getID());

            if(size > 2){

                rideNum.add(currRides.get(2).getID());
                if(size >= 3){
                    rideNum.add(currRides.get(3).getID());
                }
            }
        }
        //ride one is the first ride and therefore we go from its sx and sy
        int sx = currRides.get(0).getStartX();
        int sy = currRides.get(0).getStartY();

        int counter = 0;

        for(Ride r : currRides){
            int xE = r.getEndX();
            int yE = r.getEndY();

            int total = xE + yE;
            distanceForRides[counter] = total;
            rideIDs.put(r.getID(), total);
            counter++;
        }
        int xtest  =1;
        //check which is bigger   , asnd sorts the array    
        int[] ordered =  insertionSort(distanceForRides);
        //value list 
        for(int x = 0; x < size; x++){
            for(int y = 0; y < size; y++){
                if(ordered[x] == rideIDs.get(rideNum.get(y))){
                    //add the order of the rides in the shortest distance
                    int currRidesIndex = -1;
                    int counter3 = 0;
                    for(Ride r2: currRides){
                        if(r2.getID() == rideNum.get(y)){
                            currRidesIndex = counter3;
                        }
                        counter3++;
                    }

                    dropOffOrder.add(currRidesIndex);
                }
            }
        }
        int delete = 0;
    }

    public void setIndex (int x ){
        currentRideIndex = x;
    }

    public int[] insertionSort(int[] array){
        for(int i = 0; i < array.length  ; i ++){
            int j = i;
            while(j > 0 && array[j] > array[j-1]){
                int temp = array[j];
                array[j] = array[j-1];
                array[j - 1] = temp;
                j = j -1;
            }
        }
        return array;
    }

    /**
     * Increase the spaces and score for rides which have been picked up 
     */
    public void multiIncreaseSpaces(int rideIndex){

        if(multiSpacesMoved.get(rideIndex) == null ){
            multiSpacesMoved.set(rideIndex,  1);
        }
        else {
            //not null i.e spaces have been moved before, journey started
            multiSpacesMoved.set(rideIndex,  multiSpacesMoved.get(rideIndex) + 1);
        }

    }

    /**
     * Increase the spaces and score for rides which have been picked up 
     */
    public int multiGetSpaces(int rideIndex){
        return multiSpacesMoved.get(rideIndex);

    }

    public void resetSpaces(int rideIndex){
        //multiSpacesMoved.get(rideIndex);
    }

    public int multiComplete(int rideIndex){
        return currRides.get(rideIndex).getID();
    }

    public boolean multiIsComplete(int rideIndex){

        return currRides.get(rideIndex).getComplete();

    }

    /**
     * Get the earliest start for the journey
     * @return the ride we want ES for
     */
    public Ride getMultiEarliestTurn(int rideIndex)
    {
        return currRides.get(rideIndex);
    }

    /**
     * Is the person picked up for this ride
     * @return the passenger value 
     */
    public boolean isPassengerPickedUp(int rideIndex){

        return multiPassenger.get(rideIndex);
    }

    /**
     * Is the curr x the correct start?
     * 
     * @return whether the curr location x matches start x
     */
    public boolean multiCorrectStartX(int rideIndex){
        boolean correctsx = false;
        if(getCurrLocX() == currRides.get(rideIndex).getStartX()){
            correctsx = true;
        }
        return correctsx;
    }

    /**
     * Is the curr y the correct start?
     * 
     * @return whether the curr location y matches start y
     */
    public boolean multiCorrectStartY(int rideIndex){
        boolean correctsy = false;
        if(getCurrLocY() == currRides.get(rideIndex).getStartY()){
            correctsy = true;
        }
        return correctsy;
    }

    /**
     * Is the curr x the correct end Location ?
     * 
     * @return whether the curr location x matches end x
     */
    public boolean multiCorrectEndX(int rideIndex){
        boolean correctx = false;
        if(getCurrLocX() == currRides.get(rideIndex).getEndX()){
            correctx = true;
        }
        return correctx;
    }

    /**
     * Is the curr y the correct end Location ?
     * 
     * @return whether the curr location y matches end y
     */
    public boolean multiCorrectEndY(int rideIndex){
        boolean correctY = false;
        if(getCurrLocY() == currRides.get(rideIndex).getEndY()){
            correctY = true;
        }
        return correctY;
    }

    /**
     * Get the end x coord
     * 
     * @return the end x coord
     */
    public int multiGetStartLocX(int rideIndex) {
        return currRides.get(rideIndex).getStartX();
    }

    /**
     * Get the end x coord
     * 
     * @return the end x coord
     */
    public int multiGetStartLocY(int rideIndex) {
        return currRides.get(rideIndex).getStartY();
    }

    /**
     * Get the end x coord
     * 
     * @return the end x coord
     */
    public int multiGetEndLocX(int rideIndex) {
        return currRides.get(rideIndex).getEndX();
    }

    /**
     * Get the end x coord
     * 
     * @return the end x coord
     */
    public int multiGetEndLocY(int rideIndex) {
        return currRides.get(rideIndex).getEndY();
    }

    /**
     * Drop off the passneger by reseting passenger status and add the curr ride as completed 
     * to the completed list set in use to false.
     */
    public void multiDropOff(int rideIndex){

        if(getCurrLocX() == currRides.get(rideIndex).getEndX() && getCurrLocY() == currRides.get(rideIndex).getEndY()){
            String dropOff = currRides.get(rideIndex).getID() + "d";
            ridesCompleted =  ridesCompleted + dropOff + " ";
            multiComplete.set(rideIndex, true);
            ridesTotal ++;
            currentRideIndex ++;
            //default bool value is false so say complete is true
            //multiComplete.add(rideIndex, true);
        }

    }

    /**
     * Drop off the passneger by reseting passenger status and add the curr ride as completed 
     * to the completed list set in use to false.
     */
    public void endDropOff(int rideIndex){

        String dropOff = currRides.get(rideIndex).getID() + "d";
        ridesCompleted =  ridesCompleted + dropOff + " ";
        //multiComplete.set(rideIndex, true);
        ridesTotal ++;

        //default bool value is false so say complete is true
        //multiComplete.add(rideIndex, true);

    }

    public boolean getPassengerPickedUp(int rideIndex){
        if(multiPassenger.get(rideIndex)){
            return true;
        }
        return false;
    }

    /**
     * Get the rides needed for this car and add to a list of the rides 
     * it is assigned to do for this simulation
     */
    public void getRides(){
        //Intialise the rides
        rideIndexs = new ArrayList();
        //The rideList
        String[] rideList =  rideAllocation.split(" ");
        //Total number of rides needed to do 
        numberOfRides = Integer.parseInt(rideList[0]);
        //Add all the rides index values
        for(int x = 1; x < rideList.length; x++ ){
            rideIndexs.add(Integer.parseInt(rideList[x]));
        }
        //No passenger yet
        passenger = false;
        ridesCompleted = "";
        bonus = false;
        //Set up the next ride (first ride in this case)
        setRide();
    }

    /**
     * Set the next ride up from the list and allow the cra to complete its actions
     * for thsi particular ride 
     */
    public  void setRide(){
        //If the curr Index is not bigger than the ride list then get the next ride
        if(currIndex < rideIndexs.size()){
            //The index
            int rideNum = rideIndexs.get(currIndex);
            //The ride we need
            Ride r = World.getRides().get(rideNum);
            //The update method to update the cars fields
            updateCarRide(rideNum, r.getStartX(), r.getStartY(), r.getEndX(), r.getEndY(), currLocX, currLocY, r.earlyStart(), true, r.getLatestFinish(), rideNum + "p");
            //Car is now taken
            r.setTaken();
            //Inc the index to get next ride if we need to 
            currIndex++;
        }
    }

    /**
     * Get the car number 
     * @return the CarNum
     */
    public int getCarNum(){
        return carNum;
    }

    /**
     * Get the rides completed for this car
     * @return the curr rideCompleted 
     */
    public String getRidesCompleted(){
        return ridesCompleted;
    }

    /**
     * Get the latest finish to recieve points used in scoring etc 
     * @return the latestFinish
     */
    public int getLatestFinsh(){
        return latestFinish;
    }

    /**
     * Get the ID
     * @return the ID
     */
    public int getID(){
        return ID;
    }

    /**
     * Get the passnger if they have one
     * @return the true or false if a passenger is present
     */
    public boolean getPassenger(){
        return passenger;
    }

    /**
     * Get the earliest start for the journey
     * @return the turn to pickup 
     */
    public int getEarliestTurn()
    {
        return turnPickup;
    }

    /**
     * The update for the Car where the supplied info is given for a new ride
     * @ID                 the ride number
     * @startLocX          the start x location
     * @startLocY          the start y location
     * @endLocX the        end x location
     * @endLocY the        end y location
     * @currLocX           the curr x location
     * @currLocY           the curr y location
     * @turnPickup         the turn ride should start
     * @latestFinish       the latest finish 
     */
    public void updateCarRide(int ID ,  int startLocX, int startLocY, int endLocX, int endLocY, int currLocX, int currLocY, int turnPickup, boolean inUse, int latestFinish, String pickUpFormat) {
        //Set the values for the 
        //ID, passenger , start x , start y , end x, end y , curr x, curr y, turn pickup (earliest start), car in  use
        this.passenger = passenger;
        this.startLocX = startLocX;
        this.startLocY = startLocY;
        this.endLocX = endLocX;
        this.endLocY = endLocY;
        this.currLocX = currLocX;
        this.currLocY = currLocY;
        this.ID = ID;
        this.turnPickup = turnPickup;
        this.inUse = inUse;
        this.latestFinish = latestFinish;
        World.getRides().get(ID).setTaken();

        ridesCompleted =  ridesCompleted + pickUpFormat + " ";
        hasNotMoved = true;
        passenger = false;
        //pickedUp();
        inRide = true;
    }

    /**
     * Get the bonus status for curr ride
     * @return the bonus if its true or false
     */
    public boolean getBonus(){
        return bonus;
    }

    /**
     * Set the bonus id this ride will recieve a bonus providing it finishes before latest finish
     */
    public void setBonus(){
        bonus = true;
    }

    /**
     * Get the cars usage status
     * @return the cars usage status
     */
    public boolean inUse(){
        return inUse;
    }

    /**
     * Set the car to being in use
     * @return the in use part
     */
    public void setInUse(){
        inUse = true;
    }

    /**
     * Drop off the passneger by reseting passenger status and add the curr ride as completed 
     * to the completed list set in use to false.
     */
    public void dropoff(){
        passenger = false;
        String pickUp = ID + "d";
        ridesCompleted =  ridesCompleted + pickUp + " ";
        ridesTotal++;
        inUse = false;

    }

    /**
     * Get the total number of rides completed in the simulation 
     * @return ridesTotal 
     * 
     */
    public int getRidesTotal(){
        return ridesTotal;
    }

    /**
     * Get the curr x coord
     * 
     * @return the curr x coord
     */
    public int getCurrLocX() {
        return currLocX;
    }

    /**
     * Set the curr x coord
     * 
     * @param the curr x coord
     */ 
    public void setCurrLocX(int currLocX) {
        this.currLocX = currLocX;
    }

    /**
     * Get the curr y coord
     * 
     * @return the curr y coord
     */ 
    public int getCurrLocY() {
        return currLocY;
    }

    /**
     * Set the curr y coord
     * 
     * @param the curr y coord
     */ 
    public void setCurrLocY(int currLocY) {
        this.currLocY = currLocY;
    }

}