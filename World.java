
import java.io.BufferedReader;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.util.Random;
/**
 * world class
 * 
 * This is the World class it controls the inner workings of the simulation , it acts
 * as the control area where the simulation calciulates turns , moves, actions and allocation
 * of the simulation based of the reading of input information.
 * 
 * 
 * 
 * Current Version 7 
 * 
 * ~ contains stage 1 tasks like reading car to ride allocation 
 * ~ contains extra logic to determine what rides should go where results in much higher score 
 * 
 * Next version (8)
 * 
 * ~ version 8 will get a million + score for the large scenraio :P
 * ~ version 8 will also easily take the input / allocation file via String name when more scenarios are set
 * ~ check for more cars than rides incase index out of bounds 
 * Daniel Knight 
 */
public class World {
    //int x size of the world
    private int XSize;
    //int y size
    private int YSize;
    //The max number of turns for simulation determined by input file
    private int maxTurns;
    //The current turn number for the simulation 
    public static int turn;   
    //The bonus score awarded if the ride starts on time and finishes before the latest finish
    private int bonus;
    //The max number of cars in the fleet for the given simulation
    private int maxCars;
    //Number of rides to fulfil if possible (some may not be completed)
    private int rides;
    //A list of car objects for the scenario essentially a fleet
    private ArrayList<Car> fleet;   
    //A list of ride objects for the scenario
    public static ArrayList<Ride> rideList;   
    //The simple scoring mechanism keeps track of simulation score
    private Scoring score;
    //String list of what rides do what jobs (stage 1 allocation part)
    private ArrayList<String> allocation ;  

    //what do we need to do next 
    boolean pickingUp = true;
    boolean droppingOff = false;

    private boolean lfFactor;

    /**
     * World constructor 
     * 
     * Sets up the scenario reads the files etc 
     * 
     * @throws fileNotFound - if the file cannot be found print stack trace
     */
    public World(String worldFile) throws FileNotFoundException {
        //Allocation intialisation 
        allocation = new ArrayList<String>();
        //Get all the info from the txt input file
        //"E:\\Code for stage 2 new 26-02-19\\Stage3 Allocations\\c3.in"
        readFile(worldFile);
        //Read allocation file what cars go with what if supplied
        //readAllocationFile();
        //Print values for the rows, columns, cars, rides, bonus and steps (testing)
        //System.out.println(XSize);
        //System.out.println(YSize);
        //System.out.println(maxCars);
        //System.out.println(rides);
        //System.out.println(bonus);
        //System.out.println(maxTurns);
        //Create the world size (playing field)
        createWorldSize(XSize,YSize);
        //Scoring mechanism tracks the score
        score = new Scoring(bonus);
        //by default rusn the allocation version the one that gets the score for large scenario
        //runSimulationAllocated();
        runSimulation();
    }

    public int bonusChecker(){
        int counter = 0;
        for(Car c : fleet){
            if(c.bothBonus()){
                counter++;
            }
        }
        return counter;
    }

    public   ArrayList<Car> getCars(){
        return fleet;
    }

    /**
     *  Return the ride list for the curr simulation
     *  
     *  @return ArrayList<Ride> of rides for the simulation
     */
    public static  ArrayList<Ride> getRides(){
        return rideList;
    }

    /**
     *  Return the curr turn for the simulation 
     *  
     *  @return the int turn number
     */
    public  static int getTurn(){
        return turn;
    }

    /**
     *  Create the world  for the self driving cars and creates where 
     *  the cars actually get accessed.
     *  
     *  @param XSize the x coords
     *  @param YSize the y coords
     */
    public void createWorldSize(int XSize, int YSize) {
        //Create the fleet array of cars
        fleet = new ArrayList(maxCars);
        //Create fleet for stagfe 1 allcoation 
        //createFleetStage1();  
        //createFleet();
        createSmarterFleet();

    }

    public int getSize(){
        return rideList.size();
    }

