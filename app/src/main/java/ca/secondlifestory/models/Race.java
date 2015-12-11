/* Race.java
 * Purpose: Model class for a character race
 *
 * Created by Drew on 11/23/2015.
 */

package ca.secondlifestory.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import ca.secondlifestory.utilities.LoggerSingleton;

/**
 * Model class for character races
 */
@ParseClassName("Race")
public class Race extends ParseObject {

    private static final String LOG_TAG = Race.class.getName();

    private static final String KEY_USER = "user";
    public static final String KEY_NAME = "name";

    /**
     * Instantiates a new instance of Race
     */
    public Race() {
        // Required Empty Constructor
    }

    /**
     * Gets the User the Race belongs to
     * @return The ParseUser the race belongs to
     */
    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    /**
     * Sets the User the Race belongs to
     * @param user the Race belongs to
     */
    public void setUser(ParseUser user) {
        try {
            put(KEY_USER, user);
        } catch (Exception ex) {
            LoggerSingleton.getInstance().exception(LOG_TAG,
                    ".setUser: " + ex.getMessage(),
                    ex);

            throw ex;
        }
    }

    /**
     * Gets the name for the Character Race
     * @return The name for the Race
     */
    public String getName() {
        return getString(KEY_NAME);
    }

    /**
     * Sets the name for the Character Race
     * @param name for the Race
     */
    public void setName(String name) {
        try {
            put(KEY_NAME, name);
        } catch (Exception ex) {
            LoggerSingleton.getInstance().exception(LOG_TAG,
                    ".setName: " + ex.getMessage(),
                    ex);

            throw ex;
        }
    }

    /**
     * Gets a ParseQuery for Race objects for the current ParseUser
     * @return The ParseQuery
     */
    public static ParseQuery<Race> getQuery() {
        try {
            ParseQuery<Race> query = ParseQuery.getQuery(Race.class);
            query.whereEqualTo(Race.KEY_USER, ParseUser.getCurrentUser());

            query.fromLocalDatastore();

            return query;
        } catch (Exception ex) {
            LoggerSingleton.getInstance().exception(LOG_TAG,
                    ".getQuery: " + ex.getMessage(),
                    ex);

            throw ex;
        }
    }

    public static void setupDefaultRaces(String[] races) {
        try {
            for (String raceName : races) {
                Race race = new Race();
                race.setName(raceName);
                race.setUser(ParseUser.getCurrentUser());

                race.pinInBackground();
                race.saveEventually();
            }
        } catch (Exception ex) {
            LoggerSingleton.getInstance().exception(LOG_TAG,
                    ".setupDefaultRaces: " + ex.getMessage(),
                    ex);

            throw ex;
        }
    }
}
