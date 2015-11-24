/* Race.java
 * Purpose: Model class for a character race
 *
 * Created by Drew on 11/23/2015.
 */

package ca.secondlifestory.models;

/**
 * Model class for character races
 */
public class Race {
    private String _objectId;
    private String _name;

    /**
     * Gets the ObjectId for the Character Race
     * @return The Object for the Race
     */
    public String getObjectId() {
        return _objectId;
    }

    /**
     * Sets the ObjectId for the Character Race
     * @param objectId for the Race
     */
    public void setObjectId(String objectId) {
        this._objectId = objectId;
    }

    /**
     * Gets the name for the Character Race
     * @return The name for the Race
     */
    public String getName() {
        return _name;
    }

    /**
     * Sets the name for the Character Race
     * @param name for the Race
     */
    public void setName(String name) {
        this._name = name;
    }
}
