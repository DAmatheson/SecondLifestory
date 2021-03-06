/* UpsertFragment.java
 * Purpose: Fragment for creating or editing a character
 *
 *  Created by Drew on 11/23/2015.
 */

package ca.secondlifestory.activities.character;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.parse.ParseUser;

import java.util.List;

import ca.secondlifestory.BaseFragment;
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
public class CharacterUpsertFragment extends BaseFragment {
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface Callbacks {
        void onCharacterCreated(PlayerCharacter character);
        void onCharacterModified(PlayerCharacter character);
        void onUpsertCancelPressed();
    }

    private static final String LOG_TAG = CharacterUpsertFragment.class.getName();

    /**
     * The arguments Bundle keys
     */
    private static final String ARG_CHARACTER_ID = "CharacterUpsertFragment.characterObjectId";
    private static final String ARG_IN_EDIT_MODE = "CharacterUpsertFragment.inEditMode";

    /**
     * The serialization (saved instance state) Bundle keys
     */
    private static final String STATE_CLASS_SPINNER = "CharacterUpsertFragment.classSpinnerState";
    private static final String STATE_RACE_SPINNER = "CharacterUpsertFragment.raceSpinnerState";
    private static final String STATE_NAME = "CharacterUpsertFragment.name";
    private static final String STATE_DETAILS = "CharacterUpsertFragment.details";
    private static final String RESUMING_TAG = "CharacterUpsertFragment.restoring";

    private Callbacks mListener;

    private boolean resuming = false;

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
        try {
            CharacterUpsertFragment fragment = new CharacterUpsertFragment();

            Bundle args = new Bundle();
            args.putBoolean(ARG_IN_EDIT_MODE, false);
            fragment.setArguments(args);

            return fragment;
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".newInstance: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment set into edit mode.
     *
     * @param characterId The id of the character to edit
     * @return A new instance of CharacterUpsertFragment setup for edit.
     */
    public static CharacterUpsertFragment newInstance(String characterId) {
        try {
            CharacterUpsertFragment fragment = new CharacterUpsertFragment();

            Bundle args = new Bundle();
            args.putString(ARG_CHARACTER_ID, characterId);
            args.putBoolean(ARG_IN_EDIT_MODE, true);
            fragment.setArguments(args);

            return fragment;
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".newInstance(String): " + ex.getMessage(), ex);

            throw ex;
        }
    }

