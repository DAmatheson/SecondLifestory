/* EventDetail.java
 * Purpose: Wrapper class for an event detail ParseObject
 *
 * Created by Drew on 11/23/2015.
 */

package ca.secondlifestory.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Wrapper class for event details
 */
@ParseClassName("EventDetail")
public class EventDetail extends ParseObject {

    private static final String KEY_NAME = "name";
    private static final String KEY_CREATURE_COUNT = "creatureCount";

    /**
     * Instantiates a new instance of EventDetail
     */
    public EventDetail() {
        // Required Empty Constructor
    }

    /**
     * Gets the name for the EventDetail
     * @return The name for the EventDetail
     */
    public String getName() {
        return getString(KEY_NAME);
    }

    /**
     * Sets the name for the EventDetail
     * @param name for the EventDetail
     */
    public void setName(String name) {
        put(KEY_NAME, name);
    }

    /**
     * Gets the creature count for the EventDetail
     * @return The creature count
     */
    public int getCreatureCount() {
        return getInt(KEY_CREATURE_COUNT);
    }

    /**
     * Sets the creature count for the EventDetail
     * @param creatureCount for the EventDetail
     */
    public void setCreatureCount(int creatureCount) {
        put(KEY_CREATURE_COUNT, creatureCount);
    }

    /**
     * Gets a ParseQuery for EventDetail objects
     * @return The ParseQuery
     */
    public static ParseQuery<EventDetail> getQuery() {
        return ParseQuery.getQuery(EventDetail.class);
    }
}
