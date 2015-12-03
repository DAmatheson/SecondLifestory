/* SimpleLogger.java
 * Purpose: A simple logger implementation which just uses android.util.Log
 *
 * Created by Drew on 12/2/2015.
 */

package ca.secondlifestory.utilities;

import android.util.Log;

/**
 * Simple logger implementation which just wraps android.util.Log
 */
public class SimpleLogger implements LifestoryLogger {
    public void exception(String tag, String message, Exception e) {
        Log.e(tag, message, e);
    }

    public void info(String tag, String message) {
        Log.i(tag, message);
    }
}
