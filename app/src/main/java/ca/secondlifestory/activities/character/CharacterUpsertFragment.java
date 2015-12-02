/* UpsertFragment.java
 * Purpose: Fragment for creating or editing a character
 *
 *  Created by Drew on 11/23/2015.
 */

package ca.secondlifestory.activities.character;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.List;

import ca.secondlifestory.R;
import ca.secondlifestory.adapters.ParseSpinnerQueryAdapter;
import ca.secondlifestory.models.CharacterClass;
import ca.secondlifestory.models.PlayerCharacter;
import ca.secondlifestory.models.Race;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Callbacks} interface
 * to handle interaction events.
 * Use the {@link CharacterUpsertFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CharacterUpsertFragment extends Fragment {
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface Callbacks {
        void onCharacterCreated(PlayerCharacter character);
        void onCharacterModified(PlayerCharacter character);
        void onCancelPressed();
    }

    private static final String ARG_CHARACTER_ID = "CharacterUpsertFragment.characterObjectId";
    private static final String ARG_IN_EDIT_MODE = "CharacterUpsertFragment.inEditMode";

    private Callbacks mListener;

    private boolean inEditMode;
    private String characterId;
    private PlayerCharacter character;

    private ProgressBar loadingIndicator;

    private TextView title;
    private EditText nameText;
    private EditText detailsText;
    private Spinner raceSpinner;
    private Spinner classSpinner;

    private Button saveButton;
    private Button cancelButton;

    private ParseSpinnerQueryAdapter<Race> raceQueryAdapter;
    private ParseSpinnerQueryAdapter<CharacterClass> classQueryAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment set into create mode.
     *
     * @return A new instance of CharacterUpsertFragment setup for create.
     */
    public static CharacterUpsertFragment newInstance() {
        CharacterUpsertFragment fragment = new CharacterUpsertFragment();

        Bundle args = new Bundle();
        args.putBoolean(ARG_IN_EDIT_MODE, false);
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment set into edit mode.
     *
     * @param characterId The id of the character to edit
     * @return A new instance of CharacterUpsertFragment setup for edit.
     */
    public static CharacterUpsertFragment newInstance(String characterId) {
        CharacterUpsertFragment fragment = new CharacterUpsertFragment();

        Bundle args = new Bundle();
        args.putString(ARG_CHARACTER_ID, characterId);
        args.putBoolean(ARG_IN_EDIT_MODE, true);
        fragment.setArguments(args);

        return fragment;
    }

    public CharacterUpsertFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_character_upsert, container, false);

        loadingIndicator = (ProgressBar)v.findViewById(R.id.loadingIndicator);

        title = (TextView) v.findViewById(R.id.upsert_title);
        nameText = (EditText) v.findViewById(R.id.upsert_character_name);
        detailsText = (EditText) v.findViewById(R.id.upsert_character_details);

        raceSpinner = (Spinner) v.findViewById(R.id.upsert_character_race);
        TextView racePlaceholder = new TextView(getActivity());
        racePlaceholder.setText("Loading Races...");
        raceSpinner.setEmptyView(racePlaceholder);

        classSpinner = (Spinner) v.findViewById(R.id.upsert_character_class);
        TextView classPlaceholder = new TextView(getActivity());
        classPlaceholder.setText("Loading Classes...");
        classSpinner.setEmptyView(classPlaceholder);

        saveButton = (Button) v.findViewById(R.id.upsert_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: save stuff, validate

                if (characterId == null) {
                    mListener.onCharacterCreated(character);
                } else {
                    mListener.onCharacterModified(character);
                }
            }
        });

        cancelButton = (Button) v.findViewById(R.id.upsert_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCancelPressed();
            }
        });

        // TODO: Probably move this to resume or something. Also consider the case where getArguments is null
        if (getArguments() != null) {
            inEditMode = getArguments().getBoolean(ARG_IN_EDIT_MODE);

            if (inEditMode) {
                String characterId = getArguments().getString(ARG_CHARACTER_ID);

                setupForExistingCharacter(characterId);
            } else {
                character = new PlayerCharacter();
            }
        }

        return v;
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

    private void setupClassSpinnerItems() {
        classQueryAdapter =
            new ParseSpinnerQueryAdapter<>(getActivity(),
                new ParseQueryAdapter.QueryFactory<CharacterClass>() {
                    @Override
                    public ParseQuery<CharacterClass> create() {
                        return CharacterClass.getQuery();
                    }
                });

        classQueryAdapter.setTextKey(CharacterClass.KEY_NAME);
        classSpinner.setAdapter(classQueryAdapter);

        classQueryAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<CharacterClass>() {
            @Override
            public void onLoading() { }

            @Override
            public void onLoaded(List<CharacterClass> list, Exception e) {
                // TODO: Error handling

                CharacterClass characterClass = character.getCharacterClass();

                for (int position = 0; position < classQueryAdapter.getCount(); position++) {
                    if (classQueryAdapter.getItem(position) == characterClass) {
                        classSpinner.setSelection(position);
                        return;
                    }
                }
            }
        });


    }

    private void setupRaceSpinnerItems() {
        raceQueryAdapter =
            new ParseSpinnerQueryAdapter<>(getActivity(),
                new ParseQueryAdapter.QueryFactory<Race>() {
                    @Override
                    public ParseQuery<Race> create() {
                        ParseQuery<Race> query = Race.getQuery();

                        return query;
                    }
                });
        raceQueryAdapter.setTextKey(Race.KEY_NAME);
        raceSpinner.setAdapter(raceQueryAdapter);

        raceQueryAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<Race>() {
            @Override
            public void onLoading() { }

            @Override
            public void onLoaded(List<Race> list, Exception e) {
                // TODO: Error handling

                Race race = character.getRace();

                for (int position = 0; position < raceQueryAdapter.getCount(); position++) {
                    if (raceQueryAdapter.getItem(position) == race) {
                        raceSpinner.setSelection(position);
                        return;
                    }
                }
            }
        });
    }

    private void setupForExistingCharacter(String characterId) {
        loadingIndicator.setVisibility(View.VISIBLE);

        ParseQuery<PlayerCharacter> query = PlayerCharacter.getQuery();
        query.getInBackground(characterId, new GetCallback<PlayerCharacter>() {
            @Override
            public void done(PlayerCharacter object, ParseException e) {
                loadingIndicator.setVisibility(View.GONE);

                if (e == null) {
                    character = object;

                    title.setText(String.format("%s %s",
                            CharacterUpsertFragment.this.getString(R.string.edit),
                            character.getName()));

                    nameText.setText(character.getName());
                    detailsText.setText(character.getDetails());

                    setupRaceSpinnerItems();
                    setupClassSpinnerItems();
                } else {
                    // TODO: Handle error
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
