/**
 * Custom exception class used in the Smart Home System.
 * This exception is thrown when an invalid operation, command error, or system constraint violation occurs.
 * @author irem bekdemir
 * @version 1.0
 * @since 2026
 */

public class SmartExceptions extends Exception {

    public SmartExceptions(String message) {
        super(message);
    }
    
}