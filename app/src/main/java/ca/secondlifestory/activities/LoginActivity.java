/* LoginActivity.java
 * Purpose: Activity for the login screen of the app
 *
 *  Created by Drew on 11/17/2015.
 *
 *  Note: This currently is all handled by the app and it essentially a loading screen
 */

package ca.secondlifestory.activities;

import android.content.Intent;
import android.provider.Settings;
import android.os.Bundle;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import ca.secondlifestory.BaseActivity;
import ca.secondlifestory.R;
import ca.secondlifestory.activities.character.CharacterActivity;
import ca.secondlifestory.models.CharacterClass;
import ca.secondlifestory.models.Race;
import ca.secondlifestory.utilities.SimpleDialogFragment;

/**
 * Activity for logging in as a Parse user
 * This is currently just logs in in using device Id.
 */
public class LoginActivity extends BaseActivity implements SimpleDialogFragment.OnPositiveCloseListener {

    private static final String LOG_TAG = LoginActivity.class.getName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final String androidId =
                Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        ParseUser.logInInBackground(androidId, androidId, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    startCharacterActivity();
                } else if (e == null || e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                    createNewUser(androidId);
                } else {
                    LoginActivity.this.showLoginErrorAndFinish(e);
                }
            }
        });
    }

    /**
     * Creates a new parse user with androidId as their username and password.
     * After signing up, it starts CharacterActivity.
     * @param androidId The username and password for the new user
     */
    private void createNewUser(String androidId) {
        ParseUser newUser = new ParseUser();
        newUser.setUsername(androidId);
        newUser.setPassword(androidId);

        newUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Race.setupDefaultRaces(
                            LoginActivity.this.getResources()
                                    .getStringArray(R.array.defaultRaceNames));

                    CharacterClass.setupDefaultClasses(
                            LoginActivity.this.getResources()
                                    .getStringArray(R.array.defaultCharacterClassNames));

                    startCharacterActivity();
                } else {
                    LoginActivity.this.showLoginErrorAndFinish(e);
                }
            }
        });
    }

    /**
     * Starts CharacterActivity and finishes this activity
     */
    private void startCharacterActivity() {
        Intent intent = new Intent(this, CharacterActivity.class);

        startActivity(intent);
        finish();
    }

    /**
     * Displays a login error dialog and then finishes the activity (and app) after
     * the user hits okay
     * @param e The ParseException from the failed login
     */
    private void showLoginErrorAndFinish(ParseException e) {
        getLogger().exception(LOG_TAG, ".showLoginErrorAndFinish. ParseException code: "
                + e.getCode(), e);

        SimpleDialogFragment dialog;

        if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
            dialog = SimpleDialogFragment.newInstance(R.string.ok,
                    getString(R.string.login_activity_invalid_credentials));
        } else {
            dialog = SimpleDialogFragment.newInstance(R.string.ok,
                    getString(R.string.login_activity_internet_connection_required));
        }

        dialog.onAttach(this); // Need to manually attach. Not sure why
        dialog.show(getFragmentManager(), null);
    }

    /**
     * Callback for SimpleDialogFragments positive button
     */
    @Override
    public void onPositiveClose() {
        finish();
    }
}
