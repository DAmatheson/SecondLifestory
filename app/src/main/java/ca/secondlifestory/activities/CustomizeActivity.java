/* CustomizeActivity.java
 * Purpose: Activity for customizing the character race/classes available
 *
 *  Created by Drew on 11/17/2015.
 */

package ca.secondlifestory.activities;

import android.os.Bundle;
import android.view.MenuItem;

import ca.secondlifestory.BaseActivity;
import ca.secondlifestory.R;

public class CustomizeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