    /**
     * This runs the simulation where the code decides the allocation 
     * 
     * ~ updating soon to do it a smarter way based of curr location and nearby / upcoming rides
     */
    public void runSimulation(){
        while(turn < maxTurns){
            turn();

        }
        //add d to all unfinshed rides to work on the leaderboard
        for(Car c: fleet){
            endGame(c);
        }

        //checkTheFile();

        //System.out.println(score.getTotalScore());
        writeVehicleAllocationFile();
    }

    int var = 0;
    /**
     * Take a turn in the simulation up to max amount of turns then end , very similar to the 
     * previous code howver this one will assign based off the different factors associated with the 
     * simulation (trying to get the best and most optimal score)
     */
    public void turn() {
        for(Car car : fleet){
            //if car is not in use then do not move or do anything , when a drop off is complete it will assign new jobs, or when the current car finishes a job
            if(car.inUse() ){
                ArrayList<Ride> cRides = car.getCurrRides();

                removeCompletedRides(car);
                //do we need to change from picking up to dropping off a person
                boolean changeRide = pickUpOnTheWay(car);

                calcTheCurrentIndex(car);
                //change the index of picking up here 
                int index = car.getCurrentIndex();

                //the movemnet from curr location to start location
                if(pickingUp  ){
                    if(!(car.isPassengerPickedUp((index))) ){
                        //is this the correct x location ?
                        if(!(car.multiCorrectStartX(index))){
                            if(car.getCurrLocX() < car.multiGetStartLocX(index)){
                                car.setCurrLocX(car.getCurrLocX() + 1);
                            }
                            else if(car.getCurrLocX() > car.multiGetStartLocX(index)){
                                car.setCurrLocX(car.getCurrLocX() - 1);
                            }
                        }
                        //correct y location ?

                        else if(!(car.multiCorrectStartY(index))){
                            if(car.getCurrLocY() < car.multiGetStartLocY(index)){

                                car.setCurrLocY(car.getCurrLocY() + 1);
                            }
                            else if(car.getCurrLocY() > car.multiGetStartLocY(index)){
                                car.setCurrLocY(car.getCurrLocY() - 1);
                            }
                        }
                        car.multiPickUp(index);
                    }
                }

                //if the car has picked its passenger up then start moving one spaceeither x or y 
                else if(droppingOff  ){
                    //the drop index , just here for clarity
                    int dropindex = car.getCurrentIndex();
                    boolean noTime = false;
                    //ran out of time to debug , so plugged the null hole with this (test b gets null pointer) due to rides being removed
                    if(lfFactor){
                         noTime = car.getCurrRides().contains(null);
                    }

                    if(car.isPassengerPickedUp((dropindex)) && !(noTime)){
                        //is this the correct x location ?

                        if(!(car.multiCorrectEndX(dropindex))){

                            if(car.getCurrLocX() < car.multiGetEndLocX(dropindex)){
                                car.setCurrLocX(car.getCurrLocX() + 1);
                            }
                            else if(car.getCurrLocX() > car.multiGetEndLocX(dropindex)){
                                car.setCurrLocX(car.getCurrLocX() - 1);
                            }
                        }
                        //correct y location ?

                        else if(!(car.multiCorrectEndY(dropindex))){

                            if(car.getCurrLocY() < car.multiGetEndLocY(dropindex)){

                                car.setCurrLocY(car.getCurrLocY() + 1);
                            }
                            else if(car.getCurrLocY() > car.multiGetEndLocY(dropindex)){
                                car.setCurrLocY(car.getCurrLocY() - 1);
                            }
                        }
                    }
                    //pick up the other rides

                    //drop the passenger off and assign a new ride to this car
                    if(car.multiCorrectEndX(dropindex) && car.multiCorrectEndY(dropindex) && car.isPassengerPickedUp((dropindex)) && !(car.multiIsComplete(dropindex))){
                        //set the ride as complete used for end of simulation
                        rideList.get(car.multiComplete(dropindex)).setComplete();
                        //add the scores if ride arrives before latest finish
                        if(turn < rideList.get(car.getID()).getLatestFinish()){
                            //add the score
                            score.addScore(car.getScore(dropindex));
                            //if it started on time add bonus
                            if(car.getBonus()){
                                score.addBonusScore();
                            }
                        }
                        //Drop off passenger
                        car.multiDropOff(dropindex);
                        //Reset all rides in this cars curr rides
                        if(car.getCurrentIndex() == car.getCurrRides().size() ){
                            bonusCounter = bonusCounter + countBonuses(car);

                            //assignJobsSmarterVer1(car);

                        }
                    }

                }
            }
        }
        //increment turn counter / turn number
        //System.out.println("TURN --> " + turn);
        turn++;
    }

