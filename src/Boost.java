import java.util.ArrayList;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 * <h1>Boost</h1>
 * <h2>Represents the Boost item and its effect</h2>
 * 
 * One of the three items that can be used by Player. The most simple to code as
 * it doesn't exist on the map when activated, unlike Tomato and Oil.
 * 
 * Applies a Boost effect to the player where their acceleration is increased
 * for a set period of time, and they lose control over whether to move forward
 * or not, with it being locked going forward.
 * 
 * @author Daniel Porteous porteousd 696965
 */
public class Boost extends Item {

    /** The image file for the boost item when its on the map */
    private static final String SPRITE = "boost.png";

    /** The set acceleration that the Player is subject to while boosting */
    private static final double ACCELERATION = 0.0008;

    /** Duration of the effect, as explained below */
    private static final int DURATION = 3000;
    
    /**
     * This variable represents the timer for tracking how long the Player has
     * been boosting. Once this counter reaches DURATION, the effect ends.
     */
    private int timer = 0;

    /**
     * Standard constructor. Just calls the superclass constructor, placing it
     * on the map in the right location, as well as setting the sprite.
     * 
     * @param startX
     *            In pixels. Horizontal x-coordinate starting position.
     * @param startY
     *            In pixels. Vertical y-coordinate starting position.
     * @param itemPath
     *            The folder name of where the items are stored.
     * @throws SlickException
     */
    public Boost(int startX, int startY, String itemPath)
            throws SlickException {
        super(startX, startY);
        setSprite(new Image(ASSETS_PATH + itemPath + SPRITE));
    }

    /**
     * This method applies the effect of the Item to the Racer, namely Player.
     * Sets the players acceleration to the set amount and locks their movement
     * in the forward direction. Once the effect has reached its duration, this
     * method returns true, signifying to the Racer to cease applying the effect
     * and remove it from its active effects.
     * 
     * @param racer
     *            This is the racer on which to apply the effect. As it stands,
     *            this will only be used on the Player.
     * @param mapItems
     *            The items array is mandatorily passed into applyEffect as
     *            defined by the abstract method in Item, but is not used here.
     */
    public boolean applyEffect(Racer racer, ArrayList<Item> mapItems)
            throws SlickException {

        if (timer > DURATION) {
            return true;
        }

        racer.setAcceleration(ACCELERATION);
        racer.setMoveDir(1);
        timer += 1;
        return false;
    }

    /**
     * Because boost doesn't have an active form that physically exists in the
     * game world, there is no need to do anything here.
     * 
     * @param world
     *            The world object. Mandatory as per the abstract method in
     *            Item, but not used here.
     */
    public boolean update(World world) {
        return false;
    }

    /**
     * As above, because the item has no active form in the world this will
     * always just be false.
     */
    public boolean isActive() {
        return false;
    }
}
