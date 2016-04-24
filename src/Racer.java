import java.util.ArrayList;
import org.newdawn.slick.SlickException;

/**
 * <h1>Racer</h1>
 * <h2>Abstract class which represents all karts driving around on the map</h2>
 * 
 * This class handles the bulk of things that define a Racer (someone in a kart)
 * in the game world. This includes primarily things related to how a Racer
 * moves around the game world, as well as how they interact with Items and each
 * other.
 * 
 * Doesn't define any static methods, but does have an abstract method for the
 * Enemy and Player subclasses to implement.
 * 
 * @author Daniel Porteous porteousd 696965
 */
public abstract class Racer extends RenderableObject {

    /**
     * Racer specific constants relating to how it moves and interacts with
     * other entities in the world.
     */
    private final double BASE_ACCELERATION = 0.0005;
    private final double BASE_ANGULAR_MODIFIER = 0.004;

    /** Number of pixels in which proximity of which player picks up an item */
    private final int RACER_COLLISION_RADIUS = 40;

    /**
     * Number of pixels in the proximity of which Racer interacts with an item.
     */
    private final int ITEM_COLLISION_RADIUS = 40;

    /** Necessary constants related to the location of the karts' sprite */
    private final String KARTS_PATH = "karts/";

    /**
     * Standard variables to hold information about the movement state of Racer.
     */
    private double speed; // Speed, not velocity, this is a scalar value.
    private double acceleration;
    private double angularModifier;
    private double moveDir;
    private double rotateDir;

    /** ArrayList holding all the items currently active on the Racer */
    private ArrayList<Item> activeItems;

    /**
     * As well as calling the super constructor, which mainly just places the
     * object on the map, this constructor sets the values relating to the
     * movement of the Racer to the standard constants.
     * 
     * An ArrayList of Items is also created and given an initial size of 3, a
     * size which likely wouldn't need to be increase considering that it's
     * almost impossible for a Racer to be under more than 3 active effects at
     * one time.
     * 
     * @param startX
     *            In pixels. Horizontal x-coordinate starting position.
     * @param startY
     *            In pixels. Vertical y-coordinate starting position.
     * @throws SlickException
     */
    public Racer(double startX, double startY) throws SlickException {
        super(startX, startY);
        moveDir = 0.0;
        rotateDir = 0.0;
        speed = 0.0;
        acceleration = BASE_ACCELERATION;
        angularModifier = BASE_ANGULAR_MODIFIER;
        activeItems = new ArrayList<Item>(3);
    }

    /**
     * Simple method to check whether two Racers "equal". This method however
     * doesn't check true equality, just enough to confirm whether a Racer is
     * itself for the checkCollisions() method, so that it won't check if it's
     * colliding with itself.
     * 
     * @param racer
     *            The other Racer in question to which we will compare.
     * @return Will return true if they are "equal", false if not.
     */
    private boolean equals(Racer racer) {
        if (getMapX() == racer.getMapX() && getMapY() == racer.getMapY()) {
            return true;
        }
        return false;
    }

    /**
     * Signals that all subclasses must have this method so they can check if
     * they have collided with any items on the map. This method can't exist
     * here in the superclass, because Enemy and Player interact with Items
     * slightly differently.
     * 
     * @param mapItems
     *            The Items currently on the map, active or not.
     */
    public abstract void checkItemCollision(ArrayList<Item> mapItems);

    /**
     * When the very inner if statement is triggered, it means that the
     * useEffect() method of the item in question returned true, indicating that
     * its active effect on the player should end. As such, the Racer's
     * modifiable movement variables, namely at this point the acceleration and
     * angularModifier, are reset to the default and the item is removed from
     * the Racer's active effects ArrayList.
     *
     * @param mapItems
     *            The items currently on the map. These need to passed through
     *            to the useEffect method so when the item is activated (e.g. a
     *            tomato is thrown), it can be added to the list of items
     *            currently on the map.
     * @throws SlickException
     */
    public void processActiveItems(ArrayList<Item> mapItems)
            throws SlickException {
        for (int i = 0; i < activeItems.size(); i++) {
            if (activeItems.get(i).applyEffect(this, mapItems)) {
                acceleration = BASE_ACCELERATION;
                angularModifier = BASE_ANGULAR_MODIFIER;
                activeItems.remove(i);
                i--;
            }
        }
    }

