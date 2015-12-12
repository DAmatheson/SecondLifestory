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

    private static final String LOG_TAG = EventDetailFragment.class.getName();

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
    private String eventId = null;

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
        try {
            Bundle args = new Bundle();
            args.putString(ARG_EVENT_ID, eventId);

            EventDetailFragment fragment = new EventDetailFragment();
            fragment.setArguments(args);

            return fragment;
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".newInstance(String): " + ex.getMessage(), ex);

            throw ex;
        }
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventDetailFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            View rootView = inflater.inflate(R.layout.fragment_event_detail, container, false);

            loadingIndicator = (ProgressBar) rootView.findViewById(R.id.loadingIndicator);

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
                    try {
                        mListener.onEditClicked(eventId);
                    } catch (Exception ex) {
                        getLogger().exception(LOG_TAG, "editButton.onClick: " + ex.getMessage(), ex);

                        throw ex;
                    }
                }
            });

            deleteButton = (Button) rootView.findViewById(R.id.delete_event_button);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        SimpleDialogFragment deleteDialog = SimpleDialogFragment.newInstance(R.string.ok,
                                R.string.delete_event_message,
                                R.string.no);

                        deleteDialog.show(getFragmentManager(), null, new SimpleDialogFragment.OnPositiveCloseListener() {
                            @Override
                            public void onPositiveClose() {
                                try {
                                    // Remove the experience from the event
                                    mItem.getCharacter().subtractExperience(mItem.getExperience() / mItem.getCharacterCount());
                                    mItem.getCharacter().saveEventually();

                                    mItem.unpinInBackground();
                                    mItem.deleteEventually();

                                    boolean deathOrResurrection = mItem.getEventType() == EventTypes.DEATH ||
                                            mItem.getEventType() == EventTypes.RESURRECT;

                                    mListener.onEventDeleted(eventId, deathOrResurrection);
                                } catch (Exception ex) {
                                    getLogger().exception(LOG_TAG,
                                        "deleteButton.onPositiveClose: " + ex.getMessage(),
                                        ex);

                                    throw ex;
                                }
                            }
                        });
                    } catch (Exception ex) {
                        getLogger().exception(LOG_TAG, "deleteButton.onClick: " + ex.getMessage(), ex);

                        throw ex;
                    }
                }
            });

            editButton.setEnabled(false);
            deleteButton.setEnabled(false);

            if (savedInstanceState != null) {
                eventId = savedInstanceState.getString(ARG_EVENT_ID);
            } else if (getArguments() != null) {
                eventId = getArguments().getString(ARG_EVENT_ID);
            }

            return rootView;
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".onCreateView: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (eventId != null) {
            try {
                loadEvent(eventId);
            } catch (Exception ex) {
                getLogger().exception(LOG_TAG, ".onResume: " + ex.getMessage(), ex);

                throw ex;
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        try {
            outState.putString(ARG_EVENT_ID, eventId);
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".onSaveInstanceState: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;

        try {
            loadEvent(eventId);
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".setEventId: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    private void loadEvent(String eventId) {
        try {
            loadingIndicator.setVisibility(View.VISIBLE);
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);

            ParseQuery<Event> query = Event.getQuery();
            query.include(Event.KEY_CHARACTER);
            query.getInBackground(eventId, new GetCallback<Event>() {
                @Override
                public void done(Event object, ParseException e) {
                    try {
                        loadingIndicator.setVisibility(View.GONE);
                    } catch (Exception ex) {
                        getLogger().exception(LOG_TAG,
                            ".loadEvent.getInBackground hide loadingIndicator: " + ex.getMessage()
                            , ex);
                    }

                    if (e == null) {
                        mItem = object;

                        try {
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
                            } else {
                                experience.setText(numberFormatter.format(mItem.getExperience()) +
                                        getString(R.string.xp_gained_postfix));
                                characterCount.setText(numberFormatter.format(mItem.getCharacterCount()) +
                                        getString(R.string.characters_present_postfix));
                            }
                            description.setText(mItem.getDescription());
                        } catch (Exception ex) {
                            getLogger().exception(LOG_TAG,
                                ".loadEvent.getInBackground: " + ex.getMessage(),
                                ex);

                            Toast.makeText(EventDetailFragment.this.getActivity(),
                                    R.string.load_event_failed_error_message,
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    } else {
                        getLogger().exception(LOG_TAG,
                                ".loadEvent query: " +
                                        e.getMessage(),
                                e);

                        Toast.makeText(EventDetailFragment.this.getActivity(),
                                R.string.load_event_failed_error_message,
                                Toast.LENGTH_LONG)
                                .show();
                    }
                }
            });
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".loadEvent: " + ex.getMessage(), ex);

            throw ex;
        }
    }
}
