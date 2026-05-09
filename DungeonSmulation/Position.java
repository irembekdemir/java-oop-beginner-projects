/**
 * Position class represents the state in the dungeon grid for a specific coordinate.
 * This class is used to track the player's location and the health status during the search process for path.
 * @author irem bekdemir
 * @version 1.0
*/
public class Position {
    int r, c; // row and column indices
    int remainingHealth;

    /**
     * Constructs a new Position with coordinates(r,c) and health data.
     * @param r row index.
     * @param c column index.
     * @param remainingHealth amount of health at this state.
     */
    public Position(int r, int c, int remainingHealth) {
        this.r = r;
        this.c = c;
        this.remainingHealth = remainingHealth;
    }
    
    @Override
    public String toString() {
        return "(" + r + "," + c + "," + remainingHealth + ")"; // returns a formatted string of the position (row, column, health data)
    }
}