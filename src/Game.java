
/* SWEN20003 Object Oriented Software Development
 * Shadow Kart
 * Author: Matt Giuca <mgiuca>
 */

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Input;

/**
 * Main class for the Shadow Kart Game engine. Handles initialisation, input and
 * rendering.
 */
public class Game extends BasicGame {

    /** The game state. */
    private World world;

    /** Screen width, in pixels. */
    private static final int SCREENWIDTH = 800;
    /** Screen height, in pixels. */
    private static final int SCREENHEIGHT = 600;
    
    /** Start of the end game message. */
    private static final String endMessage = "You came ";

    /** Create a new Game object. */
    public Game() {
        super("Shadow Kart");
    }

    /**
     * Initialise the game state.
     * 
     * @param gc
     *            The Slick game container object.
     */
    @Override
    public void init(GameContainer gc) throws SlickException {
        world = new World(SCREENWIDTH, SCREENHEIGHT);
    }

    /**
     * Update the game state for a frame.
     * 
     * @param gc
     *            The Slick game container object.
     * @param delta
     *            Time passed since last frame (milliseconds).
     */
    @Override
    public void update(GameContainer gc, int delta) throws SlickException {
        // Get data about the current input (keyboard state).
        Input input = gc.getInput();

        // Update the player's rotation and position based on key presses.
        double rotateDir = 0;
        double moveDir = 0;
        if (input.isKeyDown(Input.KEY_DOWN) || input.isKeyDown(Input.KEY_S))
            moveDir -= 1;
        if (input.isKeyDown(Input.KEY_UP) || input.isKeyDown(Input.KEY_W))
            moveDir += 1;
        if (input.isKeyDown(Input.KEY_LEFT) || input.isKeyDown(Input.KEY_A))
            rotateDir -= 1;
        if (input.isKeyDown(Input.KEY_RIGHT) || input.isKeyDown(Input.KEY_D))
            rotateDir += 1;
        boolean useItem = input.isKeyDown(Input.KEY_LCONTROL)
                || input.isKeyDown(Input.KEY_RCONTROL);

        // This allows the game to quit without it crashing.
        if (input.isKeyDown(Input.KEY_Q))
            gc.exit();

        // Let World.update decide what to do with this data.
        for (int i = 0; i < delta; i++)
            world.update(rotateDir, moveDir, useItem);
    }

    /**
     * Render the entire screen, so it reflects the current game state.
     * 
     * @param gc
     *            The Slick game container object.
     * @param g
     *            The Slick graphics object, used for drawing.
     */
    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {
        // Let World.render handle the rendering.
        world.render(g);
    }

    /**
     * Method to be called from world when the player crosses the finish line.
     * Displays the end of game message with their rank.
     * 
     * @param ranking
     *            An integer between 1 and 4 inclusive.
     * @param g
     *            The slick graphics object with which we display the text.
     */
    public static void displayEndMessage(int ranking, Graphics g) {
        g.drawString(endMessage + Panel.ordinal(ranking) + "!", SCREENWIDTH / 2,
                SCREENHEIGHT / 2);
    }

    /**
     * Start-up method. Creates the game and runs it.
     * 
     * @param args
     *            Command-line arguments (ignored).
     */
    public static void main(String[] args) throws SlickException {
        AppGameContainer app = new AppGameContainer(new Game());
        // setShowFPS(true), to show frames-per-second.
        app.setShowFPS(false);
        app.setDisplayMode(SCREENWIDTH, SCREENHEIGHT, false);
        app.start();
    }
}
