/* BaseActivity.java
 * Purpose: Base activity class containing common code for all fragments
 *
 * Created by Drew on 12/2/2015.
 */

package ca.secondlifestory;

import android.support.v7.app.AppCompatActivity;

import ca.secondlifestory.utilities.LifestoryLogger;

/**
 * Base Activity class for Second Lifestory
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected LifestoryLogger getLogger() {
        return ((Application)getApplication()).getLogger();
    }
}
