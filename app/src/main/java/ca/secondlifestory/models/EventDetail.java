/* EventDetail.java
 * Purpose: Model class for an event detail
 *
 * Created by Drew on 11/23/2015.
 */

package ca.secondlifestory.models;

/**
 * Model class for event details
 */
public class EventDetail {

    private String _objectId;
    private String _eventObjectId;

    private String _name;
    private int _creatureCount;

    /**
     * Gets the ObjectId for the EventDetail
     * @return ObjectId for the EventDetail
     */
    public String getObjectId() {
        return _objectId;
    }

    /**
     * Sets the ObjectId for the EventDetail
     * @param objectId for the EventDetail
     */
    public void setObjectId(String objectId) {
        this._objectId = objectId;
    }

    /**
     * Gets the ObjectId for the Event this EventDetail is for
     * @return ObjectId for the Event the EventDetail belongs to
     */
    public String getEventObjectId() {
        return _eventObjectId;
    }

    /**
     * Sets the ObjectId for the Event this EventDetail is for
     * @param eventObjectId for the Event the EventDetail belongs to
     */
    public void setEventObjectId(String eventObjectId) {
        this._eventObjectId = eventObjectId;
    }

    /**
     * Gets the name for the EventDetail
     * @return The name for the EventDetail
     */
    public String getName() {
        return _name;
    }

    /**
     * Sets the name for the EventDetail
     * @param name for the EventDetail
     */
    public void setName(String name) {
        this._name = name;
    }

    /**
     * Gets the creature count for the EventDetail
     * @return The creature count
     */
    public int getCreatureCount() {
        return _creatureCount;
    }

    /**
     * Sets the creature count for the EventDetail
     * @param creatureCount for the EventDetail
     */
    public void setCreatureCount(int creatureCount) {
        this._creatureCount = creatureCount;
    }
}
