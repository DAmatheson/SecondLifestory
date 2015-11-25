/**
 * Created by Drew on 11/25/2015.
 */

package ca.secondlifestory.adapters;

import android.content.Context;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

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
}
