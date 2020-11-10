

/**
 * The Scoring class keeps track of what the current simulation score is
 *
 * Version 7
 * 
 * Daniel Knight
 */
public class Scoring
{
    //The total score for the simulation
    private int totalScore;
    //The scoreBonus for the simulation
    private int scoreBonus;

    /**
     * Constructor for Scoring 
     * @param scoreBonus the score bonus value
     */
    public Scoring(int scoreBonus)
    {
        this.scoreBonus = scoreBonus;
        totalScore = 0;
    }
    
    /**
     * The total score for the project
     * 
     * @param get the score for the simulation
     */
    public int getTotalScore(){
        return totalScore;
    }
    
    /**
     * Add to the curr score with the score value
     * 
     * @param scoreValue the score to add
     */
    public void addScore(int scoreValue){
        totalScore += scoreValue;
    }
    
    /**
     * Add to the curr score the bonus
     */
    public void addBonusScore(){
        totalScore += scoreBonus;
    }

}
