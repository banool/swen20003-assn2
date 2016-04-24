import java.util.ArrayList;
import org.newdawn.slick.SlickException;

/**
 * <h1>Item</h1>
 * <h2>Abstract class from which all concrete Item classes inherit.</h2>
 * 
 * The main role of this class is to handle initially creating the Items on the
 * map and then managing their update and render each game turn.
 * 
 * A series of abstract methods are also defined to ensure that these tasks can
 * be carried out in a centralised manner with the static methods here in Item.
 * 
 * Only two non-static methods are defined here. One which renders the Item
 * explicitly onto the screen. This is so Panel can display the currently held
 * item. The second applies the Tomato/Oil effect upon hitting their active
 * forms, as these two effects are exactly the same.
 * 
 * @author Daniel Porteous porteousd 696965
 */
public abstract class Item extends RenderableObject {

    public Item(double startX, double startY) {
        super(startX, startY);
    }

    /**
     * Constants specific to Item, specifically for the instantiation of new
     * items for the createItems method.
     */
    private static final String ITEMS_PATH = "items/";

    /** File locations from which we will read our data and helper variables */
    private static final String ITEM_LOCATIONS_FILE = DATA_PATH + "items.txt";

    /**
     * Item names for the reading in of the items. These are used in the
     * createItems() method.
     */
    private static final String OIL = "OilCan";
    private static final String TOMATO = "Tomato";
    private static final String BOOST = "Boost";

    /** Angular modifier for the Oil / Tomato effect */
    private static final double OIL_TOMATO_ANGLULAR_MODIFIER = 0.008;

    /**
     * Specifying that each Item derived from this class must have a useEffect
     * method which can be called when the Item is collided with.
     * 
     * @param racer
     *            The racer with which the collision occurs.
     * @param mapItems
     *            All the items on the map, so a new item can be added when the
     *            active effect of an item is activated.
     * @return Returns true if the effect duration has completed (i.e. Boost
     *         duration or duration for which to apply the Oil/Tomato effect).
     * @throws SlickException
     */
    public abstract boolean applyEffect(Racer racer, ArrayList<Item> mapItems)
            throws SlickException;

    /**
     * This method returns true if the item is active on the map. This is useful
     * because Enemies can only interact with active items.
     * 
     * @return Returns true if the item is in its active state on the map.
     */
    public abstract boolean isActive();

    /**
     * This function is used for Items for which we need to know if we need to
     * delete them from the map based on a condition specific to that item. As
     * such, Oil and Boost will always return false, because the Boost has no
     * active presence on the map and the Oil slick is removed by the collision
     * methods in Racer. The tomato, however, needs to be removed upon colliding
     * with terrain, and as such will have a method here that checks for that.
     * It also updates its position as it flies across the map
     * 
     * @param world
     *            The world object so terrain collisions can be checked.
     * @return Returns true if the item being updated needs to be removed from
     *         the map, used only for Tomato since its the only item with an
     *         additional removal condition that isn't collision with a Racer,
     *         namely collision with terrain.
     */
    public abstract boolean update(World world);

    /**
     * This method is called statically from world upon initialisation. It
     * creates all the items by reading them in from the items.txt data file and
     * then returning them in an ArrayList.
     * 
     * @return An ArrayList containing all the items on the map.
     * @throws SlickException
     */
    public static ArrayList<Item> createItems() throws SlickException {

        String[] dataFileContent = GlobalHelper
                .readTextDataFile(ITEM_LOCATIONS_FILE);

        ArrayList<Item> items = new ArrayList<Item>(dataFileContent.length);

        String[] parts;
        String name;
        int startX;
        int startY;

        for (String line : dataFileContent) {
            /*
             * This splits the line based on space; the number of spaces aren't
             * important. This allows for a more nicely formatted items.txt file
             * instead of having just once space between each piece of
             * information on a line.
             */
            parts = line.split("\\s+");
            name = parts[0];
            startX = Integer.parseInt(parts[1]);
            startY = Integer.parseInt(parts[2]);

            // The first part of the split determines which Item will be made.
            if (name.equals(OIL)) {
                items.add(new Oil(startX, startY, ITEMS_PATH));
            } else if (name.equals(TOMATO)) {
                items.add(new Tomato(startX, startY, ITEMS_PATH));
            } else if (name.equals(BOOST)) {
                items.add(new Boost(startX, startY, ITEMS_PATH));
            } else {
                System.out.println(
                        "Something has gone horribly wrong.\nCheck your data"
                                + " file for errors...");
                System.exit(-1);
            }
        }

        return items;
    }

    /**
     * Draws the Item explicitly to given coordinates on the screen. This is
     * used in Panel to draw the item in the circle in the bottom right.
     * 
     * @param x
     *            The x-coordinate to draw the top left corner of the Item to.
     * @param y
     *            The y-coordinate to draw the top left corner of the Item to.
     */
    public void renderExplicitly(double x, double y) {
        getSprite().draw((float) x, (float) y);
    }

    /**
     * This method is passed the items on the map from World, as well as world
     * itself. The update method for each item is then called. If the update
     * method returns true, that means the Item is signaling that something has
     * happened to it meaning that it needs to be removed from the map, to which
     * we oblige.
     * 
     * @param mapItems
     *            An ArrayList containing the items on the map.
     * @param world
     *            The world object. This is passed so items can check for
     *            terrain collisions and such.
     */
    public static void updateItems(ArrayList<Item> mapItems, World world) {
        for (int i = 0; i < mapItems.size(); i++) {
            if (mapItems.get(i).update(world)) {
                mapItems.remove(i);
                i--;
            }
        }
    }

    /**
     * Currently this just renders every item in the game world without
     * consideration for if they are actually in the screen frame. Calculations
     * could be introduced which calculates whether the item is in the game
     * window and therefore if it needs to be rendered. However, calculating
     * whether an item is in the game window or not for each item could end up
     * being more expensive than just rendering them all considering the fairly
     * low cost of rendering a small 2D sprite. With this in mind, it's probably
     * lighter weight to keep the method as is.
     * 
     * @param mapItems
     *            The items currently on the map.
     * @param camX
     *            Current x-coordinate of the Camera.
     * @param camY
     *            Current y-coordinate of the Camera.
     */
    public static void renderItems(ArrayList<Item> mapItems, double camX,
            double camY) {
        for (Item item : mapItems) {
            item.render(camX, camY);
        }
    }

    /**
     * This effect is held here in Item because it is common to both Oil and
     * Tomato. Simply applies the acceleration and turning lock and
     * angularModifier specified in the spec.
     * 
     * @param racer
     *            Racer to which the effect should be applied
     */
    public void applyOilTomatoEffect(Racer racer) {
        racer.setAngularModifier(OIL_TOMATO_ANGLULAR_MODIFIER);
        racer.setRotateDir(1.0);
        racer.setMoveDir(1.0);
    }

}
