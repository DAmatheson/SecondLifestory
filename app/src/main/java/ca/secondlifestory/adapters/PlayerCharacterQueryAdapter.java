/**
 * Created by Drew on 11/25/2015.
 */

package ca.secondlifestory.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import ca.secondlifestory.R;
import ca.secondlifestory.models.PlayerCharacter;

public class PlayerCharacterQueryAdapter extends ParseQueryAdapter<PlayerCharacter> {
    public PlayerCharacterQueryAdapter(Context context, ParseQueryAdapter.QueryFactory<PlayerCharacter> queryFactory) {
        super (context, queryFactory);
    }

    public PlayerCharacterQueryAdapter(Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<PlayerCharacter>() {

            @Override
            public ParseQuery<PlayerCharacter> create() {
                ParseQuery query = PlayerCharacter.getQuery();
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

        // Add the title view
        TextView characterName = (TextView) v.findViewById(R.id.character_name);
        characterName.setText(object.getName());

        TextView raceClass = (TextView) v.findViewById(R.id.race_class);
        raceClass.setText(String.format("%s %s", object.getRace(), object.getClassName()));

        return v;
    }
}
