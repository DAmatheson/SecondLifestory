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

import ca.secondlifestory.R;
import ca.secondlifestory.models.Event;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Callbacks} interface
 * to handle interaction events.
 * Use the {@link EventUpsertFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventUpsertFragment extends Fragment {
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface Callbacks {
        void onEventCreated(Event event);
        void onEventModified(Event event);
        void onCancelPressed();
    }

    private static final String ARG_CHARACTER_ID = "EventUpsertFragment.characterObjectId";
    private static final String ARG_EVENT_ID = "EventUpsertFragment.eventId";
    private static final String ARG_IN_EDIT_MODE = "EventUpsertFragment.inEditMode";

    private String characterId;
    private String eventId;
    private Boolean inEditMode;

    private Callbacks mListener;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            characterId = getArguments().getString(ARG_CHARACTER_ID);
            eventId = getArguments().getString(ARG_EVENT_ID);
            inEditMode = getArguments().getBoolean(ARG_IN_EDIT_MODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_upsert, container, false);
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
