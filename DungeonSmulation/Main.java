import java.io.*;
import java.util.*;        

/**
 * Start point for the Dungeon Simulation program.
 * Handles file I/O operations, initializes the dungeon grid, and triggers the path finding algorithm.
 * @author irem bekdemir
 * @version 1.0
 */
public class Main {
    /**
     * Main execution method.
     * @param args command line arguments: args[0] is input file, args[1] is output file.
     */
    public static void main(String[] args) {

        // input format check
         if (args.length < 2) { 
            System.out.println("Wrong Usage! Try this instead: java8 Main input.txt output.txt");
            return;}

        Scanner input = null; // initializing the variable input to prevent NullPointerException

        /**
         * checks for file not found error, continues if it is fine.
         */
        try { 
            input = new Scanner(new File(args[0]));

            // initial simulation parameters from the input file
            int n = input.nextInt(); // number of rows
            int m = input.nextInt(); // number of columns
            int initialHealth = input.nextInt(); // starting health value given in the input file

            int startRow = input.nextInt();
            int startCol = input.nextInt();

            int endRow = input.nextInt();
            int endCol = input.nextInt();

            Dungeon dungeon = new Dungeon(n, m);

            /**
            * Takes the dungeon structure from the input file.
            */

            for (int i = 0; i < n; i++) {
                int j = 0;

                while (j < m) {
                    String token = input.next();

                    for (int k = 0; k < token.length(); k++) {
                        dungeon.setRoom(i, j, token.charAt(k));
                        j++;
                    }   
                }
            }

            Player player = new Player(initialHealth);
            Logic logic = new Logic();

            //performs the search for optimal path
            logic.find(dungeon, startRow, startCol, endRow, endCol, player);

            //creates the output file writer and passes it to the Logic class
            try (PrintWriter out = new PrintWriter(new File(args[1]))) {
                logic.printResult(out, initialHealth);
            }catch (IOException e) {
                System.out.println("Error: The input file could not found.");
            }

        }catch (FileNotFoundException e) {
            System.out.println("404 File Not Found");

        } finally {
            if (input != null) {
                input.close();
            }
        }
    }
}