    /**
     * Usefull for when the starts are low and the rides are rushed (initial setUp); calculates the closest and least wasted time
     * No time to add the other features of this to get higher score based off this principle for d metro etc
     */
    public ArrayList<Ride> highBonus1(){
        //assume max movement x ands y 
        //assume max movement x ands y 
        int overallWait = XSize + YSize;
        //the index
        int index= 0;
        //counter
        int counter = 0;

        int maxScorePerRide = 0;
        int ES = maxTurns;

        int currTurn = getTurn();

        //all cars start at 0 0  therefore the x + y is the spaces needed to move 
        for(Ride r : rideList)   {
            //for metro
            if(r.getEndX() != 9999){
                //movement from 0,0 to the start x and y (find the shortest distance)
                int tempTotal = r.getStartX() + r.getStartY();
                //the earliest start
                int currES = r.earlyStart();
                //calculation = ES - (SX + SY)
                int calculation = currES - tempTotal;

                if(calculation < 0){
                    calculation = calculation * -1;
                }
                //see what has the lowest time waiting 
                if(!(r.getTaken()) &&  calculation <= overallWait && !(r.getComplete()) ){
                    index = counter;
                    ES = currES;
                    overallWait = calculation;

                }    
            }

            counter++;
        }

        Ride r = rideList.get(index);
        r.setTaken();
        //we need to check if the next ride passes any value that is below sx and sy

        ArrayList<Ride> rides = new ArrayList();
        rides.add(r); 

        return rides;
    }

    /**
     * The current index of the car based of distance , are rides closer to pick up 
     * are they further away , do we need to drop off
     */
    public void calcTheCurrentIndex(Car c){
        int cx = c.getCurrLocX();
        int cy = c.getCurrLocY();
        int smallest = maxTurns;
        int counter = 0;
        int index = 0;
        //works out the closest start ride (pickup)
        for(Ride r : c.getCurrRides() ){
            if(r != null){
                int x = cx - r.getStartX();
                int y = cy - r.getStartY();
                x = retNonNegative(x);
                y = retNonNegative(y);
                int total = x + y;
                if(total < smallest && c.getPassengerPickedUp(counter) == false && c.multiIsComplete(counter) != true){
                    smallest = total;
                    index= counter;
                }
            }
            counter++;
        }
        
        
        //works out distance to closest ride dropoff
        int smallestEnd = maxTurns;
        int counter2 = 0;
        int index2 = 0;
        for(Ride r : c.getCurrRides()){
            if(r != null){
                int x = cx - r.getEndX();
                int y = cy - r.getEndY();
                x = retNonNegative(x);
                y = retNonNegative(y);
                int total = x + y;
                if(lfFactor){
                    total = total - x;
                }

                if(total < smallestEnd  && c.getPassengerPickedUp(counter2) == true && c.multiIsComplete(counter2) != true){
                    smallestEnd = total;

                    index2= counter2;
                }
            }
            counter2++;
        }

        //the pickup is closer to curr loc
        if(smallest < smallestEnd ){
            c.changeCurrentRide(index);
            pickingUp = true;
            droppingOff = false;
        }
        //the drop off is closer to curr loc
        else{
            c.changeCurrentRide(index2);
            droppingOff = true;
            pickingUp = false;
        }

    }

