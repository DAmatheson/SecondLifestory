/* TextEntryDialogFragment.java
 * Purpose: Dialog fragment with a text input.
 *          The value is passed to the listener on positive closing.
 *
 * Created by Drew on 11/23/2015.
 */

package ca.secondlifestory.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;

import ca.secondlifestory.R;

/**
 * A {@link DialogFragment} subclass with a text input.
 * Activities that use this fragment must implement the {@link OnPositiveCloseListener}
 * interface.
 */
public class TextEntryDialogFragment extends DialogFragment {

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnPositiveCloseListener {
        void onPositiveClose(String textInput);
    }

    private static final String LOG_TAG = TextEntryDialogFragment.class.getName();

    public static final int INVALID_RES_ID = -1;
    private static final String ARG_TITLE_RES_ID = "titleResId";
    private static final String ARG_POSITIVE_TEXT_RES_ID = "positiveTextResId";
    private static final String ARG_NEGATIVE_TEXT_RES_ID = "negativeTextResId";
    private static final String ARG_EDIT_TEXT_VALUE_ID = "editTextValue";

    /**
     * The serialization (saved instance state) Bundle keys
     */
    private static final String TEXT_INPUT_VALUE = "textEntryDialogFragment.textInput";

    private OnPositiveCloseListener listener;
    private EditText textInput;

    private int titleTextResId;
    private int positiveButtonTextResId;
    private int negativeButtonTextResId;

    /**
     * Creates a new instance of the fragment with the title and positive button text
     * @param positiveButtonTextResId The resource Id for the positive button's text
     * @param titleTextResId The resource Id for the dialog's title
     * @return The setup new instance of TextEntryDialogFragment
     */
    public static TextEntryDialogFragment newInstance(int positiveButtonTextResId,
                                                      int titleTextResId) {

        return newInstance(positiveButtonTextResId, titleTextResId, INVALID_RES_ID);
    }

    /**
     * Creates a new instance of the fragment with the title and positive button text
     * @param positiveButtonTextResId The resource Id for the positive button's text
     * @param titleTextResId The resource Id for the dialog's title
     * @return The setup new instance of TextEntryDialogFragment
     */
    public static TextEntryDialogFragment newInstance(int positiveButtonTextResId,
                                                      int titleTextResId,
                                                      int negativeButtonTextResId) {

        return newInstance(positiveButtonTextResId, titleTextResId, negativeButtonTextResId, "");
    }

    /**
     * Creates a new instance of the fragment with the title and positive button text
     * @param positiveButtonTextResId The resource Id for the positive button's text
     * @param titleTextResId The resource Id for the dialog's title
     * @param negativeButtonTextResId The resource Id for the negative button's text
     * @param editTextValue The string value to initialize the edit text with
     * @return The setup new instance of TextEntryDialogFragment
     */
    public static TextEntryDialogFragment newInstance(int positiveButtonTextResId,
                                                      int titleTextResId,
                                                      int negativeButtonTextResId,
                                                      String editTextValue) {
        try {

            Bundle args = new Bundle();
            args.putInt(ARG_TITLE_RES_ID, titleTextResId);
            args.putInt(ARG_POSITIVE_TEXT_RES_ID, positiveButtonTextResId);
            args.putInt(ARG_NEGATIVE_TEXT_RES_ID, negativeButtonTextResId);
            args.putString(ARG_EDIT_TEXT_VALUE_ID, editTextValue);

            TextEntryDialogFragment fragment = new TextEntryDialogFragment();
            fragment.setArguments(args);

            return fragment;
        } catch (Exception ex) {
            Log.e(LOG_TAG, ".newInstance: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnPositiveCloseListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnPositiveCloseListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() == null) {
            throw new IllegalStateException(LOG_TAG +
                    " must be create via one of the newInstance factory methods.");
        }

        // Setup from arguments
        try {
            titleTextResId = getArguments().getInt(ARG_TITLE_RES_ID, INVALID_RES_ID);
            positiveButtonTextResId = getArguments().getInt(ARG_POSITIVE_TEXT_RES_ID, INVALID_RES_ID);
            negativeButtonTextResId = getArguments().getInt(ARG_NEGATIVE_TEXT_RES_ID, INVALID_RES_ID);
        } catch (Exception ex) {
            Log.e(LOG_TAG, ".onCreate restoring args: " + ex.getMessage(), ex);

            throw ex;
        }

        try {

            textInput = new EditText(getActivity());
            textInput.setSingleLine();
            textInput.setInputType(InputType.TYPE_CLASS_TEXT);
            textInput.setTextColor(getResources().getColor(R.color.primary_text_default_material_dark));
            textInput.setEms(16);

            if (savedInstanceState != null) {
                textInput.setText(savedInstanceState.getString(TEXT_INPUT_VALUE, ""));
            } else {
                textInput.setText(getArguments().getString(ARG_EDIT_TEXT_VALUE_ID, ""));
            }

            textInput.setSelection(textInput.length());
        } catch (Exception ex) {
            Log.e(LOG_TAG, ".onCreate textInput setup: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try {
            DialogInterface.OnClickListener yesDialogButtonClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String input = textInput.getText().toString();

                            listener.onPositiveClose(input);
                        }
                    };

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity(),
                    R.style.Theme_AppCompat_Dialog_Alert);

            alertBuilder.
                    setTitle(titleTextResId).
                    setPositiveButton(positiveButtonTextResId,
                            yesDialogButtonClickListener).
                    setView(textInput);

            if (negativeButtonTextResId != INVALID_RES_ID) {
                alertBuilder.setNegativeButton(negativeButtonTextResId, null);
            }

            return alertBuilder.create();
        } catch (Exception ex) {
            Log.e(LOG_TAG, ".onCreatePlayer: " + ex.getMessage(), ex);

            throw ex;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(TEXT_INPUT_VALUE, textInput.getText().toString());
    }
}