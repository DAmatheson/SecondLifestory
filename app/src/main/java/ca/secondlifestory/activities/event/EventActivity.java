/* EventActivity.java
 * Purpose: Activity for the character events portion of the app
 *
 *  Created by Drew on 11/17/2015.
 */

package ca.secondlifestory.activities.event;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import ca.secondlifestory.BaseActivity;
import ca.secondlifestory.R;
import ca.secondlifestory.models.Event;
import ca.secondlifestory.models.PlayerCharacter;

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

    /**
     * The intent Bundle keys
     */
    public static final String ARG_CHARACTER_ID = "characterObjectId";

    /**
     * The serialization (saved instance state) Bundle keys
     */
    private static final String ARG_IN_UPSERT_MODE = "EventActivity.inUpsertMode";

    /**
     * Whether or not the activity is in two-pane mode, i.exception. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private int previouslySelectedListIndex = ListView.INVALID_POSITION;

    private boolean inUpsertMode = false;

    private String characterId;

    private EventListFragment listFragment;
    private EventDetailFragment detailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.event_layout);
        //setContentView(R.layout.event_twopane); // Note: Uncomment this to use two-pane

        listFragment = (EventListFragment) getFragmentManager().findFragmentById(R.id.event_list);
        if (savedInstanceState != null) {
            inUpsertMode = savedInstanceState.getBoolean(ARG_IN_UPSERT_MODE);
        } else if (findViewById(R.id.event_detail) != null) {
            detailFragment = new EventDetailFragment();

            getFragmentManager()
                .beginTransaction()
                .add(R.id.event_detail, detailFragment)
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
            }
        });

        setTitle(getString(R.string.title_activity_event));
    }

    @Override
    protected void onResume() {
        characterId = getIntent().getStringExtra(ARG_CHARACTER_ID);

        listFragment.setCharacterObjectId(characterId);

        ParseQuery<PlayerCharacter> query = PlayerCharacter.getQuery();
        query.getInBackground(characterId, new GetCallback<PlayerCharacter>() {
            @Override
            public void done(PlayerCharacter object, ParseException e) {
                if (e == null) {
                    setTitle(getString(R.string.events_title_prefix) + object.getName());
                } else {
                    // TODO: Error handling
                    Toast.makeText(EventActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();

            return;
        }

        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(ARG_IN_UPSERT_MODE, inUpsertMode);
    }

    /**
     * Callback method from {@link EventListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (inUpsertMode) {
            // Return to the details fragment
            getFragmentManager().popBackStack();
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
                    replace(android.R.id.content, detailFragment).
                    addToBackStack(null).
                    commit();
        }
    }

    @Override
    public void onListLoaded() {
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

                if (checkedEvent != null) {
                    detailFragment.setEventId(checkedEvent.getObjectId());
                }
            }
        }
    }

    //region EventDetailFragment.Callbacks methods
    @Override
    public void onEditClicked(String eventId) {
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
    }

    @Override
    public void onEventDeleted(String eventId) {
        if (mTwoPane) {
            detailFragment = new EventDetailFragment();

            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.event_detail, detailFragment)
                    .commit();
        } else {
            // Go back to the list fragment
            getFragmentManager().popBackStack();
        }

        previouslySelectedListIndex = listFragment.getListView().getCheckedItemPosition();

        listFragment.notifyListChanged();
    }
    //endregion EventDetailFragment.Callbacks methods

    //region EventUpsertFragment.Callbacks methods
    @Override
    public void onEventCreated(Event event) {
        // This will re-show the detail fragment in two-pane or the list in one-pane
        inUpsertMode = false;

        getFragmentManager().popBackStack();

        listFragment.notifyListChanged();

        // TODO: Make the list load method select the new item
        //       This is going to be hard to do while using the parse query adapter.
    }

    @Override
    public void onEventModified(Event event) {
        inUpsertMode = false;

        if (mTwoPane) {
            detailFragment.notifyEventChanged();
        }

        getFragmentManager().popBackStack();

        listFragment.notifyListChanged();
    }

    @Override
    public void onUpsertCancelPressed() {
        if (mTwoPane) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.event_detail, detailFragment)
                    .commit();
        } else {
            getFragmentManager().popBackStack();
        }
    }
    //endregion EventUpsertFragment.Callbacks methods
}
