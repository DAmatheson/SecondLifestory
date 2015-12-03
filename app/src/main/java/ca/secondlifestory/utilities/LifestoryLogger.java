/* LifeStoryLogger.java
 * Purpose: Interface for the loggers implemented for Second Lifestory
 * Created by Drew on 12/2/2015.
 */

package ca.secondlifestory.utilities;

/**
 * Interface for logger's used by Second Lifestory
 */
public interface LifestoryLogger {
    void exception(String tag, String message, Exception ex);
    void info(String tag, String message);
}
