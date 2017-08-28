package com.pigdroid.gameboard.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;

import com.pigdroid.gameboard.R;

public class ProgressDialogFragment extends DialogFragment {

	public static ProgressDialogFragment newInstance() {
		ProgressDialogFragment frag = new ProgressDialogFragment ();
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		final ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setMessage(getString(R.string.loading_text));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		// Disable the back button
		dialog.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				return keyCode == KeyEvent.KEYCODE_BACK;
			}
			
		});
		return dialog;
	}

}