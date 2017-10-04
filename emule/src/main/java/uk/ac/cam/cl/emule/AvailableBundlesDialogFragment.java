package uk.ac.cam.cl.emule;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;

/**
 * Created by fergus on 12/01/2017.
 */


public class AvailableBundlesDialogFragment extends DialogFragment {


    ArrayList mSelectedItems;


    public AvailableBundlesDialogFragment() {

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        final CharSequence[] cs = bundle.getCharSequenceArray("list");
        //Use the aplist to get the proper titles for these.
        mSelectedItems = new ArrayList();  // Where we track the selected items
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setTitle("Choose Village Bundles to Download")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(cs, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    mSelectedItems.add((which));
                                } else if (mSelectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    mSelectedItems.remove(Integer.valueOf(which));
                                }
                            }
                        })
                // Set the action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        ((StatusFragment) getTargetFragment()).onDialogPositiveClick(AvailableBundlesDialogFragment.this);


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ((StatusFragment) getTargetFragment()).onDialogNegativeClick(AvailableBundlesDialogFragment.this);
                    }
                });

        return builder.create();
    }

    public ArrayList getSelectedItems() {
        return mSelectedItems;
    }
}