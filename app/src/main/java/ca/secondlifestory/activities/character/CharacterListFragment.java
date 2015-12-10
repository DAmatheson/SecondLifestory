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

        adapter = new PlayerCharacterQueryAdapter(getActivity());

        setListAdapter(adapter);

        adapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<PlayerCharacter>() {
            @Override
            public void onLoading() {

            }

            @Override
            public void onLoaded(List<PlayerCharacter> list, Exception e) {
                if (e == null) {
                    mListener.onListLoaded();
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_character_list, container);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        mActivatedPosition = position;

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        mListener.onItemSelected(adapter.getItem(position).getObjectId());
    }

    public void notifyListChanged() {
        adapter.notifyListChanged();
    }
}
