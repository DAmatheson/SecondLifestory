/* EventDetailFragment.java
 * Purpose: Fragment for the character event detail screen
 *
 *  Created by Drew on 11/17/2015.
 */

package ca.secondlifestory.activities.event;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import ca.secondlifestory.BaseFragment;
import ca.secondlifestory.R;
import ca.secondlifestory.models.Event;
import ca.secondlifestory.models.EventTypes;
import ca.secondlifestory.utilities.SimpleDialogFragment;

/**
 * A fragment representing a single Event detail screen.
 * This fragment is either contained in a {@link EventActivity}
 * in two-pane mode (on tablets) or a {@link EventDetailFragment}
 * on handsets.
 */
public class EventDetailFragment extends BaseFragment {
    public interface Callbacks {
        void onEditClicked(String eventId);
        void onEventDeleted(String eventId, boolean deathOrResurrection);
    }

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    private static final String ARG_EVENT_ID = "EventDetailFragment.eventObjectId";

    private Callbacks mListener;

    /**
     * The content this fragment is presenting.
     */
    private Event mItem;
    private String eventId;

    private ProgressBar loadingIndicator;

    private TextView eventTitle;
    private TextView date;
    private TextView experience;
    private TextView characterCount;
    private TextView description;

    private TextView experienceLabel;
    private TextView characterCountLabel;

    private Button editButton;
    private Button deleteButton;

    public static EventDetailFragment newInstance(String eventId) {
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);

        EventDetailFragment fragment = new EventDetailFragment();
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (exception.g. upon screen orientation changes).
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

        experienceLabel = (TextView) rootView.findViewById(R.id.xp_gained_label);
        characterCountLabel = (TextView) rootView.findViewById(R.id.characters_present_label);

        editButton = (Button) rootView.findViewById(R.id.edit_event_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onEditClicked(eventId);
            }
        });

        deleteButton = (Button) rootView.findViewById(R.id.delete_event_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDialogFragment deleteDialog = SimpleDialogFragment.newInstance(R.string.ok,
                        R.string.delete_event_message,
                        R.string.no);

                deleteDialog.show(getFragmentManager(), null, new SimpleDialogFragment.OnPositiveCloseListener() {
                    @Override
                    public void onPositiveClose() {

                        // Remove the experience from the event
                        mItem.getCharacter().subtractExperience(mItem.getExperience());
                        mItem.getCharacter().saveEventually();

                        mItem.unpinInBackground();
                        mItem.deleteEventually();

                        boolean deathOrResurrection = mItem.getEventType() == EventTypes.DEATH ||
                                mItem.getEventType() == EventTypes.RESURRECT;

                        mListener.onEventDeleted(eventId, deathOrResurrection);
                    }
                });
            }
        });

        editButton.setEnabled(false);
        deleteButton.setEnabled(false);

        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);

            loadEvent(eventId);
        }

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

    public void notifyEventChanged() {
        loadEvent(eventId);
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;

        loadEvent(eventId);
    }

    private void loadEvent(String eventId) {
        loadingIndicator.setVisibility(View.VISIBLE);
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);

        ParseQuery<Event> query = Event.getQuery();
        query.include(Event.KEY_CHARACTER);
        query.getInBackground(eventId, new GetCallback<Event>() {
            @Override
            public void done(Event object, ParseException e) {
                loadingIndicator.setVisibility(View.GONE);

                if (e == null) {
                    mItem = object;

                    editButton.setEnabled(true);
                    deleteButton.setEnabled(true);

                    NumberFormat numberFormatter = DecimalFormat.getNumberInstance();

                    @SuppressLint("SimpleDateFormat") // We want a fixed format regardless of locality
                    DateFormat dateFormatter = new SimpleDateFormat("EEE MMM d, yyyy");
                    DateFormat timeFormatter = SimpleDateFormat.getTimeInstance(DateFormat.SHORT,
                            Locale.getDefault());

                    eventTitle.setText(mItem.getTitle());
                    date.setText(dateFormatter.format(mItem.getDate()) + " " + timeFormatter.format(mItem.getDate()));
                    if (object.getEventType() == EventTypes.DEATH ||
                            object.getEventType() == EventTypes.RESURRECT) {
                        experience.setVisibility(View.GONE);
                        characterCount.setVisibility(View.GONE);
                        experienceLabel.setVisibility(View.GONE);
                        characterCountLabel.setVisibility(View.GONE);
                    }
                    else {
                        experience.setText(numberFormatter.format(mItem.getExperience()));
                        characterCount.setText(numberFormatter.format(mItem.getCharacterCount()));
                    }
                    description.setText(mItem.getDescription());
                } else {
                    // TODO: Error handling
                    Toast.makeText(EventDetailFragment.this.getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
