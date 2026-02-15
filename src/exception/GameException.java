package exception;

/**
 * Custom exception type representing game-specific errors
 * and rule violations.
 */

public class GameException extends Exception{

    public GameException(String message) {
        super(message);
    }
}