import java.util.ArrayList;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

/**
 * <h1>World</h1>
 * <h2>Represents the entire game world</h2>
 * 
 * Every other object in the main game frame is instantiated here. This includes
 * the player, camera and map. Special emphasis is placed on the map, which is
 * handled by and rendered inside the world class (while the other classes
 * render for themselves).
 * 
 * This class also calls the update and render methods for each of the other
 * classes, including the Player, Items and Enemies, as well as the Panel, and
 * could be considered the foundation class of the whole application.
 * 
 * @author Daniel Porteous porteousd
 * @since 2015-10-14
 *
 */
public class World implements GlobalHelper {

    /** Defining of other constants specific to the World class */
    private static final String MAP_FILE = "map.tmx";
    private final int FINISH_Y = 1026;

    /** Number of tiles wide to render */
    public static final int NUM_TILES_WIDE = 24;
    /** Number of tiles high to render */
    public static final int NUM_TILES_HIGH = 18;

    /** Location of the file holding the waypoint information */
    private static final String WAYPOINTS_FILE = DATA_PATH + "waypoints.txt";

    /** Initialising a TiledMap object in to which we will load the map. */
    private TiledMap map;

    /** The player object, through which we render our kart. */
    private Player player;

    /** Array holding all of the Enemies */
    private Enemy[] enemies;

    /**
     * An aggregate array that holds all Racers. This consists of all the
     * Enemies plus the player.
     */
    private Racer[] racers;

    /**
     * This holds each of the mapItems that have been instantiated on the whole
     * map, whether in the screen or not.
     */
    private ArrayList<Item> mapItems;

    /**
     * Array of array of doubles which will hold the waypoint information that
     * the Enemies will follow.
     */
    private double[][] waypoints;

    /**
     * Variable that will be set to true in update so that the render method can
     * determine whether to display the end of game message,
     */
    private boolean gameOver;

    /** The current rank of the player, to be displayed in panel */
    private int playerPos;

    /** The camera object which will follow the player. */
    private Camera cam;

    /**
     * Panel which is displayed in the bottom right that shows the player's
     * current rank and held item.
     */
    private Panel panel;

    /**
     * Create a new World object.
     * 
     * @param screenWidth
     *            The width of the screen in pixels.
     * @param screenHeight
     *            The height of the screen in pixels.
     * @throws SlickException
     */
    public World(int screenWidth, int screenHeight) throws SlickException {

        /* Creating the map object */
        map = new TiledMap(ASSETS_PATH + MAP_FILE, ASSETS_PATH);

        /* Creating the player at the given starting position. */
        player = new Player();

        /*
         * Calling the static method of Enemy, createEnemies(), which creates
         * and returns a static array holding all the enemies.
         */
        enemies = Enemy.createEnemies();

        /*
         * Here we make an aggregate array of Racers that holds both the Player
         * and the Enemies. This is useful to check, for example, whether any of
         * the Racers have collided with one another.
         */
        racers = new Racer[enemies.length + 1];
        racers[0] = player;
        for (int i = 0; i < enemies.length; i++) {
            racers[i + 1] = enemies[i];
        }

        // Here we get the raw data line by line from the waypoints file.
        String[] waypointsData = GlobalHelper.readTextDataFile(WAYPOINTS_FILE);

        /*
         * We process the waypoint data into usable information. This involves
         * creating an array of 2 item arrays of doubles, each item holding the
         * x and y coordinates of a waypoint.
         */
        waypoints = new double[waypointsData.length][2];
        for (int i = 0; i < waypointsData.length; i++) {
            String[] parts = waypointsData[i].split("\\s+");
            waypoints[i][0] = Double.parseDouble(parts[0]);
            waypoints[i][1] = Double.parseDouble(parts[1]);
        }

        /* Creating the camera. */
        cam = new Camera(player.getMapX(), player.getMapY(), screenWidth,
                screenHeight, NUM_TILES_WIDE, NUM_TILES_HIGH);

        // Creating the items on the map.
        mapItems = Item.createItems();

        // Instantiating the Panel.
        panel = new Panel(screenWidth, screenHeight);

        // Indicating that the game is not yet finished.
        gameOver = false;
    }

