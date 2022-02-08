import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of an owl.
 * Owls age, move, eat mice, and die.
 * 
 * @author Abbas BinVakas
 * @version 2020/07/02
 */
public class Owl extends Animal
{
    // Characteristics shared by all owls (class variables).
    
    // The age at which a owl can start to breed.
    private static final int BREEDING_AGE = 20;
    // The age to which a owl can live.
    private static final int MAX_AGE = 100;
    // The likelihood of a owl breeding.
    private static final double BREEDING_PROBABILITY = 0.30;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // The food value of a single owl. In effect, this is the
    // number of steps a owl can go before it has to eat again.
    private static final int MICE_FOOD_VALUE = 10;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The owl's age.
    private int age;
    // The owl's food level, which is increased by eating mice.
    private int foodLevel;

    /**
     * Create an owl. An owl can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the owl will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Owl(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(MICE_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = MICE_FOOD_VALUE;
        }
    }
    
    /**
     * This is what the owl does most of the time: it hunts for
     * mice. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newOwls A list to return newly born owls.
     */
    public void act(List<Animal> newOwls, boolean daytime)
    {
        incrementAge();
        incrementHunger();
        if(isAlive() && !daytime) {
            giveBirth(newOwls);            
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
     * Increase the age. This could result in the owl's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this owl more hungry. This could result in the owl's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for mice adjacent to the current location.
     * Only the first live mice is eaten.
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
            if(animal instanceof Mouse) {
                Mouse mouse = (Mouse) animal;
                if(mouse.isAlive()) { 
                    mouse.setDead();
                    foodLevel = MICE_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this owl is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newOwls A list to return newly born owls.
     */
    private void giveBirth(List<Animal> newOwls)
    {
        // New owls are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Owl young = new Owl(false, field, loc);
            newOwls.add(young);
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
     * An owl can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}