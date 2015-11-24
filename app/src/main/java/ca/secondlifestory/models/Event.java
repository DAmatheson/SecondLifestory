/* Event.java
 * Purpose: Model class for an event
 *
 * Created by Drew on 11/23/2015.
 */

package ca.secondlifestory.models;

import java.util.Date;
import java.util.List;

/**
 * Model class for character events
 */
public class Event {
    private String _objectId;

    private String _eventTypeObjectId;
    private String _eventTypeName;

    private int _characterCount;
    private int _experience;
    private String _description;
    private Date _date;

    private List<EventDetail> _eventDetails;

    /**
     * Gets the ObjectId for the Event
     * @return ObjectId for the Event
     */
    public String getObjectId() {
        return _objectId;
    }

    /**
     * Sets the ObjectId for the Event
     * @param objectId for the Event
     */
    public void setObjectId(String objectId) {
        this._objectId = objectId;
    }

    /**
     * Gets the ObjectId for the Event's EventType
     * @return ObjectId for the Event's EventType
     */
    public String getEventTypeObjectId() {
        return _eventTypeObjectId;
    }

    /**
     * Sets the ObjectId for the Event's EventType
     * @param eventTypeObjectId The ObjectId
     */
    public void setEventTypeObjectId(String eventTypeObjectId) {
        this._eventTypeObjectId = eventTypeObjectId;
    }

    /**
     * Gets the name for the Event's EventType
     * @return The name for the Event's EventType
     */
    public String getEventTypeName() {
        return _eventTypeName;
    }

    /**
     * Sets the name for the Event's EventType
     * @param eventTypeName The name for the Event's EventType
     */
    public void setEventTypeName(String eventTypeName) {
        this._eventTypeName = eventTypeName;
    }

    /**
     * Gets the number of characters present for the Event
     * @return The character count for the Event
     */
    public int getCharacterCount() {
        return _characterCount;
    }

    /**
     * Sets the number of characters present for the Event
     * @param characterCount The character count
     */
    public void setCharacterCount(int characterCount) {
        this._characterCount = characterCount;
    }

    /**
     * Gets the amount of experience from the Event
     * @return The amount of the experience from the Event
     */
    public int getExperience() {
        return _experience;
    }

    /**
     * Sets the amount of experience from the Event
     * @param experience The amount of experience
     */
    public void setExperience(int experience) {
        this._experience = experience;
    }

    /**
     * Gets the description of the Event
     * @return The description of the Event
     */
    public String getDescription() {
        return _description;
    }

    /**
     * Sets the description of the Event
     * @param description The description
     */
    public void setDescription(String description) {
        this._description = description;
    }

    /**
     * Gets the date of the Event
     * @return The date of the Event
     */
    public Date getDate() {
        return _date;
    }

    /**
     * Sets the date of the Event
     * @param date The date
     */
    public void setDate(Date date) {
        this._date = date;
    }

    /**
     * Gets the EventDetails for this Event
     * @return The EventDetails for the Event
     */
    public List<EventDetail> getEventDetails() {
        return _eventDetails;
    }

    /**
     * Sets the EventDetails for this Event
     * @param eventDetails The EventDetails
     */
    public void setEventDetails(List<EventDetail> eventDetails) {
        this._eventDetails = eventDetails;
    }
}
