/* CustomizeActivity.java
 * Purpose: Activity for customizing the character race/classes available
 *
 *  Created by Drew on 11/17/2015.
 */

package ca.secondlifestory.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import ca.secondlifestory.BaseActivity;

import com.parse.CountCallback;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import ca.secondlifestory.R;
import ca.secondlifestory.adapters.ParseSpinnerQueryAdapter;
import ca.secondlifestory.models.CharacterClass;
import ca.secondlifestory.models.PlayerCharacter;
import ca.secondlifestory.models.Race;
import ca.secondlifestory.utilities.SimpleDialogFragment;

public class CustomizeActivity extends BaseActivity {

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
        TextView racePlaceholder = new TextView(this);
        racePlaceholder.setText("Loading Races...");
        raceSpinner.setEmptyView(racePlaceholder);

        classSpinner = (Spinner) findViewById(R.id.class_spinner);
        TextView classPlaceholder = new TextView(this);
        classPlaceholder.setText("Loading Classes...");
        classSpinner.setEmptyView(classPlaceholder);

        deleteRaceButton = (Button) findViewById(R.id.delete_race_button);
        deleteClassButton = (Button) findViewById(R.id.delete_class_button);

        setupRaceSpinnerItems();
        setupClassSpinnerItems();
        setupClickListeners();
    }

    private void setupRaceSpinnerItems() {
        raceQueryAdapter =
            new ParseSpinnerQueryAdapter<>(this,
                new ParseQueryAdapter.QueryFactory<Race>() {
                    @Override
                    public ParseQuery<Race> create() {
                        return Race.getQuery();
                    }
                }, R.layout.dropdown_item_1line_withlayout);
        raceQueryAdapter.setTextKey(Race.KEY_NAME);
        raceSpinner.setAdapter(raceQueryAdapter);

        setDeleteRaceControlVisibility();
    }

    private void setDeleteRaceControlVisibility() {
        Race.getQuery().countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if (e == null) {
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
                }
            }
        });
    }

    private void setupClassSpinnerItems() {
        classQueryAdapter =
            new ParseSpinnerQueryAdapter<>(this,
                new ParseQueryAdapter.QueryFactory<CharacterClass>() {
                    @Override
                    public ParseQuery<CharacterClass> create() {
                        return CharacterClass.getQuery();
                    }
                }, R.layout.dropdown_item_1line_withlayout);
        classQueryAdapter.setTextKey(CharacterClass.KEY_NAME);
        classSpinner.setAdapter(classQueryAdapter);

        setDeleteClassControlVisibility();
    }

    private void setDeleteClassControlVisibility() {
        CharacterClass.getQuery().countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if (e == null) {
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
                }
            }
        });
    }

    private void setupClickListeners() {
        saveRaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        if (e == null){
                            if (!objects.isEmpty()) {
                                Toast.makeText(CustomizeActivity.this,
                                        R.string.new_race_already_exists,
                                        Toast.LENGTH_LONG)
                                        .show();
                            } else {
                                newRace.pinInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        newRace.saveEventually();
                                        raceQueryAdapter.loadObjects();
                                        setDeleteRaceControlVisibility();

                                        Toast.makeText(CustomizeActivity.this,
                                                R.string.new_race_created_message,
                                                Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                });

                            }
                        }
                    }
                });
            }
        });

        saveClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        if (e == null){
                            if (!objects.isEmpty()) {
                                Toast.makeText(CustomizeActivity.this,
                                        R.string.new_class_already_exists,
                                        Toast.LENGTH_LONG)
                                        .show();
                            } else {
                                newClass.pinInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        newClass.saveEventually();
                                        classQueryAdapter.loadObjects();
                                        setDeleteClassControlVisibility();

                                        Toast.makeText(CustomizeActivity.this,
                                                R.string.new_class_created_message,
                                                Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });

        deleteRaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                            if (!objects.isEmpty()) {
                                Toast.makeText(CustomizeActivity.this,
                                        R.string.cannot_delete_race,
                                        Toast.LENGTH_LONG)
                                        .show();
                            } else {
                                SimpleDialogFragment deleteDialog = SimpleDialogFragment.newInstance(R.string.ok,
                                        R.string.delete_race_confirm,
                                        R.string.no);

                                deleteDialog.show(getFragmentManager(), null, new SimpleDialogFragment.OnPositiveCloseListener() {
                                    @Override
                                    public void onPositiveClose() {
                                        ParseQuery<Race> races = Race.getQuery();
                                        races.whereEqualTo(Race.KEY_NAME, race);
                                        races.findInBackground(new FindCallback<Race>() {
                                            @Override
                                            public void done(List<Race> objects, ParseException e) {
                                                if (e == null) {
                                                    Race.deleteAllInBackground(objects);
                                                }
                                            }
                                        });

                                        race.unpinInBackground(new DeleteCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                race.deleteEventually();
                                                raceQueryAdapter.loadObjects();
                                                setDeleteRaceControlVisibility();

                                                Toast.makeText(CustomizeActivity.this,
                                                        R.string.race_deleted_message,
                                                        Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });

        deleteClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                            if (!objects.isEmpty()) {
                                Toast.makeText(CustomizeActivity.this,
                                        R.string.cannot_delete_class,
                                        Toast.LENGTH_LONG)
                                        .show();
                            } else {
                                SimpleDialogFragment deleteDialog = SimpleDialogFragment.newInstance(R.string.ok,
                                        R.string.delete_class_confirm,
                                        R.string.no);

                                deleteDialog.show(getFragmentManager(), null, new SimpleDialogFragment.OnPositiveCloseListener() {
                                    @Override
                                    public void onPositiveClose() {
                                        ParseQuery<CharacterClass> classes = CharacterClass.getQuery();
                                        classes.whereEqualTo(CharacterClass.KEY_NAME, characterClass);
                                        classes.findInBackground(new FindCallback<CharacterClass>() {
                                            @Override
                                            public void done(List<CharacterClass> objects, ParseException e) {
                                                if (e == null) {
                                                    CharacterClass.deleteAllInBackground(objects);
                                                }
                                            }
                                        });

                                        characterClass.unpinInBackground(new DeleteCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                characterClass.deleteEventually();
                                                classQueryAdapter.loadObjects();
                                                setDeleteClassControlVisibility();

                                                Toast.makeText(CustomizeActivity.this,
                                                        R.string.class_deleted_message,
                                                        Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
    }
}
