/* EventListFragment.java
 * Purpose: Fragment for the character event list
 *
 *  Created by Drew on 11/17/2015.
 */

package ca.secondlifestory.activities.event;

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
import ca.secondlifestory.adapters.EventQueryAdapter;
import ca.secondlifestory.models.Event;

/**
 * A list fragment representing a list of Events. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link EventDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class EventListFragment extends BaseListFragment {

    private EventQueryAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (exception.g. upon screen orientation changes).
     */
    public EventListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_list, container);

        return v;
    }

    public void setCharacterObjectId(String characterObjectId) {
        adapter = new EventQueryAdapter(getActivity(), characterObjectId);

        setListAdapter(adapter);

        adapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<Event>() {
            @Override
            public void onLoading() {

            }

            @Override
            public void onLoaded(List<Event> list, Exception e) {
                if (e == null) {
                    mListener.onListLoaded();
                } else {
                    // TODO: Error handling
                    Toast.makeText(EventListFragment.this.getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        mListener.onItemSelected(adapter.getItem(position).getObjectId());
    }

    public void notifyListChanged() {
        adapter.notifyListChanged();
    }
}
