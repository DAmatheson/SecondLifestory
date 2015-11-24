/* EventType.java
 * Purpose: Model class for an Event type
 *
 * Created by Drew on 11/23/2015.
 */

package ca.secondlifestory.models;

/**
 * Model class for Event types
 */
public class EventType {
    private String _objectId;
    private String _name;

    /**
     * Gets the ObjectId for the EventType
     * @return The ObjectId for the EventType
     */
    public String getObjectId() {
        return _objectId;
    }

    /**
     * Sets the ObjectId for the EventType
     * @param objectId for the EventType
     */
    public void setObjectId(String objectId) {
        this._objectId = objectId;
    }

    /**
     * Gets the name for the EventType
     * @return The name for the EventType
     */
    public String getName() {
        return _name;
    }

    /**
     * Sets the name for the EventType
     * @param name for the EventType
     */
    public void setName(String name) {
        this._name = name;
    }
}
