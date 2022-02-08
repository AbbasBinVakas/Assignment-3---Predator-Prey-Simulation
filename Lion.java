import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a snake.
 * Lions age, move, eat goats, and die.
 * 
 * @author Abbas BinVakas
 * @version 2020/07/02
 */
public class Lion extends Animal
{
    // Characteristics shared by all lions (class variables).

    // The age at which a lion can start to breed.
    private static final int BREEDING_AGE = 30;
    // The age to which a lion can live.
    private static final int MAX_AGE = 70;
    // The likelihood of a lion breeding.
    private static final double BREEDING_PROBABILITY = 0.10;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single lion. In effect, this is the
    // number of steps a lion can go before it has to eat again.
    private static final int GOAT_FOOD_VALUE = 30;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    // The lion's age.
    private int age;
    // The lion's food level, which is increased by eating goats.
    private int foodLevel;

    /**
     * Create a lion. A lion can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the lion will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Lion(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(GOAT_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = GOAT_FOOD_VALUE;
        }
    }

    /**
     * This is what the lion does most of the time: it hunts for
     * goats. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newLions A list to return newly born lions.
     */
    public void act(List<Animal> newLions, boolean daytime)
    {
        incrementAge();
        incrementHunger();
        if(isAlive() && daytime) {
            giveBirth(newLions);            
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
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
     * Increase the age. This could result in the lion's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this lion more hungry. This could result in the lion's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for goats adjacent to the current location.
     * Only the first live goats is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Goat) {
                Goat goat = (Goat) animal;
                if(goat.isAlive()) { 
                    goat.setDead();
                    foodLevel = GOAT_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * Check whether or not this lion is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newLions A list to return newly born lions.
     */
    private void giveBirth(List<Animal> newLions)
    {
        // New lions are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Lion young = new Lion(false, field, loc);
            newLions.add(young);
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
     * A lion can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}