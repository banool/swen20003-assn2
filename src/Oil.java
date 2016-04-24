import java.util.ArrayList;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 * <h1>Oil</h1>
 * <h2>Represents the Oil item and its effect</h2>
 * 
 * One of the three items that can be used by Player. This item is a fairly
 * standard example of an item which has a simple active effect.
 * 
 * Upon use an oil slick is created. This is destroyed upon collision with a
 * Racer, the control for which is handled by the Racer itself. Upon collision
 * with this Racer, the effect described in applyEffect() is applied, which
 * causes it to "spin out" for a set duration.
 * 
 * @author Daniel Porteous porteousd 696965
 */
public class Oil extends Item {

    /** The sprites for the inactive and active states */
    private static final String SPRITE_INACTIVE = "oilcan.png";
    private static final String SPRITE_ACTIVE = "oilslick.png";

    /**
     * Indicates how far behind the player the Oil slick should be created upon
     * using the item.
     */
    private static final int DROP_RANGE = 40;

    /**
     * Indicates how long the active effect should be applied to the Racer upon
     * colliding with one.
     */
    private static final int DURATION = 700;

    /**
     * Storing the item path so we can change the sprite to the active version
     * when the item used used.
     */
    private String itemPath;

    /**
     * This boolean represents whether the item is currently on the map in its
     * inactive (not yet picked up) or active (oil slick) forms.
     */
    private boolean active;

    /**
     * This variable represents the timer for tracking how long the Player has
     * been boosting. Once this counter reaches DURATION, the effect ends.
     */
    private int timer;

    /**
     * Calls the super constructor as well as initially setting the inactive
     * version of the sprite. The itemPath is stored for later use, and the item
     * is set to being inactive.
     * 
     * @param startX
     *            In pixels. Horizontal x-coordinate starting position.
     * @param startY
     *            In pixels. Vertical y-coordinate starting position.
     * @param itemPath
     *            The folder name of where the items are stored.
     * @throws SlickException
     */
    public Oil(double startX, double startY, String itemPath)
            throws SlickException {
        super(startX, startY);
        setSprite(new Image(ASSETS_PATH + itemPath + SPRITE_INACTIVE));

        this.itemPath = itemPath;
        active = false;
        timer = 0;
    }

    /**
     * This method applies the effect of the item. If the item is inactive, the
     * item's effect is just to create an active version of itself on the map to
     * then be removed from the inventory of the Player, as is handled by the
     * first part of this method.
     * 
     * The parts following handle interactions where the active form of the Oil
     * (slick form) has collided with a player and applies the spinning effect,
     * which is common to Tomato as well and therefore held in Item.
     * 
     * @param racer
     *            The Racer to which it should apply the effect.
     * @param mapItems
     *            This is passed so when the item is used, it can be re-added to
     *            the game world in its active form.
     */
    public boolean applyEffect(Racer racer, ArrayList<Item> mapItems)
            throws SlickException {

        /**
         * Indicating that if the item is inactive, using the item simply places
         * the active form of the Oil behind the Player and then immediately
         * returns true, therein being remove from the Player's active effects.
         */
        if (!active) {
            setSprite(new Image(ASSETS_PATH + itemPath + SPRITE_ACTIVE));

            setAngle(racer.getAngle());
            setMapX(racer.getMapX() - getAngle().getXComponent(DROP_RANGE));
            setMapY(racer.getMapY() - getAngle().getYComponent(DROP_RANGE));

            active = true;
            mapItems.add(this);

            return true;
        }

        /**
         * Checking to see whether the active effect should stop being applied
         * or not.
         */
        if (timer > DURATION) {
            return true;
        }

        /**
         * This part applies the active effect; so when a Racer runs into the
         * Oil slick.
         */
        applyOilTomatoEffect(racer);
        timer += 1;

        return false;

    }

    /**
     * Since the Oil slick just sits there on the map and doesn't have any
     * removal condition besides being run into by a player, this just stays
     * empty.
     * 
     * @param world
     *            The world object. Mandatory as per the abstract method in
     *            Item, but not used here.
     */
    public boolean update(World world) {
        return false;
    }

    /**
     * Fairly self explanatory. If the item is active it will return as such.
     * This is used to tell whether it should be interacted with as an item that
     * hasn't been picked up yet, or a dangerous projectile with an active
     * effect.
     */
    public boolean isActive() {
        return active;
    }

}
