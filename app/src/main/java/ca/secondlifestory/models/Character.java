/* Character.java
 * Purpose: Model class for a Character
 *
 * Created by Drew on 11/23/2015.
 */

package ca.secondlifestory.models;

/**
 * Model for a Character
 */
public class Character {
    private String _objectId;
    private String _raceObjectId;
    private String _classObjectId;

    private String _name;
    private boolean _living;
    private String _details;
    private int _experience;

    // TODO: Do we want these? Or a an instance of Race/Class. Haven't made getter/setters for these
    private String _raceName;
    private String _className;

    /**
     * Gets the ObjectId for the Character
     * @return The ObjectId for the Character
     */
    public String getObjectId() {
        return _objectId;
    }

    /**
     * Sets the ObjectId for the Character
     * @param objectId for the Character
     */
    public void setObjectId(String objectId) {
        this._objectId = objectId;
    }

    /**
     * Gets the ObjectId for the Character's Race
     * @return The ObjectId for the Character's Race
     */
    public String getRaceObjectId() {
        return _raceObjectId;
    }

    /**
     * Sets the ObjectId for the Character's Race
     * @param raceObjectId for the Character's Race
     */
    public void setRaceObjectId(String raceObjectId) {
        this._raceObjectId = raceObjectId;
    }

    /**
     * Gets the ObjectId for the Character's CharacterClass
     * @return The ObjectId for the Character's CharacterClass
     */
    public String getClassObjectId() {
        return _classObjectId;
    }

    /**
     * Sets the ObjectId for the Character's CharacterClass
     * @param classObjectId for the Character's CharacterClass
     */
    public void setClassObjectId(String classObjectId) {
        this._classObjectId = classObjectId;
    }

    /**
     * Gets the Character's name
     * @return The Character's name
     */
    public String getName() {
        return _name;
    }

    /**
     * Sets the name of the Character
     * @param name of the Character
     */
    public void setName(String name) {
        this._name = name;
    }

    /**
     * Gets the flag indicating if the Character is alive
     * @return True if the character is alive, false otherwise.
     */
    public boolean isLiving() {
        return _living;
    }

    /**
     * Sets the flag indicating if the Character is alive
     * @param living flag for if the character is alive
     */
    public void setLiving(boolean living) {
        this._living = living;
    }

    /**
     * Gets a Details/Description string for the Character
     * @return The Details for the Character
     */
    public String getDetails() {
        return _details;
    }

    /**
     * Sets the Details/Description string for the Character
     * @param details string for the Character
     */
    public void setDetails(String details) {
        this._details = details;
    }

    /**
     * Gets the amount of experience the Character has accumulated
     * @return The Character's experience amount
     */
    public int getExperience() {
        return _experience;
    }

    /**
     * Sets the amount of experience the Character has accumulate
     * @param experience amount for the Character
     */
    public void setExperience(int experience) {
        this._experience = experience;
    }
}
