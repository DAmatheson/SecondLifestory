/* EventActivity.java
 * Purpose: Activity for the character events portion of the app
 *
 *  Created by Drew on 11/17/2015.
 */

package ca.secondlifestory.activities.event;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;

import ca.secondlifestory.BaseActivity;
import ca.secondlifestory.R;
import ca.secondlifestory.models.Event;
import ca.secondlifestory.models.EventTypes;
import ca.secondlifestory.models.PlayerCharacter;
import ca.secondlifestory.utilities.TextEntryDialogFragment;

/**
 * An activity representing a list of PlayerCharacter Events. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link EventDetailFragment} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link EventListFragment} and the item details
 * (if present) is a {@link EventDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link EventListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class EventActivity extends BaseActivity implements EventListFragment.Callbacks,
                                                           EventDetailFragment.Callbacks,
                                                           EventUpsertFragment.Callbacks {

    private static final String LOG_TAG = EventActivity.class.getName();

    /**
     * The intent Bundle keys
     */
    public static final String ARG_CHARACTER_ID = "EventActivity.characterObjectId";

    /**
     * The serialization (saved instance state) Bundle keys
     */
    private static final String ARG_IN_UPSERT_MODE = "EventActivity.inUpsertMode";

    /**
     * Fragment tags
     */
    private static final String TAG_DETAIL = "EventActivity.detailFragment";

    /**
     * Whether or not the activity is in two-pane mode, i.exception. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private int previouslySelectedListIndex = ListView.INVALID_POSITION;

    private boolean inUpsertMode = false;

    private String characterId;
    private boolean characterIsAlive;

    private EventListFragment listFragment;
    private EventDetailFragment detailFragment;

    private Button deathButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.event_layout);
            //setContentView(R.layout.event_twopane); // Note: Uncomment this to use two-pane

            listFragment = (EventListFragment) getFragmentManager().findFragmentById(R.id.event_list);
            if (savedInstanceState != null) {
                inUpsertMode = savedInstanceState.getBoolean(ARG_IN_UPSERT_MODE);

                if (findViewById(R.id.event_detail) != null) {
                    detailFragment = (EventDetailFragment) getFragmentManager().findFragmentByTag(TAG_DETAIL);
                }
            } else if (findViewById(R.id.event_detail) != null) {
                detailFragment = new EventDetailFragment();

                getFragmentManager()
                        .beginTransaction()
                        .add(R.id.event_detail, detailFragment, TAG_DETAIL)
                        .commit();
            }

            if (detailFragment != null) {
                // The detail container view will be present only in the
                // large-screen layouts (res/values-large and
                // res/values-sw600dp). If this view is present, then the
                // activity should be in two-pane mode.
                mTwoPane = true;

                // In two-pane mode, list items should be given the
                // 'activated' state when touched.
                listFragment.setActivateOnItemClick(true);
            } else {
                listFragment.setActivateOnItemClick(false);
            }

            ImageButton addEventButton = (ImageButton) findViewById(R.id.add_event_button);
            addEventButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        EventUpsertFragment upsertFragment = EventUpsertFragment.newInstance(characterId);

                        inUpsertMode = true;

                        if (mTwoPane) {
                            getFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.event_detail, upsertFragment)
                                    .addToBackStack(null)
                                    .commit();
                        } else {
                            EventActivity.this.getFragmentManager()
                                    .beginTransaction()
                                    .replace(android.R.id.content, upsertFragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    } catch (Exception ex) {
                        getLogger().exception(LOG_TAG, "addEventButton.onClick: " + ex.getMessage(), ex);

                        throw ex;
                    }
                }
            });

            deathButton = (Button) findViewById(R.id.death_button);
            deathButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleLivingDialog();
                }
            });

            setTitle(getString(R.string.title_activity_event));
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".onCreate: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            characterId = getIntent().getStringExtra(ARG_CHARACTER_ID);

            listFragment.setCharacterObjectId(characterId);
            updateDeathButton();

            ParseQuery<PlayerCharacter> query = PlayerCharacter.getQuery();
            query.getInBackground(characterId, new GetCallback<PlayerCharacter>() {
                @Override
                public void done(PlayerCharacter object, ParseException e) {
                    if (e == null) {
                        try {
                            setTitle(getString(R.string.events_title_prefix) + object.getName());
                        } catch (Exception ex) {
                            getLogger().exception(LOG_TAG,
                                    "onResume.getInBackground: " + ex.getMessage(),
                                    ex);
                        }
                    } else {
                        // Note: The title is set to "Events" by default which is an okay fallback
                        getLogger().exception(LOG_TAG, ".onResume: Failed to load character.", e);
                    }
                }
            });
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".onResume: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        try {
            outState.putBoolean(ARG_IN_UPSERT_MODE, inUpsertMode);
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".onSaveInstanceState: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            // If the Home / Up button was pressed and an item is in the backstack, pop the backstack
            if (item.getItemId() == android.R.id.home &&
                    getFragmentManager().getBackStackEntryCount() > 0) {

                getFragmentManager().popBackStack();

                if (mTwoPane) {
                    detailFragment = null;
                }

                return true;
            }

            return super.onOptionsItemSelected(item);
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".onOptionsItemSelected: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();

                return;
            }

            super.onBackPressed();
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".onBackPressed: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    /**
     * Callback method from {@link EventListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        try {
            if (inUpsertMode) {
                // Return to the details fragment
                getFragmentManager().popBackStackImmediate();
            }

            if (mTwoPane) {
                // Update the detail fragment for the new id
                detailFragment.setEventId(id);
            } else {
                // In single-pane mode, simply start the detail activity
                // for the selected item ID.

                detailFragment = EventDetailFragment.newInstance(id);

                getFragmentManager().
                        beginTransaction().
                        replace(android.R.id.content, detailFragment, TAG_DETAIL).
                        addToBackStack(null).
                        commit();
            }
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".onItemSelected: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    @Override
    public void onListLoaded() {
        try {
            if (mTwoPane) {
                int index;

                if (previouslySelectedListIndex != ListView.INVALID_POSITION) {
                    if (previouslySelectedListIndex == 0) {
                        // Select the first item again
                        index = previouslySelectedListIndex < listFragment.getListAdapter().getCount()
                                ? previouslySelectedListIndex
                                : ListView.INVALID_POSITION;
                    } else if (previouslySelectedListIndex - 1 < listFragment.getListAdapter().getCount()) {
                        // Select the item before the previously selected item
                        index = previouslySelectedListIndex - 1;
                    } else {
                        index = ListView.INVALID_POSITION;
                    }

                    previouslySelectedListIndex = ListView.INVALID_POSITION;
                } else {
                    index = listFragment.getListView().getCheckedItemPosition();
                }

                if (listFragment.getListAdapter().getCount() > 0 && index != ListView.INVALID_POSITION) {
                    Event checkedEvent = (Event) listFragment.getListAdapter().getItem(index);

                    listFragment.setSelection(index);

                    if (checkedEvent != null && !inUpsertMode) {
                        detailFragment.setEventId(checkedEvent.getObjectId());
                    }
                }
            }
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".onListLoaded: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    private void toggleLivingDialog() {
        int dialogText;
        if (characterIsAlive) {
            dialogText = R.string.what_killed_you;
        }
        else {
            dialogText = R.string.what_resurrected_you;
        }

        try {
            TextEntryDialogFragment dialog = TextEntryDialogFragment.newInstance(R.string.ok,
                    dialogText, R.string.no);

            dialog.show(getFragmentManager(), null, new TextEntryDialogFragment.OnPositiveCloseListener() {
                @Override
                public void onPositiveClose(final String textInput) {
                    try {
                        ParseQuery<PlayerCharacter> query = PlayerCharacter.getQuery();
                        query.getInBackground(characterId, new GetCallback<PlayerCharacter>() {
                            @Override
                            public void done(final PlayerCharacter character, ParseException e) {
                                if (e == null) {
                                    try {
                                        if (character.isLiving()) {
                                            characterDied(character, textInput);
                                        } else {
                                            characterResurrected(character, textInput);
                                        }
                                    } catch (Exception ex) {
                                        getLogger().exception(LOG_TAG,
                                            ".toggleLivingDialog.onPositiveClose.getInBackground: " + ex.getMessage(),
                                            ex);

                                        Toast.makeText(EventActivity.this,
                                                R.string.create_res_death_event_error_message,
                                                Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                } else {
                                    getLogger().exception(LOG_TAG,
                                            ".toggleLivingDialog onPositiveClose query: " +
                                                    e.getMessage(),
                                            e);

                                    Toast.makeText(EventActivity.this,
                                            R.string.create_res_death_event_error_message,
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        });
                    } catch (Exception ex) {
                        getLogger().exception(LOG_TAG,
                            ".toggleLivingDialog.onPositiveClose: " + ex.getMessage(),
                            ex);

                        throw ex;
                    }
                }
            });
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".toggleLivingDialog: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    private void characterDied(PlayerCharacter character, String description) {
        try {
            character.setLiving(false);
            character.pinInBackground();
            character.saveEventually();

            Event event = new Event();
            event.setCharacter(character);
            event.setTitle(getString(R.string.died));
            event.setEventType(EventTypes.DEATH);
            event.setCharacterCount(1);
            event.setExperience(0);
            event.setDescription(description);
            event.setDate(new Date());

            event.pinInBackground();
            event.saveEventually();

            listFragment.notifyListChanged();
            updateDeathButton(character);
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".characterDied: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    private void characterResurrected(PlayerCharacter character, String description) {
        try {
            character.setLiving(true);
            character.pinInBackground();
            character.saveEventually();

            final Event event = new Event();
            event.setCharacter(character);
            event.setTitle(getString(R.string.resurrected));
            event.setEventType(EventTypes.RESURRECT);
            event.setCharacterCount(1);
            event.setExperience(0);
            event.setDescription(description);
            event.setDate(new Date());

            event.pinInBackground();
            event.saveEventually();

            listFragment.notifyListChanged();
            updateDeathButton(character);
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".characterResurrected: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    private void updateDeathButton() {
        try {
            ParseQuery<PlayerCharacter> query = PlayerCharacter.getQuery();
            query.getInBackground(characterId, new GetCallback<PlayerCharacter>() {
                @Override
                public void done(PlayerCharacter object, ParseException e) {
                    if (e == null) {
                        try {
                            updateDeathButton(object);
                        } catch (Exception ex) {
                            getLogger().exception(LOG_TAG,
                                ".updateDeathButton.getInBackground: " + ex.getMessage(),
                                ex);

                            throw ex;
                        }
                    } else {
                        getLogger().exception(LOG_TAG,
                                ".updateDeathButton query: " +
                                        e.getMessage(),
                                e);

                        // Informing the user of this issue doesn't really help them
                    }
                }
            });
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".updateDeathButton: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    private void updateDeathButton(PlayerCharacter character) {
        try {
            characterIsAlive = character.isLiving();
            if (characterIsAlive) {
                deathButton.setText(getString(R.string.died));
            } else {
                deathButton.setText(getString(R.string.resurrect));
            }
            deathButton.setEnabled(true);
        }  catch (Exception ex) {
            getLogger().exception(LOG_TAG,
                ".updateDeathButton(PlayerCharacter): " + ex.getMessage(),
                ex);

            throw ex;
        }
    }

    //region EventDetailFragment.Callbacks methods
    @Override
    public void onEditClicked(String eventId) {
        try {
            EventUpsertFragment upsertFragment =
                    EventUpsertFragment.newInstance(characterId, eventId);

            inUpsertMode = true;

            if (mTwoPane) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.event_detail, upsertFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                getFragmentManager()
                        .beginTransaction()
                        .replace(android.R.id.content, upsertFragment)
                        .addToBackStack(null)
                        .commit();
            }
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".onEditClicked: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    @Override
    public void onEventDeleted(String eventId, boolean deathOrResurrection) {
        try {
            if (mTwoPane) {
                detailFragment = new EventDetailFragment();

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.event_detail, detailFragment, TAG_DETAIL)
                        .commit();
            } else {
                // Go back to the list fragment
                getFragmentManager().popBackStackImmediate();
            }

            previouslySelectedListIndex = listFragment.getListView().getCheckedItemPosition();

            listFragment.notifyListChanged();

            if (deathOrResurrection) {
                deathButton.setEnabled(false);
                ParseQuery<PlayerCharacter> query = PlayerCharacter.getQuery();
                query.getInBackground(characterId, new GetCallback<PlayerCharacter>() {
                    @Override
                    public void done(final PlayerCharacter character, ParseException e) {
                        if (e == null) {
                            try {
                                ParseQuery<Event> query = Event.getQuery();

                                query.whereContainedIn(Event.KEY_EVENT_TYPE,
                                        new ArrayList<String>() {{
                                            add(EventTypes.DEATH.toString());
                                            add(EventTypes.RESURRECT.toString());
                                        }});

                                query.orderByDescending(Event.KEY_DATE);

                                query.getFirstInBackground(new GetCallback<Event>() {
                                    @Override
                                    public void done(Event event, ParseException e) {
                                        try {
                                            if (e == null && event != null) {
                                                if (event.getEventType() == EventTypes.DEATH) {
                                                    character.setLiving(false);
                                                } else {
                                                    character.setLiving(true);
                                                }
                                                updateDeathButton(character);
                                            } else {
                                                // Assume no death/resurrect event exists and
                                                // therefore they are alive

                                                character.setLiving(true);
                                                updateDeathButton(character);
                                            }
                                        }  catch (Exception ex) {
                                            getLogger().exception(LOG_TAG,
                                                ".onEventDeleted.getInBackground.getFirstInBackground: " + ex.getMessage(),
                                                ex);

                                            Toast.makeText(EventActivity.this,
                                                    R.string.death_resurrect_deleted_error_message,
                                                    Toast.LENGTH_LONG)
                                                    .show();
                                        }
                                    }
                                });
                            } catch (Exception ex) {
                                getLogger().exception(LOG_TAG, ".onEventDeleted.getInBackground: " + ex.getMessage(), ex);

                                Toast.makeText(EventActivity.this,
                                        R.string.death_resurrect_deleted_error_message,
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        } else {
                            getLogger().exception(LOG_TAG,
                                    ".onEventDeleted deathOrResurrection query: " +
                                            e.getMessage(),
                                    e);

                            Toast.makeText(EventActivity.this,
                                    R.string.death_resurrect_deleted_error_message,
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
            }
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".onEventDeleted: " + ex.getMessage(), ex);

            throw ex;
        }
    }
    //endregion EventDetailFragment.Callbacks methods

    //region EventUpsertFragment.Callbacks methods
    @Override
    public void onEventCreated(Event event) {
        // This will re-show the detail fragment in two-pane or the list in one-pane
        inUpsertMode = false;

        try {
            getFragmentManager().popBackStack();

            listFragment.notifyListChanged();

            if (mTwoPane) {
                // Make the new event the selected one
                listFragment.setSelection(0);
            }
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".onEventCreated: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    @Override
    public void onEventModified(Event event) {
        inUpsertMode = false;

        try {
            getFragmentManager().popBackStackImmediate();

            listFragment.notifyListChanged();
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".onEventModified: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    @Override
    public void onUpsertCancelPressed() {
        inUpsertMode = false;

        try {
            getFragmentManager().popBackStack();
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".onUpsertCancelPressed: " + ex.getMessage(), ex);

            throw ex;
        }
    }
    //endregion EventUpsertFragment.Callbacks methods
}
