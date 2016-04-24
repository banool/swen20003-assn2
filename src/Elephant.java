import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 * <h1>Elephant</h1>
 * <h2>Represents the Elephant Enemy</h2>
 * 
 * Elephant is the most basic Enemy and can be considered a template upon which
 * additional behaviours could be added. All the Elephant does is follow the
 * waypoints, the method for which is defined in the superclass Enemy.
 * 
 * @author Daniel Porteous porteousd 696965
 */
public class Elephant extends Enemy {

    /**
     * Starting coordiantes of Elephant
     */
    private static final int START_X = 1260;
    private static final int START_Y = 13086;

    /** The sprite that the Elephant users */
    private final String KART_NAME = "elephant.png";

    /**
     * Very basic, just sets the Elephant's location and sprite.
     * 
     * @throws SlickException
     */
    public Elephant() throws SlickException {
        super(START_X, START_Y);
        setSprite(new Image(
                ASSETS_PATH + "/" + getKartsPath() + "/" + KART_NAME));
    }

    /**
     * The determine behaviour method. All it does for elephant is follow the
     * waypoints one by one ignoring what the other Racers are doing.
     * 
     * @param waypoints
     *            The waypoints that the Enemy will follow.
     * @param player
     *            Required as is defined by abstract method, but not used here.
     */
    public void determineBehaviour(double[][] waypoints, Player player) {
        followWaypoints(waypoints);
    }

}
