/* Application.java
 * Purpose: Application class for the app
 *
 *  Created by Drew on 11/18/2015.
 */

package ca.secondlifestory;


import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;

import ca.secondlifestory.models.*;
import ca.secondlifestory.utilities.LoggerSingleton;

/**
 * Application object containing setup logic for the whole app
 */
public class Application extends android.app.Application {

    private static final String LOG_TAG = Application.class.getName();

    /**
     * SharedPreferences key for whether or not the show deceased characters checkbox is checked
     */
    public static final String ARG_SHOW_DECEASED_CHARACTERS = "Application.showDeceased";

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            Parse.enableLocalDatastore(this);

            // Register the subclasses we have
            ParseObject.registerSubclass(PlayerCharacter.class);
            ParseObject.registerSubclass(CharacterClass.class);
            ParseObject.registerSubclass(Event.class);
            ParseObject.registerSubclass(Race.class);

            // Default to only the user being allowed to see/modify created objects
            ParseACL.setDefaultACL(new ParseACL(), true);

            //noinspection SpellCheckingInspection
            Parse.initialize(this,
                    "aPkrgZv2poKhbZFhz9kTN89nLgP2a6Id6D4MNJlt"/* Application Id */,
                    "cf5NMJUsEGQqNTH4o1wkZnBRY3VhEACmkdVSY6N0" /* Client Key */);

        } catch (Exception ex) {
            LoggerSingleton.getInstance().exception(LOG_TAG,
                    ".onCreate: " + ex.getMessage(),
                    ex);

            throw ex;
        }
    }
}
