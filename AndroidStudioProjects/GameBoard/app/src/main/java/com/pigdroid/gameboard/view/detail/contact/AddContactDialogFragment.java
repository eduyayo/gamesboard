package com.pigdroid.gameboard.view.detail.contact;

import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TextView;

import com.couchbase.lite.Document;
import com.pigdroid.gameboard.app.GameBoardApplication;
import com.pigdroid.gameboard.util.DialogFragmentCallback;
import com.pigdroid.gameboard.view.ServiceDialogFragment;

public class AddContactDialogFragment extends ServiceDialogFragment {
	
	
	  @Override
      public Dialog onCreateDialog(Bundle savedInstanceState) {
  		AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
  		adb.setTitle("Add contact?");
  		adb.setMessage("Add ... as contact?");
  		adb.setIcon(android.R.drawable.ic_dialog_alert);
  		adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
  			public void onClick(DialogInterface dialog, int which) {
  				((DialogFragmentCallback) getActivity()).fragmentDialogCallback(getTag(), "ok");
  				dismiss();
  			}
  		});
          //TODO reject and cancel
  		adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
  			public void onClick(DialogInterface dialog, int which) {
  				((DialogFragmentCallback) getActivity()).fragmentDialogCallback(getTag(), "cancel");
  				dismiss();
  			}
  		});
  		return adb.create();
      }


	@Override
	protected void onKickOff() {
		super.onKickOff();
        String id = getArguments().getString("id");
        final String email = getArguments().getString("email");
        Document document = dataService.loadDocument(id);
        final Map<String, Object> selectedContact;
        if (document != null) {
            selectedContact = document.getProperties();
        } else {
            selectedContact = null;
        }
		((TextView) getDialog().findViewById(android.R.id.message)).setText("Add " + (selectedContact != null ? selectedContact.get("email") : email) + "as contact?");
	}

}