    public CharacterUpsertFragment() {
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
            if (savedInstanceState != null) {
                resuming = savedInstanceState.getBoolean(RESUMING_TAG, false);
            }
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
            View v = inflater.inflate(R.layout.fragment_character_upsert, container, false);

            loadingIndicator = (ProgressBar) v.findViewById(R.id.loadingIndicator);

            title = (TextView) v.findViewById(R.id.upsert_title);
            nameText = (EditText) v.findViewById(R.id.upsert_character_name);
            detailsText = (EditText) v.findViewById(R.id.upsert_character_details);

            raceSpinner = (Spinner) v.findViewById(R.id.upsert_character_race);
            raceSpinner.setEmptyView(v.findViewById(R.id.empty_race_item));

            classSpinner = (Spinner) v.findViewById(R.id.upsert_character_class);
            classSpinner.setEmptyView(v.findViewById(R.id.empty_class_item));

            saveButton = (Button) v.findViewById(R.id.upsert_save);
            saveButton.setEnabled(false);

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
            if (savedInstanceState != null) {
                // Restore the saved state

                inEditMode = savedInstanceState.getBoolean(ARG_IN_EDIT_MODE);
                characterId = savedInstanceState.getString(ARG_CHARACTER_ID);

                if (inEditMode) {
                    // Load character
                    character = PlayerCharacter.createWithoutData(characterId);
                } else {
                    character = new PlayerCharacter();
                }

                character.setUser(ParseUser.getCurrentUser());

                nameText.setText(savedInstanceState.getString(STATE_NAME));
                detailsText.setText(savedInstanceState.getString(STATE_DETAILS));

                setupRaceSpinnerItems(savedInstanceState.getParcelable(STATE_RACE_SPINNER));
                setupClassSpinnerItems(savedInstanceState.getParcelable(STATE_CLASS_SPINNER));
            } else if (getArguments() != null) {
                inEditMode = getArguments().getBoolean(ARG_IN_EDIT_MODE);
                characterId = getArguments().getString(ARG_CHARACTER_ID);

                if (inEditMode) {
                    characterId = getArguments().getString(ARG_CHARACTER_ID);

                    setupForExistingCharacter(characterId);
                } else {
                    character = new PlayerCharacter();

                    setupRaceSpinnerItems(null);
                    setupClassSpinnerItems(null);
                }
            }

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String name = nameText.getText().toString().trim();
                        Race race = (Race) raceSpinner.getSelectedItem();
                        CharacterClass characterClass = (CharacterClass) classSpinner.getSelectedItem();

                        if (name.equals("")) {
                            Toast.makeText(getActivity(),
                                    R.string.empty_character_name_error_message,
                                    Toast.LENGTH_LONG)
                                    .show();

                            return;
                        }

                        character.setName(name);
                        character.setRace(race);
                        character.setCharacterClass(characterClass);
                        character.setDetails(detailsText.getText().toString());
                        character.setLiving(true);

                        character.setUser(ParseUser.getCurrentUser());

                        character.pinInBackground();
                        character.saveEventually();

                        if (!inEditMode) {
                            mListener.onCharacterCreated(character);
                        } else {
                            mListener.onCharacterModified(character);
                        }
                    } catch (Exception ex) {
                        getLogger().exception(LOG_TAG, "saveButton.onClick: " + ex.getMessage(), ex);

                        throw ex;
                    }
                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        mListener.onUpsertCancelPressed();
                    } catch (Exception ex) {
                        getLogger().exception(LOG_TAG, ".cancelButton.onClick: " + ex.getMessage(), ex);

                        throw ex;
                    }
                }
            });
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".onViewCreated: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (resuming) {
            // Reload the spinners so they have the newest data
            try {
                classQueryAdapter.loadObjects();
                raceQueryAdapter.loadObjects();
            } catch (Exception ex) {
                getLogger().exception(LOG_TAG, ".onResume: " + ex.getMessage(), ex);

                throw ex;
            }

            resuming = false;
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
            outState.putParcelable(STATE_RACE_SPINNER, raceSpinner.onSaveInstanceState());
            outState.putParcelable(STATE_CLASS_SPINNER, classSpinner.onSaveInstanceState());

            outState.putString(ARG_CHARACTER_ID, characterId);
            outState.putBoolean(ARG_IN_EDIT_MODE, inEditMode);

            outState.putString(STATE_NAME, nameText.getText().toString());
            outState.putString(STATE_DETAILS, detailsText.getText().toString());

            outState.putBoolean(RESUMING_TAG, true);
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".onSaveInstanceState: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    /**
     * Updates the enabled state for the create button based on the state of the spinners
     */
    private void updateSaveButtonState() {
        try {
            Object race = raceSpinner.getSelectedItem();
            Object characterClass = classSpinner.getSelectedItem();

            if (race != null && characterClass != null) {
                saveButton.setEnabled(true);
            } else {
                saveButton.setEnabled(false);
            }
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".updateCreateButtonState: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    private void setupClassSpinnerItems(@Nullable final Parcelable restoreParcelable) {
        try {
            classQueryAdapter =
                new ParseSpinnerQueryAdapter<>(getActivity(),
                    new ParseQueryAdapter.QueryFactory<CharacterClass>() {
                        @Override
                        public ParseQuery<CharacterClass> create() {
                            try {
                                return CharacterClass.getQuery().orderByAscending(CharacterClass.KEY_NAME);
                            } catch (Exception ex) {
                                getLogger().exception(LOG_TAG,
                                    "classQueryAdapter.QueryFactory.create: " + ex.getMessage(),
                                    ex);

                                throw ex;
                            }
                        }
                    }, R.layout.dropdown_item_1line_withlayout);

            classQueryAdapter.setTextKey(CharacterClass.KEY_NAME);
            classSpinner.setAdapter(classQueryAdapter);

            classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        updateSaveButtonState();
                    } catch (Exception ex) {
                        getLogger().exception(LOG_TAG,
                            "classSpinner.onItemSelected: " + ex.getMessage(),
                            ex);

                        throw ex;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    try {
                        updateSaveButtonState();
                    } catch (Exception ex) {
                        getLogger().exception(LOG_TAG,
                            "classSpinner.onNothingSelected: " + ex.getMessage(),
                            ex);

                        throw ex;
                    }
                }
            });

            if (inEditMode || restoreParcelable != null) {
                classQueryAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<CharacterClass>() {
                    @Override
                    public void onLoading() {
                    }

                    @Override
                    public void onLoaded(List<CharacterClass> list, Exception e) {
                        if (e == null) {
                            try {
                                if (restoreParcelable != null) {
                                    classSpinner.onRestoreInstanceState(restoreParcelable);
                                } else {
                                    CharacterClass characterClass = character.getCharacterClass();

                                    for (int position = 0; position < classQueryAdapter.getCount(); position++) {
                                        if (classQueryAdapter.getItem(position) == characterClass) {
                                            classSpinner.setSelection(position);
                                            return;
                                        }
                                    }
                                }
                            } catch (Exception ex) {
                                getLogger().exception(LOG_TAG,
                                    "classQueryAdapter.onLoaded: " + ex.getMessage(),
                                    ex);

                                Toast.makeText(CharacterUpsertFragment.this.getActivity(),
                                        R.string.load_classes_failed_error_message,
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        } else {
                            getLogger().exception(LOG_TAG,
                                    ".setupClassSpinnerItems onLoaded:" +
                                            e.getMessage(),
                                    e);

                            Toast.makeText(CharacterUpsertFragment.this.getActivity(),
                                    R.string.load_classes_failed_error_message,
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
            }
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".setupClassSpinnerItems: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    private void setupRaceSpinnerItems(@Nullable final Parcelable restoreParcelable) {
        try {
            raceQueryAdapter =
                    new ParseSpinnerQueryAdapter<>(getActivity(),
                            new ParseQueryAdapter.QueryFactory<Race>() {
                                @Override
                                public ParseQuery<Race> create() {
                                    try {
                                        return Race.getQuery().orderByAscending(Race.KEY_NAME);
                                    } catch (Exception ex) {
                                        getLogger().exception(LOG_TAG,
                                            "raceQueryAdapter.QueryFactory.create: " + ex.getMessage(),
                                            ex);

                                        throw ex;
                                    }
                                }
                            }, R.layout.dropdown_item_1line_withlayout);

            raceQueryAdapter.setTextKey(Race.KEY_NAME);
            raceSpinner.setAdapter(raceQueryAdapter);

            raceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        updateSaveButtonState();
                    } catch (Exception ex) {
                        getLogger().exception(LOG_TAG,
                            "raceSpinner.onItemSelected: " + ex.getMessage(),
                            ex);

                        throw ex;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    try {
                        updateSaveButtonState();
                    } catch (Exception ex) {
                        getLogger().exception(LOG_TAG,
                            "raceSpinner.onNothingSelected: " + ex.getMessage(),
                            ex);

                        throw ex;
                    }
                }
            });

            if (inEditMode || restoreParcelable != null) {
                raceQueryAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<Race>() {
                    @Override
                    public void onLoading() {
                    }

                    @Override
                    public void onLoaded(List<Race> list, Exception e) {
                        if (e == null) {
                            try {
                                if (restoreParcelable != null) {
                                    raceSpinner.onRestoreInstanceState(restoreParcelable);
                                } else {
                                    Race race = character.getRace();

                                    for (int position = 0; position < raceQueryAdapter.getCount(); position++) {
                                        if (raceQueryAdapter.getItem(position) == race) {
                                            raceSpinner.setSelection(position);
                                            return;
                                        }
                                    }
                                }
                            } catch (Exception ex) {
                                getLogger().exception(LOG_TAG,
                                    "raceQueryAdapter.onLoaded: " + ex.getMessage(),
                                    ex);

                                throw ex;
                            }
                        } else {
                            getLogger().exception(LOG_TAG,
                                    ".setupRaceSpinnerItems onLoaded:" +
                                            e.getMessage(),
                                    e);

                            Toast.makeText(CharacterUpsertFragment.this.getActivity(),
                                    R.string.load_races_failed_error_message,
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
            }
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".setupRaceSpinnerItems: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    private void setupForExistingCharacter(String characterId) {
        try {
            loadingIndicator.setVisibility(View.VISIBLE);

            ParseQuery<PlayerCharacter> query = PlayerCharacter.getQuery();
            query.getInBackground(characterId, new GetCallback<PlayerCharacter>() {
                @Override
                public void done(PlayerCharacter object, ParseException e) {
                    try {
                        loadingIndicator.setVisibility(View.GONE);
                    } catch (Exception ex) {
                        getLogger().exception(LOG_TAG,
                            ".setupForExistingCharacter.getInBackground: " + ex.getMessage(),
                            ex);

                        throw ex;
                    }

                    if (e == null) {
                        character = object;

                        try {
                            title.setText(String.format("%s %s",
                                    CharacterUpsertFragment.this.getString(R.string.edit),
                                    character.getName()));

                            nameText.setText(character.getName());
                            detailsText.setText(character.getDetails());

                            setupRaceSpinnerItems(null);
                            setupClassSpinnerItems(null);
                        } catch (Exception ex) {
                            getLogger().exception(LOG_TAG,
                                ".setupForExistingCharacter.getInBackground: " + ex.getMessage(),
                                ex);

                            Toast.makeText(getActivity(),
                                    R.string.edit_character_load_error_message,
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    } else {
                        getLogger().exception(LOG_TAG,
                                ".setupForExistingCharacter query: " +
                                        e.getMessage(),
                                e);

                        Toast.makeText(getActivity(),
                                R.string.edit_character_load_error_message,
                                Toast.LENGTH_LONG)
                                .show();
                    }
                }
            });
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".setupForExistingCharacter: " + ex.getMessage(), ex);

            throw ex;
        }
    }
}
