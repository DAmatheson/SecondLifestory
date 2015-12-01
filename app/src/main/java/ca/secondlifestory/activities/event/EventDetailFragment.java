/* EventDetailFragment.java
 * Purpose: Fragment for the character event detail screen
 *
 *  Created by Drew on 11/17/2015.
 */

package ca.secondlifestory.activities.event;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import ca.secondlifestory.R;
import ca.secondlifestory.models.Event;

/**
 * A fragment representing a single Event detail screen.
 * This fragment is either contained in a {@link EventActivity}
 * in two-pane mode (on tablets) or a {@link EventDetailFragment}
 * on handsets.
 */
public class EventDetailFragment extends Fragment {
    public interface Callbacks {
        void onEditClicked(String characterObjectId, String eventId);
    }

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    private static final String ARG_CHARACTER_ID = "EventDetailFragment.characterObjectId";
    private static final String ARG_EVENT_ID = "EventDetailFragment.eventObjectId";

    private Callbacks mListener;

    /**
     * The content this fragment is presenting.
     */
    private Event mItem;

    private final DateFormat dateFormatter = new SimpleDateFormat("EEE, MMM, yyyy");
    private final DateFormat timeFormatter = SimpleDateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());

    private ProgressBar loadingIndicator;

    private TextView eventTitle;
    private TextView date;
    private TextView experience;
    private TextView characterCount;
    private TextView description;

    private Button editButton;
    private Button deleteButton;

    public static EventDetailFragment newInstance(String characterId, String eventId) {
        Bundle args = new Bundle();
        args.putString(ARG_CHARACTER_ID, characterId);
        args.putString(ARG_EVENT_ID, eventId);

        EventDetailFragment fragment = new EventDetailFragment();
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event_detail, container, false);

        loadingIndicator = (ProgressBar)rootView.findViewById(R.id.loadingIndicator);

        eventTitle = (TextView) rootView.findViewById(R.id.event_detail_title);
        date = (TextView) rootView.findViewById(R.id.event_date);
        experience = (TextView) rootView.findViewById(R.id.event_xp);
        characterCount = (TextView) rootView.findViewById(R.id.event_characters_present);
        description = (TextView) rootView.findViewById(R.id.event_description);

        if (getArguments() != null && getArguments().containsKey(ARG_CHARACTER_ID)) {

            loadingIndicator.setVisibility(View.VISIBLE);

            ParseQuery<Event> query = Event.getQuery();
            query.include(Event.KEY_CHARACTER);
            query.getInBackground(getArguments().getString(ARG_EVENT_ID), new GetCallback<Event>() {
                @Override
                public void done(Event object, ParseException e) {
                    loadingIndicator.setVisibility(View.GONE);

                    if (e == null) {
                        mItem = object;

                        // TODO: Event Title
                        date.setText(dateFormatter.format(mItem.getDate()) + " " + timeFormatter.format(mItem.getDate()));
                        experience.setText(Integer.toString(mItem.getExperience()));
                        characterCount.setText(Integer.toString(mItem.getCharacterCount()));
                        //description.setText(mItem.getDescription());
                    } else {
                        // TODO: Error handling
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        editButton = (Button) rootView.findViewById(R.id.edit_event_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Set this up in a better way. getArguments might return null
                mListener.onEditClicked(getArguments().getString(ARG_CHARACTER_ID), getArguments().getString(ARG_EVENT_ID));
            }
        });

        deleteButton = (Button) rootView.findViewById(R.id.delete_event_button);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mListener = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }
}
