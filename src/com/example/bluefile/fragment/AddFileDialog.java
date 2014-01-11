package com.example.bluefile.fragment;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.example.bluefile.R;

public class AddFileDialog extends DialogFragment {

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(File file, int id);
        public void onDialogNegativeClick(File file, int id);
    }
    
    // Use this instance of the interface to deliver action events
    private NoticeDialogListener mListener;
    private File fileToAdd = null;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(R.string.add_file)
		.setPositiveButton(R.string.add_file_yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				mListener.onDialogPositiveClick(fileToAdd, id);
			}
		})
		.setNegativeButton(R.string.add_file_no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				mListener.onDialogNegativeClick(fileToAdd, id);
			}
		});
		
		// Create the AlertDialog object and return it
		return builder.create();
	}
	
	// Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
    
    public void setFileToAdd(File file) {
    	fileToAdd = file;
    }

}
