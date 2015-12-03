/* SettingsActivity.java
 * Purpose: Activity for the settings screen of the app
 *
 *  Created by Drew on 11/17/2015.
 */

package ca.secondlifestory.activities;

import android.os.Bundle;

import ca.secondlifestory.BaseActivity;
import ca.secondlifestory.R;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }
}
