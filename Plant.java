import java.util.Random;

/**
 * Write a description of class Plant here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Plant implements Beings
{
    private static final double GROWTH_HEIGHT = 2; 
    
    private static final int MAX_GROWTH_HEIGHT = 10;
    
    private static final Random rand = Randomizer.getRandom();

    
    private int height;

    /**
     * Constructor for objects of class Plant
     */
    public Plant(boolean randomHeight, Field field, Location location)
    {
        if(randomHeight) {
            height = rand.nextInt(MAX_GROWTH_HEIGHT);
        }
        else {
            height = 0;
        }
        
    }
    
    protected void setEaten()
    {
        height = 0;
    }
    
    private void incrementHeight()
    {
        height++;
        if(height > MAX_GROWTH_HEIGHT) {
            setEaten();
        }
    }
    
    public void act(boolean daytime)
    {
        if(!daytime) {
            incrementHeight();
        }
    }
}
