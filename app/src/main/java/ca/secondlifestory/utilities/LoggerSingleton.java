/* LoggerSingleton.java
 * Purpose: Singleton for getting access to the application's logger.
 *
 * Created by Drew on 12/07/2015.
 */

package ca.secondlifestory.utilities;

/**
 * Class for getting access to the logger singleton for the application
 */
public final class LoggerSingleton {
    private static final LifestoryLogger logger = new SimpleLogger();

    protected LoggerSingleton() {
        // Prevent instantiation
    }

    public static LifestoryLogger getInstance() {
        return logger;
    }
}
