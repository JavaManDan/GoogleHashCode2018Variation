/**
 * This is the Ride class it's quite basic and handles the rides provided from the input file 
 * handled by the World class.
 *
 * Version 7
 * 
 * Daniel Knight
 * 
 */
public class Ride
{
    //The rideNumber
    private int rideNum;
    //The start location where the car needs to be X
    private int  startLocX;
    //The start location where the car needs to be Y
    private int  startLocY;
    //tThe end location where the car needs to be X
    private int  endLocX;
    //The end location where the car needs to be Y
    private int  endLocY;
    //Earliest start
    private int earliestStart;
    //Latest finish
    private int latestFinish;
    //This ride has been taken
    private boolean taken;
    //Completed?
    private boolean complete;
    
    private boolean pickedUp;

    /**
     * Constructor of Ride class where the params fill the correct values
     * @rideNum            the ride number
     * @startLocX          the start x location
     * @startLocY          the start y location
     * @endLocX the        end x location
     * @endLocY the        end y location
     * @earliestStart      the earliest start 
     * @latestFinish       the latest finish 
     * @taken              true or false is taken 
     *
     */
    public Ride(int rideNum , int startLocX, int startLocY, int endLocX, int endLocY, int earliestStart, int latestFinish ,boolean taken)
    {
        //Initialise instance variables
        this.rideNum = rideNum;
        this.startLocX = startLocX;
        this.startLocY = startLocY;
        this.endLocX = endLocX;
        this.endLocY = endLocY;
        this.earliestStart = earliestStart;
        this.latestFinish = latestFinish;
        this.taken = taken;
        complete = false;
    }
    
    public void setPickedUp(){
        pickedUp = true;
    }

    /**
     * Used for if this ride has been done / in progress
     * @return taken or not
     */
    public boolean getTaken(){
        return taken;
    }

    /**
     * Ride is complete so dont do this ride again
     */
    public void setComplete(){
        complete = true;
    }

    /**
     * Check whether the ride is complete
     * 
     * @return the completed status
     */
    public boolean getComplete(){
        return complete;
    }

    /**
     * Set the taken status as true and this ride is being completed by a Car object
     */
    public void setTaken(){
        taken =  true;
    }

    /**
     * Get the Id for the rideNum used for World class
     * @return the ride number
     */
    public int getID(){
        return rideNum;
    }

    /**
     * Return start X location 
     * @return X start
     */
    public int getStartX(){
        return startLocX;
    }

    /**
     * Return start Y location 
     * @return Y start
     */
    public int getStartY(){
        return startLocY;
    }

    /**
     * Return end X location 
     * @return X end
     */
    public int getEndX(){
        return endLocX;
    }

    /**
     * Return end Y location 
     * @return Y end
     */
    public int getEndY(){
        return endLocY;
    }
    
    /**
     * Return earliest start 
     * @return turn number when the ride can start
     */
    public int earlyStart(){
        return earliestStart;
    }
    
    /**
     * The latest finish of the ride
     * 
     * @return the latestFinish
     */
    public int getLatestFinish(){
        return latestFinish;
    }

}