    /**
     * Is a car within 200 moves from another in which case go towards that ride and pickup as a detour
     * 
     */
    public boolean pickUpOnTheWay(Car c){
        int cx = c.getCurrLocX();
        int cy = c.getCurrLocY();

        int prevSmallestX = maxTurns;
        int prevSmallestY = maxTurns;
        int counter = 0;
        int index = 0;
        boolean rideFound = false;

        ArrayList<Ride> rides = c.getCurrRides();
        
        //rides on route with the current Ride

        if(rides.size() < 4 || rides.contains(null)){
            for(Ride r : rideList){

                int within50x = cx - r.getStartX();
                int within50y = cy - r.getStartY();
                within50x = retNonNegative(within50x);
                within50y = retNonNegative(within50y);
                //&& within50x < prevSmallestX
                //&& within50y < prevSmallestY
                if(within50x  <= 10000  && within50x < prevSmallestX && within50y < prevSmallestY && within50y <= 10000  && !(r.getTaken())){
                    prevSmallestX = within50x;
                    prevSmallestY = within50y;
                    rideFound = true;
                    index = counter;

                }

                counter++;
            }
            int del = 0;
            //rideFound within the range if not look again later
            if(rideFound && rides.size() < 4){
                Ride r = rideList.get(index);
                r.setTaken();
                rides.add(r);
                c.populateRideData(rides.size() - 1);
            }
            else if(rideFound && rides.contains(null)) {
                int replaceIndex = rides.indexOf(null);
                Ride r = rideList.get(index);
                r.setTaken();
                //set the prev completed ride which is noiw null with new ride
                rides.set(replaceIndex,r);
                c.populateRideData(replaceIndex);

            }
        }
        return rideFound;

    }
    
    /**
     * Remove the complete rides
     */
    public void removeCompletedRides(Car c){
        ArrayList<Ride> cRides = c.getCurrRides();
        int counter = 0;
        for(Ride r: cRides){
            if(r != null){
                if(r.getComplete()){
                    //remove that ride from current index
                    c.setNull(counter);

                }
            }
            counter ++;
        }

    }

    //finish rides that are at end of sim
    public void endGame(Car c){
        //how many rides not dropped off    

        int counter = 0;

        for(Ride r : c.getCurrRides()){
            if(r != null){
                if(!(r.getComplete()) && c.getPassengerPickedUp(counter)){
                    c.endDropOff(counter);
                }
            }
            counter ++;
        }

    }
    
    /**
     * Used during testing to see the jobs left
     */
    public int jobsLeft(){
        int i = 0;
        for(Ride r : rideList){
            if(!(r.getTaken())){
                i++;
            }
        }
        return i;
    }
    
    
    /**
     * What rides are not complete yet
     */
    public ArrayList<Ride> takenButNotComplete(){
        ArrayList<Ride> a = new ArrayList();
        for(Ride r : rideList){
            if(r.getTaken() && !(r.getComplete())){
                a.add(r);
            }
        }
        return a;
    }

    /**
     *  Create the fleet of self driving cars  all start at coords x = 0  and y = 0
     */
    public void createSmarterFleet() {
        //Create a car for the fleet up to the maximum amount specified in the file 
        for(int x = 0 ; x < maxCars; x++) {
            //Initialise the car into the array
            //code here for assigning if rides are rapid
            ArrayList<Ride> list = new ArrayList();

            if(checkEarlyStarts()){
                list = lowStarts1() ;
            }
            //large x and y size
            else if(checkGridSize()){
                list = highBonus1() ;
                

            }
            else if(rides < 400){
                list = lowStarts1();
                lfFactor= true;
            }
            else {
                //smaller grids lots of rides , small car count
                //currRideIndex= initialSmallgridLowCars() ;
                list =  highBonus1();
                lfFactor= true;
            }
            //String pickUp = " " + currRideIndex + "p";
            fleet.add(new Car(x , list));
        }
        int alak = 0;
    }

    public boolean checkEarlyStarts(){
        boolean b = true;
        for(Ride r : rideList){
            if(r.earlyStart() > 20){
                b = false;
            }
        }
        return b;
    }

    public boolean checkGridSize(){
        boolean b = true;
        if(XSize < 10000 || YSize < 10000 ){
            b = false;
        }
        return b;
    }
    boolean forLeaderboard = true;
 
    /**
     * Testing used to print the rides out and their details, prints all
     */
    public void printAll(){
        for(int x = 0; x < fleet.size(); x++){
            printRidesDeatails(x);
        }
    }

