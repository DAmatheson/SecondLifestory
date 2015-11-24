/* SimpleDialogFragment.java
 * Purpose:
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
import android.util.Log;

import ca.secondlifestory.R;

/**
 * A {@link DialogFragment} subclass to simplify creating auto-restoring dialogs.
 * Activities that use this fragment must implement the {@link OnPositiveCloseListener}
 * interface.
 */
public class SimpleDialogFragment extends DialogFragment {

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnPositiveCloseListener {
        void onPositiveClose();
    }

    private static final String LOG_TAG = SimpleDialogFragment.class.getName();

    public static final int INVALID_RES_ID = -1;
    private static final String ARG_TITLE_RES_ID = "titleResId";
    private static final String ARG_POSITIVE_TEXT_RES_ID = "positiveTextResId";
    private static final String ARG_NEGATIVE_TEXT_RES_ID = "negativeTextResId";

    private OnPositiveCloseListener listener;

    private int titleTextResId;
    private int positiveButtonTextResId;
    private int negativeButtonTextResId;

    /**
     * Creates a new instance of the fragment with the title and positive button text
     * @param positiveButtonTextResId The resource Id for the positive button's text
     * @return The setup new instance of TextEntryDialogFragment
     */
    public static TextEntryDialogFragment newInstance(int positiveButtonTextResId) {
        return newInstance(positiveButtonTextResId, INVALID_RES_ID);
    }

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
     * @param negativeButtonTextResId The resource Id for the negative button's text
     * @return The setup new instance of TextEntryDialogFragment
     */
    public static TextEntryDialogFragment newInstance(int positiveButtonTextResId,
                                                      int titleTextResId,
                                                      int negativeButtonTextResId) {
        try {

            Bundle args = new Bundle();
            args.putInt(ARG_TITLE_RES_ID, titleTextResId);
            args.putInt(ARG_POSITIVE_TEXT_RES_ID, positiveButtonTextResId);
            args.putInt(ARG_NEGATIVE_TEXT_RES_ID, negativeButtonTextResId);

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
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try {
            DialogInterface.OnClickListener yesDialogButtonClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            listener.onPositiveClose();
                        }
                    };

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity(),
                    R.style.Theme_AppCompat_Dialog_Alert);

            alertBuilder.setPositiveButton(positiveButtonTextResId, yesDialogButtonClickListener);

            if (titleTextResId != INVALID_RES_ID) {
                alertBuilder.setTitle(titleTextResId);
            }

            if (negativeButtonTextResId != INVALID_RES_ID) {
                alertBuilder.setNegativeButton(negativeButtonTextResId, null);
            }

            return alertBuilder.create();
        } catch (Exception ex) {
            Log.e(LOG_TAG, ".onCreatePlayer: " + ex.getMessage(), ex);

            throw ex;
        }
    }
}
