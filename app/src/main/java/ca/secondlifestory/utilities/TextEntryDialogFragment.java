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
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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

    private OnPositiveCloseListener mListener;
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

        if (activity instanceof OnPositiveCloseListener) {
            mListener = (OnPositiveCloseListener) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() == null) {
            throw new IllegalStateException(LOG_TAG +
                    " must be created via one of the newInstance factory methods.");
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

                            mListener.onPositiveClose(input);
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

    /**
     * @inheritDoc
     */
    @Override
    public void show(@NonNull FragmentManager manager, String tag) {
        if (mListener == null) {
            String activityName = getActivity() != null
                    ? getActivity().toString()
                    : "the host activity";

            throw new IllegalStateException(
                    "Either "
                            + activityName +
                            " must implement OnPositiveCloseListener or you must use one of the show " +
                            "overloads which takes OnPositiveCloseListener as an argument");
        }

        super.show(manager, tag);
    }

    /**
     * @inheritDoc
     */
    @Override
    public int show(@NonNull FragmentTransaction transaction, String tag) {
        if (mListener == null) {
            throw new IllegalStateException(
                    "Either "
                            + getActivity().toString() +
                            " must implement OnPositiveCloseListener or you must use one of the show " +
                            "overloads which takes OnPositiveCloseListener as an argument");
        }

        return super.show(transaction, tag);
    }

    /**
     *  Sets the listener for the dialog and show the dialog.
     * @param manager The FragmentManager this fragment will be added to.
     * @param tag The tag for this fragment, as per
     *            {@link FragmentTransaction#add(Fragment, String) FragmentTransaction.add}.
     * @param listener The {@link OnPositiveCloseListener} listener
     */
    public void show(@NonNull FragmentManager manager,
                     String tag,
                     @NonNull OnPositiveCloseListener listener) {
        mListener = listener;

        super.show(manager, tag);
    }

    /**
     * Sets the listener for the dialog and shows the dialog.
     * @param transaction An existing transaction in which to add the fragment.
     * @param tag The tag for this fragment, as per
     *            {@link FragmentTransaction#add(Fragment, String) FragmentTransaction.add}.
     * @param listener The {@link OnPositiveCloseListener} listener
     * @return Returns the identifier of the committed transaction, as per
     * {@link FragmentTransaction#commit() FragmentTransaction.commit()}.
     */
    public int show(@NonNull FragmentTransaction transaction,
                    String tag,
                    @NonNull OnPositiveCloseListener listener) {
        mListener = listener;

        return super.show(transaction, tag);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(TEXT_INPUT_VALUE, textInput.getText().toString());
    }
}