    /**
     * used for testing to print the ride details
     */
    public void printRidesDeatails(int index){
        Car c = fleet.get(index);
        ArrayList<Ride> r = c.getCurrRides();

        System.out.println("|  sx  |  sy  |  ex  |  ey  |  es  |  lf  |  ");
        System.out.println("|------|------|------|------|------|------|  ");
        System.out.println("|  " + r.get(0).getStartX() +"  |  " +  r.get(0).getStartY() +"  |  " +  + r.get(0).getEndX() +"  |  " +  r.get(0).getEndY() + "  |  " + r.get(0).earlyStart() + "  |  " + r.get(0).getLatestFinish()  + "  |");
        System.out.println("|  " + r.get(1).getStartX() +"  |  " +  r.get(1).getStartY() +"  |  " +  + r.get(1).getEndX() +"  |  " +  r.get(1).getEndY() + "  |  " + r.get(1).earlyStart() + "  |  " + r.get(1).getLatestFinish()  + "  |");
        System.out.println("|  " + r.get(2).getStartX() +"  |  " +  r.get(2).getStartY() +"  |  " +  + r.get(2).getEndX() +"  |  " +  r.get(2).getEndY() + "  |  " + r.get(2).earlyStart() + "  |  " + r.get(2).getLatestFinish()  + "  |");
        System.out.println("|  " + r.get(3).getStartX() +"  |  " +  r.get(3).getStartY() +"  |  " +  + r.get(3).getEndX() +"  |  " +  r.get(3).getEndY() + "  |  " + r.get(3).earlyStart() + "  |  " + r.get(3).getLatestFinish()  + "  |");
        System.out.println("  ");
    }
    
   
    int bonusCounter = 0;
    public int countBonuses(Car c){
        int bonusCount = 0;
        for(Boolean b: c.getBonuses()){
            if(b == true){
                bonusCount ++;
            }
        }

        return bonusCount;

    }


    /**
     * Usefull for when the starts are low and the rides are rushed (initial setUp); 
     * used only for no hurry as rides all have same es and lf
     */
    public ArrayList<Ride> lowStarts1(){
        //assume max movement x ands y 
        int spacesFromZero = XSize + YSize;
        //the index
        int index= 0;
        //counter
        int counter = 0;

        int maxScorePerRide = 0;

        int currTurn = getTurn();
        //all cars start at 0 0  therefore the x + y is the spaces needed to move 
        for(Ride r : rideList)   {
            int tempTotal = r.getStartX() + r.getStartY();

            if(tempTotal < spacesFromZero && !(r.getTaken())  ){
                spacesFromZero = tempTotal;
                index = counter;

            }
            counter++;
        }

        Ride r = rideList.get(index);
        r.setTaken();
        //we need to check if the next ride passes any value that is below sx and sy

        ArrayList<Ride> rides = new ArrayList();
        rides.add(r); 

        return rides;
    }
    
    /**
     * refatcored code that returns non negative value of input
     */
    public int retNonNegative(int val){
        if(val < 0){
            return val * -1;
        }
        return val;
    }

    /**
     * print the current rides
     */
    public void printCurrRides(){
        for(Car c : fleet){
            String s = "";
            for(Ride r: c.getCurrRides()){
                s = s + r.getID() + " "; 

            }
            System.out.println(s);
        }
    }







    public int getScore(){
        return score.getTotalScore();
    }

    public int randIndex(){
        ArrayList<Integer> indexs = new ArrayList();
        int counter = 0;
        for(Ride r : rideList){
            if(!(r.getTaken()) && !(r.getComplete()) ){
                indexs.add(counter);
            }
            counter ++;
        }

        Random rand = new Random();
        int in = rand.nextInt(indexs.size());

        return indexs.get(in);
        //pick a random one 
    }

    /**
     * Basic reading of a text file to get the input
     * for the given scenario / simulation. Set up a list of rides 
     * ready for the vehicles.

     */

