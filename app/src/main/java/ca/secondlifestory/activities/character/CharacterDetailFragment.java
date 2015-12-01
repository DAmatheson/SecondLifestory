/* CharacterDetailFragment.java
 * Purpose: Fragment for the character detail screen
 *
 *  Created by Drew on 11/18/2015.
 */

package ca.secondlifestory.activities.character;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import ca.secondlifestory.R;
import ca.secondlifestory.models.PlayerCharacter;

/**
 * A fragment representing a single PlayerCharacter detail screen.
 * This fragment is contained in a {@link CharacterActivity}.
 */
public class CharacterDetailFragment extends Fragment {
    public interface Callbacks {
        void onEditClicked(String characterObjectId);
    }

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    private static final String ARG_CHARACTER_ID = "characterObjectId";

    private Callbacks mListener;

    /**
     * The content this fragment is presenting.
     */
    private PlayerCharacter mItem;

    private ProgressBar loadingIndicator;

    private TextView name;
    private TextView race;
    private TextView characterClass;
    private TextView totalXp;
    private TextView status;
    private TextView description;

    private Button editButton;
    private Button deleteButton;

    /**
     * Creates a new instance of the fragment with the specified character object id
     * @param characterObjectId The id for the character to initially show details for
     * @return The setup new instance of PlayerStatsDetailFragment
     */
    public static CharacterDetailFragment newInstance(String characterObjectId) {
        Bundle args = new Bundle();
        args.putString(ARG_CHARACTER_ID, characterObjectId);

        CharacterDetailFragment fragment = new CharacterDetailFragment();
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CharacterDetailFragment() { }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_character_detail, container, false);

        loadingIndicator = (ProgressBar)rootView.findViewById(R.id.loadingIndicator);

        name = (TextView)rootView.findViewById(R.id.character_name);
        race = (TextView)rootView.findViewById(R.id.character_race);
        characterClass = (TextView)rootView.findViewById(R.id.character_class);
        totalXp = (TextView)rootView.findViewById(R.id.character_xp);
        status = (TextView)rootView.findViewById(R.id.character_status);
        description = (TextView)rootView.findViewById(R.id.character_description);

        if (getArguments() != null) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.

            loadingIndicator.setVisibility(View.VISIBLE);

            ParseQuery<PlayerCharacter> query = PlayerCharacter.getQuery();
            query.getInBackground(getArguments().getString(ARG_CHARACTER_ID), new GetCallback<PlayerCharacter>() {
                @Override
                public void done(PlayerCharacter object, ParseException e) {
                    if (e == null) {
                        loadingIndicator.setVisibility(View.INVISIBLE);

                        mItem = object;

                        name.setText(mItem.getName());
                        race.setText(mItem.getRace().getName());
                        characterClass.setText(mItem.getCharacterClass().getName());
                        totalXp.setText(Integer.toString(mItem.getExperience()));
                        status.setText(mItem.isLiving() ? "Alive" : "Dead");
                        description.setText(mItem.getDetails());
                    } else {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        editButton = (Button)rootView.findViewById(R.id.edit_character_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onEditClicked(mItem.getObjectId());
            }
        });

        deleteButton = (Button)rootView.findViewById(R.id.delete_character_button);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (Callbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement Callbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
