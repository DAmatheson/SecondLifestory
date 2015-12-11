/* CustomizeActivity.java
 * Purpose: Activity for customizing the character race/classes available
 *
 *  Created by Drew on 11/17/2015.
 */

package ca.secondlifestory.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import ca.secondlifestory.BaseActivity;

import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.List;

import ca.secondlifestory.R;
import ca.secondlifestory.adapters.ParseSpinnerQueryAdapter;
import ca.secondlifestory.models.CharacterClass;
import ca.secondlifestory.models.PlayerCharacter;
import ca.secondlifestory.models.Race;
import ca.secondlifestory.utilities.SimpleDialogFragment;

/**
 * Activity which allows the user to customize the races and classes available
 */
public class CustomizeActivity extends BaseActivity {

    private static final String LOG_TAG = CustomizeActivity.class.getName();

    private static final String STATE_NEW_RACE_TEXT = "CustomizeActivity.newRaceText";
    private static final String STATE_NEW_CLASS_TEXT = "CustomizeActivity.newClassText";

    private EditText newRaceText;
    private EditText newClassText;

    private Button saveRaceButton;
    private Button saveClassButton;

    private Space deleteRaceSpacer;
    private Space deleteClassSpacer;
    private TextView deleteRaceLabel;
    private TextView deleteClassLabel;

    private Spinner raceSpinner;
    private Spinner classSpinner;

    private Button deleteRaceButton;
    private Button deleteClassButton;

