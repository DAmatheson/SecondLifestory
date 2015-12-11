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

import ca.secondlifestory.utilities.LoggerSingleton;

/**
 * Model for a PlayerCharacter
 */
@ParseClassName("Character")
public class PlayerCharacter extends ParseObject {

    private static final String LOG_TAG = PlayerCharacter.class.getName();

    private static final String KEY_USER = "user";
    public static final String KEY_RACE = "race";
    public static final String KEY_CLASS = "class";
    private static final String KEY_NAME = "name";
    public static final String KEY_LIVING = "living";
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
        try {
            put(KEY_RACE, race);
        } catch (Exception ex) {
            LoggerSingleton.getInstance().exception(LOG_TAG,
                    ".setRace: " + ex.getMessage(),
                    ex);

            throw ex;
        }
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
        try {
            put(KEY_CLASS, characterClass);
        } catch (Exception ex) {
            LoggerSingleton.getInstance().exception(LOG_TAG,
                    ".setCharacterClass: " + ex.getMessage(),
                    ex);

            throw ex;
        }
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
        try {
            put(KEY_LIVING, living);
        } catch (Exception ex) {
            LoggerSingleton.getInstance().exception(LOG_TAG,
                    ".setLiving: " + ex.getMessage(),
                    ex);

            throw ex;
        }
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
        try {
            put(KEY_DETAILS, details);
        } catch (Exception ex) {
            LoggerSingleton.getInstance().exception(LOG_TAG,
                    ".setDetails: " + ex.getMessage(),
                    ex);

            throw ex;
        }
    }

    /**
     * Gets the amount of experience the PlayerCharacter has accumulated
     * @return The PlayerCharacter's experience amount
     */
    public long getExperience() {
        return getLong(KEY_EXPERIENCE);
    }

    /**
     * Adds the specified amount of experience to the character
     * @param experienceToAdd amount of experience to add
     */
    public void addExperience(int experienceToAdd) {
        try {
            increment(KEY_EXPERIENCE, (long) experienceToAdd);
        } catch (Exception ex) {
            LoggerSingleton.getInstance().exception(LOG_TAG,
                    ".addExperience: " + ex.getMessage(),
                    ex);

            throw ex;
        }
    }

    /**
     * Subtracts the specified amount of experience from the character
     * @param experienceToSubtract positive integer to be subtracted
     */
    public void subtractExperience(int experienceToSubtract) {
        try {
            increment(KEY_EXPERIENCE, (long) -experienceToSubtract);
        } catch (Exception ex) {
            LoggerSingleton.getInstance().exception(LOG_TAG,
                    ".subtractExperience: " + ex.getMessage(),
                    ex);

            throw ex;
        }
    }

    /**
     * Gets a ParseQuery for PlayerCharacter objects
     * @return The ParseQuery
     */
    public static ParseQuery<PlayerCharacter> getQuery() {
        try {
            ParseQuery<PlayerCharacter> query = ParseQuery.getQuery(PlayerCharacter.class);
            query.whereEqualTo(KEY_USER, ParseUser.getCurrentUser());
            query.include(KEY_RACE).include(KEY_CLASS);

            query.fromLocalDatastore();

            return query;
        } catch (Exception ex) {
            LoggerSingleton.getInstance().exception(LOG_TAG,
                    ".getQuery: " + ex.getMessage(),
                    ex);

            throw ex;
        }
    }

    public static PlayerCharacter createWithoutData(String objectId) {
        try {
            return ParseObject.createWithoutData(PlayerCharacter.class, objectId);
        } catch (Exception ex) {
            LoggerSingleton.getInstance().exception(LOG_TAG,
                    ".createWithoutData: " + ex.getMessage(),
                    ex);

            throw ex;
        }
    }
}
