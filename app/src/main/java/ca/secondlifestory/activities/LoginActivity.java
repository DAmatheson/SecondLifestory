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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import ca.secondlifestory.R;
import ca.secondlifestory.activities.character.CharacterActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final String androidId =
                Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        ParseUser.logInInBackground(androidId, androidId, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    startCharacterActivity();
                } else {
                    // Signup failed. Look at the ParseException to see what happened.
                    user = new ParseUser();
                    user.setUsername(androidId);
                    user.setPassword(androidId);

                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                startCharacterActivity();
                            } else {
                                // TODO: Add alert informing the user they need an internet connection on startup

                                // TODO: this needs to be in the callback for alert closing.
                                LoginActivity.this.finish();
                            }
                        }
                    });
                }
            }
        });
    }

    private void startCharacterActivity() {
        Intent intent = new Intent(this, CharacterActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
    }
}
