/* Application.java
 * Purpose: Application class for the app
 *
 *  Created by Drew on 11/18/2015.
 */

package ca.secondlifestory;

import com.parse.Parse;


public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        //noinspection SpellCheckingInspection
        Parse.initialize(this,
                "aPkrgZv2poKhbZFhz9kTN89nLgP2a6Id6D4MNJlt"/* Application Id */,
                "cf5NMJUsEGQqNTH4o1wkZnBRY3VhEACmkdVSY6N0" /* Client Key */);
    }
}
