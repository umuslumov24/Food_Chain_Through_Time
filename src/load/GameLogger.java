package load;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Handles persistent logging of game events, actions,
 * and state changes for debugging or analysis.
 */

public class GameLogger {
	
    private static final String LOG_FILE = "log.txt";
    private static PrintWriter writer;

    // Initializing the logger
    public static void initialize() {
        try {
            writer = new PrintWriter(new FileWriter(LOG_FILE, true));
            writer.println("=== New Game Session Started at " + getCurrentTime() + " ===");
            writer.flush();
        } catch (IOException e) {
            System.err.println("Could not create log file: " + e.getMessage());
        }
    }

    public static void log(String message) {
        writer.println("[" + getCurrentTime() + "] " + message);
        writer.flush(); // Making sure that the message is logged
    }

    public static void close() {
        writer.println("=== Game Session Ended at " + getCurrentTime() + " ===\n");
        writer.close();
    }

    // This part is about making the code more readable 
    private static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }
}