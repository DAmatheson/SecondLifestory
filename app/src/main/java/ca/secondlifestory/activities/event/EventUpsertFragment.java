/* UpsertFragment.java
 * Purpose: Fragment for creating or editing an event
 *
 *  Created by Drew on 11/23/2015.
 */

package ca.secondlifestory.activities.event;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ca.secondlifestory.BaseFragment;
import ca.secondlifestory.R;
import ca.secondlifestory.models.Event;
import ca.secondlifestory.models.EventTypes;
import ca.secondlifestory.models.PlayerCharacter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Callbacks} interface
 * to handle interaction events.
 * Use the {@link EventUpsertFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventUpsertFragment extends BaseFragment {
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface Callbacks {
        void onEventCreated(Event event);
        void onEventModified(Event event);
        void onUpsertCancelPressed();
    }

    private static final String LOG_TAG = EventUpsertFragment.class.getName();

    /**
     * The arguments Bundle keys
     */
    private static final String ARG_CHARACTER_ID = "EventUpsertFragment.characterObjectId";
    private static final String ARG_EVENT_ID = "EventUpsertFragment.eventId";
    private static final String ARG_IN_EDIT_MODE = "EventUpsertFragment.inEditMode";

    /**
     * The serialization (saved instance state) Bundle keys
     */
    private static final String STATE_TITLE = "EventUpsertFragment.titleText";
    private static final String STATE_EVENT_TYPE_SPINNER = "EventUpsertFragment.eventTypeSpinnerState";
    private static final String STATE_CHARACTER_COUNT = "EventUpsertFragment.characterCountState";
    private static final String STATE_XP_AMOUNT = "EventUpsertFragment.xpAmountState";
    private static final String STATE_DESCRIPTION = "EventUpsertFragment.descriptionState";
    private static final String STATE_OLD_XP_AMOUNT = "EventUpsertFragment.oldXpAmountState";

    private HashMap<String, EventTypes> typeItems;
    private ArrayAdapter<String> eventTypeAdapter;

    private Callbacks mListener;

    // characterId is needed only for restoring purposes
    private String characterId;
    private String eventId;
    private Boolean inEditMode;

    private Event event;

    private int oldXpAmount;

    // Inputs
    private EditText titleText;
    private Spinner eventTypeSpinner;
    private EditText characterCountText;
    private EditText xpAmountText;
    private EditText descriptionText;

    private TextView eventTypeLabel;
    private TextView experienceLabel;
    private TextView characterCountLabel;

    private Button saveButton;
    private Button cancelButton;
    private ProgressBar loadingIndicator;

    private boolean editingDeathOrResurrection;

    /**
     * Use this factory method to create a new instance of
     * this fragment set into create mode.
     *
     * @param characterId The Id of the PlayerCharacter the Event belongs to
     * @return A new instance of fragment EventUpsertFragment setup for create.
     */
    public static EventUpsertFragment newInstance(String characterId) {
        try {
            EventUpsertFragment fragment = new EventUpsertFragment();

            Bundle args = new Bundle();
            args.putString(ARG_CHARACTER_ID, characterId);
            args.putBoolean(ARG_IN_EDIT_MODE, false);

            fragment.setArguments(args);

            return fragment;
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".newInstance(String): " + ex.getMessage(), ex);

            throw ex;
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment set into edit mode for the provided EventId.
     *
     * @param characterId The Id of the PlayerCharacter the Event belongs to
     * @param eventId The Id of the Event to edit.
     * @return A new instance of fragment EventUpsertFragment setup for edit.
     */
    public static EventUpsertFragment newInstance(String characterId, String eventId) {
        try {
            EventUpsertFragment fragment = new EventUpsertFragment();

            Bundle args = new Bundle();
            args.putString(ARG_CHARACTER_ID, characterId);
            args.putString(ARG_EVENT_ID, eventId);
            args.putBoolean(ARG_IN_EDIT_MODE, true);

            fragment.setArguments(args);

            return fragment;
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".newInstance(String, String): " + ex.getMessage(), ex);

            throw ex;
        }
    }

    public EventUpsertFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (Callbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement Callbacks");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            typeItems = new HashMap<>();
            typeItems.put(getString(R.string.event_type_combat), EventTypes.COMBAT);
            typeItems.put(getString(R.string.event_type_event), EventTypes.EVENT);
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".onCreate: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            // Inflate the layout for this fragment
            View v = inflater.inflate(R.layout.fragment_event_upsert, container, false);

            loadingIndicator = (ProgressBar) v.findViewById(R.id.loadingIndicator);

            titleText = (EditText) v.findViewById(R.id.upsert_event_title);
            eventTypeSpinner = (Spinner) v.findViewById(R.id.upsert_event_type);
            characterCountText = (EditText) v.findViewById(R.id.upsert_event_character_count);
            xpAmountText = (EditText) v.findViewById(R.id.upsert_event_experience);
            descriptionText = (EditText) v.findViewById(R.id.upsert_event_description);

            eventTypeLabel = (TextView) v.findViewById(R.id.upsert_event_type_label);
            experienceLabel = (TextView) v.findViewById(R.id.upsert_event_experience_label);
            characterCountLabel = (TextView) v.findViewById(R.id.upsert_event_character_count_label);

            saveButton = (Button) v.findViewById(R.id.upsert_save);
            cancelButton = (Button) v.findViewById(R.id.upsert_cancel);

            return v;
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".onCreateView: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            eventTypeAdapter = new ArrayAdapter<>(getActivity(),
                    R.layout.dropdown_item_1line,
                    typeItems.keySet().toArray(new String[typeItems.size()]));

            eventTypeSpinner.setAdapter(eventTypeAdapter);

            if (savedInstanceState != null) {
                // Restore the saved state

                inEditMode = savedInstanceState.getBoolean(ARG_IN_EDIT_MODE);
                characterId = savedInstanceState.getString(ARG_CHARACTER_ID);
                eventId = savedInstanceState.getString(ARG_EVENT_ID);

                if (inEditMode) {
                    event = Event.createWithoutData(eventId);
                    oldXpAmount = savedInstanceState.getInt(STATE_OLD_XP_AMOUNT);
                } else {
                    event = new Event();
                }

                event.setCharacter(PlayerCharacter.createWithoutData(characterId));

                titleText.setText(savedInstanceState.getString(STATE_TITLE));
                characterCountText.setText(savedInstanceState.getString(STATE_CHARACTER_COUNT));
                xpAmountText.setText(savedInstanceState.getString(STATE_XP_AMOUNT));
                descriptionText.setText(savedInstanceState.getString(STATE_DESCRIPTION));

                eventTypeSpinner.onRestoreInstanceState(
                        savedInstanceState.getParcelable(STATE_EVENT_TYPE_SPINNER));

            } else if (getArguments() != null) {
                characterId = getArguments().getString(ARG_CHARACTER_ID);

                inEditMode = getArguments().getBoolean(ARG_IN_EDIT_MODE);

                if (inEditMode) {
                    eventId = getArguments().getString(ARG_EVENT_ID);

                    setupForExistingEvent(eventId);
                } else {
                    event = new Event();
                    event.setCharacter(PlayerCharacter.createWithoutData(characterId));
                }
            }

            saveButton.setOnClickListener(setupSaveButtonOnClickListener());

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        mListener.onUpsertCancelPressed();
                    } catch (Exception ex) {
                        getLogger().exception(LOG_TAG,
                                "cancelButton.onClick: " + ex.getMessage(),
                                ex);

                        throw ex;
                    }
                }
            });
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".onViewCreated: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    @NonNull
    private View.OnClickListener setupSaveButtonOnClickListener() {
        try {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        EventTypes value = typeItems.get(eventTypeSpinner.getSelectedItem().toString());
                        String title = titleText.getText().toString().trim();
                        String description = descriptionText.getText().toString().trim();
                        String xpAmountString = xpAmountText.getText().toString().trim();
                        String characterCountString = characterCountText.getText().toString().trim();

                        String firstErrorMessage = null;

                        if (title.equals("")) {
                            firstErrorMessage = EventUpsertFragment.this.getString(
                                    R.string.event_title_required_error_message);

                            titleText.setError(EventUpsertFragment.this.getString(R.string.required));
                        }

                        if (!editingDeathOrResurrection) {
                            if (xpAmountString.equals("") ||
                                    xpAmountString.equals("-") ||
                                    xpAmountString.equals("+")) {
                                if (firstErrorMessage == null) firstErrorMessage =
                                        EventUpsertFragment.this.getString(R.string.event_xp_gained_required_error_message);

                                xpAmountText.setError(EventUpsertFragment.this.getString(R.string.required));
                            }

                            if (characterCountString.equals("")) {
                                if (firstErrorMessage == null)
                                    firstErrorMessage = EventUpsertFragment.this.getString(
                                            R.string.event_shared_by_required_error_message);

                                characterCountText.setError(
                                        EventUpsertFragment.this.getString(R.string.required));
                            }
                        }

                        if (firstErrorMessage != null) {
                            Toast.makeText(EventUpsertFragment.this.getActivity(),
                                    firstErrorMessage,
                                    Toast.LENGTH_SHORT)
                                    .show();

                            return;
                        }

                        final int characterCount = Integer.parseInt(characterCountString);

                        if (characterCount < 1) {
                            Toast.makeText(EventUpsertFragment.this.getActivity(),
                                    R.string.event_shared_by_greater_than_zero_error_message,
                                    Toast.LENGTH_SHORT).
                                    show();

                            characterCountText.setError(
                                    EventUpsertFragment.this.getString(R.string.must_be_greater_than_zero));

                            return;
                        }

                        final int xpAmount = Integer.parseInt(xpAmountString);

                        event.setTitle(title);
                        if (!editingDeathOrResurrection) {
                            event.setEventType(value);
                            event.setCharacterCount(characterCount);
                            event.setExperience(xpAmount);
                        }
                        event.setDescription(description);

                        if (event.getDate() == null) {
                            event.setDate(new Date());
                        }

                        ParseQuery<PlayerCharacter> query = PlayerCharacter.getQuery();
                        query.getInBackground(characterId, new GetCallback<PlayerCharacter>() {
                            @Override
                            public void done(PlayerCharacter object, ParseException e) {
                                if (e == null) {
                                    try {
                                        if (inEditMode) {
                                            int newXpAmount = (xpAmount / characterCount);

                                            object.addExperience(newXpAmount - oldXpAmount);
                                        } else {
                                            object.addExperience(xpAmount / characterCount);
                                        }
                                        object.saveEventually();

                                        event.pinInBackground();
                                        event.saveEventually();

                                        if (!inEditMode) {
                                            mListener.onEventCreated(event);
                                        } else {
                                            mListener.onEventModified(event);
                                        }

                                    } catch (Exception ex) {
                                        getLogger().exception(LOG_TAG,
                                            "saveButton.onClick.getInBackground: " + ex.getMessage(),
                                            ex);

                                        Toast.makeText(EventUpsertFragment.this.getActivity(),
                                                R.string.save_event_error_message,
                                                Toast.LENGTH_LONG)
                                                .show();
                                    }
                                } else {
                                    getLogger().exception(LOG_TAG,
                                            "saveButton.onClick.getInBackground: " +
                                                    e.getMessage(),
                                            e);

                                    Toast.makeText(EventUpsertFragment.this.getActivity(),
                                            R.string.save_event_error_message,
                                            Toast.LENGTH_LONG)
                                            .show();
                                }
                            }
                        });
                    } catch (Exception ex) {
                        getLogger().exception(LOG_TAG, "saveButton.onClick: " + ex.getMessage(), ex);

                        throw ex;
                    }
                }
            };
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG,
                ".setupSaveButtonOnClickListener: " + ex.getMessage(),
                ex);

            throw ex;
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
            outState.putParcelable(STATE_EVENT_TYPE_SPINNER, eventTypeSpinner.onSaveInstanceState());

            outState.putString(ARG_CHARACTER_ID, characterId);
            outState.putString(ARG_EVENT_ID, eventId);
            outState.putBoolean(ARG_IN_EDIT_MODE, inEditMode);

            if (inEditMode) {
                outState.putInt(STATE_OLD_XP_AMOUNT, oldXpAmount);
            }

            outState.putString(STATE_TITLE, titleText.getText().toString());
            outState.putString(STATE_CHARACTER_COUNT, characterCountText.getText().toString());
            outState.putString(STATE_XP_AMOUNT, xpAmountText.getText().toString());
            outState.putString(STATE_DESCRIPTION, descriptionText.getText().toString());
        }  catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".onSaveInstanceState: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    private void setupForExistingEvent(String eventId) {
        try {
            loadingIndicator.setVisibility(View.VISIBLE);

            View rootView = getView();

            if (rootView != null) {
                ((TextView) rootView.findViewById(R.id.upsert_title)).setText(R.string.edit_event);
            }

            ParseQuery<Event> query = Event.getQuery();
            query.getInBackground(eventId, new GetCallback<Event>() {
                @Override
                public void done(Event object, ParseException e) {
                    try {
                        loadingIndicator.setVisibility(View.GONE);
                    }  catch (Exception ex) {
                        getLogger().exception(LOG_TAG,
                            ".setupForExistingEvent.getInBackground hide loadingIndicator: " + ex.getMessage(),
                            ex);

                        throw ex;
                    }

                    if (e == null) {
                        event = object;

                        try {
                            titleText.setText(event.getTitle());
                            descriptionText.setText(event.getDescription());

                            editingDeathOrResurrection = event.getEventType() == EventTypes.DEATH ||
                                    event.getEventType() == EventTypes.RESURRECT;

                            if (editingDeathOrResurrection) {
                                characterCountLabel.setVisibility(View.GONE);
                                characterCountText.setVisibility(View.GONE);
                                experienceLabel.setVisibility(View.GONE);
                                xpAmountText.setVisibility(View.GONE);
                                eventTypeLabel.setVisibility(View.GONE);
                                eventTypeSpinner.setVisibility(View.GONE);
                            }

                            characterCountText.setText(Integer.toString(event.getCharacterCount()));
                            xpAmountText.setText(Integer.toString(event.getExperience()));
                            oldXpAmount = event.getExperience() / event.getCharacterCount();

                            // Select the type for the loaded event
                            for (Map.Entry<String, EventTypes> entry : typeItems.entrySet()) {
                                if (entry.getValue() == event.getEventType()) {
                                    eventTypeSpinner.setSelection(eventTypeAdapter.getPosition(entry.getKey()));
                                }
                            }
                        }  catch (Exception ex) {
                            getLogger().exception(LOG_TAG,
                                ".setupForExistingEvent.getInBackground: " + ex.getMessage(),
                                ex);

                            Toast.makeText(EventUpsertFragment.this.getActivity(),
                                    R.string.edit_event_load_error_message,
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    } else {
                        getLogger().exception(LOG_TAG,
                                ".setupForExistingEvent query: " +
                                        e.getMessage(),
                                e);

                        Toast.makeText(EventUpsertFragment.this.getActivity(),
                                R.string.edit_event_load_error_message,
                                Toast.LENGTH_LONG)
                                .show();
                    }
                }
            });
        }  catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".setupForExistingEvent: " + ex.getMessage(), ex);

            throw ex;
        }
    }
}
