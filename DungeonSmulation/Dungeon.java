/**
 * Dungeon class manages the structure of the dungeon.
 * Stores the room grid and provides methods to initialize and access rooms based on the given coordibates.
 * @author irem bekdemir
 * @verison 1.0
 */

public class Dungeon {

    private Room[][] grid;
    private int rows, cols;

    /**
     * Construction of a new Dungeon with specified dimensions.
     * @param r number of rows 
     * @param c number of columns 
     */
    public Dungeon(int r, int c) {
        this.rows = r;
        this.cols = c;
        this.grid = new Room[r][c]; //forms the room grid. null at first.
    }

    /**
     * Fills a specific cell in the grid with a room object based on the given type character from the input file.
     * @param r the row index.
     * @param c the column index.
     * @param type is the char representing the room type (H, T, M, K, E, X and A, B, L).
     */ 
    public void setRoom(int r, int c, char type) {

        switch (type) {

            case 'H': 
                grid[r][c] = new Room.HealingRoom(); 
                break;
            case 'T': 
                grid[r][c] = new Room.TrapRoom(); 
                break;
            case 'M': 
                grid[r][c] = new Room.MonsterRoom(); 
                break;
            case 'K': 
                grid[r][c] = new Room.KeyRoom(); 
                break;
            case 'E': 
                grid[r][c] = new Room.EmptyRoom(); 
                break;
            case 'X': 
                grid[r][c] = new Room.ExitRoom(); 
                break;

            case 'A':
            case 'B':
            case 'L':
                grid[r][c] = new Room.ArtifactRoom(type);
                break;
        }
    }

    public Room getRoom(int r, int c) {  //filled, not null anymore.
        return grid[r][c];
    }

    public int getRows() { return rows; } 
    public int getCols() { return cols; }
}