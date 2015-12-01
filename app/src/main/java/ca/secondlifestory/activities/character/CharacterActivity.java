/* CharacterActivity.java
 * Purpose: Activity for the character portion of the app
 *
 *  Created by Drew on 11/18/2015.
 */

package ca.secondlifestory.activities.character;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

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
public class CharacterActivity extends AppCompatActivity implements CharacterListFragment.Callbacks,
                                                                    CharacterDetailFragment.Callbacks,
                                                                    CharacterUpsertFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private CharacterListFragment listFragment;
    private CharacterDetailFragment detailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.character_layout);

        listFragment = (CharacterListFragment) getFragmentManager().findFragmentById(R.id.character_list);
        detailFragment = (CharacterDetailFragment) getFragmentManager().findFragmentById(R.id.character_detail);

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

                if (mTwoPane) {
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.character_detail, createFragment)
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

    /**
     * Callback method from {@link CharacterListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {

        detailFragment = CharacterDetailFragment.newInstance(id);

        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.

            getFragmentManager().beginTransaction()
                    .replace(R.id.character_detail, detailFragment)
                    .commit();
        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.

            getFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, detailFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    public void viewEventsButtonClicked(View view) {
        TextView idView = (TextView) ((ViewGroup)view.getParent()).findViewById(R.id.character_id);

        String characterObjectId = idView.getText().toString();

        Intent eventActivityIntent = new Intent(this, EventActivity.class);
        eventActivityIntent.putExtra(EventActivity.ARG_CHARACTER_ID, characterObjectId);

        startActivity(eventActivityIntent);
    }

    @Override
    public void onCharacterCreated(PlayerCharacter character) {

    }

    @Override
    public void onCharacterModified(PlayerCharacter character) {

    }

    @Override
    public void onCancelPressed() {
        if (mTwoPane) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.character_detail, detailFragment)
                    .commit();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onEditClicked(String characterObjectId) {
        CharacterUpsertFragment upsertFragment =
                CharacterUpsertFragment.newInstance(characterObjectId);

        if (mTwoPane) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.character_detail, upsertFragment)
                    .commit();
        } else {
            getFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, upsertFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
