/* Character.java
 * Purpose: Model class for a Character
 *
 * Created by Drew on 11/23/2015.
 */

package ca.secondlifestory.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Model for a Character
 */
@ParseClassName("Character")
public class Character extends ParseObject {
    private static final String KEY_USER = "user";
    private static final String KEY_RACE_OBJECT_ID = "raceObjectId";
    private static final String KEY_CLASS_OBJECT_ID = "classObjectId";
    private static final String KEY_NAME = "name";
    private static final String KEY_LIVING = "living";
    private static final String KEY_DETAILS = "details";
    private static final String KEY_EXPERIENCE = "experience";

    private static final String PARSE_NAME = Character.class.getName();

    // TODO: Do we want these? Or a an instance of Race/Class. Haven't made getter/setters for these
    private String _raceName;
    private String _className;

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
     * Gets the ObjectId for the Character's Race
     * @return The ObjectId for the Character's Race
     */
    public String getRaceObjectId() {
        return getString(KEY_RACE_OBJECT_ID);
    }

    /**
     * Sets the ObjectId for the Character's Race
     * @param raceObjectId for the Character's Race
     */
    public void setRaceObjectId(String raceObjectId) {
        put(KEY_RACE_OBJECT_ID, raceObjectId);
    }

    /**
     * Gets the ObjectId for the Character's CharacterClass
     * @return The ObjectId for the Character's CharacterClass
     */
    public String getClassObjectId() {
        return getString(KEY_CLASS_OBJECT_ID);
    }

    /**
     * Sets the ObjectId for the Character's CharacterClass
     * @param classObjectId for the Character's CharacterClass
     */
    public void setClassObjectId(String classObjectId) {
        put(KEY_CLASS_OBJECT_ID, classObjectId);
    }

    /**
     * Gets the Character's name
     * @return The Character's name
     */
    public String getName() {
        return getString(KEY_NAME);
    }

    /**
     * Sets the name of the Character
     * @param name of the Character
     */
    public void setName(String name) {
        put(KEY_NAME, name);
    }

    /**
     * Gets the flag indicating if the Character is alive
     * @return True if the character is alive, false otherwise.
     */
    public boolean isLiving() {
        return getBoolean(KEY_LIVING);
    }

    /**
     * Sets the flag indicating if the Character is alive
     * @param living flag for if the character is alive
     */
    public void setLiving(boolean living) {
        put(KEY_LIVING, living);
    }

    /**
     * Gets a Details/Description string for the Character
     * @return The Details for the Character
     */
    public String getDetails() {
        return getString(KEY_DETAILS);
    }

    /**
     * Sets the Details/Description string for the Character
     * @param details string for the Character
     */
    public void setDetails(String details) {
        put(KEY_DETAILS, details);
    }

    /**
     * Gets the amount of experience the Character has accumulated
     * @return The Character's experience amount
     */
    public int getExperience() {
        return getInt(KEY_EXPERIENCE);
    }

    /**
     * Sets the amount of experience the Character has accumulate
     * @param experience amount for the Character
     */
    public void setExperience(int experience) {
        put(KEY_EXPERIENCE, experience);
    }
}
