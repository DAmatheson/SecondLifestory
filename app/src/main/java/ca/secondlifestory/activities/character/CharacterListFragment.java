/* CharacterListFragment.java
 * Purpose: Fragment for the character list
 *
 *  Created by Drew on 11/17/2015.
 */

package ca.secondlifestory.activities.character;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseQueryAdapter;

import java.util.List;

import ca.secondlifestory.BaseListFragment;
import ca.secondlifestory.R;
import ca.secondlifestory.adapters.PlayerCharacterQueryAdapter;
import ca.secondlifestory.models.PlayerCharacter;

/**
 * A list fragment representing a list of Characters. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link CharacterDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class CharacterListFragment extends BaseListFragment {

    private static final String LOG_TAG = CharacterListFragment.class.getName();

    private PlayerCharacterQueryAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CharacterListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            adapter = new PlayerCharacterQueryAdapter(getActivity());

            setListAdapter(adapter);

            adapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<PlayerCharacter>() {
                @Override
                public void onLoading() {

                }

                @Override
                public void onLoaded(List<PlayerCharacter> list, Exception e) {
                    if (e == null) {
                        try {
                            mListener.onListLoaded();
                        } catch (Exception ex) {
                            getLogger().exception(LOG_TAG,
                                ".onCreate.onLoaded: " + ex.getMessage(),
                                ex);

                            Toast.makeText(CharacterListFragment.this.getActivity(),
                                    R.string.load_characters_failed_error_message,
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    } else {
                        getLogger().exception(LOG_TAG,
                            ".onCreate onLoaded:" +
                                    e.getMessage(),
                            e);

                        Toast.makeText(CharacterListFragment.this.getActivity(),
                            R.string.load_characters_failed_error_message,
                            Toast.LENGTH_LONG)
                            .show();
                    }
                }
            });
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".onCreate: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            return inflater.inflate(R.layout.fragment_character_list, container);
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".onCreateView: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        mActivatedPosition = position;

        try {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onItemSelected(adapter.getItem(position).getObjectId());
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".onListItemClick: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    public void notifyListChanged() {
        try {
            adapter.notifyListChanged();
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".notifyListChanged: " + ex.getMessage(), ex);

            throw ex;
        }
    }
}
