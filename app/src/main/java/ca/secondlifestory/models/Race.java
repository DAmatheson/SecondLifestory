/* Race.java
 * Purpose: Model class for a character race
 *
 * Created by Drew on 11/23/2015.
 */

package ca.secondlifestory.models;

import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Model class for character races
 */
public class Race extends ParseObject {

    private static final String KEY_USER = "user";
    private static final String KEY_NAME = "name";

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
}
