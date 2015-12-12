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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.DeleteCallback;
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

    private ProgressBar loadingIndicator;

    private Button tutorialsLinkButton;
    private Button deleteCharactersButton;
    private Button clearDatabaseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_settings);

            tutorialsLinkButton = (Button) findViewById(R.id.tutorials);
            deleteCharactersButton = (Button) findViewById(R.id.delete_all_characters_button);
            clearDatabaseButton = (Button) findViewById(R.id.delete_all_data_button);

            loadingIndicator = (ProgressBar) findViewById(R.id.loadingIndicator);

            setupTutorialsLinkButton();
            setupDeleteCharactersButton();
            setupClearDatabaseButton();
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".onCreate: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    /**
     * Sets up the click handler for the tutorials button
     */
    private void setupTutorialsLinkButton() {
        try {
            tutorialsLinkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(TUTORIALS_URL));
                        startActivity(launchBrowser);
                    } catch (Exception ex) {
                        getLogger().exception(LOG_TAG,
                            "tutorialsLinkButton.onClick: " + ex.getMessage(),
                            ex);

                        throw ex;
                    }
                }
            });
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".setupTutorialsLinkButton: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    /**
     * Sets up the click handler for the delete all characters button
     */
    private void setupDeleteCharactersButton() {
        try {
            deleteCharactersButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        SimpleDialogFragment deleteConfirmationDialog = SimpleDialogFragment.newInstance(
                                R.string.ok,
                                R.string.delete_all_characters_dialog_title,
                                R.string.no,
                                R.string.delete_all_characters_dialog_message);

                        deleteConfirmationDialog.show(getFragmentManager(), null,
                                new SimpleDialogFragment.OnPositiveCloseListener() {
                                    @Override
                                    public void onPositiveClose() {
                                        try {
                                            loadingIndicator.setVisibility(View.VISIBLE);

                                            deleteCharacters(true);
                                        } catch (Exception ex) {
                                            getLogger().exception(LOG_TAG,
                                                "setupDeleteCharactersButton.onClick.onPositiveClose: " +
                                                        ex.getMessage(),
                                                ex);

                                            // Toasts for error messages are shown by deleteCharacters
                                        }
                                    }
                                }
                        );
                    } catch (Exception ex) {
                        getLogger().exception(LOG_TAG,
                            "deleteCharactersButton.onClick: " + ex.getMessage(),
                            ex);

                        throw ex;
                    }
                }
            });
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".setupDeleteCharactersButton: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    /**
     * Sets up the click handler for the clear database button
     */
    private void setupClearDatabaseButton() {
        try {
            clearDatabaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        SimpleDialogFragment deleteConfirmationDialog = SimpleDialogFragment.newInstance(
                                R.string.ok,
                                R.string.delete_all_data_dialog_title,
                                R.string.no,
                                R.string.delete_all_data_dialog_message);

                        deleteConfirmationDialog.show(getFragmentManager(), null,
                                new SimpleDialogFragment.OnPositiveCloseListener() {
                                    @Override
                                    public void onPositiveClose() {
                                        try {
                                            deleteAllData();
                                        } catch (Exception ex) {
                                            getLogger().exception(LOG_TAG,
                                                    "clearDatabaseButton.onClick.onPositiveClose: " +
                                                            ex.getMessage(),
                                                    ex);

                                            // Toasts for error messages are shown by deleteAllData
                                        }
                                    }
                                }
                        );
                    } catch (Exception ex) {
                        getLogger().exception(LOG_TAG,
                                "clearDatabaseButton.onClick: " + ex.getMessage(),
                                ex);

                        throw ex;
                    }
                }
            });
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".setupClearDataButton: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    /**
     * Asynchronously deletes all characters, races, and character classes
     */
    private void deleteAllData() {
        try {
            loadingIndicator.setVisibility(View.VISIBLE);

            deleteCharacters(false);

            ParseQuery<Race> raceQuery = Race.getQuery();
            raceQuery.findInBackground(new FindCallback<Race>() {
                @Override
                public void done(List<Race> objects, ParseException e) {
                    if (e == null) {
                        try {
                            Race.unpinAllInBackground(objects);
                            Race.deleteAllInBackground(objects, new DeleteCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        try {
                                            Race.setupDefaultRaces(
                                                    SettingsActivity.this.getResources()
                                                            .getStringArray(R.array.defaultRaceNames));
                                        }  catch (Exception ex) {
                                            getLogger().exception(LOG_TAG,
                                                    ".deleteAllData.raceFindInBackground.deleteAllInBackground: " +
                                                            ex.getMessage(),
                                                    ex);

                                            Toast.makeText(SettingsActivity.this,
                                                    R.string.delete_all_races_error_message,
                                                    Toast.LENGTH_LONG)
                                                    .show();
                                        }
                                    } else {
                                        getLogger().exception(LOG_TAG,
                                                ".deleteAllData.raceFindInBackground.deleteAllInBackground: " +
                                                        e.getMessage(),
                                                e);

                                        Toast.makeText(SettingsActivity.this,
                                                R.string.delete_all_races_error_message,
                                                Toast.LENGTH_LONG)
                                                .show();
                                    }
                                }
                            });
                        } catch (Exception ex) {
                            getLogger().exception(LOG_TAG,
                                ".deleteAllData.raceFindInBackground: " +
                                        ex.getMessage(),
                                ex);

                            Toast.makeText(SettingsActivity.this,
                                    R.string.delete_all_races_error_message,
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
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
                        try {
                            CharacterClass.unpinAllInBackground(objects);
                            CharacterClass.deleteAllInBackground(objects, new DeleteCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        try {
                                            CharacterClass.setupDefaultClasses(
                                                    SettingsActivity.this.getResources()
                                                            .getStringArray(R.array.defaultCharacterClassNames));

                                            loadingIndicator.setVisibility(View.GONE);

                                            // Success message
                                            Toast.makeText(SettingsActivity.this,
                                                    R.string.delete_all_data_success,
                                                    Toast.LENGTH_SHORT)
                                                    .show();
                                        } catch (Exception ex) {
                                            loadingIndicator.setVisibility(View.GONE);

                                            getLogger().exception(LOG_TAG,
                                                ".deleteAllData.classFindInBackground.deleteAllInBackground: " +
                                                        ex.getMessage(),
                                                ex);

                                            Toast.makeText(SettingsActivity.this,
                                                    R.string.delete_all_classes_error_message,
                                                    Toast.LENGTH_LONG)
                                                    .show();
                                        }
                                    } else {
                                        loadingIndicator.setVisibility(View.GONE);

                                        getLogger().exception(LOG_TAG,
                                            ".deleteAllData.classFindInBackground.deleteAllInBackground: " +
                                                    e.getMessage(),
                                            e);

                                        Toast.makeText(SettingsActivity.this,
                                                R.string.delete_all_classes_error_message,
                                                Toast.LENGTH_LONG)
                                                .show();
                                    }
                                }
                            });


                        } catch (Exception ex) {
                            getLogger().exception(LOG_TAG,
                                ".deleteAllData.classFindInBackground: " + ex.getMessage(),
                                ex);

                            Toast.makeText(SettingsActivity.this,
                                    R.string.delete_all_classes_error_message,
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
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
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".deleteAllData: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    /**
     * Asynchronously deletes all characters
     */
    private void deleteCharacters(final boolean onlyDeletingCharacters) {
        try {
            ParseQuery<PlayerCharacter> query = PlayerCharacter.getQuery();

            query.findInBackground(new FindCallback<PlayerCharacter>() {
                @Override
                public void done(List<PlayerCharacter> objects, ParseException e) {
                    if (e == null) {
                        try {
                            for (PlayerCharacter character : objects) {
                                ParseQuery<Event> eventsQuery = Event.getQuery();
                                eventsQuery.whereEqualTo(Event.KEY_CHARACTER, character);

                                eventsQuery.findInBackground(new FindCallback<Event>() {
                                    @Override
                                    public void done(List<Event> objects, ParseException e) {
                                        if (e == null) {
                                            try {
                                                Event.unpinAllInBackground(objects);
                                                Event.deleteAllInBackground(objects);
                                            } catch (Exception ex) {
                                                if (onlyDeletingCharacters) {
                                                    loadingIndicator.setVisibility(View.GONE);
                                                }

                                                getLogger().exception(LOG_TAG,
                                                    ".deleteCharacters.eventsQuery.findInBackground: " +
                                                            ex.getMessage(),
                                                    ex);

                                                throw ex;
                                            }
                                        } else {
                                            if (onlyDeletingCharacters) {
                                                loadingIndicator.setVisibility(View.GONE);
                                            }

                                            getLogger().exception(LOG_TAG,
                                                    ".deleteCharacters event deletion loop: " +
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

                            PlayerCharacter.unpinAllInBackground(objects);
                            PlayerCharacter.deleteAllInBackground(objects, new DeleteCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        try {
                                            if (onlyDeletingCharacters) {
                                                loadingIndicator.setVisibility(View.GONE);

                                                // Success messaage
                                                Toast.makeText(SettingsActivity.this,
                                                        R.string.delete_all_characters_success,
                                                        Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                        } catch (Exception ex) {
                                            getLogger().exception(LOG_TAG,
                                                    ".deleteCharacters.findInBackground.deleteAllInBackground: " +
                                                            ex.getMessage(),
                                                    ex);

                                            Toast.makeText(SettingsActivity.this,
                                                    R.string.delete_all_characters_error_message,
                                                    Toast.LENGTH_LONG)
                                                    .show();
                                        }
                                    } else {
                                        if (onlyDeletingCharacters) {
                                            loadingIndicator.setVisibility(View.GONE);
                                        }

                                        getLogger().exception(LOG_TAG,
                                                ".deleteCharacters.findInBackground.deleteAllInBackground: " +
                                                        e.getMessage(),
                                                e);

                                        Toast.makeText(SettingsActivity.this,
                                                R.string.delete_all_characters_error_message,
                                                Toast.LENGTH_LONG)
                                                .show();
                                    }
                                }
                            });
                        } catch (Exception ex) {
                            if (onlyDeletingCharacters) {
                                loadingIndicator.setVisibility(View.GONE);
                            }

                            getLogger().exception(LOG_TAG,
                                ".deleteCharacters.findInBackground: " + ex.getMessage(),
                                ex);

                            Toast.makeText(SettingsActivity.this,
                                    R.string.delete_all_characters_error_message,
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    } else {
                        if (onlyDeletingCharacters) {
                            loadingIndicator.setVisibility(View.GONE);
                        }

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
        } catch (Exception ex) {
            if (onlyDeletingCharacters) {
                loadingIndicator.setVisibility(View.GONE);
            }

            getLogger().exception(LOG_TAG, ".deleteCharacters: " + ex.getMessage(), ex);

            throw ex;
        }
    }
}
