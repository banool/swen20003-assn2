import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 * <h1>Octopus</h1>
 * <h2>Represents the Octopus Enemy</h2>
 * 
 * Octopus is, as said in the spec, a bit of a jerk, and if within a certain
 * range of the Player will target them instead of the waypoints, attempting to
 * collide with them.
 * 
 * @author Daniel Porteous porteousd 696965
 */
public class Octopus extends Enemy {

    /**
     * Starting coordiantes of Octopus
     */
    private static final int START_X = 1476;
    private static final int START_Y = 13086;

    /** The sprite that the Octopus users */
    private final String KART_NAME = "octopus.png";

    /**
     * These define the lower and upper bounds of the ranges at which the
     * Octopus should cease following the waypoints and instead target the
     * Player.
     */
    private final int LOWER_BOUND = 100;
    private final int UPPER_BOUND = 250;

    /**
     * Very basic, just sets the Octopus' location and sprite.
     * 
     * @throws SlickException
     */
    public Octopus() throws SlickException {
        super(START_X, START_Y);
        setSprite(new Image(
                ASSETS_PATH + "/" + getKartsPath() + "/" + KART_NAME));
    }

    /**
     * Determines the behaviour of the Octopus. First, the distance of the
     * Octopus from the Player is calculated. If this turns out to be within
     * the previously defined boundaries, the Octopus will set its rotation
     * such that it targets the Player. If the Octopus however is outside of
     * these bounds, it will just follow the waypoints.
     * 
     * @param waypoints
     *            The waypoints that the Enemy will follow.
     * @param player
     *            Required so the Octopus knows where the Player is and whether
     *            to attempt ot crash into them.
     */
    public void determineBehaviour(double[][] waypoints, Player player) {
        double distFromPlayer = GlobalHelper.getHypotenuse(
                getMapX() - player.getMapX(), getMapY() - player.getMapY());

        if (distFromPlayer > LOWER_BOUND && distFromPlayer < UPPER_BOUND) {
            setRotateDir(
                    getCorrectRotateDir(player.getMapX(), player.getMapY()));
        } else {
            followWaypoints(waypoints);
        }
    }

}
