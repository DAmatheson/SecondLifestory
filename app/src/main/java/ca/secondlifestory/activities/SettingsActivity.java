/* SettingsActivity.java
 * Purpose: Activity for the settings screen of the app
 *
 *  Created by Drew on 11/17/2015.
 */

package ca.secondlifestory.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

import ca.secondlifestory.BaseActivity;
import ca.secondlifestory.R;
import ca.secondlifestory.models.CharacterClass;
import ca.secondlifestory.models.Event;
import ca.secondlifestory.models.PlayerCharacter;
import ca.secondlifestory.models.Race;
import ca.secondlifestory.utilities.SimpleDialogFragment;

public class SettingsActivity extends BaseActivity {

    private static final String LOG_TAG = SettingsActivity.class.getName();

    private static final String TUTORIALS_URL = "http://www.isaac-west.ca/SecondLifestory/FAQ";

    private Button tutorialsLinkButton;
    private Button deleteCharactersButton;
    private Button clearDatabaseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        tutorialsLinkButton = (Button) findViewById(R.id.tutorials);
        deleteCharactersButton = (Button) findViewById(R.id.delete_all_characters_button);
        clearDatabaseButton = (Button) findViewById(R.id.delete_all_data_button);

        setupTutorialsLinkButton();
        setupDeleteCharactersButton();
        setupClearDatabaseButton();
    }

    /**
     * Sets up the click handler for the tutorials button
     */
    private void setupTutorialsLinkButton() {
        tutorialsLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(TUTORIALS_URL));
                startActivity(launchBrowser);
            }
        });
    }

    /**
     * Sets up the click handler for the delete all characters button
     */
    private void setupDeleteCharactersButton() {
        deleteCharactersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDialogFragment deleteConfirmationDialog = SimpleDialogFragment.newInstance(
                        R.string.ok,
                        R.string.delete_all_characters_dialog_title,
                        R.string.no,
                        R.string.delete_all_characters_dialog_message);

                deleteConfirmationDialog.show(getFragmentManager(), null,
                        new SimpleDialogFragment.OnPositiveCloseListener() {
                            @Override
                            public void onPositiveClose() {
                                deleteCharacters();

                                Toast.makeText(SettingsActivity.this,
                                        R.string.delete_all_characters_success,
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                );
            }
        });
    }

    /**
     * Sets up the click handler for the clear database button
     */
    private void setupClearDatabaseButton() {
        clearDatabaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDialogFragment deleteConfirmationDialog = SimpleDialogFragment.newInstance(
                        R.string.ok,
                        R.string.delete_all_data_dialog_title,
                        R.string.no,
                        R.string.delete_all_data_dialog_message);

                deleteConfirmationDialog.show(getFragmentManager(), null,
                        new SimpleDialogFragment.OnPositiveCloseListener() {
                            @Override
                            public void onPositiveClose() {
                                deleteAllData();

                                Toast.makeText(SettingsActivity.this,
                                        R.string.delete_all_data_success,
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                );
            }
        });
    }

    /**
     * Asynchronously deletes all characters, races, and character classes
     */
    private void deleteAllData() {
        deleteCharacters();

        ParseQuery<Race> raceQuery = Race.getQuery();
        raceQuery.findInBackground(new FindCallback<Race>() {
            @Override
            public void done(List<Race> objects, ParseException e) {
                if (e == null) {

                    Race.deleteAllInBackground(objects);

                    Race.setupDefaultRaces(
                            SettingsActivity.this.getResources()
                                    .getStringArray(R.array.defaultRaceNames));
                } else {
                    getLogger().exception(LOG_TAG,
                            ".deleteAllData Race query: " +
                                    e.getMessage(),
                            e);

                    Toast.makeText(SettingsActivity.this,
                            R.string.delete_all_races_error_message,
                            Toast.LENGTH_LONG)
                        .show();
                }
            }
        });

        ParseQuery<CharacterClass> classQuery = CharacterClass.getQuery();
        classQuery.findInBackground(new FindCallback<CharacterClass>() {
            @Override
            public void done(List<CharacterClass> objects, ParseException e) {
                if (e == null) {
                    CharacterClass.deleteAllInBackground(objects);

                    CharacterClass.setupDefaultClasses(
                            SettingsActivity.this.getResources()
                                    .getStringArray(R.array.defaultCharacterClassNames));
                } else {
                    getLogger().exception(LOG_TAG,
                            ".deleteAllData CharacterClass query: " +
                                    e.getMessage(),
                            e);

                    Toast.makeText(SettingsActivity.this,
                            R.string.delete_all_classes_error_message,
                            Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    /**
     * Asynchronously deletes all characters
     */
    private void deleteCharacters() {
        ParseQuery<PlayerCharacter> query = PlayerCharacter.getQuery();

        query.findInBackground(new FindCallback<PlayerCharacter>() {
            @Override
            public void done(List<PlayerCharacter> objects, ParseException e) {
                if (e == null) {
                    for (PlayerCharacter character : objects) {
                        ParseQuery<Event> eventsQuery = Event.getQuery();
                        eventsQuery.whereEqualTo(Event.KEY_CHARACTER, character);

                        eventsQuery.findInBackground(new FindCallback<Event>() {
                            @Override
                            public void done(List<Event> objects, ParseException e) {
                                if (e == null) {
                                    Event.unpinAllInBackground(objects);
                                    Event.deleteAllInBackground(objects);
                                } else {
                                    getLogger().exception(LOG_TAG,
                                            ".deleteCharacters event deletion loop: " +
                                                e.getMessage(),
                                            e);
                                }
                            }
                        });
                    }

                    PlayerCharacter.unpinAllInBackground(objects);
                    PlayerCharacter.deleteAllInBackground(objects);
                } else {
                    getLogger().exception(LOG_TAG,
                            ".deleteCharacters query: " +
                                e.getMessage(),
                            e);

                    Toast.makeText(SettingsActivity.this,
                            R.string.delete_all_characters_error_message,
                            Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }
}
