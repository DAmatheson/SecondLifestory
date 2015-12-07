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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import ca.secondlifestory.BaseActivity;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import ca.secondlifestory.R;
import ca.secondlifestory.adapters.ParseSpinnerQueryAdapter;
import ca.secondlifestory.models.CharacterClass;
import ca.secondlifestory.models.Event;
import ca.secondlifestory.models.PlayerCharacter;
import ca.secondlifestory.models.Race;
import ca.secondlifestory.utilities.SimpleDialogFragment;

public class CustomizeActivity extends BaseActivity {

    private EditText newRaceText;
    private EditText newClassText;

    private Button saveRaceButton;
    private Button saveClassButton;

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupRaceSpinnerItems() {
        raceQueryAdapter =
            new ParseSpinnerQueryAdapter<>(this,
                new ParseQueryAdapter.QueryFactory<Race>() {
                    @Override
                    public ParseQuery<Race> create() {
                        return Race.getQuery();
                    }
                });
        raceQueryAdapter.setTextKey(Race.KEY_NAME);
        raceSpinner.setAdapter(raceQueryAdapter);
    }

    private void setupClassSpinnerItems() {
        classQueryAdapter =
            new ParseSpinnerQueryAdapter<>(this,
                new ParseQueryAdapter.QueryFactory<CharacterClass>() {
                    @Override
                    public ParseQuery<CharacterClass> create() {
                        return CharacterClass.getQuery();
                    }
                });
        classQueryAdapter.setTextKey(CharacterClass.KEY_NAME);
        classSpinner.setAdapter(classQueryAdapter);
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
                                newRace.pinInBackground();
                                newRace.saveEventually();

                                Toast.makeText(CustomizeActivity.this,
                                        R.string.new_race_created_message,
                                        Toast.LENGTH_SHORT)
                                        .show();

                                raceQueryAdapter.loadObjects();
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
                                newClass.pinInBackground();
                                newClass.saveEventually();

                                Toast.makeText(CustomizeActivity.this,
                                        R.string.new_class_created_message,
                                        Toast.LENGTH_SHORT)
                                        .show();

                                classQueryAdapter.loadObjects();
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

                                        race.unpinInBackground();
                                        race.deleteEventually();

                                        Toast.makeText(CustomizeActivity.this,
                                                R.string.race_deleted_message,
                                                Toast.LENGTH_SHORT)
                                                .show();

                                        raceQueryAdapter.loadObjects();
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

                                        characterClass.unpinInBackground();
                                        characterClass.deleteEventually();

                                        Toast.makeText(CustomizeActivity.this,
                                                R.string.class_deleted_message,
                                                Toast.LENGTH_SHORT)
                                                .show();

                                        classQueryAdapter.loadObjects();
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
