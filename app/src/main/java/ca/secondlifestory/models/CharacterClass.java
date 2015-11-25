/* CharacterClass.java
 * Purpose: Model class for a character class ParseObject
 *
 * Created by Drew on 11/23/2015.
 */

package ca.secondlifestory.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Model class for character classes
 */
@ParseClassName("CharacterClass")
public class CharacterClass extends ParseObject {
    private static final String KEY_USER = "user";
    private static final String KEY_NAME = "name";

    public static final String PARSE_NAME = CharacterClass.class.getName();

    public CharacterClass() {
        // Required Empty Constructor
    }

    /**
     * Gets the User the Character belongs to
     * @return The ParseUser the character belongs to
     */
    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    /**
     * Sets the User the Character belongs to
     * @param user the Character belongs to
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
}
