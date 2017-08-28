package com.pigdroid.gameboard.view.detail.game;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.pigdroid.gameboard.util.DialogFragmentCallback;
import com.pigdroid.gameboard.view.ServiceDialogFragment;

public class WithdrawGameDialogFragment extends ServiceDialogFragment {

	  @Override
      public Dialog onCreateDialog(Bundle savedInstanceState) {
  		AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
  		adb.setTitle("Are you sure?");
		adb.setMessage("Are you sure you want to withdraw the current game?");
  		adb.setIcon(android.R.drawable.ic_dialog_alert);
  		adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
  			public void onClick(DialogInterface dialog, int which) {
  				((DialogFragmentCallback) getActivity()).fragmentDialogCallback(getTag(), "ok");
  				dismiss();
  			}
  		});
  		adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
  			public void onClick(DialogInterface dialog, int which) {
  				((DialogFragmentCallback) getActivity()).fragmentDialogCallback(getTag(), "cancel");
  				dismiss();
  			}
  		});
  		return adb.create();
      }

}
