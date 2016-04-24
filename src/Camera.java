
/**
 * <h1>Camera</h1>
 * <h2>Used for following the player around the World</h2>
 * 
 * This is a simple class which keeps track of the player as they move around
 * the map, making sure the viewport is centered on them.
 * 
 * It provides important "translations" between the frequently confusing pixel
 * and tile values that the TiledMap class likes to use in its render() method.
 * 
 * @author Daniel Porteous porteousd
 * @since 2015-09-11
 *
 */
public class Camera implements GlobalHelper {

    /**
     * The offset indicating that the screen is being rendered 1 extra tile on
     * each side, or two in total
     */
    public static final int SCREEN_OFFSET = 1;

    /**
     * These are the global coordinates of the camera. These are necessary to
     * know where to print various objects to the screen.
     */
    private double mapX;
    private double mapY;

    /**
     * These variables aren't relative to the map like the Player class' are.
     * These instead are essentially instructions for the render call in World.
     * As such, x and y are pixels relative to the screen and sx and sy are
     * tiles relative to the map.
     */
    private double x;
    private double y;

    /** Defining our variables for tile co-ordinates on the map */
    private int sx;
    private int sy;

    /** Screen width and height */
    private int screenWidth;
    private int screenHeight;

    /** Number of tiles to capture in the viewport */
    private int numTilesWide;
    private int numTilesHigh;

    /**
     * This constructor receives a lot of arguments, which it then stores, but
     * this is preferable to having to pass them every single time update is
     * called since they're all constants.
     * 
     * @param startX
     *            The starting x-coordinate of the Camera. This will likely be
     *            that of the Player.
     * @param startY
     *            The starting y-coordinate of the Camera. This will likely be
     *            that of the Player.
     * @param screenWidth
     *            Screen width in pixels.
     * @param screenHeight
     *            Screen height in pixels.
     * @param numTilesWide
     *            Number of tiles wide to be rendered.
     * @param numTilesHigh
     *            Number of tiles high to be rendered.
     */
    public Camera(double startX, double startY, int screenWidth,
            int screenHeight, int numTilesWide, int numTilesHigh) {
        mapX = startX - screenWidth / 2;
        mapY = startY - screenHeight / 2;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.numTilesWide = numTilesWide;
        this.numTilesHigh = numTilesHigh;
    }

    /**
     * Takes in the current global/map wide position of the kart and resolves
     * this into the x and y coordinates of the screen and the tile position
     * from which to start rendering.
     * 
     * @param x
     *            In pixels. The position of the target on the horizontal plane.
     * @param y
     *            In pixels. The position of the target on the vertical plane.
     */
    public void update(double x, double y) {

        // Updating the "location" of the Camera on the map.
        mapX = x - screenWidth / 2;
        mapY = y - screenHeight / 2;

        /*
         * Getting the pixel screen offset so we have smooth camera movement.
         * This ranges between 0 and -36.0.
         */
        this.x = -(x % TILE_WIDTH);
        this.y = -(y % TILE_WIDTH);
        /*
         * We get the pixel position on the screen divided by the tile width in
         * order to get the tile position. However this is for the center of the
         * screen. To get the top left as is required, we offset it to the
         * middle by subtracting half the screen and adding 2 so that the edge
         * render offset is accounted for.
         */
        sx = (int) (x / TILE_WIDTH - numTilesWide / 2 + SCREEN_OFFSET);
        sy = (int) (y / TILE_WIDTH - numTilesHigh / 2 + SCREEN_OFFSET);
    }

    
    /**
     * Standard getter that returns x (this is relative to the screen).
     * 
     * @return x The x to be returned.
     */
    public double getX() {
        return x;
    }

    /**
     * Standard getter that returns y (this is relative to the screen).
     * 
     * @return y The y to be returned.
     */
    public double getY() {
        return y;
    }

    /**
     * Standard getter that returns sx, the horizontal tile at which to start
     * rendering.
     * 
     * @return sx The sx to be returned.
     */
    public int getSX() {
        return sx;
    }

    /**
     * Standard getter that returns sy, the vertical tile at which to start
     * rendering.
     * 
     * @return sy The sy to be returned.
     */
    public int getSY() {
        return sy;
    }

    /**
     * Standard getter that returns mapX, the global position of the camera on
     * the horizontal plane.
     * 
     * @return mapX The mapX to be returned.
     */
    public double getMapX() {
        return mapX;
    }

    /**
     * Standard getter that returns mapY, the global position of the camera on
     * the vertical plane.
     * 
     * @return mapY The mapY to be returned.
     */
    public double getMapY() {
        return mapY;
    }
}
