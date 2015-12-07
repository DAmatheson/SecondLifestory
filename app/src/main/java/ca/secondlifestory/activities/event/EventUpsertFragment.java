/* UpsertFragment.java
 * Purpose: Fragment for creating or editing an event
 *
 *  Created by Drew on 11/23/2015.
 */

package ca.secondlifestory.activities.event;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
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

    private HashMap<String, EventTypes> typeItems;

    // characterId is needed only for restoring purposes
    private String characterId;
    private String eventId;
    private Boolean inEditMode;

    private Callbacks mListener;

    private Event event;

    private ProgressBar loadingIndicator;
    private ArrayAdapter<String> eventTypeAdapter;

    // Inputs
    private EditText titleText;
    private Spinner eventTypeSpinner;
    private EditText characterCountText;
    private EditText xpAmountText;
    private EditText descriptionText;

    private Button saveButton;
    private Button cancelButton;


    /**
     * Use this factory method to create a new instance of
     * this fragment set into create mode.
     *
     * @param characterId The Id of the PlayerCharacter the Event belongs to
     * @return A new instance of fragment EventUpsertFragment setup for create.
     */
    public static EventUpsertFragment newInstance(String characterId) {
        EventUpsertFragment fragment = new EventUpsertFragment();

        Bundle args = new Bundle();
        args.putString(ARG_CHARACTER_ID, characterId);
        args.putBoolean(ARG_IN_EDIT_MODE, false);

        fragment.setArguments(args);

        return fragment;
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
        EventUpsertFragment fragment = new EventUpsertFragment();

        Bundle args = new Bundle();
        args.putString(ARG_CHARACTER_ID, characterId);
        args.putString(ARG_EVENT_ID, eventId);
        args.putBoolean(ARG_IN_EDIT_MODE, true);

        fragment.setArguments(args);

        return fragment;
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

        typeItems = new HashMap<>();
        typeItems.put(getString(R.string.event_type_combat), EventTypes.COMBAT);
        typeItems.put(getString(R.string.event_type_event), EventTypes.EVENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_event_upsert, container, false);

        loadingIndicator = (ProgressBar)v.findViewById(R.id.loadingIndicator);

        titleText = (EditText) v.findViewById(R.id.upsert_event_title);
        eventTypeSpinner = (Spinner) v.findViewById(R.id.upsert_event_type);
        characterCountText = (EditText) v.findViewById(R.id.upsert_event_character_count);
        xpAmountText = (EditText) v.findViewById(R.id.upsert_event_experience);
        descriptionText = (EditText) v.findViewById(R.id.upsert_event_description);

        saveButton = (Button) v.findViewById(R.id.upsert_save);
        cancelButton = (Button) v.findViewById(R.id.upsert_cancel);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventTypes value = typeItems.get(eventTypeSpinner.getSelectedItem().toString());
                String title = titleText.getText().toString().trim();
                String description = descriptionText.getText().toString().trim();
                String xpAmountString = xpAmountText.getText().toString().trim();
                String characterCountString = characterCountText.getText().toString().trim();

                String firstErrorMessage = null;

                if (title.equals("")) {
                    firstErrorMessage = "Title is required.";

                    titleText.setError("Required");
                }

                if (xpAmountString.equals("") ||
                        xpAmountString.equals("-") ||
                        xpAmountString.equals("+")) {
                    if (firstErrorMessage == null) firstErrorMessage = "XP Gained is required.";

                    xpAmountText.setError("Required.");
                }

                if (characterCountString.equals("")) {
                    if (firstErrorMessage == null)
                        firstErrorMessage = "# Characters Present is required.";

                    characterCountText.setError("Required.");
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
                            "# Characters present must be greater than zero.",
                            Toast.LENGTH_SHORT).
                            show();

                    characterCountText.setError("Must be greater than zero.");

                    return;
                }

                final int xpAmount = Integer.parseInt(xpAmountString);

                event.setTitle(title);
                event.setEventType(value);
                event.setCharacterCount(characterCount);
                event.setExperience(xpAmount);
                event.setDescription(description);

                ParseQuery<PlayerCharacter> query = PlayerCharacter.getQuery();
                query.getInBackground(characterId, new GetCallback<PlayerCharacter>() {
                    @Override
                    public void done(PlayerCharacter object, ParseException e) {
                        if (!inEditMode) {
                            object.addExperience(xpAmount / characterCount);
                        } else {
                            // TODO: Get the delta between the old and new XP amount.
                        }
                        object.saveEventually();
                    }
                });

                if (event.getDate() == null) {
                    event.setDate(new Date());
                }

                event.pinInBackground();
                event.saveEventually();

                if (!inEditMode) {
                    mListener.onEventCreated(event);
                } else {
                    mListener.onEventModified(event);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onUpsertCancelPressed();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(STATE_EVENT_TYPE_SPINNER, eventTypeSpinner.onSaveInstanceState());

        outState.putString(ARG_CHARACTER_ID, characterId);
        outState.putString(ARG_EVENT_ID, eventId);
        outState.putBoolean(ARG_IN_EDIT_MODE, inEditMode);

        outState.putString(STATE_TITLE, titleText.getText().toString());
        outState.putString(STATE_CHARACTER_COUNT, characterCountText.getText().toString());
        outState.putString(STATE_XP_AMOUNT, xpAmountText.getText().toString());
        outState.putString(STATE_DESCRIPTION, descriptionText.getText().toString());
    }

    private void setupForExistingEvent(String eventId) {
        loadingIndicator.setVisibility(View.VISIBLE);

        ((TextView)getView().findViewById(R.id.upsert_title)).setText(R.string.edit_event);

        ParseQuery<Event> query = Event.getQuery();
        query.getInBackground(eventId, new GetCallback<Event>() {
            @Override
            public void done(Event object, ParseException e) {
                loadingIndicator.setVisibility(View.GONE);

                if (e == null) {
                    event = object;

                    // TODO: Consider page titleText

                    titleText.setText(event.getTitle());
                    characterCountText.setText(Integer.toString(event.getCharacterCount()));
                    xpAmountText.setText(Integer.toString(event.getExperience()));
                    descriptionText.setText(event.getDescription());

                    // Select the type for the loaded event
                    for (Map.Entry<String, EventTypes> entry : typeItems.entrySet()) {
                        if (entry.getValue() == event.getEventType()) {
                            eventTypeSpinner.setSelection(eventTypeAdapter.getPosition(entry.getKey()));
                        }
                    }
                } else {
                    // TODO: Handle error
                    Toast.makeText(EventUpsertFragment.this.getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
