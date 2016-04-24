import java.util.ArrayList;
import org.newdawn.slick.SlickException;

/**
 * <h1>Enemy</h1>
 * <h2>Abstract class from which specific Enemy subclasses can be created and
 * instantiated</h2>
 * 
 * This abstract class defines standard methods that will be inherited by the
 * different subtypes of Enemy, namely Octopus, Dog and Elephant.
 * 
 * Additionally, there are static methods which can be called from World in
 * order to handle all the Enemies collectively, including update and render
 * methods. Constructors and non-static methods appear first, followed by the
 * static methods.
 * 
 * @author Daniel Porteous porteousd 696965
 */
public abstract class Enemy extends Racer {

    /**
     * Constants specific to the Enemy, namely how many there are and the range
     * at which they should start going towards the next waypoint.
     */
    private static final int NUM_ENEMIES = 3;
    private static final int WAYPOINT_RADIUS = 250;

    /** Will track which waypoint the Enemy is currently tracking towards. */
    private int currentWaypointTarget;

    /**
     * As is the trade off with abstract methods, not every subclass of Enemy
     * will need the player to be passed to them. However this is a necessary
     * sacrifice for the conciseness and extensibility offered by being able to
     * call all the subclass methods in a simple for loop in an abstract
     * superclass such as we do here with the method updateEnemies().
     * 
     * This method will vary based on the subclass, and for some (such as
     * Elephant), will be nothing but a call to the followWaypoints() method.
     *
     * @param waypoints
     *            The waypoints that the Enemy will follow. These are normally
     *            held in world as they're intrinsic to the map.
     * @param player
     *            The player object, necessary specifically for the individual
     *            behaviour of Dog and Octopus.
     */
    public abstract void determineBehaviour(double[][] waypoints,
            Player player);

    /**
     * Calls the standard constructor from the superclass Racer, which in turn
     * calls the super constructor of RenderableObject which does most of the
     * heavy lifting. The only additional step is to set our targets on the
     * first (well, 0th) waypoint.
     * 
     * @param startX
     *            In pixels. Horizontal x-coordinate starting position.
     * @param startY
     *            In pixels. Vertical y-coordinate starting position.
     * @throws SlickException
     *             Standard error thrown by slick, handled with this throws
     *             clause.
     */
    public Enemy(int startX, int startY) throws SlickException {
        super(startX, startY);
        currentWaypointTarget = 0;
    }

    /**
     * Goes through each item on the map and confirms that it is active. This
     * active check is necessary because only the player can interact with
     * inactive items. This is also the reason why this method can't be moved up
     * into the Racer class, as the behaviour is different for Player and Enemy.
     * If the item is confirmed as active, it checks if the Enemy is within the
     * set range of them. If so, it removes the item from the map and adds it to
     * that Enemy's active effects, which will be cycled through in the
     * processActiveItems() method of Racer.
     * 
     * @param mapItems
     *            An ArrayList of all the items currently on the map, both
     *            dormant and active
     */
    public void checkItemCollision(ArrayList<Item> mapItems) {
        for (int i = 0; i < mapItems.size(); i++) {
            // Making sure the item is active before interacting with it.
            if (mapItems.get(i).isActive()) {
                // Checking the Enemy is within range.
                if (GlobalHelper.getHypotenuse(
                        getMapX() - mapItems.get(i).getMapX(),
                        getMapY() - mapItems.get(i)
                                .getMapY()) < getItemCollisionRadius()) {
                    /*
                     * Adding the item to the Enemy's active items and removing
                     * it from the map.
                     */
                    getActiveItems().add(mapItems.get(i));
                    mapItems.remove(i);
                    i--;
                }
            }
        }
    }

    /**
     * Called each turn, this method checks whether the Enemy has come within
     * the range of its target waypoint. If this is the case it will increase
     * the waypoint tracker and next iteration will start to move towards the
     * next one. If it gets to the last waypoint it will cease advancing.
     * 
     * @param waypoints
     *            An array of arrays, specifically 2 item arrays holding an x
     *            and y coordinate, specifying the waypoints to which the Enemy
     *            should go, in order of travel.
     */
    public void followWaypoints(double[][] waypoints) {

        double[] target = waypoints[currentWaypointTarget];

        /*
         * Checking if we're inside our current target. If so, we'll advance to
         * the next one. The way this method is slightly limited because it only
         * works if the waypoints are in order of which the Enemy should travel
         * to them. Potential other solutions could include some algorithm which
         * finds which waypoint is closest to the Enemy which is in the
         * direction of the finish. Not necessary within the scope of the
         * project and the given data, however.
         */
        if (GlobalHelper.getHypotenuse(getMapX() - target[0],
                getMapY() - target[1]) < WAYPOINT_RADIUS) {
            if (currentWaypointTarget != waypoints.length - 1)
                currentWaypointTarget++;
            /*
             * This recalculation and reassignment of the target might not be
             * necessary here, as it would be picked up on the next iteration
             * and one ms of inaccuracy isn't the end of the world. To be on the
             * safe side we'll update it regardless as efficiency hasn't been a
             * huge consideration of this project.
             */
            target = waypoints[currentWaypointTarget];
        }
        // Getting the new rotate direction for the Enemy based on the target.
        setRotateDir(getCorrectRotateDir(target[0], target[1]));
    }

