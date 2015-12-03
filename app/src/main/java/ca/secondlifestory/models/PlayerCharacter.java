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
     * Gets the Race for the PlayerCharacter
     * @return The Race for the PlayerCharacter
     */
    public Race getRace() {
        return (Race)getParseObject(KEY_RACE);
    }

    /**
     * Sets the race for the PlayerCharacter
     * @param race for the PlayerCharacter
     */
    public void setRace(Race race) {
        put(KEY_RACE, race);
    }

    /**
     * Gets the name for the PlayerCharacter
     * @return The name for the PlayerCharacter
     */
    public CharacterClass getCharacterClass() {
        return (CharacterClass)getParseObject(KEY_CLASS);
    }

    /**
     * Sets the CharacterClass for the PlayerCharacter
     * @param characterClass for the PlayerCharacter
     */
    public void setCharacterClass(CharacterClass characterClass) {
        put(KEY_CLASS, characterClass);
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
        ParseQuery<PlayerCharacter> query = ParseQuery.getQuery(PlayerCharacter.class);
        query.whereEqualTo(KEY_USER, ParseUser.getCurrentUser());
        query.include(KEY_RACE).include(KEY_CLASS);

        query.fromLocalDatastore();

        return query;
    }

    public static PlayerCharacter createWithoutData(String objectId) {
        return ParseObject.createWithoutData(PlayerCharacter.class, objectId);
    }
}
