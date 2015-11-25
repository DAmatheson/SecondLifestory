/* Character.java
 * Purpose: Model class for a Character
 *
 * Created by Drew on 11/23/2015.
 */

package ca.secondlifestory.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Model for a PlayerCharacter
 */
@ParseClassName("Character")
public class PlayerCharacter extends ParseObject {

    private static final String KEY_USER = "user";
    private static final String KEY_RACE = "race";
    private static final String KEY_CLASS = "class";
    private static final String KEY_NAME = "name";
    private static final String KEY_LIVING = "living";
    private static final String KEY_DETAILS = "details";
    private static final String KEY_EXPERIENCE = "experience";

    /**
     * Instantiates a new instance of PlayerCharacter
     */
    public PlayerCharacter() {
        // Required Empty Constructor
    }

    /**
     * Gets the User the PlayerCharacter belongs to
     * @return The ParseUser the character belongs to
     */
    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    /**
     * Sets the User the PlayerCharacter belongs to
     * @param user the PlayerCharacter belongs to
     */
    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    /**
     * Gets the name for the PlayerCharacter's Race
     * @return The name for the PlayerCharacter's Race
     */
    public String getRace() {
        return getString(KEY_RACE);
    }

    /**
     * Sets the name for the PlayerCharacter's Race
     * @param raceName for the PlayerCharacter's Race
     */
    public void setRace(String raceName) {
        put(KEY_RACE, raceName);
    }

    /**
     * Gets the name for the PlayerCharacter's CharacterClass
     * @return The name for the PlayerCharacter's CharacterClass
     */
    public String getCharacterClass() {
        return getString(KEY_CLASS);
    }

    /**
     * Sets the className for the PlayerCharacter's CharacterClass
     * @param className for the PlayerCharacter's CharacterClass
     */
    public void setCharacterClass(String className) {
        put(KEY_CLASS, className);
    }

    /**
     * Gets the PlayerCharacter's name
     * @return The PlayerCharacter's name
     */
    public String getName() {
        return getString(KEY_NAME);
    }

    /**
     * Sets the name of the PlayerCharacter
     * @param name of the PlayerCharacter
     */
    public void setName(String name) {
        put(KEY_NAME, name);
    }

    /**
     * Gets the flag indicating if the PlayerCharacter is alive
     * @return True if the character is alive, false otherwise.
     */
    public boolean isLiving() {
        return getBoolean(KEY_LIVING);
    }

    /**
     * Sets the flag indicating if the PlayerCharacter is alive
     * @param living flag for if the character is alive
     */
    public void setLiving(boolean living) {
        put(KEY_LIVING, living);
    }

    /**
     * Gets a Details/Description string for the PlayerCharacter
     * @return The Details for the PlayerCharacter
     */
    public String getDetails() {
        return getString(KEY_DETAILS);
    }

    /**
     * Sets the Details/Description string for the PlayerCharacter
     * @param details string for the PlayerCharacter
     */
    public void setDetails(String details) {
        put(KEY_DETAILS, details);
    }

    /**
     * Gets the amount of experience the PlayerCharacter has accumulated
     * @return The PlayerCharacter's experience amount
     */
    public int getExperience() {
        return getInt(KEY_EXPERIENCE);
    }

    /**
     * Sets the amount of experience the PlayerCharacter has accumulate
     * @param experience amount for the PlayerCharacter
     */
    public void setExperience(int experience) {
        put(KEY_EXPERIENCE, experience);
    }

    /**
     * Adds the specified amount of experience to the character
     * @param experienceToAdd amount of experience to add
     */
    public void addExperience(int experienceToAdd) {
        increment(KEY_EXPERIENCE, experienceToAdd);
    }

    /**
     * Subtracts the specified amount of experience from the character
     * @param experienceToSubtract positive integer to be subtracted
     */
    public void subtractExperience(int experienceToSubtract) {
        increment(KEY_EXPERIENCE, -experienceToSubtract);
    }

    /**
     * Gets a ParseQuery for PlayerCharacter objects
     * @return The ParseQuery
     */
    public static ParseQuery<PlayerCharacter> getQuery() {
        return ParseQuery.getQuery(PlayerCharacter.class);//.fromLocalDatastore();
    }
}
