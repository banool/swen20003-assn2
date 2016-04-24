/* SWEN20003 Object Oriented Software Development
 * Shadow Kart
 * Author: Matt Giuca <mgiuca>
 */

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/** 
 * The status panel.
 * Renders itself to a fixed position in the bottom-right corner of the
 * screen, displaying the player's race ranking and item. (See the render()
 * method.)
 */
public class Panel implements GlobalHelper
{
    /** Image for the panel background. */
    private Image panel;
    
    /** Private versions of screen width and height.
     *  We do this instead of calling it publicly from Game
     *  in order to preserve encapsulation.
     */
    private int screenWidth;
    private int screenHeight;

    /**
     * Creates a new panel.
     * 
     * @param screenWidth Width of the screen in pixels.
     * @param screenHeight Height of the screen in pixels.
     * @throws SlickException
     */
    public Panel(int screenWidth, int screenHeight) throws SlickException
    {
        panel = new Image(ASSETS_PATH + "/panel.png");
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    /** 
     * Turn a cardinal number into an ordinal.
     * For example, takes a ranking such as 2, and returns a String "2nd".
     * Note: Won't work for numbers greater than 20.
     * 
     * @param ranking The current numerical integer rank of the Player. 
     * @return The ordinal rank of the player, should be between 1 and 4.
     */
    public static String ordinal(int ranking)
    {
        String rank_string = Integer.toString(ranking);
        switch (ranking)
        {
            case 1:
                rank_string += "st";
                break;
            case 2:
                rank_string += "nd";
                break;
            case 3:
                rank_string += "rd";
                break;
            default:
                rank_string += "th";
        }
        return rank_string;
    }

    /** Renders the status panel for the player.
     * @param g The current Slick graphics context.
     * @param ranking The player's current position in the race
     *  (1 = 1st, 2 = 2nd, etc).
     * @param item The player's currently-held item, or null.
     */
    public void render(Graphics g, int ranking, Item item)
    {
        // Variables for layout
        int panel_left;             // Left x coordinate of panel
        int panel_top;              // Top y coordinate of panel

        panel_left = screenWidth - 86;
        panel_top = screenHeight - 88;

        // Panel background image
        panel.draw(panel_left, panel_top);

        // Display the player's ranking
        g.drawString(ordinal(ranking), panel_left + 14, panel_top + 43);

        // Display the player's current item, if any
        if (item != null)
            item.renderExplicitly(panel_left + 32, panel_top);
    }
}
