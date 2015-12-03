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

    public static final String ARG_CHARACTER_ID = "characterObjectId";
    /**
     * Whether or not the activity is in two-pane mode, i.exception. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private String characterId;

    private EventListFragment listFragment;
    private EventDetailFragment detailFragment;

    private ImageButton addEventButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_layout);

        listFragment = (EventListFragment) getFragmentManager().findFragmentById(R.id.event_list);
        detailFragment = (EventDetailFragment) getFragmentManager().findFragmentById(R.id.event_detail);

        addEventButton = (ImageButton) findViewById(R.id.add_event_button);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventUpsertFragment upsertFragment = EventUpsertFragment.newInstance(characterId);

                if (mTwoPane) {
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.event_detail, upsertFragment)
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
                    setTitle("Events for " + object.getName());
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

    /**
     * Callback method from {@link EventListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        detailFragment = EventDetailFragment.newInstance(getIntent().getStringExtra(ARG_CHARACTER_ID), id);

        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            getFragmentManager().beginTransaction()
                    .replace(R.id.event_detail, detailFragment)
                    .commit();
        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            getFragmentManager().
                    beginTransaction().
                    replace(android.R.id.content, detailFragment).
                    addToBackStack(null).
                    commit();
        }
    }

    @Override
    public void onEditClicked(String characterObjectId, String eventId) {
        EventUpsertFragment upsertFragment =
                EventUpsertFragment.newInstance(characterObjectId, eventId);

        if (mTwoPane) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.event_detail, upsertFragment)
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
    public void onEventCreated(Event event) {
        // TODO: Refresh list?
    }

    @Override
    public void onEventModified(Event event) {
        // TODO: Refresh list?
    }

    @Override
    public void onCancelPressed() {
        if (mTwoPane) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.event_detail, detailFragment)
                    .commit();
        } else {
            getFragmentManager().popBackStack();
        }
    }
}
