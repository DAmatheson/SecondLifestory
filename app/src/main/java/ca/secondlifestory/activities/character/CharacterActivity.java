/* CharacterActivity.java
 * Purpose: Activity for the character portion of the app
 *
 *  Created by Drew on 11/18/2015.
 */

package ca.secondlifestory.activities.character;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;

import ca.secondlifestory.Application;
import ca.secondlifestory.BaseActivity;
import ca.secondlifestory.activities.CustomizeActivity;
import ca.secondlifestory.R;
import ca.secondlifestory.activities.SettingsActivity;
import ca.secondlifestory.activities.event.EventActivity;
import ca.secondlifestory.models.PlayerCharacter;

/**
 * An activity representing a list of Characters. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link CharacterDetailFragment} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link CharacterListFragment} and the item details
 * (if present) is a {@link CharacterDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link CharacterListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class CharacterActivity extends BaseActivity implements CharacterListFragment.Callbacks,
                                                                    CharacterDetailFragment.Callbacks,
                                                                    CharacterUpsertFragment.Callbacks {

    /**
     * The serialization (saved instance state) Bundle keys
     */
    private static final String ARG_IN_UPSERT_MODE = "CharacterActivity.inUpsertMode";

    private static final String TAG_DETAIL = "CharacterActivity.detailFragment";

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private int previouslySelectedListIndex = ListView.INVALID_POSITION;

    private boolean inUpsertMode = false;

    private CharacterListFragment listFragment;
    private CharacterDetailFragment detailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.character_layout);
        //setContentView(R.layout.character_twopane); // Note: Uncomment this to use two-pane

        listFragment = (CharacterListFragment) getFragmentManager().findFragmentById(R.id.character_list);

        if (savedInstanceState != null) {
            // Restore the flag. Fragments are automatically restored
            inUpsertMode = savedInstanceState.getBoolean(ARG_IN_UPSERT_MODE);

            if (findViewById(R.id.character_detail) != null) {
                detailFragment = (CharacterDetailFragment) getFragmentManager()
                        .findFragmentByTag(TAG_DETAIL);
            }
        } else if (findViewById(R.id.character_detail) != null) {
            // Create the detail fragment if in two-pane mode

            detailFragment = new CharacterDetailFragment();

            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.character_detail, detailFragment, TAG_DETAIL)
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

        ImageButton createCharacter = (ImageButton) findViewById(R.id.create_character_button);
        createCharacter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharacterUpsertFragment createFragment = CharacterUpsertFragment.newInstance();

                inUpsertMode = true;

                if (mTwoPane) {
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.character_detail, createFragment)
                            .addToBackStack(null)
                            .commit();
                } else {
                    CharacterActivity.this.getFragmentManager()
                            .beginTransaction()
                            .replace(android.R.id.content, createFragment)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        CheckBox filterDeceased = (CheckBox) findViewById(R.id.filter_show_deceased);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        filterDeceased.setChecked(
                prefs.getBoolean(Application.ARG_SHOW_DECEASED_CHARACTERS, false));

        filterDeceased.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(CharacterActivity.this);

                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(Application.ARG_SHOW_DECEASED_CHARACTERS, isChecked);
                editor.apply();

                listFragment.notifyListChanged();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);

            startActivity(intent);

            return true;
        }

        if (id == R.id.action_customize) {
            Intent intent = new Intent(this, CustomizeActivity.class);

            startActivity(intent);

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
     * Callback method from {@link CharacterListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (inUpsertMode) {
            // Return to the details fragment
            getFragmentManager().popBackStackImmediate();
        }

        if (mTwoPane) {
            // Update the detail fragment for the new id
            detailFragment.setCharacterId(id);
        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.

            detailFragment = CharacterDetailFragment.newInstance(id);

            getFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, detailFragment, TAG_DETAIL)
                    .addToBackStack(null)
                    .commit();
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
                PlayerCharacter checkedCharacter = (PlayerCharacter) listFragment.getListAdapter().
                        getItem(index);

                listFragment.setSelection(index);

                if (checkedCharacter != null && !inUpsertMode) {
                    detailFragment.setCharacterId(checkedCharacter.getObjectId());
                }
            }
        }
    }

    public void viewEventsButtonClicked(View view) {
        String characterObjectId = ((PlayerCharacter)listFragment
                .getListAdapter()
                    .getItem(listFragment.getListView().getPositionForView(view)))
            .getObjectId();

        Intent eventActivityIntent = new Intent(this, EventActivity.class);
        eventActivityIntent.putExtra(EventActivity.ARG_CHARACTER_ID, characterObjectId);

        startActivity(eventActivityIntent);
    }

    @Override
    public void onCharacterCreated(PlayerCharacter character) {
        // This will re-show the detail fragment in two-pane or the list in one-pane
        inUpsertMode = false;

        getFragmentManager().popBackStack();

        listFragment.notifyListChanged();
        listFragment.setSelection(0);
    }

    @Override
    public void onCharacterModified(PlayerCharacter character) {
        inUpsertMode = false;

        getFragmentManager().popBackStackImmediate();

        listFragment.notifyListChanged();
    }

    @Override
    public void onUpsertCancelPressed() {
        inUpsertMode = false;

        getFragmentManager().popBackStack();
    }

    @Override
    public void onEditClicked(String characterObjectId) {
        CharacterUpsertFragment upsertFragment =
                CharacterUpsertFragment.newInstance(characterObjectId);

        inUpsertMode = true;

        if (mTwoPane) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.character_detail, upsertFragment)
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
    public void onCharacterDeleted(String characterObjectId) {
        if (mTwoPane) {
            detailFragment = new CharacterDetailFragment();

            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.character_detail, detailFragment, TAG_DETAIL)
                    .commit();
        } else {
            // Go back to the list fragment
            getFragmentManager().popBackStackImmediate();
        }

        previouslySelectedListIndex = listFragment.getListView().getCheckedItemPosition();

        listFragment.notifyListChanged();
    }
}
