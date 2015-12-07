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

/**
 * Model class for character races
 */
@ParseClassName("Race")
public class Race extends ParseObject {

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
        put(KEY_USER, user);
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
        put(KEY_NAME, name);
    }

    /**
     * Gets a ParseQuery for Race objects for the current ParseUser
     * @return The ParseQuery
     */
    public static ParseQuery<Race> getQuery() {
        ParseQuery<Race> query = ParseQuery.getQuery(Race.class);
        query.whereEqualTo(Race.KEY_USER, ParseUser.getCurrentUser());

        query.fromLocalDatastore();

        return query;
    }
}