    private ParseSpinnerQueryAdapter<Race> raceQueryAdapter;
    private ParseSpinnerQueryAdapter<CharacterClass> classQueryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_customize);

            newRaceText = (EditText) findViewById(R.id.new_race_name);
            newClassText = (EditText) findViewById(R.id.new_class_name);

            saveRaceButton = (Button) findViewById(R.id.new_race_button);
            saveClassButton = (Button) findViewById(R.id.new_class_button);

            deleteRaceSpacer = (Space) findViewById(R.id.delete_race_spacer);
            deleteClassSpacer = (Space) findViewById(R.id.delete_class_spacer);
            deleteRaceLabel = (TextView) findViewById(R.id.delete_race_label);
            deleteClassLabel = (TextView) findViewById(R.id.delete_class_label);

            raceSpinner = (Spinner) findViewById(R.id.race_spinner);

            classSpinner = (Spinner) findViewById(R.id.class_spinner);

            deleteRaceButton = (Button) findViewById(R.id.delete_race_button);
            deleteClassButton = (Button) findViewById(R.id.delete_class_button);

            setupRaceSpinnerItems();
            setupClassSpinnerItems();
            setupClickListeners();

            if (savedInstanceState != null) {
                newRaceText.setText(savedInstanceState.getString(STATE_NEW_RACE_TEXT, ""));
                newClassText.setText(savedInstanceState.getString(STATE_NEW_CLASS_TEXT, ""));
            }
        }  catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".onCreate: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        try {
            outState.putString(STATE_NEW_RACE_TEXT, newRaceText.getText().toString());
            outState.putString(STATE_NEW_CLASS_TEXT, newClassText.getText().toString());
        }  catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".onSaveInstanceState: " + ex.getMessage(), ex);
        }
    }

    private void setupRaceSpinnerItems() {
        try {
            raceQueryAdapter =
                    new ParseSpinnerQueryAdapter<>(this,
                            new ParseQueryAdapter.QueryFactory<Race>() {
                                @Override
                                public ParseQuery<Race> create() {
                                    try {
                                        return Race.getQuery().orderByAscending(Race.KEY_NAME);
                                    }  catch (Exception ex) {
                                        getLogger().exception(LOG_TAG,
                                            "raceQueryAdapter.QueryFactory.create: " + ex.getMessage(),
                                            ex);

                                        throw ex;
                                    }
                                }
                            }, R.layout.dropdown_item_1line_withlayout);
            raceQueryAdapter.setTextKey(Race.KEY_NAME);
            raceSpinner.setAdapter(raceQueryAdapter);

            setDeleteRaceControlVisibility();
        }  catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".setupRaceSpinnerItems: " + ex.getMessage(), ex);
        }
    }

    private void setDeleteRaceControlVisibility() {
        try {
            Race.getQuery().countInBackground(new CountCallback() {
                @Override
                public void done(int count, ParseException e) {
                    if (e == null) {
                        try {
                            if (count == 0) {
                                deleteRaceSpacer.setVisibility(View.GONE);
                                deleteRaceLabel.setVisibility(View.GONE);
                                raceSpinner.setVisibility(View.GONE);
                                deleteRaceButton.setVisibility(View.GONE);
                            } else {
                                deleteRaceSpacer.setVisibility(View.VISIBLE);
                                deleteRaceLabel.setVisibility(View.VISIBLE);
                                raceSpinner.setVisibility(View.VISIBLE);
                                deleteRaceButton.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception ex) {
                            getLogger().exception(LOG_TAG,
                                    ".setDeleteRaceControlVisibility.countInBackground: " + ex.getMessage(),
                                    ex);
                        }
                    } else {
                        getLogger().exception(LOG_TAG,
                                ".setDeleteRaceControlVisibility countInBackground: " +
                                        e.getMessage(),
                                e);
                    }
                }
            });
        }  catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".setDeleteControlVisibility: " + ex.getMessage(), ex);
        }
    }

    private void setupClassSpinnerItems() {
        try {
            classQueryAdapter =
                    new ParseSpinnerQueryAdapter<>(this,
                            new ParseQueryAdapter.QueryFactory<CharacterClass>() {
                                @Override
                                public ParseQuery<CharacterClass> create() {
                                    try {
                                        return CharacterClass.getQuery().orderByAscending(CharacterClass.KEY_NAME);
                                    }  catch (Exception ex) {
                                        getLogger().exception(LOG_TAG,
                                            "classQueryAdapter.QueryFactory.create: " + ex.getMessage(),
                                            ex);

                                        throw ex;
                                    }
                                }
                            }, R.layout.dropdown_item_1line_withlayout);
            classQueryAdapter.setTextKey(CharacterClass.KEY_NAME);
            classSpinner.setAdapter(classQueryAdapter);

            setDeleteClassControlVisibility();
        }  catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".setupClassSpinnerItems: " + ex.getMessage(), ex);
        }
    }

    private void setDeleteClassControlVisibility() {
        try {
            CharacterClass.getQuery().countInBackground(new CountCallback() {
                @Override
                public void done(int count, ParseException e) {
                    if (e == null) {
                        try {
                            if (count == 0) {
                                deleteClassSpacer.setVisibility(View.GONE);
                                deleteClassLabel.setVisibility(View.GONE);
                                classSpinner.setVisibility(View.GONE);
                                deleteClassButton.setVisibility(View.GONE);
                            } else {
                                deleteClassSpacer.setVisibility(View.VISIBLE);
                                deleteClassLabel.setVisibility(View.VISIBLE);
                                classSpinner.setVisibility(View.VISIBLE);
                                deleteClassButton.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception ex) {
                            getLogger().exception(LOG_TAG, ".onSaveInstanceState: " + ex.getMessage(), ex);
                        }
                    } else {
                        getLogger().exception(LOG_TAG,
                                ".setDeleteClassControlVisibility countInBackground: " +
                                        e.getMessage(),
                                e);
                    }
                }
            });
        }  catch (Exception ex) {
            getLogger().exception(LOG_TAG,
                ".setDeleteClassControlVisibility: " + ex.getMessage(),
                ex);
        }
    }

    private void setupClickListeners() {
        try {
            setupSaveRaceButton();
            setupSaveClassButton();
            setupDeleteRaceButton();
            setupDeleteClassButton();
        }  catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".setupClickListeners: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    private void setupDeleteClassButton() {
        try {
            deleteClassButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        final CharacterClass characterClass = (CharacterClass) classSpinner.getSelectedItem();

                        if (characterClass == null) {
                            return;
                        }

                        ParseQuery<PlayerCharacter> characters = PlayerCharacter.getQuery();
                        characters.whereEqualTo(PlayerCharacter.KEY_CLASS, characterClass);
                        characters.findInBackground(new FindCallback<PlayerCharacter>() {
                            @Override
                            public void done(List<PlayerCharacter> objects, ParseException e) {
                                if (e == null) {
                                    try {
                                        if (!objects.isEmpty()) {
                                            Toast.makeText(CustomizeActivity.this,
                                                    R.string.cannot_delete_class,
                                                    Toast.LENGTH_LONG)
                                                    .show();
                                        } else {
                                            showDeleteClassConfirmationDialog(characterClass);
                                        }
                                    }  catch (Exception ex) {
                                        getLogger().exception(LOG_TAG,
                                            "deleteClassButton.onClick.findInBackground: " + ex.getMessage(),
                                            ex);

                                        Toast.makeText(CustomizeActivity.this,
                                                R.string.delete_class_error_message,
                                                Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                } else {
                                    getLogger().exception(LOG_TAG,
                                            ".deleteClassButton findInBackground: " +
                                                    e.getMessage(),
                                            e);

                                    Toast.makeText(CustomizeActivity.this,
                                            R.string.delete_class_error_message,
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        });
                    }  catch (Exception ex) {
                        getLogger().exception(LOG_TAG, "deleteClassButton.onClick: " + ex.getMessage(), ex);

                        throw ex;
                    }
                }
            });
        }  catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".setupDeleteClassButton: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    private void setupDeleteRaceButton() {
        try {
            deleteRaceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        final Race race = (Race) raceSpinner.getSelectedItem();

                        if (race == null) {
                            return;
                        }

                        ParseQuery<PlayerCharacter> characters = PlayerCharacter.getQuery();
                        characters.whereEqualTo(PlayerCharacter.KEY_RACE, race);
                        characters.findInBackground(new FindCallback<PlayerCharacter>() {
                            @Override
                            public void done(List<PlayerCharacter> objects, ParseException e) {
                                if (e == null) {
                                    try {
                                        if (!objects.isEmpty()) {
                                            Toast.makeText(CustomizeActivity.this,
                                                    R.string.cannot_delete_race,
                                                    Toast.LENGTH_LONG)
                                                    .show();
                                        } else {
                                            showDeleteRaceConfirmationDialog(race);
                                        }
                                    }  catch (Exception ex) {
                                        getLogger().exception(LOG_TAG,
                                            "deleteRaceButton.onClick.findInBackground: " + ex.getMessage(),
                                            ex);

                                        Toast.makeText(CustomizeActivity.this,
                                                R.string.delete_race_error_message,
                                                Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                } else {
                                    getLogger().exception(LOG_TAG,
                                            "deleteRaceButton.onClick.findInBackground: " + e.getMessage(),
                                            e);

                                    Toast.makeText(CustomizeActivity.this,
                                            R.string.delete_race_error_message,
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        });
                    }  catch (Exception ex) {
                        getLogger().exception(LOG_TAG, "deleteRaceButton.onClick: " + ex.getMessage(), ex);

                        throw ex;
                    }
                }
            });
        }  catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".setupDeleteRaceButton: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    private void setupSaveClassButton() {
        try {
            saveClassButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String newClassName = newClassText.getText().toString().trim();
                        newClassText.setText("");

                        if (newClassName.equals("")) {
                            Toast.makeText(CustomizeActivity.this,
                                    R.string.empty_class_name_error_message,
                                    Toast.LENGTH_LONG)
                                    .show();
                            return;
                        }

                        final CharacterClass newClass = new CharacterClass();
                        newClass.setName(newClassName);
                        newClass.setUser(ParseUser.getCurrentUser());

                        ParseQuery<CharacterClass> classes = CharacterClass.getQuery();
                        classes.whereEqualTo(CharacterClass.KEY_NAME, newClass.getName());
                        classes.findInBackground(new FindCallback<CharacterClass>() {
                            @Override
                            public void done(List<CharacterClass> objects, ParseException e) {
                                if (e == null) {
                                    try {
                                        if (!objects.isEmpty()) {
                                            Toast.makeText(CustomizeActivity.this,
                                                    R.string.new_class_already_exists,
                                                    Toast.LENGTH_LONG)
                                                    .show();
                                        } else {
                                            newClass.pinInBackground();
                                            newClass.saveEventually();

                                            classQueryAdapter.loadObjects();
                                            setDeleteClassControlVisibility();

                                            Toast.makeText(CustomizeActivity.this,
                                                    R.string.new_class_created_message,
                                                    Toast.LENGTH_SHORT)
                                                    .show();
                                        }
                                    }  catch (Exception ex) {
                                        getLogger().exception(LOG_TAG,
                                            "saveClassButton.onClick.findInBackground: " + ex.getMessage(),
                                            ex);

                                        Toast.makeText(CustomizeActivity.this,
                                                R.string.new_class_error_message,
                                                Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                } else {
                                    getLogger().exception(LOG_TAG,
                                            "saveClassButton.onClick.findInBackground: " +
                                                    e.getMessage(),
                                            e);

                                    Toast.makeText(CustomizeActivity.this,
                                            R.string.new_class_error_message,
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        });
                    }  catch (Exception ex) {
                        getLogger().exception(LOG_TAG,
                            "saveClassButton.onClick: " + ex.getMessage(),
                            ex);

                        throw ex;
                    }
                }
            });
        }  catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".setupSaveClassButton: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    private void setupSaveRaceButton() {
        try {
            saveRaceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String newRaceName = newRaceText.getText().toString().trim();
                        newRaceText.setText("");

                        if (newRaceName.equals("")) {
                            Toast.makeText(CustomizeActivity.this,
                                    R.string.empty_race_name_error_message,
                                    Toast.LENGTH_LONG)
                                    .show();
                            return;
                        }

                        final Race newRace = new Race();
                        newRace.setName(newRaceName);
                        newRace.setUser(ParseUser.getCurrentUser());

                        ParseQuery<Race> races = Race.getQuery();
                        races.whereEqualTo(Race.KEY_NAME, newRace.getName());
                        races.findInBackground(new FindCallback<Race>() {
                            @Override
                            public void done(List<Race> objects, ParseException e) {
                                if (e == null) {
                                    try {
                                        if (!objects.isEmpty()) {
                                            Toast.makeText(CustomizeActivity.this,
                                                    R.string.new_race_already_exists,
                                                    Toast.LENGTH_LONG)
                                                    .show();
                                        } else {
                                            newRace.pinInBackground();
                                            newRace.saveEventually();

                                            raceQueryAdapter.loadObjects();
                                            setDeleteRaceControlVisibility();

                                            Toast.makeText(CustomizeActivity.this,
                                                    R.string.new_race_created_message,
                                                    Toast.LENGTH_SHORT)
                                                    .show();
                                        }
                                    } catch (Exception ex) {
                                        getLogger().exception(LOG_TAG,
                                                "saveRaceButton.onClick.findInBackground: " + ex.getMessage(),
                                                ex);

                                        Toast.makeText(CustomizeActivity.this,
                                                R.string.new_race_error_message,
                                                Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                } else {
                                    getLogger().exception(LOG_TAG,
                                            "saveRaceButton.onClick.findInBackground: " +
                                                    e.getMessage(),
                                            e);

                                    Toast.makeText(CustomizeActivity.this,
                                            R.string.new_race_error_message,
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        });
                    } catch (Exception ex) {
                        getLogger().exception(LOG_TAG, "saveRaceButton.onClick: " + ex.getMessage(), ex);

                        throw ex;
                    }
                }
            });
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".setupSaveRaceButton: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    private void showDeleteRaceConfirmationDialog(final Race race) {
        try {
            SimpleDialogFragment deleteDialog = SimpleDialogFragment.newInstance(R.string.ok,
                    R.string.delete_race_confirm,
                    R.string.no);

            deleteDialog.show(getFragmentManager(), null, new SimpleDialogFragment.OnPositiveCloseListener() {
                @Override
                public void onPositiveClose() {
                    try {
                        ParseQuery<Race> races = Race.getQuery();
                        races.whereEqualTo(Race.KEY_NAME, race);
                        races.findInBackground(new FindCallback<Race>() {
                            @Override
                            public void done(List<Race> objects, ParseException e) {
                                if (e == null) {
                                    try {
                                        Race.deleteAllInBackground(objects);
                                    } catch (Exception ex) {
                                        getLogger().exception(LOG_TAG,
                                            ".showDeleteRaceConfirmationDialog.onPositiveClose.findInBackground: " +
                                                    ex.getMessage(),
                                            ex);

                                        Toast.makeText(CustomizeActivity.this,
                                                R.string.delete_race_error_message,
                                                Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                } else {
                                    getLogger().exception(LOG_TAG,
                                            ".showDeleteRaceConfirmationDialog.onPositiveClose.findInBackground: " +
                                                    e.getMessage(),
                                            e);

                                    Toast.makeText(CustomizeActivity.this,
                                            R.string.delete_race_error_message,
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        });

                        race.unpinInBackground();
                        race.deleteEventually();

                        raceQueryAdapter.loadObjects();
                        setDeleteRaceControlVisibility();

                        Toast.makeText(CustomizeActivity.this,
                                R.string.race_deleted_message,
                                Toast.LENGTH_SHORT)
                                .show();
                    } catch (Exception ex) {
                        getLogger().exception(LOG_TAG,
                            ".showDeleteRaceConfirmationDialog.onPositiveClose: " + ex.getMessage(),
                            ex);

                        throw ex;
                    }
                }
            });
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".showDeleteRaceConfirmationDialog: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    private void showDeleteClassConfirmationDialog(final CharacterClass characterClass) {
        try {
            SimpleDialogFragment deleteDialog = SimpleDialogFragment.newInstance(R.string.ok,
                    R.string.delete_class_confirm,
                    R.string.no);

            deleteDialog.show(getFragmentManager(), null, new SimpleDialogFragment.OnPositiveCloseListener() {
                @Override
                public void onPositiveClose() {
                    try {
                        ParseQuery<CharacterClass> classes = CharacterClass.getQuery();
                        classes.whereEqualTo(CharacterClass.KEY_NAME, characterClass);
                        classes.findInBackground(new FindCallback<CharacterClass>() {
                            @Override
                            public void done(List<CharacterClass> objects, ParseException e) {
                                if (e == null) {
                                    try {
                                        CharacterClass.deleteAllInBackground(objects);
                                    } catch (Exception ex) {
                                        getLogger().exception(LOG_TAG,
                                            ".showDeleteClassConfirmationDialog.onPositiveClose.findInBackground: " +
                                                    ex.getMessage(),
                                            ex);

                                        Toast.makeText(CustomizeActivity.this,
                                                R.string.delete_class_error_message,
                                                Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                } else {
                                    getLogger().exception(LOG_TAG,
                                            ".showDeleteClassConfirmationDialog.onPositiveClose.findInBackground: " +
                                                    e.getMessage(),
                                            e);

                                    Toast.makeText(CustomizeActivity.this,
                                            R.string.delete_class_error_message,
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        });

                        characterClass.unpinInBackground();
                        characterClass.deleteEventually();

                        classQueryAdapter.loadObjects();
                        setDeleteClassControlVisibility();

                        Toast.makeText(CustomizeActivity.this,
                                R.string.class_deleted_message,
                                Toast.LENGTH_SHORT)
                                .show();
                    } catch (Exception ex) {
                        getLogger().exception(LOG_TAG,
                            ".showDeleteClassConfirmationDialog.deleteDialog.onPositiveClose: " +
                                    ex.getMessage(),
                            ex);

                        throw ex;
                    }
                }
            });
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".showDeleteClassConfirmationDialog: " + ex.getMessage(), ex);

            throw ex;
        }
    }
}
