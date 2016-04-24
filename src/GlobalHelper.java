import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * <h1>Global Constants Interface</h1>
 * 
 * Holds useful methods and variables that are globally used by multiple
 * classes.
 * 
 * @author Daniel Porteous porteousd
 * @since 2015-09-11
 *
 */
public interface GlobalHelper {

    /**
     * Location of the "assets" and "data" directory. Consider moving these to
     * world.
     */
    public static final String ASSETS_PATH = "assets/";
    public static final String DATA_PATH = "data/";

    /** Width of each tile in pixels */
    public static final int TILE_WIDTH = 36;

    /**
     * Calculates the distance between two points using pythagoras.
     * 
     * @param a
     *            The first point, potentially an x distance.
     * @param b
     *            The second point, potentially a y distance.
     * @return Returns the "c" value, the hypotenuse that these two points
     *         create.
     */
    public static double getHypotenuse(double a, double b) {
        return Math.sqrt(Math.pow(Math.abs(a), 2) + Math.pow(Math.abs(b), 2));
    }

    /** Marker indicating when we know to start reading from the input file */
    static final String START_READING = "START";

    /**
     * This method returns the data section of a text file that is read in. Each
     * index in the array of strings is a new line. The splitting of this string
     * is left up to the method which processes the data in case each data file
     * is slightly different in layout.
     * 
     * In order to return a static array, it has to make two sweeps of file. The
     * first counts how many items there are, therefore allowing us to make the
     * right sized array, and the second then fills this array. While efficiency
     * isn't of the utmost concern, I figure that file reading is fairly cheap
     * so this solution allows us to avoid a dynamic array in exchange for an
     * (albeit slightly) more efficient static array.
     * 
     * Doesn't start reading the lines into the output array until the
     * START_READING marker is encountered.
     * 
     * 
     * Code credit for the BufferedReader snippet to: mkyong
     * http://www.mkyong.com/java/how-to-read-file-from-java-bufferedreader-
     * example/
     * 
     * @param targetFile
     *            A string containing the file path of the text file to be read.
     * @return output The array of Strings, one item for each line.
     */
    public static String[] readTextDataFile(String targetFile) {

        int numItems = 0;
        boolean reading = false;
        String sCurrentLine;

        /*
         * Making the first sweep of the target file. This will give us the
         * number of items that we need to read into our output array of
         * Strings.
         */
        try (BufferedReader br = new BufferedReader(
                new FileReader(targetFile))) {

            while ((sCurrentLine = br.readLine()) != null) {
                if (sCurrentLine.equals(START_READING)) {
                    reading = true;
                }
                if (reading) {
                    numItems++;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
         * Instantiating the output array of Strings based on the number of
         * items we've just calculated.
         */
        String[] output = new String[numItems - 1];

        /*
         * Second sweep of the file in which we actually populate the output
         * array, starting of course from when we hit the start marker.
         */
        int currentIndex = 0;
        reading = false;

        try (BufferedReader br = new BufferedReader(
                new FileReader(targetFile))) {

            while ((sCurrentLine = br.readLine()) != null) {

                /*
                 * Moving down the input text file until we get to the actual
                 * data, which will be denoted by the START_READING line.
                 */
                if (sCurrentLine.equals(START_READING)) {
                    reading = true;
                    continue;
                }

                if (reading) {
                    output[currentIndex] = sCurrentLine;
                    currentIndex += 1;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;
    }

}
