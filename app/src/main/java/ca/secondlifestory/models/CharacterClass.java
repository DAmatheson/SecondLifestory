/* CharacterClass.java
 * Purpose: Model class for a character class ParseObject
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
 * Model class for character classes
 */
@ParseClassName("CharacterClass")
public class CharacterClass extends ParseObject {

    private static final String LOG_TAG = CharacterClass.class.getName();

    private static final String KEY_USER = "user";
    public static final String KEY_NAME = "name";

    /**
     * Instantiates a new instance of CharacterClass
     */
    public CharacterClass() {
        // Required Empty Constructor
    }

    /**
     * Gets the User the CharacterClass belongs to
     * @return The ParseUser the CharacterClass belongs to
     */
    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    /**
     * Sets the User the CharacterClass belongs to
     * @param user the CharacterClass belongs to
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
     * Gets the name of the CharacterClass
     * @return The name of the CharacterClass
     */
    public String getName() {
        return getString(KEY_NAME);
    }

    /**
     * Sets the name of the CharacterClass
     * @param name of the CharacterClass
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
     * Gets a ParseQuery for CharacterClass objects for the current ParseUser
     * @return The ParseQuery
     */
    public static ParseQuery<CharacterClass> getQuery() {
        try {
            ParseQuery<CharacterClass> query = ParseQuery.getQuery(CharacterClass.class);
            query.whereEqualTo(KEY_USER, ParseUser.getCurrentUser());

            query.fromLocalDatastore();

            return query;
        } catch (Exception ex) {
            LoggerSingleton.getInstance().exception(LOG_TAG,
                    ".getQuery: " + ex.getMessage(),
                    ex);

            throw ex;
        }
    }

    public static void setupDefaultClasses(String[] classNames) {
        try {
            for (String className : classNames) {
                CharacterClass newClass = new CharacterClass();
                newClass.setName(className);
                newClass.setUser(ParseUser.getCurrentUser());

                newClass.pinInBackground();
                newClass.saveEventually();
            }
        } catch (Exception ex) {
            LoggerSingleton.getInstance().exception(LOG_TAG,
                    ".setupDefaultClasses: " + ex.getMessage(),
                    ex);

            throw ex;
        }
    }
}