    /**
     * Calculates the new direction to which the kart should rotate in order to
     * move towards the new target. This involves first finding out first
     * whether the angle of the kart is more or less than that created between
     * the kart's current position and its target. Following that, the quadrant
     * that the kart is in (consider a unit circle) is calculated using atan and
     * the rotate direction is returned accordingly.
     * 
     * @param targetX
     *            x-coordinate in pixels of the target waypoint.
     * @param targetY
     *            y-coordinate in pixels of the target waypoint.
     * @return Returns a double indicating which direction the Kart should
     *         rotate.
     */
    public double getCorrectRotateDir(double targetX, double targetY) {
        // The x and y distances from the Enemy to the target waypoint
        double xDiff = getMapX() - targetX;
        double yDiff = getMapY() - targetY;
        // Creating a new angle based on the position of the waypoint and
        // normalising it to work with the angle of Racer.
        Angle targetAngle = Angle
                .fromRadians(Math.atan2(yDiff, xDiff) + Math.PI / 2);

        if (getAngle().subtract(targetAngle).getRadians() < 0) {
            return -1.0;
        } else if (getAngle().subtract(targetAngle).getRadians() > 0) {
            return 1.0;
        } else {
            return 0.0;
        }
    }

    /**
     * Instantiates objects for each Enemy to exist in the game. Notably, unlike
     * for the Items or the Waypoints, the Enemies aren't read in from a file
     * but are instead hardcoded in. This is more acceptable however because
     * there are only 3 enemies, and little room in the current map for more.
     * 
     * @return A static array containing all the Enemies.
     * @throws SlickException
     */
    public static Enemy[] createEnemies() throws SlickException {
        Enemy[] output = new Enemy[NUM_ENEMIES];
        output[0] = new Elephant();
        output[1] = new Dog();
        output[2] = new Octopus();
        return output;
    }

    /**
     * This method is a gateway to the methods that do most of the heavy lifting
     * with the implementation of all the Enemies. As far as this method goes
     * however, all we do is call the generic Racer level update for each Enemy,
     * followed by the determineBehaviour() method, which enacts the specific
     * behaviour of the individual subclasses of Enemy.
     * 
     * @param enemies
     *            Array of all the enemies currently active on the map.
     * @param world
     *            The world object itself, passed in so we can calculate
     *            frictions.
     * @param mapItems
     *            List of all the items on the map, whether active or not.
     * @param racers
     *            Array of the Racers. This may seem a bit counter intuitive,
     *            passing Racers, Enemies and Player through individually
     *            considering that Enemies and Player are just subsets of the
     *            Racer array. However, at some stage these subsets need to be
     *            calculated, and considering this has already been done in
     *            World we may as well just pass them in instead of
     *            recalculating them.
     * @param waypoints
     *            An array of 2 item arrays containing the waypoints as doubles.
     * @param player
     *            The player object. This is used in to determine the specific
     *            behaviour of Dog and Octopus.
     * @throws SlickException
     */
    public static void updateEnemies(World world, Racer[] racers,
            Enemy[] enemies, Player player, ArrayList<Item> mapItems,
            double[][] waypoints) throws SlickException {

        for (Enemy enemy : enemies) {
            enemy.update(enemy.getRotateDir(), 1, world, mapItems, racers);
            enemy.determineBehaviour(waypoints, player);
        }
    }

    /**
     * Takes the list of enemies and calls their render methods one by one,
     * pretty simple.
     * 
     * @param enemies
     *            A list of the enemies on the map.
     * @param camX
     *            The current x-coordinate of the camera.
     * @param camY
     *            The current y-coordinate of the camera.
     */
    public static void renderEnemies(Enemy[] enemies, double camX,
            double camY) {
        // Using a for-each loop so we don't have to call getNumItems() again.
        for (Enemy enemy : enemies) {
            enemy.render(camX, camY);
        }
    }
}
