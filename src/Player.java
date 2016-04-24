import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import java.util.ArrayList;

/**
 * <h1>Player</h1>
 * <h2>Represents the player of the game</h2>
 * 
 * Handles things specific to the player. This includes implementation allowing
 * the player to pick up and use items, as well as a mechanism for figuring out
 * the rank/position of the player throughout the race.
 * 
 * @author Daniel Porteous porteousd 696965
 *
 */
public class Player extends Racer {

    /**
     * Defining the starting location of the player. These are the starting
     * coordinates as given in the spec.
     */
    private static final int START_X = 1332;
    private static final int START_Y = 13086;

    /** The sprite that the player users */
    private final String KART_NAME = "donkey.png";

    /**
     * This variable is specific to Player, and represents the item currently
     * held by Player awaiting use. Set to null by default in the constructor.
     */
    private Item heldItem;

    /**
     * Standard constructor for Player. Places the Player on the map and sets
     * their sprite. Also sets the currently held item to null.
     */
    public Player() throws SlickException {
        super(START_X, START_Y);
        setSprite(new Image(
                ASSETS_PATH + "/" + getKartsPath() + "/" + KART_NAME));
        heldItem = null;
    }

    /**
     * Goes through each item, checking if the Player is within range of it. If
     * so, the behaviour changes based on whether the Item is active or not.
     * 
     * If the Item is active, it will be added to the activeItems list and
     * removed from the map, just like would apply if it were a regular Enemy.
     * 
     * However if the item is not active, the item is removed and placed into
     * the heldItem variable that is unique to Player. This means that Player is
     * the only Racer that can interact with inactive items on the map, and as
     * such use them.
     * 
     * @param mapItems
     *            A list containing all the items currently active on the map.
     */
    public void checkItemCollision(ArrayList<Item> mapItems) {
        Item tempItem;

        for (int i = 0; i < mapItems.size(); i++) {

            // Checking to see if the Item is within pickup range of the Player.
            if (GlobalHelper.getHypotenuse(
                    getMapX() - mapItems.get(i).getMapX(), getMapY() - mapItems
                            .get(i).getMapY()) < getItemCollisionRadius()) {
                tempItem = mapItems.get(i);

                // If the item is active, add it to activeItems.
                if (tempItem.isActive()) {
                    getActiveItems().add(tempItem);
                }

                /*
                 * If the item isn't active, pick it up and store it in heldItem
                 * for later use.
                 */
                else {
                    heldItem = tempItem;
                }

                // Finally, remove the item from the map.
                mapItems.remove(i);
                i--;
            }
        }
    }

    /**
     * This method is called by the World class so it knows which item, if any,
     * to have displayed in the panel down the bottom right of the screen.
     * 
     * @return The currently held item, if any.
     */
    public Item getHeldItem() {
        return heldItem;
    }

    /**
     * This method is called by world upon receiving the key input for using the
     * currently held item. It simply adds the held item, if any, to the active
     * effects for the Player and removes it from their "hand".
     */
    public void useHeldItem() {
        if (heldItem != null) {
            getActiveItems().add(heldItem);
            heldItem = null;
        }
    }

    /**
     * Cycles through all the enemies, adding one to the current player rank if
     * that enemy has a lower y-coordinate than the player.
     * 
     * @param enemies
     *            An array containing the enemies on the map.
     * @return Returns an integer between 1 and 1 + the number of players, in
     *         this case 4.
     */
    public int getPlayerPos(Enemy[] enemies) {
        int pos = 1;
        for (Enemy enemy : enemies) {
            if (enemy.getMapY() < getMapY()) {
                pos++;
            }
        }
        return pos;
    }

}
