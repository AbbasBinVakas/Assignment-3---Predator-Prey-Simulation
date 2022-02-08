import java.util.List;
import java.util.Random;

/**
 * A simple model of a goat.
 * Goats age, move, breed, and die.
 * 
 * @author Abbas BinVakas
 * @version 2020/07/02
 */
public class Goat extends Animal
{
    // Characteristics shared by all goats (class variables).

    // The age at which a goat can start to breed.
    private static final int BREEDING_AGE = 10;
    // The age to which a goat can live.
    private static final int MAX_AGE = 20;
    // The likelihood of a goat breeding.
    private static final double BREEDING_PROBABILITY = 0.18;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The goat's age.
    private int age;

    /**
     * Create a new goat. A goat may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the goat will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Goat(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
    }

    /**
     * This is what the goat does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newGoats A list to return newly born goats.
     */
    public void act(List<Animal> newGoats, boolean daytime)
    {
        incrementAge();
        if(isAlive() && daytime) {
            giveBirth(newGoats);            
            // Try to move into a free location.
            Location newLocation = getField().freeAdjacentLocation(getLocation());
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Increase the age.
     * This could result in the goat's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Check whether or not this goat is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newGoats A list to return newly born goats.
     */
    private void giveBirth(List<Animal> newGoats)
    {
        // New goats are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Goat young = new Goat(false, field, loc);
            newGoats.add(young);
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A goat can breed if it has reached the breeding age.
     * @return true if the goat can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
