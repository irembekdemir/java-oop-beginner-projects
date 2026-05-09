import java.util.*;
import java.io.*;

/**
 * Logic class manages the pathfinding simulation using a Backtracking (DFS) approach.
 * This class calculates the optimal path based on maximum remaining health and minimum steps.
 * @author irem bekdemir
 * @version 1.0
 */
public class Logic {

    private int maxHealth = -1; //differs from damage, means no valid path found.
    private boolean foundAnyPath = false;
    private boolean died = false;

    private List <Position> bestPath = new ArrayList<>();

    /**
     * Starting the search from the start coordinate to an exit.
     * @param dungeon the map of the grid representing the simulation.
     * @param startRow initial row index.
     * @param startCol initial column index.
     * @param endRow exit row index.
     * @param endCol exit column index.
     * @param player the character in the dungeon.
     */

    public void find(Dungeon dungeon, int startRow, int startCol, int endRow, int endCol, Player player) {

        maxHealth = -1;
        foundAnyPath = false;
        died = false;
        foundKey = false; 
        bestPath.clear();

        boolean[][] visited = new boolean[dungeon.getRows()][dungeon.getCols()];
        
        List<Position> path = new ArrayList<>();

        depthFirst(dungeon, startRow, startCol, endRow, endCol, player, visited, path);
    }

    private boolean foundKey = false;

    /**
     * Recursive Depth-First Search algorithm with backtracking approach.
     * Explores all possible paths to find the one with the greatest remaining health.
     */
    private void depthFirst(Dungeon d, int r, int c, int endRow, int endCol, Player player, boolean[][] visited, List<Position> path) {

        visited[r][c] = true;

        Player newPlayer = player.copy();

        // Applying the effects of the rooms & position tracking
        d.getRoom(r, c).effectOnPlayer(newPlayer);
        path.add(new Position(r, c, newPlayer.getHealth()));

        if (newPlayer.haveKey()) {
            foundKey = true;
        }

        if (newPlayer.getHealth() == 0){
            died = true;

        } else {
            //Goal checking: is the exit reached and is the key picked up?
            if (r == endRow && c == endCol) { 

                if (newPlayer.haveKey()) {
                    foundAnyPath = true;
                    //updating best path based on remaining health and path lenght
                    if (newPlayer.getHealth() > maxHealth || (newPlayer.getHealth() == maxHealth && path.size() < bestPath.size())) {
                    maxHealth = newPlayer.getHealth();
                    bestPath = new ArrayList<>(path);
                    }
                }
            } else {
                //recursion of exploring in 4 ways
                goUp(d, r, c, endRow, endCol, newPlayer, visited, path);
                goRight(d, r, c, endRow, endCol, newPlayer, visited, path);
                goDown(d, r, c, endRow, endCol, newPlayer, visited, path);
                goLeft(d, r, c, endRow, endCol, newPlayer, visited, path);
            }
        }

        path.remove(path.size() - 1);
        visited[r][c] = false;
    }

    /**
     * Writes the simulation's results to the specified output file.
     * @param out PrintWriter object linked to the output file.
     * @param initialHealth starting health for displaying. 
     */
    public void printResult(PrintWriter out, int initialHealth) {

        if (!foundAnyPath) {
        out.println("RESULT: FAILURE");
        out.println("MAX_HEALTH: " + initialHealth);

            if (!foundKey) {
                out.println("REASON: No key found.");
            } 
        
            else if (died) {
                out.println("REASON: Player died before reaching the exit. RIP");
            } 
        
            else {
                out.println("REASON: Cannot reach the exit.");
            }

        } else {
            out.println("RESULT: SUCCESS");
            out.println("MAX_HEALTH: " + initialHealth);
            out.println("REMAINING_HEALTH: " + maxHealth);
            out.println("STEPS: " + (bestPath.size() - 1));
            out.print("PATH:\n");

            for (int i = 0; i < bestPath.size(); i++) {
                out.print(bestPath.get(i));
                if (i != bestPath.size() - 1) out.print(" -> ");
            }
        }
    }

    /**
     * Direction methods
     * @param d
     * @param r
     * @param c
     * @param endRow
     * @param endCol
     * @param player
     * @param visited
     * @param path
     */
    private void goUp(Dungeon d, int r, int c, int endRow, int endCol, Player player, boolean [][] visited, List <Position> path){
        int nextRow = r-1;
        int nextCol = c;

        if (isValid(d, nextRow, nextCol, visited)) {
            depthFirst(d, nextRow, nextCol, endRow, endCol, player, visited, path);
        }
    }
 
    private void goRight(Dungeon d, int r, int c, int endRow, int endCol, Player player, boolean [][] visited, List <Position> path){
        int nextRow = r;
        int nextCol = c+1;

        if (isValid(d, nextRow, nextCol, visited)) {
            depthFirst(d, nextRow, nextCol, endRow, endCol, player, visited, path);
        }
    }

    private void goDown(Dungeon d, int r, int c, int endRow, int endCol, Player player, boolean [][] visited, List <Position> path){
        int nextRow = r+1;
        int nextCol = c;

        if (isValid(d, nextRow, nextCol, visited)) {
            depthFirst(d, nextRow, nextCol, endRow, endCol, player, visited, path);
        }
    }

    private void goLeft(Dungeon d, int r, int c, int endRow, int endCol, Player player, boolean [][] visited, List <Position> path){
        int nextRow = r;
        int nextCol = c-1;

        if (isValid(d, nextRow, nextCol, visited)) {
            depthFirst(d, nextRow, nextCol, endRow, endCol, player, visited, path);
        }
    }
     /**
     * Checks if the next move is within boundaries and not visited yet.
     * @return true if the move is valid, false otherwise.
     */
    private boolean isValid(Dungeon d, int r, int c, boolean[][] visited) {
        return r >= 0 && c >= 0 && r < d.getRows() && c < d.getCols() && !visited[r][c];
    }
}