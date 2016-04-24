import java.util.ArrayList;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 * <h1>Tomato</h1>
 * <h2>Represents the Tomato item and its effect</h2>
 * 
 * One of the three items that can be used by Player. This has the most
 * complicated behaviour as not only does it have an activated state of
 * existence on the map, but it also moves in this state.
 * 
 * Upon use a tomato projectile is created. This is destroyed upon either
 * collision with terrain or with a Racer. If this collision is with a Racer,
 * the effect described in applyEffect() is applied, which causes it to "spin
 * out" for a set duration.
 * 
 * @author Daniel Porteous porteousd 696965
 */
public class Tomato extends Item {

    /** The sprites for the inactive and active states */
    private static final String SPRITE_INACTIVE = "tomato.png";
    private static final String SPRITE_ACTIVE = "tomato-projectile.png";

    /**
     * Indicates how far in front of the player the Tomato should be created
     * upon using the item.
     */
    private static final int DROP_RANGE = 40;

    /** Speed of travel of the Tomato projectile when used */
    private final double speed = 1.7;

    /**
     * Indicates how long the active effect should be applied to the Racer upon
     * colliding with one.
     */
    private static final int DURATION = 700;

    /**
     * This variable represents the timer for tracking how long the Player has
     * been boosting. Once this counter reaches DURATION, the effect ends.
     */
    private int timer;

    /**
     * Storing the item path so we can change the sprite to the active version
     * when the item used used.
     */
    private String itemPath;

    /**
     * This boolean represents whether the item is currently on the map in its
     * inactive (not yet picked up) or active (projectile) forms.
     */
    private boolean active;

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
    public Tomato(double startX, double startY, String itemPath)
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
     * The parts following handle interactions where the active form of the
     * Tomato (projectile form) has collided with a player and applies the
     * spinning effect, which is common to Oil as well and therefore held in
     * Item.
     * 
     * @param racer
     *            The Racer to which it should apply the effect
     * @param mapItems
     *            This is passed so when the item is used, it can be re-added to
     *            the game world in its active form.
     */
    public boolean applyEffect(Racer racer, ArrayList<Item> mapItems)
            throws SlickException {

        /**
         * This indicates how the Tomato should be interacted with if the item
         * is inactive. Note that this only applies for when used by the Player,
         * since Enemies can't interact with inactive items.
         * 
         * Because the Tomato is removed from the map upon pickup, this part
         * essentially recreates it on the map in its active form. It then
         * immediately returns true, removing it from the hand of the Player, in
         * essence passing control back to the game world.
         */
        if (!active) {
            setSprite(new Image(ASSETS_PATH + itemPath + SPRITE_ACTIVE));

            setAngle(Angle.fromDegrees(racer.getAngle().getDegrees() + 180));
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
         * This part applies the active effect; so when the Tomato projectile
         * collides with a Racer.
         */
        applyOilTomatoEffect(racer);
        timer += 1;

        return false;

    }

    /**
     * This method controls how the tomato behaves in the game world in its
     * active form. Tomato is the only item which has content in this function,
     * since its the only one that moves through the world upon use.
     * 
     * Notably, this method handles whether the Tomato has collided with
     * terrain, in which case it returns true indicating that the item should be
     * removed from the map.
     * 
     * @param world
     *            The world object. Used to check whether the Tomato projectile
     *            has collided with terrain and should therefore be removed.
     */
    public boolean update(World world) {
        // Only does anything here if active of course.
        if (active) {
            setMapX(getMapX() - getAngle().getXComponent(speed));
            setMapY(getMapY() - getAngle().getYComponent(speed));
            if (world.getFriction(getMapX() + getAngle().getXComponent(speed),
                    getMapY() + getAngle().getYComponent(speed)) >= 1) {
                return true;
            }
        }
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
