/* EventQueryAdapter.java
 * Purpose: ParseQueryAdapter for Event object lists.
 *
 * Created by Drew on 11/25/2015.
 */

package ca.secondlifestory.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import ca.secondlifestory.R;
import ca.secondlifestory.models.Event;
import ca.secondlifestory.models.PlayerCharacter;

/**
 * QueryAdapter for Event
 */
public class EventQueryAdapter extends ParseQueryAdapter<Event> {

    private final DateFormat dateFormatter = SimpleDateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());

    public EventQueryAdapter(Context context, ParseQueryAdapter.QueryFactory<Event> queryFactory) {
        super (context, queryFactory);
    }

    public EventQueryAdapter(Context context, final String characterObjectId) {
        super(context, new ParseQueryAdapter.QueryFactory<Event>() {

            @Override
            public ParseQuery<Event> create() {
                ParseQuery<Event> query = Event.getQuery();
                query.whereEqualTo(Event.KEY_CHARACTER,
                        ParseObject.createWithoutData(PlayerCharacter.class, characterObjectId));

                return query;
            }
        });
    }

    @Override
    public View getItemView(Event object, View v, ViewGroup parent) {

        if (v == null) {
            v = View.inflate(getContext(), R.layout.event_list_item, null);
        }

        super.getItemView(object, v, parent);

        TextView eventName = (TextView) v.findViewById(R.id.event_listitem_name);
        eventName.setText(object.getTitle());

        TextView eventDate = (TextView) v.findViewById(R.id.event_listitem_date);
        eventDate.setText(dateFormatter.format(object.getDate()));

        return v;
    }

    public void notifyListChanged() {
        // Unfortunately, there isn't a way to add new items with ParseQueryAdapter.
        loadObjects();
    }
}