    public void readFile(String worldFile) throws FileNotFoundException {
        //Get the file
        rideList = new ArrayList();
        //"D:\\CO655 project\\input.txt"
        FileReader file = new FileReader(worldFile);
        //Try to read the file using a buffered reader
        try(BufferedReader reader = new BufferedReader(file)){
            //The first line which contains the values in order rows , columns, vehicles, rides, bonus , steps 
            String[] firstLine =  reader.readLine().split(" ");
            // Max Rows
            XSize = Integer.parseInt(firstLine[0]); 
            //Max Columns
            YSize = Integer.parseInt(firstLine[1]); 
            //Car count
            maxCars = Integer.parseInt(firstLine[2]);   
            //Number of rides
            rides = Integer.parseInt(firstLine[3]);
            //Bonus value
            bonus = Integer.parseInt(firstLine[4]); 
            //Maximum turn in the simulationexit

            maxTurns = Integer.parseInt(firstLine[5]);  
            //The readline values etc
            String values ;
            int rideNum = 0 ;
            //Do until file is finished so empty lines null
            do  {
                //Get the values from curr line            
                values = reader.readLine();
                //If null don't print
                if(values != "" && values != null) {
                    //Split at regex " " (spaces)
                    String[] currLine =  values.split(" ");
                    //X coord for ride
                    int startX = Integer.parseInt(currLine[0]); 
                    //Y coord for ride
                    int startY = Integer.parseInt(currLine[1]); 
                    //X end coord for ride
                    int endX = Integer.parseInt(currLine[2]);   
                    //Y end coord for ride
                    int endY = Integer.parseInt(currLine[3]);
                    //Earliest pickup for ride
                    int earliestStart = Integer.parseInt(currLine[4]); 
                    //Latest finish for ride
                    int latestFinish = Integer.parseInt(currLine[5]);  
                    //Create the rides and store in the simultions list of rides.
                    rideList.add(new Ride(rideNum,startX, startY, endX, endY, earliestStart, latestFinish, false));
                    //Testing ...
                    //System.out.println(startX);
                    //System.out.println(startY);
                    //System.out.println(endX);
                    //System.out.println(endY);
                    //System.out.println(earliestStart);
                    //System.out.println(latestFinish);
                    //System.out.println("");
                }
                //Inc the ride number how many rides there are in curr simulation
                rideNum++;
                //Condition to stop the reading when value is null
            }  while(values != null); { 
            }
        }
        //Catch any exceptions basic atm will update soon
        catch(Exception e){
            e.printStackTrace();
            //System.out.println();
        }
    }

   

 

    /**
     * This method will creat an allocation file for the simulation based of the code assigning 
     * cars to rides based off a number of factors to get the highest ands optimal scores
     */
    public void writeVehicleAllocationFile(){

        //Create the submission file
        //File file = new File("submissionFile" + ".txt");
        //Write to file not read
        //file.setWritable(true);
        //Writer used to actually write a txt file
        //FileWriter writer = new FileWriter(file);
        //For all the cars write their allocation of rides completed
        for(Car car: fleet){
            //writer.write(car.getRidesTotal() + " " + car.getRidesCompleted() + "\r\n");
            System.out.println(car.getRidesTotal() +  car.getRidesCompleted() );
        }

        //Close the file we are done writing
        //writer.close();
        //Readonly for file no tampering
        //file.setReadOnly();

    }

    /**
     * This method will creat an allocation file for the simulation based of the code assigning 
     * cars to rides based off a number of factors to get the highest ands optimal scores
     */
    public void writeVehicleOutputFile(){
        try{
            //Create the submission file
            File file = new File("submissionFile" + ".txt");
            //Write to file not read
            file.setWritable(true);
            //Writer used to actually write a txt file
            FileWriter writer = new FileWriter(file);
            //For all the cars write their allocation of rides completed
            for(Car car: fleet){
                writer.write(car.getRidesTotal() + " " + car.getRidesCompleted() + "\r\n");
            }
            //Close the file we are done writing
            writer.close();
            //Readonly for file no tampering
            file.setReadOnly();
        }
        // ~ catch any exceptions basic atm will update soon
        catch(IOException e){
            //what went wrong?
            e.printStackTrace();
        }
    }
}
