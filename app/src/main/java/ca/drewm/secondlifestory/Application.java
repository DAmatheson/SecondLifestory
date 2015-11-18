/**
 * Created by Drew on 11/18/2015.
 */

package ca.drewm.secondlifestory;

import android.provider.Settings;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;


public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this,
                "aPkrgZv2poKhbZFhz9kTN89nLgP2a6Id6D4MNJlt"/* Application Id */,
                "cf5NMJUsEGQqNTH4o1wkZnBRY3VhEACmkdVSY6N0" /* Client Key */);
    }
}
