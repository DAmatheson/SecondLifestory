/* BaseListFragment.java
 * Purpose: Base ListFragment class containing common code for all fragments
 *
 * Created by Drew on 12/2/2015.
 */

package ca.secondlifestory;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import ca.secondlifestory.utilities.LifestoryLogger;

/**
 * Base ListFragment class for LifeStory
 */
public class BaseListFragment extends android.app.ListFragment {
    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        void onItemSelected(String id);
        void onListLoaded();
    }

    /**
     * The serialization (saved instance state) Bundle keys
     */
    protected static final String STATE_ACTIVATED_POSITION = "BaseListFragment.activatedPosition";
    protected static final String LIST_STATE = "BaseListFragment.listStateParcelable";

    /**
     * The current activated item position. Only used on tablets.
     */
    protected int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    protected Callbacks mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mListener = (Callbacks) activity;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null) {
            View list = view.findViewById(android.R.id.list);

            if (list != null) {
                ((ListView)list).
                        onRestoreInstanceState(savedInstanceState.getParcelable(LIST_STATE));
            }

            if (savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
                setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(LIST_STATE, getListView().onSaveInstanceState());

        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(position);

        setActivatedPosition(position);
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    protected LifestoryLogger getLogger() {
        return ((Application)getActivity().getApplication()).getLogger();
    }

    protected void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }
}
