/* CharacterClass.java
 * Purpose: Model class for a character class
 *
 * Created by Drew on 11/23/2015.
 */

package ca.secondlifestory.models;

/**
 * Model class for character classes
 */
public class CharacterClass {
    private String _objectId;
    private String _name;

    /**
     * Gets the ObjectId for the CharacterClass
     * @return The ObjectId for the CharacterClass
     */
    public String getObjectId() {
        return _objectId;
    }

    /**
     * Sets the ObjectId for the CharacterClass
     * @param objectId for the CharacterClass
     */
    public void setObjectId(String objectId) {
        this._objectId = objectId;
    }

    /**
     * Gets the name of the CharacterClass
     * @return The name of the CharacterClass
     */
    public String getName() {
        return _name;
    }

    /**
     * Sets the name of the CharacterClass
     * @param name of the CharacterClass
     */
    public void setName(String name) {
        this._name = name;
    }
}
