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

/**
 * Model class for character classes
 */
@ParseClassName("CharacterClass")
public class CharacterClass extends ParseObject {

    private static final String KEY_USER = "user";
    private static final String KEY_NAME = "name";

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
        put(KEY_USER, user);
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
        put(KEY_NAME, name);
    }

    /**
     * Gets a ParseQuery for CharacterClass objects
     * @return The ParseQuery
     */
    public static ParseQuery<CharacterClass> getQuery() {
        return ParseQuery.getQuery(CharacterClass.class);
    }
}
