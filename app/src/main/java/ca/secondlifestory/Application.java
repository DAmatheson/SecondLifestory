/* Application.java
 * Purpose: Application class for the app
 *
 *  Created by Drew on 11/18/2015.
 */

package ca.secondlifestory;


import com.parse.Parse;
import com.parse.ParseObject;

import ca.secondlifestory.models.*;

/**
 * Application object containing setup logic for the whole app
 */
public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);

        // Register the subclasses we have
        ParseObject.registerSubclass(PlayerCharacter.class);
        ParseObject.registerSubclass(CharacterClass.class);
        ParseObject.registerSubclass(Event.class);
        ParseObject.registerSubclass(Race.class);

        //noinspection SpellCheckingInspection
        Parse.initialize(this,
                "aPkrgZv2poKhbZFhz9kTN89nLgP2a6Id6D4MNJlt"/* Application Id */,
                "cf5NMJUsEGQqNTH4o1wkZnBRY3VhEACmkdVSY6N0" /* Client Key */);
    }
}
