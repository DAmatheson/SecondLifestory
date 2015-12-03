/* BaseFragment.java
 * Purpose: Base fragment class containing common code for all fragments
 *
 * Created by Drew on 12/2/2015.
 */

package ca.secondlifestory;

import android.app.Fragment;

import ca.secondlifestory.utilities.LifestoryLogger;

/**
 * Base fragment class for Second Lifestory
 */
public abstract class BaseFragment extends Fragment {

    /**
     * Get the logger for the application
     * @return  The logger for the application
     */
    protected LifestoryLogger getLogger() {
        return ((Application)getActivity().getApplication()).getLogger();
    }
}