    /**
     * This method processes all potential sources of collision for the Racer.
     * This includes terrain from the world as well as other Racers.
     * 
     * @param racers
     *            All the racers active on the map.
     * @param world
     *            The world object, so we can check friction at the potential
     *            new location.
     * @param newX
     *            Potential new x-coordinate.
     * @param newY
     *            Potential new y-coordinate.
     * @return Will return true if there will be a collision, or false
     *         otherwise.
     */
    private boolean checkCollisions(Racer[] racers, World world, double newX,
            double newY) {

        // Checking to see if the Racer is going to collide with terrain.
        if (world.getFriction(newX, newY) >= 1) {
            return true;
        }

        /*
         * Goes through each Racer in racers to confirm that the new position
         * wouldn't result in a collision. The equals() method is called each
         * time just to confirm that we aren't checking a Racer against itself,
         * in which case collision would always be true.
         */
        for (int i = 0; i < racers.length; i++) {
            if (!racers[i].equals(this)) {
                if (GlobalHelper.getHypotenuse(newX - racers[i].getMapX(),
                        newY - racers[i].getMapY()) < RACER_COLLISION_RADIUS) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Updates the angle and speed of the car, as well as processing any
     * 
     * @param rotateDir
     *            -1, 0 or 1 based on the Left or Right arrow keypresses
     *            collected in Game. Used for rotation.
     * @param moveDir
     *            Same as above except for the Up and Down keys. Used for
     *            forward/back movement.
     * @param world
     *            The world object itself so that friction can be checked based
     *            on the location of the Racer.
     * @param mapItems
     *            The items currently on the map.
     * @param racers
     *            The racers in the game, i.e. Player and the Enemies.
     */
    public void update(double rotateDir, double moveDir, World world,
            ArrayList<Item> mapItems, Racer[] racers) throws SlickException {

        /*
         * Initially setting the moveDir and rotateDir to those received from
         * the keyboard.
         */
        this.moveDir = moveDir;
        this.rotateDir = rotateDir;

        /*
         * Here we call the checkItemCollion method to check whether the Racer
         * has collided with any items on the map (only active items for
         * Enemies). If this turns out to be the case, the item is removed from
         * the map and dealt with accordingly.
         */
        checkItemCollision(mapItems);

        /*
         * Going through the items currently active on the Racer and applying
         * their effects.
         */
        processActiveItems(mapItems);

        /*
         * These lines actually finally change the movement information of the
         * Racer, taking into account the effects from the activeItems. If there
         * were no modifications made during the processActiveItems() call, this
         * will just apply the defaults received through the keyboard input from
         * World (which was received in turn from Game).
         */
        setAngle(Angle.fromRadians(
                getAngle().getRadians() + angularModifier * this.rotateDir));

        setSpeed((getSpeed() + acceleration * this.moveDir)
                * (1 - world.getFriction(getMapX(), getMapY())));

        /*
         * Here we are calculating the new position for the Racer. This new
         * position is then used to detect if the Racer will collide with
         * terrain or other Racers, in which case the speed is set to 0.
         */
        double newX = getMapX() + getAngle().getXComponent(getSpeed());
        double newY = getMapY() + getAngle().getYComponent(getSpeed());
        if (checkCollisions(racers, world, newX, newY)) {
            setSpeed(0.0);
        } else {
            /*
             * Finally, if there was no collision detected updating the position
             * on the map. The doubles will later be resolved to ints as they
             * are pixels.
             */
            setMapX(getMapX() + getAngle().getXComponent(getSpeed()));
            setMapY(getMapY() + getAngle().getYComponent(getSpeed()));
        }

    }
    

    /**
     * @return the activeItems
     */
    public ArrayList<Item> getActiveItems() {
        return activeItems;
    }

    /**
     * @return the KARTS_PATH
     */
    public String getKartsPath() {
        return KARTS_PATH;
    }

    /**
     * @return the ITEM_COLLISION_RADIUS
     */
    public int getItemCollisionRadius() {
        return ITEM_COLLISION_RADIUS;
    }

    /**
     * @return the rotateDir
     */
    public double getRotateDir() {
        return rotateDir;
    }

    /**
     * @param rotateDir
     *            the rotateDir to set
     */
    public void setRotateDir(double rotateDir) {
        this.rotateDir = rotateDir;
    }

    /**
     * @return the speed
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * @param speed
     *            the speed to set
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * @param angularModifier
     *            the angularModifier to set
     */
    public void setAngularModifier(double angularModifier) {
        this.angularModifier = angularModifier;
    }

    /**
     * @param acceleration
     *            the acceleration to set
     */
    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

    /**
     * @param moveDir
     *            the moveDir to set
     */
    public void setMoveDir(double moveDir) {
        this.moveDir = moveDir;
    }

}
