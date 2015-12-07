/* SettingsActivity.java
 * Purpose: Activity for the settings screen of the app
 *
 *  Created by Drew on 11/17/2015.
 */

package ca.secondlifestory.activities;

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

    private Button deleteCharactersButton;
    private Button clearDatabaseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        deleteCharactersButton = (Button) findViewById(R.id.delete_all_characters_button);
        clearDatabaseButton = (Button) findViewById(R.id.delete_all_data_button);

        setupDeleteCharactersButton();
        setupClearDatabaseButton();
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
                Race.deleteAllInBackground(objects);
            }
        });

        ParseQuery<CharacterClass> classQuery = CharacterClass.getQuery();
        classQuery.findInBackground(new FindCallback<CharacterClass>() {
            @Override
            public void done(List<CharacterClass> objects, ParseException e) {
                CharacterClass.deleteAllInBackground(objects);
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
                for (PlayerCharacter character : objects) {
                    ParseQuery<Event> eventsQuery = Event.getQuery();
                    eventsQuery.whereEqualTo(Event.KEY_CHARACTER, character);

                    eventsQuery.findInBackground(new FindCallback<Event>() {
                        @Override
                        public void done(List<Event> objects, ParseException e) {
                            Event.unpinAllInBackground(objects);
                            Event.deleteAllInBackground(objects);
                        }
                    });
                }

                PlayerCharacter.unpinAllInBackground(objects);
                PlayerCharacter.deleteAllInBackground(objects);
            }
        });
    }
}
