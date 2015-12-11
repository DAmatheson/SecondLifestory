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

    private static final String LOG_TAG = EventListFragment.class.getName();

    private EventQueryAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventListFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            return inflater.inflate(R.layout.fragment_event_list, container);
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".onCreateView: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    public void setCharacterObjectId(String characterObjectId) {
        try {
            adapter = new EventQueryAdapter(getActivity(), characterObjectId);

            setListAdapter(adapter);

            adapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<Event>() {
                @Override
                public void onLoading() {

                }

                @Override
                public void onLoaded(List<Event> list, Exception e) {
                    if (e == null) {
                        try {
                            setSelection(mActivatedPosition);

                            mListener.onListLoaded();
                        } catch (Exception ex) {
                            getLogger().exception(LOG_TAG,
                                ".setCharacterObjectId.onLoaded: " + ex.getMessage(),
                                ex);

                            Toast.makeText(EventListFragment.this.getActivity(),
                                    R.string.load_events_failed_error_message,
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    } else {
                        getLogger().exception(LOG_TAG,
                                ".setCharacterObjectId.onLoaded:" +
                                        e.getMessage(),
                                e);

                        Toast.makeText(EventListFragment.this.getActivity(),
                                R.string.load_events_failed_error_message,
                                Toast.LENGTH_LONG)
                                .show();
                    }
                }
            });
        } catch (Exception ex) {
            getLogger().exception(LOG_TAG, ".setCharacterObjectId: " + ex.getMessage(), ex);

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