    /**
     * Update the game state for a frame.
     * 
     * @param rotateDir
     *            The player's direction of rotation (-1 for anti-clockwise, 1
     *            for clockwise, or 0).
     * @param moveDir
     *            The player's movement in the car's axis (-1, 0 or 1).
     * @param useItem
     *            True if the useItem key is held down, false otherwise.
     */
    public void update(double rotateDir, double moveDir, boolean useItem)
            throws SlickException {

        /*
         * Checking whether the player has crossed the finish line. If so, we
         * set the acceleration to 0 and lock the steering forwards. We also set
         * the gameOver variable to true so we know to display the end game
         * message in the render method of this class. If the game is not over,
         * we keep updating the ranking of the player.
         * 
         * We also remove the ability from the player to use items if the race
         * is over.
         */
        if (player.getMapY() < FINISH_Y) {
            player.setAcceleration(0.0);
            rotateDir = 0.0;
            gameOver = true;
        } else {
            playerPos = player.getPlayerPos(enemies);
            /*
             * If the use item key is held down, left ctrl by default, it calls
             * the player.UserHeldItem() method, which in turn activates the
             * held item.
             */
            if (useItem) {
                player.useHeldItem();
            }
        }

        /*
         * Calls the player.update method. Importantly, the world object itself
         * is passed into this method such that the friction of the current
         * player position can be calculated from inside the player.
         */
        player.update(rotateDir, moveDir, this, mapItems, racers);

        /*
         * "Moving the viewport", which is essentially just updating the
         * camera's position based on the new position of the player. This could
         * easily follow a different Racer with a line like:
         * cam.update(enemies[0].getMapX(), enemies[0].getMapY());
         */
        cam.update(player.getMapX(), player.getMapY());

        /*
         * Updates all the items on the map, active or otherwise. This generally
         * entails calling the update method of each item, held individually for
         * each subclass of Item. Namely, we're checking to see if any active
         * items (e.g. tomato projectile) should be removed off the map.
         */
        Item.updateItems(mapItems, this);

        /*
         * Updates all the enemies. While this calls the update method from the
         * Racer superclass, just like for player, it also handles Enemy
         * specific behaviour such as following the waypoints, as well as the
         * individual behaviour for each subclass of Enemy, if it has any.
         */
        Enemy.updateEnemies(this, racers, enemies, player, mapItems, waypoints);

    }

    /**
     * Render the entire screen, so it reflects the current game state.
     * 
     * @param g
     *            The Slick graphics object, used for drawing.
     */
    public void render(Graphics g) throws SlickException {

        /*
         * Renders the map. Obviously we do this first, as order of rendering is
         * important such that nothing is rendered under something else
         * unintentionally and thereby is accidentally hidden.
         */
        map.render((int) cam.getX(), (int) cam.getY(), cam.getSX(), cam.getSY(),
                NUM_TILES_WIDE, NUM_TILES_HIGH);

        // Calls the render method of player to draw the kart to the screen.
        player.render(cam.getMapX(), cam.getMapY());

        /*
         * Calls the static method of Enemy that loops through each instantiated
         * Enemy and renders them to the screen.
         */
        Enemy.renderEnemies(enemies, cam.getMapX(), cam.getMapY());

        /*
         * Calls the static method of Item that loops through all the Items and
         * renders them to the screen.
         */
        Item.renderItems(mapItems, cam.getMapX(), cam.getMapY());

        // Finally, we render the panel, as this should go on top
        panel.render(g, playerPos, player.getHeldItem());

        /*
         * Checks if the game has been finished, and if so calls the end of game
         * message as held by the superclass Game.
         */
        if (gameOver)
            Game.displayEndMessage(playerPos, g);

    }

    /**
     * Uses the position of the camera to find out where the player is. While
     * the player object does have some notion of where it is on a global, pixel
     * based scale, we will use the getcenterTile() method from the Camera class
     * as this class has greater awareness its tile based position on the map.
     * 
     * This position is then used to get the friction of the tile on which the
     * player is using the getTileId() and getTileProperty() methods in
     * previously instantiating TiledMap object, map.
     * 
     * @param x
     *            In pixels. Horizontal x-coordinate position.
     * @param y
     *            In pixels. Vertical y-coordinate position.
     * @return Returns a double between 0 and 1 giving friction at the target
     *         tile.
     */
    public double getFriction(double x, double y) {
        int tileID = map.getTileId((int) (x / TILE_WIDTH),
                (int) (y / TILE_WIDTH), 0);
        return Double
                .parseDouble(map.getTileProperty(tileID, "friction", null));
    }
}
