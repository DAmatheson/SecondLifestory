/* PlayerCharacterQueryAdapter.java
 * Purpose: ParseQueryAdapter for PlayerCharacter object lists
 *
 * Created by Drew on 11/25/2015.
 */

package ca.secondlifestory.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import ca.secondlifestory.Application;
import ca.secondlifestory.R;
import ca.secondlifestory.models.PlayerCharacter;

/**
 * Query Adapter for PlayerCharacter
 */
public class PlayerCharacterQueryAdapter extends ParseQueryAdapter<PlayerCharacter> {
    public PlayerCharacterQueryAdapter(Context context, ParseQueryAdapter.QueryFactory<PlayerCharacter> queryFactory) {
        super (context, queryFactory);
    }

    public PlayerCharacterQueryAdapter(final Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<PlayerCharacter>() {

            @Override
            public ParseQuery<PlayerCharacter> create() {
                ParseQuery<PlayerCharacter> query = PlayerCharacter.getQuery();

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                boolean showDeceased = prefs.getBoolean(Application.ARG_SHOW_DECEASED_CHARACTERS, false);

                if (!showDeceased) {
                    query.whereEqualTo(PlayerCharacter.KEY_LIVING, true);
                }

                query.orderByAscending("createdAt");

                return query;
            }
        });
    }

    @Override
    public View getItemView(PlayerCharacter object, View v, ViewGroup parent) {

        if (v == null) {
            v = View.inflate(getContext(), R.layout.character_list_item, null);
        }

        super.getItemView(object, v, parent);

        TextView characterName = (TextView) v.findViewById(R.id.character_name);
        characterName.setText(object.getName());

        TextView raceClass = (TextView) v.findViewById(R.id.race_class);
        raceClass.setText(String.format("%s %s",
                object.getRace().getName(),
                object.getCharacterClass().getName()));

        return v;
    }

    public void notifyListChanged() {
        // Unfortunately, there isn't a way to add new items with ParseQueryAdapter.
        loadObjects();
    }
}
