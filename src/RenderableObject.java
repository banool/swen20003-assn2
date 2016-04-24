import org.newdawn.slick.Image;

/**
 * <h1>RenderableObject</h1>
 * <h2>Abstract class from which all other rendered objects are derived</h2> 
 *
 * The highest level class representing objects rendered into the game world.
 * All Racers and Items that exist in the game world are children of this class.
 * 
 * Represents a very basic object to be rendered in the world, holding only
 * position and angle. Besides the constructor and getters/setters, the only
 * other method in this class is the render method, which renders the
 * RenderableObject to the screen in the given location at the given angle.
 * 
 * @author Daniel Porteous porteousd 696965 *
 */
public abstract class RenderableObject implements GlobalHelper {

    /**
     * Constant representing the starting angle of an Item. Initially set to 0
     * (north). This is just a starting point, as the angle class is immutable
     * and will have to be re-instantiated statically every time we need it.
     */
    private static final float STARTING_ANGLE = 0.0F;

    /** The image holding the graphic that will be rendered to the screen */
    private Image sprite;

    /**
     * Each RenderableObject mandatorily has an angle, even if it is just set to
     * the standard of STARTING_ANGLE and left.
     */
    private Angle angle;

    /**
     * Represents the x coordinate of the RenderableObject in pixels. This is
     * stored as a double to facilitate more accurate calculations of position.
     * Only at render time are these values converted to ints.
     */
    private double mapX;

    /** Represents the x coordinate of the RenderableObject in pixels. */
    private double mapY;

    /**
     * Sets the most basic characteristics of a RenderableObject, namely its
     * position and angle. Anything more specific will be handled in the
     * subclasses extended from this one.
     * 
     * @param startX
     *            In pixels. Horizontal x-coordinate starting position.
     * @param startY
     *            In pixels. Vertical y-coordinate starting position.
     */
    public RenderableObject(double startX, double startY) {
        setAngle(Angle.fromRadians(STARTING_ANGLE));
        setMapX(startX);
        setMapY(startY);
    }

    /**
     * Renders the object onto the screen based on the position of the camera
     * viewport/position. This is calculated by taking the position of the
     * object minus the current position of the camera, resulting in a position
     * relative to the screen.
     * 
     * @param camX
     *            The current "position" of the camera (not that the camera is
     *            rendered, it simply exists in the world) on the horizontal
     *            plane.
     * @param camY
     *            As above but on the vertical y plane.
     */
    public void render(double camX, double camY) {
        getSprite().setRotation((float) getAngle().getDegrees());
        getSprite().drawCentered((float) (getMapX() - camX),
                (float) (getMapY() - camY));
    }

    
    /**
     * Standard getter that returns the horizontal mapX variable.
     * 
     * @return mapX of the object in pixels.
     */
    public double getMapX() {
        return mapX;
    }

    /**
     * Standard setter that sets the horizontal mapX variable.
     * 
     * @param mapX
     *            The mapX to set.
     */
    public void setMapX(double mapX) {
        this.mapX = mapX;
    }

    /**
     * Standard getter that returns the vertical mapY variable.
     * 
     * @return mapY of the object in pixels.
     */
    public double getMapY() {
        return mapY;
    }

    /**
     * Standard setter that sets the vertical mapY variable.
     * 
     * @param mapY
     *            The mapY to set
     */
    public void setMapY(double mapY) {
        this.mapY = mapY;
    }

    /**
     * Standard getter that returns the image sprite representing the object.
     * 
     * @return Sprite of the object as an Image.
     */
    public Image getSprite() {
        return sprite;
    }

    /**
     * Standard setter that sets the sprite of the object to the given Image.
     * 
     * @param sprite
     *            The Image object to set.
     */
    public void setSprite(Image sprite) {
        this.sprite = sprite;
    }

    /**
     * Standard getter that returns the Image sprite representing the object.
     * 
     * @return Current Angle of the RenderableObject.
     */
    public Angle getAngle() {
        return angle;
    }

    /**
     * @param angle
     *            The angle to set.
     */
    public void setAngle(Angle angle) {
        this.angle = angle;
    }

}
