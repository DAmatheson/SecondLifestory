/* CustomizeActivity.java
 * Purpose: Activity for customizing the character race/classes available
 *
 *  Created by Drew on 11/17/2015.
 */

package ca.secondlifestory.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import ca.secondlifestory.BaseActivity;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import ca.secondlifestory.R;
import ca.secondlifestory.adapters.ParseSpinnerQueryAdapter;
import ca.secondlifestory.models.CharacterClass;
import ca.secondlifestory.models.Race;

public class CustomizeActivity extends BaseActivity {

    private EditText newRaceText;
    private EditText newClassText;

    private Button saveRaceButton;
    private Button saveClassButton;

    private Spinner raceSpinner;
    private Spinner classSpinner;

    private Button deleteRaceButton;
    private Button deleteClassButton;

    private ParseSpinnerQueryAdapter<Race> raceQueryAdapter;
    private ParseSpinnerQueryAdapter<CharacterClass> classQueryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize);

        newRaceText = (EditText) findViewById(R.id.new_race_name);
        newClassText = (EditText) findViewById(R.id.new_class_name);

        saveRaceButton = (Button) findViewById(R.id.new_race_button);
        saveClassButton = (Button) findViewById(R.id.new_class_button);

        raceSpinner = (Spinner) findViewById(R.id.race_spinner);
        TextView racePlaceholder = new TextView(this);
        racePlaceholder.setText("Loading Races...");
        raceSpinner.setEmptyView(racePlaceholder);

        classSpinner = (Spinner) findViewById(R.id.class_spinner);
        TextView classPlaceholder = new TextView(this);
        classPlaceholder.setText("Loading Classes...");
        classSpinner.setEmptyView(classPlaceholder);

        deleteRaceButton = (Button) findViewById(R.id.delete_race_button);
        deleteClassButton = (Button) findViewById(R.id.delete_class_button);

        setupRaceSpinnerItems();
        setupClassSpinnerItems();

        saveRaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newRaceName = newRaceText.getText().toString().trim();
                newRaceText.setText("");

                if (newRaceName.equals("")) {
                    Toast.makeText(CustomizeActivity.this,
                        R.string.empty_race_name_error_message,
                        Toast.LENGTH_LONG)
                        .show();
                    return;
                }

                //TODO: Check for existing duplicate

                Race newRace = new Race();
                newRace.setName(newRaceName);
                newRace.setUser(ParseUser.getCurrentUser());

                newRace.pinInBackground();
                newRace.saveEventually();

                Toast.makeText(CustomizeActivity.this,
                        R.string.new_race_created_message,
                        Toast.LENGTH_LONG)
                        .show();

                raceQueryAdapter.notifyDataSetChanged();
            }
        });

        saveClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newClassName = newClassText.getText().toString().trim();
                newClassText.setText("");

                if (newClassName.equals("")) {
                    Toast.makeText(CustomizeActivity.this,
                        R.string.empty_class_name_error_message,
                        Toast.LENGTH_LONG)
                        .show();
                    return;
                }

                //TODO: Check for existing duplicate

                CharacterClass newClass = new CharacterClass();
                newClass.setName(newClassName);
                newClass.setUser(ParseUser.getCurrentUser());

                newClass.pinInBackground();
                newClass.saveEventually();

                Toast.makeText(CustomizeActivity.this,
                        R.string.new_class_created_message,
                        Toast.LENGTH_LONG)
                        .show();

                classQueryAdapter.notifyDataSetChanged();
            }
        });
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

    private void setupRaceSpinnerItems() {
        raceQueryAdapter =
                new ParseSpinnerQueryAdapter<>(this,
                        new ParseQueryAdapter.QueryFactory<Race>() {
                            @Override
                            public ParseQuery<Race> create() {
                                return Race.getQuery();
                            }
                        });
        raceQueryAdapter.setTextKey(Race.KEY_NAME);
        raceSpinner.setAdapter(raceQueryAdapter);
    }

    private void setupClassSpinnerItems() {
        classQueryAdapter =
                new ParseSpinnerQueryAdapter<>(this,
                        new ParseQueryAdapter.QueryFactory<CharacterClass>() {
                            @Override
                            public ParseQuery<CharacterClass> create() {
                                return CharacterClass.getQuery();
                            }
                        });
        classQueryAdapter.setTextKey(CharacterClass.KEY_NAME);
        classSpinner.setAdapter(classQueryAdapter);
    }
}
