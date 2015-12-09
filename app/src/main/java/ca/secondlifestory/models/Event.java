/* Event.java
 * Purpose: Model class for an event
 *
 * Created by Drew on 11/23/2015.
 */

package ca.secondlifestory.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Date;

/**
 * Model class for character events
 */
@ParseClassName("Event")
public class Event extends ParseObject {
    private static final String KEY_CHARACTER_COUNT = "characterCount";
    private static final String KEY_EXPERIENCE = "experience";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_TITLE = "title";

    public static final String KEY_CHARACTER = "character";
    public static final String KEY_EVENT_TYPE = "eventType";
    public static final String KEY_DATE = "date";

    /**
     * Instantiates a new instance of Event
     */
    public Event() {
        // Required Empty Constructor
    }

    public PlayerCharacter getCharacter() {
        return (PlayerCharacter) getParseObject(KEY_CHARACTER);
    }

    public void setCharacter(PlayerCharacter character) {
        put(KEY_CHARACTER, character);
    }

    /**
     * Sets the ObjectId of the PlayerCharacter this  Event belongs to
     * @param objectId of the PlayerCharacter this event belongs to
     */
    public void setCharacterObjectId(String objectId) {
        put(KEY_CHARACTER, objectId);
    }

    /**
     * Gets the Event's EventType
     * @return The Event's EventType
     */
    public EventTypes getEventType() {
        return EventTypes.valueOf(getString(KEY_EVENT_TYPE));
    }

    /**
     * Sets the Event's EventType
     * @param type of the Event
     */
    public void setEventType(EventTypes type) {
        put(KEY_EVENT_TYPE, type.toString());
    }

    /**
     * Gets the number of characters present for the Event
     * @return The character count for the Event
     */
    public int getCharacterCount() {
        return getInt(KEY_CHARACTER_COUNT);
    }

    /**
     * Sets the number of characters present for the Event
     * @param characterCount The character count
     */
    public void setCharacterCount(int characterCount) {
        put(KEY_CHARACTER_COUNT, characterCount);
    }

    /**
     * Gets the amount of experience from the Event
     * @return The amount of the experience from the Event
     */
    public int getExperience() {
        return getInt(KEY_EXPERIENCE);
    }

    /**
     * Sets the amount of experience from the Event
     * @param experience The amount of experience
     */
    public void setExperience(int experience) {
        put(KEY_EXPERIENCE, experience);
    }

    /**
     * Gets the description of the Event
     * @return The description of the Event
     */
    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    /**
     * Sets the description of the Event
     * @param description The description
     */
    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    /**
     * Gets the date of the Event
     * @return The date of the Event
     */
    public Date getDate() {
        return getDate(KEY_DATE);
    }

    /**
     * Sets the date of the Event
     * @param date The date
     */
    public void setDate(Date date) {
        put(KEY_DATE, date);
    }

    /**
     * Gets the title of the Event
     * @return the title of the event
     */
    public String getTitle() {
        return getString(KEY_TITLE);
    }

    /**
     * Sets the title of the Event
     * @param title The title
     */
    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    /**
     * Gets a ParseQuery for Event objects
     * @return The ParseQuery
     */
    public static ParseQuery<Event> getQuery() {
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);

        query.fromLocalDatastore();

        return query;
    }

    public static Event createWithoutData(String objectId) {
        return ParseObject.createWithoutData(Event.class, objectId);
    }
}
