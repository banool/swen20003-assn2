import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 * <h1>Dog</h1>
 * <h2>Represents the Dog Enemy</h2>
 * 
 * Dog has fairly simple behaviour, and is a real top gal. If Dog is beating the
 * player, she will go slower. If she's being beaten by the player, she'll pick
 * up the pace and try to catch up.
 * 
 * @author Daniel Porteous porteousd 696965
 */
public class Dog extends Enemy {

    /**
     * Starting coordiantes of Dog
     */
    private static final int START_X = 1404;
    private static final int START_Y = 13086;

    /** The sprite that the Dog users */
    private final String KART_NAME = "dog.png";

    /**
     * These constants represent the different base speeds that Dog will move at
     * depending on whether she's in front of or behind Player.
     */
    private final double TRAILING_PLAYER = 0.00055;
    private final double BEATING_PLAYER = 0.00045;

    /**
     * Very basic, just sets the Dog's location and sprite.
     * 
     * @throws SlickException
     */
    public Dog() throws SlickException {
        super(START_X, START_Y);
        setSprite(new Image(
                ASSETS_PATH + "/" + getKartsPath() + "/" + KART_NAME));
    }

    /**
     * This calculates whether the Dog is beating or being beaten by the Player,
     * setting its speed accordingly. It then just follows the waypoints as
     * normal, albeit at the modified speed.
     * 
     * @param waypoints
     *            The waypoints that the Enemy will follow.
     * @param player
     *            Required so the Dog knows whether to go faster or slower.
     */
    public void determineBehaviour(double[][] waypoints, Player player) {
        /*
         * No real need to have a standard speed alternative here because
         * equality with floating point numbers such as doubles is almost
         * impossible. As such, if the Dog is equal or less to the Player in y
         * position, it will just move at the speed as if it were beating the
         * Player.
         */
        if (getMapY() > player.getMapY()) {
            setAcceleration(TRAILING_PLAYER);
        } else {
            setAcceleration(BEATING_PLAYER);
        }

        followWaypoints(waypoints);
    }

}
